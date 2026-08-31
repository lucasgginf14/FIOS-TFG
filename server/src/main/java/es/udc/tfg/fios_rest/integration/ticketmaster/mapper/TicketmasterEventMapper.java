package es.udc.tfg.fios_rest.integration.ticketmaster.mapper;

import es.udc.tfg.fios_rest.event.persistence.entity.Event;
import es.udc.tfg.fios_rest.event.persistence.entity.EventSource;
import es.udc.tfg.fios_rest.event.persistence.entity.EventStatus;
import es.udc.tfg.fios_rest.event.persistence.entity.EventType;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterClassification;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterDates;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterEventEmbedded;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterEventItem;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterEventView;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterImage;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterLocation;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterNamedValue;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterPriceRange;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterProvider;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterStart;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterVenue;
import es.udc.tfg.fios_rest.integration.ticketmaster.service.TicketmasterException;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Component
public class TicketmasterEventMapper {
  private static final int MAX_DESCRIPTION_LENGTH = 2000;

  public TicketmasterEventView toView(TicketmasterEventItem item) {
    TicketmasterVenue venue = primaryVenue(item);

    return new TicketmasterEventView(
      normalizeNullable(item.id()),
      normalizeNullable(item.name()),
      description(item),
      extractEventDate(item.dates()),
      extractStartTime(item.dates()),
      venue != null ? normalizeNullable(venue.name()) : null,
      venue != null ? namedValue(venue.city()) : null,
      venue != null ? namedValue(venue.state()) : null,
      venue != null ? namedValue(venue.country()) : null,
      venue != null ? parseCoordinate(venue.location(), true) : null,
      venue != null ? parseCoordinate(venue.location(), false) : null,
      choosePosterImage(item.images()),
      normalizeNullable(item.url()),
      extractMusicalGenre(item.classifications()),
      priceMin(item.priceRanges()),
      priceMax(item.priceRanges()),
      TicketmasterProvider.TICKETMASTER
    );
  }

  public Event toImportedEvent(TicketmasterEventItem item, User createdBy) throws TicketmasterException {
    TicketmasterVenue venue = primaryVenue(item);

    Event event = new Event(
      required(item.name(), "Ticketmaster event title"),
      description(item),
      extractRequiredEventDate(item),
      extractStartTime(item.dates()),
      null,
      extractMusicalGenre(item.classifications()),
      null,
      resolvedTicketPrice(item.priceRanges()),
      choosePosterImage(item.images()),
      EventStatus.PUBLISHED,
      detectEventType(item),
      EventSource.EXTERNAL,
      required(venue != null ? venue.name() : null, "Ticketmaster venue name"),
      venue != null ? parseCoordinate(venue.location(), true) : null,
      venue != null ? parseCoordinate(venue.location(), false) : null,
      required(venue != null ? namedValue(venue.city()) : null, "Ticketmaster venue city"),
      venue != null ? namedValue(venue.state()) : null,
      required(venue != null ? namedValue(venue.country()) : null, "Ticketmaster venue country"),
      buildLocation(venue),
      TicketmasterProvider.TICKETMASTER.name(),
      required(item.id(), "Ticketmaster external id"),
      normalizeNullable(item.url()),
      null,
      null,
      createdBy
    );

    event.markImportedNow();
    return event;
  }

  private TicketmasterVenue primaryVenue(TicketmasterEventItem item) {
    TicketmasterEventEmbedded embedded = item.embedded();
    if (embedded == null || embedded.venues() == null || embedded.venues().isEmpty()) {
      return null;
    }

    return embedded.venues().get(0);
  }

  private String description(TicketmasterEventItem item) {
    String providedDescription = firstNonBlank(item.description(), item.info(), item.pleaseNote());
    if (providedDescription != null) {
      return truncateDescription(cleanDescription(providedDescription));
    }

    return truncateDescription(buildFallbackDescription(item));
  }

  private String buildFallbackDescription(TicketmasterEventItem item) {
    String title = normalizeNullable(item.name());
    String genre = extractMusicalGenre(item.classifications());
    TicketmasterVenue venue = primaryVenue(item);
    String venueName = venue != null ? normalizeNullable(venue.name()) : null;
    String city = venue != null ? namedValue(venue.city()) : null;
    List<String> sentences = new ArrayList<>();

    String location = joinNonBlank(venueName, city);
    if (title != null && location != null) {
      sentences.add(title + " en " + location + ".");
    } else if (title != null) {
      sentences.add(title + ".");
    }

    if (genre != null) {
      sentences.add("Evento de " + genre.toLowerCase(Locale.ROOT) + " publicado por Ticketmaster.");
    } else {
      sentences.add("Evento publicado por Ticketmaster.");
    }

    return String.join(" ", sentences);
  }

  private String cleanDescription(String value) {
    return value
      .replaceAll("<[^>]+>", " ")
      .replace("&nbsp;", " ")
      .replace("&amp;", "&")
      .replace("&quot;", "\"")
      .replace("&#39;", "'")
      .replace("&apos;", "'")
      .replace("&lt;", "<")
      .replace("&gt;", ">")
      .replaceAll("\\s+", " ")
      .trim();
  }

  private String truncateDescription(String value) {
    String normalized = normalizeNullable(value);
    if (normalized == null || normalized.length() <= MAX_DESCRIPTION_LENGTH) {
      return normalized;
    }

    return normalized.substring(0, MAX_DESCRIPTION_LENGTH - 3).trim() + "...";
  }

  private LocalDate extractRequiredEventDate(TicketmasterEventItem item) throws TicketmasterException {
    LocalDate eventDate = extractEventDate(item.dates());
    if (eventDate == null) {
      throw TicketmasterException.invalidResponse("Ticketmaster event does not include a valid event date", null);
    }

    return eventDate;
  }

  private LocalDate extractEventDate(TicketmasterDates dates) {
    if (dates == null || dates.start() == null) {
      return null;
    }

    TicketmasterStart start = dates.start();
    try {
      if (StringUtils.hasText(start.localDate())) {
        return LocalDate.parse(start.localDate().trim());
      }

      if (StringUtils.hasText(start.dateTime())) {
        return OffsetDateTime.parse(start.dateTime().trim()).toLocalDate();
      }
    } catch (DateTimeParseException ignored) {
      return null;
    }

    return null;
  }

  private LocalTime extractStartTime(TicketmasterDates dates) {
    if (dates == null || dates.start() == null) {
      return null;
    }

    TicketmasterStart start = dates.start();
    try {
      if (StringUtils.hasText(start.localTime())) {
        return LocalTime.parse(start.localTime().trim());
      }

      if (StringUtils.hasText(start.dateTime())) {
        return OffsetDateTime.parse(start.dateTime().trim()).toLocalTime().withNano(0);
      }
    } catch (DateTimeParseException ignored) {
      return null;
    }

    return null;
  }

  private String extractMusicalGenre(List<TicketmasterClassification> classifications) {
    TicketmasterClassification classification = primaryClassification(classifications);
    if (classification == null) {
      return null;
    }

    return firstNonBlank(
      namedValue(classification.subGenre()),
      namedValue(classification.genre()),
      namedValue(classification.segment())
    );
  }

  private TicketmasterClassification primaryClassification(List<TicketmasterClassification> classifications) {
    if (classifications == null || classifications.isEmpty()) {
      return null;
    }

    return classifications.stream()
      .sorted(Comparator.comparing(classification -> Boolean.TRUE.equals(classification.primary()) ? 0 : 1))
      .findFirst()
      .orElse(null);
  }

  private EventType detectEventType(TicketmasterEventItem item) {
    String combined = firstNonBlank(item.name(), extractMusicalGenre(item.classifications()));
    if (combined == null) {
      return EventType.OTHER;
    }

    String normalized = combined.toLowerCase(Locale.ROOT);
    if (normalized.contains("festival")) {
      return EventType.FESTIVAL;
    }
    if (normalized.contains("workshop")) {
      return EventType.WORKSHOP;
    }
    if (normalized.contains("open mic")) {
      return EventType.OPEN_MIC;
    }
    if (normalized.contains("jam")) {
      return EventType.JAM_SESSION;
    }
    if (normalized.contains("showcase")) {
      return EventType.SHOWCASE;
    }
    if (normalized.contains("concert") || normalized.contains("tour") || normalized.contains("music")) {
      return EventType.CONCERT;
    }

    return EventType.OTHER;
  }

  private BigDecimal resolvedTicketPrice(List<TicketmasterPriceRange> priceRanges) {
    BigDecimal minimum = priceMin(priceRanges);
    if (minimum != null) {
      return minimum;
    }

    return priceMax(priceRanges);
  }

  private BigDecimal priceMin(List<TicketmasterPriceRange> priceRanges) {
    if (priceRanges == null || priceRanges.isEmpty()) {
      return null;
    }

    return priceRanges.stream()
      .map(TicketmasterPriceRange::min)
      .filter(value -> value != null && value >= 0)
      .map(BigDecimal::valueOf)
      .min(Comparator.naturalOrder())
      .orElse(null);
  }

  private BigDecimal priceMax(List<TicketmasterPriceRange> priceRanges) {
    if (priceRanges == null || priceRanges.isEmpty()) {
      return null;
    }

    return priceRanges.stream()
      .map(TicketmasterPriceRange::max)
      .filter(value -> value != null && value >= 0)
      .map(BigDecimal::valueOf)
      .max(Comparator.naturalOrder())
      .orElse(null);
  }

  private String choosePosterImage(List<TicketmasterImage> images) {
    if (images == null || images.isEmpty()) {
      return null;
    }

    return images.stream()
      .filter(image -> StringUtils.hasText(image.url()))
      .max(Comparator
        .comparing((TicketmasterImage image) -> "16_9".equalsIgnoreCase(image.ratio()) ? 1 : 0)
        .thenComparing(image -> safeArea(image.width(), image.height())))
      .map(TicketmasterImage::url)
      .map(String::trim)
      .orElse(null);
  }

  private long safeArea(Integer width, Integer height) {
    if (width == null || height == null || width < 1 || height < 1) {
      return 0L;
    }

    return (long) width * height;
  }

  private Double parseCoordinate(TicketmasterLocation location, boolean latitude) {
    if (location == null) {
      return null;
    }

    String rawValue = latitude ? location.latitude() : location.longitude();
    if (!StringUtils.hasText(rawValue)) {
      return null;
    }

    try {
      double parsed = Double.parseDouble(rawValue.trim());
      if (latitude && (parsed < -90 || parsed > 90)) {
        return null;
      }
      if (!latitude && (parsed < -180 || parsed > 180)) {
        return null;
      }
      return parsed;
    } catch (NumberFormatException ignored) {
      return null;
    }
  }

  private String buildLocation(TicketmasterVenue venue) {
    if (venue == null) {
      return null;
    }

    String addressLine = venue.address() == null ? null : normalizeNullable(venue.address().line1());
    String administrativeLocation = joinNonBlank(
      namedValue(venue.city()),
      namedValue(venue.state()),
      namedValue(venue.country())
    );

    return firstNonBlank(addressLine, administrativeLocation);
  }

  private String joinNonBlank(String... values) {
    return Arrays.stream(values)
      .filter(StringUtils::hasText)
      .map(String::trim)
      .reduce((left, right) -> left + ", " + right)
      .orElse(null);
  }

  private String namedValue(TicketmasterNamedValue value) {
    return value == null ? null : normalizeNullable(value.name());
  }

  private String required(String value, String fieldName) throws TicketmasterException {
    if (!StringUtils.hasText(value)) {
      throw TicketmasterException.invalidResponse(fieldName + " is missing in Ticketmaster response", null);
    }

    return value.trim();
  }

  private String firstNonBlank(String... values) {
    for (String value : values) {
      if (StringUtils.hasText(value)) {
        return value.trim();
      }
    }

    return null;
  }

  private String normalizeNullable(String value) {
    return StringUtils.hasText(value) ? value.trim() : null;
  }
}
