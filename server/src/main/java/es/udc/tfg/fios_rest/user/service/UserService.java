package es.udc.tfg.fios_rest.user.service;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.common.exceptions.model.UserEmailExistsException;
import es.udc.tfg.fios_rest.common.exceptions.model.UserPhoneExistsException;
import es.udc.tfg.fios_rest.common.exceptions.web.AccountDisabledException;
import es.udc.tfg.fios_rest.common.exceptions.web.CredentialsAreNotValidException;
import es.udc.tfg.fios_rest.common.security.SecurityUtils;
import es.udc.tfg.fios_rest.common.security.TokenProvider;
import es.udc.tfg.fios_rest.instrument.persistence.dao.InstrumentDao;
import es.udc.tfg.fios_rest.instrument.persistence.entity.Instrument;
import es.udc.tfg.fios_rest.user.persistence.dao.UserDao;
import es.udc.tfg.fios_rest.user.persistence.entity.PlatformRole;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import es.udc.tfg.fios_rest.user.service.dto.AuthResponse;
import es.udc.tfg.fios_rest.user.service.dto.LoginRequest;
import es.udc.tfg.fios_rest.user.service.dto.UserCreateRequest;
import es.udc.tfg.fios_rest.user.service.dto.UserPrivateView;
import es.udc.tfg.fios_rest.user.service.dto.UserUpdatePasswordRequest;
import es.udc.tfg.fios_rest.user.service.dto.UserUpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserService {
    private static final String TOKEN_TYPE = "Bearer";
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private InstrumentDao instrumentDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenProvider tokenProvider;

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }

        String normalizedValue = value.trim();
        return normalizedValue.isEmpty() ? null : normalizedValue;
    }

    private String normalizeRequiredText(String value, String fieldName) {
        String normalizedValue = normalizeText(value);
        if (normalizedValue == null) {
            throw new IllegalArgumentException("The " + fieldName + " is obligatory");
        }
        return normalizedValue;
    }

    private String normalizeEmail(String email, String fieldName) {
        String normalizedEmail = normalizeRequiredText(email, fieldName).toLowerCase();

        if (!normalizedEmail.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("The email format is not valid");
        }

        return normalizedEmail;
    }

    private void validateBirthDate(LocalDate birthDate, boolean required) {
        if (birthDate == null) {
            if (required) {
                throw new IllegalArgumentException("The birth date is obligatory");
            }
            return;
        }

        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("The birth date cannot be in the future");
        }
    }

    private void validatePasswordStrength(String password, String fieldName) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("The " + fieldName + " is obligatory");
        }

        if (password.length() < 8 || password.length() > 100) {
            throw new IllegalArgumentException("The " + fieldName + " must have between 8 and 100 characters");
        }

        if (!password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("The " + fieldName + " must include at least one letter and one number");
        }
    }

    private void validateUniqueEmail(String email, Long currentUserId) throws UserEmailExistsException {
        Optional<User> existingUser = userDao.findByEmail(email);

        if (existingUser.isPresent() && !Objects.equals(existingUser.get().getId(), currentUserId)) {
            throw new UserEmailExistsException(email);
        }
    }

    private void validateUniquePhone(String phone, Long currentUserId) throws UserPhoneExistsException {
        Optional<User> existingUser = userDao.findByPhone(phone);

        if (existingUser.isPresent() && !Objects.equals(existingUser.get().getId(), currentUserId)) {
            throw new UserPhoneExistsException(phone);
        }
    }

    private User getCurrentUserEntity() throws NotFoundException {
        String currentUserLogin = SecurityUtils.getCurrentUserLogin();

        if (currentUserLogin == null) {
            throw new NotFoundException("Current user", User.class);
        }

        return userDao.findByEmail(currentUserLogin.toLowerCase())
                .orElseThrow(() -> new NotFoundException("Current user", User.class));
    }

    public UserPrivateView registerUser(UserCreateRequest request)
            throws UserEmailExistsException, UserPhoneExistsException, NotFoundException {

        String name = normalizeRequiredText(request.name(), "name");
        String firstSurname = normalizeRequiredText(request.firstSurname(), "first surname");
        String secondSurname = normalizeText(request.secondSurname());
        String email = normalizeEmail(request.email(), "email");
        String phone = normalizeRequiredText(request.phone(), "phone number");

        if (!request.password().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Password and confirm password do not match");
        }

        validatePasswordStrength(request.password(), "password");
        validateBirthDate(request.birthDate(), true);
        validateUniqueEmail(email, null);
        validateUniquePhone(phone, null);

        User user = new User(
                name,
                firstSurname,
                secondSurname,
                email,
                passwordEncoder.encode(request.password()),
                phone,
                null,
                request.birthDate(),
                true,
                PlatformRole.USER
        );

        if (request.instrumentIds() != null) {
            updatePrimaryInstrument(user, request.instrumentIds());
        }

        userDao.save(user);
        return UserPrivateView.from(user);
    }

    @Transactional(
            readOnly = true,
            noRollbackFor = {
                    CredentialsAreNotValidException.class,
                    AccountDisabledException.class,
                    AuthenticationException.class
            }
    )
    public AuthResponse login(LoginRequest request) throws CredentialsAreNotValidException, AccountDisabledException {
        String email = normalizeEmail(request.email(), "email");
        User inactiveUser = userDao.findByEmail(email)
                .filter(user -> !user.isActive())
                .orElse(null);

        if (inactiveUser != null) {
            if (passwordEncoder.matches(request.password(), inactiveUser.getPassword())) {
                logger.info("Disabled login attempt for {}", email);
                throw new AccountDisabledException();
            }

            logger.info("Invalid login attempt for {}", email);
            throw new CredentialsAreNotValidException("Bad Credentials");
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, request.password());

        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = tokenProvider.createToken(authentication);
            User user = userDao.findByEmail(email)
                    .orElseThrow(() -> new CredentialsAreNotValidException("Bad Credentials"));

            return new AuthResponse(token, TOKEN_TYPE, UserPrivateView.from(user));
        } catch (DisabledException e) {
            logger.info("Disabled login attempt for {}", email);
            throw new AccountDisabledException();
        } catch (AuthenticationException e) {
            logger.info("Invalid login attempt for {}", email);
            throw new CredentialsAreNotValidException("Bad Credentials");
        }
    }

    @Transactional(readOnly = true)
    public UserPrivateView findMe() throws NotFoundException {
        return UserPrivateView.from(getCurrentUserEntity());
    }

    @Transactional(readOnly = true)
    public Long getCurrentUserId() throws NotFoundException {
        return getCurrentUserEntity().getId();
    }

    @Deprecated
    @Transactional(readOnly = true)
    public UserPrivateView getCurrentUserWithAuthority() {
        try {
            return findMe();
        } catch (NotFoundException e) {
            throw new IllegalStateException("Current user could not be resolved", e);
        }
    }

    public UserPrivateView updateMe(UserUpdateRequest request)
            throws NotFoundException, UserPhoneExistsException {

        User user = getCurrentUserEntity();

        if (request.email() != null) {
            validateEmailIsUnchanged(request.email(), user.getEmail());
        }

        if (request.name() != null) {
            String normalizedName = normalizeRequiredText(request.name(), "name");
            user.setName(normalizedName);
        }

        if (request.firstSurname() != null) {
            String normalizedFirstSurname = normalizeRequiredText(request.firstSurname(), "first surname");
            user.setFirstSurname(normalizedFirstSurname);
        }

        if (request.secondSurname() != null) {
            user.setSecondSurname(normalizeText(request.secondSurname()));
        }

        if (request.phone() != null) {
            String normalizedPhone = normalizeRequiredText(request.phone(), "phone number");
            validateUniquePhone(normalizedPhone, user.getId());
            user.setPhone(normalizedPhone);
        }

        if (request.birthDate() != null) {
            validateBirthDate(request.birthDate(), false);
            user.setBirthDate(request.birthDate());
        }

        if (request.instrumentIds() != null) {
            updatePrimaryInstrument(user, request.instrumentIds());
        }

        userDao.update(user);
        return UserPrivateView.from(user);
    }

    private void updatePrimaryInstrument(User user, Set<Long> instrumentIds) throws NotFoundException {
        if (instrumentIds.size() > 1) {
            throw new IllegalArgumentException("Only one principal instrument can be associated with a user");
        }

        if (instrumentIds.isEmpty()) {
            user.setPrimaryInstrument(null);
            return;
        }

        Long instrumentId = instrumentIds.iterator().next();

        if (instrumentId == null || instrumentId <= 0) {
            throw new IllegalArgumentException("The instrument id must be positive");
        }

        Instrument instrument = instrumentDao.findById(instrumentId)
                .orElseThrow(() -> new NotFoundException(instrumentId.toString(), Instrument.class));
        user.setPrimaryInstrument(instrument);
    }

    private void validateEmailIsUnchanged(String requestedEmail, String currentEmail) {
        String normalizedEmail = normalizeEmail(requestedEmail, "email");

        if (!normalizedEmail.equalsIgnoreCase(currentEmail)) {
            throw new IllegalArgumentException("Email cannot be changed from this endpoint");
        }
    }

    public void updateMyPassword(UserUpdatePasswordRequest request)
            throws NotFoundException, OperationNotAllowed {

        User user = getCurrentUserEntity();

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new OperationNotAllowed("Current password is incorrect");
        }

        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new IllegalArgumentException("The new password and confirm password do not match");
        }

        validatePasswordStrength(request.newPassword(), "new password");

        if (request.newPassword().equals(request.currentPassword())) {
            throw new IllegalArgumentException("The new password must be different from the current password");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userDao.update(user);
    }

    public UserPrivateView updateMyProfileImage(String profileImage) throws NotFoundException {
        User user = getCurrentUserEntity();

        String normalizedProfileImage = normalizeRequiredText(profileImage, "profile image");
        user.setProfileImage(normalizedProfileImage);

        userDao.update(user);
        return UserPrivateView.from(user);
    }

    public UserPrivateView removeMyProfileImage() throws NotFoundException {
        User user = getCurrentUserEntity();

        user.removeProfileImage();
        userDao.update(user);

        return UserPrivateView.from(user);
    }
}
