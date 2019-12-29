package it.unibz.inf.ontouml.vp.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IRelationship;
import com.vp.plugin.model.IRelationshipEnd;

public class Association implements ModelElement {
	
	public static final String baseURI = "model:#/relation/";
	
	private final IAssociation sourceModelElement;

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

	public Association(IAssociation source) {
		this.sourceModelElement = source;
		this.type = ModelElement.TYPE_RELATION;
		this.name = source.getName();
		this.URI = ModelElement.getModelElementURI(source);
		
		String[] stereotypes = source.toStereotypeArray();
		this.stereotypes = stereotypes!=null ? new ArrayList<String>() : null;
		
		for (int i=0; stereotypes!=null && i<stereotypes.length; i++) {
			this.stereotypes.add(Stereotypes.getBaseURI(stereotypes[i]));
		}
		
		this.properties = new ArrayList<AssociationEnd>();
		this.properties.add(new AssociationEnd((IAssociationEnd) source.getFromEnd()));
		this.properties.add(new AssociationEnd((IAssociationEnd) source.getToEnd()));	
	}

	@Override
	public String getId() {
		return getSourceModelElement().getId();
	}
	
	@Override
	public IAssociation getSourceModelElement() {
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
		this.URI = Association.baseURI + URI;
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
		this.stereotypes.add(name);
	}

	public void removeStereotype(String name) {

		if (this.stereotypes.contains(name))
			this.stereotypes.remove(name);
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
		this.properties.add(property);
	}

	public void removeProperty(AssociationEnd property) {

		if (this.properties.contains(property))
			this.properties.remove(property);
	}

}