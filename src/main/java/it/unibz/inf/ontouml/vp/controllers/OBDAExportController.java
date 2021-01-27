/**
 * Access class of the Integration with Ontop toolbar button.
 *
 * <p>Author: Gustavo Ludovico Guidoni
 */
package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.views.OBDAExportHandler;

public class OBDAExportController implements VPActionController {

  @Override
  public void performAction(VPAction action) {

    OBDAExportHandler dialog = new OBDAExportHandler();

    if (OntoUMLPlugin.getOBDAExportWindowOpen() == true) return;

    OntoUMLPlugin.setOBDAExportWindowOpen(true);

    ApplicationManager.instance().getViewManager().showDialog(dialog);
  }

  /**
   * Called when the menu containing the button is accessed allowing for action manipulation, such
   * as enable/disable or selecting the button.
   *
   * <p>OBS: DOES NOT apply to this class.
   */
  @Override
  public void update(VPAction arg0) {
    // TODO Auto-generated method stub

  }
}
