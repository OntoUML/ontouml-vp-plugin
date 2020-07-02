package it.unibz.inf.ontouml.vp.features.constraints;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.VPPluginInfo;

import it.unibz.inf.ontouml.vp.OntoUMLPlugin;

public class AssociationConstraints {
	
	final private static Type mapOfListsType = new TypeToken<Map<String, List<String>>>() {
	}.getType();
	private static String filename = "association_constraints.json";
	private static String filepath;

	private static Map<String,List<String>> constraints;
	
	private static void loadConstraints() {
		try {
			if(filepath == null) {
				final ApplicationManager app = ApplicationManager.instance();
				final VPPluginInfo[] plugins = app.getPluginInfos();
				
				for (VPPluginInfo plugin : plugins) {
					if(plugin.getPluginId().equals(OntoUMLPlugin.PLUGIN_ID)) {
						File pluginDir = plugin.getPluginDir();
						filepath = pluginDir.getAbsolutePath() + File.separator + filename;
					}
				}
			}
			
			final JsonParser parser = new JsonParser();
			final JsonElement element = parser.parse(new FileReader(filepath));
			final Gson gson = new Gson();
			constraints = gson.fromJson(element, mapOfListsType);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static List<String> getAllowedActionIDs(String sourceStereotype, String targetStereotype) {
		if(constraints == null) {
			loadConstraints();
		}
		
		List<String> allowedStereotypes = constraints.get(sourceStereotype + ":" + targetStereotype);
		
		if(allowedStereotypes == null) {
			allowedStereotypes = new ArrayList<String>();
		}
		
		return allowedStereotypes
				.stream()
				.map(allowed -> ActionIds.associationStereotypeToActionID(allowed))
				.collect(Collectors.toList());
	}
	
	public static List<String> getAllowedAssociations(String sourceStereotype, String targetStereotype) {
		if(constraints == null) {
			loadConstraints();
		}
		
		List<String> allowedStereotypes = constraints.get(sourceStereotype + ":" + targetStereotype);
		
		if(allowedStereotypes == null) {
			allowedStereotypes = new ArrayList<String>();
		}
		
		return allowedStereotypes;
	}
	
	/*
	@SuppressWarnings("serial")
	public static final Map<SimpleEntry<String, String>, ArrayList<String>> allowedCombinations = new HashMap<SimpleEntry<String, String>, ArrayList<String>>(){
		{
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_CATEGORY, StereotypeUtils.STR_KIND), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_CATEGORY, StereotypeUtils.STR_QUANTITY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_CATEGORY, StereotypeUtils.STR_COLLECTIVE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_CATEGORY, StereotypeUtils.STR_RELATOR), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_CATEGORY, StereotypeUtils.STR_MODE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_CATEGORY, StereotypeUtils.STR_QUALITY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_CATEGORY, StereotypeUtils.STR_SUBKIND), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_CATEGORY, StereotypeUtils.STR_ROLE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_CATEGORY, StereotypeUtils.STR_PHASE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_CATEGORY, StereotypeUtils.STR_CATEGORY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_CATEGORY, StereotypeUtils.STR_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_CATEGORY, StereotypeUtils.STR_ROLE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_CATEGORY, StereotypeUtils.STR_PHASE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_CATEGORY, StereotypeUtils.STR_HISTORICAL_ROLE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_CATEGORY, StereotypeUtils.STR_EVENT), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_CATEGORY, StereotypeUtils.STR_TYPE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.INSTANTIATION)));
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_CATEGORY, StereotypeUtils.STR_DATATYPE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_CATEGORY, StereotypeUtils.STR_ENUMERATION), new ArrayList<>());
		
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_COLLECTIVE, StereotypeUtils.STR_KIND), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_COLLECTIVE, StereotypeUtils.STR_QUANTITY), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_COLLECTIVE, StereotypeUtils.STR_COLLECTIVE), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_COLLECTIVE, StereotypeUtils.STR_RELATOR), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_COLLECTIVE, StereotypeUtils.STR_MODE), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_COLLECTIVE, StereotypeUtils.STR_QUALITY), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_COLLECTIVE, StereotypeUtils.STR_SUBKIND), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_COLLECTIVE, StereotypeUtils.STR_ROLE), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_COLLECTIVE, StereotypeUtils.STR_PHASE), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_COLLECTIVE, StereotypeUtils.STR_CATEGORY), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_COLLECTIVE, StereotypeUtils.STR_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_COLLECTIVE, StereotypeUtils.STR_ROLE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_COLLECTIVE, StereotypeUtils.STR_PHASE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_COLLECTIVE, StereotypeUtils.STR_HISTORICAL_ROLE), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.HISTORICAL_DEPENDENCE)));
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_COLLECTIVE, StereotypeUtils.STR_EVENT), new ArrayList<>());
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_COLLECTIVE, StereotypeUtils.STR_TYPE), new ArrayList<>(Arrays.asList(ActionIds.INSTANTIATION)));
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_COLLECTIVE, StereotypeUtils.STR_DATATYPE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_COLLECTIVE, StereotypeUtils.STR_ENUMERATION), new ArrayList<>());
		
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_DATATYPE, StereotypeUtils.STR_KIND), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_DATATYPE, StereotypeUtils.STR_QUANTITY), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_DATATYPE, StereotypeUtils.STR_COLLECTIVE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_DATATYPE, StereotypeUtils.STR_RELATOR), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_DATATYPE, StereotypeUtils.STR_MODE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_DATATYPE, StereotypeUtils.STR_QUALITY), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_DATATYPE, StereotypeUtils.STR_SUBKIND), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_DATATYPE, StereotypeUtils.STR_ROLE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_DATATYPE, StereotypeUtils.STR_PHASE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_DATATYPE, StereotypeUtils.STR_CATEGORY), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_DATATYPE, StereotypeUtils.STR_MIXIN), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_DATATYPE, StereotypeUtils.STR_ROLE_MIXIN), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_DATATYPE, StereotypeUtils.STR_PHASE_MIXIN), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_DATATYPE, StereotypeUtils.STR_HISTORICAL_ROLE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_DATATYPE, StereotypeUtils.STR_EVENT), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_DATATYPE, StereotypeUtils.STR_TYPE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_DATATYPE, StereotypeUtils.STR_DATATYPE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_DATATYPE, StereotypeUtils.STR_ENUMERATION), new ArrayList<>());
		
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ENUMERATION, StereotypeUtils.STR_KIND), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ENUMERATION, StereotypeUtils.STR_QUANTITY), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ENUMERATION, StereotypeUtils.STR_COLLECTIVE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ENUMERATION, StereotypeUtils.STR_RELATOR), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ENUMERATION, StereotypeUtils.STR_MODE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ENUMERATION, StereotypeUtils.STR_QUALITY), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ENUMERATION, StereotypeUtils.STR_SUBKIND), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ENUMERATION, StereotypeUtils.STR_ROLE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ENUMERATION, StereotypeUtils.STR_PHASE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ENUMERATION, StereotypeUtils.STR_CATEGORY), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ENUMERATION, StereotypeUtils.STR_MIXIN), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ENUMERATION, StereotypeUtils.STR_ROLE_MIXIN), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ENUMERATION, StereotypeUtils.STR_PHASE_MIXIN), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ENUMERATION, StereotypeUtils.STR_HISTORICAL_ROLE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ENUMERATION, StereotypeUtils.STR_EVENT), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ENUMERATION, StereotypeUtils.STR_TYPE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ENUMERATION, StereotypeUtils.STR_DATATYPE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ENUMERATION, StereotypeUtils.STR_ENUMERATION), new ArrayList<>());
		
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_EVENT, StereotypeUtils.STR_KIND), new ArrayList<>(Arrays.asList(ActionIds.PARTICIPATION, ActionIds.CREATION, ActionIds.TERMINATION)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_EVENT, StereotypeUtils.STR_QUANTITY), new ArrayList<>(Arrays.asList(ActionIds.PARTICIPATION, ActionIds.CREATION, ActionIds.TERMINATION)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_EVENT, StereotypeUtils.STR_COLLECTIVE), new ArrayList<>(Arrays.asList(ActionIds.PARTICIPATION, ActionIds.CREATION, ActionIds.TERMINATION)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_EVENT, StereotypeUtils.STR_RELATOR), new ArrayList<>(Arrays.asList(ActionIds.CREATION, ActionIds.TERMINATION, ActionIds.MANIFESTATION)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_EVENT, StereotypeUtils.STR_MODE), new ArrayList<>(Arrays.asList(ActionIds.CREATION, ActionIds.TERMINATION, ActionIds.MANIFESTATION)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_EVENT, StereotypeUtils.STR_QUALITY), new ArrayList<>(Arrays.asList(ActionIds.CREATION, ActionIds.TERMINATION, ActionIds.MANIFESTATION)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_EVENT, StereotypeUtils.STR_SUBKIND), new ArrayList<>(Arrays.asList(ActionIds.PARTICIPATION, ActionIds.CREATION, ActionIds.TERMINATION, ActionIds.MANIFESTATION)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_EVENT, StereotypeUtils.STR_ROLE), new ArrayList<>(Arrays.asList(ActionIds.PARTICIPATION, ActionIds.CREATION, ActionIds.TERMINATION, ActionIds.MANIFESTATION)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_EVENT, StereotypeUtils.STR_PHASE), new ArrayList<>(Arrays.asList(ActionIds.PARTICIPATION, ActionIds.CREATION, ActionIds.TERMINATION, ActionIds.MANIFESTATION)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_EVENT, StereotypeUtils.STR_CATEGORY), new ArrayList<>(Arrays.asList(ActionIds.PARTICIPATION, ActionIds.CREATION, ActionIds.TERMINATION, ActionIds.MANIFESTATION)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_EVENT, StereotypeUtils.STR_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.PARTICIPATION, ActionIds.CREATION, ActionIds.TERMINATION, ActionIds.MANIFESTATION)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_EVENT, StereotypeUtils.STR_ROLE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.PARTICIPATION, ActionIds.CREATION, ActionIds.TERMINATION, ActionIds.MANIFESTATION)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_EVENT, StereotypeUtils.STR_PHASE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.PARTICIPATION, ActionIds.CREATION, ActionIds.TERMINATION, ActionIds.MANIFESTATION)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_EVENT, StereotypeUtils.STR_HISTORICAL_ROLE), new ArrayList<>(Arrays.asList(ActionIds.PARTICIPATION, ActionIds.CREATION, ActionIds.TERMINATION, ActionIds.MANIFESTATION)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_EVENT, StereotypeUtils.STR_EVENT), new ArrayList<>(Arrays.asList(ActionIds.HISTORICAL_DEPENDENCE, ActionIds.PARTICIPATIONAL)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_EVENT, StereotypeUtils.STR_TYPE), new ArrayList<>(Arrays.asList(ActionIds.INSTANTIATION)));
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_EVENT, StereotypeUtils.STR_DATATYPE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_EVENT, StereotypeUtils.STR_ENUMERATION), new ArrayList<>());
		
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_HISTORICAL_ROLE, StereotypeUtils.STR_KIND), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_HISTORICAL_ROLE, StereotypeUtils.STR_QUANTITY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_HISTORICAL_ROLE, StereotypeUtils.STR_COLLECTIVE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_HISTORICAL_ROLE, StereotypeUtils.STR_RELATOR), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_HISTORICAL_ROLE, StereotypeUtils.STR_MODE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_HISTORICAL_ROLE, StereotypeUtils.STR_QUALITY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_HISTORICAL_ROLE, StereotypeUtils.STR_SUBKIND), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_HISTORICAL_ROLE, StereotypeUtils.STR_ROLE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_HISTORICAL_ROLE, StereotypeUtils.STR_PHASE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_HISTORICAL_ROLE, StereotypeUtils.STR_CATEGORY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_HISTORICAL_ROLE, StereotypeUtils.STR_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_HISTORICAL_ROLE, StereotypeUtils.STR_ROLE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_HISTORICAL_ROLE, StereotypeUtils.STR_PHASE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_HISTORICAL_ROLE, StereotypeUtils.STR_HISTORICAL_ROLE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_HISTORICAL_ROLE, StereotypeUtils.STR_EVENT), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_HISTORICAL_ROLE, StereotypeUtils.STR_TYPE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.INSTANTIATION)));
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_HISTORICAL_ROLE, StereotypeUtils.STR_DATATYPE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_HISTORICAL_ROLE, StereotypeUtils.STR_ENUMERATION), new ArrayList<>());
		
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_KIND, StereotypeUtils.STR_KIND), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.COMPONENT_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_KIND, StereotypeUtils.STR_QUANTITY), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_KIND, StereotypeUtils.STR_COLLECTIVE), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.MEMBER_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_KIND, StereotypeUtils.STR_RELATOR), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_KIND, StereotypeUtils.STR_MODE), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_KIND, StereotypeUtils.STR_QUALITY), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_KIND, StereotypeUtils.STR_SUBKIND), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_KIND, StereotypeUtils.STR_ROLE), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_KIND, StereotypeUtils.STR_PHASE), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_KIND, StereotypeUtils.STR_CATEGORY), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_KIND, StereotypeUtils.STR_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_KIND, StereotypeUtils.STR_ROLE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_KIND, StereotypeUtils.STR_PHASE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_KIND, StereotypeUtils.STR_HISTORICAL_ROLE), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.HISTORICAL_DEPENDENCE)));
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_KIND, StereotypeUtils.STR_EVENT), new ArrayList<>());
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_KIND, StereotypeUtils.STR_TYPE), new ArrayList<>(Arrays.asList(ActionIds.INSTANTIATION)));
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_KIND, StereotypeUtils.STR_DATATYPE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_KIND, StereotypeUtils.STR_ENUMERATION), new ArrayList<>());
		
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MIXIN, StereotypeUtils.STR_KIND), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MIXIN, StereotypeUtils.STR_QUANTITY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MIXIN, StereotypeUtils.STR_COLLECTIVE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MIXIN, StereotypeUtils.STR_RELATOR), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MIXIN, StereotypeUtils.STR_MODE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MIXIN, StereotypeUtils.STR_QUALITY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MIXIN, StereotypeUtils.STR_SUBKIND), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MIXIN, StereotypeUtils.STR_ROLE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MIXIN, StereotypeUtils.STR_PHASE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MIXIN, StereotypeUtils.STR_CATEGORY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MIXIN, StereotypeUtils.STR_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MIXIN, StereotypeUtils.STR_ROLE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MIXIN, StereotypeUtils.STR_PHASE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MIXIN, StereotypeUtils.STR_HISTORICAL_ROLE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MIXIN, StereotypeUtils.STR_EVENT), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MIXIN, StereotypeUtils.STR_TYPE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.INSTANTIATION)));
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MIXIN, StereotypeUtils.STR_DATATYPE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MIXIN, StereotypeUtils.STR_ENUMERATION), new ArrayList<>());
		
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MODE, StereotypeUtils.STR_KIND), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MODE, StereotypeUtils.STR_QUANTITY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MODE, StereotypeUtils.STR_COLLECTIVE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MODE, StereotypeUtils.STR_RELATOR), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MODE, StereotypeUtils.STR_MODE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MODE, StereotypeUtils.STR_QUALITY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MODE, StereotypeUtils.STR_SUBKIND), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MODE, StereotypeUtils.STR_ROLE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MODE, StereotypeUtils.STR_PHASE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MODE, StereotypeUtils.STR_CATEGORY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MODE, StereotypeUtils.STR_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MODE, StereotypeUtils.STR_ROLE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MODE, StereotypeUtils.STR_PHASE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MODE, StereotypeUtils.STR_HISTORICAL_ROLE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MODE, StereotypeUtils.STR_EVENT), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MODE, StereotypeUtils.STR_TYPE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.INSTANTIATION)));
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MODE, StereotypeUtils.STR_DATATYPE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_MODE, StereotypeUtils.STR_ENUMERATION), new ArrayList<>());
		
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE, StereotypeUtils.STR_KIND), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE, StereotypeUtils.STR_QUANTITY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE, StereotypeUtils.STR_COLLECTIVE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE, StereotypeUtils.STR_RELATOR), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE, StereotypeUtils.STR_MODE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE, StereotypeUtils.STR_QUALITY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE, StereotypeUtils.STR_SUBKIND), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE, StereotypeUtils.STR_ROLE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE, StereotypeUtils.STR_PHASE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE, StereotypeUtils.STR_CATEGORY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE, StereotypeUtils.STR_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE, StereotypeUtils.STR_ROLE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE, StereotypeUtils.STR_PHASE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE, StereotypeUtils.STR_HISTORICAL_ROLE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE, StereotypeUtils.STR_EVENT), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE, StereotypeUtils.STR_TYPE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.INSTANTIATION)));
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE, StereotypeUtils.STR_DATATYPE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE, StereotypeUtils.STR_ENUMERATION), new ArrayList<>());
		
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE_MIXIN, StereotypeUtils.STR_KIND), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE_MIXIN, StereotypeUtils.STR_QUANTITY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE_MIXIN, StereotypeUtils.STR_COLLECTIVE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE_MIXIN, StereotypeUtils.STR_RELATOR), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE_MIXIN, StereotypeUtils.STR_MODE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE_MIXIN, StereotypeUtils.STR_QUALITY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE_MIXIN, StereotypeUtils.STR_SUBKIND), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE_MIXIN, StereotypeUtils.STR_ROLE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE_MIXIN, StereotypeUtils.STR_PHASE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE_MIXIN, StereotypeUtils.STR_CATEGORY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE_MIXIN, StereotypeUtils.STR_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE_MIXIN, StereotypeUtils.STR_ROLE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE_MIXIN, StereotypeUtils.STR_PHASE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE_MIXIN, StereotypeUtils.STR_HISTORICAL_ROLE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE_MIXIN, StereotypeUtils.STR_EVENT), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE_MIXIN, StereotypeUtils.STR_TYPE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.PHASE_MIXIN)));
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE_MIXIN, StereotypeUtils.STR_DATATYPE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_PHASE_MIXIN, StereotypeUtils.STR_ENUMERATION), new ArrayList<>());
		
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUALITY, StereotypeUtils.STR_KIND), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUALITY, StereotypeUtils.STR_QUANTITY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUALITY, StereotypeUtils.STR_COLLECTIVE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUALITY, StereotypeUtils.STR_RELATOR), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUALITY, StereotypeUtils.STR_MODE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUALITY, StereotypeUtils.STR_QUALITY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUALITY, StereotypeUtils.STR_SUBKIND), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUALITY, StereotypeUtils.STR_ROLE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUALITY, StereotypeUtils.STR_PHASE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUALITY, StereotypeUtils.STR_CATEGORY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUALITY, StereotypeUtils.STR_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUALITY, StereotypeUtils.STR_ROLE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUALITY, StereotypeUtils.STR_PHASE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUALITY, StereotypeUtils.STR_HISTORICAL_ROLE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUALITY, StereotypeUtils.STR_EVENT), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUALITY, StereotypeUtils.STR_TYPE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE, ActionIds.INSTANTIATION)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUALITY, StereotypeUtils.STR_DATATYPE), new ArrayList<>(Arrays.asList(ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUALITY, StereotypeUtils.STR_ENUMERATION), new ArrayList<>(Arrays.asList(ActionIds.HISTORICAL_DEPENDENCE)));
		
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUANTITY, StereotypeUtils.STR_KIND), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUANTITY, StereotypeUtils.STR_QUANTITY), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUANTITY, StereotypeUtils.STR_COLLECTIVE), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUANTITY, StereotypeUtils.STR_RELATOR), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUANTITY, StereotypeUtils.STR_MODE), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUANTITY, StereotypeUtils.STR_QUALITY), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUANTITY, StereotypeUtils.STR_SUBKIND), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUANTITY, StereotypeUtils.STR_ROLE), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUANTITY, StereotypeUtils.STR_PHASE), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUANTITY, StereotypeUtils.STR_CATEGORY), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUANTITY, StereotypeUtils.STR_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUANTITY, StereotypeUtils.STR_ROLE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUANTITY, StereotypeUtils.STR_PHASE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUANTITY, StereotypeUtils.STR_HISTORICAL_ROLE), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUANTITY, StereotypeUtils.STR_EVENT), new ArrayList<>());
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUANTITY, StereotypeUtils.STR_TYPE), new ArrayList<>(Arrays.asList(ActionIds.INSTANTIATION)));
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUANTITY, StereotypeUtils.STR_DATATYPE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_QUANTITY, StereotypeUtils.STR_ENUMERATION), new ArrayList<>());
		
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_RELATOR, StereotypeUtils.STR_KIND), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_RELATOR, StereotypeUtils.STR_QUANTITY), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_RELATOR, StereotypeUtils.STR_COLLECTIVE), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_RELATOR, StereotypeUtils.STR_RELATOR), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_RELATOR, StereotypeUtils.STR_MODE), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_RELATOR, StereotypeUtils.STR_QUALITY), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_RELATOR, StereotypeUtils.STR_SUBKIND), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_RELATOR, StereotypeUtils.STR_ROLE), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_RELATOR, StereotypeUtils.STR_PHASE), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_RELATOR, StereotypeUtils.STR_CATEGORY), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_RELATOR, StereotypeUtils.STR_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_RELATOR, StereotypeUtils.STR_ROLE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_RELATOR, StereotypeUtils.STR_PHASE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_RELATOR, StereotypeUtils.STR_HISTORICAL_ROLE), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_RELATOR, StereotypeUtils.STR_EVENT), new ArrayList<>(Arrays.asList(ActionIds.MATERIAL, ActionIds.MEDIATION)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_RELATOR, StereotypeUtils.STR_TYPE), new ArrayList<>(Arrays.asList(ActionIds.COMPARATIVE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE, ActionIds.INSTANTIATION)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_RELATOR, StereotypeUtils.STR_DATATYPE), new ArrayList<>(Arrays.asList(ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_RELATOR, StereotypeUtils.STR_ENUMERATION), new ArrayList<>(Arrays.asList(ActionIds.HISTORICAL_DEPENDENCE)));
		
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE, StereotypeUtils.STR_KIND), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE, StereotypeUtils.STR_QUANTITY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE, StereotypeUtils.STR_COLLECTIVE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE, StereotypeUtils.STR_RELATOR), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE, StereotypeUtils.STR_MODE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE, StereotypeUtils.STR_QUALITY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE, StereotypeUtils.STR_SUBKIND), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE, StereotypeUtils.STR_ROLE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE, StereotypeUtils.STR_PHASE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE, StereotypeUtils.STR_CATEGORY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE, StereotypeUtils.STR_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE, StereotypeUtils.STR_ROLE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE, StereotypeUtils.STR_PHASE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE, StereotypeUtils.STR_HISTORICAL_ROLE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE, StereotypeUtils.STR_EVENT), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE, StereotypeUtils.STR_TYPE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.INSTANTIATION)));
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE, StereotypeUtils.STR_DATATYPE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE, StereotypeUtils.STR_ENUMERATION), new ArrayList<>());
		
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE_MIXIN, StereotypeUtils.STR_KIND), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE_MIXIN, StereotypeUtils.STR_QUANTITY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE_MIXIN, StereotypeUtils.STR_COLLECTIVE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE_MIXIN, StereotypeUtils.STR_RELATOR), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE_MIXIN, StereotypeUtils.STR_MODE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE_MIXIN, StereotypeUtils.STR_QUALITY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE_MIXIN, StereotypeUtils.STR_SUBKIND), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE_MIXIN, StereotypeUtils.STR_ROLE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE_MIXIN, StereotypeUtils.STR_PHASE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE_MIXIN, StereotypeUtils.STR_CATEGORY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE_MIXIN, StereotypeUtils.STR_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE_MIXIN, StereotypeUtils.STR_ROLE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE_MIXIN, StereotypeUtils.STR_PHASE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE_MIXIN, StereotypeUtils.STR_HISTORICAL_ROLE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE_MIXIN, StereotypeUtils.STR_EVENT), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE_MIXIN, StereotypeUtils.STR_TYPE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.INSTANTIATION)));
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE_MIXIN, StereotypeUtils.STR_DATATYPE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_ROLE_MIXIN, StereotypeUtils.STR_ENUMERATION), new ArrayList<>());
		
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_SUBKIND, StereotypeUtils.STR_KIND), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_SUBKIND, StereotypeUtils.STR_QUANTITY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_SUBKIND, StereotypeUtils.STR_COLLECTIVE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_SUBKIND, StereotypeUtils.STR_RELATOR), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_SUBKIND, StereotypeUtils.STR_MODE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_SUBKIND, StereotypeUtils.STR_QUALITY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_SUBKIND, StereotypeUtils.STR_SUBKIND), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_SUBKIND, StereotypeUtils.STR_ROLE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_SUBKIND, StereotypeUtils.STR_PHASE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_SUBKIND, StereotypeUtils.STR_CATEGORY), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_SUBKIND, StereotypeUtils.STR_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_SUBKIND, StereotypeUtils.STR_ROLE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_SUBKIND, StereotypeUtils.STR_PHASE_MIXIN), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_SUBKIND, StereotypeUtils.STR_HISTORICAL_ROLE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.COMPONENT_OF, ActionIds.MEMBER_OF, ActionIds.SUB_COLLECTION_OF, ActionIds.SUB_QUANTITY_OF, ActionIds.HISTORICAL_DEPENDENCE)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_SUBKIND, StereotypeUtils.STR_EVENT), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION)));
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_SUBKIND, StereotypeUtils.STR_TYPE), new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.MEDIATION, ActionIds.INSTANTIATION)));
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_SUBKIND, StereotypeUtils.STR_DATATYPE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_SUBKIND, StereotypeUtils.STR_ENUMERATION), new ArrayList<>());
		
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_TYPE, StereotypeUtils.STR_KIND), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_TYPE, StereotypeUtils.STR_QUANTITY), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_TYPE, StereotypeUtils.STR_COLLECTIVE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_TYPE, StereotypeUtils.STR_RELATOR), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_TYPE, StereotypeUtils.STR_MODE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_TYPE, StereotypeUtils.STR_QUALITY), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_TYPE, StereotypeUtils.STR_SUBKIND), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_TYPE, StereotypeUtils.STR_ROLE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_TYPE, StereotypeUtils.STR_PHASE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_TYPE, StereotypeUtils.STR_CATEGORY), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_TYPE, StereotypeUtils.STR_MIXIN), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_TYPE, StereotypeUtils.STR_ROLE_MIXIN), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_TYPE, StereotypeUtils.STR_PHASE_MIXIN), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_TYPE, StereotypeUtils.STR_HISTORICAL_ROLE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_TYPE, StereotypeUtils.STR_EVENT), new ArrayList<>());
		put(new SimpleEntry<String, String>(StereotypeUtils.STR_TYPE, StereotypeUtils.STR_TYPE), new ArrayList<>(Arrays.asList(ActionIds.INSTANTIATION)));
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_TYPE, StereotypeUtils.STR_DATATYPE), new ArrayList<>());
//		put(new SimpleEntry<String, String>(StereotypeUtils.STR_TYPE, StereotypeUtils.STR_ENUMERATION), new ArrayList<>());
		}
	};
	*/
	

}
