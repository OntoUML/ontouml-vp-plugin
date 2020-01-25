package it.unibz.inf.ontouml.vp.model;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IGeneralizationSet;
import com.vp.plugin.model.IModelElement;

public class GeneralizationSet implements ModelElement {

	private final IGeneralizationSet sourceModelElement;

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

	@SerializedName("isDisjoint")
	@Expose
	private boolean isDisjoint;

	@SerializedName("isComplete")
	@Expose
	private boolean isComplete;

	@SerializedName("tuple")
	@Expose
	private List<String> tuple;
	
	@SerializedName("categorizer")
	@Expose
	private String categorizer;

	public GeneralizationSet(IGeneralizationSet source) {
		this.sourceModelElement = source;
		this.type = ModelElement.TYPE_GENERALIZATION_SET;
		setName(source.getName());
		
		setComplete(source.isCovering());
		setDisjoint(source.isDisjoint());
		setCategorizer(ModelElement.getModelElementURI(source.getPowerType()));
		
		final IGeneralization[] generalizations = source.toGeneralizationArray();
		for (int i = 0; generalizations != null && i < generalizations.length; i++) {
			addTuple(ModelElement.getModelElementURI(generalizations[i]));
		}
	}
	
	@Override
	public String getId() {
		return getSourceModelElement().getId();
	}
	
	@Override
	public IModelElement getSourceModelElement() {
		return this.sourceModelElement;
	}

	@Override
	public String getOntoUMLType() {
		return type;
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

	public boolean isDisjoint() {
		return isDisjoint;
	}

	public void setDisjoint(boolean isDisjoint) {
		this.isDisjoint = isDisjoint;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	public List<String> getTuple() {
		return tuple;
	}

	public void setTuple(List<String> tuple) {
		this.tuple = tuple;
	}

	public void addTuple(String str) {
		if(getTuple() == null) {
			setTuple(new LinkedList<String>());
		}
		this.tuple.add(str);
	}
	
	public String getCategorizer() {
		return this.categorizer;
	}

	public void setCategorizer(String categorizerURI) {
		this.categorizer = categorizerURI;
	}

}
