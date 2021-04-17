package it.unibz.inf.ontouml.vp.model.ontouml2vp;

import static it.unibz.inf.ontouml.vp.model.ontouml2vp.LoaderUtils.logElementCreation;
import static it.unibz.inf.ontouml.vp.model.uml.Class.*;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Class;
import it.unibz.inf.ontouml.vp.utils.StereotypesManager;
import java.util.stream.Collectors;

public class IClassLoader {

  static IProject vpProject = ApplicationManager.instance().getProjectManager().getProject();

  public static IClass importElement(Class fromClass) {

    logElementCreation(fromClass);

    IClass toClass = getOrCreateClass(fromClass);
    fromClass.setId(toClass.getId());

    String name = fromClass.getFirstName().orElse("Unnamed Class");
    toClass.setName(name);

    boolean isAbstract = fromClass.isAbstract();
    toClass.setAbstract(isAbstract);

    boolean isDerived = fromClass.isDerived();
    setDerived(toClass, isDerived);

    fromClass.isExtensional().ifPresent(aBoolean -> setIsExtensional(toClass, aBoolean));
    fromClass.isPowertype().ifPresent(aBoolean -> setIsPowertype(toClass, aBoolean));
    fromClass.getOrderAsString().ifPresent(aString -> setOrder(toClass, aString));

    String restrictedTo = getRestrictedToString(fromClass);
    setRestrictedTo(toClass, restrictedTo);

    fromClass
        .getStereotype()
        .ifPresent(stereotype -> StereotypesManager.applyStereotype(toClass, stereotype));

    ITaggedValueLoader.loadTaggedValues(fromClass, toClass);

    return toClass;
  }

  private static void setDerived(IClass clazz, boolean isDerived) {
    if (isDerived) clazz.setName("/" + clazz.getName());
  }

  private static String getRestrictedToString(Class clazz) {
    return clazz.getRestrictedTo().stream()
        .map(nature -> nature.name)
        .collect(Collectors.joining(" "));
  }

  private static IClass getOrCreateClass(Class fromClass) {
    IModelElement toClass = vpProject.getModelElementById(fromClass.getId());

    if (toClass instanceof IClass) {
      System.out.println("Class " + fromClass.getId() + " exists! Let's update it!");
    } else {
      System.out.println("Class " + fromClass.getId() + " not found! Let's create it");
      toClass = IModelElementFactory.instance().createClass();
    }

    return (IClass) toClass;
  }
}
