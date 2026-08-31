package es.udc.tfg.fios_rest.bandrecruitment.persistence.dao;

import es.udc.tfg.fios_rest.bandrecruitment.persistence.entity.BandRecruitment;

import java.util.Collection;
import java.util.Optional;

public interface BandRecruitmentDao {

  Collection<BandRecruitment> findAll();

  Collection<BandRecruitment> findOpen();

  Collection<BandRecruitment> findOpen(String city, String genre);

  Collection<BandRecruitment> findByBandIds(Collection<Long> bandIds);

  Optional<BandRecruitment> findById(Long id);

  BandRecruitment save(BandRecruitment bandRecruitment);

  BandRecruitment update(BandRecruitment bandRecruitment);

  void delete(BandRecruitment bandRecruitment);
}
