package es.udc.tfg.fios_rest.common.config;

import es.udc.tfg.fios_rest.band.persistence.entity.Band;
import es.udc.tfg.fios_rest.bandrecruitment.persistence.entity.BandRecruitment;
import es.udc.tfg.fios_rest.bandrecruitment.persistence.entity.BandRecruitmentLevel;
import es.udc.tfg.fios_rest.bandmember.persistence.entity.BandMember;
import es.udc.tfg.fios_rest.bandmember.persistence.entity.BandMemberRole;
import es.udc.tfg.fios_rest.equipment.persistence.entity.Equipment;
import es.udc.tfg.fios_rest.equipment.persistence.entity.EquipmentCategory;
import es.udc.tfg.fios_rest.event.persistence.entity.Event;
import es.udc.tfg.fios_rest.event.persistence.entity.EventSource;
import es.udc.tfg.fios_rest.event.persistence.entity.EventStatus;
import es.udc.tfg.fios_rest.event.persistence.entity.EventType;
import es.udc.tfg.fios_rest.eventpurchase.persistence.entity.EventPurchase;
import es.udc.tfg.fios_rest.exceptions.persistence.entity.SpaceAvailabilityException;
import es.udc.tfg.fios_rest.exceptions.persistence.entity.SpaceAvailabilityExceptionType;
import es.udc.tfg.fios_rest.favoritespace.persistence.entity.FavoriteSpace;
import es.udc.tfg.fios_rest.instrument.persistence.entity.Instrument;
import es.udc.tfg.fios_rest.instrument.persistence.entity.InstrumentCategory;
import es.udc.tfg.fios_rest.integration.ticketmaster.service.TicketmasterFeaturedEventSeedService;
import es.udc.tfg.fios_rest.message.persistence.entity.Message;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpace;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceApprovalStatus;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceLocation;
import es.udc.tfg.fios_rest.musicalspace.persistence.entity.MusicalSpaceType;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSession;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSessionState;
import es.udc.tfg.fios_rest.reservationsession.persistence.entity.ReservationSessionType;
import es.udc.tfg.fios_rest.schedule.persistence.entity.Schedule;
import es.udc.tfg.fios_rest.search.persistence.entity.Search;
import es.udc.tfg.fios_rest.search.persistence.entity.SearchIntent;
import es.udc.tfg.fios_rest.search.persistence.entity.SearchNeedType;
import es.udc.tfg.fios_rest.spaceequipment.persistence.entity.SpaceEquipment;
import es.udc.tfg.fios_rest.spaceequipment.persistence.entity.SpaceEquipmentState;
import es.udc.tfg.fios_rest.spacereview.persistence.entity.SpaceReview;
import es.udc.tfg.fios_rest.user.persistence.entity.PlatformRole;
import es.udc.tfg.fios_rest.user.persistence.entity.User;
import es.udc.tfg.fios_rest.userreview.persistence.entity.UserReview;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Objects;

@Component
@ConditionalOnProperty(
  name = "database.loader.enabled",
  havingValue = "true",
  matchIfMissing = false
)
public class DatabaseLoader {

  private static final Logger logger = LoggerFactory.getLogger(DatabaseLoader.class);
  public static final String SEED_PASSWORD = "Fios2026!";
  public static final String DEMO_PASSWORD = "12345678a";

  private final TransactionTemplate transactionTemplate;
  private final PasswordEncoder passwordEncoder;
  private final TicketmasterFeaturedEventSeedService ticketmasterFeaturedEventSeedService;

  @PersistenceContext
  private EntityManager entityManager;

  public DatabaseLoader(
    TransactionTemplate transactionTemplate,
    PasswordEncoder passwordEncoder,
    TicketmasterFeaturedEventSeedService ticketmasterFeaturedEventSeedService
  ) {
    this.transactionTemplate = transactionTemplate;
    this.passwordEncoder = passwordEncoder;
    this.ticketmasterFeaturedEventSeedService = ticketmasterFeaturedEventSeedService;
  }

  @PostConstruct
  public void initializeSeedData() {
    transactionTemplate.executeWithoutResult(status -> {
      try {
        long existingUsers = countExistingUsers();
        if (existingUsers > 0) {
          logger.info(
            "DatabaseLoader skipped because the database already contains {} users. Existing data will be preserved.",
            existingUsers
          );
          return;
        }

        loadFinalSeed();
      } catch (RuntimeException exception) {
        status.setRollbackOnly();
        throw exception;
      }
    });
  }

  private long countExistingUsers() {
    Object result = entityManager
      .createNativeQuery("select count(*) from users")
      .getSingleResult();
    return ((Number) result).longValue();
  }

  private void loadFinalSeed() {
    SeedDates dates = buildSeedDates();
    SeedUsers users = createUsers();
    SeedInstruments instruments = createInstruments();
    assignUserInstruments(users, instruments);

    SeedBands bands = createBands();
    createBandMembers(users, bands);
    createBandRecruitments(users, bands, instruments);

    SeedSpaces spaces = createSpaces(users);
    createSchedules(spaces);
    createAvailabilityExceptions(spaces, dates);

    SeedEquipment equipment = createEquipment();
    createSpaceEquipment(spaces, equipment);

    SeedReservations reservations = createReservations(users, bands, spaces, dates);
    createMessages(reservations);
    createFavorites(users, spaces);
    createReviews(reservations, spaces);
    createUserReviews(reservations);

    SeedEvents events = createInternalEvents(users, bands, spaces, dates);
    createEventPurchases(users, events);
    createSearches(users, dates);

    int ticketmasterCreated = ticketmasterFeaturedEventSeedService.ensureFeaturedEvents(users.admin());
    repairEventTextEncoding();
    assertFinalUserCounts();

    logger.info(
      "DatabaseLoader loaded final seed: 14 users, 1 admin, {} Ticketmaster events available ({} newly created)",
      ticketmasterFeaturedEventSeedService.featuredEventCount(),
      ticketmasterCreated
    );
  }

  private SeedUsers createUsers() {
    User admin = createUser(
      "Admin",
      "FIOS",
      "Admin",
      "admin@fios.com",
      "699100000",
      LocalDate.of(1988, 1, 12),
      PlatformRole.ADMIN
    );

    User alba = createUser("Alba", "Moure", "Casal", "alba.moure@fios.com", "699100001", LocalDate.of(1994, 5, 7), PlatformRole.USER);
    User nico = createUser("Nico", "Falcon", "Rivas", "nico.falcon@fios.com", "699100002", LocalDate.of(1991, 9, 18), PlatformRole.USER);
    User carla = createUser("Carla", "Santos", "Leal", "carla.santos@fios.com", "699100003", LocalDate.of(1996, 2, 23), PlatformRole.USER);
    User diego = createUser("Diego", "Rios", "Varela", "diego.rios@fios.com", "699100004", LocalDate.of(1989, 11, 4), PlatformRole.USER);
    User irene = createUser("Irene", "Valverde", "Soto", "irene.valverde@fios.com", "699100005", LocalDate.of(1993, 7, 15), PlatformRole.USER);
    User martin = createUser("Martín", "Lago", "Paz", "martin.lago@fios.com", "699100006", LocalDate.of(1990, 4, 2), PlatformRole.USER);
    User sara = createUser("Sara", "Novas", "Barreiro", "sara.novas@fios.com", "699100007", LocalDate.of(1998, 8, 28), PlatformRole.USER);
    User hugo = createUser("Hugo", "Prieto", "Méndez", "hugo.prieto@fios.com", "699100008", LocalDate.of(1992, 12, 11), PlatformRole.USER);
    User lucia = createUser("Lucía", "Castro", "Ferro", "lucia.castro@fios.com", "699100009", LocalDate.of(1995, 3, 19), PlatformRole.USER);
    User pablo = createUser("Pablo", "Veiga", "Noya", "pablo.veiga@fios.com", "699100010", LocalDate.of(1997, 10, 6), PlatformRole.USER);
    User lucas = createUser("Lucas", "García", null, "lucas@fios.com", "699100011", LocalDate.of(1999, 6, 18), PlatformRole.USER, DEMO_PASSWORD);
    User xanma = createUser("Xanma", "Alonso", null, "xanma@fios.com", "699100012", LocalDate.of(1999, 6, 18), PlatformRole.USER, DEMO_PASSWORD);
    User samu = createUser("Samu", "López", null, "samu@fios.com", "699100013", LocalDate.of(1998, 3, 4), PlatformRole.USER, DEMO_PASSWORD);
    User mati = createUser("Mati", "Fernández", null, "mati@fios.com", "699100014", LocalDate.of(1997, 11, 9), PlatformRole.USER, DEMO_PASSWORD);
    User xaquin = createUser("Xaquín", "Rodríguez", null, "xaquin@fios.com", "699100015", LocalDate.of(1996, 1, 21), PlatformRole.USER, DEMO_PASSWORD);

    return new SeedUsers(admin, alba, nico, carla, diego, irene, martin, sara, hugo, lucia, pablo, lucas, xanma, samu, mati, xaquin);
  }

  private User createUser(
    String name,
    String firstSurname,
    String secondSurname,
    String email,
    String phone,
    LocalDate birthDate,
    PlatformRole platformRole
  ) {
    return createUser(name, firstSurname, secondSurname, email, phone, birthDate, platformRole, SEED_PASSWORD);
  }

  private User createUser(
    String name,
    String firstSurname,
    String secondSurname,
    String email,
    String phone,
    LocalDate birthDate,
    PlatformRole platformRole,
    String password
  ) {
    return persist(new User(
      name,
      firstSurname,
      secondSurname,
      email,
      passwordEncoder.encode(password),
      phone,
      null,
      birthDate,
      true,
      platformRole
    ));
  }

  private SeedInstruments createInstruments() {
    Instrument voice = instrument("Voz", InstrumentCategory.VOICE);
    Instrument electricGuitar = instrument("Guitarra eléctrica", InstrumentCategory.STRINGS);
    Instrument acousticGuitar = instrument("Guitarra acústica", InstrumentCategory.STRINGS);
    Instrument bass = instrument("Bajo eléctrico", InstrumentCategory.STRINGS);
    Instrument drums = instrument("Batería", InstrumentCategory.PERCUSSION);
    Instrument keyboard = instrument("Teclado", InstrumentCategory.KEYBOARD);
    Instrument sax = instrument("Saxofón", InstrumentCategory.WIND);
    Instrument trumpet = instrument("Trompeta", InstrumentCategory.BRASS);
    Instrument synthesizer = instrument("Sintetizadores", InstrumentCategory.ELECTRONIC);

    return new SeedInstruments(
      voice,
      electricGuitar,
      acousticGuitar,
      bass,
      drums,
      keyboard,
      sax,
      trumpet,
      synthesizer
    );
  }

  private Instrument instrument(String name, InstrumentCategory category) {
    return persist(new Instrument(name, category));
  }

  private void assignUserInstruments(SeedUsers users, SeedInstruments instruments) {
    assign(users.alba(), instruments.electricGuitar());
    assign(users.nico(), instruments.drums());
    assign(users.carla(), instruments.keyboard());
    assign(users.diego(), instruments.bass());
    assign(users.irene(), instruments.voice());
    assign(users.martin(), instruments.sax());
    assign(users.sara(), instruments.drums());
    assign(users.hugo(), instruments.bass());
    assign(users.lucia(), instruments.voice());
    assign(users.pablo(), instruments.synthesizer());
    assign(users.lucas(), instruments.electricGuitar());
    assign(users.xanma(), instruments.voice());
    assign(users.samu(), instruments.acousticGuitar());
    assign(users.mati(), instruments.bass());
    assign(users.xaquin(), instruments.drums());
  }

  private void assign(User user, Instrument... instruments) {
    user.getInstruments().clear();
    user.getInstruments().addAll(List.of(instruments));
  }

  private SeedBands createBands() {
    Band brumaNorte = band(
      "Bruma Norte",
      "Trío de indie rock melódico con guitarras limpias, bases sencillas y letras urbanas. Preparan un set de sala para conciertos de 45 minutos.",
      "Indie Rock",
      "A Coruña",
      unsplashPhoto("photo-1493225457124-a3eb161ffa5f")
    );

    Band latidoVerde = band(
      "Latido Verde",
      "Proyecto de funk y soul con sección rítmica compacta, teclados cálidos y voces principales compartidas.",
      "Funk Soul",
      "Santiago de Compostela",
      unsplashPhoto("photo-1516280440614-37939bbacd81")
    );

    Band riaElectrica = band(
      "Ría Eléctrica",
      "Dúo ampliable de synth pop y electrónica suave, centrado en arreglos con secuencias y voces dobladas.",
      "Synth Pop",
      "Vigo",
      unsplashPhoto("photo-1470225620780-dba8ba36b745")
    );

    Band pedraAzul = band(
      "Pedra Azul Quartet",
      "Cuarteto instrumental de jazz fusión con saxo, bajo, batería y teclados. Trabajan repertorio propio para salas pequeñas.",
      "Jazz Fusion",
      "Pontevedra",
      unsplashPhoto("photo-1511379938547-c1f69419868d")
    );

    Band theRapants = band(
      "The Rapants",
      "Banda gallega de indie rock formada por Lucas, Xanma, Samu, Mati y Xaquín.",
      "Indie Rock",
      "A Coruña",
      "/images/the-rapants.jpg"
    );

    return new SeedBands(brumaNorte, latidoVerde, riaElectrica, pedraAzul, theRapants);
  }

  private Band band(String name, String description, String mainGenre, String baseCity, String image) {
    return persist(new Band(name, description, mainGenre, baseCity, image));
  }

  private void createBandMembers(SeedUsers users, SeedBands bands) {
    bandMember(bands.brumaNorte(), users.alba(), BandMemberRole.LEADER);
    bandMember(bands.brumaNorte(), users.diego(), BandMemberRole.MEMBER);
    bandMember(bands.brumaNorte(), users.lucia(), BandMemberRole.MEMBER);

    bandMember(bands.latidoVerde(), users.nico(), BandMemberRole.LEADER);
    bandMember(bands.latidoVerde(), users.carla(), BandMemberRole.MEMBER);
    bandMember(bands.latidoVerde(), users.sara(), BandMemberRole.MEMBER);
    bandMember(bands.latidoVerde(), users.hugo(), BandMemberRole.MEMBER);

    bandMember(bands.riaElectrica(), users.irene(), BandMemberRole.LEADER);
    bandMember(bands.riaElectrica(), users.pablo(), BandMemberRole.MEMBER);
    bandMember(bands.riaElectrica(), users.carla(), BandMemberRole.MEMBER);

    bandMember(bands.pedraAzul(), users.martin(), BandMemberRole.LEADER);
    bandMember(bands.pedraAzul(), users.sara(), BandMemberRole.MEMBER);
    bandMember(bands.pedraAzul(), users.diego(), BandMemberRole.MEMBER);
    bandMember(bands.pedraAzul(), users.hugo(), BandMemberRole.MEMBER);

    bandMember(bands.theRapants(), users.lucas(), BandMemberRole.LEADER);
    bandMember(bands.theRapants(), users.xanma(), BandMemberRole.MEMBER);
    bandMember(bands.theRapants(), users.samu(), BandMemberRole.MEMBER);
    bandMember(bands.theRapants(), users.mati(), BandMemberRole.MEMBER);
    bandMember(bands.theRapants(), users.xaquin(), BandMemberRole.MEMBER);
  }

  private void bandMember(Band band, User user, BandMemberRole role) {
    persist(new BandMember(band, user, role));
  }

  private void createBandRecruitments(SeedUsers users, SeedBands bands, SeedInstruments instruments) {
    recruitment(
      "Guitarra rítmica para directo",
      "Buscamos una guitarra con disponibilidad para ensayar entre semana y preparar repertorio propio antes de octubre.",
      "Guitarra rítmica",
      BandRecruitmentLevel.INTERMEDIATE,
      "A Coruña",
      1,
      bands.brumaNorte(),
      instruments.electricGuitar(),
      users.alba(),
      true
    );

    recruitment(
      "Corista con experiencia en soul",
      "Latido Verde necesita una voz de apoyo para armonías y pequeños arreglos en directos de sala.",
      "Corista",
      BandRecruitmentLevel.INTERMEDIATE,
      "Santiago de Compostela",
      2,
      bands.latidoVerde(),
      instruments.voice(),
      users.nico(),
      true
    );

    recruitment(
      "Batería para set electrónico",
      "Queremos incorporar batería híbrida para llevar Ría Eléctrica a formato de directo.",
      "Batería híbrida",
      BandRecruitmentLevel.ADVANCED,
      "Vigo",
      1,
      bands.riaElectrica(),
      instruments.drums(),
      users.irene(),
      true
    );

    recruitment(
      "Trompeta para temas nuevos",
      "Buscamos viento para arreglos de jazz fusión y sesiones de composición quincenales.",
      "Trompeta",
      BandRecruitmentLevel.ADVANCED,
      "Pontevedra",
      1,
      bands.pedraAzul(),
      instruments.trumpet(),
      users.martin(),
      true
    );

    recruitment(
      "Teclista suplente para showcases",
      "Oferta cerrada de prueba para mantener historial visible en administración.",
      "Teclista",
      BandRecruitmentLevel.PROFESSIONAL,
      "Santiago de Compostela",
      1,
      bands.latidoVerde(),
      instruments.keyboard(),
      users.nico(),
      false
    );

    recruitment(
      "Teclista de apoyo para The Rapants",
      "The Rapants busca apoyo de teclados para directos y arreglos de sintetizador.",
      "Teclista",
      BandRecruitmentLevel.INTERMEDIATE,
      "A Coruna",
      1,
      bands.theRapants(),
      instruments.keyboard(),
      users.lucas(),
      true
    );
  }

  private void recruitment(
    String title,
    String description,
    String roleWanted,
    BandRecruitmentLevel levelRequired,
    String city,
    int vacancies,
    Band band,
    Instrument instrument,
    User publishedBy,
    boolean open
  ) {
    BandRecruitment recruitment = new BandRecruitment(
      title,
      description,
      roleWanted,
      levelRequired,
      city,
      vacancies,
      band,
      instrument,
      publishedBy
    );

    if (!open) {
      recruitment.close();
    }

    persist(recruitment);
  }

  private SeedSpaces createSpaces(SeedUsers users) {
    MusicalSpace corunaStudio = approvedSpace(
      "Estudio Noroeste",
      "Estudio de grabación con sala de control independiente, cabina vocal y backline básico para maquetas profesionales.",
      MusicalSpaceType.RECORDING_STUDIO,
      8,
      62.0,
      true,
      location("A Coruña", "A Coruña", "Rúa Nova", "14", "2", "15003", 43.3709, -8.3958),
      users.alba(),
      unsplashPhoto("photo-1511379938547-c1f69419868d")
    );

    MusicalSpace portoAlto = approvedSpace(
      "Sala Porto Alto",
      "Sala de ensayo amplia con batería, amplificadores y acceso cómodo para descargar equipo.",
      MusicalSpaceType.REHEARSAL_ROOM,
      6,
      38.0,
      true,
      location("A Coruña", "A Coruña", "Avenida do Porto", "22", null, "15006", 43.3623, -8.4071),
      users.nico(),
      unsplashPhoto("photo-1524368535928-5b5e00ddc76b")
    );

    MusicalSpace santiagoRoom = approvedSpace(
      "Ensayo Matinal Santiago",
      "Local luminoso para ensayos de mañana, clases de instrumento y sesiones acústicas en grupos pequeños.",
      MusicalSpaceType.REHEARSAL_ROOM,
      5,
      31.5,
      true,
      location("A Coruña", "Santiago de Compostela", "Rúa de San Pedro", "9", "B", "15703", 42.8811, -8.5387),
      users.carla(),
      unsplashPhoto("photo-1507874457470-272b3c8d8ee2")
    );

    MusicalSpace lugoClassroom = approvedSpace(
      "Aula Sonora Lugo",
      "Aula equipada para clases, combos y pequeños talleres con piano digital y sistema de voces.",
      MusicalSpaceType.CLASSROOM,
      12,
      44.0,
      false,
      location("Lugo", "Lugo", "Rúa das Artes", "5", "1", "27002", 43.0097, -7.5560),
      users.diego(),
      unsplashPhoto("photo-1511671782779-c97d3d27a1d4")
    );

    MusicalSpace lerezLive = approvedSpace(
      "Nave Lérez Live",
      "Espacio de preparación de directos con pequeño escenario, luces básicas y zona para público reducido.",
      MusicalSpaceType.CONCERT_HALL,
      80,
      145.0,
      true,
      location("Pontevedra", "Pontevedra", "Rúa Lérez", "31", null, "36005", 42.4336, -8.6481),
      users.irene(),
      unsplashPhoto("photo-1501386761578-eac5c94b800a")
    );

    MusicalSpace vigoLab = approvedSpace(
      "Taller Ría Eléctrica",
      "Espacio polivalente para ensayos con sintes, pruebas de directo y pequeños workshops de producción.",
      MusicalSpaceType.MULTIPURPOSE,
      10,
      58.0,
      true,
      location("Pontevedra", "Vigo", "Rúa do Príncipe", "40", "3", "36202", 42.2406, -8.7207),
      users.martin(),
      unsplashPhoto("photo-1470225620780-dba8ba36b745")
    );

    MusicalSpace ourenseBooth = approvedSpace(
      "Cabina Brava Ourense",
      "Cabina compacta para voces, podcasts musicales, doblajes y overdubs rápidos con técnico bajo reserva.",
      MusicalSpaceType.RECORDING_STUDIO,
      4,
      24.0,
      true,
      location("Ourense", "Ourense", "Rúa do Paseo", "18", "1", "32003", 42.3368, -7.8639),
      users.sara(),
      unsplashPhoto("photo-1499364615650-ec38552f4f34")
    );

    MusicalSpace ferrolSpace = approvedSpace(
      "Espacio Marisma Ferrol",
      "Local flexible para bandas, lecturas musicales y sesiones de arreglos con capacidad media.",
      MusicalSpaceType.MULTIPURPOSE,
      18,
      73.0,
      false,
      location("A Coruña", "Ferrol", "Rúa Real", "72", null, "15402", 43.4832, -8.2369),
      users.hugo(),
      unsplashPhoto("photo-1516280440614-37939bbacd81")
    );

    MusicalSpace boa = approvedSpace(
      "Boa",
      "Local de ensayo en Carballo preparado para bandas que necesitan entrar, enchufar y trabajar repertorio con comodidad. Dispone de batería, amplificadores de guitarra y bajo, PA con monitores y tratamiento acústico en paredes, con espacio insonorizado para ensayos de hasta siete personas, preproducción y pequeños showcases.",
      MusicalSpaceType.REHEARSAL_ROOM,
      7,
      46.0,
      true,
      location("A Coruña", "Carballo", "Rúa Barcelona", "30", null, "15102", 43.2120122, -8.6925470),
      users.lucas(),
      "/images/boa-local-de-ensaio.jpg"
    );

    return new SeedSpaces(corunaStudio, portoAlto, santiagoRoom, lugoClassroom, lerezLive, vigoLab, ourenseBooth, ferrolSpace, boa);
  }

  private MusicalSpace approvedSpace(
    String name,
    String description,
    MusicalSpaceType spaceType,
    int capacity,
    double squareMeters,
    boolean soundproofed,
    MusicalSpaceLocation location,
    User manager,
    String mainImage
  ) {
    MusicalSpace space = new MusicalSpace(
      name,
      description,
      spaceType,
      capacity,
      mainImage,
      squareMeters,
      soundproofed,
      location,
      manager
    );
    space.setApprovalStatus(MusicalSpaceApprovalStatus.APPROVED);
    return persist(space);
  }

  private MusicalSpaceLocation location(
    String province,
    String city,
    String street,
    String portal,
    String floor,
    String postalCode,
    Double latitude,
    Double longitude
  ) {
    return new MusicalSpaceLocation("España", province, city, street, portal, floor, postalCode, latitude, longitude);
  }

  private void createSchedules(SeedSpaces spaces) {
    weekdaySlots(spaces.corunaStudio(), "10:00", "14:00", 34);
    weekdaySlots(spaces.corunaStudio(), "16:00", "22:00", 42);
    saturdaySlot(spaces.corunaStudio(), "10:00", "14:00", 46);

    weekdaySlots(spaces.portoAlto(), "17:00", "23:00", 24);
    saturdaySlot(spaces.portoAlto(), "11:00", "15:00", 28);

    weekdaySlots(spaces.santiagoRoom(), "09:00", "13:00", 18);
    weekdaySlots(spaces.santiagoRoom(), "16:00", "20:00", 21);

    weekdaySlots(spaces.lugoClassroom(), "10:00", "14:00", 16);
    weekdaySlots(spaces.lugoClassroom(), "17:00", "21:00", 20);

    weekdaySlots(spaces.lerezLive(), "18:00", "23:00", 78);
    saturdaySlot(spaces.lerezLive(), "17:00", "23:30", 92);

    weekdaySlots(spaces.vigoLab(), "11:00", "15:00", 27);
    weekdaySlots(spaces.vigoLab(), "17:00", "22:00", 32);

    weekdaySlots(spaces.ourenseBooth(), "10:00", "14:00", 26);
    weekdaySlots(spaces.ourenseBooth(), "16:00", "20:00", 30);

    weekdaySlots(spaces.ferrolSpace(), "16:00", "22:00", 35);
    saturdaySlot(spaces.ferrolSpace(), "10:00", "14:00", 38);

    weekdaySlots(spaces.boa(), "16:00", "22:00", 28);
    saturdaySlot(spaces.boa(), "10:00", "14:00", 32);
  }

  private void weekdaySlots(MusicalSpace space, String startTime, String endTime, int price) {
    for (DayOfWeek day : List.of(
      DayOfWeek.MONDAY,
      DayOfWeek.TUESDAY,
      DayOfWeek.WEDNESDAY,
      DayOfWeek.THURSDAY,
      DayOfWeek.FRIDAY
    )) {
      schedule(space, day, startTime, endTime, price);
    }
  }

  private void saturdaySlot(MusicalSpace space, String startTime, String endTime, int price) {
    schedule(space, DayOfWeek.SATURDAY, startTime, endTime, price);
  }

  private void schedule(MusicalSpace space, DayOfWeek day, String startTime, String endTime, int price) {
    persist(new Schedule(day, LocalTime.parse(startTime), LocalTime.parse(endTime), money(price), space));
  }

  private void createAvailabilityExceptions(SeedSpaces spaces, SeedDates dates) {
    exception(spaces.corunaStudio(), dates.futureTwo(), "16:00", "19:00", SpaceAvailabilityExceptionType.BLOCKED, "Mantenimiento de previo y revision de cableado", null);
    exception(spaces.portoAlto(), dates.futureThree(), "20:00", "23:00", SpaceAvailabilityExceptionType.BLOCKED, "Reserva técnica para ajuste de batería", null);
    exception(spaces.santiagoRoom(), dates.futureOne(), "13:00", "16:00", SpaceAvailabilityExceptionType.CUSTOM_AVAILABILITY, "Apertura extra para clase intensiva", 24);
    exception(spaces.lerezLive(), dates.futureSaturday(), "12:00", "16:00", SpaceAvailabilityExceptionType.CUSTOM_AVAILABILITY, "Prueba de sonido extraordinaria", 70);
    exception(spaces.vigoLab(), dates.futureFour(), "15:00", "17:00", SpaceAvailabilityExceptionType.CUSTOM_AVAILABILITY, "Sesion corta de sintetizadores", 29);
    exception(spaces.boa(), dates.futureFour(), "12:00", "15:00", SpaceAvailabilityExceptionType.CUSTOM_AVAILABILITY, "Apertura demo para visita guiada", 30);
    exception(spaces.ourenseBooth(), dates.futureFive(), "10:00", "12:00", SpaceAvailabilityExceptionType.BLOCKED, "Limpieza acústica programada", null);
  }

  private void exception(
    MusicalSpace space,
    LocalDate date,
    String startTime,
    String endTime,
    SpaceAvailabilityExceptionType type,
    String reason,
    Integer price
  ) {
    persist(new SpaceAvailabilityException(
      date,
      LocalTime.parse(startTime),
      LocalTime.parse(endTime),
      type,
      reason,
      price == null ? null : money(price),
      space
    ));
  }

  private SeedEquipment createEquipment() {
    Equipment amplifiers = equipment("Amplificadores de guitarra", EquipmentCategory.SOUND, "Dos combos de 50W y un cabezal con pantalla 2x12.");
    Equipment mixer = equipment("Mesa de mezclas 16 canales", EquipmentCategory.SOUND, "Mesa digital con escenas guardadas para ensayo y directo.");
    Equipment microphones = equipment("Kit de micrófonos dinámicos", EquipmentCategory.RECORDING, "Seis micrófonos para voz, batería y amplificadores.");
    Equipment acousticDrums = equipment("Batería acústica completa", EquipmentCategory.INSTRUMENT, "Batería de cinco cuerpos con herrajes y platos básicos.");
    Equipment digitalPiano = equipment("Piano digital 88 teclas", EquipmentCategory.INSTRUMENT, "Teclado contrapesado para clases, composición y directo.");
    Equipment monitors = equipment("Monitores de suelo", EquipmentCategory.SOUND, "Cuatro monitores activos para ensayo y showcase.");
    Equipment interfaceAudio = equipment("Interfaz de audio 8 entradas", EquipmentCategory.RECORDING, "Interfaz USB para maquetas y grabación multipista.");
    Equipment lights = equipment("Luces LED de escenario", EquipmentCategory.LIGHTING, "Barras LED regulables para pequeños directos.");
    Equipment micStands = equipment("Pies de micro", EquipmentCategory.ACCESSORY, "Pies rectos y de jirafa para ensayo y grabación.");

    return new SeedEquipment(amplifiers, mixer, microphones, acousticDrums, digitalPiano, monitors, interfaceAudio, lights, micStands);
  }

  private Equipment equipment(String name, EquipmentCategory category, String description) {
    return persist(new Equipment(name, category, description));
  }

  private void createSpaceEquipment(SeedSpaces spaces, SeedEquipment equipment) {
    spaceEquipment(spaces.corunaStudio(), equipment.mixer(), 1, SpaceEquipmentState.AVAILABLE, "Configurada con plantillas de grabación.");
    spaceEquipment(spaces.corunaStudio(), equipment.microphones(), 8, SpaceEquipmentState.AVAILABLE, "Incluye antivientos y pinzas.");
    spaceEquipment(spaces.corunaStudio(), equipment.interfaceAudio(), 1, SpaceEquipmentState.AVAILABLE, null);
    spaceEquipment(spaces.corunaStudio(), equipment.micStands(), 6, SpaceEquipmentState.AVAILABLE, null);

    spaceEquipment(spaces.portoAlto(), equipment.amplifiers(), 3, SpaceEquipmentState.AVAILABLE, "Un combo tiene pedalera compartida.");
    spaceEquipment(spaces.portoAlto(), equipment.acousticDrums(), 1, SpaceEquipmentState.LIMITED, "Caja propia recomendada.");
    spaceEquipment(spaces.portoAlto(), equipment.microphones(), 3, SpaceEquipmentState.AVAILABLE, null);

    spaceEquipment(spaces.santiagoRoom(), equipment.digitalPiano(), 1, SpaceEquipmentState.AVAILABLE, null);
    spaceEquipment(spaces.santiagoRoom(), equipment.microphones(), 2, SpaceEquipmentState.AVAILABLE, null);

    spaceEquipment(spaces.lugoClassroom(), equipment.digitalPiano(), 1, SpaceEquipmentState.AVAILABLE, "Uso prioritario para clases.");
    spaceEquipment(spaces.lugoClassroom(), equipment.micStands(), 4, SpaceEquipmentState.AVAILABLE, null);

    spaceEquipment(spaces.lerezLive(), equipment.mixer(), 1, SpaceEquipmentState.AVAILABLE, null);
    spaceEquipment(spaces.lerezLive(), equipment.monitors(), 4, SpaceEquipmentState.AVAILABLE, null);
    spaceEquipment(spaces.lerezLive(), equipment.lights(), 6, SpaceEquipmentState.AVAILABLE, "Escenas basicas preconfiguradas.");
    spaceEquipment(spaces.lerezLive(), equipment.microphones(), 6, SpaceEquipmentState.AVAILABLE, null);

    spaceEquipment(spaces.vigoLab(), equipment.interfaceAudio(), 1, SpaceEquipmentState.AVAILABLE, null);
    spaceEquipment(spaces.vigoLab(), equipment.digitalPiano(), 1, SpaceEquipmentState.LIMITED, "Reservar si se necesita soporte MIDI.");
    spaceEquipment(spaces.vigoLab(), equipment.monitors(), 2, SpaceEquipmentState.AVAILABLE, null);

    spaceEquipment(spaces.ourenseBooth(), equipment.microphones(), 4, SpaceEquipmentState.AVAILABLE, null);
    spaceEquipment(spaces.ourenseBooth(), equipment.interfaceAudio(), 1, SpaceEquipmentState.AVAILABLE, null);

    spaceEquipment(spaces.ferrolSpace(), equipment.amplifiers(), 2, SpaceEquipmentState.AVAILABLE, null);
    spaceEquipment(spaces.ferrolSpace(), equipment.mixer(), 1, SpaceEquipmentState.LIMITED, "Disponible con responsable del espacio.");
    spaceEquipment(spaces.ferrolSpace(), equipment.micStands(), 5, SpaceEquipmentState.AVAILABLE, null);

    spaceEquipment(spaces.boa(), equipment.amplifiers(), 2, SpaceEquipmentState.AVAILABLE, "Configurados para guitarra y bajo.");
    spaceEquipment(spaces.boa(), equipment.acousticDrums(), 1, SpaceEquipmentState.AVAILABLE, null);
    spaceEquipment(spaces.boa(), equipment.microphones(), 4, SpaceEquipmentState.AVAILABLE, null);
    spaceEquipment(spaces.boa(), equipment.monitors(), 2, SpaceEquipmentState.AVAILABLE, "Monitores preparados para showcase demo.");
    spaceEquipment(spaces.boa(), equipment.micStands(), 4, SpaceEquipmentState.AVAILABLE, null);
  }

  private void spaceEquipment(
    MusicalSpace space,
    Equipment equipment,
    int quantity,
    SpaceEquipmentState state,
    String observations
  ) {
    persist(new SpaceEquipment(quantity, state, observations, space, equipment));
  }

  private SeedReservations createReservations(SeedUsers users, SeedBands bands, SeedSpaces spaces, SeedDates dates) {
    ReservationSession completedStudio = reservation(
      dates.pastMonth(),
      "10:00",
      "12:00",
      3,
      ReservationSessionType.RECORDING,
      "Grabación de voces para una maqueta de Bruma Norte.",
      84,
      ReservationSessionState.COMPLETED,
      null,
      spaces.corunaStudio(),
      users.lucia(),
      bands.brumaNorte()
    );

    ReservationSession completedSantiago = reservation(
      dates.pastThreeWeeks(),
      "17:00",
      "19:00",
      4,
      ReservationSessionType.REHEARSAL,
      "Ensayo de groove y armonías para Latido Verde.",
      42,
      ReservationSessionState.COMPLETED,
      null,
      spaces.santiagoRoom(),
      users.hugo(),
      bands.latidoVerde()
    );

    ReservationSession completedLerez = reservation(
      dates.pastTwoWeeks(),
      "18:00",
      "21:00",
      6,
      ReservationSessionType.EVENT_PREPARATION,
      "Prueba de escenario para Pedra Azul Quartet.",
      234,
      ReservationSessionState.COMPLETED,
      null,
      spaces.lerezLive(),
      users.sara(),
      bands.pedraAzul()
    );

    ReservationSession completedLugo = reservation(
      dates.pastWeek(),
      "10:00",
      "12:00",
      2,
      ReservationSessionType.CLASS,
      "Clase de arreglos y lectura rítmica.",
      32,
      ReservationSessionState.COMPLETED,
      null,
      spaces.lugoClassroom(),
      users.pablo(),
      null
    );

    ReservationSession completedPortoPendingReviews = reservation(
      dates.pastWeek().minusDays(2),
      "18:00",
      "20:00",
      4,
      ReservationSessionType.REHEARSAL,
      "Ensayo cerrado para preparar repertorio nuevo de Bruma Norte.",
      76,
      ReservationSessionState.COMPLETED,
      null,
      spaces.portoAlto(),
      users.alba(),
      bands.brumaNorte()
    );

    ReservationSession completedVigoReviewed = reservation(
      dates.pastThreeWeeks().minusDays(2),
      "17:00",
      "19:00",
      3,
      ReservationSessionType.REHEARSAL,
      "Sesión de pruebas con sintes, bajo y voces para directo.",
      72,
      ReservationSessionState.COMPLETED,
      null,
      spaces.vigoLab(),
      users.irene(),
      bands.riaElectrica()
    );

    ReservationSession completedOurenseReviewed = reservation(
      dates.pastTwoWeeks().minusDays(1),
      "16:00",
      "18:00",
      2,
      ReservationSessionType.RECORDING,
      "Grabación de coros y capas de sintetizador para maqueta.",
      60,
      ReservationSessionState.COMPLETED,
      null,
      spaces.ourenseBooth(),
      users.carla(),
      bands.latidoVerde()
    );

    ReservationSession completedLucasAsMusician = reservation(
      dates.pastWeek().minusDays(1),
      "18:00",
      "20:00",
      5,
      ReservationSessionType.REHEARSAL,
      "Ensayo general de The Rapants antes de grabar nuevas maquetas.",
      48,
      ReservationSessionState.COMPLETED,
      null,
      spaces.portoAlto(),
      users.lucas(),
      bands.theRapants()
    );

    ReservationSession completedLucasAsManager = reservation(
      dates.pastWeek().minusDays(3),
      "17:00",
      "19:00",
      3,
      ReservationSessionType.REHEARSAL,
      "Ensayo de Bruma Norte en Boa.",
      56,
      ReservationSessionState.COMPLETED,
      null,
      spaces.boa(),
      users.lucia(),
      bands.brumaNorte()
    );

    ReservationSession pendingLucas = reservation(
      dates.futureOne(),
      "18:00",
      "20:00",
      5,
      ReservationSessionType.REHEARSAL,
      "Solicitud de The Rapants para probar repertorio con guitarras.",
      84,
      ReservationSessionState.PENDING,
      null,
      spaces.corunaStudio(),
      users.lucas(),
      bands.theRapants()
    );

    ReservationSession acceptedLucas = reservation(
      dates.futureTwo(),
      "17:00",
      "19:00",
      5,
      ReservationSessionType.REHEARSAL,
      "Preproduccion de The Rapants con sintes y bajo.",
      64,
      ReservationSessionState.ACCEPTED,
      null,
      spaces.vigoLab(),
      users.lucas(),
      bands.theRapants()
    );

    ReservationSession pendingLucasRoom = reservation(
      dates.futureThree(),
      "18:00",
      "20:00",
      3,
      ReservationSessionType.REHEARSAL,
      "Ensayo de Ria Electrica solicitado en Boa.",
      56,
      ReservationSessionState.PENDING,
      null,
      spaces.boa(),
      users.irene(),
      bands.riaElectrica()
    );

    ReservationSession pendingPorto = reservation(
      dates.futureOne(),
      "17:00",
      "19:00",
      3,
      ReservationSessionType.REHEARSAL,
      "Ensayo de Ría Eléctrica con formato trío.",
      48,
      ReservationSessionState.PENDING,
      null,
      spaces.portoAlto(),
      users.irene(),
      bands.riaElectrica()
    );

    ReservationSession pendingOurense = reservation(
      dates.futureTwo(),
      "16:00",
      "18:00",
      1,
      ReservationSessionType.RECORDING,
      "Overdubs de voz y sintetizador.",
      60,
      ReservationSessionState.PENDING,
      null,
      spaces.ourenseBooth(),
      users.carla(),
      bands.riaElectrica()
    );

    ReservationSession acceptedVigo = reservation(
      dates.futureThree(),
      "17:00",
      "20:00",
      4,
      ReservationSessionType.REHEARSAL,
      "Preproducción de repertorio nuevo.",
      96,
      ReservationSessionState.ACCEPTED,
      null,
      spaces.vigoLab(),
      users.alba(),
      bands.brumaNorte()
    );

    ReservationSession acceptedFerrol = reservation(
      dates.futureFour(),
      "16:00",
      "19:00",
      5,
      ReservationSessionType.OTHER,
      "Trabajo de arreglos para sección de vientos.",
      105,
      ReservationSessionState.ACCEPTED,
      null,
      spaces.ferrolSpace(),
      users.diego(),
      bands.pedraAzul()
    );

    ReservationSession rejectedStudio = reservation(
      dates.futureFive(),
      "10:00",
      "13:00",
      4,
      ReservationSessionType.RECORDING,
      "Solicitud inicial para batería en directo.",
      126,
      ReservationSessionState.REJECTED,
      "No hay técnico disponible para esa franja.",
      spaces.corunaStudio(),
      users.martin(),
      bands.pedraAzul()
    );

    ReservationSession cancelledPorto = reservation(
      dates.futureSaturday(),
      "11:00",
      "14:00",
      3,
      ReservationSessionType.REHEARSAL,
      "Ensayo cancelado por cambio de agenda del grupo.",
      84,
      ReservationSessionState.CANCELLED,
      "La banda movió el ensayo a la semana siguiente.",
      spaces.portoAlto(),
      users.lucia(),
      bands.brumaNorte()
    );

    return new SeedReservations(
      completedStudio,
      completedSantiago,
      completedLerez,
      completedLugo,
      completedPortoPendingReviews,
      completedVigoReviewed,
      completedOurenseReviewed,
      completedLucasAsMusician,
      completedLucasAsManager,
      pendingLucas,
      acceptedLucas,
      pendingLucasRoom,
      pendingPorto,
      pendingOurense,
      acceptedVigo,
      acceptedFerrol,
      rejectedStudio,
      cancelledPorto
    );
  }

  private ReservationSession reservation(
    LocalDate date,
    String startTime,
    String endTime,
    int attendeesCount,
    ReservationSessionType sessionType,
    String observations,
    int finalPrice,
    ReservationSessionState state,
    String cancellationReason,
    MusicalSpace space,
    User user,
    Band band
  ) {
    ReservationSession reservation = new ReservationSession(
      date,
      LocalTime.parse(startTime),
      LocalTime.parse(endTime),
      attendeesCount,
      sessionType,
      observations,
      money(finalPrice),
      space,
      user,
      band
    );
    reservation.setState(state);
    reservation.setCancellationReason(cancellationReason);
    return persist(reservation);
  }

  private void createMessages(SeedReservations reservations) {
    message(reservations.pendingPorto(), reservations.pendingPorto().getUser(), "Hola, iríamos con teclado y dos voces. Confirmamos si hay tres pies de micro disponibles.", true);
    message(reservations.pendingPorto(), reservations.pendingPorto().getMusicalSpace().getManager(), "Sí, hay tres pies libres. Os confirmo la reserva en cuanto revise la sala.", false);

    message(reservations.acceptedVigo(), reservations.acceptedVigo().getUser(), "Necesitamos conectar dos sintes y una pedalera. Llevamos nuestros cables largos.", true);
    message(reservations.acceptedVigo(), reservations.acceptedVigo().getMusicalSpace().getManager(), "Perfecto, dejo preparada la interfaz y dos monitores.", true);
    message(reservations.acceptedVigo(), reservations.acceptedVigo().getUser(), "Gracias, llegaremos quince minutos antes.", false);

    message(reservations.acceptedFerrol(), reservations.acceptedFerrol().getUser(), "La sección de viento puede montar en semicírculo?", true);
    message(reservations.acceptedFerrol(), reservations.acceptedFerrol().getMusicalSpace().getManager(), "Sí, aparto las mesas y os dejo cinco atriles.", false);

    message(reservations.rejectedStudio(), reservations.rejectedStudio().getMusicalSpace().getManager(), "Lo siento, esa franja no tiene técnico disponible para batería.", true);
    message(reservations.cancelledPorto(), reservations.cancelledPorto().getUser(), "Cancelamos esta sesión y la volveremos a pedir para el próximo sábado.", true);
    message(reservations.pendingLucas(), reservations.pendingLucas().getUser(), "Hola, somos The Rapants y queremos probar el repertorio con dos guitarras.", true);
    message(reservations.pendingLucas(), reservations.pendingLucas().getMusicalSpace().getManager(), "Perfecto, reviso la sala y te confirmo si queda tecnico disponible.", false);

    message(reservations.acceptedLucas(), reservations.acceptedLucas().getUser(), "Llevaremos pedalera, bajo y un sintetizador pequeno para la preproduccion.", true);
    message(reservations.acceptedLucas(), reservations.acceptedLucas().getMusicalSpace().getManager(), "Reserva aceptada. Dejo preparada la interfaz y dos monitores.", false);

    message(reservations.pendingLucasRoom(), reservations.pendingLucasRoom().getUser(), "Hola Lucas, nos interesa Boa para ensayar el set de Ria Electrica.", false);
    message(reservations.pendingLucasRoom(), reservations.pendingLucasRoom().getMusicalSpace().getManager(), "Genial, reviso el calendario y os confirmo esta tarde.", true);
  }

  private void message(ReservationSession reservation, User user, String content, boolean read) {
    Message message = new Message(content, reservation, user);
    if (read) {
      message.markAsRead();
    }
    persist(message);
  }

  private void createFavorites(SeedUsers users, SeedSpaces spaces) {
    favorite(users.alba(), spaces.portoAlto());
    favorite(users.alba(), spaces.vigoLab());
    favorite(users.nico(), spaces.corunaStudio());
    favorite(users.nico(), spaces.lerezLive());
    favorite(users.carla(), spaces.ourenseBooth());
    favorite(users.carla(), spaces.ferrolSpace());
    favorite(users.diego(), spaces.santiagoRoom());
    favorite(users.irene(), spaces.lerezLive());
    favorite(users.martin(), spaces.corunaStudio());
    favorite(users.sara(), spaces.lugoClassroom());
    favorite(users.hugo(), spaces.vigoLab());
    favorite(users.lucia(), spaces.portoAlto());
    favorite(users.pablo(), spaces.ourenseBooth());
    favorite(users.lucas(), spaces.portoAlto());
    favorite(users.lucas(), spaces.corunaStudio());
    favorite(users.lucas(), spaces.lerezLive());
  }

  private void favorite(User user, MusicalSpace space) {
    persist(new FavoriteSpace(user, space));
  }

  private void createReviews(SeedReservations reservations, SeedSpaces spaces) {
    review(
      "Grabamos voces con mucha calma y la cabina respondió muy bien. El flujo con la sala de control fue cómodo.",
      5,
      5,
      5,
      4,
      5,
      spaces.corunaStudio(),
      reservations.completedStudio().getUser(),
      reservations.completedStudio()
    );

    review(
      "Buen local para ensayar por la tarde. El piano digital está cuidado y la sala no molesta al vecindario.",
      4,
      5,
      4,
      4,
      5,
      spaces.santiagoRoom(),
      reservations.completedSantiago().getUser(),
      reservations.completedSantiago()
    );

    review(
      "La nave da margen para montar el directo completo. Los monitores ayudaron mucho en la prueba.",
      5,
      4,
      5,
      5,
      4,
      spaces.lerezLive(),
      reservations.completedLerez().getUser(),
      reservations.completedLerez()
    );

    review(
      "Aula tranquila y fácil de reservar. Perfecta para clases de armonía y trabajo en pareja.",
      4,
      5,
      4,
      4,
      4,
      spaces.lugoClassroom(),
      reservations.completedLugo().getUser(),
      reservations.completedLugo()
    );

    review(
      "El taller de Vigo funciona muy bien para ensayos con electrónica. Los monitores estaban bien colocados y todo quedó listo al llegar.",
      5,
      4,
      5,
      5,
      4,
      spaces.vigoLab(),
      reservations.completedVigoReviewed().getUser(),
      reservations.completedVigoReviewed()
    );

    review(
      "La cabina de Ourense es cómoda para voces y overdubs. Buena comunicación con la responsable y material preparado.",
      4,
      5,
      4,
      4,
      5,
      spaces.ourenseBooth(),
      reservations.completedOurenseReviewed().getUser(),
      reservations.completedOurenseReviewed()
    );
    review(
      "La sala estaba preparada al llegar, con los amplificadores listos y una comunicación previa muy clara para organizar el ensayo.",
      5,
      5,
      5,
      4,
      5,
      spaces.portoAlto(),
      reservations.completedLucasAsMusician().getUser(),
      reservations.completedLucasAsMusician()
    );

    review(
      "Boa resultó cómoda para un ensayo pequeño: entrada sencilla, equipo listo y comunicación ágil con la persona responsable.",
      5,
      4,
      5,
      5,
      4,
      spaces.boa(),
      reservations.completedLucasAsManager().getUser(),
      reservations.completedLucasAsManager()
    );
  }

  private void review(
    String comment,
    int overall,
    int cleanliness,
    int soundQuality,
    int equipment,
    int location,
    MusicalSpace space,
    User user,
    ReservationSession reservation
  ) {
    persist(new SpaceReview(comment, overall, cleanliness, soundQuality, equipment, location, space, user, reservation));
  }

  private void createUserReviews(SeedReservations reservations) {
    userReview(
      "Lucía dejó todo recogido, avisó de sus necesidades antes de llegar y respetó muy bien los tiempos de grabación.",
      5,
      5,
      5,
      5,
      reservations.completedStudio()
    );

    userReview(
      "Sara coordinó la prueba con claridad, llegó puntual y cuidó el montaje de escenario durante toda la sesión.",
      5,
      5,
      5,
      4,
      reservations.completedLerez()
    );

    userReview(
      "Irene comunicó los cambios de formato con antelación y dejó el taller ordenado después del ensayo.",
      4,
      5,
      4,
      5,
      reservations.completedVigoReviewed()
    );

    userReview(
      "Carla trabajó con mucha calma en cabina, cumplió los horarios y cuidó el material de grabación.",
      5,
      4,
      5,
      5,
      reservations.completedOurenseReviewed()
    );
    userReview(
      "Lucas llegó puntual, explicó con claridad las necesidades de The Rapants y dejó la sala recogida al terminar.",
      5,
      5,
      5,
      5,
      reservations.completedLucasAsMusician()
    );

    userReview(
      "Lucía coordinó el ensayo con antelación, cuidó el material de Boa y mantuvo una comunicación muy clara durante la reserva.",
      5,
      5,
      5,
      4,
      reservations.completedLucasAsManager()
    );
  }

  private void userReview(
    String comment,
    int overall,
    int communication,
    int punctuality,
    int care,
    ReservationSession reservation
  ) {
    persist(new UserReview(
      comment,
      overall,
      communication,
      punctuality,
      care,
      reservation.getMusicalSpace().getManager(),
      reservation.getUser(),
      reservation.getMusicalSpace(),
      reservation
    ));
  }

  private SeedEvents createInternalEvents(SeedUsers users, SeedBands bands, SeedSpaces spaces, SeedDates dates) {
    Event brumaLive = event(
      "Bruma Norte en Nave Lérez",
      "Concierto de presentación con repertorio propio y dos versiones preparadas para sala.",
      dates.futureSaturday().plusDays(7),
      "20:00",
      "22:30",
      "Indie Rock",
      80,
      12,
      EventStatus.PUBLISHED,
      EventType.CONCERT,
      spaces.lerezLive(),
      bands.brumaNorte(),
      users.alba(),
      unsplashPhoto("photo-1501386761578-eac5c94b800a")
    );

    Event jamSantiago = event(
      "Jam FIOS Santiago",
      "Sesión abierta para músicos locales con base rítmica y pequeñas formaciones rotativas.",
      dates.futureSaturday().plusDays(14),
      "19:00",
      "21:30",
      "Funk",
      45,
      5,
      EventStatus.PUBLISHED,
      EventType.JAM_SESSION,
      spaces.santiagoRoom(),
      null,
      users.carla(),
      unsplashPhoto("photo-1516280440614-37939bbacd81")
    );

    Event latidoShowcase = event(
      "Latido Verde showcase",
      "Showcase de funk soul con formato reducido y arreglos nuevos de voces.",
      dates.futureSaturday().plusDays(21),
      "20:30",
      "22:00",
      "Funk Soul",
      60,
      9,
      EventStatus.PUBLISHED,
      EventType.SHOWCASE,
      spaces.portoAlto(),
      bands.latidoVerde(),
      users.nico(),
      unsplashPhoto("photo-1511671782779-c97d3d27a1d4")
    );

    Event recordingWorkshop = event(
      "Taller de grabación casera",
      "Workshop pendiente de publicación sobre maquetas, interfaces y preparación de sesiones.",
      dates.futureFive().plusDays(10),
      "18:00",
      "20:00",
      "Produccion",
      18,
      15,
      EventStatus.DRAFT,
      EventType.WORKSHOP,
      spaces.corunaStudio(),
      null,
      users.alba(),
      unsplashPhoto("photo-1499364615650-ec38552f4f34")
    );

    Event lucasShowcase = event(
      "The Rapants en Boa",
      "Showcase intimo de The Rapants en Boa para presentar repertorio nuevo de indie rock gallego en formato de sala pequena.",
      dates.futureSaturday().plusDays(28),
      "20:00",
      "22:00",
      "Indie Rock",
      7,
      6,
      EventStatus.PUBLISHED,
      EventType.SHOWCASE,
      spaces.boa(),
      bands.theRapants(),
      users.lucas(),
      "/images/boa-local-de-ensaio.jpg"
    );

    return new SeedEvents(brumaLive, jamSantiago, latidoShowcase, recordingWorkshop, lucasShowcase);
  }

  private Event event(
    String title,
    String description,
    LocalDate date,
    String startTime,
    String endTime,
    String genre,
    int capacity,
    int ticketPrice,
    EventStatus status,
    EventType type,
    MusicalSpace space,
    Band band,
    User createdBy,
    String posterImage
  ) {
    MusicalSpaceLocation location = space.getLocation();
    return persist(new Event(
      title,
      description,
      date,
      LocalTime.parse(startTime),
      LocalTime.parse(endTime),
      genre,
      capacity,
      money(ticketPrice),
      posterImage,
      status,
      type,
      EventSource.INTERNAL,
      space.getName(),
      location.getLatitude(),
      location.getLongitude(),
      location.getCity(),
      location.getProvince(),
      location.getCountry(),
      location.getStreet(),
      null,
      null,
      null,
      space,
      band,
      createdBy
    ));
  }

  private void createEventPurchases(SeedUsers users, SeedEvents events) {
    purchase(users.lucia(), events.brumaLive(), 12);
    purchase(users.hugo(), events.brumaLive(), 12);
    purchase(users.carla(), events.jamSantiago(), 5);
    purchase(users.pablo(), events.latidoShowcase(), 9);
    purchase(users.lucas(), events.lucasShowcase(), 6);
    purchase(users.lucia(), events.lucasShowcase(), 6);
  }

  private void purchase(User user, Event event, int amountDue) {
    persist(new EventPurchase(user, event, money(amountDue)));
  }

  private void createSearches(SeedUsers users, SeedDates dates) {
    search(
      "Busco sala de ensayo en A Coruña para tres personas este viernes",
      "A Coruña",
      SearchIntent.SPACE,
      "17:00",
      "19:00",
      dates.futureOne(),
      SearchNeedType.REHEARSAL,
      3,
      35,
      MusicalSpaceType.REHEARSAL_ROOM,
      null,
      users.irene()
    );

    search(
      "Conciertos indie rock en Pontevedra",
      "Pontevedra",
      SearchIntent.EVENT,
      null,
      null,
      dates.futureSaturday().plusDays(7),
      SearchNeedType.GENERAL_DISCOVERY,
      null,
      null,
      null,
      "Indie Rock",
      users.lucia()
    );

    search(
      "Estudio de grabación en Ourense para voces",
      "Ourense",
      SearchIntent.SPACE,
      "16:00",
      "18:00",
      dates.futureTwo(),
      SearchNeedType.RECORDING,
      1,
      60,
      MusicalSpaceType.RECORDING_STUDIO,
      null,
      users.carla()
    );

    search(
      "Bandas de jazz fusion que busquen trompeta",
      "Pontevedra",
      SearchIntent.BOTH,
      null,
      null,
      null,
      SearchNeedType.OTHER,
      null,
      null,
      null,
      "Jazz Fusion",
      users.hugo()
    );
    search(
      "Necesito local de ensayo en A Coruna para The Rapants este viernes",
      "A Coruna",
      SearchIntent.SPACE,
      "18:00",
      "20:00",
      dates.futureOne(),
      SearchNeedType.REHEARSAL,
      5,
      60,
      MusicalSpaceType.REHEARSAL_ROOM,
      null,
      users.lucas()
    );

    search(
      "Conciertos indie rock y salas para tocar en octubre",
      "A Coruna",
      SearchIntent.BOTH,
      null,
      null,
      dates.futureSaturday().plusDays(28),
      SearchNeedType.GENERAL_DISCOVERY,
      null,
      null,
      null,
      "Indie Rock",
      users.lucas()
    );

    search(
      "Bandas que busquen guitarra para ensayar en A Coruna",
      "A Coruna",
      SearchIntent.BOTH,
      null,
      null,
      null,
      SearchNeedType.OTHER,
      null,
      null,
      null,
      "Indie Rock",
      users.lucas()
    );
  }

  private void search(
    String text,
    String city,
    SearchIntent intent,
    String startTime,
    String endTime,
    LocalDate date,
    SearchNeedType needType,
    Integer peopleCount,
    Integer maxBudget,
    MusicalSpaceType spaceType,
    String genre,
    User user
  ) {
    persist(new Search(
      text,
      city,
      intent,
      startTime == null ? null : LocalTime.parse(startTime),
      endTime == null ? null : LocalTime.parse(endTime),
      date,
      needType,
      peopleCount,
      maxBudget == null ? null : money(maxBudget),
      spaceType,
      genre,
      user
    ));
  }

  private void repairEventTextEncoding() {
    entityManager
      .createQuery("select e from Event e", Event.class)
      .getResultList()
      .forEach(event -> {
        boolean updated = false;

        String title = repairMojibake(event.getTitle());
        if (!Objects.equals(title, event.getTitle())) {
          event.setTitle(title);
          updated = true;
        }

        String description = repairMojibake(event.getDescription());
        if (!Objects.equals(description, event.getDescription())) {
          event.setDescription(description);
          updated = true;
        }

        String genre = repairMojibake(event.getMusicalGenre());
        if (!Objects.equals(genre, event.getMusicalGenre())) {
          event.setMusicalGenre(genre);
          updated = true;
        }

        String venue = repairMojibake(event.getVenueName());
        if (!Objects.equals(venue, event.getVenueName())) {
          event.setVenueName(venue);
          updated = true;
        }

        String city = repairMojibake(event.getCity());
        if (!Objects.equals(city, event.getCity())) {
          event.setCity(city);
          updated = true;
        }

        String province = repairMojibake(event.getProvince());
        if (!Objects.equals(province, event.getProvince())) {
          event.setProvince(province);
          updated = true;
        }

        String country = repairMojibake(event.getCountry());
        if (!Objects.equals(country, event.getCountry())) {
          event.setCountry(country);
          updated = true;
        }

        String location = repairMojibake(event.getLocation());
        if (!Objects.equals(location, event.getLocation())) {
          event.setLocation(location);
          updated = true;
        }

        if (updated) {
          entityManager.merge(event);
        }
      });
  }

  private String repairMojibake(String value) {
    if (value == null || !looksLikeMojibake(value)) {
      return value;
    }

    return new String(value.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
  }

  private boolean looksLikeMojibake(String value) {
    return value.indexOf('\u00c3') >= 0
      || value.indexOf('\u00c2') >= 0
      || value.indexOf('\u00e2') >= 0;
  }

  private void assertFinalUserCounts() {
    long normalUsers = countUsers(PlatformRole.USER);
    long admins = countUsers(PlatformRole.ADMIN);

    if (normalUsers != 14 || admins != 1) {
      throw new IllegalStateException(
        "Final seed expected 14 normal users and 1 admin but found "
          + normalUsers
          + " normal users and "
          + admins
          + " admins"
      );
    }
  }

  private long countUsers(PlatformRole role) {
    return entityManager
      .createQuery("select count(u) from User u where u.platformRole = :role", Long.class)
      .setParameter("role", role)
      .getSingleResult();
  }

  private SeedDates buildSeedDates() {
    LocalDate today = LocalDate.now();
    LocalDate futureOne = nextWorkingDay(today.plusDays(1));
    LocalDate futureTwo = nextWorkingDay(futureOne.plusDays(1));
    LocalDate futureThree = nextWorkingDay(futureTwo.plusDays(1));
    LocalDate futureFour = nextWorkingDay(futureThree.plusDays(1));
    LocalDate futureFive = nextWorkingDay(futureFour.plusDays(1));

    return new SeedDates(
      previousWorkingDay(today.minusDays(30)),
      previousWorkingDay(today.minusDays(21)),
      previousWorkingDay(today.minusDays(14)),
      previousWorkingDay(today.minusDays(7)),
      futureOne,
      futureTwo,
      futureThree,
      futureFour,
      futureFive,
      today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
    );
  }

  private LocalDate nextWorkingDay(LocalDate candidate) {
    LocalDate current = candidate;
    while (current.getDayOfWeek() == DayOfWeek.SATURDAY || current.getDayOfWeek() == DayOfWeek.SUNDAY) {
      current = current.plusDays(1);
    }
    return current;
  }

  private LocalDate previousWorkingDay(LocalDate candidate) {
    LocalDate current = candidate;
    while (current.getDayOfWeek() == DayOfWeek.SATURDAY || current.getDayOfWeek() == DayOfWeek.SUNDAY) {
      current = current.minusDays(1);
    }
    return current;
  }

  private BigDecimal money(int value) {
    return BigDecimal.valueOf(value);
  }

  private String unsplashPhoto(String photoId) {
    return "https://images.unsplash.com/" + photoId + "?auto=format&fit=crop&w=1200&q=80";
  }

  private <T> T persist(T entity) {
    entityManager.persist(entity);
    return entity;
  }

  private record SeedUsers(
    User admin,
    User alba,
    User nico,
    User carla,
    User diego,
    User irene,
    User martin,
    User sara,
    User hugo,
    User lucia,
    User pablo,
    User lucas,
    User xanma,
    User samu,
    User mati,
    User xaquin
  ) {
  }

  private record SeedInstruments(
    Instrument voice,
    Instrument electricGuitar,
    Instrument acousticGuitar,
    Instrument bass,
    Instrument drums,
    Instrument keyboard,
    Instrument sax,
    Instrument trumpet,
    Instrument synthesizer
  ) {
  }

  private record SeedBands(
    Band brumaNorte,
    Band latidoVerde,
    Band riaElectrica,
    Band pedraAzul,
    Band theRapants
  ) {
  }

  private record SeedSpaces(
    MusicalSpace corunaStudio,
    MusicalSpace portoAlto,
    MusicalSpace santiagoRoom,
    MusicalSpace lugoClassroom,
    MusicalSpace lerezLive,
    MusicalSpace vigoLab,
    MusicalSpace ourenseBooth,
    MusicalSpace ferrolSpace,
    MusicalSpace boa
  ) {
  }

  private record SeedEquipment(
    Equipment amplifiers,
    Equipment mixer,
    Equipment microphones,
    Equipment acousticDrums,
    Equipment digitalPiano,
    Equipment monitors,
    Equipment interfaceAudio,
    Equipment lights,
    Equipment micStands
  ) {
  }

  private record SeedReservations(
    ReservationSession completedStudio,
    ReservationSession completedSantiago,
    ReservationSession completedLerez,
    ReservationSession completedLugo,
    ReservationSession completedPortoPendingReviews,
    ReservationSession completedVigoReviewed,
    ReservationSession completedOurenseReviewed,
    ReservationSession completedLucasAsMusician,
    ReservationSession completedLucasAsManager,
    ReservationSession pendingLucas,
    ReservationSession acceptedLucas,
    ReservationSession pendingLucasRoom,
    ReservationSession pendingPorto,
    ReservationSession pendingOurense,
    ReservationSession acceptedVigo,
    ReservationSession acceptedFerrol,
    ReservationSession rejectedStudio,
    ReservationSession cancelledPorto
  ) {
  }

  private record SeedEvents(
    Event brumaLive,
    Event jamSantiago,
    Event latidoShowcase,
    Event recordingWorkshop,
    Event lucasShowcase
  ) {
  }

  private record SeedDates(
    LocalDate pastMonth,
    LocalDate pastThreeWeeks,
    LocalDate pastTwoWeeks,
    LocalDate pastWeek,
    LocalDate futureOne,
    LocalDate futureTwo,
    LocalDate futureThree,
    LocalDate futureFour,
    LocalDate futureFive,
    LocalDate futureSaturday
  ) {
  }
}
