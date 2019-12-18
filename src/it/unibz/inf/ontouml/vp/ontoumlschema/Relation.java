package it.unibz.inf.ontouml.vp.ontoumlschema;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IRelationship;

public class Relation implements StructuralElement {
	
	public static final String baseURI = "model:#/relation/";
	
	private final IRelationship sourceModelElement;

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
	private List<Property> properties;

	public Relation(IRelationship source) {
		this.sourceModelElement = source;
		this.type = StructuralElement.TYPE_RELATION;
		this.name = source.getName();
		this.URI = StructuralElement.getModelElementURI(source);
		
		if(source instanceof IAssociation) {
			String[] stereotypes = ((IAssociation) source).toStereotypeArray();
			this.stereotypes = new ArrayList<String>();
			
			for (int i=0; stereotypes!=null && i<stereotypes.length; i++) {
				this.stereotypes.add(Stereotypes.getBaseURI(stereotypes[i]));
			}			
		}
		
//		this.properties = new ArrayList<Property>();
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
	public String getType() {
		return this.type;
	}

	@Override
	public String getURI() {
		return URI;
	}

	@Override
	public void setURI(String URI) {
		this.URI = Relation.baseURI + URI;
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

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public Property getProperty(int position) {
		return this.properties.get(position);
	}

	public void addProperty(Property property) {
		this.properties.add(property);
	}

	public void removeProperty(Property property) {

		if (this.properties.contains(property))
			this.properties.remove(property);
	}

}