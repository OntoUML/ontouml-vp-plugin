package it.unibz.inf.ontouml.vp.model;

/**
 * Where a reference was found, which is also how much weight it carries. A source the author
 * attached to the very class being documented speaks about that class; one attached to the model
 * speaks about the domain, and only sometimes about this class in it.
 *
 * <p>The order of the constants is the order sources are read in, so the class' own references are
 * never crowded out by a cap meant to bound the work.
 */
public enum SourceScope {
  OWN_CLASS("a própria classe"),
  RELATED_CLASS("classe"),
  DIAGRAM("diagrama"),
  MODEL("pacote");

  private final String label;

  SourceScope(String label) {
    this.label = label;
  }

  /** How the origin is named in the prompt, with the element's name appended where there is one. */
  public String describe(String elementName) {
    if (this == OWN_CLASS) {
      return label;
    }

    return elementName == null || elementName.trim().isEmpty()
        ? label
        : label + " " + elementName.trim();
  }
}
