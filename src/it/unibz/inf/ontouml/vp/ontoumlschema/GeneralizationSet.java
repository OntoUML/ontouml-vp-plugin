package it.unibz.inf.ontouml.vp.ontoumlschema;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeneralizationSet implements StructuralElement {

	public static final String baseURI = "model:#/generalizationset/";

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

	@SerializedName("isDisjoint")
	@Expose
	private boolean isDisjoint;

	@SerializedName("isComplete")
	@Expose
	private boolean isComplete;

	@SerializedName("tuple")
	@Expose
	private List<String> tuple;

	public GeneralizationSet() {
		this.type = "GeneralizationSet";
		this.tuple = new LinkedList<String>();
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getURI() {
		return URI;
	}

	@Override
	public void setURI(String URI) {
		this.URI = GeneralizationSet.baseURI + URI;
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

	public void setURL(String uRL) {
		URL = uRL;
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

	public void addTuple(String str) {
		this.tuple.add(str);
	}

}
