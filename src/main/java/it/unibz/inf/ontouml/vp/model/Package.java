package it.unibz.inf.ontouml.vp.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * 
 * Implementation of ModelElement to handle IPackage objects to be serialized as
 * ontouml-schema/Package
 * 
 * @author Claudenir Fonseca
 * @author Tiago Prince Sales
 * @author Victor Viola
 *
 */

public class Package implements ModelElement {

	private final IPackage sourceModelElement;

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

	@SerializedName("propertyAssignments")
	@Expose
	private JsonObject propertyAssignments;

	@SerializedName("contents")
	@Expose
	private LinkedList<ModelElement> contents;

	public Package(IPackage source) {
		this.sourceModelElement = source;
		this.type = ModelElement.TYPE_PACKAGE;
		this.id = source.getId();
		setName(source.getName());
		setDescription(source.getDescription());

		final IModelElement[] children = source.toChildArray();
		for (int i = 0; children != null && i < children.length; i++) {
			final IModelElement child = children[i];

			switch (child.getModelType()) {
			case IModelElementFactory.MODEL_TYPE_PACKAGE:
				addElement(new Package((IPackage) child));
				break;
			case IModelElementFactory.MODEL_TYPE_MODEL:
				addElement(new Model((IModel) child));
				break;
			case IModelElementFactory.MODEL_TYPE_CLASS:
				addElement(new Class((IClass) child));
				break;
			// TODO Add remaining elements, maybe by adding these to relation's
			// source's package.
			// case IModelElementFactory.MODEL_TYPE_GENERALIZATION:
			// case IModelElementFactory.MODEL_TYPE_ASSOCIATION:
			// case IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS:
			// case IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET:
			}
		}

		setPropertyAssignments(ModelElement.transformPropertyAssignments(source));
	}
	
	public Package(IPackage source, HashSet<String> idElements) {
		this.sourceModelElement = source;
		this.type = ModelElement.TYPE_PACKAGE;
		this.id = source.getId();
		setName(source.getName());
		setDescription(source.getDescription());

		final IModelElement[] children = source.toChildArray();
		for (int i = 0; children != null && i < children.length; i++) {
			final IModelElement child = children[i];
			
			if(!idElements.contains(child.getId()))
				continue;
			
			switch (child.getModelType()) {
			case IModelElementFactory.MODEL_TYPE_PACKAGE:
				addElement(new Package((IPackage) child, idElements));
				break;
			case IModelElementFactory.MODEL_TYPE_MODEL:
				addElement(new Model((IModel) child, idElements));
				break;
			case IModelElementFactory.MODEL_TYPE_CLASS:
				addElement(new Class((IClass) child, idElements));
				break;
			}
		}

		setPropertyAssignments(ModelElement.transformPropertyAssignments(source));
	}

	@Override
	public IPackage getSourceModelElement() {
		return sourceModelElement;
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
		this.description = ModelElement.safeGetString(description);;
	}

	public JsonObject getPropertyAssignments() {
		return propertyAssignments;
	}

	public void setPropertyAssignments(JsonObject propertyAssignments) {
		this.propertyAssignments = propertyAssignments;
	}

	public LinkedList<ModelElement> getContents() {
		return contents;
	}

	public void setContents(LinkedList<ModelElement> contents) {
		this.contents = contents;
	}

	public void addElement(ModelElement element) {
		if (getContents() == null) {
			setContents(new LinkedList<ModelElement>());
		}

		this.contents.add(element);
	}

}
