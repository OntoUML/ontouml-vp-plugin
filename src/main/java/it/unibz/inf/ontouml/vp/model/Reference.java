package it.unibz.inf.ontouml.vp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IModelElement;

/**
 * 
 * Implementation of ModelElement to handle IModelElement objects
 * which are referred by other elements in the model. 
 * They are serialized as ontouml-schema/Reference.
 * 
 * @author Claudenir Fonseca
 * @author Tiago Prince Sales
 * @author Victor Viola
 *
 */

public class Reference {

	@SerializedName("type")
	@Expose
	private final String type;
	
	@SerializedName("id")
	@Expose
	private final String id;

	public Reference(IModelElement element) {
		this.id = element.getId();
		this.type = ModelElement.toOntoUMLSchemaType(element);

		if(this.type == null) {
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