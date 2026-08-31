package es.udc.tfg.fios_rest.bandmember.persistence.dao;

import es.udc.tfg.fios_rest.band.persistence.entity.Band;
import es.udc.tfg.fios_rest.bandmember.persistence.entity.BandMember;

import java.util.Collection;
import java.util.Optional;

public interface BandMemberDao {

  Collection<BandMember> findByBand(Long bandId);

  Collection<BandMember> findActiveByBand(Long bandId);

  Collection<BandMember> findActiveByUser(Long userId);

  Optional<BandMember> findById(Long id);

  Optional<BandMember> findByBandAndUser(Long bandId, Long userId);

  Optional<BandMember> findActiveByBandAndUser(Long bandId, Long userId);

  BandMember save(BandMember bandMember);

  BandMember update(BandMember bandMember);

  void deactivateActiveByBand(Band band);
}
