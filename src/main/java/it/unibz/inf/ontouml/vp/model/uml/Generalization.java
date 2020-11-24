package it.unibz.inf.ontouml.vp.model.uml;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IModelElement;

/**
 * Implementation of ModelElement to handle IGeneralization objects to be serialized as
 * ontouml-schema/Generalization
 *
 * @author Claudenir Fonseca
 * @author Tiago Prince Sales
 * @author Victor Viola
 */
public class Generalization implements ModelElement {

  private final IGeneralization sourceModelElement;

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

  @SerializedName("general")
  @Expose
  private Reference general;

  @SerializedName("specific")
  @Expose
  private Reference specific;

  public Generalization(IGeneralization source) {
    this.sourceModelElement = source;

    this.type = ModelElement.TYPE_GENERALIZATION;
    this.id = source.getId();
    setName(source.getName());
    setDescription(source.getDescription());

    setGeneral(new Reference(source.getFrom()));
    setSpecific(new Reference(source.getTo()));

    setPropertyAssignments(ModelElement.transformPropertyAssignments(source));
  }

  @Override
  public String getId() {
    return getSourceModelElement() != null ? getSourceModelElement().getId() : null;
  }

  @Override
  public IGeneralization getSourceModelElement() {
    return this.sourceModelElement;
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

  public Reference getGeneral() {
    return general;
  }

  public void setGeneral(Reference general) {
    this.general = general;
  }

  public Reference getSpecific() {
    return specific;
  }

  public void setSpecific(Reference specific) {
    this.specific = specific;
  }

  public static IModelElement getGeneral(IGeneralization generalization) {
    return generalization.getFrom();
  }

  public static IModelElement getSpecific(IGeneralization generalization) {
    return generalization.getTo();
  }
}
