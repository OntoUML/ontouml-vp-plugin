package it.unibz.inf.ontouml.vp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IAssociationEnd;

public class AssociationEnd implements ModelElement {
	
	private final IAssociationEnd sourceModelElement;

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

	public AssociationEnd(String name, String URI, String propertyTypeURI) {
		this.sourceModelElement = null;
		this.type = ModelElement.TYPE_PROPERTY;
		setName(name);
		setURI(URI);
		setPropertyType(propertyTypeURI);
	}
	
	public AssociationEnd(IAssociationEnd source) {
		this.sourceModelElement = source;
		this.type = ModelElement.TYPE_PROPERTY;
		setName(source.getName());
		setURI(ModelElement.getModelElementURI(source));
		setPropertyType(ModelElement.getModelElementURI(source.getTypeAsElement()));
		
		String multiplicity = source.getMultiplicity();
		
		if(multiplicity.equals(IAssociationEnd.MULTIPLICITY_UNSPECIFIED)) {
			this.lowerbound = "0";
			this.upperbound = "*";
		}
		else if (multiplicity.matches("^\\w+\\.{2}[\\w+|\\*]$")) {
			this.lowerbound = multiplicity.substring(0, multiplicity.indexOf(".."));
			this.upperbound = multiplicity.substring(multiplicity.indexOf("..") + 2, multiplicity.length());
		}
		else {			
			this.lowerbound = multiplicity;
			this.upperbound = "*";
		}
	}
	
	@Override
	public String getId() {
		return getSourceModelElement().getId();
	}
	
	@Override
	public IAssociationEnd getSourceModelElement() {
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
