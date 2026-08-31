package es.udc.tfg.fios_rest.favoritespace.service;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.favoritespace.persistence.dao.FavoriteSpaceDao;
import es.udc.tfg.fios_rest.favoritespace.persistence.entity.FavoriteSpace;
import es.udc.tfg.fios_rest.favoritespace.service.dto.FavoriteSpaceView;
import es.udc.tfg.fios_rest.musicalspace.persistence.dao.MusicalSpaceDao;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpace;
import es.udc.tfg.fios_rest.user.persistence.dao.UserDao;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import es.udc.tfg.fios_rest.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class FavoriteSpaceService {

  @Autowired
  private FavoriteSpaceDao favoriteSpaceDao;

  @Autowired
  private MusicalSpaceDao musicalSpaceDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private UserService userService;

  @Transactional(readOnly = true)
  public List<FavoriteSpaceView> findMyFavorites() throws NotFoundException {
    User currentUser = findCurrentUser();

    return favoriteSpaceDao.findByUser(currentUser.getId()).stream()
      .filter(favoriteSpace -> canViewFavorite(favoriteSpace.getMusicalSpace(), currentUser))
      .map(FavoriteSpaceView::from)
      .toList();
  }

  public FavoriteSpaceView save(Long spaceId) throws NotFoundException, OperationNotAllowed {
    User currentUser = findCurrentUser();
    validatePublicUserFlow(currentUser);
    MusicalSpace musicalSpace = findMusicalSpace(spaceId);
    validateCanFavorite(musicalSpace, currentUser);

    if (favoriteSpaceDao.findByUserAndMusicalSpace(currentUser.getId(), spaceId).isPresent()) {
      throw new IllegalArgumentException("The musical space is already saved as favorite");
    }

    FavoriteSpace favoriteSpace = new FavoriteSpace(currentUser, musicalSpace);
    favoriteSpaceDao.save(favoriteSpace);

    return FavoriteSpaceView.from(favoriteSpace);
  }

  public void delete(Long spaceId) throws NotFoundException, OperationNotAllowed {
    User currentUser = findCurrentUser();
    validatePublicUserFlow(currentUser);
    findMusicalSpace(spaceId);

    favoriteSpaceDao.findByUserAndMusicalSpace(currentUser.getId(), spaceId)
      .ifPresent(favoriteSpaceDao::delete);
  }

  private MusicalSpace findMusicalSpace(Long spaceId) throws NotFoundException {
    return musicalSpaceDao.findById(spaceId)
      .orElseThrow(() -> new NotFoundException(spaceId.toString(), MusicalSpace.class));
  }

  private User findCurrentUser() throws NotFoundException {
    Long currentUserId = userService.getCurrentUserId();
    return userDao.findById(currentUserId)
      .orElseThrow(() -> new NotFoundException(currentUserId.toString(), User.class));
  }

  private void validateCanFavorite(MusicalSpace musicalSpace, User currentUser) throws OperationNotAllowed {
    if (musicalSpace.isPubliclyVisible()) {
      return;
    }

    if (canManage(musicalSpace, currentUser)) {
      return;
    }

    throw new OperationNotAllowed("The musical space cannot be saved as favorite");
  }

  private void validatePublicUserFlow(User user) throws OperationNotAllowed {
    if (user.isAdmin()) {
      throw new OperationNotAllowed("Administrators cannot manage favorites from the public user flow");
    }
  }

  private boolean canViewFavorite(MusicalSpace musicalSpace, User currentUser) {
    return musicalSpace.isPubliclyVisible() || canManage(musicalSpace, currentUser);
  }

  private boolean canManage(MusicalSpace musicalSpace, User user) {
    return user.isAdmin() || musicalSpace.getManager().getId().equals(user.getId());
  }
}
