package es.udc.tfg.fios_rest.bandmember.service;

import es.udc.tfg.fios_rest.band.persistence.dao.BandDao;
import es.udc.tfg.fios_rest.band.persistence.entity.Band;
import es.udc.tfg.fios_rest.bandmember.persistence.entity.BandMember;
import es.udc.tfg.fios_rest.bandmember.persistence.entity.BandMemberRole;
import es.udc.tfg.fios_rest.bandmember.service.dto.BandMemberCandidateRef;
import es.udc.tfg.fios_rest.bandmember.service.dto.BandMemberCreateRequest;
import es.udc.tfg.fios_rest.bandmember.persistence.dao.BandMemberDao;
import es.udc.tfg.fios_rest.bandmember.service.dto.BandMemberRef;
import es.udc.tfg.fios_rest.bandmember.service.dto.BandMemberRoleUpdateRequest;
import es.udc.tfg.fios_rest.bandmember.service.dto.BandMemberView;
import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.user.persistence.dao.UserDao;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import es.udc.tfg.fios_rest.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class BandMemberService {
  private static final int MIN_MEMBER_CANDIDATE_QUERY_LENGTH = 2;
  private static final int MAX_MEMBER_CANDIDATE_RESULTS = 10;

  @Autowired
  private BandMemberDao bandMemberDao;

  @Autowired
  private BandDao bandDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private UserService userService;

  @Transactional(readOnly = true)
  public List<BandMemberRef> findActiveMembersByBand(Long bandId) throws NotFoundException {
    findActiveBand(bandId);

    return bandMemberDao.findActiveByBand(bandId).stream()
      .map(BandMemberRef::from)
      .toList();
  }

  @Transactional(readOnly = true)
  public List<BandMemberCandidateRef> searchMemberCandidates(Long bandId, String query)
    throws NotFoundException, OperationNotAllowed {
    Band band = findActiveBand(bandId);
    validateCanManage(band);

    String normalizedQuery = query == null ? "" : query.trim();
    if (normalizedQuery.length() < MIN_MEMBER_CANDIDATE_QUERY_LENGTH) {
      return List.of();
    }

    Set<Long> activeMemberUserIds = bandMemberDao.findActiveByBand(bandId).stream()
      .map(member -> member.getUser().getId())
      .collect(Collectors.toSet());

    return userDao.searchActiveUsers(normalizedQuery, activeMemberUserIds, MAX_MEMBER_CANDIDATE_RESULTS).stream()
      .map(BandMemberCandidateRef::from)
      .toList();
  }

  public BandMemberView addMember(Long bandId, BandMemberCreateRequest request)
    throws NotFoundException, OperationNotAllowed {
    Band band = findActiveBand(bandId);
    validateCanManage(band);

    User user = userDao.findById(request.userId())
      .orElseThrow(() -> new NotFoundException(request.userId().toString(), User.class));

    if (!user.isActive()) {
      throw new OperationNotAllowed("Inactive users cannot be added to a band");
    }

    if (user.isAdmin()) {
      throw new OperationNotAllowed("Administrators cannot be added to a band");
    }

    return bandMemberDao.findByBandAndUser(bandId, user.getId())
      .map(existingMember -> reactivateMember(existingMember, request.roleInBand()))
      .orElseGet(() -> createMember(band, user, request.roleInBand()));
  }

  public BandMemberView updateRole(Long bandId, Long memberId, BandMemberRoleUpdateRequest request)
    throws NotFoundException, OperationNotAllowed {
    Band band = findActiveBand(bandId);
    validateCanManage(band);

    BandMember bandMember = findMemberInBand(bandId, memberId);
    validateActiveMember(bandMember);

    if (bandMember.isLeader() && !BandMemberRole.LEADER.equals(request.roleInBand())) {
      validateBandKeepsLeader(band);
    }

    bandMember.setRoleInBand(request.roleInBand());
    return BandMemberView.from(bandMemberDao.update(bandMember));
  }

  public BandMemberView deactivateMember(Long bandId, Long memberId)
    throws NotFoundException, OperationNotAllowed {
    Band band = findActiveBand(bandId);
    User currentUser = findCurrentUser();
    validateCanManage(band, currentUser);

    BandMember bandMember = findMemberInBand(bandId, memberId);
    validateActiveMember(bandMember);

    if (bandMember.getUser().getId().equals(currentUser.getId())) {
      throw new OperationNotAllowed("Use the leave endpoint to leave the band");
    }

    if (bandMember.isLeader()) {
      validateBandKeepsLeader(band);
    }

    bandMember.deactivate();
    return BandMemberView.from(bandMemberDao.update(bandMember));
  }

  public BandMemberView leaveBand(Long bandId) throws NotFoundException, OperationNotAllowed {
    Band band = findActiveBand(bandId);
    User currentUser = findCurrentUser();
    BandMember bandMember = bandMemberDao.findActiveByBandAndUser(bandId, currentUser.getId())
      .orElseThrow(() -> new OperationNotAllowed("The user is not an active member of this band"));

    if (bandMember.isLeader()) {
      validateBandKeepsLeader(band);
    }

    bandMember.deactivate();
    return BandMemberView.from(bandMemberDao.update(bandMember));
  }

  private Band findActiveBand(Long bandId) throws NotFoundException {
    Band band = bandDao.findById(bandId)
      .orElseThrow(() -> new NotFoundException(bandId.toString(), Band.class));

    if (!band.isActive()) {
      throw new NotFoundException(bandId.toString(), Band.class);
    }

    return band;
  }

  private BandMember findMemberInBand(Long bandId, Long memberId) throws NotFoundException {
    BandMember bandMember = bandMemberDao.findById(memberId)
      .orElseThrow(() -> new NotFoundException(memberId.toString(), BandMember.class));

    if (!bandMember.getBand().getId().equals(bandId)) {
      throw new NotFoundException(memberId.toString(), BandMember.class);
    }

    return bandMember;
  }

  private void validateActiveMember(BandMember bandMember) throws OperationNotAllowed {
    if (!bandMember.isActive()) {
      throw new OperationNotAllowed("The band member is not active");
    }
  }

  private void validateCanManage(Band band) throws NotFoundException, OperationNotAllowed {
    validateCanManage(band, findCurrentUser());
  }

  private void validateCanManage(Band band, User currentUser) throws OperationNotAllowed {
    if (currentUser.isAdmin()) {
      return;
    }

    boolean canManage = bandMemberDao.findActiveByBandAndUser(band.getId(), currentUser.getId())
      .map(BandMember::isLeader)
      .orElse(false);

    if (!canManage) {
      throw new OperationNotAllowed("The user cannot manage members of this band");
    }
  }

  private void validateBandKeepsLeader(Band band) throws OperationNotAllowed {
    long activeLeaders = bandMemberDao.findActiveByBand(band.getId()).stream()
      .filter(BandMember::isLeader)
      .count();

    if (activeLeaders <= 1) {
      throw new OperationNotAllowed("The band must have at least one active leader");
    }
  }

  private User findCurrentUser() throws NotFoundException {
    Long currentUserId = userService.getCurrentUserId();
    return userDao.findById(currentUserId)
      .orElseThrow(() -> new NotFoundException(currentUserId.toString(), User.class));
  }

  private BandMemberView reactivateMember(BandMember bandMember, BandMemberRole roleInBand) {
    if (bandMember.isActive()) {
      throw new IllegalArgumentException("The user is already an active member of this band");
    }

    bandMember.reactivate(roleInBand);
    return BandMemberView.from(bandMemberDao.update(bandMember));
  }

  private BandMemberView createMember(Band band, User user, BandMemberRole roleInBand) {
    BandMember bandMember = new BandMember(band, user, roleInBand);
    bandMemberDao.save(bandMember);
    return BandMemberView.from(bandMember);
  }
}
