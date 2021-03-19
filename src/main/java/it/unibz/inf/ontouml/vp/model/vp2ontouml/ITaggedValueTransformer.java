package it.unibz.inf.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;
import it.unibz.inf.ontouml.vp.model.ontouml.model.ModelElement;
import it.unibz.inf.ontouml.vp.utils.StereotypesManager;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class ITaggedValueTransformer {

  public static void transform(IModelElement source, ModelElement target) {
    Map<String, Object> map = ITaggedValueTransformer.createTaggedValueMap(source);
    target.setPropertyAssignments(map);
  }

  private static Map<String, Object> createTaggedValueMap(IModelElement sourceElement) {
    ITaggedValueContainer taggedValueContainer = sourceElement.getTaggedValues();
    if (taggedValueContainer == null) {
      return new HashMap<>();
    }

    Map<String, Object> propertyAssignments = new HashMap<>();
    for (ITaggedValue taggedValue : taggedValueContainer.toTaggedValueArray()) {
      // Skips tagged values embedded in OntoUML stereotypes,
      // namely isExtensional, isPowertype, order.
      if (!isTaggedValueUserDefined(taggedValue)) {
        continue;
      }

      Map.Entry<String, Object> pair = transformTaggedValue(taggedValue);
      propertyAssignments.put(pair.getKey(), pair.getValue());
    }

    return propertyAssignments;
  }

  private static Map.Entry<String, Object> transformTaggedValue(ITaggedValue taggedValue) {
    String key = taggedValue.getName();
    Object value = taggedValue.getValue();

    if (value == null) {
      return new AbstractMap.SimpleEntry<>(key, null);
    }

    switch (taggedValue.getType()) {
      case 1:
        value = ReferenceTransformer.transformStub(taggedValue.getValueAsElement());
        break;
      case 5:
        value = safeGetIntegerValue(taggedValue);
        break;
      case 6:
        value = safeGetFloatValue(taggedValue);
        break;
      case 7:
        value = Boolean.parseBoolean((String) taggedValue.getValue());
        break;
      default:
        value = taggedValue.getValueAsString();
    }

    return new AbstractMap.SimpleEntry<>(key, value);
  }

  private static boolean isTaggedValueUserDefined(ITaggedValue taggedValue) {
    return !StereotypesManager.CLASS_TAGGED_VALUES.contains(taggedValue.getName());
  }

  private static Integer safeGetIntegerValue(ITaggedValue taggedValue) {
    try {
      return Integer.parseInt((String) taggedValue.getValue());
    } catch (Exception e) {
      return null;
    }
  }

  private static Float safeGetFloatValue(ITaggedValue taggedValue) {
    try {
      return Float.parseFloat((String) taggedValue.getValue());
    } catch (Exception e) {
      return null;
    }
  }
}
