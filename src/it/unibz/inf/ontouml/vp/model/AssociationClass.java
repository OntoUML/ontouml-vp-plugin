package it.unibz.inf.ontouml.vp.model;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IAssociationClass;
import com.vp.plugin.model.IModelElement;

public class AssociationClass implements ModelElement {

	private final IAssociationClass sourceModelElement;

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
	private List<AssociationEnd> properties;

	public AssociationClass(IAssociationClass source) {
		this.sourceModelElement = source;
		this.type = ModelElement.TYPE_RELATION;
		setName(source.getName());
		setURI(ModelElement.getModelElementURI(source));
		
		final String[] stereotypes = source.toStereotypeArray();
		for (int i=0; stereotypes!=null && i<stereotypes.length; i++) {
			addStereotype(Stereotypes.getBaseURI(stereotypes[i]));
		}
		
		final IModelElement association = source.getFrom();
		final IModelElement _class = source.getTo();
		
		addProperty(new AssociationEnd(association.getName(),
				getURI() + "/assoaciation", 
				ModelElement.getModelElementURI(association)));
		addProperty(new AssociationEnd(_class.getName(),
				getURI() + "/class", 
				ModelElement.getModelElementURI(_class)));
	}

	@Override
	public String getId() {
		return getSourceModelElement().getId();
	}
	
	@Override
	public IAssociationClass getSourceModelElement() {
		return this.sourceModelElement;
	}
	
	@Override
	public String getOntoUMLType() {
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
		if(getStereotypes() == null) {
			setStereotypes(new LinkedList<String>());
		}
		
		this.stereotypes.add(name);
	}

	public List<AssociationEnd> getProperties() {
		return properties;
	}

	public void setProperties(List<AssociationEnd> properties) {
		this.properties = properties;
	}

	public AssociationEnd getProperty(int position) {
		return this.properties.get(position);
	}

	public void addProperty(AssociationEnd property) {
		if(getProperties() == null) {
			setProperties(new LinkedList<AssociationEnd>());
		}
		
		this.properties.add(property);
	}

}
