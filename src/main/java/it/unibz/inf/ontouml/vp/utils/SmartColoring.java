package it.unibz.inf.ontouml.vp.utils;

import java.awt.Color;

import com.vp.plugin.action.VPContext;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.ISimpleRelationship;

public class SmartColoring {

	// Default colors
	public static final Color COLOR_TYPE = new Color(211, 211, 252);
	public static final Color COLOR_POWERTYPE = new Color(211, 211, 252);

	public static final Color COLOR_EVENT = new Color(252, 252, 212);

	public static final Color COLOR_ENUMERATION = new Color(255, 255, 255);
	public static final Color COLOR_DATATYPE = new Color(255, 255, 255);

	public static final Color COLOR_FUNCTIONAL_COMPLEX_KIND = new Color(255, 218, 221);
	public static final Color COLOR_COLLECTIVE = new Color(255, 218, 221);
	public static final Color COLOR_QUANTITY = new Color(255, 218, 221);
	public static final Color COLOR_RELATOR = new Color(211, 255, 211);
	public static final Color COLOR_MODE = new Color(192, 237, 255);
	public static final Color COLOR_QUALITY = new Color(192, 237, 255);

	public static final Color COLOR_FUNCTIONAL_COMPLEX_SORTAL = new Color(255, 218, 221);
	public static final Color COLOR_COLLECTIVE_SORTAL = new Color(255, 218, 221);
	public static final Color COLOR_QUANTITY_SORTAL = new Color(255, 218, 221);
	public static final Color COLOR_RELATOR_SORTAL = new Color(211, 255, 211);
	public static final Color COLOR_MODE_SORTAL = new Color(192, 237, 255);
	public static final Color COLOR_QUALITY_SORTAL = new Color(192, 237, 255);

	public static final Color COLOR_NON_SORTAL = new Color(224, 224, 224);

	/**
	 * 
	 * Paints the assigned diagram element with the assigned color. No effect
	 * whenever auto-coloring is disabled or color is <code>null</code>.
	 * 
	 * @param diagramElement
	 * @param color
	 */
	public static void paint(IClass _class) {
		if (!Configurations.getInstance().getProjectConfigurations().isAutomaticColoringEnabled())
			return;
		

		final String[] stereotypes = _class.toStereotypeArray();
		
		if(stereotypes == null || stereotypes.length == 0)
			return;
		
		
		for(int i = 0; stereotypes != null && i < stereotypes.length; i++) {
			String stereotype = stereotypes[i];
			
			switch(stereotype) {
			case StereotypeUtils.STR_TYPE:
				setColor(_class, COLOR_TYPE);
				break;
			case StereotypeUtils.STR_EVENT:
				setColor(_class, COLOR_EVENT);
				break;
			case StereotypeUtils.STR_ENUMERATION:
				setColor(_class, COLOR_ENUMERATION);
				break;
			case StereotypeUtils.STR_DATATYPE:
				setColor(_class, COLOR_DATATYPE);
				break;
			case StereotypeUtils.STR_RELATOR:
				setColor(_class, COLOR_RELATOR);
				break;
			case StereotypeUtils.STR_QUANTITY:
				setColor(_class, COLOR_QUANTITY);
				break;
			case StereotypeUtils.STR_QUALITY:
				setColor(_class, COLOR_QUALITY);
				break;
			case StereotypeUtils.STR_MODE:
				setColor(_class, COLOR_MODE);
				break;
			case StereotypeUtils.STR_KIND:
				setColor(_class, COLOR_FUNCTIONAL_COMPLEX_KIND);
				break;
			case StereotypeUtils.STR_COLLECTIVE:
				setColor(_class, COLOR_COLLECTIVE);
				break;
			case StereotypeUtils.STR_CATEGORY:
				setColor(_class, COLOR_NON_SORTAL);
				break;
			default:
				setColor(_class, getSpecializedColor(_class));
				break;
			}
		}	

	}

	private static void setColor(IClass _class, Color color) {

		final IDiagramElement[] diagramElements = _class.getDiagramElements();

		for (int i = 0; diagramElements != null && i < diagramElements.length; i++) {
			IDiagramElement diagramElement = diagramElements[i];
			if (diagramElement instanceof IShapeUIModel) {
				((IShapeUIModel) diagramElement).getFillColor().setColor1(color);
			} else {
				diagramElement.setForeground(color);
			}
		}

	}

	/**
	 * 
	 * Returns first sortal color occurring on one of the generalizations of this
	 * class. If generalization with such color is found, returns <code>null</code>.
	 * 
	 * @param context
	 * 
	 */
	public static Color getSpecializedColor(IClass _class) {
		final ISimpleRelationship[] specializations = _class.toToRelationshipArray();
		
		if(specializations==null)
			return COLOR_NON_SORTAL;

		for (int i = 0; specializations != null && i < specializations.length; i++) {
			if (!(specializations[i] instanceof IGeneralization)) {
				continue;
			}

			final IModelElement superClass = specializations[i].getFrom();
			final IDiagramElement[] superDiagramElements = superClass.getDiagramElements();

			for (int j = 0; j < superDiagramElements.length; j++) {
				if (!(superDiagramElements[j] instanceof IShapeUIModel)) {
					continue;
				}

				final Color superColor = ((IShapeUIModel) superDiagramElements[j]).getFillColor().getColor1();

				if (superColor.equals(COLOR_FUNCTIONAL_COMPLEX_KIND)
						|| superColor.equals(COLOR_FUNCTIONAL_COMPLEX_SORTAL)) {
					return COLOR_FUNCTIONAL_COMPLEX_SORTAL;
				} else if (superColor.equals(COLOR_COLLECTIVE) || superColor.equals(COLOR_COLLECTIVE_SORTAL)) {
					return COLOR_COLLECTIVE_SORTAL;
				} else if (superColor.equals(COLOR_QUANTITY) || superColor.equals(COLOR_QUANTITY_SORTAL)) {
					return COLOR_QUANTITY_SORTAL;
				} else if (superColor.equals(COLOR_RELATOR) || superColor.equals(COLOR_RELATOR_SORTAL)) {
					return COLOR_RELATOR_SORTAL;
				} else if (superColor.equals(COLOR_MODE) || superColor.equals(COLOR_MODE_SORTAL)) {
					return COLOR_MODE_SORTAL;
				} else if (superColor.equals(COLOR_QUALITY) || superColor.equals(COLOR_QUALITY_SORTAL)) {
					return COLOR_QUALITY_SORTAL;
				}
			}
		}
		return COLOR_NON_SORTAL;
	}
}
