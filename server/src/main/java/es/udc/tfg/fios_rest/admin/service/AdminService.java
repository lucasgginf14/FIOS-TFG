package es.udc.tfg.fios_rest.admin.service;

import es.udc.tfg.fios_rest.admin.service.dto.AdminOverviewView;
import es.udc.tfg.fios_rest.bandrecruitment.persistence.dao.BandRecruitmentDao;
import es.udc.tfg.fios_rest.event.persistence.dao.EventDao;
import es.udc.tfg.fios_rest.musicalspace.persistence.dao.MusicalSpaceDao;
import es.udc.tfg.fios_rest.reservationsession.persistence.dao.ReservationSessionDao;
import es.udc.tfg.fios_rest.spacereview.persistence.dao.SpaceReviewDao;
import es.udc.tfg.fios_rest.user.persistence.dao.UserDao;
import es.udc.tfg.fios_rest.userreview.persistence.dao.UserReviewDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdminService {

  @Autowired
  private UserDao userDao;

  @Autowired
  private MusicalSpaceDao musicalSpaceDao;

  @Autowired
  private ReservationSessionDao reservationSessionDao;

  @Autowired
  private SpaceReviewDao spaceReviewDao;

  @Autowired
  private UserReviewDao userReviewDao;

  @Autowired
  private EventDao eventDao;

  @Autowired
  private BandRecruitmentDao bandRecruitmentDao;

  public AdminOverviewView getOverview() {
    return new AdminOverviewView(
      userDao.findAll().size(),
      musicalSpaceDao.findAll().size(),
      reservationSessionDao.findAll().size(),
      spaceReviewDao.findAll().size(),
      userReviewDao.findAll().size(),
      eventDao.findAll(null).size(),
      bandRecruitmentDao.findAll().size()
    );
  }
}
