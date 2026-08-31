package es.udc.tfg.fios_rest.spaceequipment.service;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.common.exceptions.model.SpaceEquipmentAlreadyExistsException;
import es.udc.tfg.fios_rest.common.security.SecurityUtils;
import es.udc.tfg.fios_rest.equipment.persistence.dao.EquipmentDao;
import es.udc.tfg.fios_rest.equipment.persistence.entity.Equipment;
import es.udc.tfg.fios_rest.equipment.persistence.entity.EquipmentCategory;
import es.udc.tfg.fios_rest.musicalspace.persistence.dao.MusicalSpaceDao;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpace;
import es.udc.tfg.fios_rest.spaceequipment.persistence.dao.SpaceEquipmentDao;
import es.udc.tfg.fios_rest.spaceequipment.persistence.entity.SpaceEquipment;
import es.udc.tfg.fios_rest.spaceequipment.service.dto.SpaceEquipmentRequest;
import es.udc.tfg.fios_rest.spaceequipment.service.dto.SpaceEquipmentView;
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
public class SpaceEquipmentService {

  @Autowired
  private SpaceEquipmentDao spaceEquipmentDao;

  @Autowired
  private MusicalSpaceDao musicalSpaceDao;

  @Autowired
  private EquipmentDao equipmentDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private UserService userService;

  @Transactional(readOnly = true)
  public List<SpaceEquipmentView> findByMusicalSpace(Long spaceId) throws NotFoundException {
    MusicalSpace musicalSpace = findMusicalSpace(spaceId);

    boolean canViewPrivateSpace = findOptionalCurrentUser()
      .map(user -> canManage(musicalSpace, user))
      .orElse(false);

    if (!musicalSpace.isPubliclyVisible() && !canViewPrivateSpace) {
      throw new NotFoundException(spaceId.toString(), MusicalSpace.class);
    }

    return spaceEquipmentDao.findByMusicalSpace(spaceId).stream()
      .map(SpaceEquipmentView::from)
      .toList();
  }

  public SpaceEquipmentView create(Long spaceId, SpaceEquipmentRequest request)
    throws NotFoundException, OperationNotAllowed, SpaceEquipmentAlreadyExistsException {
    MusicalSpace musicalSpace = findMusicalSpace(spaceId);
    validateCanManage(musicalSpace);
    validateActiveSpace(musicalSpace);
    validateRequiredAttributes(request);

    Equipment equipment = resolveEquipment(request);
    validateNoDuplicate(musicalSpace.getId(), equipment.getId(), null);

    SpaceEquipment spaceEquipment = new SpaceEquipment(
      request.quantity(),
      request.state(),
      request.observations(),
      musicalSpace,
      equipment
    );

    spaceEquipmentDao.save(spaceEquipment);
    return SpaceEquipmentView.from(spaceEquipment);
  }

  public SpaceEquipmentView update(Long id, SpaceEquipmentRequest request)
    throws NotFoundException, OperationNotAllowed, SpaceEquipmentAlreadyExistsException {
    SpaceEquipment spaceEquipment = findSpaceEquipment(id);
    MusicalSpace musicalSpace = spaceEquipment.getMusicalSpace();
    validateCanManage(musicalSpace);
    validateActiveSpace(musicalSpace);
    validateRequiredAttributes(request);

    Equipment equipment = resolveEquipment(request);
    validateNoDuplicate(musicalSpace.getId(), equipment.getId(), id);

    spaceEquipment.setEquipment(equipment);
    spaceEquipment.setQuantity(request.quantity());
    spaceEquipment.setState(request.state());
    spaceEquipment.setObservations(request.observations());

    return SpaceEquipmentView.from(spaceEquipmentDao.update(spaceEquipment));
  }

  public void delete(Long id) throws NotFoundException, OperationNotAllowed {
    SpaceEquipment spaceEquipment = findSpaceEquipment(id);
    validateCanManage(spaceEquipment.getMusicalSpace());
    spaceEquipmentDao.delete(spaceEquipment);
  }

  private SpaceEquipment findSpaceEquipment(Long id) throws NotFoundException {
    return spaceEquipmentDao.findById(id)
      .orElseThrow(() -> new NotFoundException(id.toString(), SpaceEquipment.class));
  }

  private MusicalSpace findMusicalSpace(Long id) throws NotFoundException {
    return musicalSpaceDao.findById(id)
      .orElseThrow(() -> new NotFoundException(id.toString(), MusicalSpace.class));
  }

  private Equipment findEquipment(Long id) throws NotFoundException {
    return equipmentDao.findById(id)
      .orElseThrow(() -> new NotFoundException(id.toString(), Equipment.class));
  }

  private Equipment resolveEquipment(SpaceEquipmentRequest request) throws NotFoundException {
    if (request.equipmentId() != null) {
      return findEquipment(request.equipmentId());
    }

    String customName = normalizeCustomEquipmentName(request.customEquipmentName());
    return equipmentDao.findByName(customName)
      .orElseGet(() -> equipmentDao.save(new Equipment(
        customName,
        EquipmentCategory.OTHER,
        request.observations()
      )));
  }

  private void validateRequiredAttributes(SpaceEquipmentRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("The equipment request is obligatory");
    }

    if (request.quantity() == null || request.quantity() < 1) {
      throw new IllegalArgumentException("The quantity must be greater than zero");
    }

    if (request.state() == null) {
      throw new IllegalArgumentException("The equipment state is obligatory");
    }

    if (request.equipmentId() == null) {
      normalizeCustomEquipmentName(request.customEquipmentName());
      if (normalizeNullable(request.observations()) == null) {
        throw new IllegalArgumentException("Custom equipment observations are obligatory");
      }
    }
  }

  private String normalizeCustomEquipmentName(String name) {
    String normalized = normalizeNullable(name);

    if (normalized == null) {
      throw new IllegalArgumentException("The custom equipment name is obligatory");
    }

    return normalized;
  }

  private String normalizeNullable(String value) {
    if (value == null) {
      return null;
    }

    String normalized = value.trim();
    return normalized.isEmpty() ? null : normalized;
  }

  private void validateNoDuplicate(Long spaceId, Long equipmentId, Long currentSpaceEquipmentId)
    throws SpaceEquipmentAlreadyExistsException {
    Optional<SpaceEquipment> existingSpaceEquipment = spaceEquipmentDao.findByMusicalSpaceAndEquipment(spaceId, equipmentId)
      .filter(spaceEquipment -> currentSpaceEquipmentId == null || !spaceEquipment.getId().equals(currentSpaceEquipmentId));

    if (existingSpaceEquipment.isPresent()) {
      throw new SpaceEquipmentAlreadyExistsException();
    }
  }

  private void validateActiveSpace(MusicalSpace musicalSpace) throws OperationNotAllowed {
    if (!musicalSpace.isActive()) {
      throw new OperationNotAllowed("Equipment cannot be changed for inactive musical spaces");
    }
  }

  private void validateCanManage(MusicalSpace musicalSpace) throws NotFoundException, OperationNotAllowed {
    User currentUser = findCurrentUser();

    if (!canManage(musicalSpace, currentUser)) {
      throw new OperationNotAllowed("The user cannot manage equipment for this musical space");
    }
  }

  private boolean canManage(MusicalSpace musicalSpace, User user) {
    return user.isAdmin() || musicalSpace.getManager().getId().equals(user.getId());
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
}
