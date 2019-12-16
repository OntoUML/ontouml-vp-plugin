package it.unibz.inf.ontouml.vp.ontoumlschema;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Property {
	
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
	
	@SerializedName("propertyType")
    @Expose
	private String propertyType;
	
	@SerializedName("lowerbound")
    @Expose
	private String lowerbound;
	
	@SerializedName("upperbound")
    @Expose
	private String upperbound;
	
	public Property() {
		this.type = "Property";
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getURI() {
		return URI;
	}

	public void setURI(String URI) {
		this.URI = URI;
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

	public String getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}

	public String getLowerbound() {
		return lowerbound;
	}

	public void setLowerbound(String lowerbound) {
		this.lowerbound = lowerbound;
	}

	public String getUpperbound() {
		return upperbound;
	}

	public void setUpperbound(String upperbound) {
		this.upperbound = upperbound;
	}

}
