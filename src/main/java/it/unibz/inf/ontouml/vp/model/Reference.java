package it.unibz.inf.ontouml.vp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.factory.IModelElementFactory;

public class Reference {

	@SerializedName("type")
	@Expose
	private final String type;
	
	@SerializedName("id")
	@Expose
	private final String id;

	public Reference(String type, String id) {
		this.type = type;
		this.id = id;
	}

	public Reference(IModelElement element) {
		this.id = element.getId();
		this.type = ModelElement.toOntoUMLSchemaType(element);

		if(this.type == null) {
			// TODO Enhance processment of exceptions
			// Exception e = new  NullPointerException("Broken reference on ModelElement.toOntoUMLSchemaType(element)");
			// e.printStackTrace();
			System.out.println("Broken reference on ModelElement.toOntoUMLSchemaType(element)");
		}
	}

	public String getType() {
		return type;
	}

	public String getId() {
		return id;
	}
}