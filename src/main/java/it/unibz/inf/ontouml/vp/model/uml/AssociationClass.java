package it.unibz.inf.ontouml.vp.model.uml;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IAssociationClass;
import it.unibz.inf.ontouml.vp.utils.Stereotype;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of ModelElement to handle IAssociationClass objects to be serialized as
 * ontouml-schema/Association
 *
 * @author Claudenir Fonseca
 * @author Tiago Prince Sales
 * @author Victor Viola
 */
public class AssociationClass implements ModelElement {

  private final IAssociationClass sourceModelElement;

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

  @SerializedName("properties")
  @Expose
  private List<Property> properties;

  @SerializedName("propertyAssignments")
  @Expose
  private JsonObject propertyAssignments;

  @SerializedName("stereotypes")
  @Expose
  private List<String> stereotypes;

  @SerializedName("isAbstract")
  @Expose
  private boolean isAbstract;

  @SerializedName("isDerived")
  @Expose
  private boolean isDerived;

  public AssociationClass(IAssociationClass source) {
    this.sourceModelElement = source;
    this.type = ModelElement.TYPE_ASSOCIATION_CLASS;
    this.id = source.getId();
    setName(source.getName());
    setDescription(source.getDescription());

    Property sourceEnd = new Property(source, source.getFrom());
    Property targetEnd = new Property(source, source.getTo());

    addStereotype(Stereotype.DERIVATION);

    addProperty(sourceEnd);
    addProperty(targetEnd);
  }

  @Override
  public String getId() {
    return getSourceModelElement().getId();
  }

  @Override
  public IAssociationClass getSourceModelElement() {
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
  }

  public JsonObject getPropertyAssignments() {
    return propertyAssignments;
  }

  public void setPropertyAssignments(JsonObject propertyAssignments) {
    this.propertyAssignments = propertyAssignments;
  }

  public List<String> getStereotypes() {
    return this.stereotypes;
  }

  public void setStereotypes(List<String> stereotypes) {
    this.stereotypes = stereotypes;
  }

  public String getStereotype(int position) {
    return this.stereotypes.get(position);
  }

  public void addStereotype(String name) {
    if (this.stereotypes == null) this.stereotypes = new ArrayList<String>();

    this.stereotypes.add(name);
  }

  public void removeStereotype(String name) {

    if (this.stereotypes.contains(name)) this.stereotypes.remove(name);
  }

  public List<Property> getProperties() {
    return properties;
  }

  public void setProperties(List<Property> properties) {
    this.properties = properties;
  }

  public Property getProperty(int position) {
    return this.properties.get(position);
  }

  public void addProperty(Property property) {
    if (this.properties == null) this.properties = new ArrayList<Property>();

    this.properties.add(property);
  }

  public void removeProperty(Property property) {

    if (this.properties.contains(property)) this.properties.remove(property);
  }

  public boolean isAbstract() {
    return this.isAbstract;
  }

  public void setAbstract(boolean isAbstract) {
    this.isAbstract = isAbstract;
  }

  public boolean isDerived() {
    return this.isDerived;
  }

  public void setDerived(boolean isDerived) {
    this.isDerived = isDerived;
  }
}
