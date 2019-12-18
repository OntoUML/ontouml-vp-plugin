package it.unibz.inf.ontouml.vp.ontoumlschema;

import java.util.LinkedList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IPackage;

public class Package implements StructuralElement {

	public static final String baseURI = "model:#/package/";
	
	private final IPackage sourceModelElement;
	
	@SerializedName("@type")
	@Expose
	private final String type;

	@SerializedName("uri")
	@Expose
	private String URI;

	@SerializedName("url")
	@Expose
	private String URL;

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("structuralElements")
	@Expose
	private LinkedList<StructuralElement> structuralElements;

	public Package(IPackage source) {
		this.sourceModelElement = source;
		this.type = StructuralElement.TYPE_PACKAGE;
		this.name = source.getName();
		this.URI = StructuralElement.getModelElementURI(source);
		
		this.structuralElements = new LinkedList<StructuralElement>();
	}
	
	public Package(String name, String URI) {
		this.sourceModelElement = null;
		this.type = StructuralElement.TYPE_PACKAGE;
		this.name = name;
		this.URI = URI;
		
		this.structuralElements = new LinkedList<StructuralElement>();
	}

	@Override
	public IPackage getSourceModelElement() {
		return sourceModelElement;
	}
	
	@Override
	public String getId() {
		return getSourceModelElement().getId();
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
		this.URI = Package.baseURI + URI;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String URL) {
		this.URL = URL;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LinkedList<StructuralElement> getStructuralElements() {
		return structuralElements;
	}

	public void setStructuralElements(LinkedList<StructuralElement> structuralElements) {
		this.structuralElements = structuralElements;
	}

	public void addStructuralElement(StructuralElement element) {
		this.structuralElements.add(element);
	}

}
