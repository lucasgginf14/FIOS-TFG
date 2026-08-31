package es.udc.tfg.fios_rest.user.service.dto;

import es.udc.tfg.fios_rest.user.persistence.entity.User;

public final class UserProfileImages {

  private static final String DICEBEAR_INITIALS_BASE_URL = "https://api.dicebear.com/9.x/initials/";

  private UserProfileImages() {
  }

  public static String visibleProfileImage(User user) {
    if (user == null) {
      return null;
    }

    String profileImage = user.getProfileImage();
    return isLegacyInitialsAvatar(profileImage) ? null : profileImage;
  }

  private static boolean isLegacyInitialsAvatar(String profileImage) {
    return profileImage != null && profileImage.startsWith(DICEBEAR_INITIALS_BASE_URL);
  }
}
