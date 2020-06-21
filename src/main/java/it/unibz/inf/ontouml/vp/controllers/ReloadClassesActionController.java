package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

import it.unibz.inf.ontouml.vp.OntoUMLPlugin;

public class ReloadClassesActionController implements VPActionController {

   @Override
   public void performAction(VPAction action) {
      OntoUMLPlugin.reload();
   }

   @Override
   public void update(VPAction action) {
   }

}