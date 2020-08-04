 package it.unibz.inf.ontouml.vp.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IEnumerationLiteral;

public class Literal implements ModelElement {

	private final IEnumerationLiteral sourceModelElement;

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
	
	public Literal(IEnumerationLiteral source) {
		this.sourceModelElement = source;
		this.type = ModelElement.TYPE_LITERAL;
		this.id = source.getId();
		setName(source.getName());
		setDescription(source.getDescription());
		setPropertyAssignments(ModelElement.transformPropertyAssignments(source));
	}

	@Override
	public IEnumerationLiteral getSourceModelElement() {
		return this.sourceModelElement;
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
		this.description = ModelElement.safeGetString(description);;
	}
	
	public JsonObject getPropertyAssignments() {
		return propertyAssignments;
	}

	public void setPropertyAssignments(JsonObject propertyAssignments) {
		this.propertyAssignments = propertyAssignments;
	}

}