package es.udc.tfg.fios_rest.equipment.service;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.equipment.persistence.dao.EquipmentDao;
import es.udc.tfg.fios_rest.equipment.persistence.entity.Equipment;
import es.udc.tfg.fios_rest.equipment.service.dto.EquipmentRef;
import es.udc.tfg.fios_rest.equipment.service.dto.EquipmentRequest;
import es.udc.tfg.fios_rest.equipment.service.dto.EquipmentView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class EquipmentService {

  @Autowired
  private EquipmentDao equipmentDao;

  @Transactional(readOnly = true)
  public List<EquipmentRef> findAll() {
    return equipmentDao.findAll().stream()
      .map(EquipmentRef::from)
      .toList();
  }

  @Transactional(readOnly = true)
  public EquipmentView findById(Long id) throws NotFoundException {
    return EquipmentView.from(findEquipment(id));
  }

  public EquipmentView create(EquipmentRequest request) {
    String name = normalizeName(request.name());
    validateUniqueName(name, null);

    Equipment equipment = new Equipment(name, request.category(), request.description());
    equipmentDao.save(equipment);

    return EquipmentView.from(equipment);
  }

  public EquipmentView update(Long id, EquipmentRequest request) throws NotFoundException {
    Equipment equipment = findEquipment(id);
    String name = normalizeName(request.name());
    validateUniqueName(name, id);

    equipment.setName(name);
    equipment.setCategory(request.category());
    equipment.setDescription(request.description());

    return EquipmentView.from(equipmentDao.update(equipment));
  }

  private Equipment findEquipment(Long id) throws NotFoundException {
    return equipmentDao.findById(id)
      .orElseThrow(() -> new NotFoundException(id.toString(), Equipment.class));
  }

  private String normalizeName(String name) {
    if (name == null) {
      throw new IllegalArgumentException("The equipment name is obligatory");
    }

    String normalizedName = name.trim();
    if (normalizedName.isEmpty()) {
      throw new IllegalArgumentException("The equipment name is obligatory");
    }

    return normalizedName;
  }

  private void validateUniqueName(String name, Long currentEquipmentId) {
    equipmentDao.findByName(name)
      .filter(equipment -> !equipment.getId().equals(currentEquipmentId))
      .ifPresent(equipment -> {
        throw new IllegalArgumentException("An equipment with this name already exists");
      });
  }
}
