package es.udc.tfg.fios_rest.integration.ai.searchparser.controller;

import es.udc.tfg.fios_rest.integration.ai.searchparser.dto.AiSearchParseRequest;
import es.udc.tfg.fios_rest.integration.ai.searchparser.dto.AiSearchParseResult;
import es.udc.tfg.fios_rest.integration.ai.searchparser.service.AiSearchParserException;
import es.udc.tfg.fios_rest.integration.ai.searchparser.service.AiSearchParserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/search-parser")
public class AiSearchParserResource {

  private final AiSearchParserService aiSearchParserService;

  public AiSearchParserResource(AiSearchParserService aiSearchParserService) {
    this.aiSearchParserService = aiSearchParserService;
  }

  @PostMapping
  public ResponseEntity<AiSearchParseResult> parse(
    @Valid @RequestBody AiSearchParseRequest request
  ) throws AiSearchParserException {
    return ResponseEntity.ok(aiSearchParserService.parse(request.text()));
  }
}
