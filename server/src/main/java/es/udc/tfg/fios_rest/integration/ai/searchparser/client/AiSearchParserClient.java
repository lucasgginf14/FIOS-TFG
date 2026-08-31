package es.udc.tfg.fios_rest.integration.ai.searchparser.client;

import es.udc.tfg.fios_rest.integration.ai.searchparser.dto.AiSearchParserContext;
import es.udc.tfg.fios_rest.integration.ai.searchparser.dto.GeminiSearchParsePayload;
import es.udc.tfg.fios_rest.integration.ai.searchparser.service.AiSearchParserException;

public interface AiSearchParserClient {

  GeminiSearchParsePayload parse(String text, AiSearchParserContext context) throws AiSearchParserException;
}
