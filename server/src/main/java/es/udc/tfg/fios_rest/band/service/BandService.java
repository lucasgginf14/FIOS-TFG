package es.udc.tfg.fios_rest.band.service;

import es.udc.tfg.fios_rest.band.persistence.dao.BandDao;
import es.udc.tfg.fios_rest.band.persistence.entity.Band;
import es.udc.tfg.fios_rest.band.service.dto.BandRef;
import es.udc.tfg.fios_rest.band.service.dto.BandRequest;
import es.udc.tfg.fios_rest.band.service.dto.BandView;
import es.udc.tfg.fios_rest.bandmember.persistence.dao.BandMemberDao;
import es.udc.tfg.fios_rest.bandmember.persistence.entity.BandMember;
import es.udc.tfg.fios_rest.bandmember.persistence.entity.BandMemberRole;
import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.user.persistence.dao.UserDao;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import es.udc.tfg.fios_rest.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class BandService {

  @Autowired
  private BandDao bandDao;

  @Autowired
  private BandMemberDao bandMemberDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private UserService userService;

  @Transactional(readOnly = true)
  public List<BandRef> findAll() {
    return bandDao.findAllActive().stream()
      .map(BandRef::from)
      .toList();
  }

  @Transactional(readOnly = true)
  public BandView findById(Long id) throws NotFoundException {
    Band band = findBand(id);
    if (!band.isActive()) {
      throw new NotFoundException(id.toString(), Band.class);
    }
    return BandView.from(band);
  }

  @Transactional(readOnly = true)
  public List<BandRef> findMyBands() throws NotFoundException {
    User currentUser = findCurrentUser();
    return bandMemberDao.findActiveByUser(currentUser.getId()).stream()
      .map(BandMember::getBand)
      .map(BandRef::from)
      .toList();
  }

  public BandView create(BandRequest request) throws NotFoundException, OperationNotAllowed {
    User currentUser = findCurrentUser();
    validatePublicUserFlow(currentUser);
    Band band = new Band(
      request.name(),
      request.description(),
      request.mainGenre(),
      request.baseCity(),
      request.image()
    );

    bandDao.save(band);
    bandMemberDao.save(new BandMember(band, currentUser, BandMemberRole.LEADER));

    return BandView.from(band);
  }

  public BandView update(Long id, BandRequest request) throws NotFoundException, OperationNotAllowed {
    Band band = findBand(id);
    validateCanManage(band);

    if (!band.isActive()) {
      throw new OperationNotAllowed("Inactive bands cannot be updated");
    }

    band.setName(request.name());
    band.setDescription(request.description());
    band.setMainGenre(request.mainGenre());
    band.setBaseCity(request.baseCity());
    band.setImage(request.image());

    return BandView.from(bandDao.update(band));
  }

  public BandView deactivate(Long id) throws NotFoundException, OperationNotAllowed {
    Band band = findBand(id);
    validateCanManage(band);

    if (band.isActive()) {
      band.deactivate();
      bandDao.update(band);
      bandMemberDao.deactivateActiveByBand(band);
    }

    return BandView.from(band);
  }

  private Band findBand(Long id) throws NotFoundException {
    return bandDao.findById(id)
      .orElseThrow(() -> new NotFoundException(id.toString(), Band.class));
  }

  private User findCurrentUser() throws NotFoundException {
    Long currentUserId = userService.getCurrentUserId();
    return userDao.findById(currentUserId)
      .orElseThrow(() -> new NotFoundException(currentUserId.toString(), User.class));
  }

  private void validateCanManage(Band band) throws NotFoundException, OperationNotAllowed {
    User currentUser = findCurrentUser();

    validatePublicUserFlow(currentUser);

    boolean canManage = bandMemberDao.findActiveByBandAndUser(band.getId(), currentUser.getId())
      .map(BandMember::isLeader)
      .orElse(false);

    if (!canManage) {
      throw new OperationNotAllowed("The user cannot manage this band");
    }
  }

  private void validatePublicUserFlow(User user) throws OperationNotAllowed {
    if (user.isAdmin()) {
      throw new OperationNotAllowed("Administrators cannot manage bands from the public user flow");
    }
  }
}
