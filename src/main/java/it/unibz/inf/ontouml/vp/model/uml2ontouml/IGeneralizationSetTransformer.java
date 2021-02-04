package it.unibz.inf.ontouml.vp.model.uml2ontouml;

import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IGeneralizationSet;
import com.vp.plugin.model.IModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.Generalization;
import it.unibz.inf.ontouml.vp.model.ontouml.GeneralizationSet;
import it.unibz.inf.ontouml.vp.model.ontouml.ModelElement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IGeneralizationSetTransformer {

  public static ModelElement transform(IModelElement sourceElement) {
    if (!(sourceElement instanceof IGeneralizationSet)) {
      return null;
    }

    IGeneralizationSet source = (IGeneralizationSet) sourceElement;
    GeneralizationSet target = new GeneralizationSet();

    IModelElementTransformer.transform(source, target);
    ITaggedValueTransformer.transform(source, target);

    boolean isDisjoint = source.isDisjoint();
    target.setDisjoint(isDisjoint);

    boolean isComplete = source.isCovering();
    target.setComplete(isComplete);

    Class categorizer = transformCategorizer(source);
    target.setCategorizer(categorizer);

    List<Generalization> generalizations = transformGeneralizations(source);
    target.setGeneralizations(generalizations);

    return target;
  }

  public static Class transformCategorizer(IGeneralizationSet genSet) {
    IModelElement powertype = genSet.getPowerType();

    if (!(powertype instanceof IClass)) {
      return null;
    }

    return (Class) ReferenceTransformer.transformStub(powertype);
  }

  public static List<Generalization> transformGeneralizations(IGeneralizationSet genSet) {
    IGeneralization[] generalizations = genSet.toGeneralizationArray();

    if (generalizations == null) {
      return new ArrayList<>();
    }

    return Stream.of(generalizations)
        .map(g -> (Generalization) ReferenceTransformer.transformStub(g))
        .collect(Collectors.toList());
  }
}
