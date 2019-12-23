package it.unibz.inf.ontouml.vp.ontoumlschema;

import java.util.LinkedList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IHasChildrenBaseModelElement;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.factory.IModelElementFactory;

/**
 * 
 * The Container class applies to both IModel, IPackage and a number of other interfaces.
 * In OntoUML Schema, both IPackage and IModel translate to the concept of Package.
 * 
 * TODO Decide whether not to use two classes for capturing IModel and IPackage, or
 * 		to prevent the usage of IModel altogether.
 *
 */
public class Container implements StructuralElement {

	public static final String baseURI = "model:#/package/";
	
	private final IHasChildrenBaseModelElement sourceModelElement;
	
	@SerializedName("@type")
	@Expose
	private final String type;

	@SerializedName("uri")
	@Expose
	private String URI;

	@SerializedName("url")
	@Expose
	private String URL;

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("structuralElements")
	@Expose
	private LinkedList<StructuralElement> structuralElements;

	public Container(IHasChildrenBaseModelElement source) {
		this.sourceModelElement = source;
		this.type = StructuralElement.TYPE_PACKAGE;
		this.name = source.getName();
		this.URI = StructuralElement.getModelElementURI(source);
		
		this.structuralElements = new LinkedList<StructuralElement>();

		IModelElement[] children = source.toChildArray();
		for (int i = 0; children != null && i < children.length; i++) {
			IModelElement child = children[i];

			switch (child.getModelType()) {
			case IModelElementFactory.MODEL_TYPE_PACKAGE:
				Container newModelPackage = new Container((IPackage) child);
				this.addStructuralElement(newModelPackage);
				break;

			case IModelElementFactory.MODEL_TYPE_MODEL:
				Container newPackage = new Container((IPackage) child);
				this.addStructuralElement(newPackage);
				break;

			case IModelElementFactory.MODEL_TYPE_CLASS:
				Class newClass = new Class((IClass) child);
				this.addStructuralElement(newClass);
				break;

			case IModelElementFactory.MODEL_TYPE_GENERALIZATION:
				Generalization newGeneralization = new Generalization((IGeneralization) child);
				this.addStructuralElement(newGeneralization);
				break;

//			TODO Add remaining elements
//			case IModelElementFactory.MODEL_TYPE_ASSOCIATION:
//			case IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS:
//			case IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET:
			}
		}
	}
	
	public Container(String name, String URI) {
		this.sourceModelElement = null;
		this.type = StructuralElement.TYPE_PACKAGE;
		this.name = name;
		this.URI = URI;
		
		this.structuralElements = new LinkedList<StructuralElement>();
	}

	@Override
	public IHasChildrenBaseModelElement getSourceModelElement() {
		return sourceModelElement;
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
		this.URI = Container.baseURI + URI;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String URL) {
		this.URL = URL;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LinkedList<StructuralElement> getStructuralElements() {
		return structuralElements;
	}

	public void setStructuralElements(LinkedList<StructuralElement> structuralElements) {
		this.structuralElements = structuralElements;
	}

	public void addStructuralElement(StructuralElement element) {
		this.structuralElements.add(element);
	}

}
