package it.unibz.inf.ontouml.vp.model.uml2ontouml;

import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.Classifier;
import it.unibz.inf.ontouml.vp.model.ontouml.Generalization;
import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;

public class IGeneralizationTransformer {

  public static Generalization transform(IModelElement sourceElement) {
    if (!(sourceElement instanceof IGeneralization)) return null;

    IGeneralization source = (IGeneralization) sourceElement;
    Generalization target = new Generalization();

    //    System.out.println(
    //        "Master.getMetaModelElement().getName(): "
    //            + source.getMasterView().getMetaModelElement().getName());
    //    System.out.println(
    //        "Master.getDiagramUIModel().getName(): "
    //            + source.getMasterView().getDiagramUIModel().getName());
    //    System.out.println(
    //        "Master.getDiagramUIModel().getParentModel.getName(): "
    //            + source.getMasterView().getDiagramUIModel().getParentModel().getName());

    IModelElementTransformer.transform(source, target);
    ITaggedValueTransformer.transform(source, target);

    Classifier<?, ?> general = transformGeneral(source);
    target.setGeneral(general);

    Classifier<?, ?> specific = transformSpecific(source);
    target.setSpecific(specific);

    return target;
  }

  public static Classifier<?, ?> transformGeneral(IGeneralization generalization) {
    IModelElement general = generalization.getFrom();
    return (general != null) ? createClassifierStub(general) : null;
  }

  public static Classifier<?, ?> transformSpecific(IGeneralization generalization) {
    IModelElement specific = generalization.getTo();
    return (specific != null) ? createClassifierStub(specific) : null;
  }

  private static Classifier<?, ?> createClassifierStub(IModelElement classifier) {
    OntoumlElement targetType = ReferenceTransformer.transformStub(classifier);
    return targetType instanceof Classifier<?, ?> ? (Classifier<?, ?>) targetType : null;
  }
}
