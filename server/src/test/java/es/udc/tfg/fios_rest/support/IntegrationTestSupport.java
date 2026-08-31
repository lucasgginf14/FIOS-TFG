package es.udc.tfg.fios_rest.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.udc.tfg.fios_rest.FiosRestApplication;
import es.udc.tfg.fios_rest.band.persistence.dao.BandDao;
import es.udc.tfg.fios_rest.band.persistence.entity.Band;
import es.udc.tfg.fios_rest.common.security.TokenProvider;
import es.udc.tfg.fios_rest.event.persistence.dao.EventDao;
import es.udc.tfg.fios_rest.event.persistence.entity.Event;
import es.udc.tfg.fios_rest.event.persistence.entity.EventSource;
import es.udc.tfg.fios_rest.event.persistence.entity.EventStatus;
import es.udc.tfg.fios_rest.event.persistence.entity.EventType;
import es.udc.tfg.fios_rest.exceptions.persistence.dao.SpaceAvailabilityExceptionDao;
import es.udc.tfg.fios_rest.exceptions.persistence.entity.SpaceAvailabilityException;
import es.udc.tfg.fios_rest.exceptions.persistence.entity.SpaceAvailabilityExceptionType;
import es.udc.tfg.fios_rest.message.persistence.dao.MessageDao;
import es.udc.tfg.fios_rest.message.persistence.entity.Message;
import es.udc.tfg.fios_rest.musicalspace.persistence.dao.MusicalSpaceDao;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpace;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceApprovalStatus;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceLocation;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceType;
import es.udc.tfg.fios_rest.reservationsession.persistence.dao.ReservationSessionDao;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSession;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSessionState;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSessionType;
import es.udc.tfg.fios_rest.schedule.persistence.dao.ScheduleDao;
import es.udc.tfg.fios_rest.schedule.persistence.entity.Schedule;
import es.udc.tfg.fios_rest.search.persistence.dao.SearchDao;
import es.udc.tfg.fios_rest.spacereview.persistence.dao.SpaceReviewDao;
import es.udc.tfg.fios_rest.spacereview.persistence.entity.SpaceReview;
import es.udc.tfg.fios_rest.user.persistence.dao.UserDao;
import es.udc.tfg.fios_rest.user.persistence.entity.PlatformRole;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import es.udc.tfg.fios_rest.userreview.persistence.dao.UserReviewDao;
import es.udc.tfg.fios_rest.userreview.persistence.entity.UserReview;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@SpringBootTest(classes = FiosRestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class IntegrationTestSupport {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  protected PasswordEncoder passwordEncoder;

  @Autowired
  protected TokenProvider tokenProvider;

  @Autowired
  protected UserDao userDao;

  @Autowired
  protected MusicalSpaceDao musicalSpaceDao;

  @Autowired
  protected ScheduleDao scheduleDao;

  @Autowired
  protected SpaceAvailabilityExceptionDao spaceAvailabilityExceptionDao;

  @Autowired
  protected ReservationSessionDao reservationSessionDao;

  @Autowired
  protected MessageDao messageDao;

  @Autowired
  protected SpaceReviewDao spaceReviewDao;

  @Autowired
  protected UserReviewDao userReviewDao;

  @Autowired
  protected EventDao eventDao;

  @Autowired
  protected SearchDao searchDao;

  @Autowired
  protected EntityManager entityManager;

  @Autowired(required = false)
  protected BandDao bandDao;

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  protected User createUser(String email, String password) {
    return createUser(email, password, PlatformRole.USER);
  }

  protected User createAdmin(String email, String password) {
    return createUser(email, password, PlatformRole.ADMIN);
  }

  protected User createUser(String email, String password, PlatformRole role) {
    User user = new User(
      "Test",
      "User",
      role == PlatformRole.ADMIN ? "Admin" : "Member",
      email,
      passwordEncoder.encode(password),
      "phone-" + Math.abs(email.hashCode()),
      null,
      LocalDate.of(1995, 1, 1),
      true,
      role
    );
    return userDao.save(user);
  }

  protected MusicalSpace createSpace(User manager, MusicalSpaceApprovalStatus approvalStatus, boolean active) {
    MusicalSpace space = new MusicalSpace(
      "Space " + Math.abs((manager.getEmail() + approvalStatus + active).hashCode()),
      "Test musical space",
      MusicalSpaceType.REHEARSAL_ROOM,
      8,
      "image.png",
      35.0,
      true,
      new MusicalSpaceLocation("Spain", "A Coruna", "Santiago de Compostela", "Rua Test", "1", null, "15701", 42.8782, -8.5448),
      manager
    );
    space.setApprovalStatus(approvalStatus);
    space = musicalSpaceDao.save(space);
    if (!active) {
      space.deactivate();
      return musicalSpaceDao.update(space);
    }
    return space;
  }

  protected Schedule createSchedule(MusicalSpace space, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, BigDecimal price) {
    Schedule schedule = new Schedule(dayOfWeek, startTime, endTime, price, space);
    return scheduleDao.save(schedule);
  }

  protected SpaceAvailabilityException createException(
    MusicalSpace space,
    LocalDate date,
    LocalTime startTime,
    LocalTime endTime,
    SpaceAvailabilityExceptionType type
  ) {
    return createException(
      space,
      date,
      startTime,
      endTime,
      type,
      SpaceAvailabilityExceptionType.CUSTOM_AVAILABILITY.equals(type) ? BigDecimal.valueOf(20) : null
    );
  }

  protected SpaceAvailabilityException createException(
    MusicalSpace space,
    LocalDate date,
    LocalTime startTime,
    LocalTime endTime,
    SpaceAvailabilityExceptionType type,
    BigDecimal price
  ) {
    SpaceAvailabilityException spaceException = new SpaceAvailabilityException(
      date,
      startTime,
      endTime,
      type,
      "Test exception",
      price,
      space
    );
    return spaceAvailabilityExceptionDao.save(spaceException);
  }

  protected ReservationSession createReservation(
    MusicalSpace space,
    User user,
    LocalDate date,
    LocalTime startTime,
    LocalTime endTime,
    int attendeesCount,
    ReservationSessionState state
  ) {
    ReservationSession reservation = new ReservationSession(
      date,
      startTime,
      endTime,
      attendeesCount,
      ReservationSessionType.REHEARSAL,
      "Test reservation",
      BigDecimal.valueOf(20.00),
      space,
      user,
      null
    );
    reservation.setState(state);
    return reservationSessionDao.save(reservation);
  }

  protected Message createMessage(String content, ReservationSession reservationSession, User author) {
    Message message = new Message(content, reservationSession, author);
    return messageDao.save(message);
  }

  protected SpaceReview createSpaceReview(ReservationSession reservationSession, User author) {
    SpaceReview review = new SpaceReview(
      "Great space",
      5,
      4,
      5,
      4,
      5,
      reservationSession.getMusicalSpace(),
      author,
      reservationSession
    );
    return spaceReviewDao.save(review);
  }

  protected UserReview createUserReview(ReservationSession reservationSession, User reviewer) {
    UserReview review = new UserReview(
      "Great guest",
      5,
      4,
      5,
      5,
      reviewer,
      reservationSession.getUser(),
      reservationSession.getMusicalSpace(),
      reservationSession
    );
    return userReviewDao.save(review);
  }

  protected Event createPublishedEvent(String title, String city, boolean withCoordinates) {
    User creator = createUser(
      "event-creator-" + Math.abs((title + city + withCoordinates).hashCode()) + "@example.com",
      "password123"
    );

    Event event = new Event(
      title,
      "Test event",
      LocalDate.now().plusDays(10),
      LocalTime.of(20, 0),
      LocalTime.of(22, 0),
      "rock",
      100,
      BigDecimal.valueOf(15.00),
      "poster.png",
      EventStatus.PUBLISHED,
      EventType.CONCERT,
      EventSource.INTERNAL,
      "Sala Test",
      withCoordinates ? 42.8782 : null,
      withCoordinates ? -8.5448 : null,
      city,
      "A Coruna",
      "Spain",
      "Rua Event 1",
      null,
      null,
      null,
      null,
      null,
      creator
    );
    return eventDao.save(event);
  }

  protected String tokenFor(User user) {
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
      user.getEmail(),
      null,
      List.of(new SimpleGrantedAuthority(user.getPlatformRole().name()))
    );
    return tokenProvider.createToken(authentication);
  }

  protected void authenticateAs(User user) {
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
      new org.springframework.security.core.userdetails.User(
        user.getEmail(),
        user.getPassword(),
        List.of(new SimpleGrantedAuthority(user.getPlatformRole().name()))
      ),
      null,
      List.of(new SimpleGrantedAuthority(user.getPlatformRole().name()))
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
