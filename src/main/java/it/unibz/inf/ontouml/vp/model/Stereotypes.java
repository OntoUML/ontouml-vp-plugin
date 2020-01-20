package it.unibz.inf.ontouml.vp.model;

public class Stereotypes {
	
	public static final String BASE_URI_CATEGORY = "ontouml/2.0/category";
	public static final String BASE_URI_MIXIN = "ontouml/2.0/mixin";
	public static final String BASE_URI_ROLE_MIXIN = "ontouml/2.0/roleMixin";
	public static final String BASE_URI_PHASE_MIXIN = "ontouml/2.0/phaseMixin";

	public static final String BASE_URI_KIND = "ontouml/2.0/kind";
	public static final String BASE_URI_COLLECTIVE = "ontouml/2.0/collective";
	public static final String BASE_URI_QUANTITY = "ontouml/2.0/quantity";
	public static final String BASE_URI_RELATOR_KIND = "ontouml/2.0/relatorKind";
	public static final String BASE_URI_QUALITY_KIND = "ontouml/2.0/qualityKind";
	public static final String BASE_URI_MODE_KIND = "ontouml/2.0/modeKind";
	
	public static final String BASE_URI_SUBKIND = "ontouml/2.0/subkind";
	public static final String BASE_URI_ROLE = "ontouml/2.0/role";
	public static final String BASE_URI_PHASE = "ontouml/2.0/phase";

	/* OntoUML Association Stereotypes for Relation Types */
	public static final String BASE_URI_MATERIAL = "ontouml/2.0/material";
	public static final String BASE_URI_COMPARATIVE = "ontouml/2.0/comparative";
	public static final String BASE_URI_HISTORICAL = "ontouml/2.0/historical";
	
	public static final String BASE_URI_MEDIATION = "ontouml/2.0/mediation";
	public static final String BASE_URI_CHARACTERIZATION = "ontouml/2.0/characterization";
	public static final String BASE_URI_EXTERNAL_DEPENDENCE = "ontouml/2.0/externalDependence";
	
	public static final String BASE_URI_COMPONENT_OF = "ontouml/2.0/componentOf";
	public static final String BASE_URI_MEMBER_OF = "ontouml/2.0/memberOf";
	public static final String BASE_URI_SUB_COLLECTION_OF = "ontouml/2.0/subCollectionOf";
	public static final String BASE_URI_SUB_QUANTITY_OF = "ontouml/2.0/subQuantityOf";
	public static final String BASE_URI_SUM = "ontouml/2.0/sum";
	
	public static final String BASE_URI_UNKOWN = "vp/custom/";
	
	public static String getBaseURI(String stereotype) {
		
		switch(stereotype) {
			case "category":
				return BASE_URI_CATEGORY;
			case "mixin":
				return BASE_URI_MIXIN;
			case "roleMixin":
				return BASE_URI_ROLE_MIXIN;
			case "phaseMixin":
				return BASE_URI_PHASE_MIXIN;
			case "kind":
				return BASE_URI_KIND;
			case "collective":
				return BASE_URI_COLLECTIVE;
			case "quantity":
				return BASE_URI_QUANTITY;
			case "relatorKind":
				return BASE_URI_RELATOR_KIND;
			case "qualityKind":
				return BASE_URI_QUALITY_KIND;
			case "modeKind":
				return BASE_URI_MODE_KIND;
			case "subkind":
				return BASE_URI_SUBKIND;
			case "role":
				return BASE_URI_ROLE;
			case "phase":
				return BASE_URI_PHASE;
			case "material":
				return BASE_URI_MATERIAL;
			case "comparative":
				return BASE_URI_COMPARATIVE;
			case "historical":
				return BASE_URI_HISTORICAL;
			case "mediation":
				return BASE_URI_MEDIATION;
			case "characterization":
				return BASE_URI_CHARACTERIZATION;
			case "external dependence":
				return BASE_URI_EXTERNAL_DEPENDENCE;
			default:
				return BASE_URI_UNKOWN + stereotype;
		}
		
	}

}
