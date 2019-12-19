package it.unibz.inf.ontouml.vp.ontoumlschema;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Model {

	public static final String baseURI = "https://ontouml.org/archive/";
	
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

	@SerializedName("authors")
	@Expose
	private List<String> authors;

	@SerializedName("structuralElements")
	@Expose
	private List<StructuralElement> structuralElements;

	public Model() {
		this.type = "Model";
		this.authors = new ArrayList<String>();
		this.structuralElements = new ArrayList<StructuralElement>();
	}

	public String getType() {
		return type;
	}

	public String getURI() {
		return URI;
	}

	public void setURI(String URI) {
		this.URI = Model.baseURI + URI;
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

	public void setURL(String URL) {
		this.URL = URL;
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

		if (this.authors.contains(name))
			this.authors.remove(name);
	}

	public List<StructuralElement> getStructuralElements() {
		return structuralElements;
	}

	public void setStructuralElements(List<StructuralElement> elementsList) {
		this.structuralElements = elementsList;
	}

	public void addStructuralElement(StructuralElement element) {
		this.structuralElements.add(element);
	}

	public boolean removeStructuralElement(StructuralElement element) {
		return this.structuralElements.remove(element);
	}

}
