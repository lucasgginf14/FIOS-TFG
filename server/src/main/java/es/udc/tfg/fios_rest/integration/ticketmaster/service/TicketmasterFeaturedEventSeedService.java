package es.udc.tfg.fios_rest.integration.ticketmaster.service;

import es.udc.tfg.fios_rest.event.persistence.dao.EventDao;
import es.udc.tfg.fios_rest.event.persistence.entity.Event;
import es.udc.tfg.fios_rest.event.persistence.entity.EventSource;
import es.udc.tfg.fios_rest.event.persistence.entity.EventStatus;
import es.udc.tfg.fios_rest.event.persistence.entity.EventType;
import es.udc.tfg.fios_rest.integration.ticketmaster.dto.TicketmasterProvider;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@Transactional(rollbackFor = Exception.class)
public class TicketmasterFeaturedEventSeedService {

  private static final String EXTERNAL_ID_PREFIX = "tm-es-2026-featured-";

  private final EventDao eventDao;

  public TicketmasterFeaturedEventSeedService(EventDao eventDao) {
    this.eventDao = eventDao;
  }

  public int ensureFeaturedEvents(User createdBy) {
    int createdCount = 0;

    for (FeaturedEvent event : featuredEvents()) {
      if (eventDao.findByExternalSourceAndExternalId(
        TicketmasterProvider.TICKETMASTER.name(),
        event.externalId()
      ).map(existingEvent -> {
        updateFeaturedDataIfNeeded(existingEvent, event);
        return existingEvent;
      }).isPresent()) {
        continue;
      }

      eventDao.save(toEntity(event, createdBy));
      createdCount++;
    }

    return createdCount;
  }

  private void updateFeaturedDataIfNeeded(Event event, FeaturedEvent seed) {
    Venue venue = venue(seed.venueName(), seed.city());
    String description = buildDescription(seed);
    boolean updated = false;

    if (!Objects.equals(seed.title(), event.getTitle())) {
      event.setTitle(seed.title());
      updated = true;
    }

    if (!Objects.equals(description, event.getDescription())) {
      event.setDescription(description);
      updated = true;
    }

    if (!Objects.equals(seed.musicalGenre(), event.getMusicalGenre())) {
      event.setMusicalGenre(seed.musicalGenre());
      updated = true;
    }

    if (!Objects.equals(seed.venueName(), event.getVenueName())) {
      event.setVenueName(seed.venueName());
      updated = true;
    }

    if (!Objects.equals(seed.city(), event.getCity())) {
      event.setCity(seed.city());
      updated = true;
    }

    if (!Objects.equals(seed.province(), event.getProvince())) {
      event.setProvince(seed.province());
      updated = true;
    }

    if (!Objects.equals("España", event.getCountry())) {
      event.setCountry("España");
      updated = true;
    }

    if (!Objects.equals(venue.location(), event.getLocation())) {
      event.setLocation(venue.location());
      updated = true;
    }

    if (!Objects.equals(venue.latitude(), event.getLatitude())) {
      event.setLatitude(venue.latitude());
      updated = true;
    }

    if (!Objects.equals(venue.longitude(), event.getLongitude())) {
      event.setLongitude(venue.longitude());
      updated = true;
    }

    if (!Objects.equals(seed.externalUrl(), event.getExternalUrl())) {
      event.setExternalUrl(seed.externalUrl());
      updated = true;
    }

    if (seed.posterImage() != null && !Objects.equals(seed.posterImage(), event.getPosterImage())) {
      event.setPosterImage(seed.posterImage());
      updated = true;
    }

    if (updated) {
      eventDao.update(event);
    }
  }

  public int featuredEventCount() {
    return featuredEvents().size();
  }

  private Event toEntity(FeaturedEvent seed, User createdBy) {
    Venue venue = venue(seed.venueName(), seed.city());
    Event event = new Event(
      seed.title(),
      buildDescription(seed),
      seed.eventDate(),
      seed.startTime(),
      null,
      seed.musicalGenre(),
      null,
      null,
      seed.posterImage(),
      EventStatus.PUBLISHED,
      EventType.CONCERT,
      EventSource.EXTERNAL,
      seed.venueName(),
      venue.latitude(),
      venue.longitude(),
      seed.city(),
      seed.province(),
      "España",
      venue.location(),
      TicketmasterProvider.TICKETMASTER.name(),
      seed.externalId(),
      seed.externalUrl(),
      null,
      null,
      createdBy
    );

    event.markImportedNow();
    return event;
  }

  private String buildDescription(FeaturedEvent seed) {
    String artist = ticketmasterSearchTerm(seed.title());
    String subtitle = seed.title().startsWith(artist + " - ")
      ? seed.title().substring((artist + " - ").length()).trim()
      : null;
    String description = "Concierto de " + artist + " en " + seed.venueName() + ", " + seed.city() + ".";

    if (subtitle != null && !subtitle.isBlank()) {
      description += " La cita forma parte de " + subtitle + ".";
    } else if (!seed.title().equals(artist)) {
      description += " El cartel incluye " + seed.title() + ".";
    }

    return description
      + " Una propuesta de " + seed.musicalGenre().toLowerCase(Locale.ROOT)
      + " seleccionada desde la agenda musical de Ticketmaster España.";
  }

  private List<FeaturedEvent> featuredEvents() {
    return List.of(
      featured("shakira-2026-10-02-madrid", "Shakira - Las Mujeres Ya No Lloran, Residencia Europea", "2026-10-02", "20:30", "Pop", "Madrid", "Madrid", "ESTADIO SHAKIRA(Iberdrola Music)", "pop, latino, internacional, gran_recinto, residencia", "alta"),
      featured("shakira-2026-10-03-madrid", "Shakira - Las Mujeres Ya No Lloran, Residencia Europea", "2026-10-03", "20:30", "Pop", "Madrid", "Madrid", "ESTADIO SHAKIRA(Iberdrola Music)", "pop, latino, internacional, gran_recinto, residencia", "alta"),
      featured("shakira-2026-10-04-madrid", "Shakira - Las Mujeres Ya No Lloran, Residencia Europea", "2026-10-04", "20:30", "Pop", "Madrid", "Madrid", "ESTADIO SHAKIRA(Iberdrola Music)", "pop, latino, internacional, gran_recinto, residencia", "alta"),
      featured("shakira-2026-10-09-madrid", "Shakira - Las Mujeres Ya No Lloran, Residencia Europea", "2026-10-09", "20:30", "Pop", "Madrid", "Madrid", "ESTADIO SHAKIRA(Iberdrola Music)", "pop, latino, internacional, gran_recinto, residencia", "alta"),
      featured("shakira-2026-10-10-madrid", "Shakira - Las Mujeres Ya No Lloran, Residencia Europea", "2026-10-10", "20:30", "Pop", "Madrid", "Madrid", "ESTADIO SHAKIRA(Iberdrola Music)", "pop, latino, internacional, gran_recinto, residencia", "alta"),
      featured("shakira-2026-10-11-madrid", "Shakira - Las Mujeres Ya No Lloran, Residencia Europea", "2026-10-11", "20:30", "Pop", "Madrid", "Madrid", "ESTADIO SHAKIRA(Iberdrola Music)", "pop, latino, internacional, gran_recinto, residencia", "alta"),

      featured("morat-2026-10-16-barcelona", "Morat - Ya es Mañana Tour", "2026-10-16", "20:00", "Pop", "Barcelona", "Barcelona", "Palau Sant Jordi", "pop, latino, gira_nacional, gran_recinto", "alta"),
      featured("morat-2026-10-17-barcelona", "Morat - Ya es Mañana Tour", "2026-10-17", "20:00", "Pop", "Barcelona", "Barcelona", "Palau Sant Jordi", "pop, latino, gira_nacional, gran_recinto", "alta"),
      featured("morat-2026-10-21-pamplona", "Morat - Ya es Mañana Tour", "2026-10-21", "20:00", "Pop", "Pamplona/Iruña", "Navarra", "Navarra Arena", "pop, latino, gira_nacional, gran_recinto", "alta"),
      featured("morat-2026-10-23-valencia", "Morat - Ya es Mañana Tour", "2026-10-23", "20:00", "Pop", "Valencia", "Valencia", "Roig Arena", "pop, latino, gira_nacional, gran_recinto", "alta"),
      featured("morat-2026-10-25-sevilla", "Morat - Ya es Mañana Tour", "2026-10-25", "20:00", "Pop", "Sevilla", "Sevilla", "Centro Andaluz de Arte Contemporáneo", "pop, latino, gira_nacional, gran_recinto", "alta"),
      featured("morat-2026-10-27-madrid", "Morat - Ya es Mañana Tour", "2026-10-27", "20:00", "Pop", "Madrid", "Madrid", "Movistar Arena", "pop, latino, gira_nacional, gran_recinto", "alta"),
      featured("morat-2026-10-28-madrid", "Morat - Ya es Mañana Tour", "2026-10-28", "20:00", "Pop", "Madrid", "Madrid", "Movistar Arena", "pop, latino, gira_nacional, gran_recinto", "alta"),
      featured("morat-2026-10-30-madrid", "Morat - Ya es Mañana Tour", "2026-10-30", "20:00", "Pop", "Madrid", "Madrid", "Movistar Arena", "pop, latino, gira_nacional, gran_recinto", "alta"),
      featured("morat-2026-10-31-madrid", "Morat - Ya es Mañana Tour", "2026-10-31", "20:00", "Pop", "Madrid", "Madrid", "Movistar Arena", "pop, latino, gira_nacional, gran_recinto", "alta"),

      featured("hombres-g-2026-10-03-barcelona", "Hombres G - Los Mejores Años de nuestra vida", "2026-10-03", "21:00", "Pop Rock", "Barcelona", "Barcelona", "Palau Sant Jordi", "pop_rock, espanol, gira_nacional, nostalgia", "alta"),
      featured("hombres-g-2026-11-14-valencia", "Hombres G - Los Mejores Años de nuestra vida", "2026-11-14", "21:00", "Pop Rock", "Valencia", "Valencia", "Roig Arena", "pop_rock, espanol, gira_nacional, nostalgia", "alta"),
      featured("hombres-g-2026-11-21-zaragoza", "Hombres G - Los Mejores Años de nuestra vida", "2026-11-21", "21:00", "Pop Rock", "Zaragoza", "Zaragoza", "Pabellón Príncipe Felipe", "pop_rock, espanol, gira_nacional, nostalgia", "alta"),
      featured("hombres-g-2026-11-28-pamplona", "Hombres G - Los Mejores Años de nuestra vida", "2026-11-28", "21:00", "Pop Rock", "Pamplona/Iruña", "Navarra", "Navarra Arena", "pop_rock, espanol, gira_nacional, nostalgia", "alta"),
      featured("hombres-g-2026-12-05-a-coruna", "Hombres G - Los Mejores Años de nuestra vida", "2026-12-05", "21:00", "Pop Rock", "A Coruña", "A Coruña", "Coliseum", "pop_rock, espanol, gira_nacional, nostalgia", "alta"),
      featured("hombres-g-2026-12-11-madrid", "Hombres G - Los Mejores Años de nuestra vida", "2026-12-11", "21:00", "Pop Rock", "Madrid", "Madrid", "Movistar Arena", "pop_rock, espanol, gira_nacional, nostalgia", "alta"),

      featured("the-strokes-2026-10-20-barcelona", "The Strokes - Reality Awaits", "2026-10-20", "20:30", "Indie Rock", "Barcelona", "Barcelona", "Palau Sant Jordi", "indie, rock, internacional, gran_recinto", "alta"),
      featured("placebo-2026-10-01-madrid", "Placebo - 30th Anniversary Tour", "2026-10-01", "20:45", "Rock", "Madrid", "Madrid", "Movistar Arena", "rock, alternativo, internacional", "alta"),
      featured("placebo-2026-10-03-barcelona", "Placebo - 30th Anniversary Tour", "2026-10-03", "20:45", "Rock", "Barcelona", "Barcelona", "Sant Jordi Club", "rock, alternativo, internacional", "alta"),
      featured("pitbull-2026-11-04-barcelona", "Pitbull - I'm Back! + Lil Jon", "2026-11-04", "20:30", "Urbano", "Barcelona", "Barcelona", "Palau Sant Jordi", "urbano, pop, internacional, gran_recinto", "alta"),
      featured("pitbull-2026-11-05-madrid", "Pitbull - I'm Back! + Lil Jon", "2026-11-05", "20:30", "Urbano", "Madrid", "Madrid", "Movistar Arena", "urbano, pop, internacional, gran_recinto", "alta"),

      featured("bunbury-2026-12-04-madrid", "Bunbury - Nuevas Mutaciones Tour 2026", "2026-12-04", "20:30", "Rock", "Madrid", "Madrid", "Movistar Arena", "rock, espanol, gira_nacional", "alta"),
      featured("bunbury-2026-12-07-valencia", "Bunbury - Nuevas Mutaciones Tour 2026", "2026-12-07", "20:30", "Rock", "Valencia", "Valencia", "Roig Arena", "rock, espanol, gira_nacional", "alta"),
      featured("bunbury-2026-12-10-a-coruna", "Bunbury - Nuevas Mutaciones Tour 2026", "2026-12-10", "20:30", "Rock", "A Coruña", "A Coruña", "Coliseum", "rock, espanol, gira_nacional", "alta"),
      featured("bunbury-2026-12-12-zaragoza", "Bunbury - Nuevas Mutaciones Tour 2026", "2026-12-12", "20:30", "Rock", "Zaragoza", "Zaragoza", "Pabellón Príncipe Felipe", "rock, espanol, gira_nacional", "alta"),

      featured("niall-horan-2026-10-25-barcelona", "Niall Horan - Dinner Party Live On Tour", "2026-10-25", "21:15", "Pop", "Barcelona", "Barcelona", "Palau Sant Jordi", "pop, internacional, gran_recinto", "alta"),
      featured("yandel-2026-11-14-madrid", "Yandel", "2026-11-14", "21:00", "Urbano", "Madrid", "Madrid", "Movistar Arena", "urbano, latino, reggaeton, gran_recinto", "alta"),
      featured("cazzu-2026-11-25-madrid", "Cazzu - Latinaje", "2026-11-25", "21:00", "Urbano", "Madrid", "Madrid", "Live Las Ventas", "urbano, latino, trap, publico_joven", "alta"),
      featured("cazzu-2026-11-26-madrid", "Cazzu - Latinaje", "2026-11-26", "21:00", "Urbano", "Madrid", "Madrid", "Live Las Ventas", "urbano, latino, trap, publico_joven", "alta"),
      featured("cazzu-2026-11-29-barcelona", "Cazzu - Latinaje", "2026-11-29", "21:00", "Urbano", "Barcelona", "Barcelona", "Razzmatazz", "urbano, latino, trap, publico_joven", "alta"),
      featured("cazzu-2026-11-30-barcelona", "Cazzu - Latinaje", "2026-11-30", "21:00", "Urbano", "Barcelona", "Barcelona", "Razzmatazz", "urbano, latino, trap, publico_joven", "alta"),

      featured("simple-plan-2026-10-26-barcelona", "Simple Plan", "2026-10-26", "21:00", "Pop Punk", "Barcelona", "Barcelona", "Sant Jordi Club", "pop_punk, rock, internacional", "media"),
      featured("simple-plan-2026-10-27-madrid", "Simple Plan", "2026-10-27", "21:00", "Pop Punk", "Madrid", "Madrid", "Palacio Vistalegre", "pop_punk, rock, internacional", "media"),
      featured("amon-amarth-2026-11-14-madrid", "Amon Amarth", "2026-11-14", "18:00", "Metal", "Madrid", "Madrid", "Palacio Vistalegre", "metal, internacional, nicho", "media"),
      featured("hatsune-miku-2026-11-24-madrid", "Hatsune Miku - MIKU EXPO 2026 Europe", "2026-11-24", "20:30", "J-Pop", "Madrid", "Madrid", "Palacio Vistalegre", "jpop, vocaloid, tecnologico, fandom", "media"),
      featured("hatsune-miku-2026-11-25-madrid", "Hatsune Miku - MIKU EXPO 2026 Europe", "2026-11-25", "20:30", "J-Pop", "Madrid", "Madrid", "Palacio Vistalegre", "jpop, vocaloid, tecnologico, fandom", "media"),
      featured("tokio-hotel-2026-11-23-madrid", "Tokio Hotel", "2026-11-23", "20:00", "Pop Rock", "Madrid", "Madrid", "Palacio Vistalegre", "pop_rock, internacional, nostalgia", "media"),
      featured("los-fabulosos-cadillacs-2026-11-29-madrid", "Los Fabulosos Cadillacs", "2026-11-29", "20:30", "Rock Latino", "Madrid", "Madrid", "Live Las Ventas", "rock, ska, latino", "media"),
      featured("joe-bonamassa-2026-11-07-barcelona", "Joe Bonamassa - 2026 Europe Tour", "2026-11-07", "20:00", "Blues Rock", "Barcelona", "Barcelona", "Sant Jordi Club", "blues, rock, internacional", "media"),
      featured("joe-bonamassa-2026-11-08-madrid", "Joe Bonamassa - 2026 Europe Tour", "2026-11-08", "20:00", "Blues Rock", "Madrid", "Madrid", "Palacio Vistalegre", "blues, rock, internacional", "media"),
      featured("arlo-parks-2026-11-10-madrid", "Arlo Parks - Desire Tour", "2026-11-10", "20:30", "Indie Pop", "Madrid", "Madrid", "La Riviera", "indie, pop, alternativo", "media"),
      featured("arlo-parks-2026-11-13-barcelona", "Arlo Parks - Desire Tour", "2026-11-13", "20:30", "Indie Pop", "Barcelona", "Barcelona", "Sala Apolo", "indie, pop, alternativo", "media"),
      featured("kamelot-2026-11-21-barcelona", "Kamelot + Exit Eden + Temperance", "2026-11-21", "19:00", "Metal", "Barcelona", "Barcelona", "Sala Razzmatazz 1", "metal, sinfonico, internacional", "media"),
      featured("kamelot-2026-11-22-madrid", "Kamelot + Exit Eden + Temperance", "2026-11-22", "19:00", "Metal", "Madrid", "Madrid", "La Riviera", "metal, sinfonico, internacional", "media"),
      featured("alex-ubago-2026-11-04-madrid", "Alex Ubago", "2026-11-04", "21:00", "Pop", "Madrid", "Madrid", "La Riviera", "pop, espanol", "media"),
      featured("alex-ubago-2026-12-10-valencia", "Alex Ubago", "2026-12-10", "21:00", "Pop", "Valencia", "Valencia", "Auditorio Roig Arena", "pop, espanol", "media"),
      featured("anastacia-2026-10-24-gijon", "Anastacia", "2026-10-24", "21:00", "Pop", "Gijón", "Asturias", "Gijón Arena", "pop, internacional", "media")
    );
  }

  private FeaturedEvent featured(
    String idSuffix,
    String title,
    String date,
    String time,
    String musicalGenre,
    String city,
    String province,
    String venueName,
    String tags,
    String priority
  ) {
    return new FeaturedEvent(
      EXTERNAL_ID_PREFIX + idSuffix,
      title,
      LocalDate.parse(date),
      LocalTime.parse(time),
      musicalGenre,
      city,
      province,
      venueName,
      tags,
      priority,
      ticketmasterExternalUrl(title),
      ticketmasterArtistImageUrl(ticketmasterSearchTerm(title))
    );
  }

  private String ticketmasterExternalUrl(String title) {
    String searchTerm = ticketmasterSearchTerm(title);
    String artistUrl = ticketmasterArtistUrl(searchTerm);
    if (artistUrl != null) {
      return artistUrl;
    }

    return "https://www.ticketmaster.es/search?q="
      + URLEncoder.encode(searchTerm, StandardCharsets.UTF_8);
  }

  private String ticketmasterArtistUrl(String searchTerm) {
    return switch (searchTerm) {
      case "Shakira" -> "https://www.ticketmaster.es/artist/shakira-entradas/7142";
      case "Morat" -> "https://www.ticketmaster.es/artist/morat-entradas/973802";
      case "Hombres G" -> "https://www.ticketmaster.es/artist/hombres-g-entradas/11284";
      case "The Strokes" -> "https://www.ticketmaster.es/artist/the-strokes-entradas/12759";
      case "Placebo" -> "https://www.ticketmaster.es/artist/placebo-entradas/5183";
      case "Pitbull" -> "https://www.ticketmaster.es/artist/pitbull-entradas/67883";
      case "Bunbury" -> "https://www.ticketmaster.es/artist/bunbury-entradas/13858";
      case "Niall Horan" -> "https://www.ticketmaster.es/artist/niall-horan-entradas/977341";
      case "Cazzu" -> "https://www.ticketmaster.es/artist/cazzu-entradas/1213894";
      case "Simple Plan" -> "https://www.ticketmaster.es/artist/simple-plan-entradas/16164";
      case "Amon Amarth" -> "https://www.ticketmaster.es/artist/amon-amarth-entradas/35498";
      case "Hatsune Miku" -> "https://www.ticketmaster.es/artist/hatsune-miku-entradas/943109";
      case "Tokio Hotel" -> "https://www.ticketmaster.es/artist/tokio-hotel-entradas/40563";
      case "Los Fabulosos Cadillacs" -> "https://www.ticketmaster.es/artist/los-fabulosos-cadillacs-entradas/4446";
      case "Joe Bonamassa" -> "https://www.ticketmaster.es/artist/joe-bonamassa-entradas/18223";
      case "Arlo Parks" -> "https://www.ticketmaster.es/artist/arlo-parks-entradas/1013762";
      case "Kamelot" -> "https://www.ticketmaster.es/artist/kamelot-entradas/31125";
      case "Alex Ubago" -> "https://www.ticketmaster.es/artist/alex-ubago-entradas/121433";
      case "Anastacia" -> "https://www.ticketmaster.es/artist/anastacia-tickets/27328";
      default -> null;
    };
  }

  private String ticketmasterArtistImageUrl(String searchTerm) {
    return switch (searchTerm) {
      case "Shakira" -> "https://s1.ticketm.net/img/tat/dam/a/973/c46a3f3f-d7cd-4d99-8b13-e9dbba2a7973_CUSTOM.jpg";
      case "Morat" -> "https://s1.ticketm.net/img/tat/dam/a/d14/8d33b44e-845c-41cb-8ca2-b03e926a5d14_CUSTOM.jpg";
      case "Hombres G" -> "https://s1.ticketm.net/img/tat/dam/a/e39/cdfc0976-82e1-4b61-bdb6-a30d36e4be39_CUSTOM.jpg";
      case "The Strokes" -> "https://s1.ticketm.net/img/tat/dam/a/b43/fb88f322-a593-45df-b6c5-3af23d5a8b43_CUSTOM.jpg";
      case "Placebo" -> "https://s1.ticketm.net/img/tat/dam/a/f42/283d1b07-26b0-4da5-90ee-261f0479af42_CUSTOM.jpg";
      case "Pitbull" -> "https://s1.ticketm.net/img/tat/dam/a/aeb/81f889da-73cd-4887-8120-e24e0c4d4aeb_CUSTOM.jpg";
      case "Bunbury" -> "https://prismic-images.tmol.io/ticketmaster-tm-global/aYMizt0YXLCxVWtd_1440x450_bunbury.jpg?auto=format,compress&rect=37,0,1067,450&w=2048&h=864";
      case "Niall Horan" -> "https://s1.ticketm.net/img/tat/dam/a/335/442edcd4-95b1-46a0-ba6d-5fe693acd335_CUSTOM.jpg";
      case "Cazzu" -> "https://s1.ticketm.net/img/tat/dam/a/edf/4bcf2a73-7418-4262-b63d-884f91f0fedf_CUSTOM.jpg";
      case "Simple Plan" -> "https://prismic-images.tmol.io/ticketmaster-tm-global/afsI0MBOoF08xqRl_PressPic1_SimplePlan_PhotoBy%40lindseyblane.jpg?auto=format%2Ccompress&rect=0%2C936%2C2732%2C1153&w=2048&h=864";
      case "Amon Amarth" -> "https://s1.ticketm.net/img/tat/dam/a/a6f/545667e2-7fd0-4162-ba28-51d86af04a6f_CUSTOM.jpg";
      case "Hatsune Miku" -> "https://s1.ticketm.net/img/tat/dam/a/f1f/a1378a7a-e230-4d4e-b15c-333280449f1f_CUSTOM.jpg";
      case "Tokio Hotel" -> "https://s1.ticketm.net/img/tat/dam/a/8fa/bf748074-6ab7-4f47-bc84-dd3f400228fa_CUSTOM.jpg";
      case "Los Fabulosos Cadillacs" -> "https://s1.ticketm.net/img/tat/dam/a/65d/3d7f869f-7bda-4446-b3f1-bd3fdbf5765d_CUSTOM.jpg";
      case "Joe Bonamassa" -> "https://s1.ticketm.net/img/tat/dam/a/0f2/5fc2fa27-33d7-47ba-af75-dad7a6d840f2_CUSTOM.jpg";
      case "Arlo Parks" -> "https://prismic-images.tmol.io/ticketmaster-tm-global/aahtH1xvIZEnjVEV_image002-2-.png?auto=format%2Ccompress&rect=0%2C540%2C978%2C413&w=2048&h=864";
      case "Kamelot" -> "https://s1.ticketm.net/img/tat/dam/a/1e7/720ddab1-fe83-4981-a144-66cde98591e7_CUSTOM.jpg";
      case "Alex Ubago" -> "https://s1.ticketm.net/img/tat/dam/a/744/b3f60966-8565-4fc7-9370-ae9a4c97e744_CUSTOM.jpg";
      case "Anastacia" -> "https://s1.ticketm.net/img/tat/dam/a/1b2/c966f26a-47dc-44c1-8432-348fdc9261b2_CUSTOM.jpg";
      default -> null;
    };
  }

  private String ticketmasterSearchTerm(String title) {
    String searchTerm = title;
    int dashIndex = searchTerm.indexOf(" - ");
    if (dashIndex > 0) {
      searchTerm = searchTerm.substring(0, dashIndex);
    }

    int guestsIndex = searchTerm.indexOf(" + ");
    if (guestsIndex > 0) {
      searchTerm = searchTerm.substring(0, guestsIndex);
    }

    return searchTerm.trim();
  }

  private Venue venue(String venueName, String city) {
    return switch (venueName) {
      case "ESTADIO SHAKIRA(Iberdrola Music)" -> new Venue(40.4593, -3.6162, "Madrid");
      case "Movistar Arena" -> new Venue(40.4239, -3.6717, "Av. Felipe II, s/n");
      case "Palau Sant Jordi", "Sant Jordi Club" -> new Venue(41.3634, 2.1520, "Passeig Olimpic, 5-7");
      case "Navarra Arena" -> new Venue(42.7940, -1.6380, "Plaza Aizagerria, 1");
      case "Roig Arena", "Auditorio Roig Arena" -> new Venue(39.4561, -0.3583, "Carrer del Bomber Ramon Duart");
      case "Centro Andaluz de Arte Contemporáneo" -> new Venue(37.3980, -6.0090, "Camino de los Descubrimientos");
      case "Pabellón Príncipe Felipe" -> new Venue(41.6377, -0.8614, "Av. Cesáreo Alierta, 120");
      case "Coliseum" -> new Venue(43.3373, -8.4064, "Rúa Francisco Pérez Carballo, 2");
      case "Palacio Vistalegre" -> new Venue(40.3862, -3.7399, "Calle de Matilde Hernández, s/n");
      case "Live Las Ventas" -> new Venue(40.4327, -3.6638, "Calle de Alcalá, 237");
      case "Razzmatazz", "Sala Razzmatazz 1" -> new Venue(41.3974, 2.1918, "Carrer dels Almogavers, 122");
      case "La Riviera" -> new Venue(40.4139, -3.7227, "Paseo Bajo de la Virgen del Puerto, s/n");
      case "Sala Apolo" -> new Venue(41.3747, 2.1693, "Carrer Nou de la Rambla, 113");
      case "Gijón Arena" -> new Venue(43.5362, -5.6374, "Recinto Ferial Luis Adaro");
      default -> new Venue(null, null, city);
    };
  }

  private record FeaturedEvent(
    String externalId,
    String title,
    LocalDate eventDate,
    LocalTime startTime,
    String musicalGenre,
    String city,
    String province,
    String venueName,
    String tags,
    String priority,
    String externalUrl,
    String posterImage
  ) {
  }

  private record Venue(Double latitude, Double longitude, String location) {
  }
}
