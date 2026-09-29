package it.unibz.inf.ontouml.vp.model;

/**
 * How far to look for the sources cited around a class before documenting it — the on/off switch
 * this used to be, widened into how much of the neighbourhood is worth the cost of reading.
 *
 * <p>The order matches {@link SourceScope}'s own weighting: a citation on the class itself is
 * never the one a narrower scope leaves out.
 */
public enum SourceReadScope {
  NONE("Don't retrieve"),
  DIAGRAM_ONLY("Retrieve from the diagram only"),
  CURRENT_CLASS_ONLY("Retrieve from the current class only"),
  ALL_NEIGHBOURS("Retrieve from all neighbouring classes (default)");

  private final String label;

  SourceReadScope(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  @Override
  public String toString() {
    return label;
  }

  /** Whether a source found at the given origin is read under this scope. */
  public boolean includes(SourceScope scope) {
    switch (this) {
      case NONE:
        return false;
      case DIAGRAM_ONLY:
        return scope == SourceScope.DIAGRAM;
      case CURRENT_CLASS_ONLY:
        return scope == SourceScope.OWN_CLASS;
      case ALL_NEIGHBOURS:
      default:
        return true;
    }
  }

  public static SourceReadScope fromName(String name) {
    for (SourceReadScope scope : values()) {
      if (scope.name().equals(name)) {
        return scope;
      }
    }

    return ALL_NEIGHBOURS;
  }
}
