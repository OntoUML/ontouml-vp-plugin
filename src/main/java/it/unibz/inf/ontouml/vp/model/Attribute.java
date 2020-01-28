package it.unibz.inf.ontouml.vp.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IModelElement;

public class Attribute implements ModelElement {
	
	private final IAttribute sourceModelElement;

	@SerializedName("type")
	@Expose
	private final String type;

	@SerializedName("id")
	@Expose
	private final String id;

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("propertyType")
	@Expose
	private Reference propertyType;

	@SerializedName("cardinality")
	@Expose
	private String cardinality;
	
	@SerializedName("isDerived")
	@Expose
	private boolean isDerived;
	
	@SerializedName("isOrdered")
	@Expose
	private boolean isOrdered;
	
	@SerializedName("isReadOnly")
	@Expose
	private boolean isReadOnly;
	
	@SerializedName("stereotypes")
	@Expose
	private List<String> stereotypes;
	
	@SerializedName("propertyAssignments")
	@Expose
	private JsonObject propertyAssignments;
	
	@SerializedName("subsettedProperties")
	@Expose
	private List<Reference> subsettedProperties;
	
	@SerializedName("redefinedProperties")
	@Expose
	private List<Reference> redefinedProperties;
	
	@SerializedName("aggregationKind")
	@Expose
	private String aggregationKind;

	public Attribute(IAttribute source) {
		this.sourceModelElement = source;
		
		this.type = ModelElement.TYPE_PROPERTY;
		this.id = source.getId();
		setName(source.getName());
		
		IModelElement reference = source.getTypeAsElement();
		if(reference!=null) {
			setPropertyType(new Reference(reference));
		}
		else {
			// TODO Enhance processment of exceptions
			// Exception e = new NullPointerException("Property type is a non-standard string: "+source.getTypeAsString());
			// e.printStackTrace();
			System.out.println("Property type is a non-standard string: "+source.getTypeAsString());
		}
		
		if(!((source.getMultiplicity()).equals(IAttribute.MULTIPLICITY_UNSPECIFIED)))
			setCardinality(source.getMultiplicity());
		
		setDerived(source.isDerived());
		//TODO:isOrdered
		setReadOnly(source.isReadOnly());
		
		final String[] stereotypes = source.toStereotypeArray();
		for (int i=0; stereotypes != null && i<stereotypes.length; i++) {
			addStereotype(Stereotypes.getBaseURI(stereotypes[i]));
		}
		
		//TODO:Property Assignments
		JsonObject obj = new JsonObject();		
		obj.add("nonStandardProperty", JsonNull.INSTANCE);
		
		setPropertyAssignments(obj);
		
		
		Iterator<?> subsettedIterator = source.subsettedPropertyIterator();
		
		while(subsettedIterator.hasNext()){
			IAttribute atr = (IAttribute) subsettedIterator.next();
			addSubsettedProperty(new Reference(atr.getModelType(),atr.getId()));
		}
		
		Iterator<?> redefinedProperties = source.redefinedPropertyIterator();
		
		while(redefinedProperties.hasNext()){
			IAttribute rdp = (IAttribute) redefinedProperties.next();
			addRedefinedProperty(new Reference(rdp.getModelType(),rdp.getId()));
		}
		
		setAggregationKind(source.getAggregation());
	}
	
	@Override
	public String getId() {
		return getSourceModelElement().getId();
	}
	
	@Override
	public IAttribute getSourceModelElement() {
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
		if(name.length()!=0)
			this.name = name;
	}

	public Reference getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(Reference propertyType) {
		this.propertyType = propertyType;
	}

	public String getCardinality() {
		return cardinality;
	}

	public void setCardinality(String cardinality) {
		this.cardinality = cardinality;
	}
	
	public boolean isDerived() {
		return isDerived;
	}

	public void setDerived(boolean isDerived) {
		this.isDerived = isDerived;
	}
	
	public boolean isOrdered() {
		return isOrdered;
	}

	public void setOrdered(boolean isOrdered) {
		this.isOrdered = isOrdered;
	}

	public boolean isReadOnly() {
		return isReadOnly;
	}

	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
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

	public JsonObject getPropertyAssignments() {
		return propertyAssignments;
	}

	public void setPropertyAssignments(JsonObject propertyAssignments) {
		this.propertyAssignments = propertyAssignments;
	}

	public List<Reference> getSubsettedProperties() {
		return subsettedProperties;
	}

	public void setSubsettedProperties(List<Reference> subsettedProperties) {
		this.subsettedProperties = subsettedProperties;
	}
	
	public void addSubsettedProperty(Reference ref){
		if(this.subsettedProperties == null)
			this.subsettedProperties = new ArrayList<Reference>();
		
		this.subsettedProperties.add(ref);
	}
	
	public void removeSubsettedProperty(Reference ref){
		if(this.subsettedProperties != null && this.subsettedProperties.contains(ref))
			this.stereotypes.remove(ref);
	}

	public List<Reference> getRedefinedProperties() {
		return redefinedProperties;
	}

	public void setRedefinedProperties(List<Reference> redefinedPropeties) {
		this.redefinedProperties = redefinedPropeties;
	}
	
	public void addRedefinedProperty(Reference ref){
		if(this.redefinedProperties == null)
			this.redefinedProperties = new ArrayList<Reference>();
		
		this.redefinedProperties.add(ref);
	}
	
	public void removeRedefinedProperty(Reference ref){
		if(this.redefinedProperties != null && this.redefinedProperties.contains(ref))
			this.redefinedProperties.remove(ref);
	}

	public String getAggregationKind() {
		return aggregationKind;
	}

	public void setAggregationKind(int aggregation) {
		switch(aggregation){
		case 0:
			this.aggregationKind = "NONE";
			break;
		case 1:
			this.aggregationKind = "SHARED";
			break;
		case 2:
			this.aggregationKind = "COMPOSITE";
			break;
		default:
		}
	}

	public String getType() {
		return type;
	}
}
