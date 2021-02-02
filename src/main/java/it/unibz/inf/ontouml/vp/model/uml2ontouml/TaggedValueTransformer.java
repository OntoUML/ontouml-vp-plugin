package it.unibz.inf.ontouml.vp.model.uml2ontouml;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;
import it.unibz.inf.ontouml.vp.utils.StereotypesManager;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TaggedValueTransformer {

  public static boolean isTaggedValueUserDefined(ITaggedValue taggedValue) {
    return !StereotypesManager.CLASS_TAGGED_VALUES.contains(taggedValue.getName());
  }

  public static Map<String, Object> transformTaggedValue(IModelElement sourceElement) {

    ITaggedValueContainer taggedValueContainer = sourceElement.getTaggedValues();

    if (taggedValueContainer == null) {
      return new HashMap<>();
    }

    ITaggedValue[] taggedValues = taggedValueContainer.toTaggedValueArray();

    Map<String, Object> propertyAssignments =
        Stream.of(taggedValues)
            .filter(taggedValue -> isTaggedValueUserDefined(taggedValue))
            .filter(taggedValue -> taggedValue.getValueAsElement() == null)
            .collect(Collectors.toMap(ITaggedValue::getName, Function.identity()));

    Map<String, Object> elementPropertyAssignments =
        Stream.of(taggedValues)
            .filter(taggedValue -> isTaggedValueUserDefined(taggedValue))
            .filter(taggedValue -> taggedValue.getValueAsElement() != null)
            .collect(
                Collectors.toMap(
                    ITaggedValue::getName,
                    taggedValue ->
                        ReferenceTransformer.transformStub(taggedValue.getValueAsElement())));

    propertyAssignments.putAll(elementPropertyAssignments);

    return propertyAssignments;
  }
}
