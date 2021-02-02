package it.unibz.inf.ontouml.vp.model.uml2ontouml;

import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;
import it.unibz.inf.ontouml.vp.model.ontouml.Class;
import it.unibz.inf.ontouml.vp.utils.StereotypesManager;
import java.util.Map;

public class ClassTransformer {

  public static Class transformClass(IModelElement sourceElement) {
    if (!(sourceElement instanceof IClass)) return null;

    IClass source = (IClass) sourceElement;
    Class target = new Class();

    String id = source.getId();
    target.setId(id);

    String name = source.getName();
    target.addName(name);

    String description = source.getDescription();
    target.addDescription(description);

    Map<String, Object> map = TaggedValueTransformer.transformTaggedValue(source);
    target.setPropertyAssignments(map);

    String stereotype = StereotypeTransformer.getFirstStereotype(source);
    target.setStereotype(stereotype);

    boolean isAbstract = source.isAbstract();
    target.setAbstract(isAbstract);

    boolean isDerived = isDerived(source);
    target.setDerived(isDerived);

    Boolean isExtensional = isExtensional(source);
    target.setExtensional(isExtensional);

    Boolean isPowertype = isPowertype(source);
    target.setPowertype(isPowertype);

    Integer order = getOrder(source);
    target.setOrder(order);

    String[] restrictedTo = getRestrictedTo(source);
    if (restrictedTo != null) target.setRestrictedTo(restrictedTo);

    return target;
  }

  public static boolean isDerived(IClass clazz) {
    return clazz.getName() != null && clazz.getName().trim().startsWith("/");
  }

  public static String getName(IClass clazz) {
    if (isDerived(clazz)) {
      return clazz.getName().trim().substring(1);
    }

    return clazz.getName();
  }

  public static Object getValueOfTaggedValue(IClass clazz, String taggedValueName) {
    ITaggedValueContainer container = clazz.getTaggedValues();

    if (container == null) {
      return null;
    }

    ITaggedValue taggedValue = container.getTaggedValueByName(taggedValueName);
    return taggedValue != null ? taggedValue.getValue() : null;
  }

  public static Boolean isExtensional(IClass clazz) {
    Object value = getValueOfTaggedValue(clazz, StereotypesManager.PROPERTY_IS_EXTENSIONAL);
    return value instanceof Boolean ? (Boolean) value : null;
  }

  public static Boolean isPowertype(IClass clazz) {
    Object value = getValueOfTaggedValue(clazz, StereotypesManager.PROPERTY_IS_POWERTYPE);
    return value instanceof Boolean ? (Boolean) value : null;
  }

  public static Integer getOrder(IClass clazz) {
    Object value = getValueOfTaggedValue(clazz, StereotypesManager.PROPERTY_ORDER);
    return value instanceof Integer ? (Integer) value : null;
  }

  public static String[] getRestrictedTo(IClass clazz) {
    Object value = getValueOfTaggedValue(clazz, StereotypesManager.PROPERTY_RESTRICTED_TO);
    return value instanceof String ? ((String) value).split(" ") : null;
  }
}
