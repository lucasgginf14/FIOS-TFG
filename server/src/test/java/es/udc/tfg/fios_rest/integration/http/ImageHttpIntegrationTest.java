package es.udc.tfg.fios_rest.integration.http;

import es.udc.tfg.fios_rest.common.config.Properties;
import es.udc.tfg.fios_rest.support.IntegrationTestSupport;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ImageHttpIntegrationTest extends IntegrationTestSupport {

  private final List<String> uploadedFilenames = new ArrayList<>();

  @Autowired
  private Properties properties;

  @AfterEach
  void deleteUploadedImages() throws Exception {
    Path root = Paths.get(properties.getRutaImagenes()).toAbsolutePath().normalize();

    for (String filename : uploadedFilenames) {
      Files.deleteIfExists(root.resolve(filename).normalize());
    }
  }

  @Test
  void uploadImageRequiresAuthentication() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
      "file",
      "avatar.png",
      MediaType.IMAGE_PNG_VALUE,
      "fake image".getBytes()
    );

    mockMvc.perform(multipart("/api/images").file(file))
      .andExpect(status().isUnauthorized())
      .andExpect(jsonPath("$.code").value("AUTHENTICATION_REQUIRED"));
  }

  @Test
  void uploadImageReturnsPublicUrlAndCanBeRead() throws Exception {
    User user = createUser("image-user@example.com", "password");
    byte[] content = createValidPng();
    MockMultipartFile file = new MockMultipartFile(
      "file",
      "avatar.png",
      MediaType.IMAGE_PNG_VALUE,
      content
    );

    MvcResult uploadResult = mockMvc.perform(multipart("/api/images")
        .file(file)
        .header("Authorization", "Bearer " + tokenFor(user)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.url", containsString("/api/images/")))
      .andExpect(jsonPath("$.filename", containsString(".png")))
      .andReturn();

    String filename = objectMapper
      .readTree(uploadResult.getResponse().getContentAsString())
      .get("filename")
      .asText();
    uploadedFilenames.add(filename);

    mockMvc.perform(get("/api/images/{filename}", filename))
      .andExpect(status().isOk())
      .andExpect(header().string("X-Content-Type-Options", "nosniff"))
      .andExpect(content().contentType(MediaType.IMAGE_PNG))
      .andExpect(content().bytes(content));
  }

  @Test
  void uploadImageRejectsHtmlFileRenamedAsJpeg() throws Exception {
    User user = createUser("image-html-user@example.com", "password");
    MockMultipartFile file = new MockMultipartFile(
      "file",
      "avatar.jpg",
      MediaType.IMAGE_JPEG_VALUE,
      "<html><script>alert(1)</script></html>".getBytes(StandardCharsets.UTF_8)
    );

    mockMvc.perform(multipart("/api/images")
        .file(file)
        .header("Authorization", "Bearer " + tokenFor(user)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.code").value("INVALID_FILE"))
      .andExpect(jsonPath("$.message").value("El archivo enviado no es v" + (char) 225 + "lido."));
  }

  @Test
  void uploadImageRejectsTooLargeFileWithControlledError() throws Exception {
    User user = createUser("image-too-large-user@example.com", "password");
    MockMultipartFile file = new MockMultipartFile(
      "file",
      "large.png",
      MediaType.IMAGE_PNG_VALUE,
      new byte[(5 * 1024 * 1024) + 1]
    );

    mockMvc.perform(multipart("/api/images")
        .file(file)
        .header("Authorization", "Bearer " + tokenFor(user)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.code").value("FILE_TOO_LARGE"));
  }

  private byte[] createValidPng() throws Exception {
    BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    ImageIO.write(image, "png", output);
    return output.toByteArray();
  }
}
