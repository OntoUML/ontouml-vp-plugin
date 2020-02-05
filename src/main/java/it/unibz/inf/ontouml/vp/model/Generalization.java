package it.unibz.inf.ontouml.vp.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;

/**
 * 
 * Implementation of ModelElement to handle IGeneralization objects
 * to be serialized as ontouml-schema/Generalization
 * 
 * @author Claudenir Fonseca
 * @author Tiago Prince Sales
 * @author Victor Viola
 *
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
		
		setGeneral(new Reference(source.getFrom().getModelType(), source.getFrom().getId()));
		setSpecific(new Reference(source.getTo().getModelType(), source.getTo().getId()));

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
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if (description.equals("")) {
			this.description = null;
		} else {
			this.description = description;
		}
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
}
