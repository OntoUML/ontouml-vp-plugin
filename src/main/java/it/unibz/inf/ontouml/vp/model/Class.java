package it.unibz.inf.ontouml.vp.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.*;

import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 
 * Implementation of ModelElement to handle IClass and IDataType objects to be
 * serialized as ontouml-schema/Class
 * 
 * @author Claudenir Fonseca
 * @author Tiago Prince Sales
 * @author Victor Viola
 *
 */

public class Class implements ModelElement {

	private final IModelElement sourceModelElement;

	@SerializedName("type")
	@Expose
	private final String type;

	@SerializedName("id")
	@Expose
	private final String id;

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("description")
	@Expose
	private String description;

	@SerializedName("properties")
	@Expose
	private Set<Property> properties;

	@SerializedName("literals")
	@Expose
	private LinkedList<Literal> literals;

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

	@SerializedName("allowed")
	@Expose
	private JsonArray allowed;

	@SerializedName("isExtensional")
	@Expose
	private JsonElement isExtensional;

	@SerializedName("isPowertype")
	@Expose
	private JsonElement isPowertype;

	@SerializedName("order")
	@Expose
	private String order;

	private Class(IModelElement source) {
		this.sourceModelElement = source;
		this.type = ModelElement.TYPE_CLASS;
		this.id = source.getId();
		this.properties = null;
		this.stereotypes = null;
		this.literals = null;
	}

	public Class(IClass source) {
		this((IModelElement) source);

		final IAttribute[] attributes = source.toAttributeArray();
		for (int i = 0; attributes != null && i < attributes.length; i++) {
			addProperties(new Property(attributes[i]));
		}

		final String[] stereotypes = source.toStereotypeArray();
		for (int i = 0; stereotypes != null && i < stereotypes.length; i++) {
			addStereotype(stereotypes[i]);
		}

		if (this.stereotypes != null && this.stereotypes.contains(StereotypeUtils.STR_ENUMERATION)) {
			IEnumerationLiteral[] literalArray = source.toEnumerationLiteralArray();
			for (int i = 0; literalArray != null && i < literalArray.length; i++)
				addLiteral(new Literal(literalArray[i]));
		}

		setAbstract(source.isAbstract());
		setPropertyAssignments(ModelElement.transformPropertyAssignments(source));

		if (source.getName().trim().startsWith("/")) {
			setName(source.getName().substring(1));
			this.isDerived = true;
		} else {
			setName(source.getName().trim());
		}

		setDescription(source.getDescription());

		if(source.getTaggedValues() != null) {
			final Iterator<?> values = source.getTaggedValues().taggedValueIterator();

			while(values.hasNext()) {
				try{
					final ITaggedValue value = (ITaggedValue) values.next();
					final JsonParser parser = new JsonParser();
	
					if(value.getName().equals("allowed")){
						String valueString = value.getValueAsString();
						valueString = valueString
								.trim()
								.replaceAll(" +", "")
								.replaceAll(",", "\",\"");
						
						final JsonElement allowed = !valueString.equals("") ? parser.parse("[\"" + valueString + "\"]") : parser.parse("[]");
						this.allowed = allowed.isJsonArray() && ((JsonArray) allowed).size() > 0 ?
								(JsonArray) allowed : null;
					}
	
					if(value.getName().equals("isExtensional")){
						if(source.hasStereotype(StereotypeUtils.STR_COLLECTIVE)){
							this.isExtensional = 
									parser.parse(Boolean.valueOf(value.getValueAsString()).toString());
						} else {
							this.isExtensional = parser.parse("null");
						}
					}

					if(value.getName().equals("isPowertype")){
						if(source.hasStereotype(StereotypeUtils.STR_TYPE)){
							this.isPowertype = 
									parser.parse(Boolean.valueOf(value.getValueAsString()).toString());
						} else {
							this.isPowertype = parser.parse("null");
						}
					}

					if(value.getName().equals("order")){
						this.order = value.getValueAsString();
					}

				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Class(IDataType source) {
		this((IModelElement) source);

		addStereotype(StereotypeUtils.STR_DATATYPE);
		setAbstract(false);
		setDerived(false);

		setPropertyAssignments(ModelElement.transformPropertyAssignments(source));

		if (source.getName().trim().startsWith("/")) {
			setName(source.getName().substring(1));
			this.isDerived = true;
		} else {
			setName(source.getName().trim());
		}
	}

	@Override
	public IModelElement getSourceModelElement() {
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
		this.name = ModelElement.safeGetString(name);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = ModelElement.safeGetString(description);
		;
	}

	public Set<Property> getProperties() {
		return properties;
	}

	public void addProperties(Property property) {
		if (this.properties == null)
			this.properties = new HashSet<Property>();

		this.properties.add(property);
	}

	public void removeProperties(Property property) {
		if (this.properties != null && this.properties.contains(property))
			this.properties.remove(property);
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
		if (this.stereotypes == null)
			this.stereotypes = new ArrayList<String>();

		this.stereotypes.add(name);
	}

	public void removeStereotype(String name) {
		if (this.stereotypes != null && this.stereotypes.contains(name))
			this.stereotypes.remove(name);
	}

	public boolean isAbstract() {
		return this.isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	public boolean isDerived() {
		return this.isDerived;
	}

	public void setDerived(boolean isDerived) {
		this.isDerived = isDerived;
	}

	public LinkedList<Literal> getLiterals() {
		return literals;
	}

	public void setLiterals(LinkedList<Literal> literals) {
		this.literals = literals;
	}

	public void addLiteral(Literal literal) {
		if (getLiterals() == null) {
			setLiterals(new LinkedList<Literal>());
		}

		this.literals.add(literal);
	}
}