package es.udc.tfg.fios_rest.common.geo;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

public final class SpanishAutonomousCommunities {

  private static final List<Community> COMMUNITIES = List.of(
    community(
      "Andaluc\u00eda",
      List.of("Andaluc\u00eda"),
      List.of("Almer\u00eda", "C\u00e1diz", "C\u00f3rdoba", "Granada", "Huelva", "Ja\u00e9n", "M\u00e1laga", "Sevilla")
    ),
    community(
      "Arag\u00f3n",
      List.of("Arag\u00f3n"),
      List.of("Huesca", "Teruel", "Zaragoza")
    ),
    community(
      "Principado de Asturias",
      List.of("Principado de Asturias", "Asturias"),
      List.of("Asturias")
    ),
    community(
      "Islas Baleares",
      List.of("Islas Baleares", "Illes Balears", "Illas Baleares", "Baleares"),
      List.of("Islas Baleares", "Illes Balears", "Baleares")
    ),
    community(
      "Canarias",
      List.of("Canarias", "Islas Canarias", "Illas Canarias"),
      List.of("Las Palmas", "Santa Cruz de Tenerife")
    ),
    community(
      "Cantabria",
      List.of("Cantabria"),
      List.of("Cantabria")
    ),
    community(
      "Castilla-La Mancha",
      List.of("Castilla-La Mancha", "Castilla La Mancha", "Castela A Mancha", "Castela La Mancha"),
      List.of("Albacete", "Ciudad Real", "Cuenca", "Guadalajara", "Toledo")
    ),
    community(
      "Castilla y Le\u00f3n",
      List.of("Castilla y Le\u00f3n", "Castilla Le\u00f3n", "Castela e Le\u00f3n", "Castela Le\u00f3n"),
      List.of("\u00c1vila", "Burgos", "Le\u00f3n", "Palencia", "Salamanca", "Segovia", "Soria", "Valladolid", "Zamora")
    ),
    community(
      "Catalu\u00f1a",
      List.of("Catalu\u00f1a", "Catalunya"),
      List.of("Barcelona", "Girona", "Gerona", "Lleida", "L\u00e9rida", "Tarragona")
    ),
    community(
      "Comunidad Valenciana",
      List.of("Comunidad Valenciana", "Comunitat Valenciana", "Comunidade Valenciana"),
      List.of("Alicante", "Castell\u00f3n", "Castell\u00f3", "Valencia", "Val\u00e8ncia")
    ),
    community(
      "Extremadura",
      List.of("Extremadura"),
      List.of("Badajoz", "C\u00e1ceres")
    ),
    community(
      "Galicia",
      List.of("Galicia", "Galiza"),
      List.of("A Coru\u00f1a", "La Coru\u00f1a", "Coru\u00f1a", "Lugo", "Ourense", "Orense", "Pontevedra")
    ),
    community(
      "Comunidad de Madrid",
      List.of("Comunidad de Madrid", "Comunidade de Madrid", "Madrid"),
      List.of("Madrid")
    ),
    community(
      "Regi\u00f3n de Murcia",
      List.of("Regi\u00f3n de Murcia", "Rexi\u00f3n de Murcia", "Murcia"),
      List.of("Murcia")
    ),
    community(
      "Comunidad Foral de Navarra",
      List.of("Comunidad Foral de Navarra", "Comunidade Foral de Navarra", "Navarra"),
      List.of("Navarra")
    ),
    community(
      "Pa\u00eds Vasco",
      List.of("Pa\u00eds Vasco", "Euskadi"),
      List.of("\u00c1lava", "Araba", "Gipuzkoa", "Guip\u00fazcoa", "Bizkaia", "Vizcaya")
    ),
    community(
      "La Rioja",
      List.of("La Rioja", "Rioja", "A Rioxa"),
      List.of("La Rioja")
    )
  );

  private static final List<Alias> ALIASES = COMMUNITIES.stream()
    .flatMap(community -> community.aliases().stream()
      .map(alias -> new Alias(community.name(), normalize(alias))))
    .distinct()
    .sorted(Comparator.comparingInt((Alias alias) -> alias.normalizedAlias().length()).reversed())
    .toList();

  private SpanishAutonomousCommunities() {
  }

  public static List<String> canonicalNames() {
    return COMMUNITIES.stream().map(Community::name).toList();
  }

  public static Optional<String> detectIn(String value) {
    String normalized = normalize(value);
    if (normalized.isEmpty()) {
      return Optional.empty();
    }

    return ALIASES.stream()
      .filter(alias -> containsWholePhrase(normalized, alias.normalizedAlias()))
      .map(Alias::communityName)
      .findFirst();
  }

  public static Optional<String> canonicalName(String value) {
    String normalized = normalize(value);
    if (normalized.isEmpty()) {
      return Optional.empty();
    }

    return COMMUNITIES.stream()
      .filter(community -> normalize(community.name()).equals(normalized)
        || community.aliases().stream().map(SpanishAutonomousCommunities::normalize).anyMatch(normalized::equals))
      .map(Community::name)
      .findFirst();
  }

  public static boolean provinceBelongsTo(String autonomousCommunity, String province) {
    String normalizedProvince = normalize(province);
    if (normalizedProvince.isEmpty()) {
      return false;
    }

    return canonicalName(autonomousCommunity)
      .flatMap(communityName -> COMMUNITIES.stream()
        .filter(community -> community.name().equals(communityName))
        .findFirst())
      .map(community -> community.provinces().stream()
        .map(SpanishAutonomousCommunities::normalize)
        .anyMatch(candidate -> candidate.equals(normalizedProvince))
        || canonicalName(province)
          .map(provinceCommunity -> provinceCommunity.equals(community.name()))
          .orElse(false))
      .orElse(false);
  }

  public static String removeCommunityAliases(String value, String autonomousCommunity) {
    String normalized = normalize(value);
    Optional<String> canonicalName = canonicalName(autonomousCommunity);
    if (normalized.isEmpty() || canonicalName.isEmpty()) {
      return normalized;
    }

    String withoutAliases = normalized;
    List<String> aliases = new ArrayList<>();
    COMMUNITIES.stream()
      .filter(community -> community.name().equals(canonicalName.get()))
      .findFirst()
      .ifPresent(community -> aliases.addAll(community.aliases().stream()
        .map(SpanishAutonomousCommunities::normalize)
        .sorted(Comparator.comparingInt(String::length).reversed())
        .toList()));

    for (String alias : aliases) {
      withoutAliases = Pattern.compile("(^|\\s)" + Pattern.quote(alias) + "(\\s|$)")
        .matcher(withoutAliases)
        .replaceAll(" ");
    }

    return normalize(withoutAliases);
  }

  public static String normalize(String value) {
    if (value == null) {
      return "";
    }

    String withoutAccents = Normalizer.normalize(value, Normalizer.Form.NFD)
      .replaceAll("\\p{M}", "")
      .toLowerCase(Locale.ROOT);

    return withoutAccents
      .replaceAll("[^a-z0-9]+", " ")
      .replaceAll("\\s+", " ")
      .trim();
  }

  private static Community community(String name, List<String> aliases, List<String> provinces) {
    return new Community(name, aliases, provinces);
  }

  private static boolean containsWholePhrase(String haystack, String needle) {
    return !needle.isEmpty()
      && Pattern.compile("(^|\\s)" + Pattern.quote(needle) + "(\\s|$)").matcher(haystack).find();
  }

  private record Community(String name, List<String> aliases, List<String> provinces) {
  }

  private record Alias(String communityName, String normalizedAlias) {
  }
}
