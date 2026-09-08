package es.udc.tfg.fios_rest.integration.http;

import com.fasterxml.jackson.databind.JsonNode;
import es.udc.tfg.fios_rest.band.persistence.entity.Band;
import es.udc.tfg.fios_rest.bandrecruitment.persistence.dao.BandRecruitmentDao;
import es.udc.tfg.fios_rest.bandrecruitment.persistence.entity.BandRecruitment;
import es.udc.tfg.fios_rest.bandrecruitment.persistence.entity.BandRecruitmentLevel;
import es.udc.tfg.fios_rest.event.persistence.entity.Event;
import es.udc.tfg.fios_rest.event.persistence.entity.EventSource;
import es.udc.tfg.fios_rest.event.persistence.entity.EventStatus;
import es.udc.tfg.fios_rest.event.persistence.entity.EventType;
import es.udc.tfg.fios_rest.instrument.persistence.dao.InstrumentDao;
import es.udc.tfg.fios_rest.instrument.persistence.entity.Instrument;
import es.udc.tfg.fios_rest.instrument.persistence.entity.InstrumentCategory;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpace;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceApprovalStatus;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceLocation;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceType;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSessionState;
import es.udc.tfg.fios_rest.support.IntegrationTestSupport;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HomeFeaturedHttpIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private BandRecruitmentDao bandRecruitmentDao;

  @Autowired
  private InstrumentDao instrumentDao;

  @Test
  void featuredEndpointRanksTopThreeFromAllValidCandidatesWithCurrentCriteria() throws Exception {
    User manager = createUser("home-featured-manager@example.com", "password123");
    MusicalSpace linkedSpace = createFeaturedSpace(
      manager,
      "Z Rated Home Space",
      50,
      true,
      "rated-space.png",
      MusicalSpaceApprovalStatus.APPROVED,
      true
    );
    addSpaceReviews(linkedSpace, 3);
    MusicalSpace runnerSpace = createFeaturedSpace(
      manager,
      "Y Full Home Space",
      50,
      true,
      "full-space.png",
      MusicalSpaceApprovalStatus.APPROVED,
      true
    );
    MusicalSpace thirdSpace = createFeaturedSpace(
      manager,
      "X Capacity Home Space",
      40,
      true,
      "capacity-space.png",
      MusicalSpaceApprovalStatus.APPROVED,
      true
    );
    createFeaturedSpace(manager, "A First Listed Low Space", 5, false, null, MusicalSpaceApprovalStatus.APPROVED, true);
    createFeaturedSpace(manager, "B Second Listed Low Space", 8, false, null, MusicalSpaceApprovalStatus.APPROVED, true);
    MusicalSpace pendingSpace = createFeaturedSpace(
      manager,
      "ZZ Pending High Space",
      50,
      true,
      "pending-space.png",
      MusicalSpaceApprovalStatus.PENDING,
      true
    );
    MusicalSpace inactiveSpace = createFeaturedSpace(
      manager,
      "ZZ Inactive High Space",
      50,
      true,
      "inactive-space.png",
      MusicalSpaceApprovalStatus.APPROVED,
      false
    );

    User creator = createUser("home-featured-events@example.com", "password123");
    Event linkedEvent = createFeaturedEvent(
      creator,
      "Z Linked Featured Event",
      5,
      "rock",
      500,
      BigDecimal.valueOf(10),
      "linked-event.png",
      EventStatus.PUBLISHED,
      linkedSpace
    );
    Event completeEvent = createFeaturedEvent(
      creator,
      "Y Complete Featured Event",
      6,
      "rock",
      500,
      BigDecimal.valueOf(10),
      "complete-event.png",
      EventStatus.PUBLISHED,
      null
    );
    Event thirdEvent = createFeaturedEvent(
      creator,
      "X Large Featured Event",
      7,
      "rock",
      500,
      BigDecimal.valueOf(10),
      "large-event.png",
      EventStatus.PUBLISHED,
      null
    );
    createFeaturedEvent(creator, "A Earlier Low Event", 1, null, 10, BigDecimal.valueOf(40), null, EventStatus.PUBLISHED, null);
    Event draftEvent = createFeaturedEvent(
      creator,
      "ZZ Draft High Event",
      3,
      "rock",
      500,
      BigDecimal.valueOf(10),
      "draft-event.png",
      EventStatus.DRAFT,
      linkedSpace
    );
    Event pastEvent = createFeaturedEvent(
      creator,
      "ZZ Past High Event",
      -1,
      "rock",
      500,
      BigDecimal.valueOf(10),
      "past-event.png",
      EventStatus.PUBLISHED,
      linkedSpace
    );

    User leader = createUser("home-featured-recruitments@example.com", "password123");
    Instrument instrument = instrumentDao.save(new Instrument("Home Featured Guitar", InstrumentCategory.STRINGS));
    LocalDateTime now = LocalDateTime.now();
    BandRecruitment bestRecruitment = createRecruitment(
      leader,
      createBand("Z Complete Featured Band", "featured-band.png", true),
      instrument,
      "Z Complete Featured Recruitment",
      "Complete recruitment description",
      5,
      now.minusHours(2)
    );
    createRecruitment(
      leader,
      createBand("A Recent Low Band One", null, true),
      instrument,
      "A Recent Low Recruitment One",
      null,
      1,
      now.minusMinutes(10)
    );
    createRecruitment(
      leader,
      createBand("B Recent Low Band Two", null, true),
      instrument,
      "B Recent Low Recruitment Two",
      null,
      1,
      now.minusMinutes(9)
    );
    createRecruitment(
      leader,
      createBand("C Recent Low Band Three", null, true),
      instrument,
      "C Recent Low Recruitment Three",
      null,
      1,
      now.minusMinutes(8)
    );
    BandRecruitment closedRecruitment = createRecruitment(
      leader,
      createBand("ZZ Closed Featured Band", "closed-band.png", true),
      instrument,
      "ZZ Closed Featured Recruitment",
      "Closed recruitment description",
      5,
      now
    );
    closedRecruitment.close();
    bandRecruitmentDao.update(closedRecruitment);
    BandRecruitment inactiveBandRecruitment = createRecruitment(
      leader,
      createBand("ZZ Inactive Featured Band", "inactive-band.png", false),
      instrument,
      "ZZ Inactive Featured Recruitment",
      "Inactive band recruitment description",
      5,
      now
    );

    var response = mockMvc.perform(get("/api/home/featured"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.featuredSpaces.length()").value(3))
      .andExpect(jsonPath("$.featuredEvents.length()").value(3))
      .andExpect(jsonPath("$.featuredRecruitments.length()").value(3))
      .andExpect(jsonPath("$.featuredSpaces[0].id").value(linkedSpace.getId()))
      .andExpect(jsonPath("$.featuredSpaces[0].featuredReason").value("highRating"))
      .andExpect(jsonPath("$.featuredEvents[0].id").value(linkedEvent.getId()))
      .andExpect(jsonPath("$.featuredEvents[0].featuredReason").value("soon"))
      .andExpect(jsonPath("$.featuredRecruitments[0].id").value(bestRecruitment.getId()))
      .andReturn()
      .getResponse()
      .getContentAsString();

    JsonNode body = objectMapper.readTree(response);

    assertThat(ids(body.get("featuredSpaces"))).containsExactly(
      linkedSpace.getId(),
      runnerSpace.getId(),
      thirdSpace.getId()
    );
    assertThat(ids(body.get("featuredSpaces"))).doesNotContain(pendingSpace.getId(), inactiveSpace.getId());
    assertThat(ids(body.get("featuredEvents"))).containsExactly(
      linkedEvent.getId(),
      completeEvent.getId(),
      thirdEvent.getId()
    );
    assertThat(ids(body.get("featuredEvents"))).doesNotContain(draftEvent.getId(), pastEvent.getId());
    assertThat(ids(body.get("featuredRecruitments"))).contains(bestRecruitment.getId());
    assertThat(ids(body.get("featuredRecruitments"))).doesNotContain(
      closedRecruitment.getId(),
      inactiveBandRecruitment.getId()
    );
    assertScoresDescending(body.get("featuredSpaces"));
    assertScoresDescending(body.get("featuredEvents"));
    assertScoresDescending(body.get("featuredRecruitments"));
  }

  private MusicalSpace createFeaturedSpace(
    User manager,
    String name,
    int capacity,
    boolean soundproofed,
    String mainImage,
    MusicalSpaceApprovalStatus approvalStatus,
    boolean active
  ) {
    MusicalSpace space = new MusicalSpace(
      name,
      "Home featured space",
      MusicalSpaceType.REHEARSAL_ROOM,
      capacity,
      mainImage,
      35.0,
      soundproofed,
      new MusicalSpaceLocation("Spain", "A Coruna", "Santiago de Compostela", "Rua Home", "1", null, "15701", 42.8782, -8.5448),
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

  private void addSpaceReviews(MusicalSpace space, int reviewsCount) {
    for (int index = 0; index < reviewsCount; index++) {
      User reviewer = createUser("home-space-reviewer-" + space.getId() + "-" + index + "@example.com", "password123");
      var reservation = createReservation(
        space,
        reviewer,
        LocalDate.now().minusDays(index + 1),
        LocalTime.of(10, 0),
        LocalTime.of(12, 0),
        4,
        ReservationSessionState.COMPLETED
      );
      createSpaceReview(reservation, reviewer);
    }
  }

  private Event createFeaturedEvent(
    User creator,
    String title,
    int daysFromToday,
    String musicalGenre,
    Integer capacity,
    BigDecimal ticketPrice,
    String posterImage,
    EventStatus status,
    MusicalSpace musicalSpace
  ) {
    return eventDao.save(new Event(
      title,
      "Home featured event",
      LocalDate.now().plusDays(daysFromToday),
      LocalTime.of(20, 0),
      LocalTime.of(22, 0),
      musicalGenre,
      capacity,
      ticketPrice,
      posterImage,
      status,
      EventType.CONCERT,
      EventSource.INTERNAL,
      "Sala Home",
      42.8782,
      -8.5448,
      "Santiago de Compostela",
      "A Coruna",
      "Spain",
      "Rua Event 1",
      null,
      null,
      null,
      musicalSpace,
      null,
      creator
    ));
  }

  private Band createBand(String name, String image, boolean active) {
    Band band = bandDao.save(new Band(name, "Home featured band", "rock", "Santiago de Compostela", image));

    if (!active) {
      band.deactivate();
      return bandDao.update(band);
    }

    return band;
  }

  private BandRecruitment createRecruitment(
    User leader,
    Band band,
    Instrument instrument,
    String title,
    String description,
    int vacancies,
    LocalDateTime publicationDate
  ) {
    BandRecruitment recruitment = new BandRecruitment(
      title,
      description,
      "Guitarrista",
      BandRecruitmentLevel.INTERMEDIATE,
      "Santiago de Compostela",
      vacancies,
      band,
      instrument,
      leader
    );
    ReflectionTestUtils.setField(recruitment, "publicationDate", publicationDate);
    return bandRecruitmentDao.save(recruitment);
  }

  private List<Long> ids(JsonNode items) {
    List<Long> ids = new ArrayList<>();
    items.forEach(item -> ids.add(item.get("id").asLong()));
    return ids;
  }

  private void assertScoresDescending(JsonNode items) {
    for (int index = 1; index < items.size(); index++) {
      assertThat(items.get(index - 1).get("featuredScore").asDouble())
        .isGreaterThanOrEqualTo(items.get(index).get("featuredScore").asDouble());
    }
  }
}
