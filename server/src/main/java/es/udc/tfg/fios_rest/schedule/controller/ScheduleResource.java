package es.udc.tfg.fios_rest.schedule.controller;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import es.udc.tfg.fios_rest.schedule.service.ScheduleService;
import es.udc.tfg.fios_rest.schedule.service.dto.ScheduleRequest;
import es.udc.tfg.fios_rest.schedule.service.dto.ScheduleView;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ScheduleResource {

  private final ScheduleService scheduleService;

  public ScheduleResource(ScheduleService scheduleService) {
    this.scheduleService = scheduleService;
  }

  @GetMapping("/musical-spaces/{spaceId}/schedules")
  public ResponseEntity<List<ScheduleView>> findByMusicalSpace(
    @PathVariable Long spaceId
  ) throws NotFoundException {
    return ResponseEntity.ok(scheduleService.findByMusicalSpace(spaceId));
  }

  @PostMapping("/musical-spaces/{spaceId}/schedules")
  public ResponseEntity<ScheduleView> create(
    @PathVariable Long spaceId,
    @Valid @RequestBody ScheduleRequest request
  ) throws NotFoundException, OperationNotAllowed {
    ScheduleView schedule = scheduleService.create(spaceId, request);
    return ResponseEntity.created(URI.create("/api/schedules/" + schedule.id())).body(schedule);
  }

  @PutMapping("/schedules/{id}")
  public ResponseEntity<ScheduleView> update(
    @PathVariable Long id,
    @Valid @RequestBody ScheduleRequest request
  ) throws NotFoundException, OperationNotAllowed {
    return ResponseEntity.ok(scheduleService.update(id, request));
  }

  @DeleteMapping("/schedules/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) throws NotFoundException, OperationNotAllowed {
    scheduleService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
