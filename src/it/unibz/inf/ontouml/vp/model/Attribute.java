package it.unibz.inf.ontouml.vp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IRelationshipEnd;

public class Attribute implements ModelElement {
	
	public static final String baseURI = "model:#/property/";
	
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

	@SerializedName("lowerbound")
	@Expose
	private int lowerbound;

	@SerializedName("upperbound")
	@Expose
	private String upperbound;

	public Attribute(IAttribute source) {
		this.sourceModelElement = source;
		this.type = ModelElement.TYPE_PROPERTY;
		this.name = source.getName();
		this.URI = ModelElement.getModelElementURI(source);
		this.propertyType = ModelElement.getModelElementURI(source.getTypeAsElement());
		
		// TODO Update cardinalities
		String multiplicity = source.getMultiplicity();
		if(multiplicity.equals(IAssociationEnd.MULTIPLICITY_UNSPECIFIED)) {
			this.lowerbound = 0;
			this.upperbound = "*";
		}
		else if (multiplicity.contains("..")) {
			String min = multiplicity.substring(0, multiplicity.indexOf(".."));
			String max = multiplicity.substring(multiplicity.indexOf("..") + 2, multiplicity.length() - 1);
			this.lowerbound = Integer.parseInt(min);
			this.upperbound = "*";
//			TODO OntoUML Schema accepts either a number or the string "*". Discover how to do serialize that with GSON.
//			this.upperbound = max.equals("*") ? max : Integer.parseInt(max);
		}
		else {			
			this.lowerbound = multiplicity.equals("*") ? 0 : Integer.parseInt(multiplicity);
			this.upperbound = "*";
//			TODO OntoUML Schema accepts either a number or the string "*". Discover how to do serialize that with GSON.
//			this.upperbound = multiplicity.equals("*") ? multiplicity : Integer.parseInt(multiplicity);
		}
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
		this.URI = Attribute.baseURI + URI;
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

	public int getLowerbound() {
		return lowerbound;
	}

	public void setLowerbound(int lowerbound) {
		this.lowerbound = lowerbound;
	}

	public String getUpperbound() {
		return upperbound;
	}

	public void setUpperbound(String upperbound) {
		this.upperbound = upperbound;
	}

}
