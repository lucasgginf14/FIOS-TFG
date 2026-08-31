package es.udc.tfg.fios_rest.instrument.service;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.instrument.persistence.dao.InstrumentDao;
import es.udc.tfg.fios_rest.instrument.persistence.entity.Instrument;
import es.udc.tfg.fios_rest.instrument.service.dto.InstrumentRef;
import es.udc.tfg.fios_rest.instrument.service.dto.InstrumentRequest;
import es.udc.tfg.fios_rest.instrument.service.dto.InstrumentView;
import es.udc.tfg.fios_rest.instrument.service.dto.UserInstrumentUpdateRequest;
import es.udc.tfg.fios_rest.user.persistence.dao.UserDao;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import es.udc.tfg.fios_rest.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class InstrumentService {

  @Autowired
  private InstrumentDao instrumentDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private UserService userService;

  private String normalizeName(String name) {
    if (name == null) {
      throw new IllegalArgumentException("The instrument name is obligatory");
    }

    String normalizedName = name.trim();
    if (normalizedName.isEmpty()) {
      throw new IllegalArgumentException("The instrument name is obligatory");
    }
    return normalizedName;
  }

  private void validateUniqueName(String name, Long currentInstrumentId) {
    instrumentDao.findByName(name)
      .filter(instrument -> !instrument.getId().equals(currentInstrumentId))
      .ifPresent(instrument -> {
        throw new IllegalArgumentException("An instrument with this name already exists");
      });
  }

  @Transactional(readOnly = true)
  public List<InstrumentRef> findAll() {
    return instrumentDao.findAll().stream()
      .map(InstrumentRef::from)
      .toList();
  }

  @Transactional(readOnly = true)
  public InstrumentView findById(Long id) throws NotFoundException {
    return InstrumentView.from(findInstrument(id));
  }

  public InstrumentView create(InstrumentRequest request) {
    String name = normalizeName(request.name());
    validateUniqueName(name, null);

    Instrument instrument = new Instrument(name, request.category());
    instrumentDao.save(instrument);

    return InstrumentView.from(instrument);
  }

  public InstrumentView update(Long id, InstrumentRequest request) throws NotFoundException {
    Instrument instrument = findInstrument(id);
    String name = normalizeName(request.name());
    validateUniqueName(name, id);

    instrument.setName(name);
    instrument.setCategory(request.category());

    return InstrumentView.from(instrumentDao.update(instrument));
  }

  @Transactional(readOnly = true)
  public List<InstrumentRef> findMyInstruments() throws NotFoundException {
    return primaryInstrumentRefs(findCurrentUser());
  }

  public List<InstrumentRef> updateMyInstruments(UserInstrumentUpdateRequest request) throws NotFoundException {
    User user = findCurrentUser();

    if (request.instrumentIds() == null) {
      throw new IllegalArgumentException("The instrument id collection is obligatory");
    }

    if (request.instrumentIds().size() > 1) {
      throw new IllegalArgumentException("Only one principal instrument can be associated with a user");
    }

    if (request.instrumentIds().isEmpty()) {
      user.setPrimaryInstrument(null);
      userDao.update(user);
      return List.of();
    }

    Long instrumentId = request.instrumentIds().iterator().next();
    if (instrumentId == null || instrumentId <= 0) {
      throw new IllegalArgumentException("The instrument id must be positive");
    }

    user.setPrimaryInstrument(findInstrument(instrumentId));
    userDao.update(user);

    return primaryInstrumentRefs(user);
  }

  private Instrument findInstrument(Long id) throws NotFoundException {
    return instrumentDao.findById(id)
      .orElseThrow(() -> new NotFoundException(id.toString(), Instrument.class));
  }

  private User findCurrentUser() throws NotFoundException {
    Long currentUserId = userService.getCurrentUserId();
    return userDao.findById(currentUserId)
      .orElseThrow(() -> new NotFoundException(currentUserId.toString(), User.class));
  }

  private List<InstrumentRef> primaryInstrumentRefs(User user) {
    Instrument primaryInstrument = user.getPrimaryInstrument();
    return primaryInstrument == null ? List.of() : List.of(InstrumentRef.from(primaryInstrument));
  }
}
