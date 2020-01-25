package it.unibz.inf.ontouml.vp.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IGeneralization;

public class Generalization implements ModelElement {
	
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
		this.type = ModelElement.TYPE_GENERALIZATION_LINK;
		setName(source.getName());
		addGeneric(ModelElement.getModelElementURI(source.getFrom()));
		addSpecific(ModelElement.getModelElementURI(source.getTo()));
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
	public String getOntoUMLType() {
		return this.type;
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
		if(getTuple() == null) {
			setTuple(new ArrayList<String>(2));
		}
		
		this.tuple.add(str);
	}
	
	public void addGeneric(String genericURI) {
		if(getTuple() == null) {
			setTuple(new ArrayList<String>(2));
		}
		
		this.tuple.add(0, genericURI);
	}
	
	public void addSpecific(String specificURI) {
		if(getTuple() == null) {
			setTuple(new ArrayList<String>(2));
		}
		
		this.tuple.add(1, specificURI);
	}

}
