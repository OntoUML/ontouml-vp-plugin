package it.unibz.inf.ontouml.vp.ontoumlschema;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Class implements StructuralElement {
	

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
	private Set<String> stereotypes;
	
	@SerializedName("properties")
    @Expose
	private Set<Property> properties;
	
	public Class(){
		this.type  = "Class";
		this.stereotypes = new HashSet<String>();
		this.properties = new HashSet<Property>();
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
	
	public Set<String> getStereotypes() {
		return stereotypes;
	}

	public void setStereotypes(Set<String> stereotypes) {
		this.stereotypes = stereotypes;
	}

	public Set<Property> getProperties() {
		return properties;
	}

	public void setProperties(Set<Property> properties) {
		this.properties = properties;
	}
	
	public void addStereotype(String stereotype) {
		this.stereotypes.add(stereotype);
	}

}
