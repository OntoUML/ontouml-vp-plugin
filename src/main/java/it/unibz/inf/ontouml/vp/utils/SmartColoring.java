package it.unibz.inf.ontouml.vp.utils;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.model.Class;

/**
 * Implementation of the coloring feature
 *
 * @author Victor Viola
 * @author Claudenir Fonseca
 * @author Tiago Prince Sales
 */
public class SmartColoring {

   public static final Color COLOR_FOR_ABSTRACT = new Color(255, 255, 255);
   public static final Color COLOR_FOR_COLLECTIVE = new Color(255, 218, 221);
   public static final Color COLOR_FOR_EVENT = new Color(252, 252, 212);
   public static final Color COLOR_FOR_MODE = new Color(192, 237, 255);
   public static final Color COLOR_FOR_FUNCTIONAL_COMPLEX = new Color(255, 218, 221);
   public static final Color COLOR_FOR_QUALITY = new Color(192, 237, 255);
   public static final Color COLOR_FOR_QUANTITY = new Color(255, 218, 221);
   public static final Color COLOR_FOR_RELATOR = new Color(211, 255, 211);
   public static final Color COLOR_FOR_TYPE = new Color(211, 211, 252);
   public static final Color COLOR_FOR_NON_SPECIFIC = new Color(224, 224, 224);

   /**
    * Runs twice over the diagram and paint all the elements.
    */
   public static void paintAll() {
      if (!Configurations.getInstance().getProjectConfigurations().isAutomaticColoringEnabled()){
         return;
      }

      final IProject project = ApplicationManager.instance().getProjectManager().getProject();
      final IModelElement[] modelElements = project
              .toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_CLASS);

      for (int j = 0; modelElements != null && j < modelElements.length; j++){
         SmartColoring.paint((IClass) modelElements[j]);
      }
   }

   /**
    * Paints occurrences of a class based on the "restrictTo" meta-property. Affects class occurrences in all diagrams.
    * No effect whenever auto-coloring is disabled or color is <code>null</code>.
    *
    * @param _class
    */
   public static void paint(IClass _class) {
      if (!Configurations.getInstance().getProjectConfigurations().isAutomaticColoringEnabled())
         return;

      final Color defaultColor = getColor(_class);

      if (defaultColor == null) {
         return;
      }

      for (IDiagramElement classView : _class.getDiagramElements()) {
         if (classView instanceof IClassUIModel) {
            ((IClassUIModel) classView).getFillColor().setColor1(defaultColor);
         }
      }
   }

   public static void paint(IClassUIModel classDiagramElement) {
		final IClass _class = classDiagramElement.getModelElement() instanceof IClass ? 
				(IClass) classDiagramElement.getModelElement() :
				null;
      final Color defaultColor = getColor(_class);

		if(defaultColor != null) { 
         classDiagramElement .getFillColor().setColor1(defaultColor);
      }
   }

   /**
    * Returns the color of a class based on its nature
    *
    * @param _class the class to be painted
    * @return the color of the class
    */
   private static Color getColor(IClass _class) {
      String restrictedTo = Class.getRestrictedTo(_class);

      if (restrictedTo == null || restrictedTo.isEmpty()){
         final String stereotype = 
               StereotypeUtils.getUniqueStereotypeName(_class);
         final Set<String> allStereotypes = 
               StereotypeUtils.getOntoUMLAssociationStereotypeNames();
         
         if(!allStereotypes.contains(stereotype)) {
            return null;
         } else {
            return COLOR_FOR_NON_SPECIFIC;
         }
      }

      switch (restrictedTo) {
         case StereotypeUtils.RESTRICTED_TO_ABSTRACT:
            return COLOR_FOR_ABSTRACT;
         case StereotypeUtils.RESTRICTED_TO_COLLECTIVE:
            return COLOR_FOR_COLLECTIVE;
         case StereotypeUtils.RESTRICTED_TO_EVENT:
            return COLOR_FOR_EVENT;
         case StereotypeUtils.RESTRICTED_TO_MODE:
            return COLOR_FOR_MODE;
         case StereotypeUtils.RESTRICTED_TO_FUNCTIONAL_COMPLEX:
            return COLOR_FOR_FUNCTIONAL_COMPLEX;
         case StereotypeUtils.RESTRICTED_TO_QUALITY:
            return COLOR_FOR_QUALITY;
         case StereotypeUtils.RESTRICTED_TO_QUANTITY:
            return COLOR_FOR_QUANTITY;
         case StereotypeUtils.RESTRICTED_TO_RELATOR:
            return COLOR_FOR_RELATOR;
         case StereotypeUtils.RESTRICTED_TO_TYPE:
            return COLOR_FOR_TYPE;
      }

      final List<String> restrictedToList = Arrays.stream(restrictedTo.split("\\s+"))
               .map(s -> s.trim())
               .collect(Collectors.toList());

      final List<String> objectNatures = Arrays.asList(
            StereotypeUtils.RESTRICTED_TO_FUNCTIONAL_COMPLEX,
            StereotypeUtils.RESTRICTED_TO_COLLECTIVE,
            StereotypeUtils.RESTRICTED_TO_QUANTITY);

      if (objectNatures.containsAll(restrictedToList)){
         return COLOR_FOR_FUNCTIONAL_COMPLEX;
      }

      final List<String> intrinsicNature = Arrays.asList(
            StereotypeUtils.RESTRICTED_TO_MODE,
            StereotypeUtils.RESTRICTED_TO_QUALITY);

      if (intrinsicNature.containsAll(restrictedToList)){
         return COLOR_FOR_MODE;
      }

      return COLOR_FOR_NON_SPECIFIC;
   }
}
