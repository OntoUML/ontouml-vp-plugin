package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.factory.IModelElementFactory;
import it.unibz.inf.ontouml.vp.model.uml.Association;
import it.unibz.inf.ontouml.vp.model.uml.Class;
import it.unibz.inf.ontouml.vp.model.uml.ModelElement;
import it.unibz.inf.ontouml.vp.utils.ApplicationManagerUtils;
import it.unibz.inf.ontouml.vp.utils.OntoUMLConstraintsManager;
import it.unibz.inf.ontouml.vp.utils.Stereotype;
import it.unibz.inf.ontouml.vp.utils.StereotypesManager;
import it.unibz.inf.ontouml.vp.utils.ViewManagerUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FixStereotypesController implements VPActionController {

  private boolean shouldProceed = false;
  private Set<IAttribute> attributes;
  private Set<IAssociation> associations;
  private Set<IClass> classes;
  private Map<String,String> associationStereotypesMap;
  private Map<String,String> attributeStereotypesMap;
  private Map<String,String> classStereotypesMap;

  @Override
  public void update(VPAction vpAction) {}

  @Override
  public void performAction(VPAction vpAction) {
    showFixStereotypesWarning();

    if(!shouldProceed) return ;

    retrieveModelElements();
    fixElementsStereotypes();
  }

  private void showFixStereotypesWarning() {
    shouldProceed = ViewManagerUtils.showFixStereotypesWarningDialog();
  }

  private void fixElementsStereotypes() {
    classes.forEach(this::fixElementsStereotypes);
    attributes.forEach(this::fixElementsStereotypes);
    associations.forEach(this::fixAssociationsStereotypes);
  }

  private void fixElementsStereotypes(IModelElement element) {
    final String stereotype = ModelElement.getUniqueStereotypeName(element);
    final String recognizedStr = getRecognizedStereotypes(element);

    if(hasReplacement(stereotype,recognizedStr))  applyStereotype(element, recognizedStr);
  }

  private void fixAssociationsStereotypes(IAssociation association) {
    final String stereotype = ModelElement.getUniqueStereotypeName(association);
    final String recognizedStr = getRecognizedStereotypes(association);

    if(hasReplacement(stereotype,recognizedStr))  applyStereotype(association, recognizedStr);
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
    if(shouldInvert(association, replacementStereotype))
      Association.invertAssociation(association,false);

    StereotypesManager.applyStereotype(association, replacementStereotype);
  }

  private boolean shouldInvert(IAssociation association, String stereotype) {
    return !OntoUMLConstraintsManager.isStereotypeAllowed(association,stereotype) &&
        OntoUMLConstraintsManager.isStereotypeAllowedIfInverted(association,stereotype);
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

  private void retrieveModelElements() {
    retrieveClasses();
    retrieveAssociations();
    retrieveAttributes();
  }

  private void retrieveAssociations() {
    associations = new HashSet<>();
    final Iterator<?> iter =
        ApplicationManagerUtils.getAllLevelModelElements(IModelElementFactory.MODEL_TYPE_ASSOCIATION);

    while(iter != null && iter.hasNext()) {
      associations.add((IAssociation) iter.next());
    }
  }

  private void retrieveAttributes() {
    attributes = new HashSet<>();
    classes.stream()
        .flatMap(c -> Class.getAttributes(c).stream())
        .forEach(attributes::add);
  }

  private void retrieveClasses() {
    classes = new HashSet<>();
    final Iterator<?> iter =
        ApplicationManagerUtils.getAllLevelModelElements(IModelElementFactory.MODEL_TYPE_CLASS);

    while(iter != null && iter.hasNext()) {
      classes.add((IClass) iter.next());
    }
  }
}
