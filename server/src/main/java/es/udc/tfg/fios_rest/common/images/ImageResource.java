package es.udc.tfg.fios_rest.common.images;

import es.udc.tfg.fios_rest.common.exceptions.model.ModelException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Duration;

@RestController
@RequestMapping("/api/images")
public class ImageResource {

  private final ImageService imageService;

  public ImageResource(ImageService imageService) {
    this.imageService = imageService;
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ImageUploadView> uploadImage(@RequestParam("file") MultipartFile file)
    throws ModelException {
    ImageUploadResult upload = imageService.saveImage(file);
    String url = ServletUriComponentsBuilder
      .fromCurrentContextPath()
      .path("/api/images/")
      .path(upload.filename())
      .toUriString();

    return ResponseEntity
      .created(URI.create(url))
      .body(new ImageUploadView(url, upload.filename(), upload.mimeType(), upload.size()));
  }

  @GetMapping("/{filename:.+}")
  public ResponseEntity<InputStreamResource> getImage(@PathVariable String filename)
    throws ModelException {
    ImageDto image = imageService.getImage(filename);

    return ResponseEntity
      .ok()
      .contentType(MediaType.parseMediaType(image.mimeType()))
      .contentLength(image.size())
      .cacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic())
      .header("X-Content-Type-Options", "nosniff")
      .body(new InputStreamResource(image.content()));
  }
}
