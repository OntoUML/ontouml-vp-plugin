package it.unibz.inf.ontouml.vp.model.uml2ontouml;

import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IDataType;
import com.vp.plugin.model.IModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.Literal;
import it.unibz.inf.ontouml.vp.model.ontouml.Property;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IClassTransformer {

  public static Class transform(IModelElement sourceElement) {
    if (!(sourceElement instanceof IClass) && !(sourceElement instanceof IDataType)) {
      return null;
    }

    IClassAdapter source = new IClassAdapter(sourceElement);
    Class target = new Class();

    IModelElementTransformer.transform(source.get(), target);
    ITaggedValueTransformer.transform(source.get(), target);
    IStereotypeTransformer.transform(source.get(), target);

    String name = source.getName();
    target.addName(name);

    boolean isAbstract = source.isAbstract();
    target.setAbstract(isAbstract);

    boolean isDerived = source.isDerived();
    target.setDerived(isDerived);

    Boolean isExtensional = source.isExtensional();
    target.setExtensional(isExtensional);

    Boolean isPowertype = source.isPowertype();
    target.setPowertype(isPowertype);

    Integer order = source.getOrder();
    target.setOrder(order);

    String[] restrictedTo = source.getRestrictedTo();
    if (restrictedTo != null) target.setRestrictedTo(restrictedTo);

    List<Property> attributes = transformAttributes(source);
    target.setAttributes(attributes);

    List<Literal> literals = transformLiterals(source);
    target.setLiterals(literals);

    return target;
  }

  public static List<Literal> transformLiterals(IClassAdapter clazz) {
    //    if (clazz.toEnumerationLiteralArray() == null) {
    //      return new ArrayList<>();
    //    }

    return Stream.of(clazz.toEnumerationLiteralArray())
        .map(IEnumerationLiteralTransformer::transform)
        .collect(Collectors.toList());
  }

  public static List<Property> transformAttributes(IClassAdapter clazz) {
    //    if (clazz.toAttributeArray() == null) {
    //      return new ArrayList<>();
    //    }

    return Stream.of(clazz.toAttributeArray())
        .map(IPropertyTransformer::transform)
        .collect(Collectors.toList());
  }
}
