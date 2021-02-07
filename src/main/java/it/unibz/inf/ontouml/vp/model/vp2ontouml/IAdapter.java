package it.unibz.inf.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;

public interface IAdapter<T> {
  boolean isEmpty();

  IModelElement get();

  default Object getValueOfTaggedValue(String taggedValueName) {
    IModelElement element = get();
    ITaggedValueContainer container = element.getTaggedValues();

    if (container == null) {
      return null;
    }

    ITaggedValue taggedValue = container.getTaggedValueByName(taggedValueName);
    return taggedValue != null ? taggedValue.getValue() : null;
  }
}
