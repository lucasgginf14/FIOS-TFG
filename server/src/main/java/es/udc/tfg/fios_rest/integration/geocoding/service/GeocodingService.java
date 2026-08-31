package es.udc.tfg.fios_rest.integration.geocoding.service;

import es.udc.tfg.fios_rest.integration.geocoding.dto.GeocodingFreeTextRequest;
import es.udc.tfg.fios_rest.integration.geocoding.dto.GeocodingResultView;
import es.udc.tfg.fios_rest.integration.geocoding.dto.GeocodingStructuredRequest;

import java.util.List;
import java.util.Optional;

public interface GeocodingService {

  List<GeocodingResultView> search(GeocodingFreeTextRequest request) throws GeocodingException;

  List<GeocodingResultView> search(GeocodingStructuredRequest request) throws GeocodingException;

  Optional<GeocodingResultView> findFirstValidResult(GeocodingStructuredRequest request) throws GeocodingException;
}
