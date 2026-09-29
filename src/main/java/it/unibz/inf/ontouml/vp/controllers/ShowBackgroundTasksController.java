package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import it.unibz.inf.ontouml.vp.utils.BackgroundTasks;
import it.unibz.inf.ontouml.vp.utils.ViewManagerUtils;
import it.unibz.inf.ontouml.vp.views.ProgressDialogHandler;
import java.awt.Component;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Brings back the progress window of a task that was sent to the background — the way to see how it
 * is going, and the only way to cancel it once its window is gone.
 */
public class ShowBackgroundTasksController implements VPActionController {

  private static final String LABEL = "Running Tasks";

  @Override
  public void performAction(VPAction action) {
    final List<ProgressDialogHandler> tasks = BackgroundTasks.running();

    if (tasks.isEmpty()) {
      ViewManagerUtils.simpleDialog("There is no task running.");
      return;
    }

    if (tasks.size() == 1) {
      tasks.get(0).reopen();
      return;
    }

    final String[] titles = new String[tasks.size()];

    for (int i = 0; i < titles.length; i++) {
      // titles can repeat — the same class documented twice — so the position keeps them apart
      titles[i] = (i + 1) + ". " + tasks.get(i).getTitle();
    }

    final Component parent = ApplicationManager.instance().getViewManager().getRootFrame();
    final Object chosen =
        JOptionPane.showInputDialog(
            parent,
            "Which task?",
            LABEL,
            JOptionPane.QUESTION_MESSAGE,
            null,
            titles,
            titles[0]);

    for (int i = 0; chosen != null && i < titles.length; i++) {
      if (titles[i].equals(chosen)) {
        tasks.get(i).reopen();
      }
    }
  }

  /**
   * Enabled only while something is running, and says how many: an entry that opens onto "there is
   * nothing here" is not worth having in the menu.
   */
  @Override
  public void update(VPAction action) {
    final int running = BackgroundTasks.count();

    action.setEnabled(running > 0);
    action.setLabel(running > 0 ? LABEL + " (" + running + ")" : LABEL);
  }
}
