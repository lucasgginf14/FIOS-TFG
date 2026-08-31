package es.udc.tfg.fios_rest.integration.ai.searchparser.service;

import es.udc.tfg.fios_rest.integration.ai.searchparser.dto.AiSearchParseResult;

public interface AiSearchParserService {

  AiSearchParseResult parse(String text) throws AiSearchParserException;

  boolean isEnabled();
}
