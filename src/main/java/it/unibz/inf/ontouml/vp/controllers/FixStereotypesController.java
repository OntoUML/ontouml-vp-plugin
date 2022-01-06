package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.factory.IModelElementFactory;
import it.unibz.inf.ontouml.vp.model.uml.Class;
import it.unibz.inf.ontouml.vp.model.uml.ModelElement;
import it.unibz.inf.ontouml.vp.utils.ApplicationManagerUtils;
import it.unibz.inf.ontouml.vp.utils.Stereotype;
import it.unibz.inf.ontouml.vp.utils.StereotypesManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FixStereotypesController implements VPActionController {

  private static String[] typesSelection = { IModelElementFactory.MODEL_TYPE_CLASS,
      IModelElementFactory.MODEL_TYPE_ASSOCIATION };

  private Set<IModelElement> elements;
  private Map<String,String> associationStereotypesMap;
  private Map<String,String> attributeStereotypesMap;
  private Map<String,String> classStereotypesMap;

  @Override
  public void update(VPAction vpAction) {}

  @Override
  public void performAction(VPAction vpAction) {
    getModelElements();
    fixElementsStereotypes();
  }

  private void fixElementsStereotypes() {
    elements.forEach(this::fixElementsStereotypes);
  }

  private void fixElementsStereotypes(IModelElement element) {
    final String stereotype = ModelElement.getUniqueStereotypeName(element);
    final String recognizedStr = getRecognizedStereotypes(element);

    if(hasReplacement(stereotype,recognizedStr))  applyStereotype(element, recognizedStr);
  }

  private String getRecognizedStereotypes(IModelElement element) {
    final String normalizedStr = getNormalizedStereotype(element);
    final String type = element.getModelType();
    Map<String,String> stereotypesMap = getStereotypesMap(type);

    return stereotypesMap.get(normalizedStr);
  }

  private Map<String, String> getStereotypesMap(String modelType) {
    switch(modelType) {
      case IModelElementFactory.MODEL_TYPE_ASSOCIATION:
        return getAssociationStereotypesMap();
      case IModelElementFactory.MODEL_TYPE_ATTRIBUTE:
        return getAttributeStereotypesMap();
      case IModelElementFactory.MODEL_TYPE_CLASS:
        return getClassStereotypesMap();
      default:
        throw new RuntimeException("Unexpected model element of type '" + modelType + "'.");
    }
  }

  private void applyStereotype(IModelElement element, String replacementStereotype) {
    final String modelType = element.getModelType();

    switch(modelType) {
      case IModelElementFactory.MODEL_TYPE_ASSOCIATION:
        applyAssociationStereotype((IAssociation) element, replacementStereotype);
        break;
      case IModelElementFactory.MODEL_TYPE_ATTRIBUTE:
        applyAttributeStereotype((IAttribute) element, replacementStereotype);
        break;
      case IModelElementFactory.MODEL_TYPE_CLASS:
        applyClassStereotype((IClass) element, replacementStereotype);
        break;
      default:
        throw new RuntimeException("Unexpected model element of type '" + modelType + "'.");
    }
  }

  private void applyAttributeStereotype(IAttribute attribute, String replacementStereotype) {
    StereotypesManager.applyStereotype(attribute, replacementStereotype);
  }

  private void applyAssociationStereotype(IAssociation association, String replacementStereotype) {
    // TODO: check whether it is necessary to invert the association
    StereotypesManager.applyStereotype(association, replacementStereotype);
  }

  private void applyClassStereotype(IClass _class, String replacementStereotype) {
    StereotypesManager.applyStereotype(_class, replacementStereotype);
  }

  private boolean hasReplacement(String originalStereotype, String recognizedStereotype) {
    return recognizedStereotype != null
        && !recognizedStereotype.isEmpty()
        && !recognizedStereotype.equals(originalStereotype);
  }

  private String getNormalizedStereotype(String stereotype) {
    return stereotype != null ? stereotype.toLowerCase().replaceAll("\\s+","") : "";
  }

  private String getNormalizedStereotype(IModelElement element) {
    final String stereotype = ModelElement.getUniqueStereotypeName(element);
    return getNormalizedStereotype(stereotype);
  }

  private Map<String, String> getClassStereotypesMap() {
    if(classStereotypesMap == null || classStereotypesMap.isEmpty()) initializeClassStereotypeMap();
    return classStereotypesMap;
  }

  private Map<String, String> getAssociationStereotypesMap() {
    if(associationStereotypesMap == null || associationStereotypesMap.isEmpty()) initializeAssociationStereotypeMap();
    return associationStereotypesMap;
  }

  private Map<String, String> getAttributeStereotypesMap() {
    if(attributeStereotypesMap == null || attributeStereotypesMap.isEmpty()) initializeAttributeStereotypeMap();
    return attributeStereotypesMap;
  }

  private void initializeClassStereotypeMap() {
    classStereotypesMap = new HashMap<>();

    Stereotype.getOntoUMLClassStereotypeNames().forEach(str -> {
      String normalizedStr = getNormalizedStereotype(str);
      classStereotypesMap.put(normalizedStr, str);
    });

    // TODO: add a list of misspelled stereotypes to the map
  }

  private void initializeAssociationStereotypeMap() {
    associationStereotypesMap = new HashMap<>();

    Stereotype.getOntoUMLAssociationStereotypeNames().forEach(str -> {
      String normalizedStr = getNormalizedStereotype(str);
      associationStereotypesMap.put(normalizedStr, str);
    });

    // TODO: add a list of misspelled stereotypes to the map
  }

  private void initializeAttributeStereotypeMap() {
    attributeStereotypesMap = new HashMap<>();

    Stereotype.getOntoUMLAttributeStereotypeNames().forEach(str -> {
      String normalizedStr = getNormalizedStereotype(str);
      attributeStereotypesMap.put(normalizedStr, str);
    });

    // TODO: add a list of misspelled stereotypes to the map
  }

  private void getModelElements() {
    final Iterator<?> iter = ApplicationManagerUtils.getAllLevelModelElements(typesSelection);
    elements = new HashSet<>();

    while(iter != null && iter.hasNext()) {
      final IModelElement next = (IModelElement) iter.next();
      elements.add(next);
      if(Class.isClass(next)) getAttributes((IClass) next);
    }
  }

  private void getAttributes(IClass _class) {
    final Set<IAttribute> attributes = Class.getAttributes(_class);
    elements.addAll(attributes);
  }

}
