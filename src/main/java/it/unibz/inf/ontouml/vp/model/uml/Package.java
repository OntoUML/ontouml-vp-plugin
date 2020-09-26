package it.unibz.inf.ontouml.vp.model.uml;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Implementation of ModelElement to handle IPackage objects to be serialized as
 * ontouml-schema/Package
 *
 * @author Claudenir Fonseca
 * @author Tiago Prince Sales
 * @author Victor Viola
 */
public class Package implements ModelElement {

  private final IPackage sourceModelElement;

  @SerializedName("type")
  @Expose
  private final String type;

  @SerializedName("id")
  @Expose
  private final String id;

  @SerializedName("name")
  @Expose
  private String name;

  @SerializedName("description")
  @Expose
  private String description;

  @SerializedName("propertyAssignments")
  @Expose
  private JsonObject propertyAssignments;

  @SerializedName("contents")
  @Expose
  private LinkedList<ModelElement> contents;

  public Package(IPackage source) {
    this.sourceModelElement = source;
    this.type = ModelElement.TYPE_PACKAGE;
    this.id = source.getId();
    setName(source.getName());
    setDescription(source.getDescription());

    final IModelElement[] children = source.toChildArray();
    for (int i = 0; children != null && i < children.length; i++) {
      final IModelElement child = children[i];

      switch (child.getModelType()) {
        case IModelElementFactory.MODEL_TYPE_PACKAGE:
          addElement(new Package((IPackage) child));
          break;
        case IModelElementFactory.MODEL_TYPE_MODEL:
          addElement(new Model((IModel) child));
          break;
        case IModelElementFactory.MODEL_TYPE_CLASS:
          addElement(new Class((IClass) child));
          break;
      }
    }

    setPropertyAssignments(ModelElement.transformPropertyAssignments(source));
  }

  public Package(IPackage source, HashSet<String> idElements) {
    this.sourceModelElement = source;
    this.type = ModelElement.TYPE_PACKAGE;
    this.id = source.getId();
    setName(source.getName());
    setDescription(source.getDescription());

    IModelElement[] childArray = source.toChildArray();

    if (childArray == null) return;

    for (int i = 0; i < childArray.length; i++) {
      if (idElements.contains(childArray[i].getId())) {
        addModelElement(childArray[i], idElements);
      }
    }

    setPropertyAssignments(ModelElement.transformPropertyAssignments(source));
  }

  @Override
  public IPackage getSourceModelElement() {
    return sourceModelElement;
  }

  @Override
  public String getId() {
    return getSourceModelElement().getId();
  }

  @Override
  public String getOntoUMLType() {
    return this.type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = ModelElement.safeGetString(name);
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = ModelElement.safeGetString(description);
    ;
  }

  public JsonObject getPropertyAssignments() {
    return propertyAssignments;
  }

  public void setPropertyAssignments(JsonObject propertyAssignments) {
    this.propertyAssignments = propertyAssignments;
  }

  public LinkedList<ModelElement> getContents() {
    return contents;
  }

  public void setContents(LinkedList<ModelElement> contents) {
    this.contents = contents;
  }

  public void addElement(ModelElement element) {
    if (getContents() == null) {
      setContents(new LinkedList<ModelElement>());
    }

    this.contents.add(element);
  }

  private void addModelElement(IModelElement projectElement, HashSet<String> idElements) {

    switch (projectElement.getModelType()) {
      case IModelElementFactory.MODEL_TYPE_PACKAGE:
        addElement(new Package((IPackage) projectElement, idElements));
        break;

      case IModelElementFactory.MODEL_TYPE_MODEL:
        addElement(new Model((IModel) projectElement, idElements));
        break;

      case IModelElementFactory.MODEL_TYPE_CLASS:
        addElement(new Class((IClass) projectElement, idElements));
        break;

      case IModelElementFactory.MODEL_TYPE_DATA_TYPE:
        addElement(new Class((IDataType) projectElement));
        break;

      case IModelElementFactory.MODEL_TYPE_GENERALIZATION:
        IGeneralization gen = (IGeneralization) projectElement;
        IModelElement fromElement = gen.getFrom();

        if (fromElement == null) break;

        String fromType = fromElement.getModelType();

        if (fromType == null) break;

        boolean isFromClass = fromType.equals(IModelElementFactory.MODEL_TYPE_CLASS);
        boolean isFromAssociation = fromType.equals(IModelElementFactory.MODEL_TYPE_ASSOCIATION);

        if (!isFromClass && !isFromAssociation) break;

        IModelElement toElement = gen.getTo();

        if (toElement == null) break;

        String toType = toElement.getModelType();

        if (toType == null) break;

        boolean isToClass = toType.equals(IModelElementFactory.MODEL_TYPE_CLASS);
        boolean isToAssociation = toType.equals(IModelElementFactory.MODEL_TYPE_ASSOCIATION);

        if (!isToClass && !isToAssociation) break;

        addElement(new Generalization((IGeneralization) projectElement));
        break;

      case IModelElementFactory.MODEL_TYPE_ASSOCIATION:
        addElement(new Association((IAssociation) projectElement, idElements));
        break;

      case IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET:
        addElement(new GeneralizationSet((IGeneralizationSet) projectElement, idElements));
        break;

      case IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS:
        addElement(new AssociationClass((IAssociationClass) projectElement));
    }
  }
}
