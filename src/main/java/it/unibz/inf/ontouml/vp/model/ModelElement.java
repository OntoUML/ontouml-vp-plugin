package it.unibz.inf.ontouml.vp.model;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;
import com.vp.plugin.model.factory.IModelElementFactory;
import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * @author Victor Viola
 * @author Claudenir Fonseca
 */
public interface ModelElement {

  public static final String TYPE_PACKAGE = "Package";
  public static final String TYPE_CLASS = "Class";
  public static final String TYPE_RELATION = "Relation";
  public static final String TYPE_ASSOCIATION_CLASS = "Relation";
  public static final String TYPE_GENERALIZATION = "Generalization";
  public static final String TYPE_GENERALIZATION_SET = "GeneralizationSet";
  public static final String TYPE_PROPERTY = "Property";
  public static final String TYPE_LITERAL = "Literal";

  /** @return <code>IModelElement</code> on which the object is based. */
  public IModelElement getSourceModelElement();

  /** @return object's type in OntoUML Schema. */
  public String getOntoUMLType();

  /** @return object's ID (based on a <code>IModelElement</code>). */
  public String getId();

  /**
   * Returns Visual Paradigm's link to the related model element. This method removes the project's
   * name which originally start the link returned.
   *
   * @param modelElement - Instance of <code>ModelElement</code> based on a <code>IModelElement
   *     </code>.
   * @return a link identify a <code>IModelElement</code> in Visual Paradigm following the pattern
   *     <code>"vpp://modelelement/Cd.WKPaAUB22rwx4"</code>.
   */
  public static String getLink(ModelElement modelElement) {
    return modelElement.getSourceModelElement() != null
        ? ModelElement.getModelElementURI(modelElement.getSourceModelElement())
        : null;
  }

  /**
   * Returns Visual Paradigm's link to the related model element. This method removes the project's
   * name which originally start the link returned.
   *
   * @param modelElement
   * @return a link identify a <code>IModelElement</code> in Visual Paradigm following the pattern
   *     <code>"vpp://modelelement/Cd.WKPaAUB22rwx4"</code>.
   */
  public static String getModelElementURI(IModelElement modelElement) {
    if (modelElement == null) return null;

    final String link =
        ApplicationManager.instance().getProjectManager().getLink(modelElement, false);

    return link.substring(link.indexOf(".vpp:") + 1);
  }

  /**
   * Returns serialized JSON string of a <code>ModelElement</code> in OntoUML Schema.
   *
   * @param modelElement
   * @param pretty - <code>true</code> if return string should be indented.
   * @return serialized version JSON of a <code>ModelElement</code>.
   */
  public static String serialize(ModelElement modelElement, boolean pretty) {
    if (pretty) {
      return new GsonBuilder()
          .excludeFieldsWithoutExposeAnnotation()
          .serializeNulls()
          .setPrettyPrinting()
          .create()
          .toJson(modelElement);
    } else {
      return new GsonBuilder()
          .excludeFieldsWithoutExposeAnnotation()
          .serializeNulls()
          .create()
          .toJson(modelElement);
    }
  }

  /**
   * Returns serialized JSON string of the whole project in OntoUML Schema.
   *
   * @param pretty - <code>true</code> if return string should be indented.
   * @return serialized version JSON of whole project in OntoUML Schema.
   */
  public static String generateModel(boolean pretty) {
    final Model model = new Model();

    if (pretty) {
      return new GsonBuilder()
          .excludeFieldsWithoutExposeAnnotation()
          .serializeNulls()
          .setPrettyPrinting()
          .create()
          .toJson(model);
    } else {
      return new GsonBuilder()
          .excludeFieldsWithoutExposeAnnotation()
          .serializeNulls()
          .create()
          .toJson(model);
    }
  }

  public static String generateModel(HashSet<String> elements, boolean pretty) {

    final Model model = new Model(elements);
    /*
    Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
    StringSelection sl = new StringSelection(new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
    		.serializeNulls().setPrettyPrinting().create().toJson(model));
    c.setContents(sl, sl);
    */
    if (pretty) {
      return new GsonBuilder()
          .excludeFieldsWithoutExposeAnnotation()
          .serializeNulls()
          .setPrettyPrinting()
          .create()
          .toJson(model);
    } else {
      return new GsonBuilder()
          .excludeFieldsWithoutExposeAnnotation()
          .serializeNulls()
          .create()
          .toJson(model);
    }
  }

  public static String safeGetString(String s) {
    if (s != null && s.length() != 0) return s;
    return null;
  }

  public static String toOntoUMLSchemaType(IModelElement element) {
    switch (element.getModelType()) {
      case IModelElementFactory.MODEL_TYPE_MODEL:
      case IModelElementFactory.MODEL_TYPE_PACKAGE:
        return TYPE_PACKAGE;
      case IModelElementFactory.MODEL_TYPE_CLASS:
      case IModelElementFactory.MODEL_TYPE_DATA_TYPE:
        return TYPE_CLASS;
      case IModelElementFactory.MODEL_TYPE_ASSOCIATION:
      case IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS:
        return TYPE_RELATION;
      case IModelElementFactory.MODEL_TYPE_GENERALIZATION:
        return TYPE_GENERALIZATION;
      case IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET:
        return TYPE_GENERALIZATION_SET;
      case IModelElementFactory.MODEL_TYPE_ATTRIBUTE:
      case IModelElementFactory.MODEL_TYPE_ASSOCIATION_END:
        return TYPE_PROPERTY;
      case IModelElementFactory.MODEL_TYPE_ENUMERATION_LITERAL:
        return TYPE_LITERAL;
    }

    return null;
  }

  public static JsonObject transformPropertyAssignments(IModelElement sourceElement) {
    ITaggedValueContainer lContainer = sourceElement.getTaggedValues();
    if (lContainer == null) return null;

    JsonObject obj = new JsonObject();
    ITaggedValue[] lTaggedValues = lContainer.toTaggedValueArray();
    List<String> ignoredClassValues =
        Arrays.asList(
            new String[] {
              StereotypeUtils.PROPERTY_RESTRICTED_TO,
              StereotypeUtils.PROPERTY_IS_EXTENSIONAL,
              StereotypeUtils.PROPERTY_IS_POWERTYPE,
              StereotypeUtils.PROPERTY_ORDER
            });

    for (int i = 0; lTaggedValues != null && i < lTaggedValues.length; i++) {
      if (ignoredClassValues.contains(lTaggedValues[i].getName())) {
        continue;
      }

      switch (lTaggedValues[i].getType()) {
        case 1:
          JsonObject referenceTag = new JsonObject();

          if (lTaggedValues[i].getValueAsElement() != null) {
            referenceTag.addProperty(
                "type", ModelElement.toOntoUMLSchemaType(lTaggedValues[i].getValueAsElement()));
            referenceTag.addProperty("id", lTaggedValues[i].getValueAsElement().getId());
          } else {
            referenceTag = null;
          }
          obj.add(lTaggedValues[i].getName(), referenceTag);
          break;
        case 5:
          obj.addProperty(
              lTaggedValues[i].getName(), Integer.parseInt((String) lTaggedValues[i].getValue()));
          break;
        case 6:
          obj.addProperty(
              lTaggedValues[i].getName(), Float.parseFloat((String) lTaggedValues[i].getValue()));
          break;
        case 7:
          obj.addProperty(
              lTaggedValues[i].getName(),
              Boolean.parseBoolean((String) lTaggedValues[i].getValue()));
          break;
        default:
          obj.addProperty(lTaggedValues[i].getName(), (String) lTaggedValues[i].getValueAsString());
      }
    }

    if (obj.size() == 0) return null;

    return obj;
  }

  public static boolean getIsDerived(IModelElement element) {
    return element.getName().trim().startsWith("/");
  }

  public static void setIsDerived(IModelElement element, boolean isDerived) {
    final String currentName = element.getName() != null ? element.getName().trim() : "";

    if (getIsDerived(element)) {
      element.setName(currentName.substring(1));
    } else {
      element.setName("/" + currentName);
    }
  }
}
