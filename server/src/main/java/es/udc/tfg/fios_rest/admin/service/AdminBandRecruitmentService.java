package es.udc.tfg.fios_rest.admin.service;

import es.udc.tfg.fios_rest.bandrecruitment.service.BandRecruitmentService;
import es.udc.tfg.fios_rest.bandrecruitment.service.dto.BandRecruitmentRef;
import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class AdminBandRecruitmentService {

  @Autowired
  private BandRecruitmentService bandRecruitmentService;

  @Transactional(readOnly = true)
  public List<BandRecruitmentRef> findAll() {
    return bandRecruitmentService.findAllAdmin();
  }

  public void delete(Long id) throws NotFoundException {
    bandRecruitmentService.deleteAdmin(id);
  }
}
