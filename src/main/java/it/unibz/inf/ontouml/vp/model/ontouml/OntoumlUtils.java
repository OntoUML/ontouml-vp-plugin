package it.unibz.inf.ontouml.vp.model.ontouml;

import java.util.Collection;
import java.util.function.Predicate;

public class OntoumlUtils {

  /** Adds all non-null elements of the origin collection to the destination collection. */
  public static <T> void addIfNotNull(Collection<T> destination, Collection<? extends T> origin) {
    origin.stream().filter(x -> x != null).forEach(x -> destination.add(x));
  }

  public static <T> boolean contains(Collection<T> collection, Predicate<T> condition) {
    return collection.stream().anyMatch(condition);
  }
}
