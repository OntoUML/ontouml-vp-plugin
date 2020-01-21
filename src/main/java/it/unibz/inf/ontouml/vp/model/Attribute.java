package it.unibz.inf.ontouml.vp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IAttribute;

public class Attribute implements ModelElement {
	
	private final IAttribute sourceModelElement;

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

	@SerializedName("cardinality")
	@Expose
	private String cardinality;

	public Attribute(IAttribute source) {
		this.sourceModelElement = source;
		this.type = ModelElement.TYPE_PROPERTY;
		setName(source.getName());
		setURI(ModelElement.getModelElementURI(source));
		setPropertyType(ModelElement.getModelElementURI(source.getTypeAsElement()));
		
		if(!((source.getMultiplicity()).equals(IAttribute.MULTIPLICITY_UNSPECIFIED)))
			this.cardinality = source.getMultiplicity();
	}
	
	@Override
	public String getId() {
		return getSourceModelElement().getId();
	}
	
	@Override
	public IAttribute getSourceModelElement() {
		return this.sourceModelElement;
	}

	@Override
	public String getOntoUMLType() {
		return this.type;
	}

	@Override
	public String getURI() {
		return URI;
	}

	@Override
	public void setURI(String URI) {
		this.URI = URI;
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

	public String getCardinality() {
		return cardinality;
	}

	public void setCardinality(String cardinality) {
		this.cardinality = cardinality;
	}

}
