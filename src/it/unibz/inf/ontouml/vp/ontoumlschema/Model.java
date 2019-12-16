package it.unibz.inf.ontouml.vp.ontoumlschema;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Model {
	
	@SerializedName("name")
	@Expose
	private String name;
	
	@SerializedName("url")
	@Expose
	private String URL;

	@SerializedName("@type")
	@Expose
	private final String type;

	@SerializedName("uri")
	@Expose
	private String URI;

	@SerializedName("authors")
	@Expose
	private List<String> authors;

	@SerializedName("structuralElements")
	@Expose
	private List<Package> structuralElements;

	public Model() {
		this.type = "Model";
		this.authors = new ArrayList<String>();
		this.structuralElements = new ArrayList<Package>();
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

	public String getURI() {
		return URI;
	}

	public void setURI(String URI) {
		this.URI = URI;
	}

	public List<String> getAuthors() {
		return this.authors;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	public String getAuthor(int position) {
		return this.authors.get(position);
	}
	
	public void addAuthor(String name) {
		this.authors.add(name);
	}
	
	public void removeAuthor(String name) {
		
		if(this.authors.contains(name))
			this.authors.remove(name);
	}
	
	public List<Package> getStructuralElements() {
		return structuralElements;
	}

	public void setStructuralElements(List<Package> newPackage) {
		this.structuralElements = newPackage;
	}
	
	public void addStructuralElement(Package newPackage) {
		this.structuralElements.add(newPackage);
	}
	
	public void removeStructuralElement(Package newPackage) {
		
		if(this.structuralElements.contains(newPackage))
			this.structuralElements.remove(newPackage);
	}

}
