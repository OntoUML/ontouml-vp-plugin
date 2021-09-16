package it.unibz.inf.ontouml.vp.model.ontouml2vp;

import static it.unibz.inf.ontouml.vp.model.ontouml2vp.LoaderUtils.loadName;
import static it.unibz.inf.ontouml.vp.model.ontouml2vp.LoaderUtils.logElementCreation;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IAssociationClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Classifier;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Relation;
import it.unibz.inf.ontouml.vp.utils.StereotypesManager;

public class IAssociationClassLoader {

  static IProject vpProject = ApplicationManager.instance().getProjectManager().getProject();

  public static IAssociationClass importElement(Relation fromRelation) {
    logElementCreation(fromRelation);

    IAssociationClass toRelation = getOrCreateAssociation(fromRelation);
    fromRelation.setId(toRelation.getId());

    loadName(fromRelation, toRelation);

    loadSource(fromRelation, toRelation);
    loadTarget(fromRelation, toRelation);

    // Unable to process "isDerived" for IAssociationClass
    // Unable to process "isAbstract" for IAssociationClass
    // Unable to process "property ends" fully for IAssociationClass

    fromRelation
        .getStereotype()
        .ifPresent(
            stereotype -> {
              if (!"derivation".equals(stereotype)) {
                StereotypesManager.applyStereotype(toRelation, stereotype);
              }
            });

    ITaggedValueLoader.loadTaggedValues(fromRelation, toRelation);

    return toRelation;
  }

  private static void loadSource(Relation fromRelation, IAssociationClass toRelation) {
    Classifier<?, ?> fromSource = fromRelation.getSource();
    IModelElement toSource = vpProject.getModelElementById(fromSource.getId());

    if (toSource != null) {
      toRelation.setFrom(toSource);
    }
  }

  private static void loadTarget(Relation fromRelation, IAssociationClass toRelation) {
    Classifier<?, ?> fromTarget = fromRelation.getTarget();
    IModelElement toTarget = vpProject.getModelElementById(fromTarget.getId());

    if (toTarget != null) {
      toRelation.setTo(toTarget);
    }
  }

  private static IAssociationClass getOrCreateAssociation(Relation fromRelation) {
    IModelElement toRelation = vpProject.getModelElementById(fromRelation.getId());

    if (toRelation instanceof IAssociationClass) {
      System.out.println("Relation " + fromRelation.getId() + " exists! Let's update it!");
    } else {
      System.out.println("Relation " + fromRelation.getId() + " not found! Let's create it");
      toRelation = IModelElementFactory.instance().createAssociationClass();
    }

    return (IAssociationClass) toRelation;
  }
}
