package it.unibz.inf.ontouml.vp.model.ontouml;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface ElementContainer {

  List<OntoumlElement> getContents();

  List<OntoumlElement> getAllContents();

  default <T> List<T> getAllContentsByType(Class<T> type) {
    return getAllContents().stream()
        .filter(type::isInstance)
        .map(type::cast)
        .collect(Collectors.toList());
  }

  default <T extends OntoumlElement> Optional<T> getElementById(String id, Class<T> type) {

    List<OntoumlElement> elements =
        getAllContents().stream()
            .filter(e -> type.isInstance(e) && id.equals(e.getId()))
            .collect(Collectors.toList());

    if (elements.size() == 1) return Optional.of(type.cast(elements.get(0)));

    if (elements.size() == 0) return Optional.empty();

    throw new IllegalStateException(
        "There is more than one instance of " + type.getName() + " with the same id!");
  }
}
