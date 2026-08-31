package es.udc.tfg.fios_rest.common.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityUtilsTest {

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void currentUserLoginReturnsNullForAnonymousAuthentication() {
    SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken(
      "anonymous",
      "anonymousUser",
      List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
    ));

    assertThat(SecurityUtils.getCurrentUserLogin()).isNull();
  }

  @Test
  void currentUserLoginReturnsUsernameForUserDetailsPrincipal() {
    User userDetails = new User(
      "musician@example.com",
      "password",
      List.of(new SimpleGrantedAuthority("USER"))
    );
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
      userDetails,
      null,
      userDetails.getAuthorities()
    ));

    assertThat(SecurityUtils.getCurrentUserLogin()).isEqualTo("musician@example.com");
  }

  @Test
  void currentUserLoginReturnsNullForUnexpectedPrincipalType() {
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
      "raw-principal",
      null,
      List.of(new SimpleGrantedAuthority("USER"))
    ));

    assertThat(SecurityUtils.getCurrentUserLogin()).isNull();
  }
}
