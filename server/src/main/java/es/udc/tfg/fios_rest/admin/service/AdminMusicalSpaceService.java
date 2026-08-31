package es.udc.tfg.fios_rest.admin.service;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceApprovalStatus;
import es.udc.tfg.fios_rest.musicalspace.service.MusicalSpaceService;
import es.udc.tfg.fios_rest.musicalspace.service.dto.MusicalSpaceRef;
import es.udc.tfg.fios_rest.musicalspace.service.dto.MusicalSpaceView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class AdminMusicalSpaceService {

  @Autowired
  private MusicalSpaceService musicalSpaceService;

  @Transactional(readOnly = true)
  public List<MusicalSpaceRef> findAll() {
    return musicalSpaceService.findAllAdmin();
  }

  @Transactional(readOnly = true)
  public MusicalSpaceView findById(Long id) throws NotFoundException {
    return musicalSpaceService.findByIdAdmin(id);
  }

  public MusicalSpaceView updateApprovalStatus(Long id, MusicalSpaceApprovalStatus approvalStatus) throws NotFoundException {
    return musicalSpaceService.updateApprovalStatusAdmin(id, approvalStatus);
  }
}
