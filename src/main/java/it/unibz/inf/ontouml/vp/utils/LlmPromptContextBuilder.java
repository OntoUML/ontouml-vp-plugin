package it.unibz.inf.ontouml.vp.utils;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.factory.IModelElementFactory;
import it.unibz.inf.ontouml.vp.model.uml.Association;
import it.unibz.inf.ontouml.vp.model.uml.Class;
import it.unibz.inf.ontouml.vp.model.RelatedSource;
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
   * The references sitting in the References tab of every class related to this one. They are the
   * material a neighbouring class was documented from, so they are worth reading for this one too.
   * De-duplicated by URL, and the class' own references are skipped: it already has them.
   */
  public static List<RelatedSource> collectRelatedSources(IClass _class) {
    final Map<String, RelatedSource> byUrl = new LinkedHashMap<>();

    for (String url : urlsOf(_class)) {
      byUrl.put(url, null);
    }

    final Set<IClass> related = new LinkedHashSet<>();
    related.addAll(Class.getParents(_class));
    related.addAll(Class.getChildren(_class));
    related.addAll(associatedClasses(_class));

    for (IClass neighbour : related) {
      final IReference[] references = neighbour.toReferenceArray();

      for (int i = 0; references != null && i < references.length; i++) {
        final IReference reference = references[i];
        final String url = reference.getUrl();

        if (url == null || url.trim().isEmpty() || byUrl.containsKey(url.trim())) {
          continue;
        }

        final String caption =
            reference.getName() == null || reference.getName().trim().isEmpty()
                ? url.trim()
                : reference.getName().trim();

        byUrl.put(url.trim(), new RelatedSource(neighbour.getName(), caption, url.trim()));
      }
    }

    final List<RelatedSource> sources = new ArrayList<>();

    for (RelatedSource source : byUrl.values()) {
      if (source != null) {
        sources.add(source);
      }
    }

    return sources;
  }

  private static Set<String> urlsOf(IClass _class) {
    final Set<String> urls = new LinkedHashSet<>();
    final IReference[] references = _class.toReferenceArray();

    for (int i = 0; references != null && i < references.length; i++) {
      final String url = references[i].getUrl();

      if (url != null && !url.trim().isEmpty()) {
        urls.add(url.trim());
      }
    }

    return urls;
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
