package it.unibz.inf.ontouml.vp.utils;

import it.unibz.inf.ontouml.vp.views.ProgressDialogHandler;
import java.util.List;
import java.util.function.Function;
import javax.swing.SwingWorker;

public class SimpleServiceWorker extends SwingWorker<List<String>, String> {

  private final Function<SimpleServiceWorker, List<String>> task;
  private final ProgressDialogHandler dialogHandler;
  private final TaskProgress progress;
  private final TaskProgress.Step step;
  private static final String REQUEST_CANCELLED_MESSAGE = "Request cancelled by the user.";
  private static final String DEFAULT_TITLE = "Consultando o servidor OntoUML";

  /**
   * Constructs a SimpleServiceWorker object for handling pop-ups on service requests that don't
   * require user input.
   *
   * @param task - a lambda that receives as arguments the worker context and returns a list
   *     containing a single string for conclusion or cancellation messages . Firing dialogs for
   *     concluded action is automatically handled.
   */
  public SimpleServiceWorker(Function<SimpleServiceWorker, List<String>> task) {
    this(task, DEFAULT_TITLE);
  }

  /** As above, naming the task in the progress window. */
  public SimpleServiceWorker(Function<SimpleServiceWorker, List<String>> task, String title) {
    this.task = task;
    this.progress = new TaskProgress(title);
    this.step = progress.step("Aguardando a resposta do servidor");
    dialogHandler = new ProgressDialogHandler(progress);

    dialogHandler.onCancel(
        e -> {
          System.out.println(REQUEST_CANCELLED_MESSAGE);
          cancel(true);
        });
  }

  @Override
  protected void done() {
    dialogHandler.closeDialog(isCancelled());

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
    progress.start(step);
    List<String> messages;

    try {
      messages = task.apply(this);
      progress.finish(step);
    } catch (Exception e) {
      e.printStackTrace();
      progress.fail(step, e.getMessage());
      return List.of("The service request could not be concluded due to an internal error.");
    }

    return messages;
  }
}
