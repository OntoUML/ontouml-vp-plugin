package it.unibz.inf.ontouml.vp.ontoumlschema;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeneralizationLink implements StructuralElement {
	
	public static final String baseURI = "model:#/generalization/";

	@SerializedName("@type")
	@Expose
	private final String type;

	@SerializedName("uri")
	@Expose
	private String URI;

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("tuple")
	@Expose
	private List<String> tuple;

	public GeneralizationLink() {
		this.type = "GeneralizationLink";
		this.tuple = new LinkedList<String>();
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
		this.URI = GeneralizationLink.baseURI + URI;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
