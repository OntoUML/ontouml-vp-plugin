package it.unibz.inf.ontouml.vp.ontoumlschema;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;

public class Class implements StructuralElement {

	public static final String baseURI = "model:#/class/";
	
	private final IClass sourceModelElement;
	
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
	private Set<Attribute> properties;

	public Class(IClass source) {
		this.sourceModelElement = source;
		this.type = StructuralElement.TYPE_CLASS;
		this.name = source.getName();
		this.URI = StructuralElement.getModelElementURI(source);
		
		String[] stereotypes = source.toStereotypeArray();
		this.stereotypes = stereotypes!=null ? new ArrayList<String>() : null;
		
		for (int i=0; stereotypes!=null && i<stereotypes.length; i++) {
			this.stereotypes.add(Stereotypes.getBaseURI(stereotypes[i]));
		}
		
		if(this.stereotypes.isEmpty()) {
			this.stereotypes = null;
		}
		
//		TODO Update to make GSON ignore this field we set as null.
//		IAttribute[] attributes = source.toAttributeArray();
//		if(attributes != null) {			
//			this.properties = new HashSet<Attribute>();
//			for (int i = 0; attributes != null && i < attributes.length; i++) {
//				this.properties.add(new Attribute(attributes[i]));
//			}
//		}
//		else {
//			this.properties = null;
//		}
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
	public String getType() {
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
		this.stereotypes.add(name);
	}

	public void removeStereotype(String name) {

		if (this.stereotypes.contains(name))
			this.stereotypes.remove(name);
	}

	public Set<Attribute> getProperties() {
		return properties;
	}

	public void setProperties(Set<Attribute> properties) {
		this.properties = properties;
	}

}
