package it.unibz.inf.ontouml.vp.utils;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IHasChildrenBaseModelElement;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.factory.IModelElementFactory;
import it.unibz.inf.ontouml.vp.model.uml.Association;
import it.unibz.inf.ontouml.vp.model.uml.Class;
import it.unibz.inf.ontouml.vp.model.RelatedSource;
import it.unibz.inf.ontouml.vp.model.SourceScope;
import com.vp.plugin.model.IReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Builds the system and user prompts sent to the LLM for the "Suggest Metaclass (AI)"
 * feature: the system prompt embeds the UFO Modeling Decision Tree guide (loaded once and
 * cached from a bundled resource file), while the user prompt is a plain-text summary of a
 * single class's relevant modeling context (stereotype, generalizations, attributes,
 * associations).
 */
public class LlmPromptContextBuilder {

  private static final int MAX_DESCRIPTION_LENGTH = 1500;

  public static String getSystemPrompt() {
    return LlmPromptResources.getSystemPrompt();
  }

  public static String buildClassSummary(IClass _class) {
    final StringBuilder summary = new StringBuilder();

    summary.append("Class: \"").append(_class.getName()).append("\"\n");
    summary.append("Current stereotype: ").append(describeCurrentStereotype(_class)).append("\n");
    summary.append("Is abstract: ").append(_class.isAbstract()).append("\n");
    summary.append("Description: ").append(describeDescription(_class)).append("\n");
    summary.append("Superclasses: ").append(describeRelatedClasses(Class.getParents(_class)));
    summary.append("\n");
    summary.append("Subclasses: ").append(describeRelatedClasses(Class.getChildren(_class)));
    summary.append("\n");
    summary.append("Attributes:\n").append(describeAttributes(_class));
    summary.append("Associations:\n").append(describeAssociations(_class));

    return summary.toString();
  }

  /**
   * The class' documentation, which often carries the decisive evidence (what the thing is, what
   * it depends on, whether instances can stop being it). Capped so a long text cannot crowd the
   * rest of the context out of the request.
   */
  private static String describeDescription(IClass _class) {
    final String description = _class.getDescription();

    if (description == null || description.trim().isEmpty()) {
      return "(none)";
    }

    final String singleLine = description.trim().replaceAll("\\s+", " ");

    return singleLine.length() <= MAX_DESCRIPTION_LENGTH
        ? singleLine
        : singleLine.substring(0, MAX_DESCRIPTION_LENGTH) + "...";
  }

  private static String stereotypeOf(IModelElement element) {
    if (element.stereotypeCount() == 0) {
      return "no stereotype";
    }

    final IStereotype[] stereotypes = element.toStereotypeModelArray();
    final StringBuilder names = new StringBuilder();

    for (int i = 0; stereotypes != null && i < stereotypes.length; i++) {
      names.append(i == 0 ? "" : ", ").append(stereotypes[i].getName());
    }

    return names.length() == 0 ? "no stereotype" : names.toString();
  }

  private static String describeCurrentStereotype(IClass _class) {
    final String[] stereotypes = _class.toStereotypeArray();
    return stereotypes == null || stereotypes.length == 0 ? "(none)" : String.join(", ", stereotypes);
  }

  private static String describeRelatedClasses(Set<IClass> relatedClasses) {
    if (relatedClasses.isEmpty()) {
      return "(none)";
    }

    return relatedClasses.stream()
        .map(
            related -> {
              final String[] stereotypes = related.toStereotypeArray();
              final String stereotype =
                  stereotypes == null || stereotypes.length == 0
                      ? "no stereotype"
                      : String.join(", ", stereotypes);
              return related.getName() + " (" + stereotype + ")";
            })
        .collect(Collectors.joining(", "));
  }

  private static String describeAttributes(IClass _class) {
    final IAttribute[] attributes = _class.toAttributeArray();

    if (attributes == null || attributes.length == 0) {
      return "  (none)\n";
    }

    final StringBuilder builder = new StringBuilder();
    for (IAttribute attribute : attributes) {
      builder
          .append("  - ")
          .append(attribute.getName())
          .append(": ")
          .append(attribute.getTypeAsString())
          .append(" [")
          .append(attribute.getMultiplicity())
          .append("]\n");
    }

    return builder.toString();
  }

  private static String describeAssociations(IClass _class) {
    final IProject project = ApplicationManager.instance().getProjectManager().getProject();
    final IModelElement[] allAssociations =
        project.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_ASSOCIATION);
    final StringBuilder builder = new StringBuilder();
    boolean hasAssociation = false;

    for (int i = 0; allAssociations != null && i < allAssociations.length; i++) {
      final IAssociation association = (IAssociation) allAssociations[i];
      final IClass source = Association.getSource(association);
      final IClass target = Association.getTarget(association);

      if (source == null || target == null) {
        continue;
      }

      final boolean isSource = source.equals(_class);
      final boolean isTarget = target.equals(_class);

      if (!isSource && !isTarget) {
        continue;
      }

      hasAssociation = true;
      final IClass other = isSource ? target : source;
      final IAssociationEnd otherEnd = isSource ? Association.getTargetEnd(association) : Association.getSourceEnd(association);

      // the association's own stereotype (mediation, characterization, componentOf, ...) is the
      // strongest signal in the model for several branches of the decision tree
      builder
          .append("  - «")
          .append(stereotypeOf(association))
          .append("» ")
          .append(safeName(association.getName()))
          .append(" ")
          .append(isSource ? "-> " : "<- ")
          .append(other.getName())
          .append(" (")
          .append(stereotypeOf(other))
          .append("; role: ")
          .append(safeName(otherEnd.getName()))
          .append(", multiplicity: ")
          .append(otherEnd.getMultiplicity())
          .append(", aggregation: ")
          .append(otherEnd.getAggregationKind())
          .append(")\n");
    }

    return hasAssociation ? builder.toString() : "  (none)\n";
  }

  private static String safeName(String name) {
    return name == null || name.trim().isEmpty() ? "(unnamed)" : name;
  }

  // ------------------------------------------------------------------ documentation context

  /**
   * The class being documented: unlike the classification summary, its existing description goes in
   * whole, because the action refines what is there instead of replacing it.
   */
  public static String buildDocumentationClassSummary(IClass _class) {
    final StringBuilder summary = new StringBuilder();

    summary.append("Class: \"").append(_class.getName()).append("\"\n");
    summary.append("Stereotype: ").append(describeCurrentStereotype(_class)).append("\n");
    summary.append("Attributes:\n").append(describeAttributes(_class));

    final String description = _class.getDescription();

    if (description == null || description.trim().isEmpty()) {
      summary.append("\nExisting documentation: (none — this class is undocumented)\n");
    } else {
      summary.append("\nExisting documentation (improve it, do not discard it):\n");
      summary.append(description.trim()).append("\n");
    }

    return summary.toString();
  }

  /**
   * The neighbourhood of the class: every related class with the relationship that ties it, its
   * short description only — the full text would swamp the prompt — and, gathered at the end, the
   * normative sources those classes already cite, which are candidates for reuse.
   */
  public static String buildRelatedClassesSummary(IClass _class) {
    final StringBuilder summary = new StringBuilder();
    final Set<String> normativeSources = new LinkedHashSet<>();
    boolean hasRelated = false;

    for (IClass parent : Class.getParents(_class)) {
      hasRelated = true;
      appendRelated(summary, normativeSources, "specialises (superclass)", parent);
    }

    for (IClass child : Class.getChildren(_class)) {
      hasRelated = true;
      appendRelated(summary, normativeSources, "is specialised by (subclass)", child);
    }

    final IProject project = ApplicationManager.instance().getProjectManager().getProject();
    final IModelElement[] allAssociations =
        project.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_ASSOCIATION);

    for (int i = 0; allAssociations != null && i < allAssociations.length; i++) {
      final IAssociation association = (IAssociation) allAssociations[i];
      final IClass source = Association.getSource(association);
      final IClass target = Association.getTarget(association);

      if (source == null || target == null) {
        continue;
      }

      final boolean isSource = source.equals(_class);

      if (!isSource && !target.equals(_class)) {
        continue;
      }

      hasRelated = true;
      final IClass other = isSource ? target : source;
      final String relation =
          "«" + stereotypeOf(association) + "» " + safeName(association.getName())
              + (isSource ? " (outgoing)" : " (incoming)");

      appendRelated(summary, normativeSources, relation, other);
    }

    if (!hasRelated) {
      summary.append("(this class has no related classes)\n");
    }

    if (!normativeSources.isEmpty()) {
      summary.append("\nNormative sources already cited by the classes above")
          .append(" (reuse one only when it genuinely applies, keeping any (não verificado) mark):\n");

      for (String source : normativeSources) {
        summary.append("  - ").append(source).append("\n");
      }
    }

    return summary.toString();
  }

  private static void appendRelated(
      StringBuilder summary, Set<String> normativeSources, String relation, IClass related) {
    final DocumentationTemplate documentation =
        DocumentationTemplate.parse(related.getDescription());

    summary
        .append("- ")
        .append(relation)
        .append(": ")
        .append(related.getName())
        .append(" («")
        .append(describeCurrentStereotype(related))
        .append("»)\n");

    summary
        .append("    ")
        .append(
            documentation.isEmpty()
                ? "(undocumented)"
                : documentation.getShortDescription())
        .append("\n");

    normativeSources.addAll(documentation.getNormativeSources());
  }

  /**
   * Every reference reachable from this class, in the order they deserve to be read.
   *
   * <ol>
   *   <li>the class' own — the material its author attached to it, and the most direct evidence
   *       there is about what it means;
   *   <li>its neighbours' — what a related class was documented from;
   *   <li>the diagrams it appears on — sources about the slice of the domain it was drawn in;
   *   <li>the packages and model above it — sources about the domain as a whole.
   * </ol>
   *
   * <p>De-duplicated by locator, keeping the first occurrence, so a source cited in two places is
   * read once and credited to the closest one. The order matters beyond tidiness: the caller caps
   * how many sources it will read, and this is what keeps the class' own references from being
   * dropped in favour of a package's.
   */
  public static List<RelatedSource> collectSources(IClass _class) {
    final Map<String, RelatedSource> byLocator = new LinkedHashMap<>();
    final List<RelatedSource> withoutLocator = new ArrayList<>();
    final Collector collector = new Collector(byLocator, withoutLocator);

    collector.take(_class.toReferenceArray(), SourceScope.OWN_CLASS, null);

    final Set<IClass> related = new LinkedHashSet<>();
    related.addAll(Class.getParents(_class));
    related.addAll(Class.getChildren(_class));
    related.addAll(associatedClasses(_class));

    for (IClass neighbour : related) {
      collector.take(neighbour.toReferenceArray(), SourceScope.RELATED_CLASS, neighbour.getName());
    }

    for (IDiagramUIModel diagram : diagramsOf(_class)) {
      collector.take(diagram.toReferenceArray(), SourceScope.DIAGRAM, diagram.getName());
    }

    for (IHasChildrenBaseModelElement ancestor : ancestorsOf(_class)) {
      collector.take(ancestor.toReferenceArray(), SourceScope.MODEL, ancestor.getName());
    }

    final List<RelatedSource> sources = new ArrayList<>(byLocator.values());

    // a reference that names a source without pointing at one goes last: it can only be resolved
    // by a search, and the ones that carry a locator are the cheaper, surer material
    sources.addAll(withoutLocator);

    if (collector.skipped > 0) {
      System.out.println(
          "Skipped "
              + collector.skipped
              + " reference(s) of \""
              + _class.getName()
              + "\" that point inside the model (diagram, shape, element or folder): there is no"
              + " text to retrieve from those.");
    }

    return sources;
  }

  /** Gathers references from one element after another, keeping the first sighting of each. */
  private static final class Collector {

    private final Map<String, RelatedSource> byLocator;
    private final List<RelatedSource> withoutLocator;
    private final Set<String> captionsWithoutLocator = new LinkedHashSet<>();

    private int skipped;

    private Collector(Map<String, RelatedSource> byLocator, List<RelatedSource> withoutLocator) {
      this.byLocator = byLocator;
      this.withoutLocator = withoutLocator;
    }

    private void take(IReference[] references, SourceScope scope, String origin) {
      for (int i = 0; references != null && i < references.length; i++) {
        final IReference reference = references[i];

        if (!pointsOutward(reference)) {
          skipped++;
          continue;
        }

        final String locator = reference.getUrl() == null ? "" : reference.getUrl().trim();
        final String caption = captionOf(reference, locator);

        if (locator.isEmpty()) {
          // nothing to dedupe on but the caption, and an unlocated source is only worth chasing
          // once however many elements repeat it
          if (caption.isEmpty() || !captionsWithoutLocator.add(caption.toLowerCase())) {
            continue;
          }

          withoutLocator.add(source(reference, scope, origin, "", caption));
          continue;
        }

        if (byLocator.containsKey(locator)) {
          continue;
        }

        byLocator.put(locator, source(reference, scope, origin, locator, caption));
      }
    }

    private static RelatedSource source(
        IReference reference, SourceScope scope, String origin, String locator, String caption) {
      return new RelatedSource(scope, origin, caption, locator, reference.getDescription());
    }

    /**
     * Whether the reference points at something outside the model. A reference to a diagram, a
     * shape, another element or a folder is navigation within Visual Paradigm: there is no document
     * behind it to read, and the elements it links to are already in the prompt by other means.
     */
    private static boolean pointsOutward(IReference reference) {
      final int type = reference.getType();

      return type == IReference.TYPE_URL || type == IReference.TYPE_FILE;
    }

    private static String captionOf(IReference reference, String locator) {
      final String name = reference.getName();

      if (name != null && !name.trim().isEmpty()) {
        return name.trim();
      }

      final String description = reference.getDescription();

      if (!locator.isEmpty()) {
        return locator;
      }

      return description == null ? "" : description.trim();
    }
  }

  /** The diagrams this class is drawn on, each counted once. */
  private static List<IDiagramUIModel> diagramsOf(IClass _class) {
    final Map<String, IDiagramUIModel> byId = new LinkedHashMap<>();
    final IDiagramElement[] elements = _class.getDiagramElements();

    for (int i = 0; elements != null && i < elements.length; i++) {
      final IDiagramUIModel diagram = elements[i].getDiagramUIModel();

      if (diagram != null && diagram.getId() != null) {
        byId.putIfAbsent(diagram.getId(), diagram);
      }
    }

    return new ArrayList<>(byId.values());
  }

  /**
   * The packages and model containing this class, innermost first. They are where a reference that
   * applies to the whole domain is put, and Visual Paradigm offers the same References tab there.
   */
  private static List<IHasChildrenBaseModelElement> ancestorsOf(IClass _class) {
    final List<IHasChildrenBaseModelElement> ancestors = new ArrayList<>();
    IModelElement parent = _class.getParent();

    // the model tree is shallow, but a corrupt project must not turn this into an endless walk
    for (int depth = 0; parent != null && depth < 32; depth++) {
      if (parent instanceof IHasChildrenBaseModelElement && !(parent instanceof IClass)) {
        ancestors.add((IHasChildrenBaseModelElement) parent);
      }

      parent = parent.getParent();
    }

    return ancestors;
  }

  private static Set<IClass> associatedClasses(IClass _class) {
    final Set<IClass> associated = new LinkedHashSet<>();
    final IProject project = ApplicationManager.instance().getProjectManager().getProject();
    final IModelElement[] allAssociations =
        project.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_ASSOCIATION);

    for (int i = 0; allAssociations != null && i < allAssociations.length; i++) {
      final IAssociation association = (IAssociation) allAssociations[i];
      final IClass source = Association.getSource(association);
      final IClass target = Association.getTarget(association);

      if (source == null || target == null) {
        continue;
      }

      if (source.equals(_class)) {
        associated.add(target);
      } else if (target.equals(_class)) {
        associated.add(source);
      }
    }

    return associated;
  }
}
