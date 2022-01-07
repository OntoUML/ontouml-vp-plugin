package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IStereotype;
import com.vp.plugin.model.ITaggedValue;
import com.vp.plugin.model.ITaggedValueContainer;
import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.utils.ApplicationManagerUtils;
import it.unibz.inf.ontouml.vp.utils.ViewManagerUtils;
import java.util.Iterator;
import javax.swing.JOptionPane;

public class ReloadClassesController implements VPActionController {

  @Override
  public void performAction(VPAction action) {
    final String dialogResult = showInputDialog();
    if(dialogResult == null || dialogResult.isEmpty()) {
      reloadPlugin();
    } else {
      applyStereotype(dialogResult);
    }
  }

  @Override
  public void update(VPAction action) {}

  private String showInputDialog() {
    final ViewManager vm = ApplicationManager.instance().getViewManager();
    return vm.showInputDialog(vm.getRootFrame(),
        "Enter stereotype or cancel to download:",
        "Stereotype Dialog",
        JOptionPane.PLAIN_MESSAGE);
  }

  private void reloadPlugin() {
    System.out.println("----------------------------------------");
    System.out.println("Reloading OntoUML Plugin...");
    ApplicationManager app = ApplicationManager.instance();
    app.reloadPluginClasses(OntoUMLPlugin.PLUGIN_ID);
    System.out.println("Plugin reloaded!");
    ViewManagerUtils.simpleDialog("Plugin reloaded!");
  }

  private void applyStereotype(String stereotype) {
    final IDiagramElement[] views = ApplicationManagerUtils.getDiagramManager().getSelectedDiagramElements();
    final IDiagramElement selectedView = views != null && views.length > 0 ? views[0] : null;
    final IModelElement selectedElement = selectedView != null ? selectedView.getMetaModelElement() : null;

    if(selectedElement != null) {
      applyStereotype(selectedElement,stereotype);
    }
  }

  private void applyStereotype(IModelElement element, String stereotype) {
    removeStereotypes(element);
    element.addStereotype(stereotype);
  }

  private void removeStereotypes(IModelElement element) {
    final ITaggedValueContainer container = element.getTaggedValues();
    final Iterator<?> taggedValuesIterator = container != null ? container.taggedValueIterator() : null;
    final Iterator<?> stereotypeIterator = element.stereotypeModelIterator();

    while(taggedValuesIterator != null && taggedValuesIterator.hasNext()) {
      container.removeTaggedValue((ITaggedValue) taggedValuesIterator.next());
    }

    while(stereotypeIterator != null && stereotypeIterator.hasNext()) {
      element.removeStereotype((IStereotype) stereotypeIterator.next());
    }
  }
}
