package es.udc.tfg.fios_rest.bandrecruitment.service;

import es.udc.tfg.fios_rest.band.persistence.dao.BandDao;
import es.udc.tfg.fios_rest.band.persistence.entity.Band;
import es.udc.tfg.fios_rest.bandmember.persistence.dao.BandMemberDao;
import es.udc.tfg.fios_rest.bandmember.persistence.entity.BandMember;
import es.udc.tfg.fios_rest.bandrecruitment.persistence.dao.BandRecruitmentDao;
import es.udc.tfg.fios_rest.bandrecruitment.persistence.entity.BandRecruitment;
import es.udc.tfg.fios_rest.bandrecruitment.service.dto.BandRecruitmentRef;
import es.udc.tfg.fios_rest.bandrecruitment.service.dto.BandRecruitmentRequest;
import es.udc.tfg.fios_rest.bandrecruitment.service.dto.BandRecruitmentView;
import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.instrument.persistence.dao.InstrumentDao;
import es.udc.tfg.fios_rest.instrument.persistence.entity.Instrument;
import es.udc.tfg.fios_rest.user.persistence.dao.UserDao;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import es.udc.tfg.fios_rest.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class BandRecruitmentService {

  @Autowired
  private BandRecruitmentDao bandRecruitmentDao;

  @Autowired
  private BandDao bandDao;

  @Autowired
  private BandMemberDao bandMemberDao;

  @Autowired
  private InstrumentDao instrumentDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private UserService userService;

  @Transactional(readOnly = true)
  public List<BandRecruitmentRef> findOpen() {
    return bandRecruitmentDao.findOpen().stream()
      .map(BandRecruitmentRef::from)
      .toList();
  }

  @Transactional(readOnly = true)
  public BandRecruitmentView findById(Long id) throws NotFoundException {
    BandRecruitment bandRecruitment = findRecruitment(id);

    if (!bandRecruitment.getBand().isActive()) {
      throw new NotFoundException(id.toString(), BandRecruitment.class);
    }

    return BandRecruitmentView.from(bandRecruitment);
  }

  @Transactional(readOnly = true)
  public List<BandRecruitmentRef> findMyRecruitments() throws NotFoundException {
    User currentUser = findCurrentUser();
    List<Long> bandIds = bandMemberDao.findActiveByUser(currentUser.getId()).stream()
      .map(BandMember::getBand)
      .map(Band::getId)
      .toList();

    if (bandIds.isEmpty()) {
      return List.of();
    }

    return bandRecruitmentDao.findByBandIds(bandIds).stream()
      .map(BandRecruitmentRef::from)
      .toList();
  }

  @Transactional(readOnly = true)
  public List<BandRecruitmentRef> findAllAdmin() {
    return bandRecruitmentDao.findAll().stream()
      .map(BandRecruitmentRef::from)
      .toList();
  }

  public BandRecruitmentView create(Long bandId, BandRecruitmentRequest request)
    throws NotFoundException, OperationNotAllowed {
    Band band = findActiveBand(bandId);
    User currentUser = findCurrentUser();
    validateCanManage(band, currentUser);

    Instrument instrument = findInstrument(request.instrumentId());
    BandRecruitment bandRecruitment = new BandRecruitment(
      request.title(),
      request.description(),
      request.roleWanted(),
      request.levelRequired(),
      request.city(),
      request.vacancies(),
      band,
      instrument,
      currentUser
    );

    bandRecruitmentDao.save(bandRecruitment);
    return BandRecruitmentView.from(bandRecruitment);
  }

  public BandRecruitmentView update(Long id, BandRecruitmentRequest request)
    throws NotFoundException, OperationNotAllowed {
    BandRecruitment bandRecruitment = findRecruitment(id);
    validateCanManage(bandRecruitment.getBand());

    if (!bandRecruitment.getBand().isActive()) {
      throw new OperationNotAllowed("Recruitments from inactive bands cannot be updated");
    }

    if (!bandRecruitment.isOpen()) {
      throw new OperationNotAllowed("Closed recruitments cannot be updated");
    }

    Instrument instrument = findInstrument(request.instrumentId());

    bandRecruitment.setTitle(request.title());
    bandRecruitment.setDescription(request.description());
    bandRecruitment.setRoleWanted(request.roleWanted());
    bandRecruitment.setLevelRequired(request.levelRequired());
    bandRecruitment.setCity(request.city());
    bandRecruitment.setVacancies(request.vacancies());
    bandRecruitment.setInstrument(instrument);

    return BandRecruitmentView.from(bandRecruitmentDao.update(bandRecruitment));
  }

  public BandRecruitmentView close(Long id) throws NotFoundException, OperationNotAllowed {
    BandRecruitment bandRecruitment = findRecruitment(id);
    validateCanManage(bandRecruitment.getBand());

    if (!bandRecruitment.getBand().isActive()) {
      throw new OperationNotAllowed("Recruitments from inactive bands cannot be closed");
    }

    if (!bandRecruitment.isOpen()) {
      throw new OperationNotAllowed("The recruitment is already closed");
    }

    bandRecruitment.close();
    return BandRecruitmentView.from(bandRecruitmentDao.update(bandRecruitment));
  }

  public void delete(Long id) throws NotFoundException, OperationNotAllowed {
    BandRecruitment bandRecruitment = findRecruitment(id);
    validateCanManage(bandRecruitment.getBand());
    bandRecruitmentDao.delete(bandRecruitment);
  }

  public void deleteAdmin(Long id) throws NotFoundException {
    BandRecruitment bandRecruitment = findRecruitment(id);
    bandRecruitmentDao.delete(bandRecruitment);
  }

  private BandRecruitment findRecruitment(Long id) throws NotFoundException {
    return bandRecruitmentDao.findById(id)
      .orElseThrow(() -> new NotFoundException(id.toString(), BandRecruitment.class));
  }

  private Band findActiveBand(Long id) throws NotFoundException {
    Band band = bandDao.findById(id)
      .orElseThrow(() -> new NotFoundException(id.toString(), Band.class));

    if (!band.isActive()) {
      throw new NotFoundException(id.toString(), Band.class);
    }

    return band;
  }

  private Instrument findInstrument(Long id) throws NotFoundException {
    return instrumentDao.findById(id)
      .orElseThrow(() -> new NotFoundException(id.toString(), Instrument.class));
  }

  private User findCurrentUser() throws NotFoundException {
    Long currentUserId = userService.getCurrentUserId();
    return userDao.findById(currentUserId)
      .orElseThrow(() -> new NotFoundException(currentUserId.toString(), User.class));
  }

  private void validateCanManage(Band band) throws NotFoundException, OperationNotAllowed {
    validateCanManage(band, findCurrentUser());
  }

  private void validateCanManage(Band band, User currentUser) throws OperationNotAllowed {
    if (currentUser.isAdmin()) {
      throw new OperationNotAllowed("Administrators cannot manage recruitments from the public user flow");
    }

    boolean canManage = bandMemberDao.findActiveByBandAndUser(band.getId(), currentUser.getId())
      .map(BandMember::isLeader)
      .orElse(false);

    if (!canManage) {
      throw new OperationNotAllowed("The user cannot manage recruitments for this band");
    }
  }
}
