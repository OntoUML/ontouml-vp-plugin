package it.unibz.inf.ontouml.vp.model.uml2ontouml;

import com.vp.plugin.model.*;
import it.unibz.inf.ontouml.vp.model.ontouml.Decoratable;

public class IStereotypeTransformer {

  public static void transform(IModelElement source, Decoratable<?> target) {
    String stereotype = null;

    if (source instanceof IClass
        || source instanceof IAssociationClass
        || source instanceof IAssociation
        || source instanceof IAttribute
        || source instanceof IAssociationEnd) stereotype = getFirstStereotype(source);

    if (source instanceof IDataType) stereotype = "datatype";

    //    if (source instanceof IClass) {
    //      stereotype = getFirstStereotype((IClass) source);
    //    } else if (source instanceof IAssociationClass) {
    //      stereotype = getFirstStereotype((IAssociationClass) source);
    //    } else if (source instanceof IAssociation) {
    //      stereotype = getFirstStereotype((IAssociation) source);
    //    } else if (source instanceof IAttribute) {
    //      stereotype = getFirstStereotype((IAttribute) source);
    //    } else if (source instanceof IAssociationEnd) {
    //      stereotype = getFirstStereotype((IAttribute) source);
    //    }

    target.setStereotype(stereotype);
  }

  public static String getFirstStereotype(IModelElement element) {
    IStereotype[] stereotypes = element.toStereotypeModelArray();
    return (stereotypes != null && stereotypes.length > 0) ? stereotypes[0].getName() : null;
  }

  public static String getFirstStereotype(IClass clazz) {
    return getFirst(clazz.toStereotypeArray());
  }

  public static String getFirstStereotype(IAssociationClass clazz) {
    return getFirst(clazz.toStereotypeArray());
  }

  public static String getFirstStereotype(IAssociation association) {
    return getFirst(association.toStereotypeArray());
  }

  public static String getFirstStereotype(IAttribute attribute) {
    return getFirst(attribute.toStereotypeArray());
  }

  private static String getFirst(String[] array) {
    return (array.length > 0) ? array[0] : null;
  }
}
