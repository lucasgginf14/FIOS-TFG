export default {
  common: {
    actions: {
      search: "Buscar",
      login: "Iniciar sesión",
      register: "Rexistrarse",
      logout: "Pechar sesión",
      save: "Gardar",
      back: "Volver",
      close: "Pechar",
      loading: "Cargando..."
    },
    labels: {
      city: "Cidade",
      date: "Data",
      spaceType: "Tipo de espazo",
      genre: "Xénero musical",
      budget: "Orzamento",
      people: "Nº persoas",
      time: "Hora",
      capacity: "Aforo",
      onRequest: "Consultar",
      free: "Gratis",
      notifications: "Notificacións",
      language: "Idioma",
      navigation: "Navegación"
    },
    states: {
      noData: "Sen datos dispoñibles",
      backendUnavailable: "Non foi posible cargar os datos neste momento."
    },
    imageUpload: {
      upload: "Subir imaxe",
      uploading: "Subindo...",
      clear: "Quitar imaxe",
      urlPlaceholder: "https://exemplo.com/imaxe.jpg",
      error: "Non se puido subir a imaxe."
    }
  },
  apiErrors: {
    generic: "Non se puido completar a acción. Téntao de novo.",
    network: "Non hai conexión co servizo. Revisa a túa conexión ou téntao de novo nuns segundos.",
    unavailable: "O servizo non está dispoñible agora mesmo. Téntao de novo nuns minutos.",
    badRequest: "Revisa os datos introducidos. Hai algo que non parece correcto.",
    unauthorized: "A túa sesión caducou. Inicia sesión de novo para continuar.",
    forbidden: "Non tes permiso para facer esta acción.",
    notFound: "Non atopamos o que intentas abrir. Pode que xa non estea dispoñible.",
    conflict: "Non se puido gardar porque xa existe un dato igual ou relacionado.",
    rateLimited: "Fixéronse demasiados intentos seguidos. Agarda uns segundos e volve probar.",
    internal: "Houbo un problema inesperado. Téntao de novo máis tarde.",
    malformed: "Non foi posible ler a información enviada. Revisa os datos e téntao outra vez.",
    badCredentials: "O email ou o contrasinal non son correctos.",
    accountDisabled: "Esta conta está desactivada. Contacta con administración se cres que é un erro.",
    emailExists: "Xa existe unha conta con ese email.",
    phoneExists: "Xa existe unha conta con ese teléfono.",
    invalidEmail: "Escribe un email válido.",
    invalidPhone: "Introduce un teléfono válido.",
    requiredFields: "Completa os campos obrigatorios antes de continuar.",
    futureBirthDate: "A data de nacemento non pode estar no futuro.",
    passwordMismatch: "Os contrasinais non coinciden.",
    passwordWeak: "O contrasinal debe ter polo menos unha letra e un número.",
    currentPasswordIncorrect: "O contrasinal actual non é correcto.",
    passwordSameAsCurrent: "O novo contrasinal debe ser distinto do actual.",
    invalidTimeRange: "A hora de inicio debe ser anterior á hora de fin.",
    invalidDate: "A data escollida non é válida.",
    invalidNumber: "Revisa os números introducidos. Algún está fóra do rango permitido.",
    tooLong: "O texto é demasiado longo.",
    timeNotAvailable: "Ese horario non está dispoñible. Escolle outra franxa.",
    noTicketsAvailable: "Non quedan entradas dispoñibles para este evento.",
    eventAlreadyReserved: "Xa tes unha reserva activa para este evento.",
    eventAlreadyPurchased: "Xa tes unha reserva activa para este evento.",
    alreadyExists: "Xa existe un elemento igual ou relacionado.",
    spaceEquipmentAlreadyExists: "Este equipamento xa está asociado ao espazo.",
    noPermission: "Non podes facer esta acción con esta conta.",
    actionNotAvailable: "Esta acción non está dispoñible agora mesmo.",
    locationUnavailable: "Non foi posible comprobar a localización. Revisa o enderezo ou as coordenadas.",
    invalidFile: "O arquivo enviado non é válido.",
    fileTooLarge: "O arquivo supera o tamaño máximo permitido."
  },
  apiFieldErrors: {
    FIELD_REQUIRED: "Completa este campo.",
    INVALID_EMAIL: "Escribe un email válido.",
    INVALID_PHONE: "Introduce un teléfono válido.",
    INVALID_LENGTH: "Revisa a lonxitude deste campo.",
    INVALID_NUMBER: "Revisa este número.",
    INVALID_DATE: "A data escollida non é válida.",
    INVALID_TIME_RANGE: "A hora de inicio debe ser anterior á hora de fin.",
    INVALID_DATE_RANGE: "A data de inicio debe ser anterior ou igual á data de fin.",
    INVALID_COORDINATES: "Revisa as coordenadas introducidas.",
    INVALID_VALUE: "Revisa este campo."
  },
  navbar: {
    brandTagline: "plataforma musical",
    menu: {
      search: "Buscar",
      spaces: "Espazos",
      reservations: "Reservas",
      events: "Eventos",
      bands: "Bandas",
      messages: "Mensaxes"
    },
    dropdowns: {
      smartSearch: "Busca intelixente",
      smartSearchMeta: "Linguaxe natural e filtros",
      searchMap: "Mapa",
      searchMapMeta: "Vista en mapa",
      exploreSpaces: "Explorar espazos",
      exploreSpacesMeta: "Catálogo público",
      mySpaces: "Os meus espazos",
      mySpacesMeta: "Xestiona os teus locais",
      createSpace: "Crear espazo",
      createSpaceMeta: "Novo local",
      myReservations: "As miñas reservas",
      myReservationsMeta: "As túas sesións",
      receivedReservations: "Reservas recibidas",
      receivedReservationsMeta: "Solicitudes dos teus locais",
      upcomingEvents: "Próximos eventos",
      upcomingEventsMeta: "Axenda publicada",
      eventMap: "Mapa de eventos",
      eventMapMeta: "Explora por cidade",
      exploreBands: "Explorar bandas",
      exploreBandsMeta: "Listado principal",
      myBands: "As miñas bandas",
      myBandsMeta: "Vista persoal",
      createBand: "Crear banda",
      createBandMeta: "Novo proxecto",
      memberSearch: "Buscas de membros",
      memberSearchMeta: "Ofertas activas"
    },
    userMenu: {
      profile: "O meu perfil",
      profileMeta: "Datos da conta",
      reservations: "As miñas reservas",
      reservationsMeta: "Sesións reservadas",
      reviews: "As miñas reseñas",
      reviewsMeta: "Pendentes e publicadas",
      tickets: "As miñas entradas",
      ticketsMeta: "Eventos reservados",
      bands: "As miñas bandas",
      bandsMeta: "Bandas e proxectos",
      favorites: "Favoritos",
      favoritesMeta: "Espazos gardados",
      admin: "Administración",
      adminMeta: "Panel de xestión",
      logoutMeta: "Pecha a túa sesión de forma segura."
    },
    notifications: {
      title: "Notificacións",
      summary: "{count} mensaxes sen ler",
      empty: "Non tes notificacions",
      viewMessages: "Ver mensaxes",
      viewMessagesMeta: "Abre a túa caixa e continúa a conversa",
      fallbackConversation: "Conversa",
      pending: "Actividade recente"
    }
  },
  account: {
    login: {
      title: "Iniciar sesión",
      description: "Accede a FIOS coa túa conta.",
      eyebrow: "Conta",
      badge: "Acceso",
      heading: "Benvido de volta a FIOS",
      subtitle:
        "Usa o teu email e contrasinal para entrar e recuperar a túa actividade.",
      email: "Email",
      password: "Contrasinal",
      submit: "Entrar en FIOS",
      submitting: "Entrando...",
      noAccount: "Non tes conta?",
      registerLink: "Rexístrate",
      registeredSuccess: "Conta creada correctamente. Xa podes iniciar sesión.",
      autoLoginFallback:
        "A conta creouse, pero non se puido iniciar sesión automaticamente. Entra manualmente para continuar."
    },
    register: {
      title: "Crear conta",
      description: "Crea unha conta para gardar as túas reservas, mensaxes e preferencias.",
      eyebrow: "Conta",
      badge: "Rexistro",
      heading: "Crea a túa conta",
      subtitle:
        "Crea a túa conta e, se todo vai ben, entrarás automaticamente en FIOS coa sesión iniciada.",
      name: "Nome",
      firstSurname: "Primeiro apelido",
      secondSurname: "Segundo apelido",
      phone: "Teléfono",
      email: "Email",
      birthDate: "Data de nacemento",
      instrument: "Instrumento",
      noInstrument: "Sen instrumento",
      loadingInstruments: "Cargando instrumentos...",
      instrumentHelp: "Podes indicar agora o instrumento principal que tocas.",
      instrumentCatalogError: "Non se puido cargar o catálogo de instrumentos.",
      validationInstrument: "Selecciona un instrumento válido do catálogo.",
      password: "Contrasinal",
      confirmPassword: "Confirmar contrasinal",
      passwordRulesTitle: "O teu contrasinal debe cumprir:",
      formRulesTitle: "Antes de enviar revisa:",
      passwordRules: {
        minLength: "Mínimo 8 caracteres",
        hasLetter: "Incluír polo menos unha letra",
        hasNumber: "Incluír polo menos un número",
        notEmpty: "Non estar baleiro"
      },
      formRules: {
        validEmail: "Email válido",
        requiredName: "Nome obrigatorio",
        requiredFirstSurname: "Primeiro apelido obrigatorio",
        requiredPhone: "Teléfono obrigatorio",
        validPhone: "Teléfono válido",
        validBirthDate: "Data de nacemento válida"
      },
      submit: "Crear conta",
      submitting: "Creando conta...",
      backToLogin: "Volver ao inicio de sesión"
    },
    errors: {
      requiredLogin: "Debes introducir email e contrasinal.",
      loginGeneric: "Non se puido iniciar sesión. Inténtao de novo.",
      registerGeneric: "Non se puido completar o rexistro.",
      network: "Non hai conexión co servizo de acceso.",
      badCredentials: "O email ou o contrasinal non son correctos.",
      emailExists: "Ese email xa está rexistrado.",
      phoneExists: "Ese teléfono xa está rexistrado.",
      malformed: "Non foi posible ler a información enviada.",
      requiredFields: "Completa os campos obrigatorios antes de continuar.",
      passwordMismatch: "Os contrasinais non coinciden.",
      invalidEmail: "Introduce un email válido.",
      passwordWeak: "O contrasinal debe incluír polo menos unha letra e un número.",
      futureBirthDate: "A data de nacemento non pode estar no futuro."
    }
  },
  profile: {
    header: {
      eyebrow: "Perfil FIOS",
      title: "O meu perfil",
      subtitle: "Xestiona a túa información persoal e a seguridade da conta.",
      back: "Volver",
      memberSince: "Alta {date}"
    },
    actions: {
      editProfile: "Editar perfil",
      changePassword: "Cambiar contrasinal",
      saveChanges: "Gardar cambios",
      updatePassword: "Actualizar contrasinal",
      cancel: "Cancelar",
      retry: "Reintentar"
    },
    states: {
      loading: "Cargando perfil...",
      errorTitle: "Non foi posible abrir o teu perfil",
      error: "Non se puido cargar a información do perfil."
    },
    summary: {
      reservations: "Reservas",
      bands: "Bandas",
      favorites: "Favoritos",
      unreadMessages: "Mensaxes sen ler"
    },
    details: {
      eyebrow: "Datos reais",
      title: "Información da conta"
    },
    fields: {
      name: "Nome",
      firstSurname: "Primeiro apelido",
      secondSurname: "Segundo apelido",
      email: "Email",
      phone: "Teléfono",
      birthDate: "Data de nacemento",
      instruments: "Instrumentos",
      instrument: "Instrumento",
      noInstrument: "Sen instrumento",
      role: "Rol",
      createdAt: "Data de alta"
    },
    edit: {
      eyebrow: "Edición",
      title: "Actualizar perfil",
      subtitle: "Modifica os teus datos básicos. O email mantense como referencia de acceso.",
      saving: "Gardando...",
      loadingInstruments: "Cargando instrumentos...",
      instrumentHelp: "Selecciona o instrumento principal que tocas desde o catálogo de FIOS.",
      instrumentCatalogError: "Non se puido cargar o catálogo de instrumentos.",
      validationRequired: "Completa nome, primeiro apelido e teléfono antes de gardar.",
      validationBirthDate: "A data de nacemento non pode estar no futuro.",
      validationInstrument: "Selecciona un instrumento válido do catálogo.",
      error: "Non se puido actualizar o perfil."
    },
    password: {
      eyebrow: "Seguridade",
      title: "Cambiar contrasinal",
      subtitle: "Actualiza o contrasinal sen gardar datos sensibles no cliente.",
      currentPassword: "Contrasinal actual",
      newPassword: "Novo contrasinal",
      confirmPassword: "Confirmar novo contrasinal",
      saving: "Actualizando...",
      validationRequired: "Completa os tres campos de contrasinal antes de continuar.",
      validationLength: "O novo contrasinal debe ter polo menos 8 caracteres.",
      validationPolicy: "O novo contrasinal debe incluír polo menos unha letra e un número.",
      validationMatch: "A confirmación do novo contrasinal non coincide.",
      error: "Non se puido cambiar o contrasinal."
    },
    image: {
      eyebrow: "Imaxe",
      title: "Imaxe de perfil",
      subtitle: "Personaliza a imaxe pública da túa conta.",
      previewAlt: "Imaxe de perfil",
      empty: "Sen imaxe de perfil",
      inputLabel: "Imaxe",
      placeholder: "https://exemplo.com/avatar.jpg",
      save: "Actualizar imaxe",
      remove: "Eliminar imaxe",
      saving: "Actualizando...",
      removing: "Eliminando...",
      validationRequired: "Indica unha imaxe antes de gardar.",
      validationUrl: "A ligazón da imaxe non é válida.",
      error: "Non se puido actualizar a imaxe de perfil.",
      removeError: "Non se puido eliminar a imaxe de perfil."
    },
    notices: {
      profileUpdated: "O perfil actualizouse correctamente.",
      passwordUpdated: "O contrasinal actualizouse correctamente.",
      imageUpdated: "A imaxe de perfil actualizouse correctamente.",
      imageRemoved: "A imaxe de perfil eliminouse correctamente."
    },
    roles: {
      USER: "Usuario",
      ADMIN: "Administrador"
    },
    status: {
      active: "Activo",
      inactive: "Inactivo"
    },
    fallbacks: {
      noData: "Sen dato",
      noInstruments: "Sen instrumentos asociados",
      noInstrument: "Sen instrumento asociado",
      user: "Usuario FIOS"
    }
  },
  favorites: {
    header: {
      eyebrow: "Favoritos FIOS",
      back: "Volver",
      title: "Os meus favoritos",
      subtitle: "Espazos gardados para reservar máis tarde.",
      explore: "Explorar espazos"
    },
    actions: {
      retry: "Reintentar"
    },
    stats: {
      total: "Favoritos",
      cities: "Cidades",
      recent: "Recentes"
    },
    filters: {
      search: "Buscar",
      searchPlaceholder: "Buscar por nome ou cidade",
      city: "Cidade",
      allCities: "Todas as cidades",
      type: "Tipo de espazo",
      allTypes: "Todos os tipos",
      order: "Ordenar por",
      clear: "Limpar filtros",
      orderOptions: {
        recent: "Máis recentes",
        name: "Nome",
        city: "Cidade",
        rating: "Mellor valorados"
      }
    },
    list: {
      eyebrow: "Lista gardada",
      title: "Espazos favoritos",
      total: "{count} favoritos"
    },
    card: {
      imagePlaceholder: "Imaxe non dispoñible",
      locationFallback: "Localización pendente",
      capacityValue: "{value} persoas",
      soundproofed: "Insonorización",
      soundproofedYes: "Insonorizado",
      soundproofedNo: "Sen insonorizar",
      soundproofedUnknown: "Por revisar",
      rating: "Valoración",
      ratingValue: "{rating} ({count})",
      noRating: "Sen reseñas",
      savedAt: "Gardado {date}",
      viewDetail: "Ver detalle",
      remove: "Quitar favorito",
      removing: "Quitando..."
    },
    empty: {
      title: "Aínda non tes favoritos",
      text: "Garda espazos desde o detalle para recuperalos rápido cando queiras reservar.",
      filteredTitle: "Non hai resultados con estes filtros",
      filteredText: "Proba a cambiar a busca ou limpar os filtros para recuperar favoritos.",
      exploreAction: "Explorar espazos",
      resetAction: "Restablecer filtros"
    },
    states: {
      loading: "Cargando favoritos...",
      errorTitle: "Non foi posible abrir os teus favoritos",
      error: "Non se puido cargar a listaxe de favoritos."
    },
    notices: {
      removed: "O espazo quitouse de favoritos.",
      removeError: "Non se puido quitar o espazo de favoritos."
    },
    confirm: {
      remove: 'Vas quitar "{name}" de favoritos.'
    }
  },
  reviewBoard: {
    header: {
      eyebrow: "Reseñas FIOS",
      back: "Volver",
      title: "Reseñas",
      subtitle: "Valora as túas experiencias con espazos musicais e usuarios das túas reservas."
    },
    tabs: {
      pending: "Pendentes",
      mine: "As miñas reseñas",
      received: "Recibidas"
    },
    stats: {
      pending: "Pendentes",
      published: "Publicadas",
      received: "Recibidas",
      average: "Media dada",
      targets: "Obxectivos valorados"
    },
    types: {
      SPACE: "Espazo",
      USER: "Usuario",
      USER_RECEIVED: "Recibida"
    },
    actions: {
      retry: "Tentar outra vez",
      write: "Escribir reseña",
      viewSpace: "Ver espazo",
      cancel: "Cancelar"
    },
    states: {
      loading: "Cargando reseñas...",
      errorTitle: "Non se puideron abrir as túas reseñas",
      error: "Non se puido cargar a información de reseñas."
    },
    pending: {
      eyebrow: "Reservas completadas",
      title: "Pendentes de reseña",
      total: "{count} pendentes",
      fallbackSpace: "Espazo musical",
      fallbackUser: "Usuario FIOS",
      noLocation: "Localización pendente",
      noSchedule: "Horario pendente"
    },
    mine: {
      eyebrow: "Historial publicado",
      title: "As miñas reseñas",
      total: "{count} reseñas",
      fallbackSpace: "Espazo musical",
      fallbackUser: "Usuario FIOS",
      noLocation: "Localización pendente",
      noComment: "Non deixaches comentario nesta reseña."
    },
    received: {
      eyebrow: "Valoracións recibidas",
      title: "Reseñas sobre ti",
      total: "{count} recibidas"
    },
    filters: {
      search: "Buscar",
      searchPlaceholder: "Buscar por espazo, usuario ou comentario",
      minRating: "Valoración mínima",
      minRatingOption: "{value} estrelas ou máis",
      allRatings: "Todas as valoracións",
      order: "Orde",
      clear: "Limpar filtros",
      orderOptions: {
        recent: "Máis recentes",
        highest: "Mellor valoradas",
        lowest: "Peor valoradas"
      }
    },
    metrics: {
      sound: "Son",
      equipment: "Equipamento",
      cleanliness: "Limpeza",
      location: "Localización",
      communication: "Comunicación",
      punctuality: "Puntualidade",
      care: "Coidado do espazo"
    },
    form: {
      eyebrow: "Nova reseña",
      title: "Escribir reseña",
      intro: "Comparte unha valoración clara para axudar a outros músicos e ao espazo.",
      userTitle: "Reseñar usuario",
      userIntro: "Valora como foi a comunicación, puntualidade e coidado durante a reserva.",
      overallRating: "Valoración xeral",
      soundQualityRating: "Calidade do son",
      equipmentRating: "Equipamento",
      cleanlinessRating: "Limpeza",
      locationRating: "Localización",
      communicationRating: "Comunicación",
      punctualityRating: "Puntualidade",
      careRating: "Coidado do espazo",
      comment: "Comentario",
      commentPlaceholder: "Conta como foi a experiencia, que funcionou ben e que podería mellorar.",
      submit: "Publicar reseña",
      submitting: "Publicando...",
      validation: "Debes indicar unha valoración entre 1 e 5 en todos os apartados.",
      error: "Non se puido publicar a reseña."
    },
    empty: {
      pendingTitle: "Non tes reseñas pendentes",
      pendingText:
        "Cando completes unha reserva poderás valorar o espazo, e cando xestiones espazos poderás valorar a quen reservou.",
      mineTitle: "Aínda non publicaches reseñas",
      mineText: "As túas valoracións publicadas aparecerán aquí co seu desglose por categorías.",
      receivedTitle: "Aínda non tes reseñas recibidas",
      receivedText: "Cando unha persoa propietaria valore unha reserva completada contigo, aparecerá aquí.",
      filteredTitle: "Non hai reseñas con estes filtros",
      filteredText: "Proba a cambiar o texto, baixar a valoración mínima ou limpar os filtros.",
      pendingAction: "Ver reservas",
      mineAction: "Explorar espazos",
      receivedAction: "Explorar espazos",
      clearAction: "Limpar filtros"
    },
    notices: {
      created: "A reseña publicouse correctamente."
    }
  },
  instrumentBoard: {
    header: {
      eyebrow: "Instrumentos FIOS",
      back: "Volver",
      title: "Instrumentos",
      subtitle: "Explora instrumentos e configura o teu instrumento principal.",
      catalog: "Catálogo",
      mine: "O meu instrumento",
      create: "Crear instrumento"
    },
    actions: {
      retry: "Tentar outra vez",
      cancel: "Cancelar"
    },
    stats: {
      total: "Instrumentos",
      categories: "Categorías",
      mine: "O meu instrumento",
      voice: "Voz"
    },
    filters: {
      search: "Buscar",
      searchPlaceholder: "Buscar por nome do instrumento",
      category: "Categoría",
      allCategories: "Todas as categorías",
      order: "Ordenar por",
      clear: "Limpar filtros",
      orderOptions: {
        name: "Nome",
        category: "Categoría"
      }
    },
    list: {
      eyebrow: "Panel instrumental",
      catalogTitle: "Catálogo público",
      mineTitle: "O teu instrumento principal",
      total: "{count} instrumentos"
    },
    card: {
      categoryLabel: "Categoría:",
      add: "Seleccionar instrumento",
      remove: "Quitar instrumento",
      edit: "Editar",
      saving: "Gardando..."
    },
    empty: {
      catalogTitle: "Non hai instrumentos no catálogo",
      catalogText:
        "Cando existan instrumentos dispoñibles, aparecerán aquí para filtralos e engadilos ao teu perfil.",
      mineTitle: "Aínda non configuraches o teu instrumento",
      mineText:
        "Selecciona desde o catálogo o instrumento principal que tocas para mellorar o teu perfil musical.",
      filteredTitle: "Non hai resultados con estes filtros",
      filteredText: "Proba a cambiar a busca, a categoría ou limpar os filtros.",
      mineAction: "Explorar catálogo",
      clearAction: "Limpar filtros"
    },
    form: {
      createEyebrow: "Novo instrumento",
      createTitle: "Crear instrumento",
      editEyebrow: "Editar instrumento",
      editTitle: "Actualizar instrumento",
      intro: "Define un nome claro e a categoría axeitada para o catálogo público.",
      name: "Nome",
      category: "Categoría",
      selectCategory: "Selecciona unha categoría",
      create: "Crear instrumento",
      save: "Gardar cambios",
      creating: "Creando...",
      saving: "Gardando...",
      validation: "Debes indicar nome e categoría.",
      error: "Non se puido gardar o instrumento."
    },
    notices: {
      minePartial: "O catálogo cargou, pero non se puido recuperar o teu instrumento.",
      adminOnly: "Só os administradores poden crear instrumentos.",
      added: "Instrumento engadido ao teu perfil.",
      selected: "Instrumento principal actualizado.",
      removed: "Instrumento quitado do teu perfil.",
      updateError: "Non se puido actualizar o teu instrumento.",
      created: "Instrumento creado correctamente.",
      updated: "Instrumento actualizado correctamente."
    },
    states: {
      loading: "Cargando instrumentos...",
      errorTitle: "Non foi posible abrir os instrumentos",
      error: "Non se puido cargar o catálogo de instrumentos."
    },
    categories: {
      STRINGS: "Corda",
      WIND: "Vento",
      BRASS: "Metal",
      PERCUSSION: "Percusión",
      KEYBOARD: "Teclado",
      ELECTRONIC: "Electrónico",
      VOICE: "Voz",
      OTHER: "Outros"
    }
  },
  recruitmentBoard: {
    header: {
      eyebrow: "Ofertas FIOS",
      back: "Volver",
      title: "Buscas de membros",
      subtitle: "Atopa bandas que buscan músicos.",
      explore: "Explorar",
      mine: "As miñas buscas",
      create: "Publicar busca"
    },
    actions: {
      retry: "Reintentar",
      cancel: "Cancelar"
    },
    stats: {
      open: "Abertas",
      bands: "Bandas",
      instruments: "Instrumentos",
      cities: "Cidades"
    },
    filters: {
      search: "Buscar",
      searchPlaceholder: "Buscar por título, banda ou cidade",
      instrument: "Instrumento",
      allInstruments: "Todos os instrumentos",
      level: "Nivel requirido",
      allLevels: "Todos os niveis",
      city: "Cidade",
      allCities: "Todas as cidades",
      genre: "Xénero musical",
      allGenres: "Todos os xéneros",
      order: "Ordenar por",
      clear: "Limpar filtros",
      orderOptions: {
        recent: "Máis recentes",
        city: "Cidade",
        instrument: "Instrumento",
        vacancies: "Máis vacantes"
      }
    },
    list: {
      eyebrow: "Taboleiro activo",
      title: "Buscas dispoñibles",
      exploreTitle: "Explorar buscas abertas",
      mineTitle: "Xestiona as túas buscas",
      total: "{count} buscas"
    },
    card: {
      bandFallback: "Banda",
      cityFallback: "Cidade pendente",
      genreFallback: "Xénero por definir",
      instrumentFallback: "Rol aberto",
      vacancies: "{count} vacantes",
      noPublicationDate: "Data pendente",
      view: "Ver oferta",
      edit: "Editar",
      close: "Pechar busca",
      closing: "Pechando..."
    },
    empty: {
      title: "Non hai buscas activas",
      text: "Cando as bandas publiquen novas vacantes, aparecerán aquí para que as poidas explorar.",
      mineTitle: "Aínda non publicaches buscas",
      mineText: "Publica unha vacante desde unha das túas bandas para empezar a recibir interese.",
      filteredTitle: "Non hai resultados con estes filtros",
      filteredText: "Proba a cambiar a busca ou limpar os filtros para recuperar ofertas.",
      exploreAction: "Explorar bandas",
      createAction: "Publicar busca",
      createBandAction: "Crear banda",
      resetAction: "Restablecer filtros"
    },
    states: {
      loading: "Cargando buscas...",
      errorTitle: "Non foi posible abrir as buscas",
      error: "Non se puido cargar a lista de buscas."
    },
    notices: {
      privateFallback:
        "Debes iniciar sesión para ver as túas buscas. Amósanse as buscas abertas.",
      partialSupport: "A lista principal cargouse, pero faltan algúns datos auxiliares.",
      noBands: "Necesitas polo menos unha banda para publicar unha busca.",
      noManageableBands: "Só podes publicar buscas desde bandas que lideras.",
      created: "A busca publicouse correctamente.",
      updated: "A busca actualizouse correctamente.",
      closed: "A busca pechouse correctamente.",
      closeError: "Non se puido pechar a busca."
    },
    confirm: {
      close: 'Vas pechar "{title}".'
    },
    form: {
      createEyebrow: "Nova busca",
      createTitle: "Publicar busca de membros",
      editEyebrow: "Editar busca",
      editTitle: "Actualizar busca",
      intro: "Completa unha oferta simple para que outros músicos atopen o teu proxecto.",
      band: "Banda",
      selectBand: "Selecciona unha banda",
      instrument: "Instrumento",
      selectInstrument: "Selecciona un instrumento",
      title: "Título",
      roleWanted: "Rol buscado",
      levelRequired: "Nivel requirido",
      city: "Cidade",
      vacancies: "Vacantes",
      description: "Descrición",
      create: "Publicar busca",
      save: "Gardar cambios",
      creating: "Publicando...",
      saving: "Gardando...",
      validation: "Completa banda, instrumento, título, rol, cidade e vacantes antes de gardar.",
      submitError: "Non se puido gardar a busca."
    },
    detail: {
      eyebrow: "Oferta",
      titleFallback: "Detalle da busca",
      loading: "Cargando oferta...",
      error: "Non se puido cargar o detalle da oferta.",
      descriptionTitle: "Descrición",
      emptyDescription: "Esta oferta aínda non ten unha descrición ampliada.",
      contactTitle: "Contacto",
      contactHint: "Escribe á persoa que publicou esta oferta para falar sobre a vacante.",
      contactAction: "Enviar email",
      viewBand: "Explorar bandas",
      fields: {
        city: "Cidade",
        genre: "Xénero musical",
        role: "Rol buscado",
        vacancies: "Vacantes",
        publicationDate: "Publicación"
      }
    },
    status: {
      OPEN: "Aberta",
      CLOSED: "Pechada"
    }
  },
  home: {
    viewAll: "Ver todo",
    actions: {
      viewSpace: "Ver espazo",
      viewEvent: "Ver evento",
      viewOffer: "Ver oferta",
      viewBand: "Ver banda"
    },
    hero: {
      kicker: "Plataforma musical",
      title: "Atopa espazos, eventos e músicos para o teu proxecto musical",
      subtitle:
        "Descubre locais, concertos, bandas e oportunidades de colaboración desde unha única plataforma pensada para a escena musical.",
      inputPlaceholder: "Busca locais, bandas ou concertos",
      examples: "Ver exemplos de busca",
      exampleOne: "Local de ensaio para 5 músicos na Coruña este venres",
      exampleTwo: "Concertos de jazz en Santiago esta fin de semana",
      exampleThree: "Banda busca baixista en Vigo",
      metrics: {
        label: "Resumo do catálogo",
        spaces: "espazos",
        cities: "cidades",
        events: "eventos",
        recruitments: "buscas"
      }
    },
    quickFilters: {
      title: "Filtros rápidos",
      subtitle: "Axusta a busca antes de ir ao modulo completo.",
      cityPlaceholder: "A Coruña, Vigo, Santiago...",
      datePlaceholder: "Escolle data",
      spaceTypePlaceholder: "Todos os espazos",
      genrePlaceholder: "Rock, jazz, pop...",
      budgetPlaceholder: "Max €",
      peoplePlaceholder: "Asistentes",
      reset: "Limpar filtros"
    },
    featuredSpaces: {
      title: "Espazos destacados",
      subtitle: "Locais e salas listas para ensaio, gravación ou directo.",
      soundproofed: "Insonorizado",
      notSoundproofed: "Sen insonorizar",
      capacity: "Capacidade",
      estimatedPrice: "Prezo estimado",
      rating: "Valoración",
      pricePerHour: "{price}/h",
      priceRange: "{from} - {to}/h",
      reasons: {
        highRating: "Destaca polas súas valoracións e reseñas.",
        soundproofed: "Boa opción se precisas controlar o ruído.",
        large: "Interesante para grupos grandes ou montaxes amplas.",
        completeProfile: "Ten información visual suficiente para valorar o espazo.",
        balanced: "Boa opción xeral para comezar a comparar."
      }
    },
    featuredEvents: {
      title: "Eventos destacados",
      subtitle: "Unha pequena axenda para comezar a exploración.",
      reasons: {
        today: "É unha opción para hoxe.",
        soon: "Está preto na axenda.",
        accessible: "Ten unha entrada accesible ou prezo por confirmar.",
        large: "Evento con aforo amplo.",
        linked: "Está conectado con contido da plataforma.",
        upcoming: "Evento publicado e aínda dispoñible."
      }
    },
    recruitments: {
      title: "Bandas buscan membros",
      subtitle: "Oportunidades activas para incorporarte a novos proxectos.",
      vacancies: "Vacantes",
      level: "Nivel",
      instrument: "Instrumento"
    },
    personal: {
      title: "A túa zona persoal",
      subtitle: "Actividade recente e accesos rápidos ao teu contexto.",
      searches: "Ultimas buscas",
      reservations: "As miñas reservas",
      messages: "Mensaxes recentes",
      loginCtaTitle: "Inicia sesión para ver a túa zona persoal",
      loginCtaText:
        "Accede ás buscas gardadas, reservas activas e conversas recentes desde a portada.",
      unreadCount: "{count} mensaxes sen ler",
      fallbackSpace: "Espazo",
      fallbackConversation: "Conversacion",
      fallbackBand: "Banda"
    },
    stats: {
      spaces: "espazos",
      events: "eventos",
      recruitments: "ofertas"
    },
    states: {
      loadingPublic: "Cargando datos reais",
      loadingPublicText: "Estamos consultando a información publicada en FIOS.",
      loadingPersonal: "Cargando a túa actividade..."
    },
    errors: {
      publicDataWarning:
        "Non se puideron cargar todos os datos públicos neste momento.",
      spacesTitle: "Non se puideron cargar os espazos",
      spacesText:
        "A sección de espazos non está dispoñible agora mesmo. Intenta de novo desde a listaxe.",
      eventsTitle: "Non se puideron cargar os eventos",
      eventsText:
        "A axenda pública non está dispoñible agora mesmo. Intenta de novo desde eventos.",
      recruitmentsTitle: "Non se puideron cargar as buscas",
      recruitmentsText:
        "As ofertas de bandas non están dispoñibles agora mesmo. Intenta de novo desde o taboleiro.",
      searchesText: "Non se puideron cargar as túas buscas recentes.",
      reservationsText: "Non se puideron cargar as túas reservas.",
      messagesText: "Non se puideron cargar as túas mensaxes recentes."
    },
    placeholders: {
      spacesTitle: "Sen espazos destacados",
      spacesText: "Non hai espazos públicos para amosar agora mesmo.",
      eventsTitle: "Sen eventos destacados",
      eventsText: "Non hai eventos publicados para amosar agora mesmo.",
      recruitmentsTitle: "Sen buscas activas",
      recruitmentsText: "Non hai ofertas activas de bandas para amosar agora mesmo.",
      reservationsText: "As túas reservas aparecerán aquí cando existan sesións activas.",
      messagesText: "As túas conversas con mensaxes pendentes listaranse aquí.",
      searchesText: "As buscas naturais recentes amosaranse aquí."
    },
    notFound: {
      title: "Ruta non atopada",
      description: "Non atopamos a paxina que intentas abrir en FIOS.",
      text: "Pode que a ligazón cambiase ou que o enderezo non sexa correcto. Volve ao inicio para continuar explorando espazos, eventos e bandas.",
      action: "Volver ao inicio"
    }
  },
  search: {
    hero: {
      kicker: "Resultados FIOS",
      title: "Atopa espazos, eventos e bandas preto de ti",
      subtitle:
        "Escribe o que necesitas ou usa os filtros para descubrir locais, concertos e proxectos que buscan xente.",
      placeholder: "Local de ensaio en Santiago para 4 persoas este venres pola tarde",
      searching: "Buscando..."
    },
    viewModes: {
      list: "Lista",
      map: "Mapa"
    },
    results: {
      eyebrow: "Resultados mixtos",
      title: "Exploración activa",
      summary: "{count} resultados visibles"
    },
    tabs: {
      all: "Todos",
      spaces: "Espazos",
      events: "Eventos",
      bands: "Bandas",
      recruitments: "Bandas buscan membros"
    },
    filters: {
      eyebrow: "Refinar",
      title: "Filtros",
      clear: "Limpar",
      dateTitle: "Data",
      resultTypeTitle: "Tipo de resultado",
      spaceTypeTitle: "Tipo de espazo",
      genreTitle: "Xénero musical",
      budgetTitle: "Orzamento máximo",
      maxBudgetPlaceholder: "Orzamento máximo",
      peopleTitle: "Nº de persoas",
      timeTitle: "Horario",
      apply: "Aplicar filtros",
      anyBudget: "Sen límite",
      anyPeople: "Sen límite",
      maxBudgetValue: "Ata {value} EUR",
      peopleValue: "{value} persoas",
      datePresets: {
        today: "Hoxe",
        tomorrow: "Mañá",
        weekend: "Esta fin de semana",
        week: "Esta semana"
      },
      spaceTypes: {
        rehearsal: "Local de ensaio",
        recording: "Estudo de gravación",
        performance: "Sala de actuación",
        barStage: "Bar con escenario",
        multiuse: "Sala polivalente"
      },
      timeSlots: {
        morning: "Mañá",
        afternoon: "Tarde",
        night: "Noite",
        allday: "Todo o dia"
      }
    },
    states: {
      loading: "Cargando resultados...",
      emptyTitle: "Non atopamos coincidencias",
      emptyText: "Proba a cambiar a frase natural ou relaxar algúns filtros.",
      error: "Non se puido executar a busca.",
      partialBands:
        "Espazos e eventos cargados. As bandas non se puideron recuperar.",
      partialRecruitments:
        "Espazos e eventos cargados. As ofertas de bandas non se puideron recuperar."
    },
    map: {
      emptyTitle: "Non hai puntos xeolocalizados para este filtro",
      emptyText: "Mantemos a estrutura do mapa mentres segues afinando a busca.",
      placeholder: "Non se puido cargar a vista mapa neste momento.",
      popupAction: "Abrir"
    },
    chips: {
      people: "{value} persoas",
      budget: "Ata {value} EUR",
      dateRange: "{from} - {to}",
      today: "Hoxe",
      tomorrow: "Mañá"
    },
    spaceTypeLabels: {
      REHEARSAL_ROOM: "Local de ensaio",
      RECORDING_STUDIO: "Estudo de gravación",
      CONCERT_HALL: "Sala de actuación",
      CLASSROOM: "Aula",
      MULTIPURPOSE: "Sala polivalente",
      OTHER: "Espazo musical"
    },
    eventTypeLabels: {
      CONCERT: "Concerto",
      FESTIVAL: "Festival",
      WORKSHOP: "Workshop",
      OPEN_MIC: "Open mic",
      JAM_SESSION: "Jam session",
      SHOWCASE: "Showcase",
      OTHER: "Evento"
    },
    recruitmentLevelLabels: {
      BEGINNER: "Nivel inicial",
      INTERMEDIATE: "Nivel intermedio",
      ADVANCED: "Nivel avanzado",
      PROFESSIONAL: "Nivel profesional"
    },
    cards: {
      space: {
        available: "Disponible",
        capacity: "{value} persoas",
        squareMeters: "{value} m2",
        soundproofed: "Insonorizado",
        notSoundproofed: "Sen insonorizar",
        unknownSound: "Por revisar",
        estimatedPriceLabel: "Prezo estimado",
        price: "Desde {value} EUR",
        cta: "Ver detalle"
      },
      event: {
        capacity: "Aforo {value}",
        price: "{value} EUR",
        cta: "Ver evento"
      },
      band: {
        badge: "Banda",
        cta: "Ver banda"
      },
      recruitment: {
        bandFallback: "Banda",
        roleFallback: "Rol aberto",
        vacancies: "{value} vacantes",
        match: "Match {value}%",
        cta: "Ver oferta"
      }
    }
  },
  spaceDetail: {
    states: {
      loading: "Cargando espazo...",
      loadingSection: "Cargando contido...",
      error: "Non se puido cargar o detalle do espazo.",
      errorTitle: "Non foi posible abrir este espazo",
      partialData: "O espazo principal cargouse, pero faltan algunhas seccións secundarias."
    },
    header: {
      eyebrow: "Espazo musical",
      back: "Volver",
      capacity: "{value} persoas",
      soundproofed: "Insonorizado",
      notSoundproofed: "Sen insonorizar",
      squareMeters: "{value} m2"
    },
    media: {
      placeholder: "Imaxe non dispoñible",
      addFavorite: "Engadir a favoritos",
      removeFavorite: "Quitar de favoritos",
      favoriteAdded: "Espazo gardado en favoritos.",
      favoriteRemoved: "Espazo eliminado de favoritos.",
      favoriteError: "Non se puideron actualizar os favoritos."
    },
    quickServices: {
      eyebrow: "Servizos rápidos",
      title: "O esencial do espazo",
      items: {
        wifi: "WiFi incluido",
        parking: "Parking cercano",
        rest: "Zona de descanso",
        access: "Acceso adaptado"
      }
    },
    description: {
      eyebrow: "Descrición",
      title: "Como e este espazo",
      empty: "Aínda non hai descrición dispoñible para este espazo."
    },
    info: {
      eyebrow: "Información útil",
      title: "Ficha do espazo",
      items: {
        spaceType: "Tipo de espazo",
        capacity: "Capacidade maxima",
        squareMeters: "Superficie",
        opening: "Apertura estimada",
        closing: "Peche estimado",
        status: "Estado para a data elixida",
        address: "Dirección completa",
        hourlyPrice: "Prezo estimado por hora"
      },
      capacityValue: "{value} asistentes",
      squareMetersValue: "{value} m2",
      availableNow: "Disponible",
      unavailableNow: "Sen dispoñibilidade",
      notAvailable: "Non dispoñible"
    },
    equipment: {
      eyebrow: "Equipamento",
      title: "Incluido no espazo",
      count: "{value} elementos",
      quantity: "{value} uds.",
      empty: "Non hai equipamento rexistrado aínda.",
      error: "Non se puido cargar o equipamento.",
      unknown: "Equipo sen nome",
      states: {
        AVAILABLE: "Disponible",
        LIMITED: "Uso limitado",
        OUT_OF_SERVICE: "Fora de servizo"
      }
    },
    availability: {
      eyebrow: "Dispoñibilidade",
      title: "Franxas dispoñibles",
      fullSchedule: "Ver horarios completos",
      fullScheduleTitle: "Horarios completos",
      fullScheduleLoading: "Cargando horarios...",
      fullScheduleEmpty: "Este espazo aínda non ten horarios publicados.",
      fullScheduleError: "Non se puideron cargar os horarios.",
      fullScheduleClose: "Pechar horarios",
      scheduleDay: "Día",
      scheduleTime: "Horario",
      schedulePrice: "Prezo",
      date: "Data",
      quickDates: "Próximos días",
      loading: "Consultando dispoñibilidade...",
      loadingHint: "Cruzando horarios, excepcions e reservas existentes.",
      empty: "Non hai franxas libres para esta data.",
      emptySummary: "Sen franxas dispoñibles",
      emptyHint: "Proba con outro dia ou revisa os horarios completos do espazo.",
      availableSummary: "{count} franxas libres",
      availableWithBookedSummary: "{free} franxas libres · {booked} reservas",
      bookedOnlySummary: "{count} reservas para esta data",
      bookedOnlyHint: "Todas as franxas publicadas para esta data teñen reservas ou bloqueos.",
      selectedSummary: "Franxa seleccionada: {range}",
      firstSlotSummary: "Primeira franxa libre desde as {time}",
      bookedTitle: "Reservas existentes",
      bookedDetail: "Estes tramos xa están ocupados para a data seleccionada.",
      errorSummary: "Dispoñibilidade non dispoñible",
      errorHint: "Intentalo de novo ou consulta os horarios completos.",
      today: "Hoxe",
      tomorrow: "Mañá",
      chooseSlot: "Escoller franxa",
      selectedSlot: "Seleccionada",
      priceFrom: "Desde {price}",
      periods: {
        morning: "Mañá",
        afternoon: "Tarde",
        evening: "Noite",
        night: "Madrugada"
      },
      error: "Non se puido cargar a dispoñibilidade."
    },
    reviews: {
      eyebrow: "Reseñas",
      title: "Opinions doutros usuarios",
      count: "{value} reseñas",
      empty: "Aínda non hai reseñas publicadas.",
      error: "Non se puideron cargar as reseñas.",
      noComment: "Sen comentario adicional.",
      anonymous: "Usuario FIOS",
      metrics: {
        sound: "Son",
        equipment: "Equipo",
        cleanliness: "Limpeza",
        location: "Localización"
      }
    },
    map: {
      eyebrow: "Localización",
      title: "Onde está",
      empty: "Localización non dispoñible",
      noAddress: "Sen dirección detallada"
    },
    booking: {
      eyebrow: "Reserva",
      title: "Reserva este espazo",
      date: "Data",
      startTime: "Hora de inicio",
      duration: "Duracion",
      attendees: "Asistentes",
      subtotal: "Subtotal",
      total: "Total",
      submit: "Reservar agora",
      loginToReserve: "Inicia sesión para reservar",
      submitting: "Creando reserva...",
      selectSlot: "Selecciona unha franxa",
      noReviews: "Sen reseñas",
      priceRange: "De {from} a {to}/h",
      priceSingle: "{value}/h",
      success: "Reserva creada correctamente. A dispoñibilidade actualizouse.",
      durationHours: "{hours} h",
      durationMinutes: "{minutes} min",
      durationMixed: "{hours} h {minutes} min",
      errors: {
        slotRequired: "Selecciona unha franxa dispoñible antes de reservar.",
        durationRequired: "Selecciona unha duración válida para a sesión.",
        attendees: "O número de asistentes non é válido para este espazo.",
        generic: "Non se puido crear a reserva."
      }
    },
    ownerActions: {
      eyebrow: "Xestión do espazo",
      title: "Horarios, excepcións e equipamento",
      manageAvailability: "Xestionar dispoñibilidade",
      manageEquipment: "Xestionar equipamento"
    },
    spaceTypeLabels: {
      REHEARSAL_ROOM: "Local de ensaio",
      RECORDING_STUDIO: "Estudo de gravación",
      CONCERT_HALL: "Sala de actuación",
      CLASSROOM: "Aula",
      MULTIPURPOSE: "Sala polivalente",
      OTHER: "Espazo musical"
    }
  },
  spaceEquipmentManage: {
    header: {
      eyebrow: "Equipamento",
      title: "Xestionar equipamento"
    },
    states: {
      loading: "Cargando equipamento...",
      error: "Non se puido cargar o equipamento.",
      saveError: "Non se puido gardar o equipamento.",
      deleteError: "Non se puido eliminar o equipamento.",
      empty: "Este espazo aínda non ten equipamento asociado.",
      catalogEmpty: "O catálogo de equipamento está baleiro.",
      allAssigned: "Todo o equipamento do catálogo xa está asociado a este espazo."
    },
    form: {
      eyebrow: "Catálogo",
      addTitle: "Engadir equipamento",
      editTitle: "Editar equipamento",
      equipment: "Equipo",
      selectEquipment: "Selecciona un equipo",
      customEquipmentOption: "Outro material",
      customEquipment: "Nome do material",
      customEquipmentPlaceholder: "Pedalboard, atril extra, pantalla 4x12...",
      quantity: "Cantidade",
      state: "Estado",
      observations: "Observacións",
      observationsPlaceholder: "Notas internas visibles no detalle do espazo",
      customObservationsPlaceholder: "Describe marca, estado, uso ou calquera detalle necesario",
      requiredForCustom: "Obrigatorio para Outro"
    },
    list: {
      eyebrow: "Equipamento actual",
      title: "Asociacións do espazo"
    },
    actions: {
      add: "Engadir equipamento",
      adding: "Engadindo...",
      update: "Gardar cambios",
      updating: "Gardando...",
      edit: "Editar",
      delete: "Eliminar",
      reload: "Recargar",
      cancel: "Cancelar",
      cancelEdit: "Cancelar edición",
      confirmDelete: "Eliminar equipamento"
    },
    notices: {
      added: "Equipamento engadido ao espazo.",
      updated: "Equipamento actualizado correctamente.",
      deleted: "Equipamento eliminado do espazo."
    },
    validation: {
      equipment: "Selecciona un equipo do catálogo.",
      customEquipment: "Indica o nome do material.",
      customObservations: "Describe o material en observacións.",
      quantity: "Introduce unha cantidade válida.",
      state: "Selecciona un estado válido.",
      duplicate: "Este equipamento xa está asociado ao espazo."
    },
    confirm: {
      eyebrow: "Confirmación",
      deleteTitle: "Eliminar equipamento",
      deleteText: "Vas eliminar {name} deste espazo. Continuar?"
    },
    categories: {
      INSTRUMENT: "Instrumento",
      SOUND: "Son",
      LIGHTING: "Iluminación",
      RECORDING: "Gravación",
      FURNITURE: "Mobiliario",
      ACCESSORY: "Accesorio",
      OTHER: "Outro"
    }
  },
  spaceAvailabilityManage: {
    header: {
      back: "Volver aos meus espazos",
      eyebrow: "Dispoñibilidade",
      detail: "Ver detalle"
    },
    states: {
      loading: "Cargando dispoñibilidade...",
      errorTitle: "Non foi posible abrir esta xestión",
      error: "Non se puido cargar a xestión de dispoñibilidade.",
      noPermissionTitle: "Sen permisos",
      noPermission: "Só a persoa propietaria do espazo ou unha administradora poden xestionar esta dispoñibilidade.",
      inactiveTitle: "Espazo inactivo",
      inactive: "Non se pode modificar a dispoñibilidade dun espazo inactivo.",
      notApprovedTitle: "Pendente de aprobación",
      notApproved: "A xestión de dispoñibilidade móstrase cando o espazo está aprobado."
    },
    summary: {
      schedules: "Horarios",
      exceptions: "Excepcións",
      blocked: "Bloqueos",
      custom: "Personalizadas"
    },
    schedules: {
      eyebrow: "Horario habitual",
      formTitle: "Editar horario",
      listTitle: "Horarios configurados",
      empty: "Aínda non hai horarios habituais."
    },
    exceptions: {
      eyebrow: "Excepcións",
      formTitle: "Editar excepción",
      listTitle: "Excepcións configuradas",
      empty: "Aínda non hai excepcións.",
      noPrice: "Non aplica"
    },
    fields: {
      dayOfWeek: "Día",
      startTime: "Inicio",
      endTime: "Fin",
      price: "Prezo",
      timeRange: "Horario",
      date: "Data",
      exceptionType: "Tipo",
      reason: "Motivo"
    },
    actions: {
      cancelEdit: "Cancelar edición",
      edit: "Editar",
      delete: "Eliminar",
      deleting: "Eliminando...",
      saving: "Gardando...",
      saveSchedule: "Gardar horario",
      createSchedule: "Engadir horario",
      saveException: "Gardar excepción",
      createException: "Engadir excepción",
      refresh: "Actualizar"
    },
    exceptionTypes: {
      BLOCKED: "Bloqueo puntual",
      CUSTOM_AVAILABILITY: "Dispoñibilidade personalizada"
    },
    calculated: {
      eyebrow: "Resultado",
      title: "Dispoñibilidade calculada",
      loading: "Actualizando dispoñibilidade...",
      error: "Non se puido calcular a dispoñibilidade.",
      availableTitle: "Franxas libres",
      bookedTitle: "Reservas que bloquean",
      emptySlots: "Non hai franxas libres para esta data.",
      emptyBooked: "Non hai reservas activas para esta data."
    },
    validation: {
      scheduleDay: "Selecciona un día válido.",
      timeRange: "A hora de inicio debe ser anterior á hora de fin.",
      schedulePrice: "Indica un prezo maior que cero.",
      exceptionDate: "Selecciona unha data válida.",
      exceptionType: "Selecciona un tipo de excepción válido.",
      customPrice: "A dispoñibilidade personalizada precisa un prezo maior que cero."
    },
    notices: {
      scheduleCreated: "Horario engadido correctamente.",
      schedulesCreated: "{count} horarios engadidos correctamente.",
      scheduleUpdated: "Horario actualizado correctamente.",
      scheduleDeleted: "Horario eliminado correctamente.",
      scheduleError: "Non se puido gardar o horario.",
      scheduleDeleteError: "Non se puido eliminar o horario.",
      exceptionCreated: "Excepción engadida correctamente.",
      exceptionUpdated: "Excepción actualizada correctamente.",
      exceptionDeleted: "Excepción eliminada correctamente.",
      exceptionError: "Non se puido gardar a excepción.",
      exceptionDeleteError: "Non se puido eliminar a excepción."
    },
    confirm: {
      deleteSchedule: "Vas eliminar este horario habitual.",
      deleteException: "Vas eliminar esta excepción de dispoñibilidade."
    }
  },
  spaceList: {
    header: {
      eyebrow: "Catálogo FIOS",
      title: "Espazos musicais",
      subtitle: "Atopa salas, estudios e locais para o teu proxecto.",
      explore: "Explorar",
      mine: "Os meus espazos",
      create: "Crear espazo"
    },
    stats: {
      total: "Visibles",
      approved: "Aprobados",
      active: "Activos",
      cities: "Cidades",
      types: "Tipos"
    },
    filters: {
      search: "Buscar",
      searchPlaceholder: "Buscar por nome, cidade ou tipo de espazo",
      city: "Cidade",
      allCities: "Todas as cidades",
      type: "Tipo de espazo",
      allTypes: "Todos os tipos",
      minCapacity: "Aforo mínimo",
      soundproofed: "Insonorizado",
      allSoundproofed: "Calquera opcion",
      soundproofedYes: "Si",
      soundproofedNo: "Non",
      order: "Ordenar por",
      clear: "Limpar filtros",
      orderOptions: {
        rating: "Mellor valorados",
        capacity: "Maior capacidade",
        city: "Cidade",
        name: "Nome"
      }
    },
    list: {
      eyebrow: "Catálogo real",
      exploreTitle: "Explora espazos dispoñibles",
      mineTitle: "Xestiona os teus espazos",
      total: "{count} espazos"
    },
    card: {
      imagePlaceholder: "Imaxe non dispoñible",
      locationFallback: "Localización pendente",
      capacityValue: "{value} persoas",
      squareMeters: "Superficie",
      squareMetersValue: "{value} m2",
      soundproofed: "Insonorización",
      soundproofedYes: "Insonorizado",
      soundproofedNo: "Sen insonorizar",
      soundproofedUnknown: "Por revisar",
      rating: "Valoración",
      ratingValue: "{rating} ({count})",
      noRating: "Sen reseñas",
      notSpecified: "Sen dato",
      viewDetail: "Ver detalle",
      manageAvailability: "Xestionar dispoñibilidade",
      edit: "Editar",
      deactivate: "Desactivar",
      deactivating: "Desactivando..."
    },
    empty: {
      exploreTitle: "Non hai espazos públicos dispoñibles",
      exploreText:
        "Cando haxa espazos aprobados na plataforma, aparecerán aquí con acceso ao detalle.",
      mineTitle: "Aínda non publicaches espazos",
      mineText:
        "Crea o teu primeiro espazo para xestionalo desde esta pantalla e seguir o seu estado.",
      filteredTitle: "Non hai resultados con estes filtros",
      filteredText: "Proba a cambiar a busca ou relaxar algún filtro para recuperar resultados.",
      resetAction: "Restablecer filtros",
      createAction: "Crear espazo"
    },
    form: {
      createEyebrow: "Novo espazo",
      createTitle: "Crear espazo musical",
      editEyebrow: "Editar espazo",
      editTitle: "Actualizar espazo musical",
      intro: "Completa unha ficha simple para publicar ou actualizar o teu espazo.",
      pendingHint: "O espazo pode quedar pendente de aprobación antes de publicarse.",
      name: "Nome",
      description: "Descrición",
      spaceType: "Tipo de espazo",
      capacity: "Aforo",
      squareMeters: "Superficie",
      soundproofed: "Insonorizado",
      mainImage: "Imaxe principal",
      country: "País",
      province: "Provincia",
      city: "Cidade",
      street: "Rúa",
      portal: "Portal",
      floor: "Piso",
      postalCode: "Código postal",
      coordinates: "Coordenadas",
      coordinatesPlaceholder: "43.3623, -8.4115",
      coordinatesHint: "Pega as coordenadas xuntas ou unha ligazón de mapa.",
      coordinatesAction: "Aplicar",
      coordinatesApplied: "Coordenadas engadidas.",
      coordinatesError: "Non foi posible recoñecer esas coordenadas.",
      latitude: "Latitude",
      longitude: "Lonxitude",
      cancel: "Cancelar",
      save: "Gardar cambios",
      create: "Crear espazo",
      saving: "Gardando...",
      creating: "Creando...",
      validation: "Completa nome, tipo, aforo, superficie e dirección obrigatoria antes de gardar.",
      submitError: "Non se puido gardar o espazo."
    },
    status: {
      APPROVED: "Aprobado",
      PENDING: "Pendente",
      REJECTED: "Rexeitado",
      INACTIVE: "Inactivo"
    },
    notices: {
      privateFallback: "Debes iniciar sesión para ver os teus espazos. Amósase o catálogo público.",
      editLoadError: "Non se puido cargar o detalle do espazo para editalo.",
      updated: "O espazo actualizouse correctamente.",
      createdPending: "O espazo creouse e queda pendente de aprobación.",
      deactivated: "O espazo desactivouse correctamente.",
      deactivateError: "Non se puido desactivar o espazo."
    },
    confirm: {
      deactivate: 'Vas desactivar "{name}".'
    },
    states: {
      loading: "Cargando espazos...",
      errorTitle: "Non foi posible abrir os espazos",
      error: "Non se puido cargar a listaxe de espazos.",
      retry: "Reintentar"
    }
  },
  reservations: {
    header: {
      title: "As miñas reservas",
      mineTitle: "As miñas reservas",
      receivedTitle: "Reservas recibidas",
      mineSubtitle: "Controla as próximas sesións, cambios, mensaxes e reservas xa pechadas.",
      receivedSubtitle: "Xestiona solicitudes dos teus espazos e marca como completadas as sesións que xa pasaron.",
      mine: "As miñas",
      received: "Recibidas",
      modeLabel: "Tipo de reservas",
      new: "Nova reserva"
    },
    states: {
      loading: "Cargando reservas...",
      error: "Non se puideron cargar as túas reservas.",
      errorTitle: "Non foi posible abrir as túas reservas",
      cancelling: "Cancelando...",
      updatingState: "Actualizando..."
    },
    stats: {
      active: "Reservas activas",
      upcoming: "Próximas reservas",
      completed: "Reservas completadas",
      cancelled: "Reservas canceladas"
    },
    upcoming: {
      eyebrow: "A túa próxima reserva",
      fallback: "A túa próxima reserva",
      today: "A túa próxima reserva é hoxe",
      tomorrow: "A túa próxima reserva é mañá",
      inDays: "A túa próxima reserva é en {count} días"
    },
    timing: {
      today: "Hoxe",
      tomorrow: "Mañá",
      inDays: "En {count} días",
      past: "Xa pasou",
      needsClosure: "Pendente de peche",
      expiredPending: "Pendente vencida"
    },
    list: {
      eyebrow: "Seguimento",
      title: "Todas as reservas",
      receivedEyebrow: "Xestión de espazos",
      receivedTitle: "Reservas recibidas",
      total: "{count} reservas"
    },
    filters: {
      searchPlaceholder: "Buscar por nome do espazo ou cidade",
      date: "Data",
      sessionType: "Tipo de sesión",
      allSessionTypes: "Todos os tipos",
      order: "Orde",
      clear: "Limpar filtros",
      orderOptions: {
        nearest: "Data máis próxima",
        farthest: "Data máis lonxana",
        priceDesc: "Prezo maior",
        priceAsc: "Prezo menor"
      }
    },
    tabs: {
      all: "Todos",
      active: "Activas",
      pending: "Pendentes",
      completed: "Completadas",
      cancelled: "Canceladas"
    },
    cards: {
      fallbackSpace: "Espazo musical",
      noLocation: "Localización pendente",
      attendeesValue: "{count} asistentes",
      totalPrice: "Prezo total"
    },
    actions: {
      view: "Ver reserva",
      contact: "Contactar",
      modify: "Modificar",
      cancel: "Cancelar",
      accept: "Aceptar",
      reject: "Rexeitar",
      complete: "Completar",
      rebook: "Reservar de novo",
      viewReason: "Ver motivo",
      retry: "Tentar outra vez"
    },
    empty: {
      title: "Aínda non tes reservas",
      text: "Cando peches unha sesión desde un espazo musical, aparecerá aquí co seu estado, horario e accións dispoñibles.",
      action: "Explorar espazos",
      receivedTitle: "Aínda non recibiches reservas",
      receivedText:
        "Engade un espazo musical para que outras persoas poidan reservalo. Cando chegue unha solicitude, aparecerá aquí co seu estado, horario e accións dispoñibles.",
      receivedAction: "Engadir espazo",
      filteredTitle: "Non hai resultados con estes filtros",
      filteredText: "Proba a cambiar a data, o texto de busca ou o estado para recuperar reservas."
    },
    detail: {
      eyebrow: "Detalle da reserva",
      notes: "Notas",
      cancellationReason: "Motivo da cancelación",
      close: "Pechar",
      fields: {
        space: "Espazo",
        location: "Localización",
        date: "Data",
        schedule: "Horario",
        status: "Estado",
        sessionType: "Tipo de sesión",
        attendees: "Asistentes",
        price: "Prezo",
        user: "Usuario",
        band: "Banda",
        createdAt: "Creada o"
      }
    },
    edit: {
      eyebrow: "Editar reserva",
      sessionDate: "Data",
      startTime: "Hora de inicio",
      endTime: "Hora de fin",
      attendees: "Asistentes",
      sessionType: "Tipo de sesión",
      notes: "Notas",
      cancel: "Cancelar",
      save: "Gardar cambios",
      saving: "Gardando...",
      updated: "A reserva actualizouse correctamente.",
      error: "Non se puido actualizar a reserva.",
      validationRequired: "Indica data, hora de inicio e hora de fin.",
      validationTimeOrder: "A hora de inicio debe ser anterior a hora de fin.",
      validationAttendees: "Debe haber polo menos un asistente.",
      validationCapacity: "O espazo admite como máximo {capacity} asistentes."
    },
    cancel: {
      prompt: "Indica o motivo da cancelación",
      eyebrow: "Cancelación",
      title: "Cancelar reserva",
      text: "Vas cancelar a reserva de {name}.",
      reasonLabel: "Motivo da cancelación",
      reasonPlaceholder: "Explica brevemente por que cancelas esta reserva.",
      close: "Volver",
      confirm: "Cancelar reserva",
      submitting: "Cancelando...",
      emptyReason: "Debes indicar un motivo para cancelar a reserva.",
      success: "A reserva cancelouse correctamente.",
      error: "Non se puido cancelar a reserva."
    },
    stateAction: {
      eyebrow: "Xestión de reserva",
      close: "Pechar confirmación",
      cancel: "Volver",
      invalid: "Esta acción non está dispoñible para o estado actual da reserva.",
      error: "Non se puido actualizar o estado da reserva.",
      ACCEPTED: {
        title: "Aceptar reserva",
        text: "Vas aceptar a reserva de {name}.",
        confirm: "Aceptar reserva",
        success: "A reserva aceptouse correctamente."
      },
      REJECTED: {
        title: "Rexeitar reserva",
        text: "Vas rexeitar a reserva de {name}.",
        confirm: "Rexeitar reserva",
        success: "A reserva rexeitouse correctamente."
      },
      COMPLETED: {
        title: "Marcar como completada",
        text: "Vas marcar como completada a reserva de {name}.",
        confirm: "Completar reserva",
        success: "A reserva marcouse como completada."
      }
    },
    placeholders: {
      notFound: "Non se atopou a reserva solicitada.",
      rebookUnavailable: "Non se atopou o espazo orixinal para repetir esta reserva."
    },
    sessionTypes: {
      REHEARSAL: "Ensaio",
      RECORDING: "Gravación",
      CLASS: "Clase",
      EVENT_PREPARATION: "Preparación de evento",
      OTHER: "Outra sesión"
    },
    statuses: {
      PENDING: "Pendente",
      ACCEPTED: "Aceptada",
      COMPLETED: "Completada",
      CANCELLED: "Cancelada",
      REJECTED: "Rexeitada"
    }
  },
  messages: {
    header: {
      eyebrow: "Mensaxes FIOS",
      title: "Mensaxes",
      subtitle: "Conversa con espazos e usuarios sobre as túas reservas",
      unread: "{count} mensaxes sen ler"
    },
    states: {
      error: "Non se puideron cargar as túas conversas.",
      reservationUnavailable: "Non tes acceso a esa reserva ou non existe na túa bandexa.",
      openFailed: "Non se puido abrir a conversa solicitada."
    },
    list: {
      title: "Conversas",
      summary: "{count} conversas",
      searchPlaceholder: "Buscar por espazo, cidade, estado ou texto",
      emptyTitle: "Aínda non tes mensaxes",
      emptyText:
        "As túas conversas sobre reservas aparecerán aquí en canto exista polo menos unha reserva vinculada.",
      fallbackTitle: "Reserva FIOS",
      fallbackParticipant: "Usuario FIOS",
      noMessagesYet: "Aínda non hai mensaxes nesta conversa."
    },
    filters: {
      scope: "Tipo de conversa",
      allConversations: "Todas",
      myReservations: "Miñas",
      managedReservations: "Recibidas",
      status: "Estado da reserva",
      allStatuses: "Todas"
    },
    chat: {
      back: "Conversas",
      viewReservation: "Ver reserva",
      emptyTitle: "Aínda non hai mensaxes",
      emptyText: "Escribe a primeira mensaxe para iniciar esta conversa.",
      emptyInfo: "Esta reserva aínda non ten mensaxes. Podes iniciar a conversa agora.",
      placeholderTitle: "Selecciona unha conversa",
      placeholderText: "Escolle unha conversa da lista para ler as mensaxes e responder.",
      composerPlaceholder: "Escribe unha mensaxe...",
      send: "Enviar",
      sending: "Enviando...",
      sendError: "Non se puido enviar a mensaxe.",
      error: "Non se puideron cargar as mensaxes desta reserva.",
      locked: "Só podes enviar mensaxes en reservas pendentes ou aceptadas.",
      fallbackAuthor: "Usuario FIOS"
    },
    dates: {
      today: "Hoxe",
      yesterday: "Onte"
    }
  },
  events: {
    header: {
      eyebrow: "Axenda FIOS",
      title: "Eventos",
      subtitle: "Descubre concertos, sesións e actividades musicais"
    },
    actions: {
      list: "Lista",
      map: "Mapa",
      importTicketmaster: "Importar desde Ticketmaster",
      createEvent: "Crear evento",
      retry: "Tentar outra vez",
      view: "Ver evento",
      edit: "Editar",
      archive: "Arquivar",
      archiving: "Arquivando...",
        backToList: "Ver eventos",
      ticketmaster: "Ver en Ticketmaster",
      externalLink: "Abrir ligazón externa",
      viewSpace: "Ver espazo musical",
      viewBand: "Ver banda",
      cancel: "Cancelar",
      saveChanges: "Gardar cambios"
    },
    states: {
      loading: "Cargando eventos...",
      loadingDetail: "Cargando detalle do evento...",
      error: "Non se puido cargar o listado de eventos.",
      detailError: "Non se puido cargar o detalle do evento.",
      errorTitle: "Non foi posible abrir este evento"
    },
    list: {
      eyebrow: "Axenda publicada",
      title: "Explora eventos",
      total: "{count} eventos visibles"
    },
    quickTabs: {
      upcoming: "Próximos",
      today: "Hoxe",
      week: "Esta semana",
      free: "Gratis",
      external: "Externos"
    },
    filters: {
      search: "Busca",
      searchPlaceholder: "Buscar por título, cidade, lugar ou xénero",
      city: "Cidade",
      cityPlaceholder: "Santiago, Vigo, A Coruña...",
      date: "Data",
      genre: "Xénero musical",
      genrePlaceholder: "Rock, jazz, folk...",
      type: "Tipo de evento",
      allTypes: "Todos os tipos",
      source: "Orixe",
      allSources: "Todas as orixes",
      order: "Ordenar",
      freeOnly: "Só gratis",
      clear: "Limpar filtros",
      apply: "Aplicar filtros",
      orderOptions: {
        nearest: "Próximos primeiro",
        farthest: "Data máis lonxana",
        priceAsc: "Prezo menor",
        priceDesc: "Prezo maior"
      }
    },
    empty: {
      title: "Aínda non hai eventos publicados",
      text: "Cando exista axenda activa, verala aquí con filtros, mapa e acceso ao detalle.",
      action: "Restablecer vista",
      filteredTitle: "Non hai resultados con estes filtros",
      filteredText: "Proba a relaxar a busca, cambiar a data ou revisar a orixe do evento."
    },
    map: {
      loading: "Cargando mapa de eventos...",
      emptyTitle: "Non hai eventos xeolocalizados para este filtro",
      emptyText: "Podes volver á vista lista ou axustar cidade e data para atopar máis puntos.",
      error: "Non se puido cargar a vista mapa."
    },
    cards: {
      imageFallback: "Imaxe non dispoñible",
      locationFallback: "Localización pendente",
      capacity: "Aforo {value}",
      onRequest: "Consultar prezo"
    },
    types: {
      CONCERT: "Concerto",
      FESTIVAL: "Festival",
      WORKSHOP: "Workshop",
      OPEN_MIC: "Open mic",
      JAM_SESSION: "Jam session",
      SHOWCASE: "Showcase",
      OTHER: "Evento"
    },
    sources: {
      INTERNAL: "FIOS",
      EXTERNAL: "Ticketmaster"
    },
    statuses: {
      DRAFT: "Pendente",
      PUBLISHED: "Publicado",
      CANCELLED: "Cancelado",
      ARCHIVED: "Arquivado"
    },
    admin: {
      createEyebrow: "Novo evento",
      createTitle: "Crear evento",
      editEyebrow: "Editar evento",
      editTitle: "Actualizar evento",
      saving: "Gardando...",
      validation: "Completa título, data, lugar, cidade e país antes de gardar.",
      capacityValidation: "Indica un aforo maior ca cero para os eventos internos.",
      priceValidation: "O prezo da entrada non pode ser negativo.",
      created: "O evento creouse correctamente.",
      updated: "O evento actualizouse correctamente.",
      deleted: "O evento arquivouse correctamente.",
      error: "Non se puido gardar o evento.",
      deleteConfirm: "Queres arquivar este evento?",
      deleteError: "Non se puido arquivar o evento.",
      fields: {
        title: "Título",
        type: "Tipo",
        status: "Estado",
        date: "Data",
        startTime: "Hora inicio",
        endTime: "Hora fin",
        city: "Cidade",
        province: "Provincia",
        country: "País",
        venueName: "Lugar",
        genre: "Xénero musical",
        capacity: "Aforo",
        ticketPrice: "Prezo",
        latitude: "Latitude",
        longitude: "Lonxitude",
        location: "Dirección / localización",
        posterImage: "Imaxe",
        externalUrl: "Ligazón externa",
        description: "Descrición",
        source: "Orixe"
      }
    },
    ticketmaster: {
      eyebrow: "Importacion externa",
      title: "Importar desde Ticketmaster",
      city: "Cidade",
      cityPlaceholder: "A Coruña, Vigo, Santiago, Madrid...",
      keyword: "Artista ou evento",
      keywordPlaceholder: "Rels B, festival, concerto...",
      genre: "Xénero musical",
      genrePlaceholder: "Opcional",
      startDate: "Data inicio",
      endDate: "Data fin",
      countryCode: "País",
      hint: "Deixa a cidade baleira para buscar en toda España; usa cidade cando queiras acoutar unha zona concreta.",
      search: "Buscar",
      searching: "Buscando...",
      import: "Importar",
      importing: "Importando...",
      bulkImport: "Importar {count} resultados",
      bulkImporting: "Importando resultados...",
      empty: "Non se atoparon eventos externos para esta busca.",
      emptyHint: "Proba sen artista ou xénero, ou cambia a cidade se queres descubrir eventos dispoñibles noutra zona.",
      broadenSearch: "Buscar sen artista nin xénero",
      searchAgain: "Buscar outra vez",
      clearKeyword: "Quitar artista",
      clearGenre: "Quitar xénero",
      useSpainAndSearch: "Usar España e buscar",
      activeFilters: "Busca: {filters}",
      noActiveFilters: "Busca ampla",
      countryMismatch: "A cidade semella española, pero o país seleccionado é {country}.",
      countries: {
        ES: "España",
        GB: "Reino Unido",
        US: "Estados Unidos",
        PT: "Portugal",
        FR: "Francia",
        DE: "Alemania",
        IT: "Italia",
        IE: "Irlanda"
      },
      errors: {
        missingKey: "A conexión con Ticketmaster non está lista. Avísalle á persoa responsable.",
        disabled: "A conexión con Ticketmaster está desactivada.",
        timeout: "Ticketmaster está tardando demasiado. Proba de novo nuns segundos.",
        rateLimit: "Ticketmaster limitou temporalmente as buscas. Agarda uns segundos antes de reintentar.",
        provider: "Ticketmaster rexeitou a consulta. Revisa país, cidade e filtros."
      },
      error: "Non se puido consultar Ticketmaster.",
      imported: "Evento importado correctamente.",
      bulkImported: "{count} eventos importados desde Ticketmaster. {existing} xa estaban en FIOS.",
      importError: "Non se puido importar o evento externo."
    },
    tickets: {
      header: {
        back: "Volver",
        eyebrow: "As miñas entradas",
        title: "As túas entradas",
        subtitle: "Consulta as entradas que reservaches en FIOS. O importe págase presencialmente o día do evento.",
        explore: "Ver eventos"
      },
      states: {
        loading: "Cargando as túas entradas...",
        errorTitle: "Non foi posible abrir as túas entradas",
        error: "Non se puideron cargar as túas entradas."
      },
      actions: {
        retry: "Tentar de novo",
        viewEvent: "Ver evento",
        viewEventFor: "Ver evento: {title}"
      },
      stats: {
        label: "Resumo das túas entradas",
        total: "Entradas gardadas",
        upcoming: "Próximos eventos",
        paid: "Importe a pagar"
      },
      list: {
        eyebrow: "Entradas gardadas",
        title: "Lista de entradas",
        total: "{count} entradas"
      },
      card: {
        ticketLabel: "Entrada FIOS",
        cancelledLabel: "Entrada cancelada",
        purchasedAt: "Reservada o {date}",
        date: "Data",
        time: "Hora",
        place: "Lugar",
        amountDue: "Importe a pagar",
        pricePaid: "Importe a pagar",
        paymentNote: "O pagamento realizarase presencialmente o día do evento.",
        cancelledAt: "Renuncia",
        noTime: "Hora pendente",
        locationFallback: "Lugar pendente",
        untitled: "Evento sen título",
        upcoming: "Entrada para un próximo evento",
        past: "Evento xa celebrado",
        cancelled: "Reserva cancelada"
      },
      empty: {
        title: "Aínda non tes entradas",
        text: "Cando reserves unha entrada para un evento de FIOS, aparecerá aquí gardada na túa conta.",
        action: "Ver eventos"
      }
    },
    detail: {
      back: "Volver",
      eyebrow: "Detalle do evento",
      descriptionTitle: "Descrición",
      emptyDescription: "Este evento aínda non ten descrición publicada.",
      noGenre: "Xénero por definir",
      infoEyebrow: "Información",
      infoTitle: "Datos principais",
      linksEyebrow: "Contexto",
      linksTitle: "Ligazóns e relacións",
      mapEyebrow: "Mapa",
      mapTitle: "Localización do evento",
      mapEmpty: "Localización non dispoñible",
      noLocation: "Sen cidade",
      noVenue: "Lugar pendente",
      noAddress: "Sen dirección detallada",
      noSpace: "Sen espazo musical asociado",
      noBand: "Sen banda asociada",
      noCapacity: "Aforo non dispoñible",
      capacityValue: "{value} asistentes",
      reservedValue: "{value} reservadas",
      availableValue: "{value} dispoñibles",
      purchase: {
        eyebrow: "Entrada FIOS",
        title: "Reserva a túa entrada",
        text: "FIOS gardará a túa reserva. Non se realiza ningún pagamento na plataforma.",
        remainingText: "Quedan {count} entradas dispoñibles. Ao reservar, FIOS gardará a túa entrada sen realizar ningún pagamento.",
        savedTitle: "Entrada reservada",
        savedText: "Xa tes unha entrada reservada. Importe a pagar: {price}.",
        paymentNote: "O pagamento realizarase presencialmente o día do evento.",
        soldOutTitle: "Entradas esgotadas",
        soldOutText: "Non quedan entradas dispoñibles para este evento.",
        unavailableTitle: "Reserva non dispoñible",
        unavailableText: "Este evento non permite reservar entradas agora mesmo.",
        buyAction: "Reservar entrada",
        loginAction: "Iniciar sesión para reservar",
        savedAction: "Entrada reservada",
        soldOutAction: "Entradas esgotadas",
        unavailableAction: "Non dispoñible",
        saving: "Reservando entrada...",
        success: "Entrada reservada. Importe a pagar: {price}.",
        error: "Non se puido reservar a entrada.",
        cancelAction: "Renunciar á entrada",
        cancelConfirm: "Vas renunciar a esta entrada. A praza volverá quedar dispoñible.",
        cancelSuccess: "Renunciaches á entrada. A praza volve estar dispoñible.",
        cancelError: "Non se puido renunciar á entrada."
      },
      organizer: {
        eyebrow: "Organización",
        title: "Entradas reservadas",
        loading: "Cargando reservas...",
        empty: "Aínda non hai entradas reservadas.",
        error: "Non se puideron cargar as entradas reservadas.",
        userFallback: "Usuario #{id}"
      },
      fields: {
        date: "Data",
        time: "Horario",
        city: "Cidade",
        venue: "Lugar",
        price: "Prezo",
        capacity: "Aforo",
        reserved: "Reservadas",
        available: "Dispoñibles",
        location: "Dirección",
        space: "Espazo musical",
        band: "Banda"
      }
    }
  },
  bands: {
    header: {
      title: "Bandas",
      mineTitle: "As miñas bandas",
      subtitle: "Explora proxectos musicais activos, xéneros e bandas que buscan integrantes.",
      mineSubtitle: "Xestiona os teus proxectos, membros e buscas abertas desde un único lugar.",
      create: "Crear banda"
    },
    states: {
      loading: "Cargando bandas...",
      error: "Non se puideron cargar as bandas.",
      errorTitle: "Non foi posible abrir as bandas",
      partialData: "Cargáronse as bandas principais, pero faltan algúns datos secundarios.",
      partialMembers: "Non se puideron cargar todos os membros das bandas.",
      savingBand: "Gardando banda...",
      publishingRecruitment: "Publicando busca...",
      publishingEvent: "Enviando evento...",
      closingRecruitment: "Pechando..."
    },
    stats: {
      total: "Total de bandas",
      totalMine: "As miñas bandas",
      totalPublic: "Bandas en FIOS",
      active: "Bandas activas",
      members: "Membros totais",
      recruitments: "Buscas activas",
      genres: "Xéneros activos"
    },
    members: {
      eyebrow: "Membros",
      title: "Equipo principal",
      count: "{count} membros",
      empty: "Esta banda aínda non ten membros visibles.",
      noInstruments: "Sen instrumentos asociados"
    },
    memberManagement: {
      eyebrow: "Membros da banda",
      loading: "Cargando membros...",
      userId: "ID de usuario",
      userSearch: "Buscar usuario",
      userSearchPlaceholder: "Email ou nome",
      userSearchHint: "Escribe e selecciona unha persoa da lista.",
      userSearchMinLength: "Escribe polo menos 2 caracteres.",
      searchingUsers: "Buscando usuarios...",
      userSearchEmpty: "Non hai usuarios dispoñibles con esa busca.",
      userSearchError: "Non se puideron buscar usuarios.",
      selectedUser: "Seleccionado: {name}",
      add: "Engadir membro",
      adding: "Engadindo...",
      removing: "Actualizando...",
      remove: "Quitar",
      empty: "Esta banda aínda non ten membros activos.",
      editPermission: "Só as persoas líderes ou administradoras poden editar bandas.",
      managePermission: "Só as persoas líderes ou administradoras poden xestionar membros.",
      loadError: "Non se puideron cargar os membros.",
      invalidUserId: "Introduce un ID de usuario válido.",
      invalidUserSelection: "Selecciona un usuario da lista.",
      addSuccess: "Membro engadido correctamente.",
      addError: "Non se puido engadir o membro.",
      roleUpdated: "Rol actualizado correctamente.",
      roleError: "Non se puido actualizar o rol.",
      removeConfirm: "Vas quitar a {name} da banda. Continuar?",
      removed: "Membro eliminado da banda.",
      removeError: "Non se puido quitar o membro.",
      userFallback: "Usuario #{id}"
    },
    recruitments: {
      eyebrow: "Buscas",
      title: "Buscas activas",
      publicEyebrow: "Oportunidades",
      publicTitle: "Bandas que buscan membros",
      count: "{count} buscas",
      empty: "Non hai buscas activas para as túas bandas.",
      publicEmpty: "Non hai buscas abertas agora mesmo.",
      vacancyShort: "{count} vac.",
      closed: "A busca pechouse correctamente.",
      closeError: "Non se puido pechar a busca."
    },
    list: {
      eyebrow: "Catálogo persoal",
      title: "Todas as miñas bandas",
      mineEyebrow: "Catálogo persoal",
      mineTitle: "Todas as miñas bandas",
      publicEyebrow: "Catálogo público",
      publicTitle: "Bandas en FIOS",
      total: "{count} bandas"
    },
    views: {
      explore: "Explorar bandas",
      mine: "As miñas bandas"
    },
    public: {
      eyebrow: "Explorar",
      title: "Descubre bandas e proxectos musicais",
      text: "Consulta bandas activas, xéneros, cidades e ofertas abertas sen iniciar sesión.",
      register: "Crear conta",
      login: "Iniciar sesión",
      recruitments: "Ver buscas abertas",
      howEyebrow: "Como usalo",
      howTitle: "Do catálogo a colaboración",
      howText: "Primeiro explora bandas, despois abre o seu detalle e, se encaixa, revisa as súas buscas activas.",
      stepExplore: "Filtra por cidade, xénero ou estado",
      stepOpen: "Abre o detalle de calquera banda",
      stepRecruit: "Consulta ofertas para unirte"
    },
    filters: {
      searchPlaceholder: "Buscar por nome da banda ou cidade",
      genre: "Xénero musical",
      allGenres: "Todos os xéneros",
      status: "Estado",
      allStates: "Todos os estados",
      order: "Ordenar",
      clear: "Limpar filtros",
      orderOptions: {
        recent: "Actividade recente",
        name: "Nome",
        city: "Cidade",
        members: "Máis membros"
      }
    },
    tabs: {
      all: "Todas",
      active: "Activas",
      forming: "En formación",
      recruiting: "Buscando membros"
    },
    actions: {
      viewBand: "Ver banda",
      viewDetail: "Ver detalle",
      manageMembers: "Xestionar membros",
      publishRecruitment: "Publicar busca",
      publishEvent: "Propoñer evento",
      leaveBand: "Saír da banda",
      searchMembers: "Buscar membros",
      edit: "Editar",
      viewRecruitment: "Ver oferta",
      manageRecruitment: "Xestionar",
      closeRecruitment: "Pechar",
      retry: "Tentar outra vez",
      cancel: "Cancelar"
    },
    detail: {
      loading: "Cargando banda...",
      errorTitle: "Non foi posible abrir esta banda",
      error: "Non se puido cargar o detalle da banda.",
      eyebrow: "Detalle da banda",
      imageFallback: "Imaxe da banda non dispoñible",
      viewRecruitments: "Ver buscas activas",
      membersTitle: "Membros da banda",
      recruitmentsTitle: "Buscas abertas",
      heroLead: "{members} en {city}",
      createdAtInline: "Desde {date}",
      activeRecruitment: "Busca aberta",
      roleGuest: "Visitante",
      fields: {
        members: "Membros",
        recruitments: "Buscas",
        createdAt: "Data de alta",
        role: "A túa relación"
      }
    },
    leave: {
      eyebrow: "Membros",
      title: "Saír da banda",
      confirm: 'Tes a certeza de que queres saír de "{name}"?',
      confirmAction: "Saír da banda",
      success: "Saíches da banda correctamente.",
      error: "Non se puido saír da banda."
    },
    cards: {
      imageFallback: "Imaxe da banda non dispoñible",
      noDescription: "Sen descrición dispoñible aínda.",
      noGenre: "Xénero por definir",
      noCity: "Cidade pendente",
      membersValue: "{count} membros"
    },
    empty: {
      title: "Aínda non formas parte de ningunha banda",
      text: "Crea a túa primeira banda para xestionar membros, publicar buscas e concentrar a túa actividade musical en FIOS.",
      publicTitle: "Non hai bandas públicas aínda",
      publicText: "Cando haxa proxectos activos aparecerán aquí xunto coas súas buscas abertas.",
      create: "Crear banda",
      explore: "Explorar bandas",
      filteredTitle: "Non hai bandas que coincidan con estes filtros",
      filteredText: "Proba a cambiar o xénero, o estado ou o texto de busca."
    },
    modals: {
      band: {
        createEyebrow: "Nova banda",
        createTitle: "Crear banda",
        editEyebrow: "Editar banda",
        editTitle: "Actualizar banda",
        name: "Nome",
        genre: "Xénero principal",
        city: "Cidade base",
        image: "Imaxe",
        description: "Descrición",
        submit: "Crear banda",
        update: "Gardar cambios",
        validation: "Completa nome, xénero e cidade antes de gardar.",
        created: "A banda creouse correctamente.",
        updated: "A banda actualizouse correctamente.",
        error: "Non se puido gardar a banda."
      },
      recruitment: {
        eyebrow: "Nova busca",
        title: "Publicar busca de membros",
        band: "Banda",
        selectBand: "Selecciona unha banda",
        instrument: "Instrumento",
        selectInstrument: "Selecciona un instrumento",
        positionTitle: "Título",
        role: "Rol buscado",
        level: "Nivel requirido",
        city: "Cidade",
        vacancies: "Vacantes",
        description: "Descrición",
        submit: "Publicar busca",
        validation:
          "Completa banda, instrumento, título, rol, cidade e vacantes antes de publicar.",
        created: "A busca publicouse correctamente.",
        error: "Non se puido publicar a busca."
      },
      event: {
        eyebrow: "Axenda da banda",
        title: "Propoñer evento",
        band: "Banda",
        selectBand: "Selecciona unha banda",
        musicalSpace: "Espazo musical",
        noMusicalSpace: "Sen espazo asociado",
        eventTitle: "Título",
        type: "Tipo",
        date: "Data",
        startTime: "Hora de inicio",
        endTime: "Hora de fin",
        venueName: "Lugar",
        city: "Cidade",
        province: "Provincia",
        country: "País",
        genre: "Xénero musical",
        capacity: "Aforo",
        ticketPrice: "Prezo",
        location: "Enderezo",
        posterImage: "Imaxe do cartel",
        description: "Descrición",
        submit: "Enviar a revisión",
        validation: "Completa banda, título, tipo, data, lugar, cidade e país antes de enviar.",
        timeValidation: "A hora de inicio debe ser anterior á hora de fin.",
        created: "O evento queda pendente de aprobación do administrador.",
        error: "Non se puido enviar o evento."
      }
    },
    placeholders: {
      noLeaderBand: "Necesitas ser responsable de polo menos unha banda para publicar unha busca.",
      noLeaderEventBand: "Necesitas ser responsable dunha banda activa para propoñer un evento."
    },
    statuses: {
      active: "Activa",
      inactive: "Inactiva",
      forming: "En formación",
      recruiting: "Buscando membros"
    },
    levels: {
      BEGINNER: "Inicial",
      INTERMEDIATE: "Intermedio",
      ADVANCED: "Avanzado",
      PROFESSIONAL: "Profesional"
    },
    roles: {
      LEADER: "Responsable",
      MEMBER: "Membro"
    }
  },
  admin: {
    header: {
      eyebrow: "Administración FIOS",
      title: "Panel de administración",
      subtitle:
        "Supervisa usuarios, espazos, reservas, reseñas e eventos desde un único lugar.",
      roleBadge: "Só administración"
    },
    actions: {
      refreshOverview: "Actualizar resumo",
      refresh: "Recargar",
      retry: "Reintentar",
      resetFilters: "Limpar filtros",
      close: "Pechar"
    },
    sections: {
      navigation: "Navegación do panel de xestión",
      overview: "Resumo",
      users: "Usuarios",
      spaces: "Espazos",
      reservations: "Reservas",
      reviews: "Reseñas",
      events: "Eventos",
      recruitments: "Buscas de membros"
    },
    summary: {
      visibleOfTotal: "{visible} de {total}"
    },
    forbidden: {
      title: "Acceso restrinxido",
      text: "Estas pantallas só están dispoñibles para contas con permiso de administración.",
      action: "Volver ao inicio"
    },
    confirm: {
      eyebrow: "Confirmación"
    },
    badges: {
      active: "Activo",
      inactive: "Inactivo",
      inactiveUsers: "Usuarios inactivos"
    },
    roles: {
      ADMIN: "Administrador",
      USER: "Usuario"
    },
    approvalStatuses: {
      PENDING: "Pendente",
      APPROVED: "Aprobado",
      REJECTED: "Rexeitado"
    },
    metrics: {
      users: "Usuarios",
      usersSubtitle: "{count} administradores",
      pendingSpaces: "Espazos pendentes",
      pendingSpacesSubtitle: "{count} aprobados",
      activeReservations: "Reservas activas",
      activeReservationsSubtitle: "{count} pendentes",
      reviews: "Reseñas",
      reviewsSubtitle: "Moderación dispoñible",
      events: "Eventos",
      eventsSubtitle: "Catálogo administrativo",
      recruitments: "Buscas de membros",
      recruitmentsSubtitle: "Ofertas supervisadas"
    },
    states: {
      loadingOverview: "Cargando resumo do panel...",
      loadingUsers: "Cargando usuarios...",
      loadingSpaces: "Cargando espazos musicais...",
      loadingReservations: "Cargando reservas...",
      loadingReviews: "Cargando reseñas...",
      loadingEvents: "Cargando eventos...",
      loadingRecruitments: "Cargando buscas de membros...",
      errorTitle: "Non se puido cargar esta sección",
      overviewError: "Non se puido cargar o resumo do panel de administración.",
      usersError: "Non se puido cargar a lista de usuarios.",
      spacesError: "Non se puido cargar a lista de espazos.",
      reservationsError: "Non se puido cargar a lista de reservas.",
      reviewsError: "Non se puido cargar a lista de reseñas.",
      eventsError: "Non se puido cargar a lista de eventos.",
      recruitmentsError: "Non se puido cargar a lista de buscas de membros.",
      actionError: "Non se puido completar a acción solicitada."
    },
    filters: {
      allRoles: "Todos os roles",
      allStates: "Todos os estados",
      allStatuses: "Todos os estados",
      allSources: "Todas as orixes",
      allRatings: "Todas as puntúacions",
      allInstruments: "Todos os instrumentos"
    },
    fallbacks: {
      space: "Espazo musical",
      event: "Evento",
      recruitment: "Busca",
      currentSession: "sesión actual"
    },
    overview: {
      title: "Visión xeral",
      subtitle: "Lectura rápida do estado do ecosistema e do uso dos recursos administrables.",
      userBreakdown: "Distribucion de usuarios",
      spaceBreakdown: "Estados de aprobación",
      reservationBreakdown: "Estados de reserva",
      securityTitle: "Acceso protexido",
      securityText:
        "Sesión actual: {email}. Só as contas con permiso de administración poden entrar aquí.",
      securityAction: "Revisar usuarios"
    },
    users: {
      title: "Xestión de usuarios",
      subtitle:
        "Consulta a listaxe real de usuarios e activa ou desactiva contas sen expoñer datos sensibles.",
      emptyTitle: "Non hai usuarios para mostrar",
      emptyText: "Axusta os filtros ou volve cargar a sección para recuperar resultados.",
      activatedSuccess: "Usuario activado correctamente.",
      deactivatedSuccess: "Usuario desactivado correctamente.",
      promotedSuccess: "Usuario convertido en administrador correctamente.",
      adminRoleRevokedSuccess: "Rol de administrador retirado correctamente.",
      selfActionBlocked: "Non podes modificar a túa propia conta de administrador.",
      confirmActivateTitle: "Activar usuario",
      confirmDeactivateTitle: "Desactivar usuario",
      confirmPromoteTitle: "Facer administrador",
      confirmRevokeAdminTitle: "Quitar administrador",
      confirmActivateText: "Vas activar a conta de {name}.",
      confirmDeactivateText: "Vas desactivar a conta de {name}.",
      confirmPromoteText: "Vas converter a conta de {name} en administradora.",
      confirmRevokeAdminText:
        "Vas quitar o rol de administrador a conta de {name}.",
      filters: {
        search: "Buscar por nome, email ou teléfono"
      },
      columns: {
        name: "Nome",
        email: "Email",
        phone: "Teléfono",
        role: "Rol",
        status: "Estado",
        createdAt: "Alta",
        actions: "Accións"
      },
      actions: {
        activate: "Activar",
        deactivate: "Desactivar",
        promoteToAdmin: "Facer administrador",
        removeAdminRole: "Quitar administrador",
        currentUser: "A túa conta"
      }
    },
    spaces: {
      title: "Aprobación de espazos",
      subtitle: "Controla o estado de aprobación dos espazos musicais publicados na plataforma.",
      emptyTitle: "Non hai espazos para mostrar",
      emptyText: "Non se atoparon espazos cos filtros actuais.",
      confirmTitle: "Actualizar aprobación",
      confirmText: 'Vas marcar "{name}" como {status}.',
      updatedSuccess: "Estado do espazo actualizado correctamente.",
      filters: {
        search: "Buscar por nome, cidade ou responsable",
        city: "Filtrar por cidade"
      },
      columns: {
        name: "Espazo",
        city: "Cidade",
        manager: "Responsable",
        type: "Tipo",
        approvalStatus: "Aprobación",
        active: "Activo",
        actions: "Accións"
      },
      actions: {
        approve: "Aprobar",
        reject: "Rexeitar",
        markPending: "Marcar pendente",
        availability: "Dispoñibilidade",
        viewPublic: "Ver público",
        applyStatus: "Aplicar estado"
      }
    },
    reservations: {
      title: "Supervision de reservas",
      subtitle: "Consulta sesións reais, enlaza conversas e cancela reservas con motivo.",
      emptyTitle: "Non hai reservas para mostrar",
      emptyText: "Proba a cambiar texto, data ou estado para recuperar resultados.",
      cancelTitle: "Cancelar reserva",
      cancelText: "Vas cancelar a reserva asociada a {name}.",
      cancelReasonLabel: "Motivo da cancelacion",
      cancelReasonPlaceholder: "Explica ao usuario por que se cancela esta reserva.",
      cancelReasonRequired: "Debes indicar un motivo para cancelar a reserva.",
      cancelSuccess: "Reserva cancelada correctamente.",
      filters: {
        search: "Buscar por espazo, usuario, banda ou notas"
      },
      columns: {
        space: "Espazo",
        user: "Usuario",
        date: "Data",
        time: "Horario",
        status: "Estado",
        price: "Prezo",
        attendees: "Asistentes",
        actions: "Accións"
      },
      actions: {
        messages: "Mensaxes",
        cancel: "Cancelar"
      }
    },
    reviews: {
      title: "Moderación de reseñas",
      subtitle:
        "Elimina contido conflictivo e supervisa valoracións publicadas sobre espazos e usuarios.",
      emptyTitle: "Non hai reseñas para mostrar",
      emptyText: "Non se atoparon reseñas cos filtros actuais.",
      noComment: "Sen comentario adicional.",
      deletedSuccess: "Reseña eliminada correctamente.",
      confirmDeleteTitle: "Eliminar reseña",
      confirmDeleteText: "Vas borrar unha reseña de {target}.",
      ratingLabel: "{value}/5",
      types: {
        SPACE: "Espazo",
        USER: "Usuario"
      },
      filters: {
        search: "Buscar por obxectivo, autor, usuario ou comentario",
        ratingValue: "{value} estrelas"
      },
      columns: {
        type: "Tipo",
        space: "Obxectivo",
        user: "Autor",
        rating: "Puntuación",
        comment: "Comentario",
        date: "Data",
        actions: "Accións"
      },
      actions: {
        delete: "Borrar"
      }
    },
    events: {
      title: "Xestión de eventos",
      subtitle:
        "Supervisa eventos propios e externos, abre o seu detalle público e actualiza os seus datos.",
      emptyTitle: "Non hai eventos para mostrar",
      emptyText: "Non se atoparon eventos cos filtros actuais.",
      createdSuccess: "Evento creado correctamente.",
      updatedSuccess: "Evento actualizado correctamente.",
      publishedSuccess: "Evento publicado correctamente.",
      archivedSuccess: "Evento arquivado correctamente.",
      saveError: "Non se puido gardar o evento.",
      confirmArchiveTitle: "Arquivar evento",
      confirmArchiveText: 'Vas arquivar o evento "{title}".',
      filters: {
        search: "Buscar por título, lugar, cidade ou xénero",
        city: "Filtrar por cidade"
      },
      columns: {
        title: "Evento",
        date: "Data",
        city: "Cidade",
        source: "Orixe",
        status: "Estado",
        type: "Tipo",
        actions: "Accións"
      },
      actions: {
        create: "Crear evento",
        importTicketmaster: "Importar Ticketmaster",
        view: "Ver detalle",
        publish: "Publicar",
        edit: "Editar",
        archive: "Arquivar"
      }
    },
    recruitments: {
      title: "Buscas de membros",
      subtitle:
        "Supervisa ofertas publicadas polas bandas e elimina publicacións cando sexa necesario.",
      emptyTitle: "Non hai buscas para mostrar",
      emptyText: "Non se atoparon publicacións cos filtros actuais.",
      deletedSuccess: "Busca eliminada correctamente.",
      confirmDeleteTitle: "Eliminar busca",
      confirmDeleteText: 'Vas borrar a publicación "{title}".',
      statuses: {
        OPEN: "Aberta",
        CLOSED: "Pechada"
      },
      filters: {
        search: "Buscar por título, banda, instrumento ou cidade"
      },
      columns: {
        title: "Título",
        band: "Banda",
        instrument: "Instrumento",
        level: "Nivel",
        city: "Cidade",
        status: "Estado",
        publicationDate: "Publicación",
        actions: "Accións"
      },
      actions: {
        delete: "Eliminar"
      }
    }
  }
};
