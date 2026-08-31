package es.udc.tfg.fios_rest.common.security;

import es.udc.tfg.fios_rest.common.config.Properties;
import es.udc.tfg.fios_rest.user.persistence.dao.UserDao;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

@Component
public class TokenProvider {
  private static final String AUTHORITIES_KEY = "auth";

  @Autowired
  private Properties properties;

  @Autowired
  private UserDao userDao;

  public boolean validateToken(String authToken) {
    Claims claims = parseClaims(authToken);
    return resolveTokenUser(claims.getSubject())
      .filter(es.udc.tfg.fios_rest.user.persistence.entity.User::isActive)
      .isPresent();
  }

  public Authentication getAuthentication(String authToken) {
    Claims claims = parseClaims(authToken);
    es.udc.tfg.fios_rest.user.persistence.entity.User currentUser = resolveTokenUser(claims.getSubject())
      .filter(es.udc.tfg.fios_rest.user.persistence.entity.User::isActive)
      .orElseThrow(() -> new IllegalArgumentException("Invalid user token"));

    GrantedAuthority authority = new SimpleGrantedAuthority(currentUser.getPlatformRole().name());
    Collection<GrantedAuthority> authorities = Collections.singleton(authority);
    User user = new User(currentUser.getEmail(), "", authorities);
    return new UsernamePasswordAuthenticationToken(user, authToken, authorities);
  }

  public String createToken(Authentication authentication) {
    es.udc.tfg.fios_rest.user.persistence.entity.User currentUser = resolveTokenUser(authentication.getName())
      .filter(es.udc.tfg.fios_rest.user.persistence.entity.User::isActive)
      .orElseThrow(() -> new IllegalArgumentException("Invalid user authentication"));
    String authority = currentUser.getPlatformRole().name();

    long now = (new Date()).getTime();
    Date validity = new Date(now + (properties.getJwtValidity() * 1000));

    SecretKey key = Keys.hmacShaKeyFor(properties.getJwtSecretKey().getBytes(StandardCharsets.UTF_8));
    return Jwts.builder().subject(currentUser.getId().toString()).claim(AUTHORITIES_KEY, authority).signWith(key)
        .expiration(validity).compact();
  }

  private Claims parseClaims(String authToken) {
    SecretKey key = Keys.hmacShaKeyFor(properties.getJwtSecretKey().getBytes(StandardCharsets.UTF_8));
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(authToken).getPayload();
  }

  private Optional<es.udc.tfg.fios_rest.user.persistence.entity.User> resolveTokenUser(String subject) {
    if (subject == null || subject.isBlank()) {
      return Optional.empty();
    }

    try {
      return userDao.findById(Long.valueOf(subject));
    } catch (NumberFormatException e) {
      return userDao.findByEmail(subject.toLowerCase());
    }
  }
}
