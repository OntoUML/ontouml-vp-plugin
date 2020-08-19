package it.unibz.inf.ontouml.vp.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.VPPluginInfo;

import it.unibz.inf.ontouml.vp.OntoUMLPlugin;

public class OntoUMLConstraintsManager {

	final private static Type mapOfListsType = new TypeToken<Map<String, List<String>>>() {}.getType();
	
	private static String GENERALIZATION_CONSTRAINTS_FILENAME = "generalization_constraints.json";
	private static String ASSOCIATION_CONSTRAINTS_FILENAME = "association_constraints.json";
	
	private static String ALLOWED_SUBCLASSES = "allowedSubClassesFor";
	private static String ALLOWED_SUPERCLASSES = "allowedSuperClassesFor";

	private static Map<String,List<String>> allowedSuperClassesFor, allowedSubClassesFor;

	private static Map<String,List<String>> associationConstraints;
	
	
	private static void loadConstraints() {
		try {
			final ApplicationManager app = ApplicationManager.instance();
			final VPPluginInfo pluginInfo = app.getPluginInfo(OntoUMLPlugin.PLUGIN_ID);
			
			File generalizationsConstraintsFile = new File(pluginInfo.getPluginDir(),GENERALIZATION_CONSTRAINTS_FILENAME);
			File associationConstraintsFile = new File(pluginInfo.getPluginDir(),ASSOCIATION_CONSTRAINTS_FILENAME);
			
			final JsonParser parser = new JsonParser();
			final JsonObject gConstraintsObject = parser.parse(new FileReader(generalizationsConstraintsFile)).getAsJsonObject();
			final JsonObject aConstraintsObject = parser.parse(new FileReader(associationConstraintsFile)).getAsJsonObject();
			
			final Gson gson = new Gson();
			allowedSubClassesFor = gson.fromJson(gConstraintsObject.getAsJsonObject(ALLOWED_SUBCLASSES), mapOfListsType);
			allowedSuperClassesFor = gson.fromJson(gConstraintsObject.getAsJsonObject(ALLOWED_SUPERCLASSES), mapOfListsType);
			associationConstraints = gson.fromJson(aConstraintsObject, mapOfListsType);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static List<String> getAllowedStereotypeActionsOnGeneral(String stereotype) {
		if(allowedSuperClassesFor == null || allowedSubClassesFor == null) {
			loadConstraints();
		}
		
		List<String> allowedStereotypes = allowedSubClassesFor.get(stereotype);
		
		if(allowedStereotypes == null) {
			allowedStereotypes = new ArrayList<String>();
		}
		
		return allowedStereotypes
				.stream()
				.map(allowed -> ActionIdManager.classStereotypeToActionID(allowed))
				.collect(Collectors.toList());
	}
	
	public static List<String> getAllowedStereotypeActionsSpecific(String stereotype) {
		if(allowedSuperClassesFor == null || allowedSubClassesFor == null) {
			loadConstraints();
		}
		
		List<String> allowedStereotypes = allowedSuperClassesFor.get(stereotype);
		
		if(allowedStereotypes == null) {
			allowedStereotypes = new ArrayList<String>();
		}
		
		return allowedStereotypes
				.stream()
				.map(allowed -> ActionIdManager.classStereotypeToActionID(allowed))
				.collect(Collectors.toList());
	}
	

	public static List<String> getAllowedStereotypeActionsOnAssociation(String sourceStereotype, String targetStereotype) {
		if(associationConstraints == null) {
			loadConstraints();
		}
		
		List<String> allowedStereotypes = associationConstraints.get(sourceStereotype + ":" + targetStereotype);
		
		if(allowedStereotypes == null) {
			allowedStereotypes = new ArrayList<String>();
		}
		
		return allowedStereotypes
				.stream()
				.map(allowed -> ActionIdManager.associationStereotypeToActionID(allowed))
				.collect(Collectors.toList());
	}

}