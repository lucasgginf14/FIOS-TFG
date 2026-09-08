package es.udc.tfg.fios_rest.common.config;

import es.udc.tfg.fios_rest.common.security.JWTConfigurer;
import es.udc.tfg.fios_rest.common.security.MyAccessDeniedHandler;
import es.udc.tfg.fios_rest.common.security.MyUnauthorizedEntryPoint;
import es.udc.tfg.fios_rest.common.security.MyUserDetailsService;
import es.udc.tfg.fios_rest.common.security.TokenProvider;
import es.udc.tfg.fios_rest.user.persistence.entity.PlatformRole;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration {

  @Autowired
  private Properties properties;

  @Autowired
  private MyUnauthorizedEntryPoint myUnauthorizedEntryPoint;

  @Autowired
  private MyAccessDeniedHandler myAccessDeniedHandler;

  @Autowired
  private TokenProvider tokenProvider;

  @Autowired
  private MyUserDetailsService myUserDetailsService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // @formatter:off
    http
      .csrf((csrf) -> csrf.disable())
      .exceptionHandling((exceptionHandling) -> exceptionHandling
        .authenticationEntryPoint(myUnauthorizedEntryPoint)
        .accessDeniedHandler(myAccessDeniedHandler)
      )
      .headers((headers) -> headers.frameOptions((frameOptions) -> frameOptions.disable()))
      .sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests((authorize) -> authorize
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/account/register").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/account/login").permitAll()
        .requestMatchers("/api/account/**").authenticated()
        .requestMatchers(HttpMethod.GET, "/api/home/**").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/images/**").permitAll()
        .requestMatchers("/api/images/**").authenticated()
        .requestMatchers(HttpMethod.GET, "/api/instruments/**").permitAll()
        .requestMatchers("/api/instruments/**").hasAnyAuthority(PlatformRole.ADMIN.toString())
        .requestMatchers(HttpMethod.GET, "/api/equipments").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/equipments/**").permitAll()
        .requestMatchers("/api/equipments/**").hasAnyAuthority(PlatformRole.ADMIN.toString())
        .requestMatchers(HttpMethod.GET, "/api/band-recruitments/me").authenticated()
        .requestMatchers(HttpMethod.GET, "/api/band-recruitments").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/band-recruitments/**").permitAll()
        .requestMatchers("/api/band-recruitments/**").authenticated()
        .requestMatchers(HttpMethod.GET, "/api/musical-spaces/me").authenticated()
        .requestMatchers(HttpMethod.GET, "/api/musical-spaces/me/reservations").authenticated()
        .requestMatchers(HttpMethod.GET, "/api/musical-spaces/*/exceptions").authenticated()
        .requestMatchers(HttpMethod.GET, "/api/musical-spaces").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/musical-spaces/**").permitAll()
        .requestMatchers("/api/musical-spaces/**").authenticated()
        .requestMatchers(HttpMethod.GET, "/api/search/history").authenticated()
        .requestMatchers(HttpMethod.POST, "/api/search/natural-language").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/search/**").permitAll()
        .requestMatchers("/api/geocoding/**").authenticated()
        .requestMatchers("/api/ticketmaster/**").authenticated()
        .requestMatchers("/api/ai/search-parser/**").authenticated()
        .requestMatchers("/api/schedules/**").authenticated()
        .requestMatchers("/api/exceptions/**").authenticated()
        .requestMatchers("/api/space-equipment/**").authenticated()
        .requestMatchers("/api/reservations/**").authenticated()
        .requestMatchers("/api/event-purchases/**").authenticated()
        .requestMatchers(HttpMethod.GET, "/api/events/*/reservations").authenticated()
        .requestMatchers(HttpMethod.GET, "/api/events/**").permitAll()
        .requestMatchers("/api/events/**").authenticated()
        .requestMatchers(HttpMethod.GET, "/api/bands/me").authenticated()
        .requestMatchers(HttpMethod.GET, "/api/bands/*/members/candidates").authenticated()
        .requestMatchers(HttpMethod.GET, "/api/bands/**").permitAll()
        .requestMatchers("/api/bands/**").authenticated()
        .requestMatchers("/api/admin/**").hasAnyAuthority(PlatformRole.ADMIN.toString())
        .requestMatchers("/api/space-reviews/**").authenticated()
        .requestMatchers("/api/user-reviews/**").authenticated()
        .requestMatchers("/api/users/*/reviews").authenticated()
        .requestMatchers("/api/users/*/rating").authenticated()
        .requestMatchers("/api/favorites/**").authenticated()
        .requestMatchers("/api/messages/**").authenticated()
        .requestMatchers("/**").authenticated())
      .with(securityConfigurerAdapter(), Customizer.withDefaults());
    // @formatter:on
    return http.build();
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("*").allowedOrigins(properties.getClientHost());
      }
    };
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
    throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Autowired
  public void configureAuth(AuthenticationManagerBuilder auth) {
    try {
      auth.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder);
    } catch (Exception e) {
      throw new BeanInitializationException("SecurityConfiguration.configureAuth failed", e);
    }
  }

  private JWTConfigurer securityConfigurerAdapter() {
    return new JWTConfigurer(tokenProvider);
  }
}
