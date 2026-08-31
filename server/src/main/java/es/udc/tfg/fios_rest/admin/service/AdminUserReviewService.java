package es.udc.tfg.fios_rest.admin.service;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.userreview.service.UserReviewService;
import es.udc.tfg.fios_rest.userreview.service.dto.UserReviewView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class AdminUserReviewService {

  @Autowired
  private UserReviewService userReviewService;

  @Transactional(readOnly = true)
  public List<UserReviewView> findAll() {
    return userReviewService.findAllAdmin();
  }

  public void delete(Long id) throws NotFoundException {
    userReviewService.deleteAdmin(id);
  }
}
