package es.udc.tfg.fios_rest.integration.model;

import es.udc.tfg.fios_rest.band.persistence.entity.Band;
import es.udc.tfg.fios_rest.bandmember.persistence.dao.BandMemberDao;
import es.udc.tfg.fios_rest.bandmember.persistence.entity.BandMember;
import es.udc.tfg.fios_rest.bandmember.persistence.entity.BandMemberRole;
import es.udc.tfg.fios_rest.bandmember.service.BandMemberService;
import es.udc.tfg.fios_rest.bandmember.service.dto.BandMemberCreateRequest;
import es.udc.tfg.fios_rest.bandrecruitment.persistence.dao.BandRecruitmentDao;
import es.udc.tfg.fios_rest.bandrecruitment.persistence.entity.BandRecruitmentLevel;
import es.udc.tfg.fios_rest.bandrecruitment.service.BandRecruitmentService;
import es.udc.tfg.fios_rest.bandrecruitment.service.dto.BandRecruitmentRequest;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.equipment.persistence.dao.EquipmentDao;
import es.udc.tfg.fios_rest.equipment.persistence.entity.Equipment;
import es.udc.tfg.fios_rest.equipment.persistence.entity.EquipmentCategory;
import es.udc.tfg.fios_rest.event.persistence.entity.Event;
import es.udc.tfg.fios_rest.event.persistence.entity.EventSource;
import es.udc.tfg.fios_rest.event.persistence.entity.EventStatus;
import es.udc.tfg.fios_rest.event.persistence.entity.EventType;
import es.udc.tfg.fios_rest.event.service.EventService;
import es.udc.tfg.fios_rest.event.service.dto.EventRequest;
import es.udc.tfg.fios_rest.favoritespace.persistence.dao.FavoriteSpaceDao;
import es.udc.tfg.fios_rest.favoritespace.persistence.entity.FavoriteSpace;
import es.udc.tfg.fios_rest.favoritespace.service.FavoriteSpaceService;
import es.udc.tfg.fios_rest.instrument.persistence.dao.InstrumentDao;
import es.udc.tfg.fios_rest.instrument.persistence.entity.Instrument;
import es.udc.tfg.fios_rest.instrument.persistence.entity.InstrumentCategory;
import es.udc.tfg.fios_rest.instrument.service.InstrumentService;
import es.udc.tfg.fios_rest.instrument.service.dto.UserInstrumentUpdateRequest;
import es.udc.tfg.fios_rest.message.service.MessageService;
import es.udc.tfg.fios_rest.message.service.dto.MessageRequest;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpace;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceApprovalStatus;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSessionState;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSessionType;
import es.udc.tfg.fios_rest.reservationsession.service.ReservationSessionService;
import es.udc.tfg.fios_rest.reservationsession.service.dto.ReservationSessionRequest;
import es.udc.tfg.fios_rest.search.persistence.entity.Search;
import es.udc.tfg.fios_rest.search.persistence.entity.SearchIntent;
import es.udc.tfg.fios_rest.spaceequipment.persistence.dao.SpaceEquipmentDao;
import es.udc.tfg.fios_rest.spaceequipment.persistence.entity.SpaceEquipment;
import es.udc.tfg.fios_rest.spaceequipment.persistence.entity.SpaceEquipmentState;
import es.udc.tfg.fios_rest.spaceequipment.service.SpaceEquipmentService;
import es.udc.tfg.fios_rest.spaceequipment.service.dto.SpaceEquipmentRequest;
import es.udc.tfg.fios_rest.spacereview.service.SpaceReviewService;
import es.udc.tfg.fios_rest.spacereview.service.dto.SpaceReviewRequest;
import es.udc.tfg.fios_rest.support.IntegrationTestSupport;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RelationshipModelIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private BandMemberDao bandMemberDao;

  @Autowired
  private BandMemberService bandMemberService;

  @Autowired
  private BandRecruitmentDao bandRecruitmentDao;

  @Autowired
  private BandRecruitmentService bandRecruitmentService;

  @Autowired
  private InstrumentDao instrumentDao;

  @Autowired
  private InstrumentService instrumentService;

  @Autowired
  private FavoriteSpaceDao favoriteSpaceDao;

  @Autowired
  private FavoriteSpaceService favoriteSpaceService;

  @Autowired
  private EquipmentDao equipmentDao;

  @Autowired
  private SpaceEquipmentDao spaceEquipmentDao;

  @Autowired
  private SpaceEquipmentService spaceEquipmentService;

  @Autowired
  private ReservationSessionService reservationSessionService;

  @Autowired
  private MessageService messageService;

  @Autowired
  private SpaceReviewService spaceReviewService;

  @Autowired
  private EventService eventService;

  @Test
  void searchEntriesRequireAuthenticatedUserAtDomainAndDatabaseLevel() {
    assertThatThrownBy(() -> new Search(
      "sala en Santiago",
      "Santiago de Compostela",
      SearchIntent.BOTH,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null
    ))
      .isInstanceOf(NullPointerException.class)
      .hasMessageContaining("user");

    assertThatThrownBy(() -> {
      entityManager.createNativeQuery("insert into search_entry (original_text, search_date) values (:text, current_timestamp)")
        .setParameter("text", "orphan search")
        .executeUpdate();
      entityManager.flush();
    }).hasRootCauseInstanceOf(JdbcSQLIntegrityConstraintViolationException.class);
  }

  @Test
  void eventsRequireCreatorAtDomainAndDatabaseLevel() {
    assertThatThrownBy(() -> new Event(
      "Rel Orphan Event",
      "No creator",
      LocalDate.now().plusDays(7),
      LocalTime.of(20, 0),
      LocalTime.of(22, 0),
      "rock",
      100,
      BigDecimal.valueOf(12),
      "poster.png",
      EventStatus.PUBLISHED,
      EventType.CONCERT,
      EventSource.INTERNAL,
      "Sala Rel",
      42.8782,
      -8.5448,
      "Santiago de Compostela",
      "A Coruna",
      "Spain",
      "Rua Rel 1",
      null,
      null,
      null,
      null,
      null,
      null
    ))
      .isInstanceOf(NullPointerException.class)
      .hasMessageContaining("createdBy");

    assertThatThrownBy(() -> {
      entityManager.createNativeQuery("""
          insert into musical_event
            (title, event_date, status, event_type, source, venue_name, city, country)
          values
            (:title, :eventDate, :status, :eventType, :source, :venueName, :city, :country)
        """)
        .setParameter("title", "Orphan DB Event")
        .setParameter("eventDate", LocalDate.now().plusDays(7))
        .setParameter("status", EventStatus.PUBLISHED.name())
        .setParameter("eventType", EventType.CONCERT.name())
        .setParameter("source", EventSource.INTERNAL.name())
        .setParameter("venueName", "Sala Rel")
        .setParameter("city", "Santiago de Compostela")
        .setParameter("country", "Spain")
        .executeUpdate();
      entityManager.flush();
    }).hasRootCauseInstanceOf(JdbcSQLIntegrityConstraintViolationException.class);
  }

  @Test
  void userPrimaryInstrumentPersistsReplacesAndRejectsMultipleSelection() throws Exception {
    User user = createUser("rel-instruments-user@example.com", "password123");
    Instrument guitar = instrumentDao.save(new Instrument("Rel Guitar", InstrumentCategory.STRINGS));
    Instrument drums = instrumentDao.save(new Instrument("Rel Drums", InstrumentCategory.PERCUSSION));
    authenticateAs(user);

    instrumentService.updateMyInstruments(new UserInstrumentUpdateRequest(Set.of(guitar.getId())));
    entityManager.flush();
    entityManager.clear();

    assertThat(userDao.findById(user.getId()).orElseThrow().getInstruments())
      .extracting(Instrument::getId)
      .containsExactly(guitar.getId());
    assertThat(countRows("user_instrument", "user_id", user.getId())).isEqualTo(1L);

    instrumentService.updateMyInstruments(new UserInstrumentUpdateRequest(Set.of(drums.getId())));
    entityManager.flush();
    entityManager.clear();

    assertThat(userDao.findById(user.getId()).orElseThrow().getInstruments())
      .extracting(Instrument::getId)
      .containsExactly(drums.getId());
    assertThat(countRows("user_instrument", "user_id", user.getId())).isEqualTo(1L);

    assertThatThrownBy(() ->
      instrumentService.updateMyInstruments(new UserInstrumentUpdateRequest(Set.of(guitar.getId(), drums.getId())))
    ).isInstanceOf(IllegalArgumentException.class);

    assertThatThrownBy(() -> {
      entityManager.createNativeQuery("insert into user_instrument (user_id, instrument_id) values (:userId, :instrumentId)")
        .setParameter("userId", user.getId())
        .setParameter("instrumentId", guitar.getId())
        .executeUpdate();
      entityManager.flush();
    }).hasRootCauseInstanceOf(JdbcSQLIntegrityConstraintViolationException.class);
  }

  @Test
  void favoriteSpaceJoinEntityRoundTripsDeletesAndRejectsDuplicatePairs() throws Exception {
    User manager = createUser("rel-favorite-manager@example.com", "password123");
    User user = createUser("rel-favorite-user@example.com", "password123");
    MusicalSpace space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    authenticateAs(user);

    var favorite = favoriteSpaceService.save(space.getId());
    entityManager.flush();
    entityManager.clear();

    assertThat(favoriteSpaceDao.findByUser(user.getId()))
      .extracting(FavoriteSpace::getId)
      .containsExactly(favorite.id());
    assertThat(favoriteSpaceDao.findByUserAndMusicalSpace(user.getId(), space.getId()))
      .get()
      .matches(found -> found.getMusicalSpace().getId().equals(space.getId()));

    favoriteSpaceService.delete(space.getId());
    entityManager.flush();
    entityManager.clear();

    assertThat(favoriteSpaceDao.findByUserAndMusicalSpace(user.getId(), space.getId())).isEmpty();

    favoriteSpaceDao.save(new FavoriteSpace(
      entityManager.getReference(User.class, user.getId()),
      entityManager.getReference(MusicalSpace.class, space.getId())
    ));
    entityManager.flush();

    assertThatThrownBy(() -> {
      favoriteSpaceDao.save(new FavoriteSpace(
        entityManager.getReference(User.class, user.getId()),
        entityManager.getReference(MusicalSpace.class, space.getId())
      ));
      entityManager.flush();
    }).hasRootCauseInstanceOf(JdbcSQLIntegrityConstraintViolationException.class);
  }

  @Test
  void spaceEquipmentStoresExtraAttributesDeletesAndRejectsDuplicatePairs() throws Exception {
    User manager = createUser("rel-equipment-manager@example.com", "password123");
    MusicalSpace space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    Equipment mixer = equipmentDao.save(new Equipment("Rel Mixer", EquipmentCategory.SOUND, "Digital mixer"));
    authenticateAs(manager);

    var created = spaceEquipmentService.create(space.getId(), new SpaceEquipmentRequest(
      mixer.getId(),
      2,
      SpaceEquipmentState.AVAILABLE,
      "Front desk"
    ));
    entityManager.flush();
    entityManager.clear();

    SpaceEquipment loaded = spaceEquipmentDao.findByMusicalSpaceAndEquipment(space.getId(), mixer.getId()).orElseThrow();
    assertThat(loaded.getId()).isEqualTo(created.id());
    assertThat(loaded.getQuantity()).isEqualTo(2);
    assertThat(loaded.getState()).isEqualTo(SpaceEquipmentState.AVAILABLE);
    assertThat(loaded.getObservations()).isEqualTo("Front desk");

    spaceEquipmentService.delete(created.id());
    entityManager.flush();
    entityManager.clear();

    assertThat(spaceEquipmentDao.findById(created.id())).isEmpty();

    spaceEquipmentDao.save(new SpaceEquipment(
      1,
      SpaceEquipmentState.LIMITED,
      null,
      entityManager.getReference(MusicalSpace.class, space.getId()),
      entityManager.getReference(Equipment.class, mixer.getId())
    ));
    entityManager.flush();

    assertThatThrownBy(() -> {
      spaceEquipmentDao.save(new SpaceEquipment(
        3,
        SpaceEquipmentState.OUT_OF_SERVICE,
        "Duplicate physical relation",
        entityManager.getReference(MusicalSpace.class, space.getId()),
        entityManager.getReference(Equipment.class, mixer.getId())
      ));
      entityManager.flush();
    }).hasRootCauseInstanceOf(JdbcSQLIntegrityConstraintViolationException.class);
  }

  @Test
  void bandMemberAllowsOneUserInSeveralBandsReactivatesAndRejectsDuplicatePairs() throws Exception {
    User leaderOne = createUser("rel-member-leader-one@example.com", "password123");
    User leaderTwo = createUser("rel-member-leader-two@example.com", "password123");
    User musician = createUser("rel-member-musician@example.com", "password123");
    Band bandOne = createBandWithLeader(leaderOne, "Rel Member Band One");
    Band bandTwo = createBandWithLeader(leaderTwo, "Rel Member Band Two");

    BandMember membership = bandMemberDao.save(new BandMember(bandOne, musician, BandMemberRole.MEMBER));
    membership.deactivate();
    bandMemberDao.update(membership);
    entityManager.flush();
    authenticateAs(leaderOne);

    bandMemberService.addMember(bandOne.getId(), new BandMemberCreateRequest(musician.getId(), BandMemberRole.LEADER));
    bandMemberDao.save(new BandMember(bandTwo, musician, BandMemberRole.MEMBER));
    entityManager.flush();
    entityManager.clear();

    assertThat(bandMemberDao.findByBand(bandOne.getId()))
      .filteredOn(member -> member.getUser().getId().equals(musician.getId()))
      .hasSize(1)
      .allMatch(BandMember::isActive);
    assertThat(bandMemberDao.findActiveByUser(musician.getId()))
      .extracting(member -> member.getBand().getId())
      .containsExactlyInAnyOrder(bandOne.getId(), bandTwo.getId());

    assertThatThrownBy(() -> {
      bandMemberDao.save(new BandMember(
        entityManager.getReference(Band.class, bandOne.getId()),
        entityManager.getReference(User.class, musician.getId()),
        BandMemberRole.MEMBER
      ));
      entityManager.flush();
    }).hasRootCauseInstanceOf(JdbcSQLIntegrityConstraintViolationException.class);
  }

  @Test
  void bandRecruitmentLinksBandInstrumentPublisherAndCanBeDeleted() throws Exception {
    User leader = createUser("rel-recruitment-leader@example.com", "password123");
    Band band = createBandWithLeader(leader, "Rel Recruitment Band");
    Instrument bass = instrumentDao.save(new Instrument("Rel Bass", InstrumentCategory.STRINGS));
    authenticateAs(leader);

    var created = bandRecruitmentService.create(band.getId(), new BandRecruitmentRequest(
      "Bass player wanted",
      "Looking for rehearsal partner",
      "Bass",
      BandRecruitmentLevel.INTERMEDIATE,
      "Santiago de Compostela",
      1,
      bass.getId()
    ));
    entityManager.flush();
    entityManager.clear();

    var loaded = bandRecruitmentDao.findById(created.id()).orElseThrow();
    assertThat(loaded.getBand().getId()).isEqualTo(band.getId());
    assertThat(loaded.getInstrument().getId()).isEqualTo(bass.getId());
    assertThat(loaded.getPublishedBy().getId()).isEqualTo(leader.getId());
    assertThat(bandRecruitmentDao.findByBandIds(List.of(band.getId())))
      .extracting(recruitment -> recruitment.getId())
      .contains(created.id());
    assertThat(bandRecruitmentDao.findOpen())
      .extracting(recruitment -> recruitment.getId())
      .contains(created.id());

    bandRecruitmentService.delete(created.id());
    entityManager.flush();
    entityManager.clear();

    assertThat(bandRecruitmentDao.findById(created.id())).isEmpty();
  }

  @Test
  void reservationMessageReviewAndEventRelationshipsRoundTrip() throws Exception {
    User manager = createUser("rel-flow-manager@example.com", "password123");
    User customer = createUser("rel-flow-customer@example.com", "password123");
    User leader = createUser("rel-flow-leader@example.com", "password123");
    MusicalSpace space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    Band band = createBandWithLeader(leader, "Rel Flow Band");
    bandMemberDao.save(new BandMember(band, customer, BandMemberRole.MEMBER));
    LocalDate futureDate = LocalDate.now().plusDays(9);
    createSchedule(space, futureDate.getDayOfWeek(), LocalTime.of(18, 0), LocalTime.of(22, 0), BigDecimal.valueOf(40));
    authenticateAs(customer);

    var reservation = reservationSessionService.create(new ReservationSessionRequest(
      space.getId(),
      futureDate,
      LocalTime.of(18, 0),
      LocalTime.of(20, 0),
      3,
      ReservationSessionType.REHEARSAL,
      "Band booking",
      band.getId()
    ));

    assertThat(reservation.user().id()).isEqualTo(customer.getId());
    assertThat(reservation.musicalSpace().id()).isEqualTo(space.getId());
    assertThat(reservation.band().id()).isEqualTo(band.getId());

    messageService.sendMessage(reservation.id(), new MessageRequest("Customer message"));
    authenticateAs(manager);
    messageService.sendMessage(reservation.id(), new MessageRequest("Manager message"));
    entityManager.flush();
    entityManager.clear();

    assertThat(messageDao.findByReservationSession(reservation.id()))
      .extracting(message -> message.getUser().getId())
      .containsExactly(customer.getId(), manager.getId());

    var completedReservation = createReservation(
      space,
      customer,
      LocalDate.now().minusDays(3),
      LocalTime.of(16, 0),
      LocalTime.of(17, 0),
      2,
      ReservationSessionState.COMPLETED
    );
    authenticateAs(customer);
    var review = spaceReviewService.create(completedReservation.getId(), new SpaceReviewRequest(
      "Clean and practical",
      5,
      5,
      4,
      4,
      5
    ));

    assertThat(spaceReviewDao.findByReservationSession(completedReservation.getId()))
      .get()
      .matches(found -> found.getId().equals(review.id()));
    assertThat(spaceReviewDao.findByUser(customer.getId()))
      .extracting(found -> found.getId())
      .contains(review.id());
    assertThat(spaceReviewDao.findByMusicalSpace(space.getId()))
      .extracting(found -> found.getId())
      .contains(review.id());

    authenticateAs(leader);
    var event = eventService.createForBand(band.getId(), eventRequest("Rel Flow Event", space.getId()));
    entityManager.flush();
    entityManager.clear();

    assertThat(eventDao.findById(event.id()))
      .get()
      .matches(found -> found.getBand().getId().equals(band.getId()))
      .matches(found -> found.getCreatedBy().getId().equals(leader.getId()))
      .matches(found -> found.getMusicalSpace().getId().equals(space.getId()));
  }

  @Test
  void spaceEquipmentCreationRequiresSpaceManagerOrAdmin() {
    User manager = createUser("rel-equipment-permission-manager@example.com", "password123");
    User outsider = createUser("rel-equipment-permission-outsider@example.com", "password123");
    MusicalSpace space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    Equipment amplifier = equipmentDao.save(new Equipment("Rel Permission Amp", EquipmentCategory.SOUND, null));
    authenticateAs(outsider);

    assertThatThrownBy(() -> spaceEquipmentService.create(space.getId(), new SpaceEquipmentRequest(
      amplifier.getId(),
      1,
      SpaceEquipmentState.AVAILABLE,
      null
    )))
      .isInstanceOf(OperationNotAllowed.class)
      .hasMessageContaining("cannot manage equipment");
  }

  @Test
  void bandRecruitmentCreationRequiresActiveBandLeaderOrAdmin() {
    User leader = createUser("rel-recruitment-permission-leader@example.com", "password123");
    User outsider = createUser("rel-recruitment-permission-outsider@example.com", "password123");
    Band band = createBandWithLeader(leader, "Rel Recruitment Permission Band");
    Instrument voice = instrumentDao.save(new Instrument("Rel Voice", InstrumentCategory.VOICE));
    authenticateAs(outsider);

    assertThatThrownBy(() -> bandRecruitmentService.create(band.getId(), new BandRecruitmentRequest(
      "Singer wanted",
      "Permission test",
      "Voice",
      BandRecruitmentLevel.BEGINNER,
      "Santiago de Compostela",
      1,
      voice.getId()
    )))
      .isInstanceOf(OperationNotAllowed.class)
      .hasMessageContaining("cannot manage recruitments");
  }

  @Test
  void reservationBandAssociationRequiresActiveBandMembership() {
    User manager = createUser("rel-reservation-band-manager@example.com", "password123");
    User leader = createUser("rel-reservation-band-leader@example.com", "password123");
    User customer = createUser("rel-reservation-band-customer@example.com", "password123");
    MusicalSpace space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    Band band = createBandWithLeader(leader, "Rel Reservation Band");
    LocalDate date = LocalDate.now().plusDays(12);
    createSchedule(space, date.getDayOfWeek(), LocalTime.of(18, 0), LocalTime.of(21, 0), BigDecimal.valueOf(30));
    authenticateAs(customer);

    assertThatThrownBy(() -> reservationSessionService.create(new ReservationSessionRequest(
      space.getId(),
      date,
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionType.REHEARSAL,
      "Invalid band association",
      band.getId()
    )))
      .isInstanceOf(OperationNotAllowed.class)
      .hasMessageContaining("not an active member of the band");
  }

  private Band createBandWithLeader(User leader, String name) {
    Band band = bandDao.save(new Band(name, "Relationship model band", "Rock", "Santiago de Compostela", "band.png"));
    bandMemberDao.save(new BandMember(band, leader, BandMemberRole.LEADER));
    entityManager.flush();
    return band;
  }

  private EventRequest eventRequest(String title, Long musicalSpaceId) {
    return new EventRequest(
      title,
      "Relationship event",
      LocalDate.now().plusDays(18),
      LocalTime.of(20, 0),
      LocalTime.of(22, 0),
      "rock",
      100,
      BigDecimal.valueOf(12),
      "poster.png",
      EventStatus.PUBLISHED,
      EventType.CONCERT,
      EventSource.INTERNAL,
      "Sala Rel",
      42.8782,
      -8.5448,
      "Santiago de Compostela",
      "A Coruna",
      "Spain",
      "Rua Rel 1",
      null,
      null,
      null,
      musicalSpaceId,
      null
    );
  }

  private long countRows(String tableName, String columnName, Long value) {
    Number count = (Number) entityManager
      .createNativeQuery("select count(*) from " + tableName + " where " + columnName + " = :value")
      .setParameter("value", value)
      .getSingleResult();
    return count.longValue();
  }
}
