package es.udc.tfg.fios_rest.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "properties")
public class Properties {
  // Necesario para usarlo en JWTFilter
  public static String CLIENT_HOST;

  private String clientHost;
  private String jwtSecretKey;
  private Long jwtValidity;
  private String rutaImagenes;
  private DatabaseLoader databaseLoader = new DatabaseLoader();
  private Geocoding geocoding = new Geocoding();
  private Ticketmaster ticketmaster = new Ticketmaster();
  private Ai ai = new Ai();

  public String getClientHost() {
    return clientHost;
  }

  public void setClientHost(String clientHost) {
    Properties.CLIENT_HOST = clientHost;
    this.clientHost = clientHost;
  }

  public String getJwtSecretKey() {
    return jwtSecretKey;
  }

  public void setJwtSecretKey(String jwtSecretKey) {
    this.jwtSecretKey = jwtSecretKey;
  }

  public Long getJwtValidity() {
    return jwtValidity;
  }

  public void setJwtValidity(Long jwtValidity) {
    this.jwtValidity = jwtValidity;
  }

  public String getRutaImagenes() {
    return rutaImagenes;
  }

  public void setRutaImagenes(String rutaImagenes) {
    this.rutaImagenes = rutaImagenes;
  }

  public DatabaseLoader getDatabaseLoader() {
    return databaseLoader;
  }

  public void setDatabaseLoader(DatabaseLoader databaseLoader) {
    this.databaseLoader = databaseLoader == null ? new DatabaseLoader() : databaseLoader;
  }

  public Geocoding getGeocoding() {
    return geocoding;
  }

  public void setGeocoding(Geocoding geocoding) {
    this.geocoding = geocoding == null ? new Geocoding() : geocoding;
  }

  public Ticketmaster getTicketmaster() {
    return ticketmaster;
  }

  public void setTicketmaster(Ticketmaster ticketmaster) {
    this.ticketmaster = ticketmaster == null ? new Ticketmaster() : ticketmaster;
  }

  public Ai getAi() {
    return ai;
  }

  public void setAi(Ai ai) {
    this.ai = ai == null ? new Ai() : ai;
  }

  public static class DatabaseLoader {
    private boolean enabled = false;

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }
  }

  public static class Geocoding {
    private String baseUrl = "https://nominatim.openstreetmap.org";
    private String userAgent = "FIOS-REST/1.0 (TFG geocoding integration)";
    private int timeout = 3000;
    private String countryCodes = "es";
    private int limit = 5;

    public String getBaseUrl() {
      return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
    }

    public String getUserAgent() {
      return userAgent;
    }

    public void setUserAgent(String userAgent) {
      this.userAgent = userAgent;
    }

    public int getTimeout() {
      return timeout;
    }

    public void setTimeout(int timeout) {
      this.timeout = timeout;
    }

    public String getCountryCodes() {
      return countryCodes;
    }

    public void setCountryCodes(String countryCodes) {
      this.countryCodes = countryCodes;
    }

    public int getLimit() {
      return limit;
    }

    public void setLimit(int limit) {
      this.limit = limit;
    }
  }

  public static class Ticketmaster {
    private boolean enabled = false;
    private String baseUrl = "https://app.ticketmaster.com/discovery/v2";
    private String apiKey;
    private int timeout = 3000;
    private String defaultCountryCode = "ES";
    private int defaultSize = 10;

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public String getBaseUrl() {
      return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
    }

    public String getApiKey() {
      return apiKey;
    }

    public void setApiKey(String apiKey) {
      this.apiKey = apiKey;
    }

    public int getTimeout() {
      return timeout;
    }

    public void setTimeout(int timeout) {
      this.timeout = timeout;
    }

    public String getDefaultCountryCode() {
      return defaultCountryCode;
    }

    public void setDefaultCountryCode(String defaultCountryCode) {
      this.defaultCountryCode = defaultCountryCode;
    }

    public int getDefaultSize() {
      return defaultSize;
    }

    public void setDefaultSize(int defaultSize) {
      this.defaultSize = defaultSize;
    }
  }

  public static class Ai {
    private SearchParser searchParser = new SearchParser();

    public SearchParser getSearchParser() {
      return searchParser;
    }

    public void setSearchParser(SearchParser searchParser) {
      this.searchParser = searchParser == null ? new SearchParser() : searchParser;
    }
  }

  public static class SearchParser {
    private boolean enabled = false;
    private String provider = "GEMINI";
    private String baseUrl = "https://generativelanguage.googleapis.com/v1beta";
    private String apiKey;
    private String model = "gemini-2.5-flash";
    private int timeout = 3000;
    private int maxOutputTokens = 512;
    private double temperature = 0d;

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }

    public String getProvider() {
      return provider;
    }

    public void setProvider(String provider) {
      this.provider = provider;
    }

    public String getBaseUrl() {
      return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
    }

    public String getApiKey() {
      return apiKey;
    }

    public void setApiKey(String apiKey) {
      this.apiKey = apiKey;
    }

    public String getModel() {
      return model;
    }

    public void setModel(String model) {
      this.model = model;
    }

    public int getTimeout() {
      return timeout;
    }

    public void setTimeout(int timeout) {
      this.timeout = timeout;
    }

    public int getMaxOutputTokens() {
      return maxOutputTokens;
    }

    public void setMaxOutputTokens(int maxOutputTokens) {
      this.maxOutputTokens = maxOutputTokens;
    }

    public double getTemperature() {
      return temperature;
    }

    public void setTemperature(double temperature) {
      this.temperature = temperature;
    }
  }
}
