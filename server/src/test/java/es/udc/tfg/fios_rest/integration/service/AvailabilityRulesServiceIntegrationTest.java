package es.udc.tfg.fios_rest.integration.service;

import es.udc.tfg.fios_rest.availability.service.AvailabilityPricingService;
import es.udc.tfg.fios_rest.availability.service.CalculatedAvailabilityService;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.exceptions.persistence.entity.SpaceAvailabilityExceptionType;
import es.udc.tfg.fios_rest.exceptions.service.SpaceAvailabilityExceptionService;
import es.udc.tfg.fios_rest.exceptions.service.dto.SpaceAvailabilityExceptionRequest;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceApprovalStatus;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSessionState;
import es.udc.tfg.fios_rest.schedule.service.ScheduleService;
import es.udc.tfg.fios_rest.schedule.service.dto.ScheduleRequest;
import es.udc.tfg.fios_rest.support.IntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AvailabilityRulesServiceIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private ScheduleService scheduleService;

  @Autowired
  private SpaceAvailabilityExceptionService exceptionService;

  @Autowired
  private CalculatedAvailabilityService calculatedAvailabilityService;

  @Autowired
  private AvailabilityPricingService availabilityPricingService;

  @Test
  void scheduleCreationRejectsOverlappingSlotsForSameDay() throws Exception {
    var manager = createUser("schedule-manager@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    authenticateAs(manager);

    scheduleService.create(space.getId(), new ScheduleRequest(
      DayOfWeek.MONDAY,
      LocalTime.of(10, 0),
      LocalTime.of(12, 0),
      BigDecimal.valueOf(20)
    ));

    assertThatThrownBy(() -> scheduleService.create(space.getId(), new ScheduleRequest(
      DayOfWeek.MONDAY,
      LocalTime.of(11, 0),
      LocalTime.of(13, 0),
      BigDecimal.valueOf(25)
    )))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("The schedule overlaps with another schedule for this musical space");
  }

  @Test
  void exceptionCreationRejectsOverlappingRangesForSameDate() throws Exception {
    var manager = createUser("exception-manager@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    authenticateAs(manager);

    LocalDate date = LocalDate.now().plusDays(5);
    exceptionService.create(space.getId(), new SpaceAvailabilityExceptionRequest(
      date,
      LocalTime.of(12, 0),
      LocalTime.of(13, 0),
      SpaceAvailabilityExceptionType.BLOCKED,
      "Private lesson",
      null
    ));

    assertThatThrownBy(() -> exceptionService.create(space.getId(), new SpaceAvailabilityExceptionRequest(
      date,
      LocalTime.of(12, 30),
      LocalTime.of(14, 0),
      SpaceAvailabilityExceptionType.CUSTOM_AVAILABILITY,
      "Extended opening",
      BigDecimal.valueOf(25)
    )))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("The exception overlaps with another exception for this musical space");
  }

  @Test
  void customAvailabilityRequiresPositivePriceAndBlockedAvailabilityRejectsPrice() throws Exception {
    var manager = createUser("exception-price-manager@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    authenticateAs(manager);

    LocalDate date = LocalDate.now().plusDays(6);

    assertThatThrownBy(() -> exceptionService.create(space.getId(), new SpaceAvailabilityExceptionRequest(
      date,
      LocalTime.of(9, 0),
      LocalTime.of(11, 0),
      SpaceAvailabilityExceptionType.CUSTOM_AVAILABILITY,
      "Special opening",
      null
    )))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Custom availability must include a price");

    assertThatThrownBy(() -> exceptionService.create(space.getId(), new SpaceAvailabilityExceptionRequest(
      date,
      LocalTime.of(9, 0),
      LocalTime.of(11, 0),
      SpaceAvailabilityExceptionType.CUSTOM_AVAILABILITY,
      "Special opening",
      BigDecimal.ZERO
    )))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("The custom availability price must be greater than zero");

    assertThatThrownBy(() -> exceptionService.create(space.getId(), new SpaceAvailabilityExceptionRequest(
      date,
      LocalTime.of(12, 0),
      LocalTime.of(13, 0),
      SpaceAvailabilityExceptionType.BLOCKED,
      "Maintenance",
      BigDecimal.TEN
    )))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Blocked availability cannot include a price");
  }

  @Test
  void calculatedAvailabilityCombinesBaseScheduleBlockedRangesAndCustomAvailability() throws Exception {
    var manager = createUser("availability-manager@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    authenticateAs(manager);

    LocalDate monday = LocalDate.now().with(java.time.temporal.TemporalAdjusters.next(DayOfWeek.MONDAY));
    scheduleService.create(space.getId(), new ScheduleRequest(
      monday.getDayOfWeek(),
      LocalTime.of(10, 0),
      LocalTime.of(14, 0),
      BigDecimal.valueOf(40)
    ));
    exceptionService.create(space.getId(), new SpaceAvailabilityExceptionRequest(
      monday,
      LocalTime.of(11, 0),
      LocalTime.of(12, 0),
      SpaceAvailabilityExceptionType.BLOCKED,
      "Maintenance",
      null
    ));
    exceptionService.create(space.getId(), new SpaceAvailabilityExceptionRequest(
      monday,
      LocalTime.of(16, 0),
      LocalTime.of(18, 0),
      SpaceAvailabilityExceptionType.CUSTOM_AVAILABILITY,
      "Special opening",
      BigDecimal.valueOf(20)
    ));

    var availability = calculatedAvailabilityService.findAvailability(space.getId(), monday);

    assertThat(availability.slots())
      .extracting(slot -> slot.startTime() + "-" + slot.endTime())
      .containsExactly("10:00-11:00", "12:00-14:00", "16:00-18:00");
    assertThat(availability.slots())
      .extracting(slot -> slot.price())
      .containsExactly(
        BigDecimal.valueOf(10).setScale(2),
        BigDecimal.valueOf(20).setScale(2),
        BigDecimal.valueOf(20).setScale(2)
      );
  }

  @Test
  void calculatedAvailabilityDoesNotExposeLegacyCustomAvailabilityWithoutPrice() throws Exception {
    var manager = createUser("availability-unpriced-manager@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    authenticateAs(manager);

    LocalDate date = LocalDate.now().plusDays(7);
    createException(
      space,
      date,
      LocalTime.of(16, 0),
      LocalTime.of(18, 0),
      SpaceAvailabilityExceptionType.CUSTOM_AVAILABILITY,
      null
    );

    var availability = calculatedAvailabilityService.findAvailability(space.getId(), date);

    assertThat(availability.slots()).isEmpty();

    assertThatThrownBy(() -> availabilityPricingService.calculatePrice(
      space.getId(),
      date,
      LocalTime.of(16, 0),
      LocalTime.of(17, 0)
    ))
      .isInstanceOf(OperationNotAllowed.class)
      .hasMessageContaining("The reservation price could not be calculated for the selected time range");
  }

  @Test
  void calculatedAvailabilitySubtractsBlockingReservationsOnly() throws Exception {
    var manager = createUser("availability-reservation-manager@example.com", "password123");
    var customer = createUser("availability-reservation-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    authenticateAs(manager);

    LocalDate monday = LocalDate.now().with(java.time.temporal.TemporalAdjusters.next(DayOfWeek.MONDAY));
    scheduleService.create(space.getId(), new ScheduleRequest(
      monday.getDayOfWeek(),
      LocalTime.of(10, 0),
      LocalTime.of(15, 0),
      BigDecimal.valueOf(40)
    ));
    createReservation(space, customer, monday, LocalTime.of(11, 0), LocalTime.of(12, 0), 2, ReservationSessionState.PENDING);
    createReservation(space, customer, monday, LocalTime.of(13, 0), LocalTime.of(14, 0), 2, ReservationSessionState.ACCEPTED);
    createReservation(space, customer, monday, LocalTime.of(12, 15), LocalTime.of(12, 45), 2, ReservationSessionState.CANCELLED);

    var availability = calculatedAvailabilityService.findAvailability(space.getId(), monday);

    assertThat(availability.slots())
      .extracting(slot -> slot.startTime() + "-" + slot.endTime())
      .containsExactly("10:00-11:00", "12:00-13:00", "14:00-15:00");
  }

  @Test
  void schedulesCannotBeChangedForInactiveSpaces() {
    var manager = createUser("inactive-manager@example.com", "password123");
    var inactiveSpace = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, false);
    authenticateAs(manager);

    assertThatThrownBy(() -> scheduleService.create(inactiveSpace.getId(), new ScheduleRequest(
      DayOfWeek.TUESDAY,
      LocalTime.of(9, 0),
      LocalTime.of(11, 0),
      BigDecimal.valueOf(18)
    )))
      .isInstanceOf(OperationNotAllowed.class)
      .hasMessageContaining("Schedules cannot be changed for inactive musical spaces");
  }
}
