package it.unibz.inf.ontouml.vp.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.ISimpleRelationship;
import com.vp.plugin.model.factory.IModelElementFactory;

/**
 * 
 * Implementation of the coloring feature
 * 
 * @author Victor Viola
 * @author Claudenir Fonseca
 * @author Tiago Prince Sales
 *
 */
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
	public static final Color COLOR_TYPE_SORTAL = new Color(211, 211, 252);

	public static final Color COLOR_UNKNOWN = new Color(224, 224, 224);

	/**
	 * 
	 * Defines and paint the color of the diagram element. No effect whenever auto-coloring is disabled or color is <code>null</code>.
	 * 
	 * @param _class
	 */
	public static void paint(IClass _class) {
		if (!Configurations.getInstance().getProjectConfigurations().isAutomaticColoringEnabled())
			return;

		final String[] stereotypes = _class.toStereotypeArray();

		// I changed this check so that the smart-paint does not run if a class has more than 1 stereotype.
		if (stereotypes == null || stereotypes.length != 1) {
			setColor(_class, COLOR_UNKNOWN);
			return;
		}

		String stereotype = stereotypes[0];

		switch (stereotype) {
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
		case StereotypeUtils.STR_MIXIN:
		case StereotypeUtils.STR_ROLE_MIXIN:
		case StereotypeUtils.STR_PHASE_MIXIN:
			setColor(_class, inferColorBasedOnSubClasses(_class));
			break;
		case StereotypeUtils.STR_ROLE:
		case StereotypeUtils.STR_SUBKIND:
		case StereotypeUtils.STR_PHASE:
		case StereotypeUtils.STR_HISTORICAL_ROLE:
			setColor(_class, inferColorBasedSuperClasses(_class));
			break;
		}

	}

	/**
	 * 
	 * Paints the assigned diagram element with the assigned color.
	 * 
	 * @param _class
	 */

	public static void setColor(IClass _class, Color color) {

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
	 * Infer color of the class based on its super classes.
	 * 
	 * @param _class
	 * 
	 */
	public static Color inferColorBasedSuperClasses(IClass _class) {
		final ISimpleRelationship[] relationshipsAsTarget = _class.toToRelationshipArray();
		final ArrayList<Color> superClassesColors = new ArrayList<Color>();

		for (int i = 0; relationshipsAsTarget != null && i < relationshipsAsTarget.length; i++) {
			final ISimpleRelationship relationship = relationshipsAsTarget[i];
			final String relationshipType = relationship.getModelType();
			final String superClassType = relationship.getFrom() != null ? relationship.getFrom().getModelType() : "";

			if (!(relationshipType.equals(IModelElementFactory.MODEL_TYPE_GENERALIZATION))
					|| !(superClassType.equals(IModelElementFactory.MODEL_TYPE_CLASS))) {
				continue;
			}

			final IClass superClass = (IClass) relationshipsAsTarget[i].getFrom();
			final IDiagramElement[] superClassShapes = superClass.getDiagramElements();

			for (int j = 0; j < superClassShapes.length; j++) {
				if (!(superClassShapes[j] instanceof IShapeUIModel)) {
					continue;
				}

				final Color superClassColor = ((IShapeUIModel) superClassShapes[j]).getFillColor().getColor1();

				if (!getSortalColor(superClassColor).equals(COLOR_UNKNOWN) && !superClassesColors.contains(getSortalColor(superClassColor))) {
					superClassesColors.add(getSortalColor(superClassColor));
				}
			}
		}

		return superClassesColors.size() == 1 ? superClassesColors.get(0) : COLOR_UNKNOWN;
	}

	/**
	 * 
	 * Infer color of the mixins based on its specialized classes.
	 * 
	 * @param _class
	 * 
	 */
	public static Color inferColorBasedOnSubClasses(IClass _class) {
		final ISimpleRelationship[] relationshipsAsSource = _class.toFromRelationshipArray();
		final ArrayList<Color> subClassesColors = new ArrayList<Color>();

		if (relationshipsAsSource == null)
			return COLOR_UNKNOWN;

		for (int i = 0; relationshipsAsSource != null && i < relationshipsAsSource.length; i++) {
			final ISimpleRelationship relationship = relationshipsAsSource[i];
			final String relationshipType = relationship.getModelType();
			final String subClassType = relationship.getTo() != null ? relationship.getTo().getModelType() : "";

			if (!(relationshipType.equals(IModelElementFactory.MODEL_TYPE_GENERALIZATION))
					|| !(subClassType.equals(IModelElementFactory.MODEL_TYPE_CLASS))) {
				continue;
			}

			final IClass subClass = (IClass) relationship.getTo();
			final IDiagramElement[] subClassShapes = subClass.getDiagramElements();

			for (int j = 0; j < subClassShapes.length; j++) {
				if (!(subClassShapes[j] instanceof IShapeUIModel)) {
					continue;
				}

				final Color subClassColor = ((IShapeUIModel) subClassShapes[j]).getFillColor().getColor1();

				if (!subClassesColors.contains(getSortalColor(subClassColor))) {
					subClassesColors.add(getSortalColor(subClassColor));
				}
			}
		}

		final List<Color> listWithoutDuplicates = subClassesColors.stream().distinct().collect(Collectors.toList());

		return listWithoutDuplicates.size() == 1 ? listWithoutDuplicates.get(0) : COLOR_UNKNOWN;
	}

	/**
	 * 
	 * Get sortal color based on a given color.
	 * 
	 * @param color
	 * 
	 */
	private static Color getSortalColor(Color color) {

		if (color.equals(COLOR_FUNCTIONAL_COMPLEX_KIND) || color.equals(COLOR_FUNCTIONAL_COMPLEX_SORTAL)) {
			return COLOR_FUNCTIONAL_COMPLEX_SORTAL;
		} else if (color.equals(COLOR_COLLECTIVE) || color.equals(COLOR_COLLECTIVE_SORTAL)) {
			return COLOR_COLLECTIVE_SORTAL;
		} else if (color.equals(COLOR_QUANTITY) || color.equals(COLOR_QUANTITY_SORTAL)) {
			return COLOR_QUANTITY_SORTAL;
		} else if (color.equals(COLOR_RELATOR) || color.equals(COLOR_RELATOR_SORTAL)) {
			return COLOR_RELATOR_SORTAL;
		} else if (color.equals(COLOR_MODE) || color.equals(COLOR_MODE_SORTAL)) {
			return COLOR_MODE_SORTAL;
		} else if (color.equals(COLOR_QUALITY) || color.equals(COLOR_QUALITY_SORTAL)) {
			return COLOR_QUALITY_SORTAL;
		} else if (color.equals(COLOR_TYPE)) {
			return COLOR_TYPE_SORTAL;
		}
		return COLOR_UNKNOWN;

	}

	// TODO: this method runs twice because it must guarantee that all ultimate
	// sortals were painted. Improve the code by not relying on the color of classes
	// related by generalization.
	/**
	 * Runs twice over the diagram and paint all the elements.
	 */
	public static void smartPaint() {
		final IProject project = ApplicationManager.instance().getProjectManager().getProject();
		final IModelElement[] modelElements = project
				.toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_CLASS);

		for (int i = 0; i <= 1; i++) {
			for (int j = 0; modelElements != null && j < modelElements.length; j++) {
				SmartColoring.paint((IClass) modelElements[j]);
			}
		}
	}

}
