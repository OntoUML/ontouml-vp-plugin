package it.unibz.inf.ontouml.vp.utils;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IClass;
import it.unibz.inf.ontouml.vp.controllers.ApplyClassStereotypeId;
import it.unibz.inf.ontouml.vp.controllers.LlmAccessController;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.LlmConfigurations;
import it.unibz.inf.ontouml.vp.controllers.LlmAccessController.SuggestionResult;
import it.unibz.inf.ontouml.vp.model.MetaclassSuggestion;
import it.unibz.inf.ontouml.vp.model.uml.Class;
import it.unibz.inf.ontouml.vp.views.ProgressDialogHandler;
import it.unibz.inf.ontouml.vp.views.SuggestMetaclassView;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

/**
 * Background worker for the "Suggest Metaclass (AI)" feature. Shows a cancellable progress
 * dialog while the LLM request is in flight, then on completion opens {@link
 * SuggestMetaclassView} and, if the user applies the suggestion (or an override), applies the
 * chosen stereotype exactly as {@code ApplyClassStereotypeMenuManager#performAction()} does.
 */
public class SuggestMetaclassWorker extends SwingWorker<SuggestionResult, Void> {

  private static final String DEFAULT_ERROR_MESSAGE = "The AI metaclass suggestion request failed.";

  private final IClass targetClass;
  private final ProgressDialogHandler dialogHandler;

  public SuggestMetaclassWorker(IClass targetClass) {
    this.targetClass = targetClass;
    this.dialogHandler = new ProgressDialogHandler();
    this.dialogHandler.onCancel(e -> cancel(true));
  }

  @Override
  protected SuggestionResult doInBackground() throws Exception {
    dialogHandler.showDialog();

    final LlmConfigurations config = Configurations.getInstance().getLlmConfigurations();
    final String systemPrompt = LlmPromptContextBuilder.getSystemPrompt();
    final String userPrompt = LlmPromptContextBuilder.buildClassSummary(targetClass);

    return LlmAccessController.requestMetaclassSuggestion(
        config, targetClass.getName(), systemPrompt, userPrompt);
  }

  @Override
  protected void done() {
    dialogHandler.closeDialog();

    if (isCancelled()) {
      return;
    }

    final SuggestionResult result;

    try {
      result = get();
    } catch (InterruptedException | ExecutionException e) {
      ViewManagerUtils.simpleDialog(friendlyMessage(e));
      return;
    }

    final SuggestMetaclassView view =
        new SuggestMetaclassView(targetClass, result.getSuggestion(), result.getExchange());
    ApplicationManager.instance().getViewManager().showDialog(view);

    if (view.wasApplied()) {
      applySuggestion(view.getSelectedStereotype());
    }
  }

  private void applySuggestion(String stereotype) {
    StereotypesManager.applyStereotype(targetClass, stereotype);

    ApplyClassStereotypeId.fromStereotype(stereotype)
        .filter(ApplyClassStereotypeId::hasKnownNature)
        .ifPresent(id -> Class.setDefaultRestrictedTo(targetClass));
  }

  private String friendlyMessage(Exception e) {
    final Throwable cause = e.getCause() != null ? e.getCause() : e;
    return cause.getMessage() != null ? cause.getMessage() : DEFAULT_ERROR_MESSAGE;
  }
}
