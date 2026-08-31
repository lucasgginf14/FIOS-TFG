export default {
  common: {
    actions: {
      search: "Buscar",
      login: "Iniciar sesión",
      register: "Registrarse",
      logout: "Cerrar sesión",
      save: "Guardar",
      back: "Volver",
      close: "Cerrar",
      loading: "Cargando..."
    },
    labels: {
      city: "Ciudad",
      date: "Fecha",
      spaceType: "Tipo de espacio",
      genre: "Género musical",
      budget: "Presupuesto",
      people: "Nº personas",
      time: "Hora",
      capacity: "Aforo",
      onRequest: "Consultar",
      free: "Gratis",
      notifications: "Notificaciones",
      language: "Idioma",
      navigation: "Navegación"
    },
    states: {
      noData: "Sin datos disponibles",
      backendUnavailable: "No pudimos cargar los datos en este momento."
    },
    imageUpload: {
      upload: "Subir imagen",
      uploading: "Subiendo...",
      clear: "Quitar imagen",
      urlPlaceholder: "https://ejemplo.com/imagen.jpg",
      error: "No se pudo subir la imagen."
    }
  },
  apiErrors: {
    generic: "No se pudo completar la acción. Inténtalo de nuevo.",
    network: "No hay conexión con el servicio. Revisa tu conexión o inténtalo de nuevo en unos segundos.",
    unavailable: "El servicio no está disponible ahora mismo. Inténtalo de nuevo en unos minutos.",
    badRequest: "Revisa los datos introducidos. Hay algo que no parece correcto.",
    unauthorized: "Tu sesión ha caducado. Inicia sesión de nuevo para continuar.",
    forbidden: "No tienes permiso para hacer esta acción.",
    notFound: "No encontramos lo que intentas abrir. Puede que ya no esté disponible.",
    conflict: "No se pudo guardar porque ya existe un dato igual o relacionado.",
    rateLimited: "Se han hecho demasiados intentos seguidos. Espera unos segundos y vuelve a probar.",
    internal: "Ha ocurrido un problema inesperado. Inténtalo de nuevo más tarde.",
    malformed: "No pudimos leer la información enviada. Revisa los datos e inténtalo otra vez.",
    badCredentials: "El email o la contraseña no son correctos.",
    accountDisabled: "Esta cuenta está desactivada. Contacta con administración si crees que es un error.",
    emailExists: "Ya existe una cuenta con ese email.",
    phoneExists: "Ya existe una cuenta con ese teléfono.",
    invalidEmail: "Escribe un email válido.",
    invalidPhone: "Introduce un teléfono válido.",
    requiredFields: "Completa los campos obligatorios antes de continuar.",
    futureBirthDate: "La fecha de nacimiento no puede estar en el futuro.",
    passwordMismatch: "Las contraseñas no coinciden.",
    passwordWeak: "La contraseña debe tener al menos una letra y un número.",
    currentPasswordIncorrect: "La contraseña actual no es correcta.",
    passwordSameAsCurrent: "La nueva contraseña debe ser distinta de la actual.",
    invalidTimeRange: "La hora de inicio debe ser anterior a la hora de fin.",
    invalidDate: "La fecha elegida no es válida.",
    invalidNumber: "Revisa los números introducidos. Alguno está fuera del rango permitido.",
    tooLong: "El texto es demasiado largo.",
    timeNotAvailable: "Ese horario no está disponible. Elige otra franja.",
    noTicketsAvailable: "No quedan entradas disponibles para este evento.",
    eventAlreadyReserved: "Ya tienes una reserva activa para este evento.",
    eventAlreadyPurchased: "Ya tienes una reserva activa para este evento.",
    alreadyExists: "Ya existe un elemento igual o relacionado.",
    spaceEquipmentAlreadyExists: "Este equipamiento ya está asociado al espacio.",
    noPermission: "No puedes hacer esta acción con esta cuenta.",
    actionNotAvailable: "Esta acción no está disponible ahora mismo.",
    locationUnavailable: "No pudimos comprobar la ubicación. Revisa la dirección o las coordenadas.",
    invalidFile: "El archivo enviado no es válido.",
    fileTooLarge: "El archivo supera el tamaño máximo permitido."
  },
  apiFieldErrors: {
    FIELD_REQUIRED: "Completa este campo.",
    INVALID_EMAIL: "Escribe un email válido.",
    INVALID_PHONE: "Introduce un teléfono válido.",
    INVALID_LENGTH: "Revisa la longitud de este campo.",
    INVALID_NUMBER: "Revisa este número.",
    INVALID_DATE: "La fecha elegida no es válida.",
    INVALID_TIME_RANGE: "La hora de inicio debe ser anterior a la hora de fin.",
    INVALID_DATE_RANGE: "La fecha de inicio debe ser anterior o igual a la fecha de fin.",
    INVALID_COORDINATES: "Revisa las coordenadas introducidas.",
    INVALID_VALUE: "Revisa este campo."
  },
  navbar: {
    brandTagline: "plataforma musical",
    menu: {
      search: "Buscar",
      spaces: "Espacios",
      reservations: "Reservas",
      events: "Eventos",
      bands: "Bandas",
      messages: "Mensajes"
    },
    dropdowns: {
      smartSearch: "Búsqueda inteligente",
      smartSearchMeta: "Texto natural y filtros",
      searchMap: "Mapa",
      searchMapMeta: "Vista en mapa",
      exploreSpaces: "Explorar espacios",
      exploreSpacesMeta: "Catálogo público",
      mySpaces: "Mis espacios",
      mySpacesMeta: "Gestiona tus locales",
      createSpace: "Crear espacio",
      createSpaceMeta: "Nuevo local",
      myReservations: "Mis reservas",
      myReservationsMeta: "Tus sesiones",
      receivedReservations: "Reservas recibidas",
      receivedReservationsMeta: "Solicitudes de tus locales",
      upcomingEvents: "Próximos eventos",
      upcomingEventsMeta: "Agenda publicada",
      eventMap: "Mapa de eventos",
      eventMapMeta: "Explora por ciudad",
      exploreBands: "Explorar bandas",
      exploreBandsMeta: "Listado principal",
      myBands: "Mis bandas",
      myBandsMeta: "Vista personal",
      createBand: "Crear banda",
      createBandMeta: "Nueva banda o proyecto",
      memberSearch: "Búsquedas de miembros",
      memberSearchMeta: "Ofertas activas"
    },
    userMenu: {
      profile: "Mi perfil",
      profileMeta: "Datos de la cuenta",
      reservations: "Mis reservas",
      reservationsMeta: "Sesiones reservadas",
      reviews: "Mis reseñas",
      reviewsMeta: "Pendientes y publicadas",
      tickets: "Mis entradas",
      ticketsMeta: "Eventos reservados",
      bands: "Mis bandas",
      bandsMeta: "Bandas y proyectos",
      favorites: "Favoritos",
      favoritesMeta: "Espacios guardados",
      admin: "Administración",
      adminMeta: "Panel de gestión",
      logoutMeta: "Cierra tu sesión de forma segura."
    },
    notifications: {
      title: "Notificaciones",
      summary: "{count} mensajes sin leer",
      empty: "No tienes notificaciones",
      viewMessages: "Ver mensajes",
      viewMessagesMeta: "Abre tu bandeja y continúa las conversaciones",
      fallbackConversation: "Conversación",
      pending: "Actividad reciente"
    }
  },
  account: {
    login: {
      title: "Iniciar sesión",
      description: "Accede a FIOS con tu cuenta.",
      eyebrow: "Cuenta",
      badge: "Acceso",
      heading: "Bienvenido a FIOS",
      subtitle:
        "Usa tu email y contraseña para entrar y recuperar tu actividad.",
      email: "Email",
      password: "Contraseña",
      submit: "Entrar en FIOS",
      submitting: "Entrando...",
      noAccount: "¿No tienes cuenta?",
      registerLink: "Regístrate",
      registeredSuccess: "Cuenta creada correctamente. Ya puedes iniciar sesión.",
      autoLoginFallback:
        "La cuenta se ha creado, pero no se pudo iniciar sesión automáticamente. Entra manualmente para continuar."
    },
    register: {
      title: "Crear cuenta",
      description: "Crea una cuenta para guardar tus reservas, mensajes y preferencias.",
      eyebrow: "Cuenta",
      badge: "Registro",
      heading: "Crea tu cuenta",
      subtitle:
        "Crea tu cuenta y, si todo va bien, accederás automáticamente a FIOS con la sesión iniciada.",
      name: "Nombre",
      firstSurname: "Primer apellido",
      secondSurname: "Segundo apellido",
      phone: "Teléfono",
      email: "Email",
      birthDate: "Fecha de nacimiento",
      instrument: "Instrumento",
      noInstrument: "Sin instrumento",
      loadingInstruments: "Cargando instrumentos...",
      instrumentHelp: "Puedes indicar ahora el instrumento principal que tocas.",
      instrumentCatalogError: "No se pudo cargar el catálogo de instrumentos.",
      validationInstrument: "Selecciona un instrumento válido del catálogo.",
      password: "Contraseña",
      confirmPassword: "Confirmar contraseña",
      passwordRulesTitle: "Tu contraseña debe cumplir:",
      formRulesTitle: "Antes de enviar revisa:",
      passwordRules: {
        minLength: "Mínimo 8 caracteres",
        hasLetter: "Incluir al menos una letra",
        hasNumber: "Incluir al menos un número",
        notEmpty: "No estar vacía"
      },
      formRules: {
        validEmail: "Email válido",
        requiredName: "Nombre obligatorio",
        requiredFirstSurname: "Primer apellido obligatorio",
        requiredPhone: "Teléfono obligatorio",
        validPhone: "Teléfono válido",
        validBirthDate: "Fecha de nacimiento válida"
      },
      submit: "Crear cuenta",
      submitting: "Creando cuenta...",
      backToLogin: "Volver al inicio de sesión"
    },
    errors: {
      requiredLogin: "Debes introducir email y contraseña.",
      loginGeneric: "No se pudo iniciar sesión. Inténtalo de nuevo.",
      registerGeneric: "No se pudo completar el registro.",
      network: "No hay conexión con el servicio de acceso.",
      badCredentials: "El email o la contraseña no son correctos.",
      emailExists: "Ese email ya está registrado.",
      phoneExists: "Ese teléfono ya está registrado.",
      malformed: "No pudimos leer la información enviada.",
      requiredFields: "Completa los campos obligatorios antes de continuar.",
      passwordMismatch: "Las contraseñas no coinciden.",
      invalidEmail: "Introduce un email válido.",
      passwordWeak: "La contraseña debe incluir al menos una letra y un número.",
      futureBirthDate: "La fecha de nacimiento no puede estar en el futuro."
    }
  },
  profile: {
    header: {
      eyebrow: "Perfil FIOS",
      title: "Mi perfil",
      subtitle: "Gestiona tu información personal y seguridad.",
      back: "Volver",
      memberSince: "Alta {date}"
    },
    actions: {
      editProfile: "Editar perfil",
      changePassword: "Cambiar contraseña",
      saveChanges: "Guardar cambios",
      updatePassword: "Actualizar contraseña",
      cancel: "Cancelar",
      retry: "Reintentar"
    },
    states: {
      loading: "Cargando perfil...",
      errorTitle: "No pudimos abrir tu perfil",
      error: "No se pudo cargar la información del perfil."
    },
    summary: {
      reservations: "Reservas",
      bands: "Bandas",
      favorites: "Favoritos",
      unreadMessages: "Mensajes sin leer"
    },
    details: {
      eyebrow: "Datos reales",
      title: "Información de la cuenta"
    },
    fields: {
      name: "Nombre",
      firstSurname: "Primer apellido",
      secondSurname: "Segundo apellido",
      email: "Email",
      phone: "Teléfono",
      birthDate: "Fecha de nacimiento",
      instruments: "Instrumentos",
      instrument: "Instrumento",
      noInstrument: "Sin instrumento",
      role: "Rol",
      createdAt: "Fecha de alta"
    },
    edit: {
      eyebrow: "Edición",
      title: "Actualizar perfil",
      subtitle: "Modifica tus datos básicos. El email se mantiene como referencia de acceso.",
      saving: "Guardando...",
      loadingInstruments: "Cargando instrumentos...",
      instrumentHelp: "Selecciona el instrumento principal que tocas desde el catálogo de FIOS.",
      instrumentCatalogError: "No se pudo cargar el catálogo de instrumentos.",
      validationRequired: "Completa nombre, primer apellido y teléfono antes de guardar.",
      validationBirthDate: "La fecha de nacimiento no puede estar en el futuro.",
      validationInstrument: "Selecciona un instrumento válido del catálogo.",
      error: "No se pudo actualizar el perfil."
    },
    password: {
      eyebrow: "Seguridad",
      title: "Cambiar contraseña",
      subtitle: "Actualiza tu contraseña sin guardar datos sensibles en el cliente.",
      currentPassword: "Contraseña actual",
      newPassword: "Nueva contraseña",
      confirmPassword: "Confirmar nueva contraseña",
      saving: "Actualizando...",
      validationRequired: "Completa los tres campos de contraseña antes de continuar.",
      validationLength: "La nueva contraseña debe tener al menos 8 caracteres.",
      validationPolicy: "La nueva contraseña debe incluir al menos una letra y un número.",
      validationMatch: "La confirmación de la nueva contraseña no coincide.",
      error: "No se pudo cambiar la contraseña."
    },
    image: {
      eyebrow: "Imagen",
      title: "Imagen de perfil",
      subtitle: "Personaliza la imagen pública de tu cuenta.",
      previewAlt: "Imagen de perfil",
      empty: "Sin imagen de perfil",
      inputLabel: "Imagen",
      placeholder: "https://ejemplo.com/avatar.jpg",
      save: "Actualizar imagen",
      remove: "Eliminar imagen",
      saving: "Actualizando...",
      removing: "Eliminando...",
      validationRequired: "Indica una imagen antes de guardar.",
      validationUrl: "El enlace de la imagen no es válido.",
      error: "No se pudo actualizar la imagen de perfil.",
      removeError: "No se pudo eliminar la imagen de perfil."
    },
    notices: {
      profileUpdated: "El perfil se ha actualizado correctamente.",
      passwordUpdated: "La contraseña se ha actualizado correctamente.",
      imageUpdated: "La imagen de perfil se ha actualizado correctamente.",
      imageRemoved: "La imagen de perfil se ha eliminado correctamente."
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
      noData: "Sin dato",
      noInstruments: "Sin instrumentos asociados",
      noInstrument: "Sin instrumento asociado",
      user: "Usuario FIOS"
    }
  },
  favorites: {
    header: {
      eyebrow: "Favoritos FIOS",
      back: "Volver",
      title: "Mis favoritos",
      subtitle: "Espacios guardados para reservar más tarde.",
      explore: "Explorar espacios"
    },
    actions: {
      retry: "Reintentar"
    },
    stats: {
      total: "Favoritos",
      cities: "Ciudades",
      recent: "Recientes"
    },
    filters: {
      search: "Buscar",
      searchPlaceholder: "Buscar por nombre o ciudad",
      city: "Ciudad",
      allCities: "Todas las ciudades",
      type: "Tipo de espacio",
      allTypes: "Todos los tipos",
      order: "Ordenar por",
      clear: "Limpiar filtros",
      orderOptions: {
        recent: "Más recientes",
        name: "Nombre",
        city: "Ciudad",
        rating: "Mejor valorados"
      }
    },
    list: {
      eyebrow: "Lista guardada",
      title: "Espacios favoritos",
      total: "{count} favoritos"
    },
    card: {
      imagePlaceholder: "Imagen no disponible",
      locationFallback: "Ubicación pendiente",
      capacityValue: "{value} personas",
      soundproofed: "Insonorización",
      soundproofedYes: "Insonorizado",
      soundproofedNo: "Sin insonorizar",
      soundproofedUnknown: "Por revisar",
      rating: "Valoración",
      ratingValue: "{rating} ({count})",
      noRating: "Sin reseñas",
      savedAt: "Guardado {date}",
      viewDetail: "Ver detalle",
      remove: "Quitar favorito",
      removing: "Quitando..."
    },
    empty: {
      title: "Todavía no tienes favoritos",
      text: "Guarda espacios desde el detalle para recuperarlos rápido cuando quieras reservar.",
      filteredTitle: "No hay resultados con estos filtros",
      filteredText: "Prueba a cambiar la búsqueda o limpiar los filtros para recuperar favoritos.",
      exploreAction: "Explorar espacios",
      resetAction: "Restablecer filtros"
    },
    states: {
      loading: "Cargando favoritos...",
      errorTitle: "No pudimos abrir tus favoritos",
      error: "No se pudo cargar la lista de favoritos."
    },
    notices: {
      removed: "El espacio se ha quitado de favoritos.",
      removeError: "No se pudo quitar el espacio de favoritos."
    },
    confirm: {
      remove: 'Vas a quitar "{name}" de favoritos.'
    }
  },
  reviewBoard: {
    header: {
      eyebrow: "Reseñas FIOS",
      back: "Volver",
      title: "Reseñas",
      subtitle: "Valora tus experiencias con espacios musicales y usuarios de tus reservas."
    },
    tabs: {
      pending: "Pendientes",
      mine: "Mis reseñas",
      received: "Recibidas"
    },
    stats: {
      pending: "Pendientes",
      published: "Publicadas",
      received: "Recibidas",
      average: "Media dada",
      targets: "Objetivos valorados"
    },
    types: {
      SPACE: "Espacio",
      USER: "Usuario",
      USER_RECEIVED: "Recibida"
    },
    actions: {
      retry: "Reintentar",
      write: "Escribir reseña",
      viewSpace: "Ver espacio",
      cancel: "Cancelar"
    },
    states: {
      loading: "Cargando reseñas...",
      errorTitle: "No se pudieron abrir tus reseñas",
      error: "No se pudo cargar la información de reseñas."
    },
    pending: {
      eyebrow: "Reservas completadas",
      title: "Pendientes de reseña",
      total: "{count} pendientes",
      fallbackSpace: "Espacio musical",
      fallbackUser: "Usuario FIOS",
      noLocation: "Ubicación pendiente",
      noSchedule: "Horario pendiente"
    },
    mine: {
      eyebrow: "Historial publicado",
      title: "Mis reseñas",
      total: "{count} reseñas",
      fallbackSpace: "Espacio musical",
      fallbackUser: "Usuario FIOS",
      noLocation: "Ubicación pendiente",
      noComment: "No dejaste comentario en esta reseña."
    },
    received: {
      eyebrow: "Valoraciones recibidas",
      title: "Reseñas sobre ti",
      total: "{count} recibidas"
    },
    filters: {
      search: "Buscar",
      searchPlaceholder: "Buscar por espacio, usuario o comentario",
      minRating: "Valoración mínima",
      minRatingOption: "{value} estrellas o más",
      allRatings: "Todas las valoraciones",
      order: "Orden",
      clear: "Limpiar filtros",
      orderOptions: {
        recent: "Más recientes",
        highest: "Mejor valoradas",
        lowest: "Peor valoradas"
      }
    },
    metrics: {
      sound: "Sonido",
      equipment: "Equipamiento",
      cleanliness: "Limpieza",
      location: "Ubicación",
      communication: "Comunicación",
      punctuality: "Puntualidad",
      care: "Cuidado del espacio"
    },
    form: {
      eyebrow: "Nueva reseña",
      title: "Escribir reseña",
      intro: "Comparte una valoración clara para ayudar a otros músicos y al espacio.",
      userTitle: "Reseñar usuario",
      userIntro: "Valora cómo fue la comunicación, puntualidad y cuidado durante la reserva.",
      overallRating: "Valoración general",
      soundQualityRating: "Calidad del sonido",
      equipmentRating: "Equipamiento",
      cleanlinessRating: "Limpieza",
      locationRating: "Ubicación",
      communicationRating: "Comunicación",
      punctualityRating: "Puntualidad",
      careRating: "Cuidado del espacio",
      comment: "Comentario",
      commentPlaceholder: "Cuenta cómo fue la experiencia, qué funcionó bien y qué podría mejorar.",
      submit: "Publicar reseña",
      submitting: "Publicando...",
      validation: "Debes indicar una valoración entre 1 y 5 en todos los apartados.",
      error: "No se pudo publicar la reseña."
    },
    empty: {
      pendingTitle: "No tienes reseñas pendientes",
      pendingText:
        "Cuando completes una reserva podrás valorar el espacio, y cuando gestiones espacios podrás valorar a quien reservó.",
      mineTitle: "Todavía no has publicado reseñas",
      mineText: "Tus valoraciones publicadas aparecerán aquí con su desglose por categorías.",
      receivedTitle: "Todavía no tienes reseñas recibidas",
      receivedText: "Cuando un propietario valore una reserva completada contigo, aparecerá aquí.",
      filteredTitle: "No hay reseñas con estos filtros",
      filteredText: "Prueba a cambiar el texto, bajar la valoración mínima o limpiar los filtros.",
      pendingAction: "Ver reservas",
      mineAction: "Explorar espacios",
      receivedAction: "Explorar espacios",
      clearAction: "Limpiar filtros"
    },
    notices: {
      created: "La reseña se ha publicado correctamente."
    }
  },
  instrumentBoard: {
    header: {
      eyebrow: "Instrumentos FIOS",
      back: "Volver",
      title: "Instrumentos",
      subtitle: "Explora instrumentos y configura tu instrumento principal.",
      catalog: "Catálogo",
      mine: "Mi instrumento",
      create: "Crear instrumento"
    },
    actions: {
      retry: "Reintentar",
      cancel: "Cancelar"
    },
    stats: {
      total: "Instrumentos",
      categories: "Categorías",
      mine: "Mi instrumento",
      voice: "Voz"
    },
    filters: {
      search: "Buscar",
      searchPlaceholder: "Buscar por nombre de instrumento",
      category: "Categoría",
      allCategories: "Todas las categorías",
      order: "Ordenar por",
      clear: "Limpiar filtros",
      orderOptions: {
        name: "Nombre",
        category: "Categoría"
      }
    },
    list: {
      eyebrow: "Panel instrumental",
      catalogTitle: "Catálogo público",
      mineTitle: "Tu instrumento principal",
      total: "{count} instrumentos"
    },
    card: {
      categoryLabel: "Categoría:",
      add: "Seleccionar instrumento",
      remove: "Quitar instrumento",
      edit: "Editar",
      saving: "Guardando..."
    },
    empty: {
      catalogTitle: "No hay instrumentos en el catálogo",
      catalogText:
        "Cuando existan instrumentos disponibles, aparecerán aquí para filtrarlos y anadirlos a tu perfil.",
      mineTitle: "Todavía no has configurado tu instrumento",
      mineText:
        "Selecciona desde el catálogo el instrumento principal que tocas para mejorar tu perfil musical.",
      filteredTitle: "No hay resultados con estos filtros",
      filteredText: "Prueba a cambiar la búsqueda, la categoría o limpiar los filtros.",
      mineAction: "Explorar catálogo",
      clearAction: "Limpiar filtros"
    },
    form: {
      createEyebrow: "Nuevo instrumento",
      createTitle: "Crear instrumento",
      editEyebrow: "Editar instrumento",
      editTitle: "Actualizar instrumento",
      intro: "Define un nombre claro y la categoría adecuada para el catálogo público.",
      name: "Nombre",
      category: "Categoría",
      selectCategory: "Selecciona una categoría",
      create: "Crear instrumento",
      save: "Guardar cambios",
      creating: "Creando...",
      saving: "Guardando...",
      validation: "Debes indicar nombre y categoría.",
      error: "No se pudo guardar el instrumento."
    },
    notices: {
      minePartial: "El catálogo cargó, pero no se pudo recuperar tu instrumento.",
      adminOnly: "Solo los administradores pueden crear instrumentos.",
      added: "Instrumento añadido a tu perfil.",
      selected: "Instrumento principal actualizado.",
      removed: "Instrumento quitado de tu perfil.",
      updateError: "No se pudo actualizar tu instrumento.",
      created: "Instrumento creado correctamente.",
      updated: "Instrumento actualizado correctamente."
    },
    states: {
      loading: "Cargando instrumentos...",
      errorTitle: "No pudimos abrir los instrumentos",
      error: "No se pudo cargar el catálogo de instrumentos."
    },
    categories: {
      STRINGS: "Cuerda",
      WIND: "Viento",
      BRASS: "Metal",
      PERCUSSION: "Percusión",
      KEYBOARD: "Teclado",
      ELECTRONIC: "Electrónico",
      VOICE: "Voz",
      OTHER: "Otros"
    }
  },
  recruitmentBoard: {
    header: {
      eyebrow: "Ofertas FIOS",
      back: "Volver",
      title: "Búsquedas de miembros",
      subtitle: "Encuentra bandas que buscan músicos.",
      explore: "Explorar",
      mine: "Mis búsquedas",
      create: "Publicar búsqueda"
    },
    actions: {
      retry: "Reintentar",
      cancel: "Cancelar"
    },
    stats: {
      open: "Abiertas",
      bands: "Bandas",
      instruments: "Instrumentos",
      cities: "Ciudades"
    },
    filters: {
      search: "Buscar",
      searchPlaceholder: "Buscar por título, banda o ciudad",
      instrument: "Instrumento",
      allInstruments: "Todos los instrumentos",
      level: "Nivel requerido",
      allLevels: "Todos los niveles",
      city: "Ciudad",
      allCities: "Todas las ciudades",
      genre: "Género musical",
      allGenres: "Todos los géneros",
      order: "Ordenar por",
      clear: "Limpiar filtros",
      orderOptions: {
        recent: "Más recientes",
        city: "Ciudad",
        instrument: "Instrumento",
        vacancies: "Más vacantes"
      }
    },
    list: {
      eyebrow: "Listado activo",
      title: "Búsquedas disponibles",
      exploreTitle: "Explorar búsquedas abiertas",
      mineTitle: "Gestiona tus búsquedas",
      total: "{count} búsquedas"
    },
    card: {
      bandFallback: "Banda",
      cityFallback: "Ciudad pendiente",
      genreFallback: "Género por definir",
      instrumentFallback: "Rol abierto",
      vacancies: "{count} vacantes",
      noPublicationDate: "Fecha pendiente",
      view: "Ver oferta",
      edit: "Editar",
      close: "Cerrar búsqueda",
      closing: "Cerrando..."
    },
    empty: {
      title: "No hay búsquedas activas",
      text: "Cuando las bandas publiquen nuevas vacantes, aparecerán aquí para que puedas explorarlas.",
      mineTitle: "Todavía no has publicado búsquedas",
      mineText: "Publica una vacante desde una de tus bandas para empezar a recibir interes.",
      filteredTitle: "No hay resultados con estos filtros",
      filteredText: "Prueba a cambiar la búsqueda o limpiar los filtros para recuperar ofertas.",
      exploreAction: "Explorar bandas",
      createAction: "Publicar búsqueda",
      createBandAction: "Crear banda",
      resetAction: "Restablecer filtros"
    },
    states: {
      loading: "Cargando búsquedas...",
      errorTitle: "No pudimos abrir las búsquedas",
      error: "No se pudo cargar la lista de búsquedas."
    },
    notices: {
      privateFallback:
        "Debes iniciar sesión para ver tus búsquedas. Mostramos las búsquedas abiertas.",
      partialSupport: "La lista principal se cargo, pero faltan algunos datos auxiliares.",
      noBands: "Necesitas al menos una banda para publicar una búsqueda.",
      noManageableBands: "Solo puedes publicar búsquedas desde bandas que lideras.",
      created: "La búsqueda se ha publicado correctamente.",
      updated: "La búsqueda se ha actualizado correctamente.",
      closed: "La búsqueda se ha cerrado correctamente.",
      closeError: "No se pudo cerrar la búsqueda."
    },
    confirm: {
      close: 'Vas a cerrar "{title}".'
    },
    form: {
      createEyebrow: "Nueva búsqueda",
      createTitle: "Publicar búsqueda de miembros",
      editEyebrow: "Editar búsqueda",
      editTitle: "Actualizar búsqueda",
      intro: "Completa una oferta simple para que otros músicos encuentren tu proyecto.",
      band: "Banda",
      selectBand: "Selecciona una banda",
      instrument: "Instrumento",
      selectInstrument: "Selecciona un instrumento",
      title: "Título",
      roleWanted: "Rol buscado",
      levelRequired: "Nivel requerido",
      city: "Ciudad",
      vacancies: "Vacantes",
      description: "Descripción",
      create: "Publicar búsqueda",
      save: "Guardar cambios",
      creating: "Publicando...",
      saving: "Guardando...",
      validation: "Completa banda, instrumento, título, rol, ciudad y vacantes antes de guardar.",
      submitError: "No se pudo guardar la búsqueda."
    },
    detail: {
      eyebrow: "Oferta",
      titleFallback: "Detalle de la búsqueda",
      loading: "Cargando oferta...",
      error: "No se pudo cargar el detalle de la oferta.",
      descriptionTitle: "Descripción",
      emptyDescription: "Esta oferta todavía no tiene una descripción ampliada.",
      contactTitle: "Contacto",
      contactHint: "Escribe a la persona que publicó esta oferta para hablar sobre la vacante.",
      contactAction: "Enviar email",
      viewBand: "Explorar bandas",
      fields: {
        city: "Ciudad",
        genre: "Género musical",
        role: "Rol buscado",
        vacancies: "Vacantes",
        publicationDate: "Publicación"
      }
    },
    status: {
      OPEN: "Abierta",
      CLOSED: "Cerrada"
    }
  },
  home: {
    viewAll: "Ver todos",
    actions: {
      viewSpace: "Ver espacio",
      viewEvent: "Ver evento",
      viewOffer: "Ver oferta",
      viewBand: "Ver banda"
    },
    hero: {
      kicker: "Plataforma musical",
      title: "Encuentra espacios, eventos y músicos para tu proyecto musical",
      subtitle:
        "Descubre locales, conciertos, bandas y oportunidades de colaboración desde una única plataforma pensada para la escena musical.",
      inputPlaceholder: "Busca locales, bandas o conciertos",
      examples: "Ver ejemplos de búsqueda",
      exampleOne: "Local de ensayo para 5 músicos en A Coruña este viernes",
      exampleTwo: "Conciertos de jazz en Santiago este fin de semana",
      exampleThree: "Banda busca bajista en Vigo",
      metrics: {
        label: "Resumen del catálogo",
        spaces: "espacios",
        cities: "ciudades",
        events: "eventos",
        recruitments: "búsquedas"
      }
    },
    quickFilters: {
      title: "Filtros rápidos",
      subtitle: "Ajusta la búsqueda antes de saltar al módulo completo.",
      cityPlaceholder: "A Coruña, Vigo, Santiago...",
      datePlaceholder: "Selecciona fecha",
      spaceTypePlaceholder: "Todos los espacios",
      genrePlaceholder: "Rock, jazz, pop...",
      budgetPlaceholder: "Max €",
      peoplePlaceholder: "Asistentes",
      reset: "Limpiar filtros"
    },
    featuredSpaces: {
      title: "Espacios destacados",
      subtitle: "Locales y salas listos para ensayo, grabación o directo.",
      soundproofed: "Insonorizado",
      notSoundproofed: "Sin insonorizar",
      capacity: "Capacidad",
      estimatedPrice: "Precio estimado",
      rating: "Valoración",
      pricePerHour: "{price}/h",
      priceRange: "{from} - {to}/h",
      reasons: {
        highRating: "Destaca por sus valoraciones y reseñas.",
        soundproofed: "Buena opción si necesitas controlar el ruido.",
        large: "Interesante para grupos grandes o montajes amplios.",
        completeProfile: "Tiene información visual suficiente para valorar el espacio.",
        balanced: "Buena opción general para empezar a comparar."
      }
    },
    featuredEvents: {
      title: "Eventos destacados",
      subtitle: "Una agenda breve para arrancar la exploración.",
      reasons: {
        today: "Es una opción para hoy.",
        soon: "Está cerca en la agenda.",
        accessible: "Tiene una entrada accesible o precio por confirmar.",
        large: "Evento con aforo amplio.",
        linked: "Está conectado con contenido de la plataforma.",
        upcoming: "Evento publicado y todavía disponible."
      }
    },
    recruitments: {
      title: "Bandas buscan miembros",
      subtitle: "Oportunidades activas para incorporarte a nuevos proyectos.",
      vacancies: "Vacantes",
      level: "Nivel",
      instrument: "Instrumento"
    },
    personal: {
      title: "Tu zona personal",
      subtitle: "Actividad reciente y accesos rápidos a tu contexto.",
      searches: "Ultimás búsquedas",
      reservations: "Mis reservas",
      messages: "Mensajes recientes",
      loginCtaTitle: "Inicia sesión para ver tu zona personal",
      loginCtaText:
        "Accede a tus búsquedas guardadas, reservas activas y conversaciones recientes desde la portada.",
      unreadCount: "{count} mensajes sin leer",
      fallbackSpace: "Espacio",
      fallbackConversation: "Conversación",
      fallbackBand: "Banda"
    },
    stats: {
      spaces: "espacios",
      events: "eventos",
      recruitments: "ofertas"
    },
    states: {
      loadingPublic: "Cargando datos reales",
      loadingPublicText: "Estamos consultando la información publicada en FIOS.",
      loadingPersonal: "Cargando tu actividad..."
    },
    errors: {
      publicDataWarning:
        "No se pudieron cargar todos los datos públicos en este momento.",
      spacesTitle: "No se pudieron cargar los espacios",
      spacesText:
        "La sección de espacios no está disponible ahora mismo. Inténtalo de nuevo desde el listado.",
      eventsTitle: "No se pudieron cargar los eventos",
      eventsText:
        "La agenda pública no está disponible ahora mismo. Inténtalo de nuevo desde eventos.",
      recruitmentsTitle: "No se pudieron cargar las búsquedas",
      recruitmentsText:
        "Las ofertas de bandas no están disponibles ahora mismo. Inténtalo de nuevo desde el tablero.",
      searchesText: "No se pudieron cargar tus búsquedas recientes.",
      reservationsText: "No se pudieron cargar tus reservas.",
      messagesText: "No se pudieron cargar tus mensajes recientes."
    },
    placeholders: {
      spacesTitle: "Sin espacios destacados",
      spacesText: "No hay espacios públicos para mostrar ahora mismo.",
      eventsTitle: "Sin eventos destacados",
      eventsText: "No hay eventos publicados para mostrar ahora mismo.",
      recruitmentsTitle: "Sin búsquedas activas",
      recruitmentsText: "No hay ofertas activas de bandas para mostrar ahora mismo.",
      reservationsText: "Tus reservas aparecerán aquí cuando existan sesiones activas.",
      messagesText: "Tus conversaciones con mensajes pendientes se listarán aquí.",
      searchesText: "Las búsquedas naturales recientes se mostrarán aquí."
    },
    notFound: {
      title: "Ruta no encontrada",
      description: "No hemos encontrado la página que intentas abrir en FIOS.",
      text: "Puede que el enlace haya cambiado o que la dirección no sea correcta. Vuelve al inicio para continuar explorando espacios, eventos y bandas.",
      action: "Volver al inicio"
    }
  },
  search: {
    hero: {
      kicker: "Resultados FIOS",
      title: "Encuentra espacios, eventos y bandas cerca de ti",
      subtitle:
        "Escribe lo que necesitas o usa los filtros para descubrir locales, conciertos y proyectos que buscan gente.",
      placeholder: "Local de ensayo en Santiago para 4 personas este viernes por la tarde",
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
      spaces: "Espacios",
      events: "Eventos",
      bands: "Bandas",
      recruitments: "Bandas buscan miembros"
    },
    filters: {
      eyebrow: "Refinar",
      title: "Filtros",
      clear: "Limpiar",
      dateTitle: "Fecha",
      resultTypeTitle: "Tipo de resultado",
      spaceTypeTitle: "Tipo de espacio",
      genreTitle: "Género musical",
      budgetTitle: "Presupuesto máximo",
      maxBudgetPlaceholder: "Presupuesto máximo",
      peopleTitle: "Nº de personas",
      timeTitle: "Horario",
      apply: "Aplicar filtros",
      anyBudget: "Sin límite",
      anyPeople: "Sin límite",
      maxBudgetValue: "Hasta {value} EUR",
      peopleValue: "{value} personas",
      datePresets: {
        today: "Hoy",
        tomorrow: "Mañana",
        weekend: "Este fin de semana",
        week: "Esta semana"
      },
      spaceTypes: {
        rehearsal: "Local de ensayo",
        recording: "Estudio de grabación",
        performance: "Sala de actuación",
        barStage: "Bar con escenario",
        multiuse: "Sala polivalente"
      },
      timeSlots: {
        morning: "Mañana",
        afternoon: "Tarde",
        night: "Noche",
        allday: "Todo el dia"
      }
    },
    states: {
      loading: "Cargando resultados...",
      emptyTitle: "No hemos encontrado coincidencias",
      emptyText: "Prueba a cambiar la frase natural o relajar algunos filtros.",
      error: "No se pudo ejecutar la búsqueda.",
      partialBands:
        "Espacios y eventos cargados. Las bandas no se pudieron recuperar.",
      partialRecruitments:
        "Espacios y eventos cargados. Las ofertas de bandas no se pudieron recuperar."
    },
    map: {
      emptyTitle: "No hay puntos geolocalizados para este filtro",
      emptyText: "Mantuvimos la estructura del mapa para que puedas seguir afinando la búsqueda.",
      placeholder: "No se pudo cargar la vista mapa en este momento.",
      popupAction: "Abrir"
    },
    chips: {
      people: "{value} personas",
      budget: "Hasta {value} EUR",
      dateRange: "{from} - {to}",
      today: "Hoy",
      tomorrow: "Mañana"
    },
    spaceTypeLabels: {
      REHEARSAL_ROOM: "Local de ensayo",
      RECORDING_STUDIO: "Estudio de grabación",
      CONCERT_HALL: "Sala de actuación",
      CLASSROOM: "Aula",
      MULTIPURPOSE: "Sala polivalente",
      OTHER: "Espacio musical"
    },
    eventTypeLabels: {
      CONCERT: "Concierto",
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
        capacity: "{value} personas",
        squareMeters: "{value} m2",
        soundproofed: "Insonorizado",
        notSoundproofed: "Sin insonorizar",
        unknownSound: "A revisar",
        estimatedPriceLabel: "Precio estimado",
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
        roleFallback: "Rol abierto",
        vacancies: "{value} vacantes",
        match: "Match {value}%",
        cta: "Ver oferta"
      }
    }
  },
  spaceDetail: {
    states: {
      loading: "Cargando espacio...",
      loadingSection: "Cargando contenido...",
      error: "No se pudo cargar el detalle del espacio.",
      errorTitle: "No pudimos abrir este espacio",
      partialData: "Se cargo el espacio principal, pero faltan algunos datos secundarios."
    },
    header: {
      eyebrow: "Espacio musical",
      back: "Volver",
      capacity: "{value} personas",
      soundproofed: "Insonorizado",
      notSoundproofed: "Sin insonorizar",
      squareMeters: "{value} m2"
    },
    media: {
      placeholder: "Imagen no disponible",
      addFavorite: "Añadir a favoritos",
      removeFavorite: "Quitar de favoritos",
      favoriteAdded: "Espacio guardado en favoritos.",
      favoriteRemoved: "Espacio eliminado de favoritos.",
      favoriteError: "No se pudo actualizar favoritos."
    },
    quickServices: {
      eyebrow: "Servicios rápidos",
      title: "Lo esencial del espacio",
      items: {
        wifi: "WiFi incluido",
        parking: "Parking cercano",
        rest: "Zona de descanso",
        access: "Acceso adaptado"
      }
    },
    description: {
      eyebrow: "Descripción",
      title: "Cómo es este espacio",
      empty: "Todavía no hay descripción disponible para este espacio."
    },
    info: {
      eyebrow: "Información útil",
      title: "Ficha del espacio",
      items: {
        spaceType: "Tipo de espacio",
        capacity: "Capacidad máxima",
        squareMeters: "Superficie",
        opening: "Apertura estimada",
        closing: "Cierre estimado",
        status: "Estado para la fecha elegida",
        address: "Dirección completa",
        hourlyPrice: "Precio estimado por hora"
      },
      capacityValue: "{value} asistentes",
      squareMetersValue: "{value} m2",
      availableNow: "Disponible",
      unavailableNow: "Sin disponibilidad",
      notAvailable: "No disponible"
    },
    equipment: {
      eyebrow: "Equipamiento",
      title: "Incluido en el espacio",
      count: "{value} elementos",
      quantity: "{value} uds.",
      empty: "No hay equipamiento registrado todavía.",
      error: "No se pudo cargar el equipamiento.",
      unknown: "Equipo sin nombre",
      states: {
        AVAILABLE: "Disponible",
        LIMITED: "Uso limitado",
        OUT_OF_SERVICE: "Fuera de servicio"
      }
    },
    availability: {
      eyebrow: "Disponibilidad",
      title: "Franjas disponibles",
      fullSchedule: "Ver horarios completos",
      fullScheduleTitle: "Horarios completos",
      fullScheduleLoading: "Cargando horarios...",
      fullScheduleEmpty: "Este espacio todavía no tiene horarios publicados.",
      fullScheduleError: "No se pudieron cargar los horarios.",
      fullScheduleClose: "Cerrar horarios",
      scheduleDay: "Día",
      scheduleTime: "Horario",
      schedulePrice: "Precio",
      date: "Fecha",
      quickDates: "Próximos días",
      loading: "Consultando disponibilidad...",
      loadingHint: "Estamos cruzando horarios, excepciones y reservas existentes.",
      empty: "No hay franjas libres para esta fecha.",
      emptySummary: "Sin franjas disponibles",
      emptyHint: "Prueba con otro día o revisa los horarios completos del espacio.",
      availableSummary: "{count} franjas libres",
      availableWithBookedSummary: "{free} franjas libres · {booked} reservas",
      bookedOnlySummary: "{count} reservas para esta fecha",
      bookedOnlyHint: "Todas las franjas publicadas para esta fecha tienen reservas o bloqueos.",
      selectedSummary: "Franja seleccionada: {range}",
      firstSlotSummary: "Primera franja libre desde las {time}",
      bookedTitle: "Reservas existentes",
      bookedDetail: "Estos tramos ya están ocupados para la fecha seleccionada.",
      error: "No se pudo cargar la disponibilidad.",
      errorSummary: "Disponibilidad no disponible",
      errorHint: "Inténtalo de nuevo o consulta los horarios completos.",
      today: "Hoy",
      tomorrow: "Mañana",
      chooseSlot: "Elegir franja",
      selectedSlot: "Seleccionada",
      priceFrom: "Desde {price}",
      periods: {
        morning: "Mañana",
        afternoon: "Tarde",
        evening: "Noche",
        night: "Madrugada"
      }
    },
    reviews: {
      eyebrow: "Reseñas",
      title: "Opiniones de otros usuarios",
      count: "{value} reseñas",
      empty: "Todavía no hay reseñas publicadas.",
      error: "No se pudieron cargar las reseñas.",
      noComment: "Sin comentario adicional.",
      anonymous: "Usuario FIOS",
      metrics: {
        sound: "Sonido",
        equipment: "Equipo",
        cleanliness: "Limpieza",
        location: "Ubicación"
      }
    },
    map: {
      eyebrow: "Ubicación",
      title: "Dónde está",
      empty: "Ubicación no disponible",
      noAddress: "Sin dirección detallada"
    },
    booking: {
      eyebrow: "Reserva",
      title: "Reserva este espacio",
      date: "Fecha",
      startTime: "Hora de inicio",
      duration: "Duración",
      attendees: "Asistentes",
      subtotal: "Subtotal",
      total: "Total",
      submit: "Reservar ahora",
      loginToReserve: "Inicia sesión para reservar",
      submitting: "Creando reserva...",
      selectSlot: "Selecciona una franja",
      noReviews: "Sin reseñas",
      priceRange: "De {from} a {to}/h",
      priceSingle: "{value}/h",
      success: "Reserva creada correctamente. La disponibilidad se ha actualizado.",
      durationHours: "{hours} h",
      durationMinutes: "{minutes} min",
      durationMixed: "{hours} h {minutes} min",
      errors: {
        slotRequired: "Selecciona una franja disponible antes de reservar.",
        durationRequired: "Selecciona una duración válida para la sesión.",
        attendees: "El número de asistentes no es válido para este espacio.",
        generic: "No se pudo crear la reserva."
      }
    },
    ownerActions: {
      eyebrow: "Gestión del espacio",
      title: "Horarios, excepciones y equipamiento",
      manageAvailability: "Gestionar disponibilidad",
      manageEquipment: "Gestionar equipamiento"
    },
    spaceTypeLabels: {
      REHEARSAL_ROOM: "Local de ensayo",
      RECORDING_STUDIO: "Estudio de grabación",
      CONCERT_HALL: "Sala de actuación",
      CLASSROOM: "Aula",
      MULTIPURPOSE: "Sala polivalente",
      OTHER: "Espacio musical"
    }
  },
  spaceEquipmentManage: {
    header: {
      eyebrow: "Equipamiento",
      title: "Gestionar equipamiento"
    },
    states: {
      loading: "Cargando equipamiento...",
      error: "No se pudo cargar el equipamiento.",
      saveError: "No se pudo guardar el equipamiento.",
      deleteError: "No se pudo eliminar el equipamiento.",
      empty: "Este espacio todavía no tiene equipamiento asociado.",
      catalogEmpty: "El catálogo de equipamiento está vacío.",
      allAssigned: "Todo el equipamiento del catálogo ya está asociado a este espacio."
    },
    form: {
      eyebrow: "Catálogo",
      addTitle: "Añadir equipamiento",
      editTitle: "Editar equipamiento",
      equipment: "Equipo",
      selectEquipment: "Selecciona un equipo",
      customEquipmentOption: "Otro material",
      customEquipment: "Nombre del material",
      customEquipmentPlaceholder: "Pedalboard, atril extra, pantalla 4x12...",
      quantity: "Cantidad",
      state: "Estado",
      observations: "Observaciones",
      observationsPlaceholder: "Notas internas visibles en el detalle del espacio",
      customObservationsPlaceholder: "Describe marca, estado, uso o cualquier detalle necesario",
      requiredForCustom: "Obligatorio para Otro"
    },
    list: {
      eyebrow: "Equipamiento actual",
      title: "Asociaciones del espacio"
    },
    actions: {
      add: "Añadir equipamiento",
      adding: "Añadiendo...",
      update: "Guardar cambios",
      updating: "Guardando...",
      edit: "Editar",
      delete: "Eliminar",
      reload: "Recargar",
      cancel: "Cancelar",
      cancelEdit: "Cancelar edición",
      confirmDelete: "Eliminar equipamiento"
    },
    notices: {
      added: "Equipamiento añadido al espacio.",
      updated: "Equipamiento actualizado correctamente.",
      deleted: "Equipamiento eliminado del espacio."
    },
    validation: {
      equipment: "Selecciona un equipo del catálogo.",
      customEquipment: "Indica el nombre del material.",
      customObservations: "Describe el material en observaciones.",
      quantity: "Introduce una cantidad válida.",
      state: "Selecciona un estado válido.",
      duplicate: "Este equipamiento ya está asociado al espacio."
    },
    confirm: {
      eyebrow: "Confirmación",
      deleteTitle: "Eliminar equipamiento",
      deleteText: "Vas a eliminar {name} de este espacio. ¿Continuar?"
    },
    categories: {
      INSTRUMENT: "Instrumento",
      SOUND: "Sonido",
      LIGHTING: "Iluminación",
      RECORDING: "Grabación",
      FURNITURE: "Mobiliario",
      ACCESSORY: "Accesorio",
      OTHER: "Otro"
    }
  },
  spaceAvailabilityManage: {
    header: {
      back: "Volver a mis espacios",
      eyebrow: "Disponibilidad",
      detail: "Ver detalle"
    },
    states: {
      loading: "Cargando disponibilidad...",
      errorTitle: "No pudimos abrir esta gestión",
      error: "No se pudo cargar la gestión de disponibilidad.",
      noPermissionTitle: "Sin permisos",
      noPermission: "Solo el propietario del espacio o un administrador pueden gestionar esta disponibilidad.",
      inactiveTitle: "Espacio inactivo",
      inactive: "No se puede modificar la disponibilidad de un espacio inactivo.",
      notApprovedTitle: "Pendiente de aprobación",
      notApproved: "La gestión de disponibilidad se muestra cuando el espacio está aprobado."
    },
    summary: {
      schedules: "Horarios",
      exceptions: "Excepciones",
      blocked: "Bloqueos",
      custom: "Personalizadas"
    },
    schedules: {
      eyebrow: "Horario habitual",
      formTitle: "Editar horario",
      listTitle: "Horarios configurados",
      empty: "Aún no hay horarios habituales."
    },
    exceptions: {
      eyebrow: "Excepciones",
      formTitle: "Editar excepción",
      listTitle: "Excepciones configuradas",
      empty: "Aún no hay excepciones.",
      noPrice: "No aplica"
    },
    fields: {
      dayOfWeek: "Día",
      startTime: "Inicio",
      endTime: "Fin",
      price: "Precio",
      timeRange: "Horario",
      date: "Fecha",
      exceptionType: "Tipo",
      reason: "Motivo"
    },
    actions: {
      cancelEdit: "Cancelar edición",
      edit: "Editar",
      delete: "Eliminar",
      deleting: "Eliminando...",
      saving: "Guardando...",
      saveSchedule: "Guardar horario",
      createSchedule: "Añadir horario",
      saveException: "Guardar excepción",
      createException: "Añadir excepción",
      refresh: "Actualizar"
    },
    exceptionTypes: {
      BLOCKED: "Bloqueo puntual",
      CUSTOM_AVAILABILITY: "Disponibilidad personalizada"
    },
    calculated: {
      eyebrow: "Resultado",
      title: "Disponibilidad calculada",
      loading: "Actualizando disponibilidad...",
      error: "No se pudo calcular la disponibilidad.",
      availableTitle: "Franjas libres",
      bookedTitle: "Reservas que bloquean",
      emptySlots: "No hay franjas libres para esta fecha.",
      emptyBooked: "No hay reservas activas para esta fecha."
    },
    validation: {
      scheduleDay: "Selecciona un día válido.",
      timeRange: "La hora de inicio debe ser anterior a la hora de fin.",
      schedulePrice: "Indica un precio mayor que cero.",
      exceptionDate: "Selecciona una fecha válida.",
      exceptionType: "Selecciona un tipo de excepción válido.",
      customPrice: "La disponibilidad personalizada necesita un precio mayor que cero."
    },
    notices: {
      scheduleCreated: "Horario añadido correctamente.",
      schedulesCreated: "{count} horarios añadidos correctamente.",
      scheduleUpdated: "Horario actualizado correctamente.",
      scheduleDeleted: "Horario eliminado correctamente.",
      scheduleError: "No se pudo guardar el horario.",
      scheduleDeleteError: "No se pudo eliminar el horario.",
      exceptionCreated: "Excepción añadida correctamente.",
      exceptionUpdated: "Excepción actualizada correctamente.",
      exceptionDeleted: "Excepción eliminada correctamente.",
      exceptionError: "No se pudo guardar la excepción.",
      exceptionDeleteError: "No se pudo eliminar la excepción."
    },
    confirm: {
      deleteSchedule: "Vas a eliminar este horario habitual.",
      deleteException: "Vas a eliminar esta excepción de disponibilidad."
    }
  },
  spaceList: {
    header: {
      eyebrow: "Catálogo FIOS",
      title: "Espacios musicales",
      subtitle: "Encuentra salas, estudios y locales para tu proyecto.",
      explore: "Explorar",
      mine: "Mis espacios",
      create: "Crear espacio"
    },
    stats: {
      total: "Visibles",
      approved: "Aprobados",
      active: "Activos",
      cities: "Ciudades",
      types: "Tipos"
    },
    filters: {
      search: "Buscar",
      searchPlaceholder: "Buscar por nombre, ciudad o tipo de espacio",
      city: "Ciudad",
      allCities: "Todas las ciudades",
      type: "Tipo de espacio",
      allTypes: "Todos los tipos",
      minCapacity: "Aforo mínimo",
      soundproofed: "Insonorizado",
      allSoundproofed: "Cualquier opción",
      soundproofedYes: "Sí",
      soundproofedNo: "No",
      order: "Ordenar por",
      clear: "Limpiar filtros",
      orderOptions: {
        rating: "Mejor valorados",
        capacity: "Mayor capacidad",
        city: "Ciudad",
        name: "Nombre"
      }
    },
    list: {
      eyebrow: "Listado real",
      exploreTitle: "Explorar espacios disponibles",
      mineTitle: "Gestiona tus espacios",
      total: "{count} espacios"
    },
    card: {
      imagePlaceholder: "Imagen no disponible",
      locationFallback: "Ubicación pendiente",
      capacityValue: "{value} personas",
      squareMeters: "Superficie",
      squareMetersValue: "{value} m2",
      soundproofed: "Insonorización",
      soundproofedYes: "Insonorizado",
      soundproofedNo: "Sin insonorizar",
      soundproofedUnknown: "Por revisar",
      rating: "Valoración",
      ratingValue: "{rating} ({count})",
      noRating: "Sin reseñas",
      notSpecified: "Sin dato",
      viewDetail: "Ver detalle",
      manageAvailability: "Gestionar disponibilidad",
      edit: "Editar",
      deactivate: "Desactivar",
      deactivating: "Desactivando..."
    },
    empty: {
      exploreTitle: "No hay espacios públicos disponibles",
      exploreText:
        "Cuando haya espacios aprobados en la plataforma, aparecerán aquí con acceso al detalle.",
      mineTitle: "Todavía no has publicado espacios",
      mineText: "Crea tu primer espacio para gestionarlo desde esta pantalla y seguir su estado.",
      filteredTitle: "No hay resultados con estos filtros",
      filteredText:
        "Prueba a cambiar la búsqueda o relajar algún filtro para recuperar resultados.",
      resetAction: "Restablecer filtros",
      createAction: "Crear espacio"
    },
    form: {
      createEyebrow: "Nuevo espacio",
      createTitle: "Crear espacio musical",
      editEyebrow: "Editar espacio",
      editTitle: "Actualizar espacio musical",
      intro: "Completa una ficha simple para publicar o actualizar tu espacio.",
      pendingHint: "El espacio puede quedar pendiente de aprobación antes de publicarse.",
      name: "Nombre",
      description: "Descripción",
      spaceType: "Tipo de espacio",
      capacity: "Aforo",
      squareMeters: "Superficie",
      soundproofed: "Insonorizado",
      mainImage: "Imagen principal",
      country: "País",
      province: "Provincia",
      city: "Ciudad",
      street: "Calle",
      portal: "Portal",
      floor: "Piso",
      postalCode: "Código postal",
      coordinates: "Coordenadas",
      coordinatesPlaceholder: "43.3623, -8.4115",
      coordinatesHint: "Pega las coordenadas juntas o un enlace de mapa.",
      coordinatesAction: "Aplicar",
      coordinatesApplied: "Coordenadas añadidas.",
      coordinatesError: "No pudimos reconocer esas coordenadas.",
      latitude: "Latitud",
      longitude: "Longitud",
      cancel: "Cancelar",
      save: "Guardar cambios",
      create: "Crear espacio",
      saving: "Guardando...",
      creating: "Creando...",
      validation:
        "Completa nombre, tipo, aforo, superficie y dirección obligatoria antes de guardar.",
      submitError: "No se pudo guardar el espacio."
    },
    status: {
      APPROVED: "Aprobado",
      PENDING: "Pendiente",
      REJECTED: "Rechazado",
      INACTIVE: "Inactivo"
    },
    notices: {
      privateFallback: "Debes iniciar sesión para ver tus espacios. Mostramos el catálogo público.",
      editLoadError: "No se pudo cargar el detalle del espacio para editarlo.",
      updated: "El espacio se ha actualizado correctamente.",
      createdPending: "El espacio se ha creado y queda pendiente de aprobación.",
      deactivated: "El espacio se ha desactivado correctamente.",
      deactivateError: "No se pudo desactivar el espacio."
    },
    confirm: {
      deactivate: 'Vas a desactivar "{name}".'
    },
    states: {
      loading: "Cargando espacios...",
      errorTitle: "No pudimos abrir los espacios",
      error: "No se pudo cargar el listado de espacios.",
      retry: "Reintentar"
    }
  },
  reservations: {
    header: {
      title: "Mis reservas",
      mineTitle: "Mis reservas",
      receivedTitle: "Reservas recibidas",
      mineSubtitle: "Controla tus próximas sesiones, cambios, mensajes y reservas ya cerradas.",
      receivedSubtitle: "Gestiona solicitudes de tus espacios y marca como completadas las sesiones que ya pasaron.",
      mine: "Mis reservas",
      received: "Recibidas",
      modeLabel: "Tipo de reservas",
      new: "Nueva reserva"
    },
    states: {
      loading: "Cargando reservas...",
      error: "No se pudieron cargar tus reservas.",
      errorTitle: "No pudimos abrir tus reservas",
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
      eyebrow: "Tu próxima reserva",
      fallback: "Tu próxima reserva",
      today: "Tu próxima reserva es hoy",
      tomorrow: "Tu próxima reserva es mañana",
      inDays: "Tu próxima reserva es en {count} días"
    },
    timing: {
      today: "Hoy",
      tomorrow: "Mañana",
      inDays: "En {count} días",
      past: "Ya pasó",
      needsClosure: "Pendiente de cierre",
      expiredPending: "Pendiente vencida"
    },
    list: {
      eyebrow: "Seguimiento",
      title: "Todas las reservas",
      receivedEyebrow: "Gestión de espacios",
      receivedTitle: "Reservas recibidas",
      total: "{count} reservas"
    },
    filters: {
      searchPlaceholder: "Buscar por nombre del espacio o ciudad",
      date: "Fecha",
      sessionType: "Tipo de sesión",
      allSessionTypes: "Todos los tipos",
      order: "Orden",
      clear: "Limpiar filtros",
      orderOptions: {
        nearest: "Fecha más cercana",
        farthest: "Fecha más lejana",
        priceDesc: "Precio mayor",
        priceAsc: "Precio menor"
      }
    },
    tabs: {
      all: "Todos",
      active: "Activas",
      pending: "Pendientes",
      completed: "Completadas",
      cancelled: "Canceladas"
    },
    cards: {
      fallbackSpace: "Espacio musical",
      noLocation: "Ubicación pendiente",
      attendeesValue: "{count} asistentes",
      totalPrice: "Precio total"
    },
    actions: {
      view: "Ver reserva",
      contact: "Contactar",
      modify: "Modificar",
      cancel: "Cancelar",
      accept: "Aceptar",
      reject: "Rechazar",
      complete: "Completar",
      rebook: "Volver a reservar",
      viewReason: "Ver motivo",
      retry: "Reintentar"
    },
    empty: {
      title: "Todavía no tienes reservas",
      text: "Cuando cierres una sesión desde un espacio musical, aparecerá aquí con su estado, horario y acciones disponibles.",
      action: "Explorar espacios",
      receivedTitle: "Todavía no has recibido reservas",
      receivedText:
        "Añade un espacio musical para que otros usuarios puedan reservarlo. Cuando llegue una solicitud, aparecerá aquí con su estado, horario y acciones disponibles.",
      receivedAction: "Añadir espacio",
      filteredTitle: "No hay resultados con estos filtros",
      filteredText:
        "Prueba a cambiar la fecha, el texto de búsqueda o el estado para recuperar reservas."
    },
    detail: {
      eyebrow: "Detalle de reserva",
      notes: "Notas",
      cancellationReason: "Motivo de cancelación",
      close: "Cerrar",
      fields: {
        space: "Espacio",
        location: "Ubicación",
        date: "Fecha",
        schedule: "Horario",
        status: "Estado",
        sessionType: "Tipo de sesión",
        attendees: "Asistentes",
        price: "Precio",
        user: "Usuario",
        band: "Banda",
        createdAt: "Creada el"
      }
    },
    edit: {
      eyebrow: "Editar reserva",
      sessionDate: "Fecha",
      startTime: "Hora de inicio",
      endTime: "Hora de fin",
      attendees: "Asistentes",
      sessionType: "Tipo de sesión",
      notes: "Notas",
      cancel: "Cancelar",
      save: "Guardar cambios",
      saving: "Guardando...",
      updated: "La reserva se ha actualizado correctamente.",
      error: "No se pudo actualizar la reserva.",
      validationRequired: "Indica fecha, hora de inicio y hora de fin.",
      validationTimeOrder: "La hora de inicio debe ser anterior a la hora de fin.",
      validationAttendees: "Debe haber al menos un asistente.",
      validationCapacity: "El espacio admite como máximo {capacity} asistentes."
    },
    cancel: {
      prompt: "Indica el motivo de la cancelación",
      eyebrow: "Cancelación",
      title: "Cancelar reserva",
      text: "Vas a cancelar la reserva de {name}.",
      reasonLabel: "Motivo de cancelación",
      reasonPlaceholder: "Explica brevemente por qué cancelas esta reserva.",
      close: "Volver",
      confirm: "Cancelar reserva",
      submitting: "Cancelando...",
      emptyReason: "Debes indicar un motivo para cancelar la reserva.",
      success: "La reserva se ha cancelado correctamente.",
      error: "No se pudo cancelar la reserva."
    },
    stateAction: {
      eyebrow: "Gestión de reserva",
      close: "Cerrar confirmación",
      cancel: "Volver",
      invalid: "Esta acción no está disponible para el estado actual de la reserva.",
      error: "No se pudo actualizar el estado de la reserva.",
      ACCEPTED: {
        title: "Aceptar reserva",
        text: "Vas a aceptar la reserva de {name}.",
        confirm: "Aceptar reserva",
        success: "La reserva se ha aceptado correctamente."
      },
      REJECTED: {
        title: "Rechazar reserva",
        text: "Vas a rechazar la reserva de {name}.",
        confirm: "Rechazar reserva",
        success: "La reserva se ha rechazado correctamente."
      },
      COMPLETED: {
        title: "Marcar como completada",
        text: "Vas a marcar como completada la reserva de {name}.",
        confirm: "Completar reserva",
        success: "La reserva se ha marcado como completada."
      }
    },
    placeholders: {
      notFound: "No se encontró la reserva solicitada.",
      rebookUnavailable: "No se encontró el espacio original para repetir esta reserva."
    },
    sessionTypes: {
      REHEARSAL: "Ensayo",
      RECORDING: "Grabación",
      CLASS: "Clase",
      EVENT_PREPARATION: "Preparación de evento",
      OTHER: "Otra sesión"
    },
    statuses: {
      PENDING: "Pendiente",
      ACCEPTED: "Aceptada",
      COMPLETED: "Completada",
      CANCELLED: "Cancelada",
      REJECTED: "Rechazada"
    }
  },
  messages: {
    header: {
      eyebrow: "Mensajes FIOS",
      title: "Mensajes",
      subtitle: "Conversa con espacios y usuarios sobre tus reservas",
      unread: "{count} mensajes no leídos"
    },
    states: {
      error: "No se pudieron cargar tus conversaciones.",
      reservationUnavailable: "No tienes acceso a esa reserva o no existe en tu bandeja.",
      openFailed: "No se pudo abrir la conversación solicitada."
    },
    list: {
      title: "Conversaciones",
      summary: "{count} conversaciones",
      searchPlaceholder: "Buscar por espacio, ciudad, estado o texto",
      emptyTitle: "No tienes mensajes todavía",
      emptyText:
        "Tus conversaciones sobre reservas aparecerán aquí en cuanto exista al menos una reserva vinculada.",
      fallbackTitle: "Reserva FIOS",
      fallbackParticipant: "Usuario FIOS",
      noMessagesYet: "Todavía no hay mensajes en esta conversación."
    },
    filters: {
      scope: "Tipo de conversación",
      allConversations: "Todas",
      myReservations: "Mis reservas",
      managedReservations: "Recibidas",
      status: "Estado de la reserva",
      allStatuses: "Todas"
    },
    chat: {
      back: "Conversaciones",
      viewReservation: "Ver reserva",
      emptyTitle: "Todavía no hay mensajes",
      emptyText: "Escribe el primer mensaje para iniciar esta conversación.",
      emptyInfo: "Esta reserva todavía no tiene mensajes. Puedes iniciar la conversación ahora.",
      placeholderTitle: "Selecciona una conversación",
      placeholderText: "Elige una conversación de la lista para leer sus mensajes y responder.",
      composerPlaceholder: "Escribe un mensaje...",
      send: "Enviar",
      sending: "Enviando...",
      sendError: "No se pudo enviar el mensaje.",
      error: "No se pudieron cargar los mensajes de esta reserva.",
      locked: "Solo puedes enviar mensajes en reservas pendientes o aceptadas.",
      fallbackAuthor: "Usuario FIOS"
    },
    dates: {
      today: "Hoy",
      yesterday: "Ayer"
    }
  },
  events: {
    header: {
      eyebrow: "Agenda FIOS",
      title: "Eventos",
      subtitle: "Descubre conciertos, sesiones y actividades musicales"
    },
    actions: {
      list: "Lista",
      map: "Mapa",
      importTicketmaster: "Importar desde Ticketmaster",
      createEvent: "Crear evento",
      retry: "Reintentar",
      view: "Ver evento",
      edit: "Editar",
      archive: "Archivar",
      archiving: "Archivando...",
        backToList: "Ver eventos",
      ticketmaster: "Ver en Ticketmaster",
      externalLink: "Abrir enlace externo",
      viewSpace: "Ver espacio musical",
      viewBand: "Ver banda",
      cancel: "Cancelar",
      saveChanges: "Guardar cambios"
    },
    states: {
      loading: "Cargando eventos...",
      loadingDetail: "Cargando detalle del evento...",
      error: "No se pudo cargar el listado de eventos.",
      detailError: "No se pudo cargar el detalle del evento.",
      errorTitle: "No pudimos abrir este evento"
    },
    list: {
      eyebrow: "Agenda publicada",
      title: "Explora eventos",
      total: "{count} eventos visibles"
    },
    quickTabs: {
      upcoming: "Próximos",
      today: "Hoy",
      week: "Esta semana",
      free: "Gratis",
      external: "Externos"
    },
    filters: {
      search: "Búsqueda",
      searchPlaceholder: "Buscar por título, ciudad, lugar o género",
      city: "Ciudad",
      cityPlaceholder: "Santiago, Vigo, A Coruña...",
      date: "Fecha",
      genre: "Género musical",
      genrePlaceholder: "Rock, jazz, folk...",
      type: "Tipo de evento",
      allTypes: "Todos los tipos",
      source: "Origen",
      allSources: "Todos los orígenes",
      order: "Ordenar",
      freeOnly: "Solo gratis",
      clear: "Limpiar filtros",
      apply: "Aplicar filtros",
      orderOptions: {
        nearest: "Próximos primero",
        farthest: "Fecha más lejana",
        priceAsc: "Precio menor",
        priceDesc: "Precio mayor"
      }
    },
    empty: {
      title: "Todavía no hay eventos publicados",
      text: "Cuando exista agenda activa, la verás aquí con filtros, mapa y acceso al detalle.",
      action: "Restablecer vista",
      filteredTitle: "No hay resultados con estos filtros",
      filteredText: "Prueba a relajar la búsqueda, cambiar la fecha o revisar el origen del evento."
    },
    map: {
      loading: "Cargando mapa de eventos...",
      emptyTitle: "No hay eventos geolocalizados para este filtro",
      emptyText:
        "Puedes volver a la vista lista o ajustar la ciudad y la fecha para encontrar más puntos.",
      error: "No se pudo cargar la vista mapa."
    },
    cards: {
      imageFallback: "Imagen no disponible",
      locationFallback: "Ubicación pendiente",
      capacity: "Aforo {value}",
      onRequest: "Consultar precio"
    },
    types: {
      CONCERT: "Concierto",
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
      DRAFT: "Pendiente",
      PUBLISHED: "Publicado",
      CANCELLED: "Cancelado",
      ARCHIVED: "Archivado"
    },
    admin: {
      createEyebrow: "Nuevo evento",
      createTitle: "Crear evento",
      editEyebrow: "Editar evento",
      editTitle: "Actualizar evento",
      saving: "Guardando...",
      validation: "Completa título, fecha, lugar, ciudad y país antes de guardar.",
      capacityValidation: "Indica un aforo mayor que cero para los eventos internos.",
      priceValidation: "El precio de la entrada no puede ser negativo.",
      created: "El evento se ha creado correctamente.",
      updated: "El evento se ha actualizado correctamente.",
      deleted: "El evento se ha archivado correctamente.",
      error: "No se pudo guardar el evento.",
      deleteConfirm: "¿Quieres archivar este evento?",
      deleteError: "No se pudo archivar el evento.",
      fields: {
        title: "Título",
        type: "Tipo",
        status: "Estado",
        date: "Fecha",
        startTime: "Hora inicio",
        endTime: "Hora fin",
        city: "Ciudad",
        province: "Provincia",
        country: "País",
        venueName: "Lugar",
        genre: "Género musical",
        capacity: "Aforo",
        ticketPrice: "Precio",
        latitude: "Latitud",
        longitude: "Longitud",
        location: "Dirección / localización",
        posterImage: "Imagen",
        externalUrl: "Enlace externo",
        description: "Descripción",
        source: "Origen"
      }
    },
    ticketmaster: {
      eyebrow: "Importación externa",
      title: "Importar desde Ticketmaster",
      city: "Ciudad",
      cityPlaceholder: "A Coruña, Vigo, Santiago, Madrid...",
      keyword: "Artista o evento",
      keywordPlaceholder: "Rels B, festival, concierto...",
      genre: "Género musical",
      genrePlaceholder: "Opcional",
      startDate: "Fecha inicio",
      endDate: "Fecha fin",
      countryCode: "País",
      hint: "Deja la ciudad vacía para buscar en toda España; usa ciudad cuando quieras acotar a una zona concreta.",
      search: "Buscar",
      searching: "Buscando...",
      import: "Importar",
      importing: "Importando...",
      bulkImport: "Importar {count} resultados",
      bulkImporting: "Importando resultados...",
      empty: "No se encontraron eventos externos para esta búsqueda.",
      emptyHint: "Prueba sin artista o género, o cambia la ciudad si quieres descubrir eventos disponibles en otra zona.",
      broadenSearch: "Buscar sin artista ni género",
      searchAgain: "Buscar otra vez",
      clearKeyword: "Quitar artista",
      clearGenre: "Quitar género",
      useSpainAndSearch: "Usar España y buscar",
      activeFilters: "Búsqueda: {filters}",
      noActiveFilters: "Búsqueda amplia",
      countryMismatch: "La ciudad parece española, pero el país seleccionado es {country}.",
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
        missingKey: "La conexión con Ticketmaster no está lista. Avísale a la persona responsable.",
        disabled: "La conexión con Ticketmaster está desactivada.",
        timeout: "Ticketmaster está tardando demasiado. Prueba de nuevo en unos segundos.",
        rateLimit: "Ticketmaster ha limitado temporalmente las búsquedas. Espera unos segundos antes de reintentar.",
        provider: "Ticketmaster ha rechazado la consulta. Revisa país, ciudad y filtros."
      },
      error: "No se pudo consultar Ticketmaster.",
      imported: "Evento importado correctamente.",
      bulkImported: "{count} eventos importados desde Ticketmaster. {existing} ya estaban en FIOS.",
      importError: "No se pudo importar el evento externo."
    },
    tickets: {
      header: {
        back: "Volver",
        eyebrow: "Mis entradas",
        title: "Tus entradas",
        subtitle: "Consulta las entradas que has reservado en FIOS. El importe se paga presencialmente el día del evento.",
        explore: "Ver eventos"
      },
      states: {
        loading: "Cargando tus entradas...",
        errorTitle: "No pudimos abrir tus entradas",
        error: "No se pudieron cargar tus entradas."
      },
      actions: {
        retry: "Intentar de nuevo",
        viewEvent: "Ver evento",
        viewEventFor: "Ver evento: {title}"
      },
      stats: {
        label: "Resumen de tus entradas",
        total: "Entradas guardadas",
        upcoming: "Próximos eventos",
        paid: "Importe a pagar"
      },
      list: {
        eyebrow: "Entradas guardadas",
        title: "Lista de entradas",
        total: "{count} entradas"
      },
      card: {
        ticketLabel: "Entrada FIOS",
        cancelledLabel: "Entrada cancelada",
        purchasedAt: "Reservada el {date}",
        date: "Fecha",
        time: "Hora",
        place: "Lugar",
        amountDue: "Importe a pagar",
        pricePaid: "Importe a pagar",
        paymentNote: "El pago se realizará presencialmente el día del evento.",
        cancelledAt: "Renuncia",
        noTime: "Hora pendiente",
        locationFallback: "Lugar pendiente",
        untitled: "Evento sin título",
        upcoming: "Entrada para un próximo evento",
        past: "Evento ya celebrado",
        cancelled: "Reserva cancelada"
      },
      empty: {
        title: "Todavía no tienes entradas",
        text: "Cuando reserves una entrada para un evento de FIOS, aparecerá aquí guardada en tu cuenta.",
        action: "Ver eventos"
      }
    },
    detail: {
      back: "Volver",
      eyebrow: "Detalle del evento",
      descriptionTitle: "Descripción",
      emptyDescription: "Este evento todavía no tiene descripción publicada.",
      noGenre: "Género por definir",
      infoEyebrow: "Información",
      infoTitle: "Datos principales",
      linksEyebrow: "Contexto",
      linksTitle: "Enlaces y relaciones",
      mapEyebrow: "Mapa",
      mapTitle: "Ubicación del evento",
      mapEmpty: "Ubicación no disponible",
      noLocation: "Sin ciudad",
      noVenue: "Lugar pendiente",
      noAddress: "Sin dirección detallada",
      noSpace: "Sin espacio musical asociado",
      noBand: "Sin banda asociada",
      noCapacity: "Aforo no disponible",
      capacityValue: "{value} asistentes",
      reservedValue: "{value} reservadas",
      availableValue: "{value} disponibles",
      purchase: {
        eyebrow: "Entrada FIOS",
        title: "Reserva tu entrada",
        text: "FIOS guardará tu reserva. No se realiza ningún pago en la plataforma.",
        remainingText: "Quedan {count} entradas disponibles. Al reservar, FIOS guardará tu entrada sin realizar ningún pago.",
        savedTitle: "Entrada reservada",
        savedText: "Ya tienes una entrada reservada. Importe a pagar: {price}.",
        paymentNote: "El pago se realizará presencialmente el día del evento.",
        soldOutTitle: "Entradas agotadas",
        soldOutText: "No quedan entradas disponibles para este evento.",
        unavailableTitle: "Reserva no disponible",
        unavailableText: "Este evento no permite reservar entradas ahora mismo.",
        buyAction: "Reservar entrada",
        loginAction: "Iniciar sesión para reservar",
        savedAction: "Entrada reservada",
        soldOutAction: "Entradas agotadas",
        unavailableAction: "No disponible",
        saving: "Reservando entrada...",
        success: "Entrada reservada. Importe a pagar: {price}.",
        error: "No se pudo reservar la entrada.",
        cancelAction: "Renunciar a la entrada",
        cancelConfirm: "Vas a renunciar a esta entrada. La plaza volverá a quedar disponible.",
        cancelSuccess: "Has renunciado a la entrada. La plaza vuelve a estar disponible.",
        cancelError: "No se pudo renunciar a la entrada."
      },
      organizer: {
        eyebrow: "Organización",
        title: "Entradas reservadas",
        loading: "Cargando reservas...",
        empty: "Todavía no hay entradas reservadas.",
        error: "No se pudieron cargar las entradas reservadas.",
        userFallback: "Usuario #{id}"
      },
      fields: {
        date: "Fecha",
        time: "Horario",
        city: "Ciudad",
        venue: "Lugar",
        price: "Precio",
        capacity: "Aforo",
        reserved: "Reservadas",
        available: "Disponibles",
        location: "Dirección",
        space: "Espacio musical",
        band: "Banda"
      }
    }
  },
  bands: {
    header: {
      title: "Bandas",
      mineTitle: "Mis bandas",
      subtitle: "Explora proyectos musicales activos, géneros y bandas que buscan integrantes.",
      mineSubtitle: "Gestiona tus proyectos, miembros y búsquedas abiertas desde un único lugar.",
      create: "Crear banda"
    },
    states: {
      loading: "Cargando bandas...",
      error: "No se pudieron cargar las bandas.",
      errorTitle: "No pudimos abrir las bandas",
      partialData: "Se cargaron las bandas principales, pero faltan algunos datos secundarios.",
      partialMembers: "No se pudieron cargar todos los miembros de las bandas.",
      savingBand: "Guardando banda...",
      publishingRecruitment: "Publicando búsqueda...",
      publishingEvent: "Enviando evento...",
      closingRecruitment: "Cerrando..."
    },
    stats: {
      total: "Total de bandas",
      totalMine: "Mis bandas",
      totalPublic: "Bandas en FIOS",
      active: "Bandas activas",
      members: "Miembros totales",
      recruitments: "Búsquedas activas",
      genres: "Géneros activos"
    },
    members: {
      eyebrow: "Miembros",
      title: "Equipo principal",
      count: "{count} miembros",
      empty: "Esta banda todavía no tiene miembros visibles.",
      noInstruments: "Sin instrumentos asociados"
    },
    memberManagement: {
      eyebrow: "Miembros de la banda",
      loading: "Cargando miembros...",
      userId: "ID de usuario",
      userSearch: "Buscar usuario",
      userSearchPlaceholder: "Email o nombre",
      userSearchHint: "Escribe y selecciona una persona de la lista.",
      userSearchMinLength: "Escribe al menos 2 caracteres.",
      searchingUsers: "Buscando usuarios...",
      userSearchEmpty: "No hay usuarios disponibles con esa búsqueda.",
      userSearchError: "No se pudieron buscar usuarios.",
      selectedUser: "Seleccionado: {name}",
      add: "Añadir miembro",
      adding: "Añadiendo...",
      removing: "Actualizando...",
      remove: "Quitar",
      empty: "Esta banda todavía no tiene miembros activos.",
      editPermission: "Solo las personas líderes o administradoras pueden editar bandas.",
      managePermission: "Solo las personas líderes o administradoras pueden gestionar miembros.",
      loadError: "No se pudieron cargar los miembros.",
      invalidUserId: "Introduce un ID de usuario válido.",
      invalidUserSelection: "Selecciona un usuario de la lista.",
      addSuccess: "Miembro añadido correctamente.",
      addError: "No se pudo añadir el miembro.",
      roleUpdated: "Rol actualizado correctamente.",
      roleError: "No se pudo actualizar el rol.",
      removeConfirm: "Vas a quitar a {name} de la banda. ¿Continuar?",
      removed: "Miembro eliminado de la banda.",
      removeError: "No se pudo quitar el miembro.",
      userFallback: "Usuario #{id}"
    },
    recruitments: {
      eyebrow: "Búsquedas",
      title: "Búsquedas activas",
      publicEyebrow: "Oportunidades",
      publicTitle: "Bandas que buscan miembros",
      count: "{count} búsquedas",
      empty: "No hay búsquedas activas para tus bandas.",
      publicEmpty: "No hay búsquedas abiertas ahora mismo.",
      vacancyShort: "{count} vac.",
      closed: "La búsqueda se ha cerrado correctamente.",
      closeError: "No se pudo cerrar la búsqueda."
    },
    list: {
      eyebrow: "Catálogo personal",
      title: "Todas mis bandas",
      mineEyebrow: "Catálogo personal",
      mineTitle: "Todas mis bandas",
      publicEyebrow: "Catálogo público",
      publicTitle: "Bandas en FIOS",
      total: "{count} bandas"
    },
    views: {
      explore: "Explorar bandas",
      mine: "Mis bandas"
    },
    public: {
      eyebrow: "Explorar",
      title: "Descubre bandas y proyectos musicales",
      text: "Consulta bandas activas, géneros, ciudades y ofertas abiertas sin iniciar sesión.",
      register: "Crear cuenta",
      login: "Iniciar sesión",
      recruitments: "Ver búsquedas abiertas",
      howEyebrow: "Cómo usarlo",
      howTitle: "Del catálogo a la colaboración",
      howText: "Primero explora bandas, después abre su detalle y, si encaja, revisa sus búsquedas activas.",
      stepExplore: "Filtra por ciudad, género o estado",
      stepOpen: "Abre el detalle de cualquier banda",
      stepRecruit: "Consulta ofertas para unirte"
    },
    filters: {
      searchPlaceholder: "Buscar por nombre de banda o ciudad",
      genre: "Género musical",
      allGenres: "Todos los géneros",
      status: "Estado",
      allStates: "Todos los estados",
      order: "Ordenar",
      clear: "Limpiar filtros",
      orderOptions: {
        recent: "Actividad reciente",
        name: "Nombre",
        city: "Ciudad",
        members: "Más miembros"
      }
    },
    tabs: {
      all: "Todas",
      active: "Activas",
      forming: "En formación",
      recruiting: "Buscando miembros"
    },
    actions: {
      viewBand: "Ver banda",
      viewDetail: "Ver detalle",
      manageMembers: "Gestionar miembros",
      publishRecruitment: "Publicar búsqueda",
      publishEvent: "Proponer evento",
      leaveBand: "Salir de la banda",
      searchMembers: "Buscar miembros",
      edit: "Editar",
      viewRecruitment: "Ver oferta",
      manageRecruitment: "Gestionar",
      closeRecruitment: "Cerrar",
      retry: "Reintentar",
      cancel: "Cancelar"
    },
    detail: {
      loading: "Cargando banda...",
      errorTitle: "No pudimos abrir esta banda",
      error: "No se pudo cargar el detalle de la banda.",
      eyebrow: "Detalle de banda",
      imageFallback: "Imagen de banda no disponible",
      viewRecruitments: "Ver búsquedas activas",
      membersTitle: "Miembros de la banda",
      recruitmentsTitle: "Búsquedas abiertas",
      heroLead: "{members} en {city}",
      createdAtInline: "Desde {date}",
      activeRecruitment: "Búsqueda abierta",
      roleGuest: "Visitante",
      fields: {
        members: "Miembros",
        recruitments: "Búsquedas",
        createdAt: "Fecha de alta",
        role: "Tu relación"
      }
    },
    leave: {
      eyebrow: "Miembros",
      title: "Salir de la banda",
      confirm: '¿Tienes claro que quieres salir de "{name}"?',
      confirmAction: "Salir de la banda",
      success: "Has salido de la banda correctamente.",
      error: "No se pudo salir de la banda."
    },
    cards: {
      imageFallback: "Imagen de banda no disponible",
      noDescription: "Sin descripción disponible todavía.",
      noGenre: "Género por definir",
      noCity: "Ciudad pendiente",
      membersValue: "{count} miembros"
    },
    empty: {
      title: "Todavía no formás parte de ninguna banda",
      text: "Crea tu primera banda para gestionar miembros, publicar búsquedas y concentrar tu actividad musical en FIOS.",
      publicTitle: "No hay bandas públicas aínda",
      publicText: "Cuando haya proyectos activos aparecerán aquí junto con sus búsquedas abiertas.",
      create: "Crear banda",
      explore: "Explorar bandas",
      filteredTitle: "No hay bandas que coincidan con estos filtros",
      filteredText: "Prueba a cambiar el género, el estado o el texto de búsqueda."
    },
    modals: {
      band: {
        createEyebrow: "Nueva banda",
        createTitle: "Crear banda",
        editEyebrow: "Editar banda",
        editTitle: "Actualizar banda",
        name: "Nombre",
        genre: "Género principal",
        city: "Ciudad base",
        image: "Imagen",
        description: "Descripción",
        submit: "Crear banda",
        update: "Guardar cambios",
        validation: "Completa nombre, género y ciudad antes de guardar.",
        created: "La banda se ha creado correctamente.",
        updated: "La banda se ha actualizado correctamente.",
        error: "No se pudo guardar la banda."
      },
      recruitment: {
        eyebrow: "Nueva búsqueda",
        title: "Publicar búsqueda de miembros",
        band: "Banda",
        selectBand: "Selecciona una banda",
        instrument: "Instrumento",
        selectInstrument: "Selecciona un instrumento",
        positionTitle: "Título",
        role: "Rol buscado",
        level: "Nivel requerido",
        city: "Ciudad",
        vacancies: "Vacantes",
        description: "Descripción",
        submit: "Publicar búsqueda",
        validation:
          "Completa banda, instrumento, título, rol, ciudad y vacantes antes de publicar.",
        created: "La búsqueda se ha publicado correctamente.",
        error: "No se pudo publicar la búsqueda."
      },
      event: {
        eyebrow: "Agenda de la banda",
        title: "Proponer evento",
        band: "Banda",
        selectBand: "Selecciona una banda",
        musicalSpace: "Espacio musical",
        noMusicalSpace: "Sin espacio asociado",
        eventTitle: "Título",
        type: "Tipo",
        date: "Fecha",
        startTime: "Hora de inicio",
        endTime: "Hora de fin",
        venueName: "Lugar",
        city: "Ciudad",
        province: "Provincia",
        country: "País",
        genre: "Género musical",
        capacity: "Aforo",
        ticketPrice: "Precio",
        location: "Dirección",
        posterImage: "Imagen del cartel",
        description: "Descripción",
        submit: "Enviar a revisión",
        validation: "Completa banda, título, tipo, fecha, lugar, ciudad y país antes de enviar.",
        timeValidation: "La hora de inicio debe ser anterior a la hora de fin.",
        created: "El evento queda pendiente de aprobación del administrador.",
        error: "No se pudo enviar el evento."
      }
    },
    placeholders: {
      noLeaderBand: "Necesitas ser responsable de al menos una banda para publicar una búsqueda.",
      noLeaderEventBand: "Necesitas ser responsable de una banda activa para proponer un evento."
    },
    statuses: {
      active: "Activa",
      inactive: "Inactiva",
      forming: "En formación",
      recruiting: "Buscando miembros"
    },
    levels: {
      BEGINNER: "Inicial",
      INTERMEDIATE: "Intermedio",
      ADVANCED: "Avanzado",
      PROFESSIONAL: "Profesional"
    },
    roles: {
      LEADER: "Responsable",
      MEMBER: "Miembro"
    }
  },
  admin: {
    header: {
      eyebrow: "Administración FIOS",
      title: "Panel de administración",
      subtitle:
        "Supervisa usuarios, espacios, reservas, reseñas y eventos desde un único lugar.",
      roleBadge: "Solo administración"
    },
    actions: {
      refreshOverview: "Actualizar resumen",
      refresh: "Recargar",
      retry: "Reintentar",
      resetFilters: "Limpiar filtros",
      close: "Cerrar"
    },
    sections: {
      navigation: "Navegación del panel de gestión",
      overview: "Resumen",
      users: "Usuarios",
      spaces: "Espacios",
      reservations: "Reservas",
      reviews: "Reseñas",
      events: "Eventos",
      recruitments: "Búsquedas miembros"
    },
    summary: {
      visibleOfTotal: "{visible} de {total}"
    },
    forbidden: {
      title: "Acceso restringido",
      text: "Estas pantallas solo están disponibles para cuentas con permiso de administración.",
      action: "Volver al inicio"
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
      PENDING: "Pendiente",
      APPROVED: "Aprobado",
      REJECTED: "Rechazado"
    },
    metrics: {
      users: "Usuarios",
      usersSubtitle: "{count} administradores",
      pendingSpaces: "Espacios pendientes",
      pendingSpacesSubtitle: "{count} aprobados",
      activeReservations: "Reservas activas",
      activeReservationsSubtitle: "{count} pendientes",
      reviews: "Reseñas",
      reviewsSubtitle: "Moderación disponible",
      events: "Eventos",
      eventsSubtitle: "Catálogo administrativo",
      recruitments: "Búsquedas de miembros",
      recruitmentsSubtitle: "Ofertas supervisadas"
    },
    states: {
      loadingOverview: "Cargando resumen del panel...",
      loadingUsers: "Cargando usuarios...",
      loadingSpaces: "Cargando espacios musicales...",
      loadingReservations: "Cargando reservas...",
      loadingReviews: "Cargando reseñas...",
      loadingEvents: "Cargando eventos...",
      loadingRecruitments: "Cargando búsquedas de miembros...",
      errorTitle: "No se pudo cargar esta sección",
      overviewError: "No se pudo cargar el resumen del panel de administración.",
      usersError: "No se pudo cargar la lista de usuarios.",
      spacesError: "No se pudo cargar la lista de espacios.",
      reservationsError: "No se pudo cargar la lista de reservas.",
      reviewsError: "No se pudo cargar la lista de reseñas.",
      eventsError: "No se pudo cargar la lista de eventos.",
      recruitmentsError: "No se pudo cargar la lista de búsquedas de miembros.",
      actionError: "No se pudo completar la acción solicitada."
    },
    filters: {
      allRoles: "Todos los roles",
      allStates: "Todos los estados",
      allStatuses: "Todos los estados",
      allSources: "Todos los orígenes",
      allRatings: "Todas las puntuaciones",
      allInstruments: "Todos los instrumentos"
    },
    fallbacks: {
      space: "Espacio musical",
      event: "Evento",
      recruitment: "Búsqueda",
      currentSession: "sesión actual"
    },
    overview: {
      title: "Visión general",
      subtitle:
        "Lectura rápida del estado del ecosistema y del uso de los recursos administrables.",
      userBreakdown: "Distribución de usuarios",
      spaceBreakdown: "Estados de aprobación",
      reservationBreakdown: "Estados de reserva",
      securityTitle: "Acceso protegido",
      securityText:
        "Sesión actual: {email}. Solo las cuentas con permiso de administración pueden entrar aquí.",
      securityAction: "Revisar usuarios"
    },
    users: {
      title: "Gestión de usuarios",
      subtitle:
        "Consulta el listado real de usuarios y activa o desactiva cuentas sin exponer datos sensibles.",
      emptyTitle: "No hay usuarios para mostrar",
      emptyText: "Ajusta los filtros o vuelve a cargar la sección para recuperar resultados.",
      activatedSuccess: "Usuario activado correctamente.",
      deactivatedSuccess: "Usuario desactivado correctamente.",
      promotedSuccess: "Usuario convertido en administrador correctamente.",
      adminRoleRevokedSuccess: "Rol de administrador retirado correctamente.",
      selfActionBlocked: "No puedes modificar tu propia cuenta de administrador.",
      confirmActivateTitle: "Activar usuario",
      confirmDeactivateTitle: "Desactivar usuario",
      confirmPromoteTitle: "Hacer administrador",
      confirmRevokeAdminTitle: "Quitar administrador",
      confirmActivateText: "Vas a activar la cuenta de {name}.",
      confirmDeactivateText: "Vas a desactivar la cuenta de {name}.",
      confirmPromoteText: "Vas a convertir la cuenta de {name} en administradora.",
      confirmRevokeAdminText:
        "Vas a quitar el rol de administrador a la cuenta de {name}.",
      filters: {
        search: "Buscar por nombre, email o teléfono"
      },
      columns: {
        name: "Nombre",
        email: "Email",
        phone: "Teléfono",
        role: "Rol",
        status: "Estado",
        createdAt: "Alta",
        actions: "Acciones"
      },
      actions: {
        activate: "Activar",
        deactivate: "Desactivar",
        promoteToAdmin: "Hacer administrador",
        removeAdminRole: "Quitar administrador",
        currentUser: "Tu cuenta"
      }
    },
    spaces: {
      title: "Aprobación de espacios",
      subtitle:
        "Controla el estado de aprobación de los espacios musicales publicados en la plataforma.",
      emptyTitle: "No hay espacios para mostrar",
      emptyText: "No se encontraron espacios con los filtros actuales.",
      confirmTitle: "Actualizar aprobación",
      confirmText: 'Vas a marcar "{name}" como {status}.',
      updatedSuccess: "Estado del espacio actualizado correctamente.",
      filters: {
        search: "Buscar por nombre, ciudad o responsable",
        city: "Filtrar por ciudad"
      },
      columns: {
        name: "Espacio",
        city: "Ciudad",
        manager: "Responsable",
        type: "Tipo",
        approvalStatus: "Aprobación",
        active: "Activo",
        actions: "Acciones"
      },
      actions: {
        approve: "Aprobar",
        reject: "Rechazar",
        markPending: "Marcar pendiente",
        availability: "Disponibilidad",
        viewPublic: "Ver público",
        applyStatus: "Aplicar estado"
      }
    },
    reservations: {
      title: "Supervisión de reservas",
      subtitle: "Consulta sesiones reales, enlaza conversaciones y cancela reservas con motivo.",
      emptyTitle: "No hay reservas para mostrar",
      emptyText: "Prueba a cambiar texto, fecha o estado para recuperar resultados.",
      cancelTitle: "Cancelar reserva",
      cancelText: "Vas a cancelar la reserva asociada a {name}.",
      cancelReasonLabel: "Motivo de cancelación",
      cancelReasonPlaceholder: "Explica al usuario por qué se cancela esta reserva.",
      cancelReasonRequired: "Debes indicar un motivo para cancelar la reserva.",
      cancelSuccess: "Reserva cancelada correctamente.",
      filters: {
        search: "Buscar por espacio, usuario, banda o notas"
      },
      columns: {
        space: "Espacio",
        user: "Usuario",
        date: "Fecha",
        time: "Horario",
        status: "Estado",
        price: "Precio",
        attendees: "Asistentes",
        actions: "Acciones"
      },
      actions: {
        messages: "Mensajes",
        cancel: "Cancelar"
      }
    },
    reviews: {
      title: "Moderación de reseñas",
      subtitle:
        "Elimina contenido conflictivo y supervisa valoraciones publicadas sobre espacios y usuarios.",
      emptyTitle: "No hay reseñas para mostrar",
      emptyText: "No se encontraron reseñas con los filtros actuales.",
      noComment: "Sin comentario adicional.",
      deletedSuccess: "Reseña eliminada correctamente.",
      confirmDeleteTitle: "Eliminar reseña",
      confirmDeleteText: "Vas a borrar una reseña de {target}.",
      ratingLabel: "{value}/5",
      types: {
        SPACE: "Espacio",
        USER: "Usuario"
      },
      filters: {
        search: "Buscar por objetivo, autor, usuario o comentario",
        ratingValue: "{value} estrellas"
      },
      columns: {
        type: "Tipo",
        space: "Objetivo",
        user: "Autor",
        rating: "Puntuación",
        comment: "Comentario",
        date: "Fecha",
        actions: "Acciones"
      },
      actions: {
        delete: "Borrar"
      }
    },
    events: {
      title: "Gestión de eventos",
      subtitle:
        "Supervisa eventos propios y externos, abre su detalle público y actualiza sus datos.",
      emptyTitle: "No hay eventos para mostrar",
      emptyText: "No se encontraron eventos con los filtros actuales.",
      createdSuccess: "Evento creado correctamente.",
      updatedSuccess: "Evento actualizado correctamente.",
      publishedSuccess: "Evento publicado correctamente.",
      archivedSuccess: "Evento archivado correctamente.",
      saveError: "No se pudo guardar el evento.",
      confirmArchiveTitle: "Archivar evento",
      confirmArchiveText: 'Vas a archivar el evento "{title}".',
      filters: {
        search: "Buscar por título, lugar, ciudad o género",
        city: "Filtrar por ciudad"
      },
      columns: {
        title: "Evento",
        date: "Fecha",
        city: "Ciudad",
        source: "Origen",
        status: "Estado",
        type: "Tipo",
        actions: "Acciones"
      },
      actions: {
        create: "Crear evento",
        importTicketmaster: "Importar Ticketmaster",
        view: "Ver detalle",
        publish: "Publicar",
        edit: "Editar",
        archive: "Archivar"
      }
    },
    recruitments: {
      title: "Búsquedas de miembros",
      subtitle:
        "Supervisa ofertas publicadas por las bandas y elimina publicaciones cuando sea necesario.",
      emptyTitle: "No hay búsquedas para mostrar",
      emptyText: "No se encontraron publicaciones con los filtros actuales.",
      deletedSuccess: "Búsqueda eliminada correctamente.",
      confirmDeleteTitle: "Eliminar búsqueda",
      confirmDeleteText: 'Vas a borrar la publicación "{title}".',
      statuses: {
        OPEN: "Abierta",
        CLOSED: "Cerrada"
      },
      filters: {
        search: "Buscar por título, banda, instrumento o ciudad"
      },
      columns: {
        title: "Título",
        band: "Banda",
        instrument: "Instrumento",
        level: "Nivel",
        city: "Ciudad",
        status: "Estado",
        publicationDate: "Publicación",
        actions: "Acciones"
      },
      actions: {
        delete: "Eliminar"
      }
    }
  }
};
