package it.unibz.inf.ontouml.vp.ontoumlschema;

public interface StructuralElement {
	
	public static final String description = "This field contains entities present in OntoUML structural aspects (packages, classes, relations, generalizations and so on). Contained packages may have their on fields 'structuralElements'.";
	
	public String getType();
	public String getURI();
	
	public void setURI(String uri);
	
}
