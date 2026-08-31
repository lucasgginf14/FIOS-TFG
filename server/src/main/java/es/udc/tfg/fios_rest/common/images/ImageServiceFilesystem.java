package es.udc.tfg.fios_rest.common.images;

import es.udc.tfg.fios_rest.common.config.Properties;
import es.udc.tfg.fios_rest.common.exceptions.model.ModelException;
import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class ImageServiceFilesystem implements ImageService {

  private static final Logger logger = LoggerFactory.getLogger(ImageServiceFilesystem.class);
  private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;
  private static final Map<String, String> ALLOWED_EXTENSIONS = Map.of(
    ".jpg", MediaType.IMAGE_JPEG_VALUE,
    ".jpeg", MediaType.IMAGE_JPEG_VALUE,
    ".png", MediaType.IMAGE_PNG_VALUE,
    ".gif", MediaType.IMAGE_GIF_VALUE,
    ".webp", "image/webp"
  );

  private final Properties properties;
  private Path rootLocation;

  public ImageServiceFilesystem(Properties properties) {
    this.properties = properties;
  }

  @Override
  public String saveImage(MultipartFile file, Long id) throws ModelException {
    validateImage(file);
    String filename = StringUtils.cleanPath(Objects.requireNonNullElse(file.getOriginalFilename(), ""));
    String extension = getExtension(file, filename);
    validateImageBytes(file, extension);

    try (InputStream is = file.getInputStream()) {
      Files.copy(is, resolveStoredPath(id + extension), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      logger.warn("Image could not be saved", e);
      throw new ModelException("No se pudo guardar la imagen.");
    }

    return filename;
  }

  @Override
  public ImageUploadResult saveImage(MultipartFile file) throws ModelException {
    validateImage(file);
    String originalFilename = StringUtils.cleanPath(Objects.requireNonNullElse(file.getOriginalFilename(), ""));
    String extension = getExtension(file, originalFilename);
    validateImageBytes(file, extension);
    String filename = UUID.randomUUID() + extension;

    try (InputStream is = file.getInputStream()) {
      Files.copy(is, resolveStoredPath(filename), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      logger.warn("Image could not be saved", e);
      throw new ModelException("No se pudo guardar la imagen.");
    }

    return new ImageUploadResult(filename, getImageMediaType(filename), file.getSize());
  }

  @Override
  public ImageDto getImagen(Long id, String nombreImagen) throws ModelException {
    return getImage(id + getExtension(nombreImagen));
  }

  @Override
  public ImageDto getImage(String filename) throws ModelException {
    String safeFilename = sanitizeStoredFilename(filename);
    Path path = resolveStoredPath(safeFilename);

    if (!Files.exists(path) || !Files.isRegularFile(path)) {
      throw new NotFoundException(safeFilename, ImageDto.class);
    }

    try {
      return new ImageDto(
        safeFilename,
        getImageMediaType(safeFilename),
        Files.newInputStream(path),
        Files.size(path)
      );
    } catch (IOException e) {
      logger.warn("Image could not be retrieved", e);
      throw new ModelException("No se pudo recuperar la imagen.");
    }
  }

  @Override
  public void deleteImage(Long id, String nombreImagen) throws ModelException {
    deleteImage(id + getExtension(nombreImagen));
  }

  @Override
  public void deleteImage(String filename) throws ModelException {
    String safeFilename = sanitizeStoredFilename(filename);
    Path path = resolveStoredPath(safeFilename);

    try {
      if (!Files.exists(path)) {
        throw new ModelException("La imagen no existe.");
      }
      Files.delete(path);
    } catch (IOException e) {
      logger.warn("Image could not be deleted", e);
      throw new ModelException("No se pudo eliminar la imagen.");
    }
  }

  private Path getRootLocation() {
    if (rootLocation == null) {
      rootLocation = Paths.get(properties.getRutaImagenes()).toAbsolutePath().normalize();

      try {
        Files.createDirectories(rootLocation);
      } catch (IOException e) {
        throw new RuntimeException("No se puede crear el directorio de imagenes", e);
      }
    }

    return rootLocation;
  }

  private void validateImage(MultipartFile file) throws ModelException {
    if (file == null || file.isEmpty()) {
      throw new ModelException("Selecciona una imagen antes de continuar.");
    }

    if (file.getSize() > MAX_IMAGE_SIZE) {
      throw new ModelException("La imagen supera el tamaño máximo permitido de 5 MB.");
    }

    String contentType = file.getContentType();
    if (StringUtils.hasText(contentType) && !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
      throw new ModelException("El archivo debe ser una imagen.");
    }
  }

  private String sanitizeStoredFilename(String filename) throws ModelException {
    String safeFilename = StringUtils.cleanPath(Objects.requireNonNullElse(filename, "")).trim();

    if (!StringUtils.hasText(safeFilename) || safeFilename.contains("/") || safeFilename.contains("\\")) {
      throw new ModelException("Nombre de imagen inválido.");
    }

    getExtension(safeFilename);
    return safeFilename;
  }

  private String getExtension(MultipartFile file, String filename) throws ModelException {
    if (StringUtils.hasText(filename) && filename.lastIndexOf(".") >= 0) {
      return getExtension(filename);
    }

    String contentType = file.getContentType();
    if (!StringUtils.hasText(contentType)) {
      throw new ModelException("La imagen debe tener extensión jpg, jpeg, png, gif o webp.");
    }

    return switch (contentType.toLowerCase(Locale.ROOT)) {
      case MediaType.IMAGE_JPEG_VALUE -> ".jpg";
      case MediaType.IMAGE_PNG_VALUE -> ".png";
      case MediaType.IMAGE_GIF_VALUE -> ".gif";
      case "image/webp" -> ".webp";
      default -> throw new ModelException("La imagen debe tener extensión jpg, jpeg, png, gif o webp.");
    };
  }

  private String getExtension(String filename) throws ModelException {
    int extensionStart = filename.lastIndexOf(".");

    if (extensionStart < 0 || extensionStart == filename.length() - 1) {
      throw new ModelException("La imagen debe tener extensión jpg, jpeg, png, gif o webp.");
    }

    String extension = filename.substring(extensionStart).toLowerCase(Locale.ROOT);
    if (!ALLOWED_EXTENSIONS.containsKey(extension)) {
      throw new ModelException("La imagen debe tener extensión jpg, jpeg, png, gif o webp.");
    }

    return extension;
  }

  private void validateImageBytes(MultipartFile file, String extension) throws ModelException {
    if (".webp".equals(extension)) {
      validateWebpBytes(file);
      return;
    }

    try (ImageInputStream imageInputStream = ImageIO.createImageInputStream(file.getInputStream())) {
      if (imageInputStream == null) {
        throw new ModelException("El archivo debe ser una imagen válida.");
      }

      Iterator<ImageReader> readers = ImageIO.getImageReaders(imageInputStream);
      if (!readers.hasNext()) {
        throw new ModelException("El archivo debe ser una imagen válida.");
      }

      ImageReader reader = readers.next();
      try {
        reader.setInput(imageInputStream, true, true);
        String detectedFormat = reader.getFormatName();
        if (!extensionMatchesDetectedFormat(extension, detectedFormat)) {
          throw new ModelException("La extensión de la imagen no coincide con su contenido.");
        }

        int width = reader.getWidth(0);
        int height = reader.getHeight(0);
        if (width <= 0 || height <= 0) {
          throw new ModelException("El archivo debe ser una imagen válida.");
        }
      } finally {
        reader.dispose();
      }
    } catch (IOException | RuntimeException e) {
      throw new ModelException("El archivo debe ser una imagen válida.");
    }
  }

  private boolean extensionMatchesDetectedFormat(String extension, String detectedFormat) {
    if (!StringUtils.hasText(detectedFormat)) {
      return false;
    }

    String normalizedFormat = detectedFormat.toLowerCase(Locale.ROOT);
    return switch (extension) {
      case ".jpg", ".jpeg" -> "jpeg".equals(normalizedFormat) || "jpg".equals(normalizedFormat);
      case ".png" -> "png".equals(normalizedFormat);
      case ".gif" -> "gif".equals(normalizedFormat);
      default -> false;
    };
  }

  private void validateWebpBytes(MultipartFile file) throws ModelException {
    try (InputStream inputStream = file.getInputStream()) {
      byte[] header = inputStream.readNBytes(12);
      boolean webpSignature = header.length == 12
        && header[0] == 'R'
        && header[1] == 'I'
        && header[2] == 'F'
        && header[3] == 'F'
        && header[8] == 'W'
        && header[9] == 'E'
        && header[10] == 'B'
        && header[11] == 'P';

      if (!webpSignature) {
        throw new ModelException("El archivo debe ser una imagen válida.");
      }
    } catch (IOException | RuntimeException e) {
      throw new ModelException("El archivo debe ser una imagen válida.");
    }
  }

  private String getImageMediaType(String filename) throws ModelException {
    return ALLOWED_EXTENSIONS.getOrDefault(getExtension(filename), MediaType.IMAGE_JPEG_VALUE);
  }

  private Path resolveStoredPath(String filename) throws ModelException {
    Path destination = getRootLocation().resolve(filename).normalize();

    if (!destination.startsWith(getRootLocation())) {
      throw new ModelException("Nombre de imagen inválido.");
    }

    return destination;
  }
}
