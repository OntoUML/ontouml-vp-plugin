package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import it.unibz.inf.ontouml.vp.model.vp2ontouml.Uml2OntoumlTransformer;
import it.unibz.inf.ontouml.vp.utils.ViewManagerUtils;
import java.awt.*;
import java.io.IOException;

/**
 * Implementation of toolbar button action responsible for performing model verification.
 *
 * @author Victor Viola
 * @author Claudenir Fonseca
 */
public class ModelVerificationController implements VPActionController {

  private static final String MODEL_VERIFICATION_ACTION =
      "it.unibz.inf.ontouml.vp.actions.ModelVerificationAction";
  private static final String DIAGRAM_VERIFICATION_ACTION =
      "it.unibz.inf.ontouml.vp.actions.DiagramVerificationAction";

  private String getSerializedProject(VPAction action) throws IOException {
    switch (action.getActionId()) {
      case MODEL_VERIFICATION_ACTION:
        return Uml2OntoumlTransformer.transformAndSerialize();
      case DIAGRAM_VERIFICATION_ACTION:
        // TODO: add serialization support to diagrams only
      default:
        return null;
    }
  }

  /**
   * Performs OntoUML model verification.
   *
   * @param action
   */
  @Override
  public void performAction(VPAction action) {
    try {
      final String project = getSerializedProject(action);
      final String result = OntoUMLServerAccessController.requestModelVerification(project);
      ViewManagerUtils.logVerificationResponse(result);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Called when the menu containing the button is accessed allowing for action manipulation, such
   * as enable/disable or selecting the button.
   *
   * <p>OBS: DOES NOT apply to this class.
   */
  @Override
  public void update(VPAction action) {}
}
