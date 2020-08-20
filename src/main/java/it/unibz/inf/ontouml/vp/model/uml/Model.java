package it.unibz.inf.ontouml.vp.model.uml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationClass;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IDataType;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IGeneralizationSet;
import com.vp.plugin.model.IModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;

/**
 * 
 * Implementation of ModelElement to handle IModel objects to be serialized as
 * ontouml-schema/Package
 * 
 * @author Claudenir Fonseca
 * @author Tiago Prince Sales
 * @author Victor Viola
 *
 */

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

	@SerializedName("description")
	@Expose
	private String description = null;

	@SerializedName("contents")
	@Expose
	private List<ModelElement> contents;

	@SerializedName("propertyAssignments")
	@Expose
	private JsonObject propertyAssignments = null;

	/**
	 * 
	 * Constructs a model to contain all project's model elements independent of a
	 * <code>IModelElement</code>.
	 * 
	 */
	public Model() {
		final IProject project = ApplicationManager.instance().getProjectManager().getProject();
		final String[] rootLevelElements = { IModelElementFactory.MODEL_TYPE_PACKAGE,
				IModelElementFactory.MODEL_TYPE_MODEL, IModelElementFactory.MODEL_TYPE_CLASS,
				IModelElementFactory.MODEL_TYPE_DATA_TYPE };
		final String[] anyLevelElements = { IModelElementFactory.MODEL_TYPE_GENERALIZATION,
				IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET, IModelElementFactory.MODEL_TYPE_ASSOCIATION,
				IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS };

		this.sourceModelElement = null;
		this.type = ModelElement.TYPE_PACKAGE;
		this.id = project.getId();
		this.setName(project.getName());
		this.addModelElements(project.toModelElementArray(rootLevelElements));
		this.addModelElements(project.toAllLevelModelElementArray(anyLevelElements));
	}

	/**
	 * 
	 * Constructs a model based on the model elements defined by the user
	 *
	 * 
	 */
	public Model(HashSet<String> idElements) {
		final IProject project = ApplicationManager.instance().getProjectManager().getProject();
		final String[] rootLevelElements = { IModelElementFactory.MODEL_TYPE_PACKAGE,
				IModelElementFactory.MODEL_TYPE_MODEL, IModelElementFactory.MODEL_TYPE_CLASS,
				IModelElementFactory.MODEL_TYPE_DATA_TYPE };
		final String[] anyLevelElements = { IModelElementFactory.MODEL_TYPE_GENERALIZATION,
				IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET, IModelElementFactory.MODEL_TYPE_ASSOCIATION,
				IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS };

		this.sourceModelElement = null;
		this.type = ModelElement.TYPE_PACKAGE;
		this.id = project.getId();
		this.setName(project.getName());

		if (idElements == null)
			return;

		if (containsDiagramModel(idElements)) {
			HashSet<IModelElement> modelElements = new HashSet<IModelElement>();

			Iterator<String> ite = idElements.iterator();
			// add only valid elements
			while (ite.hasNext()) {
				String id = ite.next();

				if (project.getModelElementById(id) == null)
					continue;
				else
					modelElements.add(project.getModelElementById(id));
			}

			IModelElement[] elementsArray = new IModelElement[modelElements.size()];
			modelElements.toArray(elementsArray);

			addModelElements(elementsArray, idElements);
		} else {
			addModelElements(project.toModelElementArray(rootLevelElements), idElements);
			addModelElements(project.toAllLevelModelElementArray(anyLevelElements), idElements);
		}

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
		this.id = source.getId();
		this.setName(source.getName());
		this.addModelElements(source.toChildArray());
	}

	public Model(IModel source, HashSet<String> idElements) {
		this.sourceModelElement = source;
		this.type = ModelElement.TYPE_PACKAGE;
		this.id = source.getId();
		this.setName(source.getName());

		IModelElement[] childArray = source.toChildArray();

		if (childArray == null)
			return;

		for (int i = 0; i < childArray.length; i++) {
			if (idElements.contains(childArray[i].getId())) {
				this.addModelElement(childArray[i], idElements);
			}
		}

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
		this.name = ModelElement.safeGetString(name);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = ModelElement.safeGetString(description);
	}

	public List<ModelElement> getElements() {
		return contents;
	}

	public void setElements(List<ModelElement> elementsList) {
		this.contents = elementsList;
	}

	public void addElement(ModelElement element) {
		if (this.contents == null)
			this.contents = new ArrayList<ModelElement>();

		this.contents.add(element);
	}

	public boolean removeElement(ModelElement element) {
		return this.contents.remove(element);
	}

	private void addModelElement(IModelElement projectElement, HashSet<String> idElements) {

		switch (projectElement.getModelType()) {
		case IModelElementFactory.MODEL_TYPE_PACKAGE:
			addElement(new Package((IPackage) projectElement, idElements));
			break;

		case IModelElementFactory.MODEL_TYPE_MODEL:
			addElement(new Model((IModel) projectElement, idElements));
			break;

		case IModelElementFactory.MODEL_TYPE_CLASS:
			addElement(new Class((IClass) projectElement, idElements));
			break;

		case IModelElementFactory.MODEL_TYPE_DATA_TYPE:
			addElement(new Class((IDataType) projectElement));
			break;

		case IModelElementFactory.MODEL_TYPE_GENERALIZATION:
			IGeneralization gen = (IGeneralization) projectElement;
			IModelElement fromElement = gen.getFrom();

			if (fromElement == null)
				break;

			String fromType = fromElement.getModelType();

			if (fromType == null)
				break;

			boolean isFromClass = fromType.equals(IModelElementFactory.MODEL_TYPE_CLASS);
			boolean isFromAssociation = fromType.equals(IModelElementFactory.MODEL_TYPE_ASSOCIATION);

			if (!isFromClass && !isFromAssociation)
				break;

			IModelElement toElement = gen.getTo();

			if (toElement == null)
				break;

			String toType = toElement.getModelType();

			if (toType == null)
				break;

			boolean isToClass = toType.equals(IModelElementFactory.MODEL_TYPE_CLASS);
			boolean isToAssociation = toType.equals(IModelElementFactory.MODEL_TYPE_ASSOCIATION);

			if (!isToClass && !isToAssociation)
				break;

			addElement(new Generalization((IGeneralization) projectElement));
			break;

		case IModelElementFactory.MODEL_TYPE_ASSOCIATION:
			addElement(new Association((IAssociation) projectElement, idElements));
			break;

		case IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET:
			addElement(new GeneralizationSet((IGeneralizationSet) projectElement, idElements));
			break;

		case IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS:
			addElement(new AssociationClass((IAssociationClass) projectElement));
		}
	}

	private void addModelElements(IModelElement[] modelElements, HashSet<String> idElements) {
		for (int i = 0; modelElements != null && i < modelElements.length; i++) {
			final IModelElement projectElement = modelElements[i];

			if (!idElements.contains(projectElement.getId()))
				continue;

			addModelElement(projectElement, idElements);

		}
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

			case IModelElementFactory.MODEL_TYPE_DATA_TYPE:
				addElement(new Class((IDataType) projectElement));
				break;

			case IModelElementFactory.MODEL_TYPE_GENERALIZATION:
				IGeneralization gen = (IGeneralization) projectElement;
				IModelElement fromElement = gen.getFrom();

				if (fromElement == null)
					break;

				String fromType = fromElement.getModelType();

				if (fromType == null)
					break;

				boolean isFromClass = fromType.equals(IModelElementFactory.MODEL_TYPE_CLASS);
				boolean isFromAssociation = fromType.equals(IModelElementFactory.MODEL_TYPE_ASSOCIATION);

				if (!isFromClass && !isFromAssociation)
					break;

				IModelElement toElement = gen.getTo();

				if (toElement == null)
					break;

				String toType = toElement.getModelType();

				if (toType == null)
					break;

				boolean isToClass = toType.equals(IModelElementFactory.MODEL_TYPE_CLASS);
				boolean isToAssociation = toType.equals(IModelElementFactory.MODEL_TYPE_ASSOCIATION);

				if (!isToClass && !isToAssociation)
					break;

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

	private static boolean containsDiagramModel(HashSet<String> idElements) {
		final IProject project = ApplicationManager.instance().getProjectManager().getProject();

		Iterator<String> ite = idElements.iterator();

		while (ite.hasNext()) {
			String id = ite.next();

			if (project.getDiagramById(id) == null)
				continue;
			else
				return true;

		}

		return false;
	}

}