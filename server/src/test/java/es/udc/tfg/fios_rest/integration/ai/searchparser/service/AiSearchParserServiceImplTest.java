package es.udc.tfg.fios_rest.integration.ai.searchparser.service;

import es.udc.tfg.fios_rest.common.config.Properties;
import es.udc.tfg.fios_rest.integration.ai.searchparser.dto.GeminiSearchParsePayload;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceType;
import es.udc.tfg.fios_rest.search.persistence.entity.SearchIntent;
import es.udc.tfg.fios_rest.search.persistence.entity.SearchNeedType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class AiSearchParserServiceImplTest {

  @Test
  void normalizesAliasesFlexibleTimesBudgetAndConfidence() throws Exception {
    AiSearchParserServiceImpl service = serviceReturning(new GeminiSearchParsePayload(
      "local",
      "Vigo",
      null,
      "18h",
      "20:00:00",
      4,
      BigDecimal.valueOf(40.567),
      "local de ensaio",
      "R&B",
      "ensaiar",
      1.4d
    ));

    var result = service.parse("Busco local de ensaio en Vigo para catro persoas de 18 a 20");

    assertThat(result.intent()).isEqualTo(SearchIntent.SPACE);
    assertThat(result.city()).isEqualTo("Vigo");
    assertThat(result.startTime()).isEqualTo(LocalTime.of(18, 0));
    assertThat(result.endTime()).isEqualTo(LocalTime.of(20, 0));
    assertThat(result.peopleCount()).isEqualTo(4);
    assertThat(result.maxBudget()).isEqualByComparingTo("40.57");
    assertThat(result.spaceType()).isEqualTo(MusicalSpaceType.REHEARSAL_ROOM);
    assertThat(result.musicalGenre()).isEqualTo("r&b");
    assertThat(result.needType()).isEqualTo(SearchNeedType.REHEARSAL);
    assertThat(result.confidence()).isEqualTo(1.0d);
  }

  @Test
  void dropsDanglingEndTimeAndInvalidRanges() throws Exception {
    AiSearchParserServiceImpl danglingEndService = serviceReturning(new GeminiSearchParsePayload(
      "SPACE",
      null,
      null,
      null,
      "20:00",
      null,
      null,
      null,
      null,
      null,
      0.7d
    ));

    assertThat(danglingEndService.parse("hasta las 20").endTime()).isNull();

    AiSearchParserServiceImpl invalidRangeService = serviceReturning(new GeminiSearchParsePayload(
      "SPACE",
      null,
      null,
      "22",
      "20",
      null,
      null,
      null,
      null,
      null,
      0.7d
    ));

    var result = invalidRangeService.parse("de 22 a 20");

    assertThat(result.startTime()).isNull();
    assertThat(result.endTime()).isNull();
  }

  @Test
  void normalizesAutonomousCommunityAliasesAndDateRanges() throws Exception {
    LocalDate today = LocalDate.now();
    AiSearchParserServiceImpl service = serviceReturning(new GeminiSearchParsePayload(
      "EVENT",
      null,
      null,
      "Catalunya",
      null,
      today.toString(),
      today.plusDays(7).toString(),
      null,
      null,
      null,
      null,
      null,
      null,
      "concerto",
      0.91d
    ));

    var result = service.parse("eventos en Catalunya nos proximos dias");

    assertThat(result.intent()).isEqualTo(SearchIntent.EVENT);
    assertThat(result.autonomousCommunity()).isEqualTo("Catalu\u00f1a");
    assertThat(result.date()).isNull();
    assertThat(result.dateFrom()).isEqualTo(today);
    assertThat(result.dateTo()).isEqualTo(today.plusDays(7));
    assertThat(result.needType()).isEqualTo(SearchNeedType.PERFORMANCE);
  }

  @Test
  void promotesCommunityReturnedAsCityAndDropsInvalidDateRange() throws Exception {
    AiSearchParserServiceImpl service = serviceReturning(new GeminiSearchParsePayload(
      "EVENT",
      "Galicia",
      null,
      null,
      null,
      LocalDate.now().plusDays(7).toString(),
      LocalDate.now().toString(),
      null,
      null,
      null,
      null,
      null,
      null,
      "performance",
      0.8d
    ));

    var result = service.parse("eventos en Galicia");

    assertThat(result.city()).isNull();
    assertThat(result.autonomousCommunity()).isEqualTo("Galicia");
    assertThat(result.dateFrom()).isNull();
    assertThat(result.dateTo()).isNull();
  }

  private AiSearchParserServiceImpl serviceReturning(GeminiSearchParsePayload payload) {
    return new AiSearchParserServiceImpl((text, context) -> payload, new Properties());
  }
}
