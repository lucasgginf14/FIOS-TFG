package es.udc.tfg.fios_rest.common.images;

import es.udc.tfg.fios_rest.common.exceptions.model.ModelException;
import org.springframework.web.multipart.MultipartFile;


public interface ImageService {

  String saveImage(MultipartFile file, Long id) throws ModelException;

  ImageUploadResult saveImage(MultipartFile file) throws ModelException;

  ImageDto getImagen(Long id, String imagen) throws ModelException;

  ImageDto getImage(String filename) throws ModelException;

  void deleteImage(Long id, String nombreImagen) throws ModelException;

  void deleteImage(String filename) throws ModelException;
}
