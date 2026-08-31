package es.udc.tfg.fios_rest.integration.service;

import es.udc.tfg.fios_rest.availability.service.CalculatedAvailabilityService;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.exceptions.persistence.entity.SpaceAvailabilityExceptionType;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceApprovalStatus;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSessionState;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSessionType;
import es.udc.tfg.fios_rest.reservationsession.service.ReservationSessionService;
import es.udc.tfg.fios_rest.reservationsession.service.dto.ReservationCancellationRequest;
import es.udc.tfg.fios_rest.reservationsession.service.dto.ReservationSessionRequest;
import es.udc.tfg.fios_rest.reservationsession.service.dto.ReservationStateUpdateRequest;
import es.udc.tfg.fios_rest.support.IntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationSessionServiceIntegrationTest extends IntegrationTestSupport {

  @Autowired
  private ReservationSessionService reservationSessionService;

  @Autowired
  private CalculatedAvailabilityService calculatedAvailabilityService;

  @Test
  void createReservationSucceedsWhenRangeFitsAvailabilityAndCapacity() throws Exception {
    var manager = createUser("reservation-manager@example.com", "password123");
    var customer = createUser("reservation-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    LocalDate date = LocalDate.now().plusDays(2);
    createSchedule(space, date.getDayOfWeek(), LocalTime.of(18, 0), LocalTime.of(21, 0), BigDecimal.valueOf(30));
    authenticateAs(customer);

    var reservation = reservationSessionService.create(new ReservationSessionRequest(
      space.getId(),
      date,
      LocalTime.of(18, 30),
      LocalTime.of(20, 0),
      4,
      ReservationSessionType.REHEARSAL,
      "Band rehearsal",
      null
    ));

    assertThat(reservation.state()).isEqualTo(ReservationSessionState.PENDING);
    assertThat(reservation.finalPrice()).isEqualByComparingTo("15.00");
    assertThat(reservation.user().id()).isEqualTo(customer.getId());
  }

  @Test
  void createReservationSucceedsForPartialMorningSlot() throws Exception {
    var manager = createUser("partial-morning-manager@example.com", "password123");
    var customer = createUser("partial-morning-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    LocalDate date = LocalDate.now().plusDays(1);
    createSchedule(space, date.getDayOfWeek(), LocalTime.of(10, 0), LocalTime.of(14, 0), BigDecimal.valueOf(40));
    authenticateAs(customer);

    var reservation = reservationSessionService.create(new ReservationSessionRequest(
      space.getId(),
      date,
      LocalTime.of(11, 30),
      LocalTime.of(13, 30),
      3,
      ReservationSessionType.REHEARSAL,
      "Morning rehearsal",
      null
    ));

    assertThat(reservation.startTime()).isEqualTo(LocalTime.of(11, 30));
    assertThat(reservation.endTime()).isEqualTo(LocalTime.of(13, 30));
    assertThat(reservation.finalPrice()).isEqualByComparingTo("20.00");

    var availability = calculatedAvailabilityService.findAvailability(space.getId(), date);

    assertThat(availability.slots())
      .extracting(slot -> slot.startTime() + "-" + slot.endTime())
      .containsExactly("10:00-11:30", "13:30-14:00");
    assertThat(availability.bookedSlots())
      .extracting(slot -> slot.startTime() + "-" + slot.endTime() + "-" + slot.state())
      .containsExactly("11:30-13:30-PENDING");
  }

  @Test
  void createReservationUsesCustomAvailabilityPriceWhenNoBaseScheduleExists() throws Exception {
    var manager = createUser("custom-price-manager@example.com", "password123");
    var customer = createUser("custom-price-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    LocalDate date = LocalDate.now().plusDays(2);
    createException(
      space,
      date,
      LocalTime.of(16, 0),
      LocalTime.of(18, 0),
      SpaceAvailabilityExceptionType.CUSTOM_AVAILABILITY,
      BigDecimal.valueOf(28)
    );
    authenticateAs(customer);

    var reservation = reservationSessionService.create(new ReservationSessionRequest(
      space.getId(),
      date,
      LocalTime.of(16, 30),
      LocalTime.of(17, 30),
      2,
      ReservationSessionType.REHEARSAL,
      "Special opening rehearsal",
      null
    ));

    assertThat(reservation.finalPrice()).isEqualByComparingTo("14.00");
  }

  @Test
  void createReservationRejectsRangeOutsideCalculatedAvailability() {
    var manager = createUser("out-manager@example.com", "password123");
    var customer = createUser("out-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    LocalDate date = LocalDate.now().plusDays(3);
    createSchedule(space, date.getDayOfWeek(), LocalTime.of(18, 0), LocalTime.of(20, 0), BigDecimal.valueOf(30));
    authenticateAs(customer);

    assertThatThrownBy(() -> reservationSessionService.create(new ReservationSessionRequest(
      space.getId(),
      date,
      LocalTime.of(20, 0),
      LocalTime.of(21, 0),
      2,
      ReservationSessionType.REHEARSAL,
      null,
      null
    )))
      .isInstanceOf(OperationNotAllowed.class)
      .hasMessageContaining("The requested time range is not available");
  }

  @Test
  void createReservationRejectsUnpricedCustomAvailabilityInsteadOfSavingZeroPrice() {
    var manager = createUser("custom-unpriced-manager@example.com", "password123");
    var customer = createUser("custom-unpriced-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    LocalDate date = LocalDate.now().plusDays(3);
    createException(
      space,
      date,
      LocalTime.of(16, 0),
      LocalTime.of(18, 0),
      SpaceAvailabilityExceptionType.CUSTOM_AVAILABILITY,
      null
    );
    authenticateAs(customer);

    assertThatThrownBy(() -> reservationSessionService.create(new ReservationSessionRequest(
      space.getId(),
      date,
      LocalTime.of(16, 30),
      LocalTime.of(17, 30),
      2,
      ReservationSessionType.REHEARSAL,
      null,
      null
    )))
      .isInstanceOf(OperationNotAllowed.class)
      .hasMessageContaining("The requested time range is not available");
  }

  @Test
  void createReservationRejectsOverlappingReservation() {
    var manager = createUser("overlap-manager@example.com", "password123");
    var customer = createUser("overlap-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    LocalDate date = LocalDate.now().plusDays(4);
    createSchedule(space, date.getDayOfWeek(), LocalTime.of(18, 0), LocalTime.of(21, 0), BigDecimal.valueOf(30));
    createReservation(space, customer, date, LocalTime.of(18, 30), LocalTime.of(19, 30), 2, ReservationSessionState.ACCEPTED);
    authenticateAs(customer);

    assertThatThrownBy(() -> reservationSessionService.create(new ReservationSessionRequest(
      space.getId(),
      date,
      LocalTime.of(19, 0),
      LocalTime.of(20, 0),
      2,
      ReservationSessionType.REHEARSAL,
      null,
      null
    )))
      .isInstanceOf(OperationNotAllowed.class)
      .hasMessageContaining("overlaps with another active reservation");
  }

  @Test
  void createReservationSucceedsInEarlierFreeRangeWhenLaterRangeIsReserved() throws Exception {
    var manager = createUser("partial-free-manager@example.com", "password123");
    var customer = createUser("partial-free-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    LocalDate date = LocalDate.now().plusDays(4);
    createSchedule(space, date.getDayOfWeek(), LocalTime.of(10, 0), LocalTime.of(14, 0), BigDecimal.valueOf(30));
    createReservation(space, customer, date, LocalTime.of(12, 0), LocalTime.of(13, 0), 2, ReservationSessionState.ACCEPTED);
    authenticateAs(customer);

    var reservation = reservationSessionService.create(new ReservationSessionRequest(
      space.getId(),
      date,
      LocalTime.of(10, 0),
      LocalTime.of(11, 0),
      2,
      ReservationSessionType.REHEARSAL,
      null,
      null
    ));

    assertThat(reservation.startTime()).isEqualTo(LocalTime.of(10, 0));
    assertThat(reservation.endTime()).isEqualTo(LocalTime.of(11, 0));
    assertThat(reservation.state()).isEqualTo(ReservationSessionState.PENDING);

    var availability = calculatedAvailabilityService.findAvailability(space.getId(), date);

    assertThat(availability.slots())
      .extracting(slot -> slot.startTime() + "-" + slot.endTime())
      .containsExactly("11:00-12:00", "13:00-14:00");
  }

  @Test
  void updatePendingReservationIgnoresCurrentReservationWhenCheckingAvailability() throws Exception {
    var manager = createUser("update-self-manager@example.com", "password123");
    var customer = createUser("update-self-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    LocalDate date = LocalDate.now().plusDays(4);
    createSchedule(space, date.getDayOfWeek(), LocalTime.of(18, 0), LocalTime.of(21, 0), BigDecimal.valueOf(30));
    var reservation = createReservation(space, customer, date, LocalTime.of(18, 30), LocalTime.of(19, 30), 2, ReservationSessionState.PENDING);
    authenticateAs(customer);

    var updatedReservation = reservationSessionService.update(reservation.getId(), new ReservationSessionRequest(
      space.getId(),
      date,
      LocalTime.of(18, 30),
      LocalTime.of(20, 0),
      3,
      ReservationSessionType.REHEARSAL,
      "Updated rehearsal",
      null
    ));

    assertThat(updatedReservation.id()).isEqualTo(reservation.getId());
    assertThat(updatedReservation.endTime()).isEqualTo(LocalTime.of(20, 0));
    assertThat(updatedReservation.attendeesCount()).isEqualTo(3);
  }

  @Test
  void createReservationRejectsAttendeesAboveSpaceCapacity() {
    var manager = createUser("capacity-manager@example.com", "password123");
    var customer = createUser("capacity-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    LocalDate date = LocalDate.now().plusDays(5);
    createSchedule(space, date.getDayOfWeek(), LocalTime.of(18, 0), LocalTime.of(20, 0), BigDecimal.valueOf(30));
    authenticateAs(customer);

    assertThatThrownBy(() -> reservationSessionService.create(new ReservationSessionRequest(
      space.getId(),
      date,
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      99,
      ReservationSessionType.REHEARSAL,
      null,
      null
    )))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("The attendees count cannot exceed the musical space capacity");
  }

  @Test
  void cancelAndStateTransitionsRespectBusinessRules() throws Exception {
    var manager = createUser("state-manager@example.com", "password123");
    var customer = createUser("state-customer@example.com", "password123");
    var admin = createAdmin("state-admin@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    LocalDate date = LocalDate.now().plusDays(6);
    createSchedule(space, date.getDayOfWeek(), LocalTime.of(18, 0), LocalTime.of(21, 0), BigDecimal.valueOf(30));

    var pendingReservation = createReservation(space, customer, date, LocalTime.of(18, 0), LocalTime.of(19, 0), 2, ReservationSessionState.PENDING);
    authenticateAs(manager);
    var acceptedReservation = reservationSessionService.updateState(
      pendingReservation.getId(),
      new ReservationStateUpdateRequest(ReservationSessionState.ACCEPTED)
    );
    assertThat(acceptedReservation.state()).isEqualTo(ReservationSessionState.ACCEPTED);

    assertThatThrownBy(() -> reservationSessionService.updateState(
      pendingReservation.getId(),
      new ReservationStateUpdateRequest(ReservationSessionState.COMPLETED)
    ))
      .isInstanceOf(OperationNotAllowed.class)
      .hasMessageContaining("already ended");

    var pastAcceptedReservation = createReservation(
      space,
      customer,
      LocalDate.now().minusDays(1),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.ACCEPTED
    );
    var completedReservation = reservationSessionService.updateState(
      pastAcceptedReservation.getId(),
      new ReservationStateUpdateRequest(ReservationSessionState.COMPLETED)
    );
    assertThat(completedReservation.state()).isEqualTo(ReservationSessionState.COMPLETED);

    var otherReservation = createReservation(space, customer, date.plusDays(1), LocalTime.of(18, 0), LocalTime.of(19, 0), 2, ReservationSessionState.ACCEPTED);
    authenticateAs(admin);
    assertThatThrownBy(() -> reservationSessionService.updateState(
      otherReservation.getId(),
      new ReservationStateUpdateRequest(ReservationSessionState.REJECTED)
    ))
      .isInstanceOf(OperationNotAllowed.class)
      .hasMessageContaining("Administrators cannot mutate reservations from the public user flow");

    var cancelledReservation = reservationSessionService.cancelAdmin(
      otherReservation.getId(),
      new ReservationCancellationRequest("Cancelled by admin for operational reasons")
    );
    assertThat(cancelledReservation.state()).isEqualTo(ReservationSessionState.CANCELLED);
    assertThat(cancelledReservation.cancellationReason()).isEqualTo("Cancelled by admin for operational reasons");

    var startedReservation = createReservation(
      space,
      customer,
      LocalDate.now().minusDays(2),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.ACCEPTED
    );
    assertThatThrownBy(() -> reservationSessionService.cancel(
      startedReservation.getId(),
      new ReservationCancellationRequest("Too late")
    ))
      .isInstanceOf(OperationNotAllowed.class)
      .hasMessageContaining("Administrators cannot mutate reservations from the public user flow");

    authenticateAs(customer);
    assertThatThrownBy(() -> reservationSessionService.cancel(
      startedReservation.getId(),
      new ReservationCancellationRequest("Too late")
    ))
      .isInstanceOf(OperationNotAllowed.class)
      .hasMessageContaining("already started");
  }

  @Test
  void invalidStateTransitionIsRejected() {
    var manager = createUser("invalid-state-manager@example.com", "password123");
    var customer = createUser("invalid-state-customer@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    var reservation = createReservation(
      space,
      customer,
      LocalDate.now().plusDays(7),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.PENDING
    );
    authenticateAs(manager);

    assertThatThrownBy(() -> reservationSessionService.updateState(
      reservation.getId(),
      new ReservationStateUpdateRequest(ReservationSessionState.COMPLETED)
    ))
      .isInstanceOf(OperationNotAllowed.class)
      .hasMessageContaining("Invalid reservation state transition");
  }

  @Test
  void reservationCanBeViewedByOwnerManagerAndAdmin() throws Exception {
    var manager = createUser("view-manager@example.com", "password123");
    var customer = createUser("view-customer@example.com", "password123");
    var admin = createAdmin("view-admin@example.com", "password123");
    var outsider = createUser("view-outsider@example.com", "password123");
    var space = createSpace(manager, MusicalSpaceApprovalStatus.APPROVED, true);
    var reservation = createReservation(
      space,
      customer,
      LocalDate.now().plusDays(8),
      LocalTime.of(18, 0),
      LocalTime.of(19, 0),
      2,
      ReservationSessionState.PENDING
    );

    authenticateAs(customer);
    assertThat(reservationSessionService.findById(reservation.getId()).id()).isEqualTo(reservation.getId());

    authenticateAs(manager);
    assertThat(reservationSessionService.findById(reservation.getId()).id()).isEqualTo(reservation.getId());

    authenticateAs(admin);
    assertThat(reservationSessionService.findById(reservation.getId()).id()).isEqualTo(reservation.getId());

    authenticateAs(outsider);
    assertThatThrownBy(() -> reservationSessionService.findById(reservation.getId()))
      .isInstanceOf(OperationNotAllowed.class)
      .hasMessageContaining("cannot view this reservation");
  }
}
