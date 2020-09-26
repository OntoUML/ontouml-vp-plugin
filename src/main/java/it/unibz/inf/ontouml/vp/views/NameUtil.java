package it.unibz.inf.ontouml.vp.views;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.*;

public class NameUtil {

  public static String getDisplayName(Object obj) {
    if (obj == null) return "";

    if (obj instanceof String) return obj.toString();

    if (obj instanceof IDiagramUIModel) {
      IDiagramUIModel diagram = (IDiagramUIModel) obj;

      return diagram.getName() != null ? diagram.getName() : "Diagram";
    }

    if (obj instanceof IAttribute) {
      IAttribute attribute = (IAttribute) obj;

      String attributeName = attribute.getName() != null ? attribute.getName() : "";

      String attributeType = "";
      if (attribute.getType() != null) attributeType = attribute.getTypeAsString();

      return attributeName + " : " + attributeType;
    }

    if (obj instanceof IAssociation) {
      IAssociation association = (IAssociation) obj;
      String assocName = "";

      if (association.getName() != null && !association.getName().equals(""))
        assocName = association.getName() + " ";

      String nameFrom = association.getFrom() == null ? "" : association.getFrom().getName();
      String nameTo = association.getTo() == null ? "" : association.getTo().getName();

      return assocName + "(" + nameFrom + " -> " + nameTo + ")";
    }

    if (obj instanceof IAssociationEnd) {
      IAssociationEnd assocEnd = (IAssociationEnd) obj;

      String endName = assocEnd.getName() != null ? assocEnd.getName() : "";
      String typeName = assocEnd.getType() != null ? assocEnd.getTypeAsString() : "";

      return endName + " : " + typeName;
    }

    if (obj instanceof IGeneralization) {
      IGeneralization generalization = (IGeneralization) obj;

      String childTypeName = generalization.getTo() != null ? generalization.getTo().getName() : "";
      String parentTypeName =
          generalization.getFrom() != null ? generalization.getFrom().getName() : "";

      return "(" + childTypeName + " -> " + parentTypeName + ")";
    }

    if (obj instanceof IAssociationClass) {
      IAssociationClass assocClass = (IAssociationClass) obj;

      String nameFrom = assocClass.getFrom() != null ? assocClass.getFrom().getName() : "";
      String nameTo = assocClass.getTo() != null ? assocClass.getTo().getName() : "";

      return "(" + nameFrom + " -> " + nameTo + ")";
    }

    if (obj instanceof IModelElement) {
      IModelElement element = (IModelElement) obj;
      return element.getName() != null ? element.getName() : "ModelElement " + element.getId();
    }

    return "Element";
  }
}
