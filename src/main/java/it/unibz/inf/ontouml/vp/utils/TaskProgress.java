package it.unibz.inf.ontouml.vp.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * What a long-running task is doing, in steps a person can follow.
 *
 * <p>Documenting one class can mean reading a dozen sources, converting a PDF with Docling,
 * searching the web and asking the model — minutes of work behind a bar that used to say only
 * "Loading". The worker declares its steps up front and marks them as it goes; the progress dialog
 * and the message pane, which show it while the task is in the background, both read from here.
 *
 * <p>It is written from the worker's thread and read from the event thread, so every access is
 * synchronised, and readers get an immutable {@link Snapshot} rather than the live steps. There is
 * no listener: the dialog polls, which keeps a slow or late reader from ever holding up the worker.
 */
public class TaskProgress {

  /** Where a step stands. */
  public enum State {
    PENDING,
    RUNNING,
    DONE,
    /** Not needed this time, with the reason: web search is off, there was nothing to read. */
    SKIPPED,
    FAILED
  }

  /** A read-only view of one step at the moment a snapshot was taken. */
  public static final class StepView {

    private final String label;
    private final State state;
    private final String note;
    private final int current;
    private final int total;

    private StepView(String label, State state, String note, int current, int total) {
      this.label = label;
      this.state = state;
      this.note = note;
      this.current = current;
      this.total = total;
    }

    public String getLabel() {
      return label;
    }

    public State getState() {
      return state;
    }

    /** Why a step was skipped or failed; empty otherwise. */
    public String getNote() {
      return note;
    }

    public int getCurrent() {
      return current;
    }

    /** Zero when the step has no denominator, which is what makes its bar indeterminate. */
    public int getTotal() {
      return total;
    }

    public boolean hasCount() {
      return total > 0;
    }
  }

  /** Everything a reader needs, frozen at one instant. */
  public static final class Snapshot {

    private final String title;
    private final List<StepView> steps;
    private final String activity;
    private final long elapsedMillis;

    private Snapshot(String title, List<StepView> steps, String activity, long elapsedMillis) {
      this.title = title;
      this.steps = Collections.unmodifiableList(steps);
      this.activity = activity;
      this.elapsedMillis = elapsedMillis;
    }

    public String getTitle() {
      return title;
    }

    public List<StepView> getSteps() {
      return steps;
    }

    /** What is happening inside the running step: the file being converted, the part being read. */
    public String getActivity() {
      return activity;
    }

    public long getElapsedMillis() {
      return elapsedMillis;
    }

    /** The step being worked on, or null between steps and after the last one. */
    public StepView getRunning() {
      for (StepView step : steps) {
        if (step.state == State.RUNNING) {
          return step;
        }
      }

      return null;
    }

    /** One line saying where things are, for places too small for the whole list. */
    public String summarise() {
      final StepView running = getRunning();

      if (running == null) {
        return title;
      }

      final StringBuilder line = new StringBuilder(running.label);

      if (running.hasCount()) {
        line.append(" (").append(running.current).append(" de ").append(running.total).append(")");
      }

      if (!activity.isEmpty()) {
        line.append(" — ").append(activity);
      }

      return line.toString();
    }
  }

  /** A step as the worker holds it: the handle used to mark it. */
  public static final class Step {

    private final String label;
    private State state = State.PENDING;
    private String note = "";
    private int current;
    private int total;

    private Step(String label) {
      this.label = label;
    }
  }

  private final String title;
  private final List<Step> steps = new ArrayList<>();
  private final long startedAt = System.currentTimeMillis();
  private String activity = "";

  public TaskProgress(String title) {
    this.title = title;
  }

  /** For a caller with no dialog to report to — tests, and code that is only being reused. */
  public static TaskProgress silent() {
    return new TaskProgress("");
  }

  /** Declares a step. Steps are shown in the order they are declared. */
  public synchronized Step step(String label) {
    final Step step = new Step(label);
    steps.add(step);

    return step;
  }

  public synchronized void start(Step step) {
    step.state = State.RUNNING;
    step.note = "";
    activity = "";
  }

  public synchronized void finish(Step step) {
    step.state = State.DONE;
    step.note = "";
    activity = "";
  }

  /** Marks a step as not needed this time. A step that does not run must say so, not stay pending. */
  public synchronized void skip(Step step, String reason) {
    step.state = State.SKIPPED;
    step.note = reason == null ? "" : reason;
  }

  public synchronized void fail(Step step, String reason) {
    step.state = State.FAILED;
    step.note = reason == null ? "" : reason;
    activity = "";
  }

  /**
   * Marks whichever step is running as failed, for a worker that has caught an exception and does
   * not care to track which step it was in.
   */
  public synchronized void failRunning(String reason) {
    for (Step step : steps) {
      if (step.state == State.RUNNING) {
        fail(step, reason);
      }
    }
  }

  /** Sets "3 of 12": the numerator and denominator that make the bar determinate. */
  public synchronized void count(Step step, int current, int total) {
    step.current = current;
    step.total = total;
  }

  /** What is happening inside the running step. Cleared when the step changes. */
  public synchronized void activity(String text) {
    activity = text == null ? "" : text;
  }

  public synchronized Snapshot snapshot() {
    final List<StepView> views = new ArrayList<>();

    for (Step step : steps) {
      views.add(new StepView(step.label, step.state, step.note, step.current, step.total));
    }

    return new Snapshot(title, views, activity, System.currentTimeMillis() - startedAt);
  }
}
