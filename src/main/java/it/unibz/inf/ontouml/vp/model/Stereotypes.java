package it.unibz.inf.ontouml.vp.model;

import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

public class Stereotypes {
	
	public static String getBaseURI(String stereotype) {
		
		if(StereotypeUtils.STEREOTYPES.contains(stereotype))
			return "ontouml/2.0/"+stereotype;
		
		return "vp/custom/"+stereotype;

	}

}
