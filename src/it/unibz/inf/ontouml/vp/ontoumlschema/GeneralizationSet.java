package it.unibz.inf.ontouml.vp.ontoumlschema;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeneralizationSet implements StructuralElement {

	@SerializedName("@type")
    @Expose
	private final String type;
	
	@SerializedName("uri")
    @Expose
	private String URI;
	
	@SerializedName("isDisjoint")
    @Expose
	private boolean isDisjoint;
	
	@SerializedName("isComplete")
    @Expose
	private boolean isComplete;
	
	@SerializedName("tuple")
    @Expose
	private List<String> tuple;

	GeneralizationSet(){
		this.type = "GeneralizationSet";
		this.tuple = new LinkedList<String>();
	}
	
	@Override
	public String getType() {
		return null;
	}

	@Override
	public String getURI() {
		return URI;
	}
	
	@Override
	public void setURI(String uRI) {
		URI = uRI;
	}
	
	public boolean isDisjoint() {
		return isDisjoint;
	}

	public void setDisjoint(boolean isDisjoint) {
		this.isDisjoint = isDisjoint;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	

	public List<String> getTuple() {
		return tuple;
	}

	public void setTuple(List<String> tuple) {
		this.tuple = tuple;
	}

}
