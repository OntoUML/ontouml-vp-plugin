package it.unibz.inf.ontouml.vp.utils;

import it.unibz.inf.ontouml.vp.views.ProgressDialogHandler;
import java.util.ArrayList;
import java.util.List;

/**
 * The tasks that are running right now, so one sent to the background can be found again.
 *
 * <p>A task whose dialog was closed with "continue in the background" has no window and, until it
 * finishes, no other trace: without a way back to it, the user could neither see how it was doing
 * nor cancel it. The menu entry reads this list and reopens the dialog of the one chosen.
 *
 * <p>Entries add themselves when their dialog is first shown and leave when the worker closes it,
 * which every worker already does in {@code done()} — including when it failed or was cancelled.
 */
public class BackgroundTasks {

  private static final List<ProgressDialogHandler> RUNNING = new ArrayList<>();

  private BackgroundTasks() {}

  public static synchronized void register(ProgressDialogHandler task) {
    if (!RUNNING.contains(task)) {
      RUNNING.add(task);
    }
  }

  public static synchronized void unregister(ProgressDialogHandler task) {
    RUNNING.remove(task);
  }

  /** The tasks in progress, oldest first. */
  public static synchronized List<ProgressDialogHandler> running() {
    return new ArrayList<>(RUNNING);
  }

  public static synchronized int count() {
    return RUNNING.size();
  }
}
