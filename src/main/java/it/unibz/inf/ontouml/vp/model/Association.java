package it.unibz.inf.ontouml.vp.model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationEnd;

public class Association implements ModelElement {
	
	private final IAssociation sourceModelElement;

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
	private List<AssociationEnd> properties;

	@SerializedName("stereotypes")
	@Expose
	private List<String> stereotypes;
	
	@SerializedName("isAbstract")
	@Expose
	private boolean isAbstract;
	
	@SerializedName("isDerived")
	@Expose
	private boolean isDerived;

	public Association(IAssociation source) {
		this.sourceModelElement = source;
		
		this.type = ModelElement.TYPE_RELATION;
		this.id = source.getId();
		setName(source.getName());
		
		addProperty(new AssociationEnd((IAssociationEnd) source.getFromEnd()));
		addProperty(new AssociationEnd((IAssociationEnd) source.getToEnd()));

		
		String[] stereotypes = source.toStereotypeArray();
		this.stereotypes = stereotypes!=null ? new ArrayList<String>() : null;
		
		for (int i=0; stereotypes!=null && i<stereotypes.length; i++) {
			addStereotype(Stereotypes.getBaseURI(stereotypes[i]));
		}
		
		setAbstract(source.isAbstract());
		setDerived(source.isDerived());	
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if(name.length()!=0)
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
		if(this.properties == null)
			this.properties = new ArrayList<AssociationEnd>();
		
		this.properties.add(property);
	}

	public void removeProperty(AssociationEnd property) {

		if (this.properties.contains(property))
			this.properties.remove(property);
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