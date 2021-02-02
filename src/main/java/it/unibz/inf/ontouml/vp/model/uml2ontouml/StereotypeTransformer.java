package it.unibz.inf.ontouml.vp.model.uml2ontouml;

import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationClass;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;

public class StereotypeTransformer {

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
