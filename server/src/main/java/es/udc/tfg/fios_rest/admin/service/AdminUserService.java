package es.udc.tfg.fios_rest.admin.service;

import es.udc.tfg.fios_rest.admin.service.dto.AdminUserRef;
import es.udc.tfg.fios_rest.admin.service.dto.AdminUserView;
import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.user.persistence.dao.UserDao;
import es.udc.tfg.fios_rest.user.persistence.entity.PlatformRole;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import es.udc.tfg.fios_rest.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(rollbackFor = Exception.class)
public class AdminUserService {

  @Autowired
  private UserDao userDao;

  @Autowired
  private UserService userService;

  @Transactional(readOnly = true)
  public List<AdminUserRef> findAll() {
    return userDao.findAll().stream()
      .map(AdminUserRef::from)
      .toList();
  }

  @Transactional(readOnly = true)
  public AdminUserView findById(Long id) throws NotFoundException {
    return AdminUserView.from(findUser(id));
  }

  public AdminUserView setActive(Long id, boolean active) throws NotFoundException, OperationNotAllowed {
    User user = findUser(id);

    if (!active && isCurrentUser(user)) {
      throw new OperationNotAllowed("Administrators cannot deactivate their own account");
    }

    if (active) {
      user.activate();
    } else {
      user.deactivate();
    }

    return AdminUserView.from(userDao.update(user));
  }

  public AdminUserView promoteToAdmin(Long id) throws NotFoundException {
    User user = findUser(id);
    user.setPlatformRole(PlatformRole.ADMIN);
    return AdminUserView.from(userDao.update(user));
  }

  public AdminUserView revokeAdminRole(Long id) throws NotFoundException, OperationNotAllowed {
    User user = findUser(id);

    if (isCurrentUser(user)) {
      throw new OperationNotAllowed("Administrators cannot remove their own administrator role");
    }

    user.setPlatformRole(PlatformRole.USER);
    return AdminUserView.from(userDao.update(user));
  }

  private boolean isCurrentUser(User user) throws NotFoundException {
    return Objects.equals(user.getId(), userService.getCurrentUserId());
  }

  private User findUser(Long id) throws NotFoundException {
    return userDao.findById(id)
      .orElseThrow(() -> new NotFoundException(id.toString(), User.class));
  }
}
