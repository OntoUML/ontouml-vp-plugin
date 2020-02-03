package it.unibz.inf.ontouml.vp.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;

public class Class implements ModelElement {

	private final IClass sourceModelElement;
	
	@SerializedName("type")
	@Expose
	private final String type;
	
	@SerializedName("id")
	@Expose
	private final String id;
	
	@SerializedName("name")
	@Expose
	private String name;
	
	@SerializedName("properties")
	@Expose
	private Set<Attribute> properties;
	
	@SerializedName("propertyAssignments")
	@Expose
	private JsonObject propertyAssignments;

	@SerializedName("stereotypes")
	@Expose
	private List<String> stereotypes;
	
	@SerializedName("isAbstract")
	@Expose
	private boolean isAbstract;
	
	@SerializedName("isDerived")
	@Expose
	private boolean isDerived;

	public Class(IClass source) {
		this.sourceModelElement = source;
		this.type = ModelElement.TYPE_CLASS;
		this.id = source.getId();
		setName(source.getName());
		
		final IAttribute[] attributes = source.toAttributeArray();
		for (int i = 0; attributes != null && i < attributes.length; i++) {
			addProperties(new Attribute(attributes[i]));
		}
		
		final String[] stereotypes = source.toStereotypeArray();
		for (int i=0; stereotypes != null && i<stereotypes.length; i++) {
			addStereotype(Stereotypes.getBaseURI(stereotypes[i]));
		}
		
		setAbstract(source.isAbstract());
		
		
		
		ITaggedValueContainer lContainer = source.getTaggedValues();
		if (lContainer != null) {
			JsonObject obj = new JsonObject();
			ITaggedValue[] lTaggedValues = lContainer.toTaggedValueArray();

			for (int i = 0; lTaggedValues != null && i < lTaggedValues.length; i++)
				obj.addProperty(lTaggedValues[i].getName(),
						lTaggedValues[i].getValueAsString());
			
			setPropertyAssignments(obj);
		}
		
		//TODO:HOW WE KNOW IS DERIVED
		this.isDerived = false;
		
	}

	@Override
	public IClass getSourceModelElement() {
		return this.sourceModelElement;
	}
	
	@Override
	public String getId() {
		return getSourceModelElement().getId();
	}

	@Override
	public String getOntoUMLType() {
		return this.type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if(name.length()!=0)
			this.name = name;
	}
	
	public Set<Attribute> getProperties() {
		return properties;
	}

	public void addProperties(Attribute attribute) {
		if(this.properties == null)
			this.properties = new HashSet<Attribute>();
		
		this.properties.add(attribute);
	}
	
	public void removeProperties(Attribute attribute) {
		if (this.properties != null && this.properties.contains(attribute))
			this.properties.remove(attribute);
	}
	
	public JsonObject getPropertyAssignments() {
		return propertyAssignments;
	}

	public void setPropertyAssignments(JsonObject propertyAssignments) {
		this.propertyAssignments = propertyAssignments;
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
		if(this.stereotypes == null)
			this.stereotypes = new ArrayList<String>();
		
		this.stereotypes.add(name);
	}

	public void removeStereotype(String name) {
		if(this.stereotypes != null && this.stereotypes.contains(name))
			this.stereotypes.remove(name);
	}
	
	public boolean isAbstract(){
		return this.isAbstract;
	}
	
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	
	public boolean isDerived(){
		return this.isDerived;
	}
	
	public void setDerived(boolean isDerived) {
		this.isDerived = isDerived;
	}
}
