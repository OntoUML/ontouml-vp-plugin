package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.diagram.IBaseDiagramElement;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import it.unibz.inf.ontouml.vp.model.ServiceIssue;
import it.unibz.inf.ontouml.vp.model.VerificationServiceResult;
import it.unibz.inf.ontouml.vp.model.vp2ontouml.Uml2OntoumlTransformer;
import it.unibz.inf.ontouml.vp.utils.ViewManagerUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

  /**
   * Performs OntoUML model verification.
   *
   * @param action
   */
  @Override
  public void performAction(VPAction action) {
    try {
      final String project = Uml2OntoumlTransformer.transformAndSerialize();
      final VerificationServiceResult result =
          OntoUMLServerAccessController.requestModelVerification(project);

      if (DIAGRAM_VERIFICATION_ACTION.equals(action.getActionId())) {
        filterDiagramIssueOnly(result);
      }

      ViewManagerUtils.logVerificationResult(result);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void filterDiagramIssueOnly(VerificationServiceResult result) {
    if (result == null) {
      return;
    }

    final IDiagramUIModel activeDiagram =
        ApplicationManager.instance().getDiagramManager().getActiveDiagram();
    final IDiagramElement[] diagramElements = activeDiagram.toDiagramElementArray();

    if (diagramElements == null) {
      result.setResult(null);
      return;
    }

    final List<ServiceIssue> verificationIssues = result.getResult();
    final Set<String> presentModelElementsIds =
        Arrays.stream(diagramElements)
            .map(IBaseDiagramElement::getModelElement)
            .map(modelElement -> modelElement != null ? modelElement.getId() : null)
            .collect(Collectors.toSet());
    final List<ServiceIssue> filteredVerificationIssues =
        verificationIssues.stream()
            .filter(
                issue ->
                    issue.getSource() != null
                        && presentModelElementsIds.contains(issue.getSource().getId()))
            .collect(Collectors.toList());

    result.setResult(filteredVerificationIssues);
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
