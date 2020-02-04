package it.unibz.inf.ontouml.vp.model;

import java.util.LinkedList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IAssociationClass;
import com.vp.plugin.model.IModelElement;

public class AssociationClass implements ModelElement {

	private final IAssociationClass sourceModelElement;

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
	private List<Reference> properties;
	
	public AssociationClass(IAssociationClass source) {
		this.sourceModelElement = source;
		
		this.type = ModelElement.TYPE_ASSOCIATION_CLASS;
		this.id = source.getId();
		setName(source.getName());
		
		final IModelElement association = source.getFrom();
		final IModelElement _class = source.getTo();
		
		addProperty(new Reference(association.getName(),association.getId()));
		addProperty(new Reference(_class.getName(),_class.getId()));
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Reference> getProperties() {
		return properties;
	}

	public void setProperties(List<Reference> properties) {
		this.properties = properties;
	}

	public Reference getProperty(int position) {
		return this.properties.get(position);
	}

	public void addProperty(Reference property) {
		if(getProperties() == null) {
			setProperties(new LinkedList<Reference>());
		}
		
		this.properties.add(property);
	}
}
