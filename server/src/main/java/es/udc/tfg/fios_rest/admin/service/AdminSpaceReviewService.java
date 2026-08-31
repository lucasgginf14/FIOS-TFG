package es.udc.tfg.fios_rest.admin.service;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.spacereview.service.SpaceReviewService;
import es.udc.tfg.fios_rest.spacereview.service.dto.SpaceReviewView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class AdminSpaceReviewService {

  @Autowired
  private SpaceReviewService spaceReviewService;

  @Transactional(readOnly = true)
  public List<SpaceReviewView> findAll() {
    return spaceReviewService.findAllAdmin();
  }

  public void delete(Long id) throws NotFoundException {
    spaceReviewService.deleteAdmin(id);
  }
}
