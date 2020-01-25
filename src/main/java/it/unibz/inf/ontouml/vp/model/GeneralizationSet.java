package it.unibz.inf.ontouml.vp.model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IGeneralizationSet;
import com.vp.plugin.model.IModelElement;

public class GeneralizationSet implements ModelElement {

	private final IGeneralizationSet sourceModelElement;

	@SerializedName("type")
	@Expose
	private final String type;

	@SerializedName("id")
	@Expose
	private final String id;

	@SerializedName("name")
	@Expose
	private String name;
	
	@SerializedName("categorizer")
	@Expose
	private Reference categorizer;

	@SerializedName("generalizations")
	@Expose
	private List<Reference> generalizations;

	@SerializedName("isDisjoint")
	@Expose
	private boolean isDisjoint;

	@SerializedName("isComplete")
	@Expose
	private boolean isComplete;
	
	public GeneralizationSet(IGeneralizationSet source) {
		this.sourceModelElement = source;
		
		this.type = ModelElement.TYPE_GENERALIZATION_SET;
		this.id = source.getId();
		setName(source.getName());
		
		setComplete(source.isCovering());
		setDisjoint(source.isDisjoint());
		
		if(source.getPowerType()!=null)
			setCategorizer(new Reference(source.getPowerType().getName(), source.getPowerType().getId()));
		
		final IGeneralization[] generalizations = source.toGeneralizationArray();
		for (int i = 0; generalizations != null && i < generalizations.length; i++) {
			addGeneralization(new Reference(generalizations[i].getName(), generalizations[i].getId()));
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
	
	public Reference getCategorizer() {
		return this.categorizer;
	}

	public void setCategorizer(Reference categorizer) {
		this.categorizer = categorizer;
	}
	
	public List<Reference> getGeneralizations() {
		return generalizations;
	}

	public void setGeneralizations(List<Reference> generalizations) {
		this.generalizations = generalizations;
	}
	
	public void addGeneralization(Reference ref){
		if(this.generalizations == null)
			this.generalizations = new ArrayList<Reference>();
		
		this.generalizations.add(ref);
	}
	
	public void removeGeneralization(Reference ref){
		if(this.generalizations != null && this.generalizations.contains(ref))
			this.generalizations.remove(ref);
	}

}
