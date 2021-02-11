package it.unibz.inf.ontouml.vp.model.ontouml2vp;

import static it.unibz.inf.ontouml.vp.model.ontouml2vp.LoaderUtils.getToClass;
import static it.unibz.inf.ontouml.vp.model.ontouml2vp.LoaderUtils.loadName;

import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IEnumerationLiteral;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Literal;

public class IEnumerationLoader {

  public static void importLiterals(Class fromClass) {
    if (!fromClass.isEnumeration()) return;

    IClass toClass = getToClass(fromClass);

    if (toClass == null) return;

    fromClass.getLiterals().forEach(l -> importLiteral(toClass, l));
  }

  public static void importLiteral(IClass toClass, Literal fromLiteral) {
    IEnumerationLiteral toLiteral = toClass.createEnumerationLiteral();
    loadName(fromLiteral, toLiteral);
    ITaggedValueLoader.loadTaggedValues(fromLiteral, toLiteral);
  }
}
