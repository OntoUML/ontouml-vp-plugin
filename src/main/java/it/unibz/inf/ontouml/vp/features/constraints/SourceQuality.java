package it.unibz.inf.ontouml.vp.features.constraints;

import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.vp.plugin.action.VPAction;

public class SourceQuality {
	// each ArrayList is a set of permitted associations when the source is Quality
	@SuppressWarnings("serial")
	public static final Map<String, ArrayList<String>> allowedRelationsMap = new HashMap<String, ArrayList<String>>() {
		{
			put(StereotypeUtils.STR_KIND, new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
			put(StereotypeUtils.STR_QUANTITY, new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
			put(StereotypeUtils.STR_COLLECTIVE, new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
			put(StereotypeUtils.STR_RELATOR, new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
			put(StereotypeUtils.STR_MODE, new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
			put(StereotypeUtils.STR_QUALITY, new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
			put(StereotypeUtils.STR_SUBKIND, new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
			put(StereotypeUtils.STR_ROLE, new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
			put(StereotypeUtils.STR_PHASE, new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
			put(StereotypeUtils.STR_CATEGORY, new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
			put(StereotypeUtils.STR_MIXIN, new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
			put(StereotypeUtils.STR_ROLE_MIXIN, new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
			put(StereotypeUtils.STR_PHASE_MIXIN, new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
			put(StereotypeUtils.STR_HISTORICAL_ROLE, new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE)));
			put(StereotypeUtils.STR_EVENT, new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL)));
			put(StereotypeUtils.STR_TYPE, new ArrayList<>(Arrays.asList(ActionIds.CHARACTERIZATION, ActionIds.COMPARATIVE, ActionIds.EXTERNAL_DEPENDENCE, ActionIds.MATERIAL, ActionIds.HISTORICAL_DEPENDENCE, ActionIds.INSTANTIATION)));
			put(StereotypeUtils.STR_DATATYPE, new ArrayList<>(Arrays.asList(ActionIds.HISTORICAL_DEPENDENCE)));
			put(StereotypeUtils.STR_ENUMERATION, new ArrayList<>(Arrays.asList(ActionIds.HISTORICAL_DEPENDENCE)));
		}
	};

	public static void setAction(VPAction action, String targetStereotype) {
		ArrayList<String> allowedRelations = allowedRelationsMap.get(targetStereotype);

		if (allowedRelations != null)
			setActionForTarget(action, allowedRelations);
		else
			action.setEnabled(false);
	}

	public static void setActionForTarget(VPAction action, ArrayList<String> target) {
		if (target.contains(action.getActionId()))
			action.setEnabled(true);
		else
			action.setEnabled(false);
	}
}
