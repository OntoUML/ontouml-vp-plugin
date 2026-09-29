package it.unibz.inf.ontouml.vp.views;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;
import it.unibz.inf.ontouml.vp.utils.BackgroundTasks;
import it.unibz.inf.ontouml.vp.utils.TaskProgress;
import it.unibz.inf.ontouml.vp.utils.ViewManagerUtils;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * The progress dialog of a long task: its steps, and two ways out — cancel it, or close the window
 * and let it finish in the background.
 *
 * <p>Going to the background closes the dialog without touching the worker. The worker's own
 * {@code done()} does what it has always done when the work ends, so the result dialog simply
 * appears later. While it runs unseen, its progress is written to the plugin's message pane, and
 * {@link BackgroundTasks} lets the dialog be reopened — with its Cancel — from the menu.
 *
 * <p>The window's own close button means "background", never "cancel": cancelling throws work away,
 * and that should take a button that says so. It used to do nothing at all, which left a modal
 * dialog that the user could not get out of.
 */
public class ProgressDialogHandler implements IDialogHandler {

  /** How often progress is written to the message pane while the dialog is closed. */
  private static final int BACKGROUND_REPORT_MILLIS = 1500;

  private final TaskProgress progress;
  private final StepProgressPanel panel;
  private final String statusKey = "task-" + System.identityHashCode(this);

  private IDialog dialog;

  /** The window is on screen right now. */
  private volatile boolean visible = false;

  /** The worker is done with this dialog for good; it can no longer be shown. */
  private volatile boolean finished = false;

  private Timer backgroundReporter;
  private String lastReported = "";

  /** Whether any progress line was ever written to the message pane for this task. */
  private volatile boolean wasReportedToPane = false;

  public ProgressDialogHandler(TaskProgress progress) {
    this.progress = progress;
    this.panel = new StepProgressPanel(progress);

    onCancel(e -> System.out.println("Cancelling dialog with default behaviour."));
    panel.setBackgroundAction(e -> enterBackground(true));
  }

  @Override
  public Component getComponent() {
    return panel;
  }

  @Override
  public void prepare(IDialog dialog) {
    this.dialog = dialog;

    dialog.setTitle(title());
    dialog.setModal(true);
    dialog.setResizable(false);
    dialog.pack();
  }

  @Override
  public void shown() {
    visible = true;
    stopBackgroundReporting();
    panel.startRefreshing();
  }

  /**
   * Called when the user asks to close the window. That is a request to carry on in the background,
   * not to stop: the window closes, and the work goes on.
   */
  @Override
  public boolean canClosed() {
    if (!finished) {
      enterBackground(false);
    }

    return true;
  }

  /** Shows the dialog — the first time, or again after it was sent to the background. */
  public void showDialog() {
    if (finished) {
      System.out.println("The progress dialog was already closed and cannot be shown.");
    } else if (visible) {
      System.out.println("The progress dialog is already visible and cannot be shown twice.");
    } else {
      BackgroundTasks.register(this);
      SwingUtilities.invokeLater(
          () -> {
            // a task that finished while this was queued must not get a dialog it can no longer use
            if (!finished && !visible) {
              ApplicationManager.instance().getViewManager().showDialog(this);
            }
          });
    }
  }

  /** Brings a task that went to the background back on screen. */
  public boolean reopen() {
    if (finished || visible) {
      return !finished;
    }

    showDialog();

    return true;
  }

  /** The worker has finished, however it did. */
  public void closeDialog() {
    closeDialog(false);
  }

  /**
   * @param cancelled whether the task was cancelled rather than completed, for the last line
   *     written to the message pane.
   */
  public void closeDialog(boolean cancelled) {
    // Cancel closes the dialog and then the worker's done() closes it again; the last line must be
    // written once
    if (finished) {
      return;
    }

    final boolean wasReporting = backgroundReporter != null || wasReportedToPane;

    finished = true;
    BackgroundTasks.unregister(this);
    stopBackgroundReporting();
    panel.stopRefreshing();

    if (visible && dialog != null) {
      dialog.close();
    }

    visible = false;

    if (wasReporting) {
      ViewManagerUtils.finishStatus(
          statusKey, "[" + title() + "] " + (cancelled ? "cancelado." : "concluído."));
    }
  }

  public void onCancel(Consumer<ActionEvent> cancelAction) {
    panel.setCancelAction(
        e -> {
          cancelAction.accept(e);
          closeDialog(true);
        });
  }

  public String getTitle() {
    return title();
  }

  public TaskProgress getProgress() {
    return progress;
  }

  private String title() {
    final String title = progress.snapshot().getTitle();

    return title.isEmpty() ? "Processando" : title;
  }

  /**
   * Closes the window, if it is not already closing, and starts reporting to the message pane.
   *
   * @param closeWindow false when the window is already on its way out — the close button was
   *     pressed and Visual Paradigm is about to close it itself.
   */
  private void enterBackground(boolean closeWindow) {
    if (!visible || finished) {
      return;
    }

    visible = false;
    panel.stopRefreshing();

    if (closeWindow && dialog != null) {
      dialog.close();
    }

    startBackgroundReporting();
  }

  private void startBackgroundReporting() {
    stopBackgroundReporting();
    reportToPane();

    backgroundReporter = new Timer(BACKGROUND_REPORT_MILLIS, e -> reportToPane());
    backgroundReporter.start();
  }

  private void stopBackgroundReporting() {
    if (backgroundReporter != null) {
      backgroundReporter.stop();
      backgroundReporter = null;
    }
  }

  /**
   * Writes one line saying where the task is. Only a line that has changed is written, so a long
   * conversion does not rewrite the same text every second.
   */
  private void reportToPane() {
    if (finished) {
      return;
    }

    final String line = "[" + title() + "] em segundo plano — " + progress.snapshot().summarise();

    if (line.equals(lastReported)) {
      return;
    }

    lastReported = line;
    wasReportedToPane = true;
    ViewManagerUtils.logStatus(statusKey, line);
  }
}
