package es.udc.tfg.fios_rest.common.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {
  private SecurityUtils() {
  }

  public static String getCurrentUserLogin() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      return null;
    }

    if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
      return springSecurityUser.getUsername();
    }

    return null;
  }

  public static boolean getCurrentUserIsAdmin() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    if (authentication != null) {
      return authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
    }
    return false;
  }
}
