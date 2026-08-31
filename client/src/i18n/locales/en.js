export default {
  common: {
    actions: {
      search: "Search",
      login: "Sign in",
      register: "Sign up",
      logout: "Sign out",
      save: "Save",
      back: "Back",
      close: "Close",
      loading: "Loading..."
    },
    labels: {
      city: "City",
      date: "Date",
      spaceType: "Space type",
      genre: "Music genre",
      budget: "Budget",
      people: "People",
      time: "Time",
      capacity: "Capacity",
      onRequest: "On request",
      free: "Free",
      notifications: "Notifications",
      language: "Language",
      navigation: "Navigation"
    },
    states: {
      noData: "No data available",
      backendUnavailable: "We could not load the data right now."
    },
    imageUpload: {
      upload: "Upload image",
      uploading: "Uploading...",
      clear: "Remove image",
      urlPlaceholder: "https://example.com/image.jpg",
      error: "The image could not be uploaded."
    }
  },
  apiErrors: {
    generic: "We could not complete the action. Please try again.",
    network: "The service cannot be reached. Check your connection or try again in a few seconds.",
    unavailable: "The service is not available right now. Please try again in a few minutes.",
    badRequest: "Check the information you entered. Something does not look right.",
    unauthorized: "Your session has expired. Sign in again to continue.",
    forbidden: "You do not have permission to do this.",
    notFound: "We could not find what you tried to open. It may no longer be available.",
    conflict: "This could not be saved because similar or related information already exists.",
    rateLimited: "Too many attempts were made in a row. Wait a few seconds and try again.",
    internal: "Something unexpected happened. Please try again later.",
    malformed: "We could not read the information sent. Check it and try again.",
    badCredentials: "The email or password is not correct.",
    accountDisabled: "This account is disabled. Contact administration if you think this is a mistake.",
    emailExists: "An account with that email already exists.",
    phoneExists: "An account with that phone number already exists.",
    invalidEmail: "Enter a valid email address.",
    invalidPhone: "Enter a valid phone number.",
    requiredFields: "Complete the required fields before continuing.",
    futureBirthDate: "Birth date cannot be in the future.",
    passwordMismatch: "The passwords do not match.",
    passwordWeak: "The password must include at least one letter and one number.",
    currentPasswordIncorrect: "The current password is not correct.",
    passwordSameAsCurrent: "The new password must be different from the current one.",
    invalidTimeRange: "The start time must be before the end time.",
    invalidDate: "The selected date is not valid.",
    invalidNumber: "Check the numbers you entered. One of them is outside the allowed range.",
    tooLong: "The text is too long.",
    timeNotAvailable: "That time is not available. Choose another slot.",
    noTicketsAvailable: "There are no tickets left for this event.",
    eventAlreadyReserved: "You already have an active reservation for this event.",
    eventAlreadyPurchased: "You already have an active reservation for this event.",
    alreadyExists: "A similar or related item already exists.",
    spaceEquipmentAlreadyExists: "This equipment is already associated with the space.",
    noPermission: "You cannot do this with this account.",
    actionNotAvailable: "This action is not available right now.",
    locationUnavailable: "We could not check the location. Review the address or coordinates.",
    invalidFile: "The uploaded file is not valid.",
    fileTooLarge: "The file exceeds the maximum allowed size."
  },
  apiFieldErrors: {
    FIELD_REQUIRED: "Complete this field.",
    INVALID_EMAIL: "Enter a valid email address.",
    INVALID_PHONE: "Enter a valid phone number.",
    INVALID_LENGTH: "Check the length of this field.",
    INVALID_NUMBER: "Check this number.",
    INVALID_DATE: "The selected date is not valid.",
    INVALID_TIME_RANGE: "The start time must be before the end time.",
    INVALID_DATE_RANGE: "The start date must be before or equal to the end date.",
    INVALID_COORDINATES: "Check the coordinates entered.",
    INVALID_VALUE: "Check this field."
  },
  navbar: {
    brandTagline: "music platform",
    menu: {
      search: "Search",
      spaces: "Spaces",
      reservations: "Reservations",
      events: "Events",
      bands: "Bands",
      messages: "Messages"
    },
    dropdowns: {
      smartSearch: "Smart search",
      smartSearchMeta: "Natural language and filters",
      searchMap: "Map",
      searchMapMeta: "Map view",
      exploreSpaces: "Explore spaces",
      exploreSpacesMeta: "Public catalog",
      mySpaces: "My spaces",
      mySpacesMeta: "Manage your places",
      createSpace: "Create space",
      createSpaceMeta: "New place",
      myReservations: "My reservations",
      myReservationsMeta: "Your sessions",
      receivedReservations: "Received reservations",
      receivedReservationsMeta: "Requests for your places",
      upcomingEvents: "Upcoming events",
      upcomingEventsMeta: "Published agenda",
      eventMap: "Event map",
      eventMapMeta: "Explore by city",
      exploreBands: "Explore bands",
      exploreBandsMeta: "Main directory",
      myBands: "My bands",
      myBandsMeta: "Personal overview",
      createBand: "Create band",
      createBandMeta: "New project",
      memberSearch: "Member searches",
      memberSearchMeta: "Active listings"
    },
    userMenu: {
      profile: "My profile",
      profileMeta: "Account details",
      reservations: "My reservations",
      reservationsMeta: "Booked sessions",
      reviews: "My reviews",
      reviewsMeta: "Pending and published",
      tickets: "My tickets",
      ticketsMeta: "Reserved events",
      bands: "My bands",
      bandsMeta: "Bands and projects",
      favorites: "Favorites",
      favoritesMeta: "Saved spaces",
      admin: "Administration",
      adminMeta: "Management panel",
      logoutMeta: "Sign out of your account safely."
    },
    notifications: {
      title: "Notifications",
      summary: "{count} unread messages",
      empty: "You have no notifications",
      viewMessages: "View messages",
      viewMessagesMeta: "Open your inbox and continue the conversation",
      fallbackConversation: "Conversation",
      pending: "Recent activity"
    }
  },
  account: {
    login: {
      title: "Sign in",
      description:
        "Access FIOS with your account.",
      eyebrow: "Account",
      badge: "Sign in",
      heading: "Welcome back to FIOS",
      subtitle:
        "Use your email and password to sign in and recover your activity.",
      email: "Email",
      password: "Password",
      submit: "Enter FIOS",
      submitting: "Signing in...",
      noAccount: "No account yet?",
      registerLink: "Create one",
      registeredSuccess: "Account created successfully. You can sign in now.",
      autoLoginFallback:
        "Your account was created, but automatic sign-in failed. Please sign in manually to continue."
    },
    register: {
      title: "Create account",
      description: "Create an account to keep your reservations, messages and preferences.",
      eyebrow: "Account",
      badge: "Sign up",
      heading: "Create your account",
      subtitle:
        "Create your account and, if everything goes well, you will enter FIOS automatically with your session started.",
      name: "Name",
      firstSurname: "First surname",
      secondSurname: "Second surname",
      phone: "Phone",
      email: "Email",
      birthDate: "Birth date",
      instrument: "Instrument",
      noInstrument: "No instrument",
      loadingInstruments: "Loading instruments...",
      instrumentHelp: "You can add the main instrument you play now.",
      instrumentCatalogError: "Unable to load the instrument catalog.",
      validationInstrument: "Select a valid instrument from the catalog.",
      password: "Password",
      confirmPassword: "Confirm password",
      passwordRulesTitle: "Your password must:",
      formRulesTitle: "Before sending, check:",
      passwordRules: {
        minLength: "Be at least 8 characters",
        hasLetter: "Include at least one letter",
        hasNumber: "Include at least one number",
        notEmpty: "Not be empty"
      },
      formRules: {
        validEmail: "Use a valid email",
        requiredName: "Name is required",
        requiredFirstSurname: "First surname is required",
        requiredPhone: "Phone is required",
        validPhone: "Use a valid phone",
        validBirthDate: "Birth date must be valid"
      },
      submit: "Create account",
      submitting: "Creating account...",
      backToLogin: "Back to login"
    },
    errors: {
      requiredLogin: "Email and password are required.",
      loginGeneric: "Unable to sign in. Please try again.",
      registerGeneric: "Unable to complete the registration.",
      network: "The sign-in service cannot be reached.",
      badCredentials: "The email or password is not correct.",
      emailExists: "That email is already registered.",
      phoneExists: "That phone number is already registered.",
      malformed: "We could not read the information sent.",
      requiredFields: "Complete the required fields before continuing.",
      passwordMismatch: "The passwords do not match.",
      invalidEmail: "Enter a valid email address.",
      passwordWeak: "Password must include at least one letter and one number.",
      futureBirthDate: "Birth date cannot be in the future."
    }
  },
  profile: {
    header: {
      eyebrow: "FIOS Profile",
      title: "My profile",
      subtitle: "Manage your personal information and account security.",
      back: "Back",
      memberSince: "Member since {date}"
    },
    actions: {
      editProfile: "Edit profile",
      changePassword: "Change password",
      saveChanges: "Save changes",
      updatePassword: "Update password",
      cancel: "Cancel",
      retry: "Retry"
    },
    states: {
      loading: "Loading profile...",
      errorTitle: "We could not open your profile",
      error: "Unable to load profile information."
    },
    summary: {
      reservations: "Reservations",
      bands: "Bands",
      favorites: "Favorites",
      unreadMessages: "Unread messages"
    },
    details: {
      eyebrow: "Live data",
      title: "Account information"
    },
    fields: {
      name: "Name",
      firstSurname: "First surname",
      secondSurname: "Second surname",
      email: "Email",
      phone: "Phone",
      birthDate: "Birth date",
      instruments: "Instruments",
      instrument: "Instrument",
      noInstrument: "No instrument",
      role: "Role",
      createdAt: "Created at"
    },
    edit: {
      eyebrow: "Editing",
      title: "Update profile",
      subtitle: "Adjust your basic details. Email stays as the sign-in reference.",
      saving: "Saving...",
      loadingInstruments: "Loading instruments...",
      instrumentHelp: "Select the main instrument you play from the FIOS catalog.",
      instrumentCatalogError: "Unable to load the instrument catalog.",
      validationRequired: "Complete name, first surname and phone before saving.",
      validationBirthDate: "Birth date cannot be in the future.",
      validationInstrument: "Select a valid instrument from the catalog.",
      error: "Unable to update the profile."
    },
    password: {
      eyebrow: "Security",
      title: "Change password",
      subtitle: "Update your password without storing sensitive values in the client.",
      currentPassword: "Current password",
      newPassword: "New password",
      confirmPassword: "Confirm new password",
      saving: "Updating...",
      validationRequired: "Complete all three password fields before continuing.",
      validationLength: "The new password must be at least 8 characters long.",
      validationPolicy: "The new password must include at least one letter and one number.",
      validationMatch: "The new password confirmation does not match.",
      error: "Unable to change the password."
    },
    image: {
      eyebrow: "Image",
      title: "Profile image",
      subtitle: "Customize your public account image.",
      previewAlt: "Profile image",
      empty: "No profile image",
      inputLabel: "Image",
      placeholder: "https://example.com/avatar.jpg",
      save: "Update image",
      remove: "Remove image",
      saving: "Updating...",
      removing: "Removing...",
      validationRequired: "Provide an image before saving.",
      validationUrl: "The image link is not valid.",
      error: "Unable to update the profile image.",
      removeError: "Unable to remove the profile image."
    },
    notices: {
      profileUpdated: "The profile has been updated successfully.",
      passwordUpdated: "The password has been updated successfully.",
      imageUpdated: "The profile image has been updated successfully.",
      imageRemoved: "The profile image has been removed successfully."
    },
    roles: {
      USER: "User",
      ADMIN: "Administrator"
    },
    status: {
      active: "Active",
      inactive: "Inactive"
    },
    fallbacks: {
      noData: "No data",
      noInstruments: "No associated instruments",
      noInstrument: "No associated instrument",
      user: "FIOS user"
    }
  },
  favorites: {
    header: {
      eyebrow: "FIOS favorites",
      back: "Back",
      title: "My favorites",
      subtitle: "Saved spaces to book later.",
      explore: "Explore spaces"
    },
    actions: {
      retry: "Retry"
    },
    stats: {
      total: "Favorites",
      cities: "Cities",
      recent: "Recent"
    },
    filters: {
      search: "Search",
      searchPlaceholder: "Search by name or city",
      city: "City",
      allCities: "All cities",
      type: "Space type",
      allTypes: "All types",
      order: "Sort by",
      clear: "Clear filters",
      orderOptions: {
        recent: "Most recent",
        name: "Name",
        city: "City",
        rating: "Top rated"
      }
    },
    list: {
      eyebrow: "Saved list",
      title: "Favorite spaces",
      total: "{count} favorites"
    },
    card: {
      imagePlaceholder: "Image unavailable",
      locationFallback: "Location pending",
      capacityValue: "{value} people",
      soundproofed: "Soundproofing",
      soundproofedYes: "Soundproofed",
      soundproofedNo: "Not soundproofed",
      soundproofedUnknown: "Needs review",
      rating: "Rating",
      ratingValue: "{rating} ({count})",
      noRating: "No reviews",
      savedAt: "Saved {date}",
      viewDetail: "View detail",
      remove: "Remove favorite",
      removing: "Removing..."
    },
    empty: {
      title: "You do not have favorites yet",
      text: "Save spaces from the detail view to bring them back quickly when you want to book.",
      filteredTitle: "No results match these filters",
      filteredText: "Try changing the search or clearing filters to recover favorites.",
      exploreAction: "Explore spaces",
      resetAction: "Reset filters"
    },
    states: {
      loading: "Loading favorites...",
      errorTitle: "We could not open your favorites",
      error: "Unable to load the favorites list."
    },
    notices: {
      removed: "The space has been removed from favorites.",
      removeError: "Unable to remove the space from favorites."
    },
    confirm: {
      remove: 'You are about to remove "{name}" from favorites.'
    }
  },
  reviewBoard: {
    header: {
      eyebrow: "FIOS reviews",
      back: "Back",
      title: "Reviews",
      subtitle: "Rate your experiences with music spaces and reservation users."
    },
    tabs: {
      pending: "Pending",
      mine: "My reviews",
      received: "Received"
    },
    stats: {
      pending: "Pending",
      published: "Published",
      received: "Received",
      average: "Average given",
      targets: "Rated targets"
    },
    types: {
      SPACE: "Space",
      USER: "User",
      USER_RECEIVED: "Received"
    },
    actions: {
      retry: "Retry",
      write: "Write review",
      viewSpace: "View space",
      cancel: "Cancel"
    },
    states: {
      loading: "Loading reviews...",
      errorTitle: "We could not open your reviews",
      error: "Unable to load review information."
    },
    pending: {
      eyebrow: "Completed reservations",
      title: "Pending reviews",
      total: "{count} pending",
      fallbackSpace: "Music space",
      fallbackUser: "FIOS user",
      noLocation: "Location pending",
      noSchedule: "Time pending"
    },
    mine: {
      eyebrow: "Published history",
      title: "My reviews",
      total: "{count} reviews",
      fallbackSpace: "Music space",
      fallbackUser: "FIOS user",
      noLocation: "Location pending",
      noComment: "You did not leave a comment on this review."
    },
    received: {
      eyebrow: "Received ratings",
      title: "Reviews about you",
      total: "{count} received"
    },
    filters: {
      search: "Search",
      searchPlaceholder: "Search by space, user or comment",
      minRating: "Minimum rating",
      minRatingOption: "{value} stars or more",
      allRatings: "All ratings",
      order: "Sort by",
      clear: "Clear filters",
      orderOptions: {
        recent: "Most recent",
        highest: "Top rated",
        lowest: "Lowest rated"
      }
    },
    metrics: {
      sound: "Sound",
      equipment: "Equipment",
      cleanliness: "Cleanliness",
      location: "Location",
      communication: "Communication",
      punctuality: "Punctuality",
      care: "Space care"
    },
    form: {
      eyebrow: "New review",
      title: "Write review",
      intro: "Share a clear rating to help other musicians and the space.",
      userTitle: "Review user",
      userIntro: "Rate communication, punctuality and care during the reservation.",
      overallRating: "Overall rating",
      soundQualityRating: "Sound quality",
      equipmentRating: "Equipment",
      cleanlinessRating: "Cleanliness",
      locationRating: "Location",
      communicationRating: "Communication",
      punctualityRating: "Punctuality",
      careRating: "Space care",
      comment: "Comment",
      commentPlaceholder: "Tell how the experience felt, what worked well and what could improve.",
      submit: "Publish review",
      submitting: "Publishing...",
      validation: "You must provide a 1 to 5 rating in every category.",
      error: "Unable to publish the review."
    },
    empty: {
      pendingTitle: "You do not have pending reviews",
      pendingText:
        "Once a reservation is completed, you can review the space, and space managers can review the person who booked.",
      mineTitle: "You have not published reviews yet",
      mineText: "Your published ratings will appear here with the category breakdown.",
      receivedTitle: "You do not have received reviews yet",
      receivedText: "When a space owner reviews a completed reservation with you, it will appear here.",
      filteredTitle: "No reviews match these filters",
      filteredText: "Try changing the text, lowering the minimum rating or clearing the filters.",
      pendingAction: "View reservations",
      mineAction: "Explore spaces",
      receivedAction: "Explore spaces",
      clearAction: "Clear filters"
    },
    notices: {
      created: "The review has been published successfully."
    }
  },
  instrumentBoard: {
    header: {
      eyebrow: "FIOS instruments",
      back: "Back",
      title: "Instruments",
      subtitle: "Explore instruments and configure your main instrument.",
      catalog: "Catalog",
      mine: "My instrument",
      create: "Create instrument"
    },
    actions: {
      retry: "Retry",
      cancel: "Cancel"
    },
    stats: {
      total: "Instruments",
      categories: "Categories",
      mine: "My instrument",
      voice: "Voice"
    },
    filters: {
      search: "Search",
      searchPlaceholder: "Search by instrument name",
      category: "Category",
      allCategories: "All categories",
      order: "Sort by",
      clear: "Clear filters",
      orderOptions: {
        name: "Name",
        category: "Category"
      }
    },
    list: {
      eyebrow: "Instrument panel",
      catalogTitle: "Public catalog",
      mineTitle: "Your main instrument",
      total: "{count} instruments"
    },
    card: {
      categoryLabel: "Category:",
      add: "Select instrument",
      remove: "Remove instrument",
      edit: "Edit",
      saving: "Saving..."
    },
    empty: {
      catalogTitle: "There are no instruments in the catalog",
      catalogText:
        "When instruments are available, they will appear here so you can filter them and add them to your profile.",
      mineTitle: "You have not configured your instrument yet",
      mineText: "Select the main instrument you play from the catalog to improve your music profile.",
      filteredTitle: "No results match these filters",
      filteredText: "Try changing the search, category or clearing the filters.",
      mineAction: "Explore catalog",
      clearAction: "Clear filters"
    },
    form: {
      createEyebrow: "New instrument",
      createTitle: "Create instrument",
      editEyebrow: "Edit instrument",
      editTitle: "Update instrument",
      intro: "Define a clear name and the right category for the public catalog.",
      name: "Name",
      category: "Category",
      selectCategory: "Select a category",
      create: "Create instrument",
      save: "Save changes",
      creating: "Creating...",
      saving: "Saving...",
      validation: "You must provide name and category.",
      error: "Unable to save the instrument."
    },
    notices: {
      minePartial: "The catalog loaded, but your instrument could not be retrieved.",
      adminOnly: "Only administrators can create instruments.",
      added: "Instrument added to your profile.",
      selected: "Main instrument updated.",
      removed: "Instrument removed from your profile.",
      updateError: "Unable to update your instrument.",
      created: "Instrument created successfully.",
      updated: "Instrument updated successfully."
    },
    states: {
      loading: "Loading instruments...",
      errorTitle: "We could not open the instruments",
      error: "Unable to load the instrument catalog."
    },
    categories: {
      STRINGS: "Strings",
      WIND: "Wind",
      BRASS: "Brass",
      PERCUSSION: "Percussion",
      KEYBOARD: "Keyboard",
      ELECTRONIC: "Electronic",
      VOICE: "Voice",
      OTHER: "Other"
    }
  },
  recruitmentBoard: {
    header: {
      eyebrow: "FIOS listings",
      back: "Back",
      title: "Member searches",
      subtitle: "Find bands looking for musicians.",
      explore: "Explore",
      mine: "My searches",
      create: "Publish search"
    },
    actions: {
      retry: "Retry",
      cancel: "Cancel"
    },
    stats: {
      open: "Open",
      bands: "Bands",
      instruments: "Instruments",
      cities: "Cities"
    },
    filters: {
      search: "Search",
      searchPlaceholder: "Search by title, band or city",
      instrument: "Instrument",
      allInstruments: "All instruments",
      level: "Required level",
      allLevels: "All levels",
      city: "City",
      allCities: "All cities",
      genre: "Music genre",
      allGenres: "All genres",
      order: "Sort by",
      clear: "Clear filters",
      orderOptions: {
        recent: "Most recent",
        city: "City",
        instrument: "Instrument",
        vacancies: "Most vacancies"
      }
    },
    list: {
      eyebrow: "Active board",
      title: "Available searches",
      exploreTitle: "Explore open searches",
      mineTitle: "Manage your searches",
      total: "{count} searches"
    },
    card: {
      bandFallback: "Band",
      cityFallback: "City pending",
      genreFallback: "Genre pending",
      instrumentFallback: "Open role",
      vacancies: "{count} vacancies",
      noPublicationDate: "Date pending",
      view: "View listing",
      edit: "Edit",
      close: "Close search",
      closing: "Closing..."
    },
    empty: {
      title: "There are no active searches",
      text: "Once bands publish new openings, they will appear here for musicians to explore.",
      mineTitle: "You have not published searches yet",
      mineText: "Publish a vacancy from one of your bands to start receiving interest.",
      filteredTitle: "No results match these filters",
      filteredText: "Try changing the search or clearing filters to recover listings.",
      exploreAction: "Explore bands",
      createAction: "Publish search",
      createBandAction: "Create band",
      resetAction: "Reset filters"
    },
    states: {
      loading: "Loading searches...",
      errorTitle: "We could not open the searches",
      error: "Unable to load the search list."
    },
    notices: {
      privateFallback:
        "You need to sign in to see your searches. Showing the open listings instead.",
      partialSupport: "The main list loaded, but some support data could not be retrieved.",
      noBands: "You need at least one band to publish a search.",
      noManageableBands: "You can only publish searches for bands you lead.",
      created: "The search has been published successfully.",
      updated: "The search has been updated successfully.",
      closed: "The search has been closed successfully.",
      closeError: "Unable to close the search."
    },
    confirm: {
      close: 'You are about to close "{title}".'
    },
    form: {
      createEyebrow: "New search",
      createTitle: "Publish member search",
      editEyebrow: "Edit search",
      editTitle: "Update search",
      intro: "Complete a simple listing so other musicians can find your project.",
      band: "Band",
      selectBand: "Select a band",
      instrument: "Instrument",
      selectInstrument: "Select an instrument",
      title: "Title",
      roleWanted: "Role wanted",
      levelRequired: "Required level",
      city: "City",
      vacancies: "Vacancies",
      description: "Description",
      create: "Publish search",
      save: "Save changes",
      creating: "Publishing...",
      saving: "Saving...",
      validation: "Complete band, instrument, title, role, city and vacancies before saving.",
      submitError: "Unable to save the search."
    },
    detail: {
      eyebrow: "Listing",
      titleFallback: "Search detail",
      loading: "Loading listing...",
      error: "Unable to load the listing detail.",
      descriptionTitle: "Description",
      emptyDescription: "This listing does not have an extended description yet.",
      contactTitle: "Contact",
      contactHint: "Email the person who published this listing to talk about the vacancy.",
      contactAction: "Send email",
      viewBand: "Explore bands",
      fields: {
        city: "City",
        genre: "Music genre",
        role: "Role wanted",
        vacancies: "Vacancies",
        publicationDate: "Published"
      }
    },
    status: {
      OPEN: "Open",
      CLOSED: "Closed"
    }
  },
  home: {
    viewAll: "View all",
    actions: {
      viewSpace: "View space",
      viewEvent: "View event",
      viewOffer: "View offer",
      viewBand: "View band"
    },
    hero: {
      kicker: "Music platform",
      title: "Find spaces, events and musicians for your music project",
      subtitle:
        "Discover places, concerts, bands and collaboration opportunities from one platform built for the local music scene.",
      inputPlaceholder: "Search spaces, bands or concerts",
      examples: "See search examples",
      exampleOne: "Rehearsal room for 5 musicians in A Coruña this Friday",
      exampleTwo: "Jazz concerts in Santiago this weekend",
      exampleThree: "Band looking for bassist in Vigo",
      metrics: {
        label: "Catalog summary",
        spaces: "spaces",
        cities: "cities",
        events: "events",
        recruitments: "open calls"
      }
    },
    quickFilters: {
      title: "Quick filters",
      subtitle: "Shape the request before jumping into the full search module.",
      cityPlaceholder: "A Coruña, Vigo, Santiago...",
      datePlaceholder: "Pick a date",
      spaceTypePlaceholder: "All spaces",
      genrePlaceholder: "Rock, jazz, pop...",
      budgetPlaceholder: "Max €",
      peoplePlaceholder: "Attendees",
      reset: "Reset filters"
    },
    featuredSpaces: {
      title: "Featured spaces",
      subtitle: "Rooms and places ready for rehearsal, recording or live shows.",
      soundproofed: "Soundproofed",
      notSoundproofed: "Not soundproofed",
      capacity: "Capacity",
      estimatedPrice: "Estimated price",
      rating: "Rating",
      pricePerHour: "{price}/h",
      priceRange: "{from} - {to}/h",
      reasons: {
        highRating: "Highlighted for strong ratings and reviews.",
        soundproofed: "A good option when noise control matters.",
        large: "Useful for larger groups or wider setups.",
        completeProfile: "Has enough visual information to evaluate the space.",
        balanced: "A solid general option to start comparing."
      }
    },
    featuredEvents: {
      title: "Featured events",
      subtitle: "A concise agenda to kick off exploration.",
      reasons: {
        today: "An option for today.",
        soon: "Coming up soon in the agenda.",
        accessible: "Affordable ticket or price to be confirmed.",
        large: "Event with broad capacity.",
        linked: "Connected with platform content.",
        upcoming: "Published event that is still available."
      }
    },
    recruitments: {
      title: "Bands looking for members",
      subtitle: "Active opportunities to join new projects.",
      vacancies: "Vacancies",
      level: "Level",
      instrument: "Instrument"
    },
    personal: {
      title: "Your personal area",
      subtitle: "Recent activity and quick access to your current context.",
      searches: "Latest searches",
      reservations: "My reservations",
      messages: "Recent messages",
      loginCtaTitle: "Sign in to unlock your personal area",
      loginCtaText:
        "Access saved searches, active reservations and recent conversations directly from the home screen.",
      unreadCount: "{count} unread messages",
      fallbackSpace: "Space",
      fallbackConversation: "Conversation",
      fallbackBand: "Band"
    },
    stats: {
      spaces: "spaces",
      events: "events",
      recruitments: "listings"
    },
    states: {
      loadingPublic: "Loading real data",
      loadingPublicText: "We are checking the information published in FIOS.",
      loadingPersonal: "Loading your activity..."
    },
    errors: {
      publicDataWarning: "We could not load all public information right now.",
      spacesTitle: "Spaces could not be loaded",
      spacesText: "The spaces section is not available right now. Try again from the full listing.",
      eventsTitle: "Events could not be loaded",
      eventsText: "The public agenda is not available right now. Try again from events.",
      recruitmentsTitle: "Member searches could not be loaded",
      recruitmentsText: "Band opportunities are not available right now. Try again from the board.",
      searchesText: "Your recent searches could not be loaded.",
      reservationsText: "Your reservations could not be loaded.",
      messagesText: "Your recent messages could not be loaded."
    },
    placeholders: {
      spacesTitle: "No featured spaces",
      spacesText: "There are no public spaces to show right now.",
      eventsTitle: "No featured events",
      eventsText: "There are no published events to show right now.",
      recruitmentsTitle: "No active searches",
      recruitmentsText: "There are no active band opportunities to show right now.",
      reservationsText: "Your reservations will appear here once sessions become active.",
      messagesText: "Your conversations with unread messages will show up here.",
      searchesText: "Recent natural-language searches will be listed here."
    },
    notFound: {
      title: "Page not found",
      description: "We could not find the page you are trying to open in FIOS.",
      text: "The link may have changed or the address may be incorrect. Return home to keep exploring spaces, events and bands.",
      action: "Back to home"
    }
  },
  search: {
    hero: {
      kicker: "FIOS Results",
      title: "Find spaces, events and bands near you",
      subtitle:
        "Type what you need or use the filters to discover rehearsal rooms, gigs and projects looking for people.",
      placeholder: "Rehearsal room in Santiago for 4 people this Friday afternoon",
      searching: "Searching..."
    },
    viewModes: {
      list: "List",
      map: "Map"
    },
    results: {
      eyebrow: "Mixed results",
      title: "Active discovery",
      summary: "{count} visible results"
    },
    tabs: {
      all: "All",
      spaces: "Spaces",
      events: "Events",
      bands: "Bands",
      recruitments: "Bands looking for members"
    },
    filters: {
      eyebrow: "Refine",
      title: "Filters",
      clear: "Clear",
      dateTitle: "Date",
      resultTypeTitle: "Result type",
      spaceTypeTitle: "Space type",
      genreTitle: "Music genre",
      budgetTitle: "Maximum budget",
      maxBudgetPlaceholder: "Maximum budget",
      peopleTitle: "People",
      timeTitle: "Time",
      apply: "Apply filters",
      anyBudget: "No limit",
      anyPeople: "No limit",
      maxBudgetValue: "Up to {value} EUR",
      peopleValue: "{value} people",
      datePresets: {
        today: "Today",
        tomorrow: "Tomorrow",
        weekend: "This weekend",
        week: "This week"
      },
      spaceTypes: {
        rehearsal: "Rehearsal room",
        recording: "Recording studio",
        performance: "Performance hall",
        barStage: "Bar with stage",
        multiuse: "Multi-use room"
      },
      timeSlots: {
        morning: "Morning",
        afternoon: "Afternoon",
        night: "Night",
        allday: "All day"
      }
    },
    states: {
      loading: "Loading results...",
      emptyTitle: "No matches found",
      emptyText: "Try changing the natural query or relaxing a few filters.",
      error: "Unable to run the search.",
      partialBands: "Spaces and events loaded. Bands could not be loaded.",
      partialRecruitments: "Spaces and events loaded. Band member searches could not be loaded."
    },
    map: {
      emptyTitle: "There are no geolocated points for this filter",
      emptyText: "The map structure stays available while you refine the search.",
      placeholder: "The map view could not be loaded right now.",
      popupAction: "Open"
    },
    chips: {
      people: "{value} people",
      budget: "Up to {value} EUR",
      dateRange: "{from} - {to}",
      today: "Today",
      tomorrow: "Tomorrow"
    },
    spaceTypeLabels: {
      REHEARSAL_ROOM: "Rehearsal room",
      RECORDING_STUDIO: "Recording studio",
      CONCERT_HALL: "Performance hall",
      CLASSROOM: "Classroom",
      MULTIPURPOSE: "Multi-purpose room",
      OTHER: "Music space"
    },
    eventTypeLabels: {
      CONCERT: "Concert",
      FESTIVAL: "Festival",
      WORKSHOP: "Workshop",
      OPEN_MIC: "Open mic",
      JAM_SESSION: "Jam session",
      SHOWCASE: "Showcase",
      OTHER: "Event"
    },
    recruitmentLevelLabels: {
      BEGINNER: "Beginner level",
      INTERMEDIATE: "Intermediate level",
      ADVANCED: "Advanced level",
      PROFESSIONAL: "Professional level"
    },
    cards: {
      space: {
        available: "Available",
        capacity: "{value} people",
        squareMeters: "{value} m2",
        soundproofed: "Soundproofed",
        notSoundproofed: "Not soundproofed",
        unknownSound: "Check details",
        estimatedPriceLabel: "Estimated price",
        price: "From {value} EUR",
        cta: "View detail"
      },
      event: {
        capacity: "Capacity {value}",
        price: "{value} EUR",
        cta: "View event"
      },
      band: {
        badge: "Band",
        cta: "View band"
      },
      recruitment: {
        bandFallback: "Band",
        roleFallback: "Open role",
        vacancies: "{value} vacancies",
        match: "Match {value}%",
        cta: "View offer"
      }
    }
  },
  spaceDetail: {
    states: {
      loading: "Loading space...",
      loadingSection: "Loading content...",
      error: "Unable to load the space detail.",
      errorTitle: "We could not open this space",
      partialData: "The main space loaded, but some secondary sections could not be retrieved."
    },
    header: {
      eyebrow: "Music space",
      back: "Back",
      capacity: "{value} people",
      soundproofed: "Soundproofed",
      notSoundproofed: "Not soundproofed",
      squareMeters: "{value} m2"
    },
    media: {
      placeholder: "Image unavailable",
      addFavorite: "Add to favorites",
      removeFavorite: "Remove from favorites",
      favoriteAdded: "Space saved to favorites.",
      favoriteRemoved: "Space removed from favorites.",
      favoriteError: "Unable to update favorites."
    },
    quickServices: {
      eyebrow: "Quick services",
      title: "Essential space highlights",
      items: {
        wifi: "WiFi included",
        parking: "Parking nearby",
        rest: "Rest area",
        access: "Accessible access"
      }
    },
    description: {
      eyebrow: "Description",
      title: "About this space",
      empty: "There is no description available for this space yet."
    },
    info: {
      eyebrow: "Useful information",
      title: "Space profile",
      items: {
        spaceType: "Space type",
        capacity: "Maximum capacity",
        squareMeters: "Surface",
        opening: "Estimated opening",
        closing: "Estimated closing",
        status: "Status for the selected date",
        address: "Full address",
        hourlyPrice: "Estimated hourly price"
      },
      capacityValue: "{value} attendees",
      squareMetersValue: "{value} m2",
      availableNow: "Available",
      unavailableNow: "Unavailable",
      notAvailable: "Not available"
    },
    equipment: {
      eyebrow: "Equipment",
      title: "Included in the space",
      count: "{value} items",
      quantity: "{value} units",
      empty: "There is no registered equipment yet.",
      error: "Unable to load the equipment.",
      unknown: "Unnamed equipment",
      states: {
        AVAILABLE: "Available",
        LIMITED: "Limited use",
        OUT_OF_SERVICE: "Out of service"
      }
    },
    availability: {
      eyebrow: "Availability",
      title: "Available slots",
      fullSchedule: "View full schedule",
      fullScheduleTitle: "Full schedule",
      fullScheduleLoading: "Loading schedules...",
      fullScheduleEmpty: "This space has no published schedules yet.",
      fullScheduleError: "Unable to load schedules.",
      fullScheduleClose: "Close schedules",
      scheduleDay: "Day",
      scheduleTime: "Time",
      schedulePrice: "Price",
      date: "Date",
      quickDates: "Next days",
      loading: "Checking availability...",
      loadingHint: "Checking schedules, exceptions and existing bookings.",
      empty: "There are no free slots for this date.",
      emptySummary: "No slots available",
      emptyHint: "Try another day or review the full schedule for this space.",
      availableSummary: "{count} free slots",
      availableWithBookedSummary: "{free} free slots · {booked} bookings",
      bookedOnlySummary: "{count} bookings for this date",
      bookedOnlyHint: "All published slots for this date already have bookings or blocks.",
      selectedSummary: "Selected slot: {range}",
      firstSlotSummary: "First free slot from {time}",
      bookedTitle: "Existing bookings",
      bookedDetail: "These ranges are already occupied for the selected date.",
      error: "Unable to load availability.",
      errorSummary: "Availability unavailable",
      errorHint: "Try again or check the full schedule.",
      today: "Today",
      tomorrow: "Tomorrow",
      chooseSlot: "Choose slot",
      selectedSlot: "Selected",
      priceFrom: "From {price}",
      periods: {
        morning: "Morning",
        afternoon: "Afternoon",
        evening: "Evening",
        night: "Late night"
      }
    },
    reviews: {
      eyebrow: "Reviews",
      title: "What other users say",
      count: "{value} reviews",
      empty: "There are no published reviews yet.",
      error: "Unable to load reviews.",
      noComment: "No additional comment.",
      anonymous: "FIOS user",
      metrics: {
        sound: "Sound",
        equipment: "Equipment",
        cleanliness: "Cleanliness",
        location: "Location"
      }
    },
    map: {
      eyebrow: "Location",
      title: "Where it is",
      empty: "Location unavailable",
      noAddress: "No detailed address"
    },
    booking: {
      eyebrow: "Booking",
      title: "Book this space",
      date: "Date",
      startTime: "Start time",
      duration: "Duration",
      attendees: "Attendees",
      subtotal: "Subtotal",
      total: "Total",
      submit: "Book now",
      loginToReserve: "Sign in to book",
      submitting: "Creating booking...",
      selectSlot: "Select a slot",
      noReviews: "No reviews",
      priceRange: "From {from} to {to}/h",
      priceSingle: "{value}/h",
      success: "Booking created successfully. Availability has been updated.",
      durationHours: "{hours} h",
      durationMinutes: "{minutes} min",
      durationMixed: "{hours} h {minutes} min",
      errors: {
        slotRequired: "Select an available slot before booking.",
        durationRequired: "Select a valid session duration.",
        attendees: "The attendees count is not valid for this space.",
        generic: "Unable to create the booking."
      }
    },
    ownerActions: {
      eyebrow: "Space management",
      title: "Schedules, exceptions and equipment",
      manageAvailability: "Manage availability",
      manageEquipment: "Manage equipment"
    },
    spaceTypeLabels: {
      REHEARSAL_ROOM: "Rehearsal room",
      RECORDING_STUDIO: "Recording studio",
      CONCERT_HALL: "Performance hall",
      CLASSROOM: "Classroom",
      MULTIPURPOSE: "Multi-purpose room",
      OTHER: "Music space"
    }
  },
  spaceEquipmentManage: {
    header: {
      eyebrow: "Equipment",
      title: "Manage equipment"
    },
    states: {
      loading: "Loading equipment...",
      error: "Unable to load the equipment.",
      saveError: "Unable to save the equipment.",
      deleteError: "Unable to delete the equipment.",
      empty: "This space does not have associated equipment yet.",
      catalogEmpty: "The equipment catalog is empty.",
      allAssigned: "All catalog equipment is already associated with this space."
    },
    form: {
      eyebrow: "Catalog",
      addTitle: "Add equipment",
      editTitle: "Edit equipment",
      equipment: "Equipment",
      selectEquipment: "Select equipment",
      customEquipmentOption: "Other material",
      customEquipment: "Material name",
      customEquipmentPlaceholder: "Pedalboard, extra stand, 4x12 cab...",
      quantity: "Quantity",
      state: "State",
      observations: "Observations",
      observationsPlaceholder: "Internal notes visible on the space detail",
      customObservationsPlaceholder: "Describe brand, condition, use or any needed detail",
      requiredForCustom: "Required for Other"
    },
    list: {
      eyebrow: "Current equipment",
      title: "Space associations"
    },
    actions: {
      add: "Add equipment",
      adding: "Adding...",
      update: "Save changes",
      updating: "Saving...",
      edit: "Edit",
      delete: "Delete",
      reload: "Reload",
      cancel: "Cancel",
      cancelEdit: "Cancel edit",
      confirmDelete: "Delete equipment"
    },
    notices: {
      added: "Equipment added to the space.",
      updated: "Equipment updated successfully.",
      deleted: "Equipment removed from the space."
    },
    validation: {
      equipment: "Select equipment from the catalog.",
      customEquipment: "Enter the material name.",
      customObservations: "Describe the material in observations.",
      quantity: "Enter a valid quantity.",
      state: "Select a valid state.",
      duplicate: "This equipment is already associated with the space."
    },
    confirm: {
      eyebrow: "Confirmation",
      deleteTitle: "Delete equipment",
      deleteText: "You are about to remove {name} from this space. Continue?"
    },
    categories: {
      INSTRUMENT: "Instrument",
      SOUND: "Sound",
      LIGHTING: "Lighting",
      RECORDING: "Recording",
      FURNITURE: "Furniture",
      ACCESSORY: "Accessory",
      OTHER: "Other"
    }
  },
  spaceAvailabilityManage: {
    header: {
      back: "Back to my spaces",
      eyebrow: "Availability",
      detail: "View detail"
    },
    states: {
      loading: "Loading availability...",
      errorTitle: "We could not open this management view",
      error: "Unable to load availability management.",
      noPermissionTitle: "No permission",
      noPermission: "Only the space owner or an administrator can manage this availability.",
      inactiveTitle: "Inactive space",
      inactive: "Availability cannot be changed for an inactive space.",
      notApprovedTitle: "Pending approval",
      notApproved: "Availability management is shown when the space is approved."
    },
    summary: {
      schedules: "Schedules",
      exceptions: "Exceptions",
      blocked: "Blocks",
      custom: "Custom"
    },
    schedules: {
      eyebrow: "Regular schedule",
      formTitle: "Edit schedule",
      listTitle: "Configured schedules",
      empty: "There are no regular schedules yet."
    },
    exceptions: {
      eyebrow: "Exceptions",
      formTitle: "Edit exception",
      listTitle: "Configured exceptions",
      empty: "There are no exceptions yet.",
      noPrice: "Not applicable"
    },
    fields: {
      dayOfWeek: "Day",
      startTime: "Start",
      endTime: "End",
      price: "Price",
      timeRange: "Time",
      date: "Date",
      exceptionType: "Type",
      reason: "Reason"
    },
    actions: {
      cancelEdit: "Cancel edit",
      edit: "Edit",
      delete: "Delete",
      deleting: "Deleting...",
      saving: "Saving...",
      saveSchedule: "Save schedule",
      createSchedule: "Add schedule",
      saveException: "Save exception",
      createException: "Add exception",
      refresh: "Refresh"
    },
    exceptionTypes: {
      BLOCKED: "One-off block",
      CUSTOM_AVAILABILITY: "Custom availability"
    },
    calculated: {
      eyebrow: "Result",
      title: "Calculated availability",
      loading: "Updating availability...",
      error: "Unable to calculate availability.",
      availableTitle: "Free slots",
      bookedTitle: "Blocking reservations",
      emptySlots: "There are no free slots for this date.",
      emptyBooked: "There are no active reservations for this date."
    },
    validation: {
      scheduleDay: "Select a valid day.",
      timeRange: "The start time must be before the end time.",
      schedulePrice: "Enter a price greater than zero.",
      exceptionDate: "Select a valid date.",
      exceptionType: "Select a valid exception type.",
      customPrice: "Custom availability needs a price greater than zero."
    },
    notices: {
      scheduleCreated: "Schedule added successfully.",
      schedulesCreated: "{count} schedules added successfully.",
      scheduleUpdated: "Schedule updated successfully.",
      scheduleDeleted: "Schedule deleted successfully.",
      scheduleError: "Unable to save the schedule.",
      scheduleDeleteError: "Unable to delete the schedule.",
      exceptionCreated: "Exception added successfully.",
      exceptionUpdated: "Exception updated successfully.",
      exceptionDeleted: "Exception deleted successfully.",
      exceptionError: "Unable to save the exception.",
      exceptionDeleteError: "Unable to delete the exception."
    },
    confirm: {
      deleteSchedule: "You are about to delete this regular schedule.",
      deleteException: "You are about to delete this availability exception."
    }
  },
  spaceList: {
    header: {
      eyebrow: "FIOS catalog",
      title: "Music spaces",
      subtitle: "Find rooms, studios and places for your next project.",
      explore: "Explore",
      mine: "My spaces",
      create: "Create space"
    },
    stats: {
      total: "Visible",
      approved: "Approved",
      active: "Active",
      cities: "Cities",
      types: "Types"
    },
    filters: {
      search: "Search",
      searchPlaceholder: "Search by name, city or space type",
      city: "City",
      allCities: "All cities",
      type: "Space type",
      allTypes: "All types",
      minCapacity: "Minimum capacity",
      soundproofed: "Soundproofed",
      allSoundproofed: "Any option",
      soundproofedYes: "Yes",
      soundproofedNo: "No",
      order: "Sort by",
      clear: "Clear filters",
      orderOptions: {
        rating: "Top rated",
        capacity: "Highest capacity",
        city: "City",
        name: "Name"
      }
    },
    list: {
      eyebrow: "Live catalog",
      exploreTitle: "Explore available spaces",
      mineTitle: "Manage your spaces",
      total: "{count} spaces"
    },
    card: {
      imagePlaceholder: "Image unavailable",
      locationFallback: "Location pending",
      capacityValue: "{value} people",
      squareMeters: "Surface",
      squareMetersValue: "{value} m2",
      soundproofed: "Soundproofing",
      soundproofedYes: "Soundproofed",
      soundproofedNo: "Not soundproofed",
      soundproofedUnknown: "Needs review",
      rating: "Rating",
      ratingValue: "{rating} ({count})",
      noRating: "No reviews",
      notSpecified: "Not specified",
      viewDetail: "View detail",
      manageAvailability: "Manage availability",
      edit: "Edit",
      deactivate: "Deactivate",
      deactivating: "Deactivating..."
    },
    empty: {
      exploreTitle: "There are no public spaces yet",
      exploreText:
        "Once approved spaces are available on the platform, they will appear here with detail access.",
      mineTitle: "You have not published spaces yet",
      mineText: "Create your first space to manage it here and track its status.",
      filteredTitle: "No results match these filters",
      filteredText: "Try changing the search or relaxing a filter to recover results.",
      resetAction: "Reset filters",
      createAction: "Create space"
    },
    form: {
      createEyebrow: "New space",
      createTitle: "Create music space",
      editEyebrow: "Edit space",
      editTitle: "Update music space",
      intro: "Complete a simple profile to publish or update your space.",
      pendingHint: "The space may need approval before it is published.",
      name: "Name",
      description: "Description",
      spaceType: "Space type",
      capacity: "Capacity",
      squareMeters: "Surface",
      soundproofed: "Soundproofed",
      mainImage: "Main image",
      country: "Country",
      province: "Province",
      city: "City",
      street: "Street",
      portal: "Portal",
      floor: "Floor",
      postalCode: "Postal code",
      coordinates: "Coordinates",
      coordinatesPlaceholder: "43.3623, -8.4115",
      coordinatesHint: "Paste the coordinates together or a map link.",
      coordinatesAction: "Apply",
      coordinatesApplied: "Coordinates added.",
      coordinatesError: "We could not recognize those coordinates.",
      latitude: "Latitude",
      longitude: "Longitude",
      cancel: "Cancel",
      save: "Save changes",
      create: "Create space",
      saving: "Saving...",
      creating: "Creating...",
      validation:
        "Complete name, type, capacity, surface and required address fields before saving.",
      submitError: "Unable to save the space."
    },
    status: {
      APPROVED: "Approved",
      PENDING: "Pending",
      REJECTED: "Rejected",
      INACTIVE: "Inactive"
    },
    notices: {
      privateFallback:
        "You need to sign in to see your spaces. Showing the public catalog instead.",
      editLoadError: "Unable to load the space detail for editing.",
      updated: "The space has been updated successfully.",
      createdPending: "The space has been created and is now pending approval.",
      deactivated: "The space has been deactivated successfully.",
      deactivateError: "Unable to deactivate the space."
    },
    confirm: {
      deactivate: 'You are about to deactivate "{name}".'
    },
    states: {
      loading: "Loading spaces...",
      errorTitle: "We could not open the spaces",
      error: "Unable to load the space list.",
      retry: "Retry"
    }
  },
  reservations: {
    header: {
      title: "My reservations",
      mineTitle: "My reservations",
      receivedTitle: "Received reservations",
      mineSubtitle: "Track upcoming sessions, changes, messages and reservations that are already closed.",
      receivedSubtitle: "Manage requests for your spaces and complete sessions once they have passed.",
      mine: "My reservations",
      received: "Received",
      modeLabel: "Reservation type",
      new: "New booking"
    },
    states: {
      loading: "Loading reservations...",
      error: "Unable to load your reservations.",
      errorTitle: "We could not open your reservations",
      cancelling: "Cancelling...",
      updatingState: "Updating..."
    },
    stats: {
      active: "Active reservations",
      upcoming: "Upcoming reservations",
      completed: "Completed reservations",
      cancelled: "Cancelled reservations"
    },
    upcoming: {
      eyebrow: "Your next reservation",
      fallback: "Your next reservation",
      today: "Your next reservation is today",
      tomorrow: "Your next reservation is tomorrow",
      inDays: "Your next reservation is in {count} days"
    },
    timing: {
      today: "Today",
      tomorrow: "Tomorrow",
      inDays: "In {count} days",
      past: "Already passed",
      needsClosure: "Needs closing",
      expiredPending: "Expired pending"
    },
    list: {
      eyebrow: "Tracking",
      title: "All reservations",
      receivedEyebrow: "Space management",
      receivedTitle: "Received reservations",
      total: "{count} reservations"
    },
    filters: {
      searchPlaceholder: "Search by space name or city",
      date: "Date",
      sessionType: "Session type",
      allSessionTypes: "All session types",
      order: "Sort by",
      clear: "Clear filters",
      orderOptions: {
        nearest: "Nearest date",
        farthest: "Farthest date",
        priceDesc: "Highest price",
        priceAsc: "Lowest price"
      }
    },
    tabs: {
      all: "All",
      active: "Active",
      pending: "Pending",
      completed: "Completed",
      cancelled: "Cancelled"
    },
    cards: {
      fallbackSpace: "Music space",
      noLocation: "Location pending",
      attendeesValue: "{count} attendees",
      totalPrice: "Total price"
    },
    actions: {
      view: "View booking",
      contact: "Contact",
      modify: "Modify",
      cancel: "Cancel",
      accept: "Accept",
      reject: "Reject",
      complete: "Complete",
      rebook: "Book again",
      viewReason: "View reason",
      retry: "Retry"
    },
    empty: {
      title: "You do not have reservations yet",
      text: "As soon as you create a session from a music space, it will appear here with status, timing and available actions.",
      action: "Explore spaces",
      receivedTitle: "You have not received reservations yet",
      receivedText:
        "Add a music space so other users can book it. When a request arrives, it will appear here with status, timing and available actions.",
      receivedAction: "Add space",
      filteredTitle: "No reservations match these filters",
      filteredText: "Try changing the date, search text or status to bring reservations back."
    },
    detail: {
      eyebrow: "Reservation detail",
      notes: "Notes",
      cancellationReason: "Cancellation reason",
      close: "Close",
      fields: {
        space: "Space",
        location: "Location",
        date: "Date",
        schedule: "Schedule",
        status: "Status",
        sessionType: "Session type",
        attendees: "Attendees",
        price: "Price",
        user: "User",
        band: "Band",
        createdAt: "Created at"
      }
    },
    edit: {
      eyebrow: "Edit reservation",
      sessionDate: "Date",
      startTime: "Start time",
      endTime: "End time",
      attendees: "Attendees",
      sessionType: "Session type",
      notes: "Notes",
      cancel: "Cancel",
      save: "Save changes",
      saving: "Saving...",
      updated: "The reservation has been updated successfully.",
      error: "Unable to update the reservation.",
      validationRequired: "Provide date, start time and end time.",
      validationTimeOrder: "Start time must be before end time.",
      validationAttendees: "There must be at least one attendee.",
      validationCapacity: "The space allows a maximum of {capacity} attendees."
    },
    cancel: {
      prompt: "Enter the cancellation reason",
      eyebrow: "Cancellation",
      title: "Cancel reservation",
      text: "You are about to cancel the reservation for {name}.",
      reasonLabel: "Cancellation reason",
      reasonPlaceholder: "Briefly explain why you are cancelling this reservation.",
      close: "Back",
      confirm: "Cancel reservation",
      submitting: "Cancelling...",
      emptyReason: "You need to provide a reason before cancelling the reservation.",
      success: "The reservation has been cancelled successfully.",
      error: "Unable to cancel the reservation."
    },
    stateAction: {
      eyebrow: "Reservation management",
      close: "Close confirmation",
      cancel: "Back",
      invalid: "This action is not available for the current reservation state.",
      error: "Unable to update the reservation state.",
      ACCEPTED: {
        title: "Accept reservation",
        text: "You are about to accept the reservation for {name}.",
        confirm: "Accept reservation",
        success: "The reservation has been accepted successfully."
      },
      REJECTED: {
        title: "Reject reservation",
        text: "You are about to reject the reservation for {name}.",
        confirm: "Reject reservation",
        success: "The reservation has been rejected successfully."
      },
      COMPLETED: {
        title: "Mark as completed",
        text: "You are about to mark the reservation for {name} as completed.",
        confirm: "Complete reservation",
        success: "The reservation has been marked as completed."
      }
    },
    placeholders: {
      notFound: "The requested reservation could not be found.",
      rebookUnavailable: "The original space for this reservation could not be found."
    },
    sessionTypes: {
      REHEARSAL: "Rehearsal",
      RECORDING: "Recording",
      CLASS: "Class",
      EVENT_PREPARATION: "Event preparation",
      OTHER: "Other session"
    },
    statuses: {
      PENDING: "Pending",
      ACCEPTED: "Accepted",
      COMPLETED: "Completed",
      CANCELLED: "Cancelled",
      REJECTED: "Rejected"
    }
  },
  messages: {
    header: {
      eyebrow: "FIOS inbox",
      title: "Messages",
      subtitle: "Talk to spaces and users about your reservations",
      unread: "{count} unread messages"
    },
    states: {
      error: "Unable to load your conversations.",
      reservationUnavailable:
        "You cannot access that reservation or it is not available in your inbox.",
      openFailed: "Unable to open the requested conversation."
    },
    list: {
      title: "Conversations",
      summary: "{count} conversations",
      searchPlaceholder: "Search by space, city, state or text",
      emptyTitle: "You do not have messages yet",
      emptyText:
        "Your reservation conversations will appear here as soon as there is at least one linked reservation.",
      fallbackTitle: "FIOS reservation",
      fallbackParticipant: "FIOS user",
      noMessagesYet: "There are no messages in this conversation yet."
    },
    filters: {
      scope: "Conversation type",
      allConversations: "All",
      myReservations: "Mine",
      managedReservations: "Received",
      status: "Reservation status",
      allStatuses: "All"
    },
    chat: {
      back: "Conversations",
      viewReservation: "View reservation",
      emptyTitle: "There are no messages yet",
      emptyText: "Write the first message to start this conversation.",
      emptyInfo: "This reservation has no messages yet. You can start the conversation now.",
      placeholderTitle: "Select a conversation",
      placeholderText: "Choose a conversation from the list to read messages and reply.",
      composerPlaceholder: "Write a message...",
      send: "Send",
      sending: "Sending...",
      sendError: "Unable to send the message.",
      error: "Unable to load the messages for this reservation.",
      locked: "You can only send messages for pending or accepted reservations.",
      fallbackAuthor: "FIOS user"
    },
    dates: {
      today: "Today",
      yesterday: "Yesterday"
    }
  },
  events: {
    header: {
      eyebrow: "FIOS agenda",
      title: "Events",
      subtitle: "Discover concerts, sessions and music activities"
    },
    actions: {
      list: "List",
      map: "Map",
      importTicketmaster: "Import from Ticketmaster",
      createEvent: "Create event",
      retry: "Retry",
      view: "View event",
      edit: "Edit",
      archive: "Archive",
      archiving: "Archiving...",
        backToList: "View events",
      ticketmaster: "View on Ticketmaster",
      externalLink: "Open external link",
      viewSpace: "View music space",
      viewBand: "View band",
      cancel: "Cancel",
      saveChanges: "Save changes"
    },
    states: {
      loading: "Loading events...",
      loadingDetail: "Loading event detail...",
      error: "Unable to load the event list.",
      detailError: "Unable to load the event detail.",
      errorTitle: "We could not open this event"
    },
    list: {
      eyebrow: "Published agenda",
      title: "Explore events",
      total: "{count} visible events"
    },
    quickTabs: {
      upcoming: "Upcoming",
      today: "Today",
      week: "This week",
      free: "Free",
      external: "External"
    },
    filters: {
      search: "Search",
      searchPlaceholder: "Search by title, city, place or genre",
      city: "City",
      cityPlaceholder: "Santiago, Vigo, A Coruña...",
      date: "Date",
      genre: "Music genre",
      genrePlaceholder: "Rock, jazz, folk...",
      type: "Event type",
      allTypes: "All types",
      source: "Source",
      allSources: "All sources",
      order: "Sort by",
      freeOnly: "Free only",
      clear: "Clear filters",
      apply: "Apply filters",
      orderOptions: {
        nearest: "Upcoming first",
        farthest: "Farthest date",
        priceAsc: "Lowest price",
        priceDesc: "Highest price"
      }
    },
    empty: {
      title: "There are no published events yet",
      text: "As soon as the agenda is available, it will appear here with filters, map mode and detail access.",
      action: "Reset view",
      filteredTitle: "No results match these filters",
      filteredText: "Try relaxing the search, changing the date or reviewing the event source."
    },
    map: {
      loading: "Loading event map...",
      emptyTitle: "There are no geolocated events for this filter",
      emptyText: "You can return to the list view or adjust city and date to find more points.",
      error: "Unable to load the map view."
    },
    cards: {
      imageFallback: "Image unavailable",
      locationFallback: "Location pending",
      capacity: "Capacity {value}",
      onRequest: "Price on request"
    },
    types: {
      CONCERT: "Concert",
      FESTIVAL: "Festival",
      WORKSHOP: "Workshop",
      OPEN_MIC: "Open mic",
      JAM_SESSION: "Jam session",
      SHOWCASE: "Showcase",
      OTHER: "Event"
    },
    sources: {
      INTERNAL: "FIOS",
      EXTERNAL: "Ticketmaster"
    },
    statuses: {
      DRAFT: "Pending",
      PUBLISHED: "Published",
      CANCELLED: "Cancelled",
      ARCHIVED: "Archived"
    },
    admin: {
      createEyebrow: "New event",
      createTitle: "Create event",
      editEyebrow: "Edit event",
      editTitle: "Update event",
      saving: "Saving...",
      validation: "Complete title, date, place, city and country before saving.",
      capacityValidation: "Enter a capacity greater than zero for internal events.",
      priceValidation: "The ticket price cannot be negative.",
      created: "The event has been created successfully.",
      updated: "The event has been updated successfully.",
      deleted: "The event has been archived successfully.",
      error: "Unable to save the event.",
      deleteConfirm: "Do you want to archive this event?",
      deleteError: "Unable to archive the event.",
      fields: {
        title: "Title",
        type: "Type",
        status: "Status",
        date: "Date",
        startTime: "Start time",
        endTime: "End time",
        city: "City",
        province: "Province",
        country: "Country",
        venueName: "Place",
        genre: "Music genre",
        capacity: "Capacity",
        ticketPrice: "Price",
        latitude: "Latitude",
        longitude: "Longitude",
        location: "Address / location",
        posterImage: "Image",
        externalUrl: "External link",
        description: "Description",
        source: "Source"
      }
    },
    ticketmaster: {
      eyebrow: "External import",
      title: "Import from Ticketmaster",
      city: "City",
      cityPlaceholder: "A Coruña, Vigo, Santiago, Madrid...",
      keyword: "Artist or event",
      keywordPlaceholder: "Rels B, festival, concert...",
      genre: "Music genre",
      genrePlaceholder: "Optional",
      startDate: "Start date",
      endDate: "End date",
      countryCode: "Country",
      hint: "Leave city empty to search across Spain; use city when you want to narrow the search to a specific area.",
      search: "Search",
      searching: "Searching...",
      import: "Import",
      importing: "Importing...",
      bulkImport: "Import {count} results",
      bulkImporting: "Importing results...",
      empty: "No external events were found for this search.",
      emptyHint: "Try removing artist or genre filters, or change the city to discover events in another area.",
      broadenSearch: "Search without artist or genre",
      searchAgain: "Search again",
      clearKeyword: "Clear artist",
      clearGenre: "Clear genre",
      useSpainAndSearch: "Use Spain and search",
      activeFilters: "Search: {filters}",
      noActiveFilters: "Broad search",
      countryMismatch: "The city looks Spanish, but the selected country is {country}.",
      countries: {
        ES: "Spain",
        GB: "United Kingdom",
        US: "United States",
        PT: "Portugal",
        FR: "France",
        DE: "Germany",
        IT: "Italy",
        IE: "Ireland"
      },
      errors: {
        missingKey: "The Ticketmaster connection is not ready. Please tell the person responsible.",
        disabled: "The Ticketmaster connection is disabled.",
        timeout: "Ticketmaster is taking too long. Try again in a few seconds.",
        rateLimit: "Ticketmaster temporarily limited searches. Wait a few seconds before retrying.",
        provider: "Ticketmaster rejected the query. Check country, city and filters."
      },
      error: "Unable to query Ticketmaster.",
      imported: "Event imported successfully.",
      bulkImported: "{count} events imported from Ticketmaster. {existing} were already in FIOS.",
      importError: "Unable to import the external event."
    },
    tickets: {
      header: {
        back: "Back",
        eyebrow: "My tickets",
        title: "Your tickets",
        subtitle: "Check the tickets you reserved on FIOS. The amount is paid in person on the day of the event.",
        explore: "View events"
      },
      states: {
        loading: "Loading your tickets...",
        errorTitle: "We could not open your tickets",
        error: "Unable to load your tickets."
      },
      actions: {
        retry: "Try again",
        viewEvent: "View event",
        viewEventFor: "View event: {title}"
      },
      stats: {
        label: "Your ticket summary",
        total: "Saved tickets",
        upcoming: "Upcoming events",
        paid: "Amount due"
      },
      list: {
        eyebrow: "Saved tickets",
        title: "Ticket list",
        total: "{count} tickets"
      },
      card: {
        ticketLabel: "FIOS ticket",
        cancelledLabel: "Cancelled ticket",
        purchasedAt: "Reserved on {date}",
        date: "Date",
        time: "Time",
        place: "Place",
        amountDue: "Amount due",
        pricePaid: "Amount due",
        paymentNote: "Payment will be made in person on the day of the event.",
        cancelledAt: "Renounced at",
        noTime: "Time pending",
        locationFallback: "Place pending",
        untitled: "Untitled event",
        upcoming: "Ticket for an upcoming event",
        past: "Event already held",
        cancelled: "Reservation cancelled"
      },
      empty: {
        title: "You do not have tickets yet",
        text: "When you reserve a ticket for a FIOS event, it will appear here saved to your account.",
        action: "View events"
      }
    },
    detail: {
      back: "Back",
      eyebrow: "Event detail",
      descriptionTitle: "Description",
      emptyDescription: "This event does not have a published description yet.",
      noGenre: "Genre pending",
      infoEyebrow: "Information",
      infoTitle: "Main facts",
      linksEyebrow: "Context",
      linksTitle: "Links and relations",
      mapEyebrow: "Map",
      mapTitle: "Event location",
      mapEmpty: "Location unavailable",
      noLocation: "No city",
      noVenue: "Place pending",
      noAddress: "No detailed address",
      noSpace: "No linked music space",
      noBand: "No linked band",
      noCapacity: "Capacity unavailable",
      capacityValue: "{value} attendees",
      reservedValue: "{value} reserved",
      availableValue: "{value} available",
      purchase: {
        eyebrow: "FIOS ticket",
        title: "Reserve your ticket",
        text: "FIOS will save your reservation. No payment is made through the platform.",
        remainingText: "{count} tickets left. When you reserve, FIOS will save your ticket without taking payment.",
        savedTitle: "Ticket reserved",
        savedText: "You already have a reserved ticket. Amount due: {price}.",
        paymentNote: "Payment will be made in person on the day of the event.",
        soldOutTitle: "Sold out",
        soldOutText: "There are no tickets left for this event.",
        unavailableTitle: "Reservation unavailable",
        unavailableText: "This event does not allow ticket reservation right now.",
        buyAction: "Reserve ticket",
        loginAction: "Sign in to reserve",
        savedAction: "Ticket reserved",
        soldOutAction: "Tickets sold out",
        unavailableAction: "Unavailable",
        saving: "Reserving ticket...",
        success: "Ticket reserved. Amount due: {price}.",
        error: "Unable to reserve the ticket.",
        cancelAction: "Renounce ticket",
        cancelConfirm: "You are about to renounce this ticket. The place will become available again.",
        cancelSuccess: "You renounced the ticket. The place is available again.",
        cancelError: "Unable to renounce the ticket."
      },
      organizer: {
        eyebrow: "Organization",
        title: "Reserved tickets",
        loading: "Loading reservations...",
        empty: "There are no reserved tickets yet.",
        error: "Unable to load reserved tickets.",
        userFallback: "User #{id}"
      },
      fields: {
        date: "Date",
        time: "Time",
        city: "City",
        venue: "Place",
        price: "Price",
        capacity: "Capacity",
        reserved: "Reserved",
        available: "Available",
        location: "Address",
        space: "Music space",
        band: "Band"
      }
    }
  },
  bands: {
    header: {
      title: "Bands",
      mineTitle: "My bands",
      subtitle: "Explore active music projects, genres and bands looking for members.",
      mineSubtitle: "Manage your projects, members and open searches from one place.",
      create: "Create band"
    },
    states: {
      loading: "Loading bands...",
      error: "Unable to load bands.",
      errorTitle: "We could not open bands",
      partialData: "Main bands loaded, but some secondary data could not be retrieved.",
      partialMembers: "Some band members could not be loaded.",
      savingBand: "Saving band...",
      publishingRecruitment: "Publishing search...",
      publishingEvent: "Submitting event...",
      closingRecruitment: "Closing..."
    },
    stats: {
      total: "Total bands",
      totalMine: "My bands",
      totalPublic: "Bands on FIOS",
      active: "Active bands",
      members: "Total members",
      recruitments: "Active searches",
      genres: "Active genres"
    },
    members: {
      eyebrow: "Members",
      title: "Core members",
      count: "{count} members",
      empty: "This band does not have visible members yet.",
      noInstruments: "No associated instruments"
    },
    memberManagement: {
      eyebrow: "Band members",
      loading: "Loading members...",
      userId: "User ID",
      userSearch: "Find user",
      userSearchPlaceholder: "Email or name",
      userSearchHint: "Type and select a person from the list.",
      userSearchMinLength: "Type at least 2 characters.",
      searchingUsers: "Searching users...",
      userSearchEmpty: "No available users match that search.",
      userSearchError: "Users could not be searched.",
      selectedUser: "Selected: {name}",
      add: "Add member",
      adding: "Adding...",
      removing: "Updating...",
      remove: "Remove",
      empty: "This band does not have active members yet.",
      editPermission: "Only leaders or administrators can edit bands.",
      managePermission: "Only leaders or administrators can manage members.",
      loadError: "Members could not be loaded.",
      invalidUserId: "Enter a valid user ID.",
      invalidUserSelection: "Select a user from the list.",
      addSuccess: "Member added successfully.",
      addError: "The member could not be added.",
      roleUpdated: "Role updated successfully.",
      roleError: "The role could not be updated.",
      removeConfirm: "You are about to remove {name} from the band. Continue?",
      removed: "Member removed from the band.",
      removeError: "The member could not be removed.",
      userFallback: "User #{id}"
    },
    recruitments: {
      eyebrow: "Searches",
      title: "Active member searches",
      publicEyebrow: "Opportunities",
      publicTitle: "Bands looking for members",
      count: "{count} searches",
      empty: "There are no active member searches for your bands.",
      publicEmpty: "There are no open searches right now.",
      vacancyShort: "{count} vac.",
      closed: "The member search has been closed successfully.",
      closeError: "The member search could not be closed."
    },
    list: {
      eyebrow: "Personal catalog",
      title: "All my bands",
      mineEyebrow: "Personal catalog",
      mineTitle: "All my bands",
      publicEyebrow: "Public catalog",
      publicTitle: "Bands on FIOS",
      total: "{count} bands"
    },
    views: {
      explore: "Explore bands",
      mine: "My bands"
    },
    public: {
      eyebrow: "Explore",
      title: "Discover bands and music projects",
      text: "Browse active bands, genres, cities and open opportunities without signing in.",
      register: "Create account",
      login: "Sign in",
      recruitments: "View open searches",
      howEyebrow: "How it works",
      howTitle: "From catalog to collaboration",
      howText: "Explore bands first, open their detail and check active searches when there is a fit.",
      stepExplore: "Filter by city, genre or status",
      stepOpen: "Open any band detail",
      stepRecruit: "Check offers to join"
    },
    filters: {
      searchPlaceholder: "Search by band name or city",
      genre: "Music genre",
      allGenres: "All genres",
      status: "Status",
      allStates: "All statuses",
      order: "Sort by",
      clear: "Clear filters",
      orderOptions: {
        recent: "Recent activity",
        name: "Name",
        city: "City",
        members: "Most members"
      }
    },
    tabs: {
      all: "All",
      active: "Active",
      forming: "Forming",
      recruiting: "Looking for members"
    },
    actions: {
      viewBand: "View band",
      viewDetail: "View detail",
      manageMembers: "Manage members",
      publishRecruitment: "Publish search",
      publishEvent: "Propose event",
      leaveBand: "Leave band",
      searchMembers: "Search members",
      edit: "Edit",
      viewRecruitment: "View listing",
      manageRecruitment: "Manage",
      closeRecruitment: "Close",
      retry: "Retry",
      cancel: "Cancel"
    },
    detail: {
      loading: "Loading band...",
      errorTitle: "We could not open this band",
      error: "Band detail could not be loaded.",
      eyebrow: "Band detail",
      imageFallback: "Band image unavailable",
      viewRecruitments: "View active member searches",
      membersTitle: "Band members",
      recruitmentsTitle: "Open member searches",
      heroLead: "{members} in {city}",
      createdAtInline: "Since {date}",
      activeRecruitment: "Open search",
      roleGuest: "Visitor",
      fields: {
        members: "Members",
        recruitments: "Member searches",
        createdAt: "Created at",
        role: "Your relation"
      }
    },
    leave: {
      eyebrow: "Members",
      title: "Leave band",
      confirm: 'Are you sure you want to leave "{name}"?',
      confirmAction: "Leave band",
      success: "You have left the band successfully.",
      error: "Unable to leave the band."
    },
    cards: {
      imageFallback: "Band image unavailable",
      noDescription: "No description available yet.",
      noGenre: "Genre pending",
      noCity: "City pending",
      membersValue: "{count} members"
    },
    empty: {
      title: "You are not part of any band yet",
      text: "Create your first band to manage members, publish searches and keep your music activity together in FIOS.",
      publicTitle: "There are no public bands yet",
      publicText: "Active projects will appear here together with their open searches.",
      create: "Create band",
      explore: "Explore bands",
      filteredTitle: "No bands match these filters",
      filteredText: "Try changing the genre, status or search text."
    },
    modals: {
      band: {
        createEyebrow: "New band",
        createTitle: "Create band",
        editEyebrow: "Edit band",
        editTitle: "Update band",
        name: "Name",
        genre: "Main genre",
        city: "Base city",
        image: "Image",
        description: "Description",
        submit: "Create band",
        update: "Save changes",
        validation: "Complete name, genre and city before saving.",
        created: "The band has been created successfully.",
        updated: "The band has been updated successfully.",
        error: "Unable to save the band."
      },
      recruitment: {
        eyebrow: "New search",
        title: "Publish member search",
        band: "Band",
        selectBand: "Select a band",
        instrument: "Instrument",
        selectInstrument: "Select an instrument",
        positionTitle: "Title",
        role: "Role wanted",
        level: "Required level",
        city: "City",
        vacancies: "Vacancies",
        description: "Description",
        submit: "Publish search",
        validation: "Complete band, instrument, title, role, city and vacancies before publishing.",
        created: "The search has been published successfully.",
        error: "Unable to publish the search."
      },
      event: {
        eyebrow: "Band agenda",
        title: "Propose event",
        band: "Band",
        selectBand: "Select a band",
        musicalSpace: "Musical space",
        noMusicalSpace: "No linked space",
        eventTitle: "Title",
        type: "Type",
        date: "Date",
        startTime: "Start time",
        endTime: "End time",
        venueName: "Venue",
        city: "City",
        province: "Province",
        country: "Country",
        genre: "Music genre",
        capacity: "Capacity",
        ticketPrice: "Price",
        location: "Address",
        posterImage: "Poster image",
        description: "Description",
        submit: "Submit for review",
        validation: "Complete band, title, type, date, venue, city and country before submitting.",
        timeValidation: "The start time must be before the end time.",
        created: "The event is pending administrator approval.",
        error: "Unable to submit the event."
      }
    },
    placeholders: {
      noLeaderBand: "You need to be responsible for at least one band to publish a search.",
      noLeaderEventBand: "You need to be responsible for an active band to propose an event."
    },
    statuses: {
      active: "Active",
      inactive: "Inactive",
      forming: "Forming",
      recruiting: "Looking for members"
    },
    levels: {
      BEGINNER: "Beginner",
      INTERMEDIATE: "Intermediate",
      ADVANCED: "Advanced",
      PROFESSIONAL: "Professional"
    },
    roles: {
      LEADER: "Responsible",
      MEMBER: "Member"
    }
  },
  admin: {
    header: {
      eyebrow: "FIOS Administration",
      title: "Administration panel",
      subtitle:
        "Supervise users, spaces, reservations, reviews and events from one place.",
      roleBadge: "Administrators only"
    },
    actions: {
      refreshOverview: "Refresh overview",
      refresh: "Reload",
      retry: "Retry",
      resetFilters: "Clear filters",
      close: "Close"
    },
    sections: {
      navigation: "Admin panel navigation",
      overview: "Overview",
      users: "Users",
      spaces: "Spaces",
      reservations: "Reservations",
      reviews: "Reviews",
      events: "Events",
      recruitments: "Member searches"
    },
    summary: {
      visibleOfTotal: "{visible} of {total}"
    },
    forbidden: {
      title: "Restricted access",
      text: "These screens are only available to accounts with administrator access.",
      action: "Go home"
    },
    confirm: {
      eyebrow: "Confirmation"
    },
    badges: {
      active: "Active",
      inactive: "Inactive",
      inactiveUsers: "Inactive users"
    },
    roles: {
      ADMIN: "Administrator",
      USER: "User"
    },
    approvalStatuses: {
      PENDING: "Pending",
      APPROVED: "Approved",
      REJECTED: "Rejected"
    },
    metrics: {
      users: "Users",
      usersSubtitle: "{count} administrators",
      pendingSpaces: "Pending spaces",
      pendingSpacesSubtitle: "{count} approved",
      activeReservations: "Active reservations",
      activeReservationsSubtitle: "{count} pending",
      reviews: "Reviews",
      reviewsSubtitle: "Moderation available",
      events: "Events",
      eventsSubtitle: "Admin catalog",
      recruitments: "Member searches",
      recruitmentsSubtitle: "Supervised listings"
    },
    states: {
      loadingOverview: "Loading admin overview...",
      loadingUsers: "Loading users...",
      loadingSpaces: "Loading musical spaces...",
      loadingReservations: "Loading reservations...",
      loadingReviews: "Loading reviews...",
      loadingEvents: "Loading events...",
      loadingRecruitments: "Loading member searches...",
      errorTitle: "This section could not be loaded",
      overviewError: "The administration overview could not be loaded.",
      usersError: "The user list could not be loaded.",
      spacesError: "The space list could not be loaded.",
      reservationsError: "The reservation list could not be loaded.",
      reviewsError: "The review list could not be loaded.",
      eventsError: "The event list could not be loaded.",
      recruitmentsError: "The member search list could not be loaded.",
      actionError: "The requested action could not be completed."
    },
    filters: {
      allRoles: "All roles",
      allStates: "All states",
      allStatuses: "All statuses",
      allSources: "All sources",
      allRatings: "All ratings",
      allInstruments: "All instruments"
    },
    fallbacks: {
      space: "Musical space",
      event: "Event",
      recruitment: "Listing",
      currentSession: "current session"
    },
    overview: {
      title: "General overview",
      subtitle: "Quick reading of the platform state and the use of manageable resources.",
      userBreakdown: "User distribution",
      spaceBreakdown: "Approval states",
      reservationBreakdown: "Reservation states",
      securityTitle: "Protected access",
      securityText:
        "Current session: {email}. Only accounts with administrator access can enter here.",
      securityAction: "Review users"
    },
    users: {
      title: "User management",
      subtitle:
        "Review the real user list and activate or deactivate accounts without exposing sensitive data.",
      emptyTitle: "No users to display",
      emptyText: "Adjust filters or reload the section to recover results.",
      activatedSuccess: "User activated successfully.",
      deactivatedSuccess: "User deactivated successfully.",
      promotedSuccess: "User promoted to administrator successfully.",
      adminRoleRevokedSuccess: "Administrator role removed successfully.",
      selfActionBlocked: "You cannot modify your own administrator account.",
      confirmActivateTitle: "Activate user",
      confirmDeactivateTitle: "Deactivate user",
      confirmPromoteTitle: "Make administrator",
      confirmRevokeAdminTitle: "Remove administrator",
      confirmActivateText: "You are about to activate the account for {name}.",
      confirmDeactivateText: "You are about to deactivate the account for {name}.",
      confirmPromoteText: "You are about to make {name}'s account an administrator.",
      confirmRevokeAdminText:
        "You are about to remove the administrator role from {name}'s account.",
      filters: {
        search: "Search by name, email or phone"
      },
      columns: {
        name: "Name",
        email: "Email",
        phone: "Phone",
        role: "Role",
        status: "Status",
        createdAt: "Created",
        actions: "Actions"
      },
      actions: {
        activate: "Activate",
        deactivate: "Deactivate",
        promoteToAdmin: "Make administrator",
        removeAdminRole: "Remove administrator",
        currentUser: "Your account"
      }
    },
    spaces: {
      title: "Space approval",
      subtitle: "Control the approval status of musical spaces published on the platform.",
      emptyTitle: "No spaces to display",
      emptyText: "No spaces were found for the current filters.",
      confirmTitle: "Update approval",
      confirmText: 'You are about to mark "{name}" as {status}.',
      updatedSuccess: "Space status updated successfully.",
      filters: {
        search: "Search by name, city or responsible person",
        city: "Filter by city"
      },
      columns: {
        name: "Space",
        city: "City",
        manager: "Responsible person",
        type: "Type",
        approvalStatus: "Approval",
        active: "Active",
        actions: "Actions"
      },
      actions: {
        approve: "Approve",
        reject: "Reject",
        markPending: "Mark pending",
        availability: "Availability",
        viewPublic: "View public",
        applyStatus: "Apply status"
      }
    },
    reservations: {
      title: "Reservation supervision",
      subtitle: "Review real sessions, open conversations and cancel reservations with a reason.",
      emptyTitle: "No reservations to display",
      emptyText: "Try changing text, date or status to recover results.",
      cancelTitle: "Cancel reservation",
      cancelText: "You are about to cancel the reservation linked to {name}.",
      cancelReasonLabel: "Cancellation reason",
      cancelReasonPlaceholder: "Explain to the user why this reservation is being cancelled.",
      cancelReasonRequired: "You must provide a reason to cancel the reservation.",
      cancelSuccess: "Reservation cancelled successfully.",
      filters: {
        search: "Search by space, user, band or notes"
      },
      columns: {
        space: "Space",
        user: "User",
        date: "Date",
        time: "Time",
        status: "Status",
        price: "Price",
        attendees: "Attendees",
        actions: "Actions"
      },
      actions: {
        messages: "Messages",
        cancel: "Cancel"
      }
    },
    reviews: {
      title: "Review moderation",
      subtitle: "Delete problematic content and supervise reviews published for spaces and users.",
      emptyTitle: "No reviews to display",
      emptyText: "No reviews were found for the current filters.",
      noComment: "No additional comment.",
      deletedSuccess: "Review deleted successfully.",
      confirmDeleteTitle: "Delete review",
      confirmDeleteText: "You are about to remove a review for {target}.",
      ratingLabel: "{value}/5",
      types: {
        SPACE: "Space",
        USER: "User"
      },
      filters: {
        search: "Search by target, author, user or comment",
        ratingValue: "{value} stars"
      },
      columns: {
        type: "Type",
        space: "Target",
        user: "Author",
        rating: "Rating",
        comment: "Comment",
        date: "Date",
        actions: "Actions"
      },
      actions: {
        delete: "Delete"
      }
    },
    events: {
      title: "Event management",
      subtitle:
        "Supervise your own and external events, open their public detail and update their information.",
      emptyTitle: "No events to display",
      emptyText: "No events were found for the current filters.",
      createdSuccess: "Event created successfully.",
      updatedSuccess: "Event updated successfully.",
      publishedSuccess: "Event published successfully.",
      archivedSuccess: "Event archived successfully.",
      saveError: "The event could not be saved.",
      confirmArchiveTitle: "Archive event",
      confirmArchiveText: 'You are about to archive the event "{title}".',
      filters: {
        search: "Search by title, place, city or genre",
        city: "Filter by city"
      },
      columns: {
        title: "Event",
        date: "Date",
        city: "City",
        source: "Source",
        status: "Status",
        type: "Type",
        actions: "Actions"
      },
      actions: {
        create: "Create event",
        importTicketmaster: "Import Ticketmaster",
        view: "View detail",
        publish: "Publish",
        edit: "Edit",
        archive: "Archive"
      }
    },
    recruitments: {
      title: "Member searches",
      subtitle: "Supervise listings published by bands and remove posts when necessary.",
      emptyTitle: "No listings to display",
      emptyText: "No listings were found for the current filters.",
      deletedSuccess: "Listing deleted successfully.",
      confirmDeleteTitle: "Delete listing",
      confirmDeleteText: 'You are about to delete the post "{title}".',
      statuses: {
        OPEN: "Open",
        CLOSED: "Closed"
      },
      filters: {
        search: "Search by title, band, instrument or city"
      },
      columns: {
        title: "Title",
        band: "Band",
        instrument: "Instrument",
        level: "Level",
        city: "City",
        status: "Status",
        publicationDate: "Published",
        actions: "Actions"
      },
      actions: {
        delete: "Delete"
      }
    }
  }
};
