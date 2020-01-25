package it.unibz.inf.ontouml.vp.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationClass;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IGeneralizationSet;
import com.vp.plugin.model.IModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;

public class Model implements ModelElement {

	private final IModel sourceModelElement;

	@SerializedName("type")
	@Expose
	private final String type;

	@SerializedName("id")
	@Expose
	private final String id;

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("authors")
	@Expose
	private List<String> authors;

	@SerializedName("elements")
	@Expose
	private List<ModelElement> elements;

	/**
	 * 
	 * Constructs a model to contain all project's model elements independent of a
	 * <code>IModelElement</code>.
	 * 
	 */
	public Model() {
		final IProject project = ApplicationManager.instance().getProjectManager().getProject();
		final String[] rootLevelElements = { IModelElementFactory.MODEL_TYPE_PACKAGE,
				IModelElementFactory.MODEL_TYPE_MODEL, IModelElementFactory.MODEL_TYPE_CLASS };
		final String[] anyLevelElements = { IModelElementFactory.MODEL_TYPE_GENERALIZATION,
				IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET, IModelElementFactory.MODEL_TYPE_ASSOCIATION,
				IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS };

		this.sourceModelElement = null;
		this.type = ModelElement.TYPE_MODEL;
		this.addAuthor(project.getProjectProperties().getAuthor());
		this.id = project.getId();
		this.setName(project.getName());
		this.addModelElements(project.toModelElementArray(rootLevelElements));
		this.addModelElements(project.toAllLevelModelElementArray(anyLevelElements));
	}

	/**
	 * 
	 * Constructs a model based on a <code>IModelElement</code> and which is
	 * serialized as a Package in OntoUML Schema.
	 * 
	 */
	public Model(IModel source) {
		this.sourceModelElement = source;
		this.type = ModelElement.TYPE_PACKAGE;
		this.setName(source.getName());
		this.id = source.getId();
		this.addModelElements(source.toChildArray());
	}

	@Override
	public String getId() {
		return getSourceModelElement() != null ? getSourceModelElement().getId() : null;
	}

	@Override
	public IModel getSourceModelElement() {
		return this.sourceModelElement;
	}

	public String getOntoUMLType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getAuthors() {
		return this.authors;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	public String getAuthor(int position) {
		return this.authors.get(position);
	}

	public void addAuthor(String name) {
		if (this.authors == null)
			this.authors = new ArrayList<String>();

		this.authors.add(name);
	}

	public void removeAuthor(String name) {
		if (this.authors.contains(name))
			this.authors.remove(name);
	}

	public List<ModelElement> getelements() {
		return elements;
	}

	public void setelements(List<ModelElement> elementsList) {
		this.elements = elementsList;
	}

	public void addElement(ModelElement element) {
		if (this.elements == null)
			this.elements = new ArrayList<ModelElement>();

		this.elements.add(element);
	}

	public boolean removeElement(ModelElement element) {
		return this.elements.remove(element);
	}
	
	private void addModelElements(IModelElement[] modelElements) {
		for (int i = 0; modelElements != null && i < modelElements.length; i++) {
			final IModelElement projectElement = modelElements[i];
			
			switch (projectElement.getModelType()) {
			case IModelElementFactory.MODEL_TYPE_PACKAGE:
				addElement(new Package((IPackage) projectElement));
				break;
			case IModelElementFactory.MODEL_TYPE_MODEL:
				addElement(new Model((IModel) projectElement));
				break;
			case IModelElementFactory.MODEL_TYPE_CLASS:
				addElement(new Class((IClass) projectElement));
				break;
			case IModelElementFactory.MODEL_TYPE_GENERALIZATION:
				addElement(new Generalization((IGeneralization) projectElement));
				break;
			case IModelElementFactory.MODEL_TYPE_ASSOCIATION:
				addElement(new Association((IAssociation) projectElement));
				break;
			case IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET:
				addElement(new GeneralizationSet((IGeneralizationSet) projectElement));
				break;
			case IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS:
				addElement(new AssociationClass((IAssociationClass) projectElement));
			}
		}
	}

}
