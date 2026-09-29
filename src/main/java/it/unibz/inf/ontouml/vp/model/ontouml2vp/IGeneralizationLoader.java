package it.unibz.inf.ontouml.vp.model.ontouml2vp;

import static it.unibz.inf.ontouml.vp.model.ontouml2vp.LoaderUtils.loadName;
import static it.unibz.inf.ontouml.vp.model.ontouml2vp.LoaderUtils.logElementCreation;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Generalization;
import it.unibz.inf.ontouml.vp.utils.ViewManagerUtils;

public class IGeneralizationLoader {

  static IProject vpProject = ApplicationManager.instance().getProjectManager().getProject();

  public static IGeneralization importElement(Generalization fromGeneralization) {

    logElementCreation(fromGeneralization);

    IGeneralization toGeneralization = getOrCreateGeneralization(fromGeneralization);
    fromGeneralization.setId(toGeneralization.getId());

    loadName(fromGeneralization, toGeneralization);

    loadGeneral(fromGeneralization, toGeneralization);
    loadSpecific(fromGeneralization, toGeneralization);

    ITaggedValueLoader.loadTaggedValues(fromGeneralization, toGeneralization);

    return toGeneralization;
  }

  private static void loadGeneral(Generalization fromGen, IGeneralization toGen) {
    fromGen
        .getGeneral()
        .map(general -> general.getId())
        .map(id -> vpProject.getModelElementById(id))
        .ifPresent(general -> setEnd(toGen, general, true));
  }

  private static void loadSpecific(Generalization fromGen, IGeneralization toGen) {
    fromGen
        .getSpecific()
        .map(specific -> specific.getId())
        .map(id -> vpProject.getModelElementById(id))
        .ifPresent(specific -> setEnd(toGen, specific, false));
  }

  /**
   * Points one end of a generalization at the given element.
   *
   * <p>Visual Paradigm validates every <code>setFrom</code>/<code>setTo</code> call and throws an
   * <code>IllegalArgumentException</code> ("Invalid connection...") when it rejects the resulting
   * connection. On VP 17 that happens even when an end is re-assigned the very element it already
   * holds, which is the usual case here because element ids survive the round trip to the server.
   * Since the whole import runs before any diagram is created, a single rejection used to abort
   * the run and leave the project half-updated. So the call is skipped when nothing would change,
   * and a rejection is reported and skipped instead of aborting.
   */
  private static void setEnd(IGeneralization toGen, IModelElement element, boolean isGeneral) {
    final IModelElement current = isGeneral ? toGen.getFrom() : toGen.getTo();

    if (current != null && current.getId().equals(element.getId())) {
      return;
    }

    try {
      if (isGeneral) {
        toGen.setFrom(element);
      } else {
        toGen.setTo(element);
      }
    } catch (RuntimeException e) {
      ViewManagerUtils.log(
          "Could not set the "
              + (isGeneral ? "general" : "specific")
              + " end of a generalization to \""
              + element.getName()
              + "\": "
              + e.getMessage());
      e.printStackTrace();
    }
  }

  private static IGeneralization getOrCreateGeneralization(Generalization fromGeneralization) {
    IModelElement toGeneralization = vpProject.getModelElementById(fromGeneralization.getId());

    if (toGeneralization instanceof IGeneralization) {
      System.out.println(
          "Generalization " + fromGeneralization.getId() + " exists! Let's update it!");
    } else {
      System.out.println(
          "Generalization " + fromGeneralization.getId() + " not found! Let's create it");
      toGeneralization = IModelElementFactory.instance().createGeneralization();
    }

    return (IGeneralization) toGeneralization;
  }
}
