package es.udc.tfg.fios_rest.integration.geocoding.client;

import es.udc.tfg.fios_rest.integration.geocoding.dto.GeocodingFreeTextRequest;
import es.udc.tfg.fios_rest.integration.geocoding.dto.GeocodingProvider;
import es.udc.tfg.fios_rest.integration.geocoding.dto.GeocodingResultView;
import es.udc.tfg.fios_rest.integration.geocoding.dto.GeocodingStructuredRequest;
import es.udc.tfg.fios_rest.integration.geocoding.service.GeocodingException;

import java.util.List;

public interface GeocodingClient {

  List<GeocodingResultView> search(GeocodingFreeTextRequest request) throws GeocodingException;

  List<GeocodingResultView> search(GeocodingStructuredRequest request) throws GeocodingException;

  GeocodingProvider getProvider();
}
