package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPContext;
import com.vp.plugin.action.VPContextActionController;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IModelElement;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.utils.SuggestMetaclassWorker;
import it.unibz.inf.ontouml.vp.utils.VPContextUtils;
import it.unibz.inf.ontouml.vp.utils.ViewManagerUtils;
import java.awt.event.ActionEvent;
import java.util.Set;

/**
 * Implementation of the context sensitive action that triggers an AI-based OntoUML metaclass
 * (stereotype) suggestion for a single selected class.
 */
public class SuggestMetaclassController implements VPContextActionController {

  private static final String MISSING_API_KEY_MESSAGE =
      "The AI provider is not configured yet. Open the OntoUML Plugin Settings, pick a"
          + " provider and fill in its API key (local providers need none), then try again.";

  @Override
  public void performAction(VPAction action, VPContext context, ActionEvent event) {
    final IClass selectedClass = getSingleSelectedClass(context);

    if (selectedClass == null) {
      return;
    }

    if (!Configurations.getInstance().getLlmConfigurations().isConfigured()) {
      ViewManagerUtils.simpleDialog(MISSING_API_KEY_MESSAGE);
      return;
    }

    new SuggestMetaclassWorker(selectedClass).execute();
  }

  @Override
  public void update(VPAction action, VPContext context) {
    // without a configured provider there is nothing to send the class to, so the action is
    // off rather than opening a dialog that only says so; it comes back once the settings are saved
    action.setEnabled(
        getSingleSelectedClass(context) != null
            && Configurations.getInstance().getLlmConfigurations().isConfigured());
  }

  private IClass getSingleSelectedClass(VPContext context) {
    final Set<IModelElement> elements = VPContextUtils.getModelElements(context);

    if (elements.size() != 1) {
      return null;
    }

    final IModelElement element = elements.iterator().next();
    return element instanceof IClass ? (IClass) element : null;
  }
}
