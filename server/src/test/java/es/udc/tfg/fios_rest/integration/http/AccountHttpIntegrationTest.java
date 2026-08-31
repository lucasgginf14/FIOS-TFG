package es.udc.tfg.fios_rest.integration.http;

import com.fasterxml.jackson.databind.JsonNode;
import es.udc.tfg.fios_rest.common.config.DatabaseLoader;
import es.udc.tfg.fios_rest.instrument.persistence.dao.InstrumentDao;
import es.udc.tfg.fios_rest.instrument.persistence.entity.Instrument;
import es.udc.tfg.fios_rest.instrument.persistence.entity.InstrumentCategory;
import es.udc.tfg.fios_rest.support.IntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountHttpIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private InstrumentDao instrumentDao;

  private static String badCredentialsMessage() {
    return "El email o la contrase" + (char) 241 + "a no son correctos.";
  }

  @Test
  void registerCreatesUserAndReturnsCreatedPayload() throws Exception {
    String body = objectMapper.writeValueAsString(Map.of(
      "name", "Laura",
      "firstSurname", "Lopez",
      "secondSurname", "Perez",
      "email", "laura@example.com",
      "password", "password123",
      "confirmPassword", "password123",
      "phone", "+34 600-111-222",
      "birthDate", LocalDate.of(1998, 4, 12).toString()
    ));

    mockMvc.perform(post("/api/account/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isCreated())
      .andExpect(header().string("Location", "/api/account/me"))
      .andExpect(jsonPath("$.email").value("laura@example.com"))
      .andExpect(jsonPath("$.name").value("Laura"))
      .andExpect(jsonPath("$.phone").value("+34 600-111-222"))
      .andExpect(jsonPath("$.password").doesNotExist());

    assertThat(userDao.findByEmail("laura@example.com")).isPresent();
  }

  @Test
  void registerCanCreateUserWithPrimaryInstrument() throws Exception {
    var guitar = instrumentDao.save(new Instrument("Registro Guitarra", InstrumentCategory.STRINGS));

    String body = objectMapper.writeValueAsString(Map.of(
      "name", "Laura",
      "firstSurname", "Lopez",
      "secondSurname", "Perez",
      "email", "laura-instrument@example.com",
      "password", "password123",
      "confirmPassword", "password123",
      "phone", "+34 600-111-223",
      "birthDate", LocalDate.of(1998, 4, 12).toString(),
      "instrumentIds", List.of(guitar.getId())
    ));

    mockMvc.perform(post("/api/account/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.email").value("laura-instrument@example.com"))
      .andExpect(jsonPath("$.instrument.id").value(guitar.getId()))
      .andExpect(jsonPath("$.instrument.name").value("Registro Guitarra"))
      .andExpect(jsonPath("$.instruments.length()").value(1))
      .andExpect(jsonPath("$.instruments[0].id").value(guitar.getId()));

    entityManager.flush();
    entityManager.clear();

    var createdUser = userDao.findByEmail("laura-instrument@example.com").orElseThrow();
    assertThat(createdUser.getInstruments())
      .extracting(Instrument::getId)
      .containsExactly(guitar.getId());
  }

  @Test
  void registerRejectsDuplicateEmail() throws Exception {
    createUser("duplicate@example.com", "password123");

    String body = objectMapper.writeValueAsString(Map.of(
      "name", "Laura",
      "firstSurname", "Lopez",
      "secondSurname", "Perez",
      "email", "duplicate@example.com",
      "password", "password123",
      "confirmPassword", "password123",
      "phone", "600111223",
      "birthDate", LocalDate.of(1998, 4, 12).toString()
    ));

    mockMvc.perform(post("/api/account/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isConflict())
      .andExpect(jsonPath("$.status").value(409))
      .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_EXISTS"))
      .andExpect(jsonPath("$.message").value("Ya existe una cuenta con ese email."))
      .andExpect(jsonPath("$.path").value("/api/account/register"));
  }

  @Test
  void loginReturnsJwtAndAuthenticatedUser() throws Exception {
    createUser("login@example.com", "password123");

    String response = mockMvc.perform(post("/api/account/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "email", "login@example.com",
          "password", "password123"
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.token").isNotEmpty())
      .andExpect(jsonPath("$.tokenType").value("Bearer"))
      .andExpect(jsonPath("$.user.email").value("login@example.com"))
      .andExpect(jsonPath("$.user.password").doesNotExist())
      .andReturn()
      .getResponse()
      .getContentAsString();

    JsonNode json = objectMapper.readTree(response);
    assertThat(json.get("token").asText()).isNotBlank();
  }

  @Test
  void seedPasswordMeetsCurrentPolicyAndAllowsLogin() throws Exception {
    createUser("seed-password-login@example.com", DatabaseLoader.SEED_PASSWORD);

    mockMvc.perform(post("/api/account/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "email", "seed-password-login@example.com",
          "password", DatabaseLoader.SEED_PASSWORD
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.user.email").value("seed-password-login@example.com"));
  }

  @Test
  void loginRejectsInvalidCredentials() throws Exception {
    createUser("bad-login@example.com", "password123");

    mockMvc.perform(post("/api/account/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "email", "bad-login@example.com",
          "password", "wrong-password"
        ))))
      .andExpect(status().isUnauthorized())
      .andExpect(jsonPath("$.status").value(401))
      .andExpect(jsonPath("$.code").value("BAD_CREDENTIALS"))
      .andExpect(jsonPath("$.message").value(badCredentialsMessage()));
  }

  @Test
  void loginRejectsMissingUserWithControlledUnauthorizedResponse() throws Exception {
    mockMvc.perform(post("/api/account/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "email", "missing-login@example.com",
          "password", "password123"
        ))))
      .andExpect(status().isUnauthorized())
      .andExpect(jsonPath("$.status").value(401))
      .andExpect(jsonPath("$.code").value("BAD_CREDENTIALS"))
      .andExpect(jsonPath("$.message").value(badCredentialsMessage()));
  }

  @Test
  void loginRejectsMissingUserAndWrongPasswordWithSamePublicResponse() throws Exception {
    createUser("same-login-response@example.com", "password123");

    MvcResult wrongPassword = mockMvc.perform(post("/api/account/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "email", "same-login-response@example.com",
          "password", "wrong-password"
        ))))
      .andExpect(status().isUnauthorized())
      .andReturn();

    MvcResult missingUser = mockMvc.perform(post("/api/account/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "email", "same-login-response-missing@example.com",
          "password", "wrong-password"
        ))))
      .andExpect(status().isUnauthorized())
      .andReturn();

    JsonNode wrongPasswordBody = objectMapper.readTree(wrongPassword.getResponse().getContentAsString());
    JsonNode missingUserBody = objectMapper.readTree(missingUser.getResponse().getContentAsString());

    assertThat(wrongPassword.getResponse().getStatus()).isNotEqualTo(500);
    assertThat(missingUser.getResponse().getStatus()).isNotEqualTo(500);
    assertThat(missingUserBody.get("status").asInt()).isEqualTo(wrongPasswordBody.get("status").asInt());
    assertThat(missingUserBody.get("code").asText()).isEqualTo(wrongPasswordBody.get("code").asText());
    assertThat(missingUserBody.get("message").asText()).isEqualTo(wrongPasswordBody.get("message").asText());
  }

  @Test
  void loginRejectsDisabledUserWithControlledForbiddenResponse() throws Exception {
    var disabledUser = createUser("disabled-login@example.com", "password123");
    disabledUser.deactivate();
    userDao.update(disabledUser);
    entityManager.flush();
    entityManager.clear();

    mockMvc.perform(post("/api/account/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "email", "disabled-login@example.com",
          "password", "password123"
        ))))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.status").value(403))
      .andExpect(jsonPath("$.code").value("ACCOUNT_DISABLED"))
      .andExpect(jsonPath("$.message").value(
        "Esta cuenta est" + (char) 225 + " desactivada. Contacta con administraci" + (char) 243 +
          "n si crees que es un error."
      ));
  }

  @Test
  void registerRejectsInvalidPhoneVariantsWithFieldError() throws Exception {
    mockMvc.perform(post("/api/account/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(registrationBody("short-phone@example.com", "12345678")))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.status").value(400))
      .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
      .andExpect(jsonPath("$.fieldErrors[?(@.field=='phone' && @.code=='INVALID_PHONE')]").exists());

    mockMvc.perform(post("/api/account/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(registrationBody("long-phone@example.com", "1234567890123456")))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.status").value(400))
      .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
      .andExpect(jsonPath("$.fieldErrors[?(@.field=='phone' && @.code=='INVALID_PHONE')]").exists());
  }

  @Test
  void registerRejectsInvalidEmailBlankFieldsAndTooLongTextWithFieldErrors() throws Exception {
    mockMvc.perform(post("/api/account/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(registrationBody("not-an-email", "600111225")))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
      .andExpect(jsonPath("$.fieldErrors[?(@.field=='email' && @.code=='INVALID_EMAIL')]").exists());

    String blankBody = objectMapper.writeValueAsString(Map.of(
      "name", "",
      "firstSurname", "",
      "email", "",
      "password", "",
      "confirmPassword", "",
      "phone", "",
      "birthDate", LocalDate.of(1998, 4, 12).toString()
    ));

    mockMvc.perform(post("/api/account/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(blankBody))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
      .andExpect(jsonPath("$.fieldErrors[?(@.field=='name' && @.code=='FIELD_REQUIRED')]").exists())
      .andExpect(jsonPath("$.fieldErrors[?(@.field=='firstSurname' && @.code=='FIELD_REQUIRED')]").exists())
      .andExpect(jsonPath("$.fieldErrors[?(@.field=='email' && @.code=='FIELD_REQUIRED')]").exists());

    String tooLongBody = objectMapper.writeValueAsString(Map.of(
      "name", "A".repeat(101),
      "firstSurname", "Lopez",
      "email", "too-long-name@example.com",
      "password", "password123",
      "confirmPassword", "password123",
      "phone", "600111226",
      "birthDate", LocalDate.of(1998, 4, 12).toString()
    ));

    mockMvc.perform(post("/api/account/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(tooLongBody))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
      .andExpect(jsonPath("$.fieldErrors[?(@.field=='name' && @.code=='INVALID_LENGTH')]").exists());
  }

  @Test
  void malformedJsonReturnsUniformBadRequestResponse() throws Exception {
    mockMvc.perform(post("/api/account/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"email\":\"broken@example.com\""))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.timestamp").exists())
      .andExpect(jsonPath("$.status").value(400))
      .andExpect(jsonPath("$.error").value("Bad Request"))
      .andExpect(jsonPath("$.code").value("MALFORMED_JSON"))
      .andExpect(jsonPath("$.message").value(
        "No pudimos leer la informaci" + (char) 243 + "n enviada. Revisa los datos e int" +
          (char) 233 + "ntalo otra vez."
      ))
      .andExpect(jsonPath("$.path").value("/api/account/register"))
      .andExpect(jsonPath("$.fieldErrors").isArray());
  }

  @Test
  void registerRejectsWeakPassword() throws Exception {
    String body = objectMapper.writeValueAsString(Map.of(
      "name", "Laura",
      "firstSurname", "Lopez",
      "secondSurname", "Perez",
      "email", "weak-password@example.com",
      "password", "aaaaaaaa",
      "confirmPassword", "aaaaaaaa",
      "phone", "600111224",
      "birthDate", LocalDate.of(1998, 4, 12).toString()
    ));

    mockMvc.perform(post("/api/account/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.code").value("PASSWORD_WEAK"));
  }

  @Test
  void meRequiresValidTokenAndReturnsCurrentUserWhenAuthenticated() throws Exception {
    var user = createUser("me@example.com", "password123");

    mockMvc.perform(get("/api/account/me"))
      .andExpect(status().isUnauthorized())
      .andExpect(jsonPath("$.status").value(401))
      .andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"))
      .andExpect(jsonPath("$.path").value("/api/account/me"));

    mockMvc.perform(get("/api/account/me")
        .header("Authorization", "Bearer " + tokenFor(user)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.email").value("me@example.com"))
      .andExpect(jsonPath("$.instruments").isArray())
      .andExpect(jsonPath("$.instruments.length()").value(0));
  }

  @Test
  void meReturnsCurrentUserPrimaryInstrument() throws Exception {
    var user = createUser("me-instruments@example.com", "password123");
    var bass = instrumentDao.save(new Instrument("Baixo", InstrumentCategory.STRINGS));

    user.setPrimaryInstrument(bass);
    userDao.update(user);
    entityManager.flush();
    entityManager.clear();

    mockMvc.perform(get("/api/account/me")
        .header("Authorization", "Bearer " + tokenFor(user)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.email").value("me-instruments@example.com"))
      .andExpect(jsonPath("$.instrument.name").value("Baixo"))
      .andExpect(jsonPath("$.instruments.length()").value(1))
      .andExpect(jsonPath("$.instruments[0].name").value("Baixo"));
  }

  @Test
  void meRejectsMalformedAndInactiveUserTokens() throws Exception {
    var user = createUser("inactive-token@example.com", "password123");
    String token = tokenFor(user);
    user.deactivate();
    userDao.update(user);

    mockMvc.perform(get("/api/account/me")
        .header("Authorization", "Bearer malformed-token"))
      .andExpect(status().isUnauthorized());

    mockMvc.perform(get("/api/account/me")
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isUnauthorized());
  }

  @Test
  void publicEndpointsIgnoreMalformedTokenAndContinueAsAnonymous() throws Exception {
    mockMvc.perform(get("/api/musical-spaces")
        .header("Authorization", "Bearer malformed-token"))
      .andExpect(status().isOk());
  }

  @Test
  void tokenCreatedWithUserIdSurvivesEmailChangeInDatabase() throws Exception {
    var user = createUser("jwt-id-subject@example.com", "password123");
    String token = tokenFor(user);

    user.setEmail("jwt-id-subject-updated@example.com");
    userDao.update(user);
    entityManager.flush();
    entityManager.clear();

    mockMvc.perform(get("/api/account/me")
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.email").value("jwt-id-subject-updated@example.com"));
  }

  @Test
  void updateProfileAndPasswordApplyBusinessChanges() throws Exception {
    var user = createUser("profile@example.com", "password123");
    String token = tokenFor(user);

    mockMvc.perform(put("/api/account/me")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "name", "Updated",
          "phone", "699888777"
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("Updated"))
      .andExpect(jsonPath("$.phone").value("699888777"));

    mockMvc.perform(put("/api/account/me/password")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "currentPassword", "password123",
          "newPassword", "newpassword123",
          "confirmNewPassword", "newpassword123"
        ))))
      .andExpect(status().isNoContent());

    mockMvc.perform(post("/api/account/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "email", "profile@example.com",
          "password", "newpassword123"
        ))))
      .andExpect(status().isOk());
  }

  @Test
  void updateProfileCanSetChangeKeepAndClearPrimaryInstrument() throws Exception {
    var user = createUser("profile-instrument@example.com", "password123");
    var guitar = instrumentDao.save(new Instrument("Perfil Guitarra", InstrumentCategory.STRINGS));
    var bass = instrumentDao.save(new Instrument("Perfil Bajo", InstrumentCategory.STRINGS));
    String token = tokenFor(user);

    mockMvc.perform(put("/api/account/me")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "name", "Instrumental",
          "instrumentIds", List.of(guitar.getId())
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("Instrumental"))
      .andExpect(jsonPath("$.instrument.name").value("Perfil Guitarra"))
      .andExpect(jsonPath("$.instruments.length()").value(1))
      .andExpect(jsonPath("$.instruments[0].id").value(guitar.getId()));

    mockMvc.perform(put("/api/account/me")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "phone", "699111222"
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.phone").value("699111222"))
      .andExpect(jsonPath("$.instrument.name").value("Perfil Guitarra"))
      .andExpect(jsonPath("$.instruments.length()").value(1));

    mockMvc.perform(put("/api/account/me")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "instrumentIds", List.of(bass.getId())
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.instrument.name").value("Perfil Bajo"))
      .andExpect(jsonPath("$.instruments.length()").value(1))
      .andExpect(jsonPath("$.instruments[0].id").value(bass.getId()));

    mockMvc.perform(put("/api/account/me")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "instrumentIds", List.of()
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.instrument").doesNotExist())
      .andExpect(jsonPath("$.instruments.length()").value(0));

    entityManager.flush();
    entityManager.clear();

    Number instrumentRows = (Number) entityManager
      .createNativeQuery("select count(*) from user_instrument where user_id = :userId")
      .setParameter("userId", user.getId())
      .getSingleResult();

    assertThat(userDao.findById(user.getId()).orElseThrow().getInstruments()).isEmpty();
    assertThat(instrumentRows.longValue()).isZero();
  }

  @Test
  void updateProfileRejectsUnknownPrimaryInstrument() throws Exception {
    var user = createUser("profile-instrument-missing@example.com", "password123");
    var guitar = instrumentDao.save(new Instrument("Perfil Guitarra Persistente", InstrumentCategory.STRINGS));
    user.setPrimaryInstrument(guitar);
    userDao.update(user);
    entityManager.flush();
    entityManager.clear();

    mockMvc.perform(put("/api/account/me")
        .header("Authorization", "Bearer " + tokenFor(user))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "instrumentIds", List.of(999999L)
        ))))
      .andExpect(status().isNotFound());

    entityManager.flush();
    entityManager.clear();

    assertThat(userDao.findById(user.getId()).orElseThrow().getInstruments())
      .extracting(Instrument::getId)
      .containsExactly(guitar.getId());
  }

  @Test
  void myInstrumentsEndpointMaintainsASinglePrimaryInstrument() throws Exception {
    var user = createUser("single-instrument-endpoint@example.com", "password123");
    var guitar = instrumentDao.save(new Instrument("Endpoint Guitarra", InstrumentCategory.STRINGS));
    var drums = instrumentDao.save(new Instrument("Endpoint Bater\u00eda", InstrumentCategory.PERCUSSION));
    String token = tokenFor(user);

    mockMvc.perform(put("/api/account/me/instruments")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "instrumentIds", List.of(guitar.getId())
        ))))
      .andExpect(status().isUnauthorized());

    mockMvc.perform(put("/api/account/me/instruments")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "instrumentIds", List.of(guitar.getId())
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1))
      .andExpect(jsonPath("$[0].id").value(guitar.getId()));

    mockMvc.perform(put("/api/account/me/instruments")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "instrumentIds", List.of(drums.getId())
        ))))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1))
      .andExpect(jsonPath("$[0].id").value(drums.getId()));

    mockMvc.perform(put("/api/account/me/instruments")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "instrumentIds", List.of(guitar.getId(), drums.getId())
        ))))
      .andExpect(status().isBadRequest());

    mockMvc.perform(get("/api/account/me/instruments")
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(1))
      .andExpect(jsonPath("$[0].id").value(drums.getId()));
  }

  @Test
  void updatePasswordRejectsWeakPasswordWithSamePolicyAsRegistration() throws Exception {
    var user = createUser("weak-profile-password@example.com", "password123");
    String token = tokenFor(user);

    mockMvc.perform(put("/api/account/me/password")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "currentPassword", "password123",
          "newPassword", "aaaaaaaa",
          "confirmNewPassword", "aaaaaaaa"
        ))))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.code").value("PASSWORD_WEAK"));
  }

  @Test
  void updateMeRejectsEmailChangesAndKeepsCurrentTokenUsable() throws Exception {
    var user = createUser("stable-email@example.com", "password123");
    String token = tokenFor(user);

    mockMvc.perform(put("/api/account/me")
        .header("Authorization", "Bearer " + token)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of(
          "email", "changed-email@example.com",
          "name", "Changed"
        ))))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
      .andExpect(jsonPath("$.message").value("No puedes cambiar el email desde el perfil."));

    assertThat(userDao.findByEmail("stable-email@example.com")).isPresent();
    assertThat(userDao.findByEmail("changed-email@example.com")).isEmpty();

    mockMvc.perform(get("/api/account/me")
        .header("Authorization", "Bearer " + token))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.email").value("stable-email@example.com"));
  }

  private String registrationBody(String email, String phone) throws Exception {
    return objectMapper.writeValueAsString(Map.of(
      "name", "Laura",
      "firstSurname", "Lopez",
      "secondSurname", "Perez",
      "email", email,
      "password", "password123",
      "confirmPassword", "password123",
      "phone", phone,
      "birthDate", LocalDate.of(1998, 4, 12).toString()
    ));
  }
}
