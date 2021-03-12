package it.unibz.inf.ontouml.vp.utils;

import it.unibz.inf.ontouml.vp.views.ProgressDialogHandler;
import java.util.List;
import java.util.function.Function;
import javax.swing.SwingWorker;

public class SimpleServiceWorker extends SwingWorker<List<String>, String> {

  private final Function<SimpleServiceWorker, List<String>> task;
  private final ProgressDialogHandler dialogHandler;
  private static final String REQUEST_CANCELLED_MESSAGE = "Request cancelled by the user.";

  /**
   * Constructs a SimpleServiceWorker object for handling pop-ups on service requests that don't
   * require user input.
   *
   * @param task - a lambda that receives as arguments the worker context and returns a list
   *     containing a single string for conclusion or cancellation messages . Firing dialogs for
   *     concluded action is automatically handled.
   */
  public SimpleServiceWorker(Function<SimpleServiceWorker, List<String>> task) {
    this.task = task;
    dialogHandler = new ProgressDialogHandler();

    dialogHandler.onCancel(
        e -> {
          System.out.println(REQUEST_CANCELLED_MESSAGE);
          cancel(true);
        });
  }

  @Override
  protected void done() {
    dialogHandler.closeDialog();

    if (isCancelled()) {
      ViewManagerUtils.log(REQUEST_CANCELLED_MESSAGE);
      return;
    }

    try {
      if (!get().isEmpty()) {
        String message = get().get(0);
        ViewManagerUtils.simpleDialog(message);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  protected List<String> doInBackground() {
    dialogHandler.showDialog();
    List<String> messages;

    try {
      messages = task.apply(this);
    } catch (Exception e) {
      e.printStackTrace();
      return List.of("The service request could not be concluded due to an internal error.");
    }

    return messages;
  }
}
