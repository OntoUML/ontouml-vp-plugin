package it.unibz.inf.ontouml.vp.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;

public class Association implements ModelElement {

	private final IAssociation sourceModelElement;

	@SerializedName("type")
	@Expose
	private final String type;

	@SerializedName("id")
	@Expose
	private final String id;

	@SerializedName("name")
	@Expose
	private String name;

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

	public Association(IAssociation source) {
		this.sourceModelElement = source;

		this.type = ModelElement.TYPE_RELATION;
		this.id = source.getId();

		addProperty(new Property((IAssociationEnd) source.getFromEnd()));
		addProperty(new Property((IAssociationEnd) source.getToEnd()));

		String[] stereotypes = source.toStereotypeArray();
		this.stereotypes = stereotypes != null ? new ArrayList<String>() : null;

		for (int i = 0; stereotypes != null && i < stereotypes.length; i++) {
			addStereotype(stereotypes[i]);
		}

		ITaggedValueContainer lContainer = source.getTaggedValues();
		if (lContainer != null) {
			JsonObject obj = new JsonObject();
			ITaggedValue[] lTaggedValues = lContainer.toTaggedValueArray();

			for (int i = 0; lTaggedValues != null && i < lTaggedValues.length; i++) {
				switch (lTaggedValues[i].getType()) {
				case 1:
					JsonObject reference = new JsonObject();
					reference.addProperty("type", ModelElement.toOntoUMLSchemaType(lTaggedValues[i].getValueAsElement()));
					reference.addProperty("id", lTaggedValues[i].getValueAsElement().getId());
					obj.add(lTaggedValues[i].getName(), reference);
					break;
				case 5:
					obj.addProperty(lTaggedValues[i].getName(), Integer.parseInt((String) lTaggedValues[i].getValue()));
					break;
				case 6:
					obj.addProperty(lTaggedValues[i].getName(), Float.parseFloat((String) lTaggedValues[i].getValue()));
					break;
				case 7:
					obj.addProperty(lTaggedValues[i].getName(), Boolean.parseBoolean((String) lTaggedValues[i].getValue()));
					break;
				default:
					obj.addProperty(lTaggedValues[i].getName(), (String) lTaggedValues[i].getValueAsString());
				}
			}
			setPropertyAssignments(obj);
		}

		setName(source.getName());
		setAbstract(source.isAbstract());
		setDerived(source.isDerived());
	}

	@Override
	public String getId() {
		return getSourceModelElement().getId();
	}

	@Override
	public IAssociation getSourceModelElement() {
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
		this.name = name;
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
		this.stereotypes.add(name);
	}

	public void removeStereotype(String name) {

		if (this.stereotypes.contains(name))
			this.stereotypes.remove(name);
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
		if (this.properties == null)
			this.properties = new ArrayList<Property>();

		this.properties.add(property);
	}

	public void removeProperty(Property property) {

		if (this.properties.contains(property))
			this.properties.remove(property);
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