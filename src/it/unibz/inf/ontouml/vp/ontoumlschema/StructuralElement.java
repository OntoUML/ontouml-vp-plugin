package it.unibz.inf.ontouml.vp.ontoumlschema;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IModelElement;

public interface StructuralElement {
	
	public static final String description = "This field contains entities present in OntoUML structural aspects (packages, classes, relations, generalizations and so on). Contained packages may have their on fields 'structuralElements'.";
	
	public static final String TYPE_PACKAGE = "Package";
	public static final String TYPE_CLASS = "Class";
	public static final String TYPE_RELATION = "Relation";
	public static final String TYPE_ASSOCIATION_CLASS = "Relation";
	public static final String TYPE_GENERALIZATION_LINK = "GeneralizationLink";
	public static final String TYPE_GENERALIZATION_SET = "GeneralizationSet";
	public static final String TYPE_PROPERTY = "Property";
	
	public IModelElement getSourceModelElement();
	
	public String getType();
	
	public String getId();
	
	public String getURI();
	
	public void setURI(String uri);
	
	public static String getModelElementURI(StructuralElement elem) {
		if(elem.getSourceModelElement() == null) {
			return null;
		}
		else {
			// Captures project's link to the model element and ignores first part which concerns the project's name.
			return StructuralElement.getModelElementURI(elem.getSourceModelElement());
		}
	}
	
	public static String getModelElementURI(IModelElement elem) {
		// Captures project's link to the model element and ignores first part which concerns the project's name.
		String link = ApplicationManager.instance().getProjectManager().getLink(elem, false);
		return link.substring(link.indexOf(".vpp:")+1);
	}
}
