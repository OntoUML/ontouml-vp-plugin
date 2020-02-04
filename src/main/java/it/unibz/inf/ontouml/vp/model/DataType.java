package it.unibz.inf.ontouml.vp.model;

import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IDataType;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;

public class DataType implements ModelElement {

	private final IDataType sourceModelElement;
	
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
	private Set<Property> properties;
	
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

	public DataType(IDataType source) {
		this.sourceModelElement = source;
		this.type = ModelElement.TYPE_CLASS;
		this.id = source.getId();
		setName(source.getName());
		
		this.properties = null;
		
		addStereotype(Stereotypes.getBaseURI(StereotypeUtils.STR_DATATYPE));
	
		setAbstract(false);
		setDerived(false);
		
		ITaggedValueContainer lContainer = source.getTaggedValues();
		if (lContainer != null) {
			JsonObject obj = new JsonObject();
			ITaggedValue[] lTaggedValues = lContainer.toTaggedValueArray();

			for (int i = 0; lTaggedValues != null && i < lTaggedValues.length; i++){
				
				
				obj.addProperty(lTaggedValues[i].getName(),lTaggedValues[i].getValueAsString());
			}
			
			setPropertyAssignments(obj);
		}
	}

	@Override
	public IDataType getSourceModelElement() {
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
	
	public Set<Property> getProperties() {
		return properties;
	}

	public void addProperties(Property attribute) {
		if(this.properties == null)
			this.properties = new HashSet<Property>();
		
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
