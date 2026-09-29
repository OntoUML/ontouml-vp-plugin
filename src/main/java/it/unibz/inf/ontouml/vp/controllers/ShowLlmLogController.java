package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import it.unibz.inf.ontouml.vp.views.LlmConversationLogView;

/** Opens the log of the AI conversations made in the current session. */
public class ShowLlmLogController implements VPActionController {

  @Override
  public void performAction(VPAction action) {
    ApplicationManager.instance().getViewManager().showDialog(new LlmConversationLogView());
  }

  @Override
  public void update(VPAction action) {}
}
