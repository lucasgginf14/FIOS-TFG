package es.udc.tfg.fios_rest.search.controller;

import es.udc.tfg.fios_rest.common.exceptions.model.NotFoundException;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceType;
import es.udc.tfg.fios_rest.search.service.SearchService;
import es.udc.tfg.fios_rest.search.service.dto.NaturalLanguageSearchRequest;
import es.udc.tfg.fios_rest.search.service.dto.NaturalLanguageSearchView;
import es.udc.tfg.fios_rest.search.service.dto.SearchCriteria;
import es.udc.tfg.fios_rest.search.service.dto.SearchHistoryView;
import es.udc.tfg.fios_rest.search.service.dto.SearchMapView;
import es.udc.tfg.fios_rest.search.service.dto.SearchView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/search")
@Validated
public class SearchResource {

  private final SearchService searchService;

  public SearchResource(SearchService searchService) {
    this.searchService = searchService;
  }

  @GetMapping
  public ResponseEntity<SearchView> search(
    @RequestParam(required = false) @Size(max = 120) String city,
    @RequestParam(required = false) @Size(max = 120) String province,
    @RequestParam(required = false) @Size(max = 120) String autonomousCommunity,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
    @RequestParam(required = false) MusicalSpaceType spaceType,
    @RequestParam(required = false) @Positive Integer peopleCount,
    @RequestParam(required = false) @Size(max = 100) String musicalGenre,
    @RequestParam(required = false) @PositiveOrZero BigDecimal maxBudget
  ) {
    return ResponseEntity.ok(searchService.search(new SearchCriteria(
      city,
      province,
      autonomousCommunity,
      date,
      dateFrom,
      dateTo,
      startTime,
      endTime,
      spaceType,
      peopleCount,
      musicalGenre,
      maxBudget
    )));
  }

  @PostMapping("/natural-language")
  public ResponseEntity<NaturalLanguageSearchView> searchNaturalLanguage(
    @Valid @RequestBody NaturalLanguageSearchRequest request
  ) {
    return ResponseEntity.ok(searchService.searchNaturalLanguage(request));
  }

  @GetMapping("/history")
  public ResponseEntity<List<SearchHistoryView>> findHistory() throws NotFoundException {
    return ResponseEntity.ok(searchService.findHistory());
  }

  @GetMapping("/map")
  public ResponseEntity<SearchMapView> searchMap(
    @RequestParam(required = false) @Size(max = 120) String city,
    @RequestParam(required = false) @Size(max = 120) String province,
    @RequestParam(required = false) @Size(max = 120) String autonomousCommunity,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
    @RequestParam(required = false) MusicalSpaceType spaceType,
    @RequestParam(required = false) @Positive Integer peopleCount,
    @RequestParam(required = false) @Size(max = 100) String musicalGenre,
    @RequestParam(required = false) @PositiveOrZero BigDecimal maxBudget
  ) {
    return ResponseEntity.ok(searchService.searchMap(new SearchCriteria(
      city,
      province,
      autonomousCommunity,
      date,
      dateFrom,
      dateTo,
      startTime,
      endTime,
      spaceType,
      peopleCount,
      musicalGenre,
      maxBudget
    )));
  }
}
