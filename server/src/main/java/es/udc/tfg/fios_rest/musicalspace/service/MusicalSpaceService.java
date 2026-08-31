package es.udc.tfg.fios_rest.musicalspace.service;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.common.security.SecurityUtils;
import es.udc.tfg.fios_rest.musicalspace.persistence.dao.MusicalSpaceDao;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpace;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceApprovalStatus;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceLocation;
import es.udc.tfg.fios_rest.musicalspace.service.dto.MusicalSpaceLocationRequest;
import es.udc.tfg.fios_rest.musicalspace.service.dto.MusicalSpaceRef;
import es.udc.tfg.fios_rest.musicalspace.service.dto.MusicalSpaceRequest;
import es.udc.tfg.fios_rest.musicalspace.service.dto.MusicalSpaceView;
import es.udc.tfg.fios_rest.user.persistence.dao.UserDao;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import es.udc.tfg.fios_rest.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class MusicalSpaceService {

  @Autowired
  private MusicalSpaceDao musicalSpaceDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private UserService userService;

  @Transactional(readOnly = true)
  public List<MusicalSpaceRef> findPublic() {
    return musicalSpaceDao.findPublic().stream()
      .map(MusicalSpaceRef::from)
      .toList();
  }

  @Transactional(readOnly = true)
  public MusicalSpaceView findById(Long id) throws NotFoundException {
    MusicalSpace musicalSpace = findMusicalSpace(id);

    if (musicalSpace.isPubliclyVisible() || findOptionalCurrentUser().map(user -> canManage(musicalSpace, user)).orElse(false)) {
      return MusicalSpaceView.from(musicalSpace);
    }

    throw new NotFoundException(id.toString(), MusicalSpace.class);
  }

  @Transactional(readOnly = true)
  public List<MusicalSpaceRef> findMySpaces() throws NotFoundException {
    User currentUser = findCurrentUser();

    return musicalSpaceDao.findByManager(currentUser.getId()).stream()
      .map(MusicalSpaceRef::from)
      .toList();
  }

  @Transactional(readOnly = true)
  public List<MusicalSpaceRef> findAllAdmin() {
    return musicalSpaceDao.findAll().stream()
      .map(MusicalSpaceRef::from)
      .toList();
  }

  @Transactional(readOnly = true)
  public MusicalSpaceView findByIdAdmin(Long id) throws NotFoundException {
    return MusicalSpaceView.from(findMusicalSpace(id));
  }

  public MusicalSpaceView create(MusicalSpaceRequest request) throws NotFoundException, OperationNotAllowed {
    User currentUser = findCurrentUser();
    validatePublicUserFlow(currentUser);
    MusicalSpace musicalSpace = new MusicalSpace(
      request.name(),
      request.description(),
      request.spaceType(),
      request.capacity(),
      request.mainImage(),
      request.squareMeters(),
      request.soundproofed(),
      toLocation(request.location()),
      currentUser
    );

    musicalSpaceDao.save(musicalSpace);
    return MusicalSpaceView.from(musicalSpace);
  }

  public MusicalSpaceView update(Long id, MusicalSpaceRequest request)
    throws NotFoundException, OperationNotAllowed {
    MusicalSpace musicalSpace = findMusicalSpace(id);
    validateCanManage(musicalSpace);

    if (!musicalSpace.isActive()) {
      throw new OperationNotAllowed("Inactive musical spaces cannot be updated");
    }

    musicalSpace.setName(request.name());
    musicalSpace.setDescription(request.description());
    musicalSpace.setSpaceType(request.spaceType());
    musicalSpace.setCapacity(request.capacity());
    musicalSpace.setMainImage(request.mainImage());
    musicalSpace.setSquareMeters(request.squareMeters());
    musicalSpace.setSoundproofed(request.soundproofed());
    musicalSpace.setLocation(toLocation(request.location()));

    return MusicalSpaceView.from(musicalSpaceDao.update(musicalSpace));
  }

  public MusicalSpaceView deactivate(Long id) throws NotFoundException, OperationNotAllowed {
    MusicalSpace musicalSpace = findMusicalSpace(id);
    validateCanManage(musicalSpace);

    if (musicalSpace.isActive()) {
      musicalSpace.deactivate();
      musicalSpaceDao.update(musicalSpace);
    }

    return MusicalSpaceView.from(musicalSpace);
  }

  public MusicalSpaceView updateApprovalStatusAdmin(Long id, MusicalSpaceApprovalStatus approvalStatus) throws NotFoundException {
    MusicalSpace musicalSpace = findMusicalSpace(id);
    musicalSpace.setApprovalStatus(approvalStatus);
    return MusicalSpaceView.from(musicalSpaceDao.update(musicalSpace));
  }

  private MusicalSpace findMusicalSpace(Long id) throws NotFoundException {
    return musicalSpaceDao.findById(id)
      .orElseThrow(() -> new NotFoundException(id.toString(), MusicalSpace.class));
  }

  private MusicalSpaceLocation toLocation(MusicalSpaceLocationRequest request) {
    return new MusicalSpaceLocation(
      request.country(),
      request.province(),
      request.city(),
      request.street(),
      request.portal(),
      request.floor(),
      request.postalCode(),
      request.latitude(),
      request.longitude()
    );
  }

  private User findCurrentUser() throws NotFoundException {
    Long currentUserId = userService.getCurrentUserId();
    return userDao.findById(currentUserId)
      .orElseThrow(() -> new NotFoundException(currentUserId.toString(), User.class));
  }

  private Optional<User> findOptionalCurrentUser() {
    String currentUserLogin = SecurityUtils.getCurrentUserLogin();

    if (currentUserLogin == null) {
      return Optional.empty();
    }

    return userDao.findByEmail(currentUserLogin.toLowerCase());
  }

  private void validateCanManage(MusicalSpace musicalSpace) throws NotFoundException, OperationNotAllowed {
    User currentUser = findCurrentUser();

    validatePublicUserFlow(currentUser);

    if (!canManage(musicalSpace, currentUser)) {
      throw new OperationNotAllowed("The user cannot manage this musical space");
    }
  }

  private boolean canManage(MusicalSpace musicalSpace, User user) {
    return musicalSpace.getManager().getId().equals(user.getId());
  }

  private void validatePublicUserFlow(User user) throws OperationNotAllowed {
    if (user.isAdmin()) {
      throw new OperationNotAllowed("Administrators cannot manage musical spaces from the public user flow");
    }
  }
}
