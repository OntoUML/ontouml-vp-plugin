package it.unibz.inf.ontouml.vp.model.ontouml2vp;

import static it.unibz.inf.ontouml.vp.model.ontouml2vp.LoaderUtils.loadName;
import static it.unibz.inf.ontouml.vp.model.ontouml2vp.LoaderUtils.logElementCreation;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IDataType;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Class;
import java.util.Optional;

public class IDataTypeLoader {

  static IProject vpProject = ApplicationManager.instance().getProjectManager().getProject();

  public static IDataType importElement(Class fromPrimitive) {
    logElementCreation(fromPrimitive);

    IDataType toPrimitive = getOrCreateDataType(fromPrimitive);
    fromPrimitive.setId(toPrimitive.getId());

    loadName(fromPrimitive, toPrimitive);

    return toPrimitive;
  }

  private static IDataType getOrCreateDataType(Class fromClass) {
    IModelElement toDatatype = vpProject.getModelElementById(fromClass.getId());

    if (toDatatype instanceof IDataType) {
      System.out.println(
          "Datatype "
              + fromClass.getFirstName().orElse("")
              + " ("
              + fromClass.getId()
              + ") exists. Let's update it!");
    } else {

      Optional<IDataType> toPrimitiveDatatype =
          LoaderUtils.getAllDatatypes().stream()
              .filter(d -> d.getName().equals(fromClass.getFirstName().orElse("")))
              .findFirst();

      if (toPrimitiveDatatype.isEmpty()) {
        toDatatype = IModelElementFactory.instance().createDataType();
        System.out.println(
            "Datatype "
                + fromClass.getFirstName().orElse("")
                + " ("
                + fromClass.getId()
                + ") not found. Let's create it!");
      } else {
        toDatatype = toPrimitiveDatatype.get();
        System.out.println(
            "Datatype "
                + fromClass.getFirstName().orElse("")
                + " ("
                + fromClass.getId()
                + ") exists. Let's update it!");
      }
    }

    return (IDataType) toDatatype;
  }
}
