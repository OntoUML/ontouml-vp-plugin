package it.unibz.inf.ontouml.vp.features.constraints;

import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ClassConstraints {

	@SuppressWarnings("serial")
	public static final Map<String, ArrayList<String>> allowedSubCombinations = new HashMap<String, ArrayList<String>>() {
		{
			put(StereotypeUtils.STR_KIND, new ArrayList<>(Arrays.asList(ActionIds.SUBKIND, ActionIds.ROLE, ActionIds.PHASE, ActionIds.HISTORICAL_ROLE)));
			put(StereotypeUtils.STR_QUANTITY, new ArrayList<>(Arrays.asList(ActionIds.SUBKIND, ActionIds.ROLE, ActionIds.PHASE, ActionIds.HISTORICAL_ROLE)));
			put(StereotypeUtils.STR_COLLECTIVE, new ArrayList<>(Arrays.asList(ActionIds.SUBKIND, ActionIds.ROLE, ActionIds.PHASE, ActionIds.HISTORICAL_ROLE)));
			put(StereotypeUtils.STR_RELATOR, new ArrayList<>(Arrays.asList(ActionIds.SUBKIND, ActionIds.ROLE, ActionIds.PHASE, ActionIds.HISTORICAL_ROLE)));
			put(StereotypeUtils.STR_MODE, new ArrayList<>(Arrays.asList(ActionIds.SUBKIND, ActionIds.ROLE, ActionIds.PHASE, ActionIds.HISTORICAL_ROLE)));
			put(StereotypeUtils.STR_QUALITY, new ArrayList<>(Arrays.asList(ActionIds.SUBKIND, ActionIds.ROLE, ActionIds.PHASE, ActionIds.HISTORICAL_ROLE)));
			put(StereotypeUtils.STR_SUBKIND, new ArrayList<>(Arrays.asList(ActionIds.SUBKIND, ActionIds.ROLE, ActionIds.PHASE, ActionIds.HISTORICAL_ROLE)));
			put(StereotypeUtils.STR_ROLE, new ArrayList<>(Arrays.asList(ActionIds.ROLE, ActionIds.HISTORICAL_ROLE)));
			put(StereotypeUtils.STR_PHASE, new ArrayList<>(Arrays.asList(ActionIds.ROLE, ActionIds.PHASE, ActionIds.HISTORICAL_ROLE)));
			put(StereotypeUtils.STR_CATEGORY, new ArrayList<>(Arrays.asList(ActionIds.KIND, ActionIds.QUANTITY, ActionIds.COLLECTIVE, ActionIds.RELATOR, ActionIds.MODE, ActionIds.QUALITY, ActionIds.SUBKIND, ActionIds.CATEGORY, ActionIds.ROLE_MIXIN, ActionIds.PHASE_MIXIN)));
			put(StereotypeUtils.STR_MIXIN, new ArrayList<>(Arrays.asList(ActionIds.KIND, ActionIds.QUANTITY, ActionIds.COLLECTIVE, ActionIds.RELATOR, ActionIds.MODE, ActionIds.QUALITY, ActionIds.SUBKIND, ActionIds.ROLE, ActionIds.PHASE, ActionIds.CATEGORY, ActionIds.MIXIN, ActionIds.ROLE_MIXIN, ActionIds.PHASE_MIXIN, ActionIds.HISTORICAL_ROLE)));
			put(StereotypeUtils.STR_ROLE_MIXIN, new ArrayList<>(Arrays.asList(ActionIds.ROLE, ActionIds.ROLE_MIXIN, ActionIds.HISTORICAL_ROLE)));
			put(StereotypeUtils.STR_PHASE_MIXIN, new ArrayList<>(Arrays.asList(ActionIds.PHASE, ActionIds.PHASE_MIXIN)));
			put(StereotypeUtils.STR_HISTORICAL_ROLE, new ArrayList<>(Arrays.asList(ActionIds.ROLE, ActionIds.HISTORICAL_ROLE)));
			put(StereotypeUtils.STR_EVENT, new ArrayList<>(Arrays.asList(ActionIds.EVENT)));
			put(StereotypeUtils.STR_TYPE, new ArrayList<>(Arrays.asList(ActionIds.ROLE, ActionIds.PHASE, ActionIds.HISTORICAL_ROLE, ActionIds.TYPE)));
			put(StereotypeUtils.STR_DATATYPE, new ArrayList<>(Arrays.asList(ActionIds.DATATYPE)));
			put(StereotypeUtils.STR_ENUMERATION, new ArrayList<>(Arrays.asList(ActionIds.ENUMERATION)));			
		}
	};
	
	@SuppressWarnings("serial")
	public static final Map<String, ArrayList<String>> allowedSuperCombinations = new HashMap<String, ArrayList<String>>() {
		{
			put(StereotypeUtils.STR_KIND, new ArrayList<>(Arrays.asList(ActionIds.CATEGORY, ActionIds.MIXIN)));
			put(StereotypeUtils.STR_QUANTITY, new ArrayList<>(Arrays.asList(ActionIds.CATEGORY, ActionIds.MIXIN)));
			put(StereotypeUtils.STR_COLLECTIVE, new ArrayList<>(Arrays.asList(ActionIds.CATEGORY, ActionIds.MIXIN)));
			put(StereotypeUtils.STR_RELATOR, new ArrayList<>(Arrays.asList(ActionIds.CATEGORY, ActionIds.MIXIN)));
			put(StereotypeUtils.STR_MODE, new ArrayList<>(Arrays.asList(ActionIds.CATEGORY, ActionIds.MIXIN)));
			put(StereotypeUtils.STR_QUALITY, new ArrayList<>(Arrays.asList(ActionIds.CATEGORY, ActionIds.MIXIN)));
			put(StereotypeUtils.STR_SUBKIND, new ArrayList<>(Arrays.asList(ActionIds.KIND, ActionIds.QUANTITY, ActionIds.COLLECTIVE, ActionIds.RELATOR, ActionIds.MODE, ActionIds.QUALITY, ActionIds.SUBKIND, ActionIds.CATEGORY, ActionIds.MIXIN)));
			// TODO: update StereotypeUtils.STR_ROLE
			put(StereotypeUtils.STR_ROLE, new ArrayList<>(Arrays.asList(ActionIds.KIND, ActionIds.QUANTITY, ActionIds.COLLECTIVE, ActionIds.RELATOR, ActionIds.MODE, ActionIds.QUALITY, ActionIds.SUBKIND, ActionIds.ROLE, ActionIds.PHASE, ActionIds.MIXIN, ActionIds.ROLE_MIXIN, ActionIds.HISTORICAL_ROLE, ActionIds.TYPE)));
			// TODO: update StereotypeUtils.STR_PHASE
			put(StereotypeUtils.STR_PHASE, new ArrayList<>(Arrays.asList(ActionIds.KIND, ActionIds.QUANTITY, ActionIds.COLLECTIVE, ActionIds.RELATOR, ActionIds.MODE, ActionIds.QUALITY, ActionIds.SUBKIND, ActionIds.PHASE, ActionIds.MIXIN, ActionIds.PHASE_MIXIN, ActionIds.TYPE)));
			put(StereotypeUtils.STR_CATEGORY, new ArrayList<>(Arrays.asList(ActionIds.CATEGORY, ActionIds.MIXIN)));
			put(StereotypeUtils.STR_MIXIN, new ArrayList<>(Arrays.asList(ActionIds.MIXIN)));
			// TODO: test StereotypeUtils.STR_ROLE_MIXIN
			put(StereotypeUtils.STR_ROLE_MIXIN, new ArrayList<>(Arrays.asList(ActionIds.CATEGORY, ActionIds.MIXIN, ActionIds.ROLE_MIXIN, ActionIds.PHASE_MIXIN, ActionIds.HISTORICAL_ROLE_MIXIN)));
			put(StereotypeUtils.STR_PHASE_MIXIN, new ArrayList<>(Arrays.asList(ActionIds.CATEGORY, ActionIds.MIXIN, ActionIds.PHASE_MIXIN)));
			// TODO: test StereotypeUtils.STR_HISTORICAL_ROLE
			put(StereotypeUtils.STR_HISTORICAL_ROLE, new ArrayList<>(Arrays.asList(ActionIds.KIND, ActionIds.QUANTITY, ActionIds.COLLECTIVE, ActionIds.RELATOR, ActionIds.MODE, ActionIds.QUALITY, ActionIds.SUBKIND, ActionIds.ROLE, ActionIds.PHASE, ActionIds.CATEGORY, ActionIds.MIXIN, ActionIds.ROLE_MIXIN, ActionIds.PHASE_MIXIN, ActionIds.HISTORICAL_ROLE, ActionIds.HISTORICAL_ROLE_MIXIN, ActionIds.TYPE)));
			// TODO: test StereotypeUtils.STR_HISTORICAL_ROLE_MIXIN
			put(StereotypeUtils.STR_HISTORICAL_ROLE_MIXIN, new ArrayList<>(Arrays.asList(ActionIds.CATEGORY, ActionIds.MIXIN, ActionIds.ROLE_MIXIN, ActionIds.PHASE_MIXIN, ActionIds.HISTORICAL_ROLE_MIXIN)));
			put(StereotypeUtils.STR_EVENT, new ArrayList<>(Arrays.asList(ActionIds.EVENT)));
			put(StereotypeUtils.STR_TYPE, new ArrayList<>(Arrays.asList(ActionIds.TYPE)));
			put(StereotypeUtils.STR_DATATYPE, new ArrayList<>(Arrays.asList(ActionIds.DATATYPE)));
			put(StereotypeUtils.STR_ENUMERATION, new ArrayList<>(Arrays.asList(ActionIds.ENUMERATION)));			
		}
	};
}