package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;
import com.vp.plugin.model.ITaggedValueDefinition;
import com.vp.plugin.model.ITaggedValueDefinitionContainer;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

public class ReloadClassesActionController implements VPActionController {

   private IClass _class;

   @Override
   public void performAction(VPAction action) {
      OntoUMLPlugin.reload();

      final IModelElementFactory factory = 
            IModelElementFactory.instance();
      
      if(_class == null) {
         _class = factory.createClass();
      }

      StereotypeUtils.generate();
      IStereotype _type = StereotypeUtils
            .STEREOTYPE_ELEMENTS.get(StereotypeUtils.STR_TYPE);
      
      _class.addStereotype(_type);

      ITaggedValueContainer container = _class.getTaggedValues();

      if(container == null) {
         container = factory.createTaggedValueContainer();
         _class.setTaggedValues(container);
      }

      final String propertyName = 
            StereotypeUtils.PROPERTY_IS_POWERTYPE;
      ITaggedValue value = container
            .getTaggedValueByName(propertyName);
      
      if(value == null) {
         ITaggedValueDefinitionContainer definitionContainer =
               _type.getTaggedValueDefinitions();
         ITaggedValueDefinition definition =
               definitionContainer
               .getTaggedValueDefinitionByName(propertyName);

         value = container.createTaggedValue();
         value.setTagDefinition(definition);
      }

      final boolean valueAsBoolean = Boolean
            .parseBoolean(value.getValueAsString());
      value.setValue(!valueAsBoolean);

      container.removeTaggedValue(value);
      _class.addStereotype(_type);

      value = container
            .getTaggedValueByName(propertyName);
      
      if(value == null) {
         ITaggedValueDefinitionContainer definitionContainer =
               _type.getTaggedValueDefinitions();
         ITaggedValueDefinition definition =
               definitionContainer
               .getTaggedValueDefinitionByName(propertyName);

         value = container.createTaggedValue();
         value.setTagDefinition(definition);
      }

      value.setValue(!valueAsBoolean);
   }

   @Override
   public void update(VPAction action) {
   }

}