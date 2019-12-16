package it.unibz.inf.ontouml.vp.ontoumlschema;

import java.util.LinkedList;
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
	
	@SerializedName("properties")
    @Expose
	private List<String> properties;
	
	public Relation(){
		this.type = "Relation";
		this.properties = new LinkedList<String>();
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
	
	public List<String> getProperties() {
		return properties;
	}

	public void setProperties(List<String> properties) {
		this.properties = properties;
	}

}