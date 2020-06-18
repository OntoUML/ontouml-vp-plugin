package it.unibz.inf.ontouml.vp.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IShapeUIModel;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;

/**
 * Implementation of the coloring feature
 *
 * @author Victor Viola
 * @author Claudenir Fonseca
 * @author Tiago Prince Sales
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

   public static final Color COLOR_FOR_NATURE_ABSTRACT = new Color(255, 255, 255);
   public static final Color COLOR_FOR_NATURE_COLLECTIVE = new Color(255, 218, 221);
   public static final Color COLOR_FOR_NATURE_EVENT = new Color(252, 252, 212);
   public static final Color COLOR_FOR_NATURE_MODE = new Color(192, 237, 255);
   public static final Color COLOR_FOR_NATURE_FUNCTIONAL_COMPLEX = new Color(255, 218, 221);
   public static final Color COLOR_FOR_NATURE_QUALITY = new Color(192, 237, 255);
   public static final Color COLOR_FOR_NATURE_QUANTITY = new Color(255, 218, 221);
   public static final Color COLOR_FOR_NATURE_RELATOR = new Color(211, 255, 211);
   public static final Color COLOR_FOR_NATURE_TYPE = new Color(211, 211, 252);
   public static final Color COLOR_FOR_NON_SPECIFIC = new Color(224, 224, 224);

   /**
    * Runs twice over the diagram and paint all the elements.
    */
   public static void paintAll() {
      if (!Configurations.getInstance().getProjectConfigurations().isAutomaticColoringEnabled())
         return;

      final IProject project = ApplicationManager.instance().getProjectManager().getProject();
      final IModelElement[] modelElements = project
              .toAllLevelModelElementArray(IModelElementFactory.MODEL_TYPE_CLASS);

//       TODO: this method was running twice because it must guarantee that all ultimate
//       sortals were painted. Improve the code by not relying on the color of classes
//       related by generalization.
//      for (int i = 0; i <= 1; i++)

      for (int j = 0; modelElements != null && j < modelElements.length; j++)
         SmartColoring.paint((IClass) modelElements[j]);
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

      if (defaultColor == null)
         return;

      for (IDiagramElement classView : _class.getDiagramElements()) {
         if (classView instanceof IShapeUIModel)
            ((IShapeUIModel) classView).getFillColor().setColor1(defaultColor);
         else
            classView.setForeground(defaultColor);
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
      ITaggedValue taggedValue = StereotypeUtils.getTaggedValue(_class, StereotypeUtils.PROPERTY_RESTRICTED_TO);

      if (taggedValue == null)
         return null;

      String restrictedTo = taggedValue.getValueAsString();

      switch (restrictedTo) {
         case StereotypeUtils.NATURE_ABSTRACT:
            return COLOR_FOR_NATURE_ABSTRACT;
         case StereotypeUtils.NATURE_COLLECTIVE:
            return COLOR_FOR_NATURE_COLLECTIVE;
         case StereotypeUtils.NATURE_EVENT:
            return COLOR_FOR_NATURE_EVENT;
         case StereotypeUtils.NATURE_MODE:
            return COLOR_FOR_NATURE_MODE;
         case StereotypeUtils.NATURE_FUNCTIONAL_COMPLEX:
            return COLOR_FOR_NATURE_FUNCTIONAL_COMPLEX;
         case StereotypeUtils.NATURE_QUALITY:
            return COLOR_FOR_NATURE_QUALITY;
         case StereotypeUtils.NATURE_QUANTITY:
            return COLOR_FOR_NATURE_QUANTITY;
         case StereotypeUtils.NATURE_RELATOR:
            return COLOR_FOR_NATURE_RELATOR;
         case StereotypeUtils.NATURE_TYPE:
            return COLOR_FOR_NATURE_TYPE;
      }

      if (restrictedTo == null || restrictedTo.isEmpty())
         return COLOR_FOR_NON_SPECIFIC;

      List<String> restrictedToList =
              Arrays.stream(restrictedTo.split(","))
                      .map(s -> s.trim())
                      .collect(Collectors.toList());

      List<String> objectNatures =
              Arrays.asList(StereotypeUtils.NATURE_FUNCTIONAL_COMPLEX, StereotypeUtils.NATURE_COLLECTIVE, StereotypeUtils.NATURE_QUANTITY);

      if (objectNatures.containsAll(restrictedToList))
         return COLOR_FOR_NATURE_FUNCTIONAL_COMPLEX;

      List<String> intrinsicNature = Arrays.asList(StereotypeUtils.NATURE_MODE, StereotypeUtils.NATURE_QUALITY);

      if (intrinsicNature.containsAll(restrictedToList))
         return COLOR_FOR_NATURE_MODE;

      return COLOR_FOR_NON_SPECIFIC;
   }

   /**
    * Infer color of the class based on its super classes.
    *
    * @param _class
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
    * Infer color of the mixins based on its specialized classes.
    *
    * @param _class
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
    * Get sortal color based on a given color.
    *
    * @param color
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


}
