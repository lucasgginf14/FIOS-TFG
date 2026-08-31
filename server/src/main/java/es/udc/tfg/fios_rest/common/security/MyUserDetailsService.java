package es.udc.tfg.fios_rest.common.security;


import es.udc.tfg.fios_rest.user.persistence.dao.UserDao;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;

@Component
public class MyUserDetailsService implements UserDetailsService {
  private final Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);

  @Autowired
  private UserDao userDAO;

  @Override
  @Transactional(
    readOnly = true,
    noRollbackFor = {UsernameNotFoundException.class, DisabledException.class}
  )
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    String normalizedEmail = StringUtils.hasText(email) ? email.trim().toLowerCase() : "";
    User user = userDAO.findByEmail(normalizedEmail)
      .orElseThrow(() -> new UsernameNotFoundException("Bad Credentials"));
    if (!user.isActive()) {
      throw new DisabledException("User account is disabled");
    }
    logger.info("Loaded user {} with authority {}", normalizedEmail, user.getPlatformRole().name());
    GrantedAuthority authority = new SimpleGrantedAuthority(user.getPlatformRole().name());
    return new org.springframework.security.core.userdetails.User(normalizedEmail, user.getPassword(),
      Collections.singleton(authority));
  }
}
