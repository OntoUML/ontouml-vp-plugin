package it.unibz.inf.ontouml.vp.utils;

import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IReference;
import com.vp.plugin.model.factory.IModelElementFactory;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Publishes the sources cited in a class' documentation into Visual Paradigm's own References tab,
 * so they are navigable there instead of living only as text inside the description.
 */
public class ClassReferences {

  /**
   * Adds one reference per cited source that carries a URL. Sources without a URL (a bare "Lei
   * 14.133/2021, art. 8º") stay in the description only: VP's URL references need a target, and
   * inventing one would be worse than leaving the citation as prose.
   *
   * @return how many references were actually added.
   */
  public static int addFrom(IClass _class, String documentation) {
    final DocumentationTemplate parsed = DocumentationTemplate.parse(documentation);
    final Set<String> existing = existingUrls(_class);
    int added = 0;

    for (String source : parsed.getAllSources()) {
      final String url = DocumentationTemplate.urlOf(source);

      // skip what has no URL, and never add the same URL twice
      if (url == null || !existing.add(url)) {
        continue;
      }

      final IReference reference = IModelElementFactory.instance().createReference();
      reference.setType(IReference.TYPE_URL);
      reference.setUrl(url);
      reference.setName(DocumentationTemplate.captionOf(source));
      reference.setDescription(source);

      _class.addReference(reference);
      added++;
    }

    return added;
  }

  private static Set<String> existingUrls(IClass _class) {
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

  /** How many of the cited sources would become references, for the confirmation in the dialog. */
  public static int countLinkable(String documentation) {
    final List<String> sources = DocumentationTemplate.parse(documentation).getAllSources();
    int linkable = 0;

    for (String source : sources) {
      if (DocumentationTemplate.urlOf(source) != null) {
        linkable++;
      }
    }

    return linkable;
  }
}
