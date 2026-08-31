package es.udc.tfg.fios_rest.bandmember.service.dto;

import es.udc.tfg.fios_rest.band.service.dto.BandRef;
import es.udc.tfg.fios_rest.bandmember.persistence.entity.BandMember;
import es.udc.tfg.fios_rest.bandmember.persistence.entity.BandMemberRole;
import es.udc.tfg.fios_rest.instrument.service.dto.InstrumentRef;
import es.udc.tfg.fios_rest.user.service.dto.UserPublicRef;

import java.time.LocalDateTime;
import java.util.List;

public record BandMemberView(
  Long id,
  BandRef band,
  UserPublicRef user,
  BandMemberRole roleInBand,
  List<InstrumentRef> instruments,
  LocalDateTime joinDate,
  LocalDateTime leaveDate,
  boolean active
) {

  public static BandMemberView from(BandMember bandMember) {
    InstrumentRef primaryInstrument = bandMember.getUser().getPrimaryInstrument() == null
      ? null
      : InstrumentRef.from(bandMember.getUser().getPrimaryInstrument());

    return new BandMemberView(
      bandMember.getId(),
      BandRef.from(bandMember.getBand()),
      UserPublicRef.from(bandMember.getUser()),
      bandMember.getRoleInBand(),
      primaryInstrument == null ? List.of() : List.of(primaryInstrument),
      bandMember.getJoinDate(),
      bandMember.getLeaveDate(),
      bandMember.isActive()
    );
  }
}
