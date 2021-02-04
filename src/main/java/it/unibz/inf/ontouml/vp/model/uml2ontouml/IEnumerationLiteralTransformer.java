package it.unibz.inf.ontouml.vp.model.uml2ontouml;

import com.vp.plugin.model.IEnumerationLiteral;
import it.unibz.inf.ontouml.vp.model.ontouml.Literal;

public class IEnumerationLiteralTransformer {

  public static Literal transform(IEnumerationLiteral source) {
    if (source == null) {
      return null;
    }

    Literal target = new Literal();
    IModelElementTransformer.transform(source, target);
    ITaggedValueTransformer.transform(source, target);

    return target;
  }
}
