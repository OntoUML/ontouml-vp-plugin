package it.unibz.inf.ontouml.vp.model;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IModelElement;

public class Generalization implements StructuralElement {
	
	public static final String baseURI = "model:#/generalization/";
	
	private final IGeneralization sourceModelElement;

	@SerializedName("@type")
	@Expose
	private final String type;

	@SerializedName("uri")
	@Expose
	private String URI;

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("tuple")
	@Expose
	private List<String> tuple;

	public Generalization(IGeneralization source) {
		this.sourceModelElement = source;
		this.type = StructuralElement.TYPE_GENERALIZATION_LINK;
		this.name = source.getName();
		this.URI = StructuralElement.getModelElementURI(source);
				
		// Add's first the superclass URI and then subclass URI. Here the order is mandatory.
		this.tuple = new LinkedList<String>();
		tuple.add(StructuralElement.getModelElementURI(source.getFrom()));
		tuple.add(StructuralElement.getModelElementURI(source.getTo()));
	}
	
	@Override
	public String getId() {
		return getSourceModelElement() != null ? getSourceModelElement().getId() : null;
	}
	
	@Override
	public IGeneralization getSourceModelElement() {
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
		this.URI = Generalization.baseURI + URI;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getTuple() {
		return tuple;
	}

	public void setTuple(List<String> tuple) {
		this.tuple = tuple;
	}

	public void addTuple(String str) {
		this.tuple.add(str);
	}

}
