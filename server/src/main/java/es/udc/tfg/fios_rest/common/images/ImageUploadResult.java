package es.udc.tfg.fios_rest.common.images;

public record ImageUploadResult(
  String filename,
  String mimeType,
  long size
) {
}
