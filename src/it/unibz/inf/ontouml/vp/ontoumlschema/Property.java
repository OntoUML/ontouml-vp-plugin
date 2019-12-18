package it.unibz.inf.ontouml.vp.ontoumlschema;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IRelationshipEnd;

public class Property implements StructuralElement {
	
	public static final String baseURI = "model:#/property/";
	
	private final IRelationshipEnd sourceModelElement;

	@SerializedName("@type")
	@Expose
	private final String type;

	@SerializedName("uri")
	@Expose
	private String URI;

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("url")
	@Expose
	private String URL;

	@SerializedName("propertyType")
	@Expose
	private String propertyType;

	@SerializedName("lowerbound")
	@Expose
	private String lowerbound;

	@SerializedName("upperbound")
	@Expose
	private String upperbound;

	public Property(IRelationshipEnd source) {
		this.sourceModelElement = source;
		this.type = "Property";
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
	public String getType() {
		return this.type;
	}

	@Override
	public String getURI() {
		return URI;
	}

	@Override
	public void setURI(String URI) {
		this.URI = Property.baseURI + URI;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public String getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}

	public String getLowerbound() {
		return lowerbound;
	}

	public void setLowerbound(String lowerbound) {
		this.lowerbound = lowerbound;
	}

	public String getUpperbound() {
		return upperbound;
	}

	public void setUpperbound(String upperbound) {
		this.upperbound = upperbound;
	}

}
