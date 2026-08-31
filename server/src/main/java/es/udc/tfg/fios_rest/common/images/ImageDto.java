package es.udc.tfg.fios_rest.common.images;

import java.io.InputStream;

public record ImageDto(
  String filename,
  String mimeType,
  InputStream content,
  long size
) {
}
