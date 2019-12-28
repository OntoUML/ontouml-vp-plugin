package it.unibz.inf.ontouml.vp.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IPackage;
import com.vp.plugin.model.factory.IModelElementFactory;

public class Model implements StructuralElement {

	public static final String baseURI = "https://ontouml.org/archive/";
	
	private final IModel sourceModelElement;
	
	@SerializedName("@type")
	@Expose
	private final String type;

	@SerializedName("uri")
	@Expose
	private String URI;

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("url")
	@Expose
	private String URL;

	@SerializedName("authors")
	@Expose
	private List<String> authors;

	@SerializedName("structuralElements")
	@Expose
	private List<StructuralElement> structuralElements;

	public Model() {
		this.sourceModelElement = null;
		this.type = StructuralElement.TYPE_MODEL;
	}
	
	public Model(IModel source) {
		this.sourceModelElement = source;
		this.type = StructuralElement.TYPE_PACKAGE;
		this.name = source.getName();
		this.URI = StructuralElement.getModelElementURI(source);
		
		IModelElement[] children = source.toChildArray();

		if(children != null) {
			this.structuralElements = new LinkedList<StructuralElement>();

			for (int i = 0; i < children.length; i++) {
				IModelElement child = children[i];
	
				switch (child.getModelType()) {
				case IModelElementFactory.MODEL_TYPE_PACKAGE:
					Package newPackage = new Package((IPackage) child);
					this.addStructuralElement(newPackage);
					break;
					
				case IModelElementFactory.MODEL_TYPE_MODEL:
					Model newModel = new Model((IModel) child);
					this.addStructuralElement(newModel);
					break;
	
				case IModelElementFactory.MODEL_TYPE_CLASS:
					Class newClass = new Class((IClass) child);
					this.addStructuralElement(newClass);
					break;
	
				case IModelElementFactory.MODEL_TYPE_GENERALIZATION:
					Generalization newGeneralization = new Generalization((IGeneralization) child);
					this.addStructuralElement(newGeneralization);
					break;
	
	//			TODO Add remaining elements, maybe by adding these to relation's source's package.
	//			case IModelElementFactory.MODEL_TYPE_ASSOCIATION:
	//			case IModelElementFactory.MODEL_TYPE_ASSOCIATION_CLASS:
	//			case IModelElementFactory.MODEL_TYPE_GENERALIZATION_SET:
				}
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

	public String getType() {
		return type;
	}

	public String getURI() {
		return URI;
	}

	public void setURI(String URI) {
		this.URI = Model.baseURI + URI;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String URL) {
		this.URL = URL;
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
		if(this.authors == null)
			this.authors = new ArrayList<String>();
		
		this.authors.add(name);
	}

	public void removeAuthor(String name) {
		if (this.authors.contains(name))
			this.authors.remove(name);
	}

	public List<StructuralElement> getStructuralElements() {
		return structuralElements;
	}

	public void setStructuralElements(List<StructuralElement> elementsList) {
		this.structuralElements = elementsList;
	}

	public void addStructuralElement(StructuralElement element) {
		if(this.structuralElements == null)
			this.structuralElements = new ArrayList<StructuralElement>();
		
		this.structuralElements.add(element);
	}

	public boolean removeStructuralElement(StructuralElement element) {
		return this.structuralElements.remove(element);
	}

}
