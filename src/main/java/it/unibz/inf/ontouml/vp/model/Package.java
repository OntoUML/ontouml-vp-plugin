package it.unibz.inf.ontouml.vp.model;

import java.util.LinkedList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.factory.IModelElementFactory;

public class Package implements ModelElement {

	private final IPackage sourceModelElement;
	
	@SerializedName("type")
	@Expose
	private final String type;

	@SerializedName("id")
	@Expose
	private String id;

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("elements")
	@Expose
	private LinkedList<ModelElement> elements;

	public Package(IPackage source) {
		this.sourceModelElement = source;
		this.type = ModelElement.TYPE_PACKAGE;
		setName(source.getName());
		
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
//			TODO Add remaining elements, maybe by adding these to relation's source's package.
//			case IModelElementFactory.MODEL_TYPE_GENERALIZATION:
//			case IModelElementFactory.MODEL_TYPE_ASSOCIATION:
//			case IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS:
//			case IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET:
			}
		}
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
		this.name = name;
	}

	public LinkedList<ModelElement> getElements() {
		return elements;
	}

	public void setElements(LinkedList<ModelElement> elements) {
		this.elements = elements;
	}

	public void addElement(ModelElement element) {
		if(getElements() == null) {
			setElements(new LinkedList<ModelElement>());
		}
		
		this.elements.add(element);
	}

}
