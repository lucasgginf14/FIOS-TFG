package es.udc.tfg.fios_rest.bandmember.controller;

import es.udc.tfg.fios_rest.bandmember.service.BandMemberService;
import es.udc.tfg.fios_rest.bandmember.service.dto.BandMemberCandidateRef;
import es.udc.tfg.fios_rest.bandmember.service.dto.BandMemberCreateRequest;
import es.udc.tfg.fios_rest.bandmember.service.dto.BandMemberRef;
import es.udc.tfg.fios_rest.bandmember.service.dto.BandMemberRoleUpdateRequest;
import es.udc.tfg.fios_rest.bandmember.service.dto.BandMemberView;
import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.common.exceptions.model.OperationNotAllowed;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/bands/{bandId}")
public class BandMemberResource {

  private final BandMemberService bandMemberService;

  public BandMemberResource(BandMemberService bandMemberService) {
    this.bandMemberService = bandMemberService;
  }

  @GetMapping("/members")
  public ResponseEntity<List<BandMemberRef>> findActiveMembersByBand(
    @PathVariable Long bandId
  ) throws NotFoundException {
    return ResponseEntity.ok(bandMemberService.findActiveMembersByBand(bandId));
  }

  @GetMapping("/members/candidates")
  public ResponseEntity<List<BandMemberCandidateRef>> searchMemberCandidates(
    @PathVariable Long bandId,
    @RequestParam(defaultValue = "") String query
  ) throws NotFoundException, OperationNotAllowed {
    return ResponseEntity.ok(bandMemberService.searchMemberCandidates(bandId, query));
  }

  @PostMapping("/members")
  public ResponseEntity<BandMemberView> addMember(
    @PathVariable Long bandId,
    @Valid @RequestBody BandMemberCreateRequest request
  ) throws NotFoundException, OperationNotAllowed {
    BandMemberView bandMember = bandMemberService.addMember(bandId, request);
    return ResponseEntity.created(URI.create("/api/bands/" + bandId + "/members/" + bandMember.id()))
      .body(bandMember);
  }

  @PatchMapping("/members/{memberId}/role")
  public ResponseEntity<BandMemberView> updateRole(
    @PathVariable Long bandId,
    @PathVariable Long memberId,
    @Valid @RequestBody BandMemberRoleUpdateRequest request
  ) throws NotFoundException, OperationNotAllowed {
    return ResponseEntity.ok(bandMemberService.updateRole(bandId, memberId, request));
  }

  @PatchMapping("/members/{memberId}/deactivate")
  public ResponseEntity<BandMemberView> deactivateMember(
    @PathVariable Long bandId,
    @PathVariable Long memberId
  ) throws NotFoundException, OperationNotAllowed {
    return ResponseEntity.ok(bandMemberService.deactivateMember(bandId, memberId));
  }

  @PatchMapping("/leave")
  public ResponseEntity<BandMemberView> leaveBand(
    @PathVariable Long bandId
  ) throws NotFoundException, OperationNotAllowed {
    return ResponseEntity.ok(bandMemberService.leaveBand(bandId));
  }
}
