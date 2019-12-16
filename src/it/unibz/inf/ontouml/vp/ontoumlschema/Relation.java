package it.unibz.inf.ontouml.vp.ontoumlschema;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Relation implements StructuralElement {
	
	@SerializedName("@type")
    @Expose
	private final String type;
	
	@SerializedName("uri")
    @Expose
	private String URI;
	
	@SerializedName("name")
    @Expose
	private String name;
	
	@SerializedName("stereotypes")
    @Expose
	private List<String> stereotypes;
	
	@SerializedName("properties")
    @Expose
	private List<Property> properties;
	
	public Relation(){
		this.type = "Relation";
		this.stereotypes = new ArrayList<String>();
		this.properties = new ArrayList<Property>();
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
		this.URI = URI;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
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
		
		if(this.stereotypes.contains(name))
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
		this.properties.add(property);
	}
	
	public void removeProperty(Property property) {
		
		if(this.properties.contains(property))
			this.properties.remove(property);
	}

}