package es.udc.tfg.fios_rest.common.images;

public record ImageUploadView(
  String url,
  String filename,
  String mimeType,
  long size
) {
}
