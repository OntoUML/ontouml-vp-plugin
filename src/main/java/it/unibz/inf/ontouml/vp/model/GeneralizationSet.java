package it.unibz.inf.ontouml.vp.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IGeneralizationSet;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;

/**
 * 
 * Implementation of ModelElement to handle IGeneralizationSet objects to be
 * serialized as ontouml-schema/GeneralizationSet
 * 
 * @author Claudenir Fonseca
 * @author Tiago Prince Sales
 * @author Victor Viola
 *
 */

public class GeneralizationSet implements ModelElement {

	private final IGeneralizationSet sourceModelElement;

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

	@SerializedName("categorizer")
	@Expose
	private Reference categorizer;

	@SerializedName("generalizations")
	@Expose
	private List<Reference> generalizations;

	@SerializedName("isDisjoint")
	@Expose
	private boolean isDisjoint;

	@SerializedName("isComplete")
	@Expose
	private boolean isComplete;

	public GeneralizationSet(IGeneralizationSet source) {
		this.sourceModelElement = source;

		this.type = ModelElement.TYPE_GENERALIZATION_SET;
		this.id = source.getId();
		setName(source.getName());
		setDescription(source.getDescription());

		setComplete(source.isCovering());
		setDisjoint(source.isDisjoint());

		if (source.getPowerType() != null)
			setCategorizer(new Reference(source.getPowerType()));

		final IGeneralization[] generalizations = source.toGeneralizationArray();
		for (int i = 0; generalizations != null && i < generalizations.length; i++)
			addGeneralization(new Reference(generalizations[i]));

		ITaggedValueContainer lContainer = source.getTaggedValues();
		if (lContainer != null) {
			JsonObject obj = new JsonObject();
			ITaggedValue[] lTaggedValues = lContainer.toTaggedValueArray();

			for (int i = 0; lTaggedValues != null && i < lTaggedValues.length; i++) {
				switch (lTaggedValues[i].getType()) {
				case 1:
					JsonObject referenceTag = new JsonObject();

					if (lTaggedValues[i].getValueAsElement() != null) {
						referenceTag.addProperty("type", ModelElement.toOntoUMLSchemaType(lTaggedValues[i].getValueAsElement()));
						referenceTag.addProperty("id", lTaggedValues[i].getValueAsElement().getId());
					} else {
						referenceTag.add("type", null);
						referenceTag.add("id", null);
					}
					obj.add(lTaggedValues[i].getName(), referenceTag);
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
		return getSourceModelElement().getId();
	}

	@Override
	public IModelElement getSourceModelElement() {
		return this.sourceModelElement;
	}

	@Override
	public String getOntoUMLType() {
		return type;
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

	public boolean isDisjoint() {
		return isDisjoint;
	}

	public void setDisjoint(boolean isDisjoint) {
		this.isDisjoint = isDisjoint;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	public Reference getCategorizer() {
		return this.categorizer;
	}

	public void setCategorizer(Reference categorizer) {
		this.categorizer = categorizer;
	}

	public List<Reference> getGeneralizations() {
		return generalizations;
	}

	public void setGeneralizations(List<Reference> generalizations) {
		this.generalizations = generalizations;
	}

	public void addGeneralization(Reference ref) {
		if (this.generalizations == null)
			this.generalizations = new ArrayList<Reference>();

		this.generalizations.add(ref);
	}

	public void removeGeneralization(Reference ref) {
		if (this.generalizations != null && this.generalizations.contains(ref))
			this.generalizations.remove(ref);
	}

}
