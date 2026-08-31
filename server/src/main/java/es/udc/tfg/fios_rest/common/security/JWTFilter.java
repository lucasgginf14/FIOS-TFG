package es.udc.tfg.fios_rest.common.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class JWTFilter extends GenericFilterBean {
  private final Logger logger = LoggerFactory.getLogger(JWTFilter.class);

  private final TokenProvider tokenProvider;

  public JWTFilter(TokenProvider tokenProvider) {
    this.tokenProvider = tokenProvider;
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {
    HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
    String jwt = resolveToken(httpServletRequest);
    if (StringUtils.hasText(jwt)) {
      try {
        if (!this.tokenProvider.validateToken(jwt)) {
          SecurityContextHolder.clearContext();
        } else {
          Authentication authentication = this.tokenProvider.getAuthentication(jwt);
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      } catch (ExpiredJwtException eje) {
        logger.info("Expired JWT token - {}", eje.getMessage());
        SecurityContextHolder.clearContext();
      } catch (SignatureException eje) {
        logger.info("Invalid JWT signature - {}", eje.getMessage());
        SecurityContextHolder.clearContext();
      } catch (JwtException | IllegalArgumentException eje) {
        logger.info("Invalid JWT token - {}", eje.getMessage());
        SecurityContextHolder.clearContext();
      }
    }

    filterChain.doFilter(servletRequest, servletResponse);
  }

  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(JWTConfigurer.AUTHORIZATION_HEADER);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
