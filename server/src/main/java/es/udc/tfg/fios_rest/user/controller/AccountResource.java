package es.udc.tfg.fios_rest.user.controller;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.common.exceptions.model.UserEmailExistsException;
import es.udc.tfg.fios_rest.common.exceptions.model.UserPhoneExistsException;
import es.udc.tfg.fios_rest.common.exceptions.web.AccountDisabledException;
import es.udc.tfg.fios_rest.common.exceptions.web.CredentialsAreNotValidException;
import es.udc.tfg.fios_rest.instrument.service.InstrumentService;
import es.udc.tfg.fios_rest.instrument.service.dto.InstrumentRef;
import es.udc.tfg.fios_rest.instrument.service.dto.UserInstrumentUpdateRequest;
import es.udc.tfg.fios_rest.user.service.UserService;
import es.udc.tfg.fios_rest.user.service.dto.AuthResponse;
import es.udc.tfg.fios_rest.user.service.dto.LoginRequest;
import es.udc.tfg.fios_rest.user.service.dto.UserCreateRequest;
import es.udc.tfg.fios_rest.user.service.dto.UserPrivateView;
import es.udc.tfg.fios_rest.user.service.dto.UserUpdatePasswordRequest;
import es.udc.tfg.fios_rest.user.service.dto.UserUpdateProfileImageRequest;
import es.udc.tfg.fios_rest.user.service.dto.UserUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountResource {

  private final UserService userService;
  private final InstrumentService instrumentService;

  public AccountResource(UserService userService, InstrumentService instrumentService) {
    this.userService = userService;
    this.instrumentService = instrumentService;
  }

  @PostMapping("/register")
  public ResponseEntity<UserPrivateView> register(
    @Valid @RequestBody UserCreateRequest request
  ) throws UserEmailExistsException, UserPhoneExistsException, NotFoundException {
    UserPrivateView user = userService.registerUser(request);
    return ResponseEntity.created(URI.create("/api/account/me")).body(user);
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(
    @Valid @RequestBody LoginRequest request
  ) throws CredentialsAreNotValidException, AccountDisabledException {
    return ResponseEntity.ok(userService.login(request));
  }

  @GetMapping("/me")
  public ResponseEntity<UserPrivateView> findMe() throws NotFoundException {
    return ResponseEntity.ok(userService.findMe());
  }

  @PutMapping("/me")
  public ResponseEntity<UserPrivateView> updateMe(
    @Valid @RequestBody UserUpdateRequest request
  ) throws NotFoundException, UserEmailExistsException, UserPhoneExistsException {
    return ResponseEntity.ok(userService.updateMe(request));
  }

  @PutMapping("/me/password")
  public ResponseEntity<Void> updateMyPassword(
    @Valid @RequestBody UserUpdatePasswordRequest request
  ) throws NotFoundException, OperationNotAllowed {
    userService.updateMyPassword(request);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/me/profile-image")
  public ResponseEntity<UserPrivateView> updateMyProfileImage(
    @Valid @RequestBody UserUpdateProfileImageRequest request
  ) throws NotFoundException {
    return ResponseEntity.ok(userService.updateMyProfileImage(request.profileImage()));
  }

  @DeleteMapping("/me/profile-image")
  public ResponseEntity<UserPrivateView> removeMyProfileImage() throws NotFoundException {
    return ResponseEntity.ok(userService.removeMyProfileImage());
  }

  @GetMapping("/me/instruments")
  public ResponseEntity<List<InstrumentRef>> findMyInstruments() throws NotFoundException {
    return ResponseEntity.ok(instrumentService.findMyInstruments());
  }

  @PutMapping("/me/instruments")
  public ResponseEntity<List<InstrumentRef>> updateMyInstruments(
    @Valid @RequestBody UserInstrumentUpdateRequest request
  ) throws NotFoundException {
    return ResponseEntity.ok(instrumentService.updateMyInstruments(request));
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout() {
    // JWT logout is stateless: the backend has no server session to invalidate.
    // The client completes logout by deleting its stored Bearer token.
    return ResponseEntity.noContent().build();
  }
}
