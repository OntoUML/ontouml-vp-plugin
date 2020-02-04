package it.unibz.inf.ontouml.vp.model;

import com.google.gson.GsonBuilder;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.factory.IModelElementFactory;

/**
 * 
 * @author Victor Viola
 * @author Claudenir Fonseca
 *
 */
public interface ModelElement {

	public static final String TYPE_MODEL = "Model";
	public static final String TYPE_PACKAGE = "Package";
	public static final String TYPE_CLASS = "Class";
	public static final String TYPE_RELATION = "Association";
	public static final String TYPE_ASSOCIATION_CLASS = "Association";
	public static final String TYPE_GENERALIZATION = "Generalization";
	public static final String TYPE_GENERALIZATION_SET = "GeneralizationSet";
	public static final String TYPE_PROPERTY = "Property";

	/**
	 * 
	 * @return <code>IModelElement</code> on which the object is based.
	 * 
	 */
	public IModelElement getSourceModelElement();

	/**
	 * 
	 * @return object's type in OntoUML Schema.
	 * 
	 */
	public String getOntoUMLType();

	/**
	 * 
	 * @return object's ID (based on a <code>IModelElement</code>).
	 * 
	 */
	public String getId();

	/**
	 * 
	 * Returns Visual Paradigm's link to the related model element. This method
	 * removes the project's name which originally start the link returned.
	 * 
	 * @param modelElement
	 *            - Instance of <code>ModelElement</code> based on a
	 *            <code>IModelElement</code>.
	 * 
	 * @return a link identify a <code>IModelElement</code> in Visual Paradigm
	 *         following the pattern
	 *         <code>"vpp://modelelement/Cd.WKPaAUB22rwx4"</code>.
	 * 
	 */
	public static String getLink(ModelElement modelElement) {
		return modelElement.getSourceModelElement() != null ? ModelElement
				.getModelElementURI(modelElement.getSourceModelElement())
				: null;
	}

	/**
	 * 
	 * Returns Visual Paradigm's link to the related model element. This method
	 * removes the project's name which originally start the link returned.
	 * 
	 * @param modelElement
	 * 
	 * @return a link identify a <code>IModelElement</code> in Visual Paradigm
	 *         following the pattern
	 *         <code>"vpp://modelelement/Cd.WKPaAUB22rwx4"</code>.
	 * 
	 */
	public static String getModelElementURI(IModelElement modelElement) {
		if (modelElement == null)
			return null;

		final String link = ApplicationManager.instance().getProjectManager()
				.getLink(modelElement, false);

		return link.substring(link.indexOf(".vpp:") + 1);
	}

	/**
	 * 
	 * Returns serialized JSON string of a <code>ModelElement</code> in OntoUML
	 * Schema.
	 * 
	 * @param modelElement
	 * @param pretty
	 *            - <code>true</code> if return string should be indented.
	 * 
	 * @return serialized version JSON of a <code>ModelElement</code>.
	 * 
	 */
	public static String serialize(ModelElement modelElement, boolean pretty) {
		if (pretty) {
			return new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
					.serializeNulls().setPrettyPrinting().create().toJson(modelElement);
		} else {
			return new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
					.serializeNulls().create().toJson(modelElement);
		}
	}

	/**
	 * 
	 * Returns serialized JSON string of the whole project in OntoUML Schema.
	 * 
	 * @param pretty
	 *            - <code>true</code> if return string should be indented.
	 * 
	 * @return serialized version JSON of whole project in OntoUML Schema.
	 * 
	 */
	public static String generateModel(boolean pretty) {
		final Model model = new Model();

		if (pretty) {
			return new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
					.serializeNulls().setPrettyPrinting().create().toJson(model);
		} else {
			return new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
					.serializeNulls().create().toJson(model);
		}
	}

	public static String toOntoUMLSchemaType(IModelElement element) {
		switch (element.getModelType()) {
			case IModelElementFactory.MODEL_TYPE_MODEL:
				return TYPE_MODEL;
			case IModelElementFactory.MODEL_TYPE_PACKAGE:
				return TYPE_PACKAGE;
			case IModelElementFactory.MODEL_TYPE_CLASS:
				return TYPE_CLASS;
			case IModelElementFactory.MODEL_TYPE_ASSOCIATION:
				return TYPE_RELATION;
			case IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS:
				return TYPE_ASSOCIATION_CLASS;
			case IModelElementFactory.MODEL_TYPE_GENERALIZATION:
				return TYPE_GENERALIZATION;
			case IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET:
				return TYPE_GENERALIZATION_SET;
			case IModelElementFactory.MODEL_TYPE_ATTRIBUTE:
				return TYPE_PROPERTY;
			case IModelElementFactory.MODEL_TYPE_DATA_TYPE:
				return TYPE_CLASS;
		}

		return null;
	}
}
