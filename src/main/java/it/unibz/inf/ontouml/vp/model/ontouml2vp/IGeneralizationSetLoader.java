package it.unibz.inf.ontouml.vp.model.ontouml2vp;

import static it.unibz.inf.ontouml.vp.model.ontouml2vp.LoaderUtils.loadName;
import static it.unibz.inf.ontouml.vp.model.ontouml2vp.LoaderUtils.logElementCreation;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import it.unibz.inf.ontouml.vp.model.ontouml.model.GeneralizationSet;

public class IGeneralizationSetLoader {

  static IProject vpProject = ApplicationManager.instance().getProjectManager().getProject();

  public static IGeneralizationSet importElement(GeneralizationSet fromGs) {

    logElementCreation(fromGs);

    IGeneralizationSet toGs = getOrCreateGeneralizationSet(fromGs);
    fromGs.setId(toGs.getId());

    loadName(fromGs, toGs);

    toGs.setCovering(fromGs.isComplete());
    toGs.setDisjoint(fromGs.isDisjoint());

    loadPowertype(fromGs, toGs);
    loadGeneralizations(fromGs, toGs);

    ITaggedValueLoader.loadTaggedValues(fromGs, toGs);

    return toGs;
  }

  private static void loadPowertype(GeneralizationSet fromGs, IGeneralizationSet toGs) {
    fromGs
        .getCategorizer()
        .map(cat -> cat.getId())
        .map(id -> vpProject.getModelElementById(id))
        .filter(elem -> elem instanceof IClass)
        .ifPresent(clazz -> toGs.setPowerType(clazz));
  }

  private static void loadGeneralizations(GeneralizationSet fromGs, IGeneralizationSet toGs) {
    fromGs.getGeneralizations().stream()
        .map(gen -> gen.getId())
        .map(id -> vpProject.getModelElementById(id))
        .filter(elem -> elem instanceof IGeneralization)
        .map(elem -> (IGeneralization) elem)
        .forEach(gen -> toGs.addGeneralization(gen));
  }

  private static IGeneralizationSet getOrCreateGeneralizationSet(GeneralizationSet fromGs) {
    IModelElement toGs = vpProject.getModelElementById(fromGs.getId());

    if (toGs instanceof IGeneralizationSet) {
      System.out.println("GeneralizationSet " + fromGs.getId() + " exists! Let's update it!");
    } else {
      System.out.println("GeneralizationSet " + fromGs.getId() + " not found! Let's create it");
      toGs = IModelElementFactory.instance().createGeneralizationSet();
    }

    return (IGeneralizationSet) toGs;
  }
}
