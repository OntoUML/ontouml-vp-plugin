package it.unibz.inf.ontouml.vp.views;

import it.unibz.inf.ontouml.vp.utils.TaskProgress;
import it.unibz.inf.ontouml.vp.utils.TaskProgress.Snapshot;
import it.unibz.inf.ontouml.vp.utils.TaskProgress.State;
import it.unibz.inf.ontouml.vp.utils.TaskProgress.StepView;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.Timer;

/**
 * The progress dialog's content: the task's steps and where each one stands, what the running one
 * is doing, how long it has been going, and the two ways out — cancel it, or let it carry on in the
 * background.
 *
 * <p>It replaces a 200×200 box that said "Loading" over an indeterminate bar, which was fine while
 * a request took a second and says nothing useful about one that reads a dozen sources, converts a
 * PDF and asks the model three times.
 *
 * <p>The panel polls the {@link TaskProgress} rather than being told about changes. The worker
 * never waits on a reader, and a panel that is closed and reopened simply picks up where the task
 * is now.
 */
public class StepProgressPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  private static final int REFRESH_MILLIS = 400;

  /** Width the activity line wraps at: the panel's width less its padding. */
  private static final int ACTIVITY_WIDTH = 468;

  private static final Color DONE_COLOR = new Color(0, 128, 0);
  private static final Color RUNNING_COLOR = new Color(0, 90, 170);
  private static final Color MUTED_COLOR = Color.GRAY;
  private static final Color FAILED_COLOR = Color.RED.darker();

  private final TaskProgress progress;

  private final JLabel titleLabel = new JLabel(" ");
  private final JPanel stepsPanel = new JPanel(new GridBagLayout());
  private final List<JLabel> stepLabels = new ArrayList<>();
  private final List<JLabel> noteLabels = new ArrayList<>();
  private final JTextArea activityText = new JTextArea(2, 40);
  private final JProgressBar bar = new JProgressBar();
  private final JLabel elapsedLabel = new JLabel(" ");
  private final JButton cancelButton = new JButton("Cancelar");
  private final JButton backgroundButton = new JButton("Continuar em segundo plano");

  private Timer timer;

  public StepProgressPanel(TaskProgress progress) {
    super(new GridBagLayout());
    this.progress = progress;

    setBorder(BorderFactory.createEmptyBorder(14, 16, 12, 16));
    setPreferredSize(new Dimension(500, 330));

    titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));

    // A text area, not a label: a label wraps only through HTML, and its wrap width did not follow
    // the width it was actually given — the sentence saying Docling's first conversion can take
    // minutes was being cut off mid-word. A text area wraps at the width it really has.
    activityText.setEditable(false);
    activityText.setFocusable(false);
    activityText.setOpaque(false);
    activityText.setLineWrap(true);
    activityText.setWrapStyleWord(true);
    activityText.setBorder(BorderFactory.createEmptyBorder());
    activityText.setForeground(MUTED_COLOR);
    activityText.setFont(new JLabel().getFont());
    // room for two lines, so a long message wrapping does not push the bar down as it arrives
    activityText.setPreferredSize(new Dimension(ACTIVITY_WIDTH, 34));
    activityText.setMinimumSize(new Dimension(ACTIVITY_WIDTH, 34));
    elapsedLabel.setForeground(MUTED_COLOR);

    bar.setStringPainted(true);
    bar.setIndeterminate(true);

    backgroundButton.setToolTipText(
        "Fecha esta janela e deixa a tarefa terminando. O andamento aparece no painel de"
            + " mensagens, e a janela pode ser reaberta em OntoUML Plugin → Running Tasks.");

    add(titleLabel, at(0, 0, 2, 0, 0, 8, GridBagConstraints.HORIZONTAL));
    add(stepsPanel, at(0, 1, 2, 1.0, 1.0, 8, GridBagConstraints.BOTH));
    add(activityText, at(0, 2, 2, 0, 0, 6, GridBagConstraints.HORIZONTAL));
    add(bar, at(0, 3, 2, 0, 0, 4, GridBagConstraints.HORIZONTAL));
    add(elapsedLabel, at(0, 4, 2, 0, 0, 10, GridBagConstraints.HORIZONTAL));

    final JPanel buttons = new JPanel(new BorderLayout(8, 0));
    buttons.add(cancelButton, BorderLayout.WEST);
    buttons.add(backgroundButton, BorderLayout.EAST);
    add(buttons, at(0, 5, 2, 0, 0, 0, GridBagConstraints.HORIZONTAL));

    refresh();
  }

  private static GridBagConstraints at(
      int x, int y, int width, double weightx, double weighty, int bottom, int fill) {
    final GridBagConstraints constraints = new GridBagConstraints();
    constraints.gridx = x;
    constraints.gridy = y;
    constraints.gridwidth = width;
    constraints.weightx = weightx;
    constraints.weighty = weighty;
    constraints.fill = fill;
    constraints.anchor = GridBagConstraints.NORTHWEST;
    constraints.insets = new Insets(0, 0, bottom, 0);

    return constraints;
  }

  /** Starts redrawing on a timer, for as long as the panel is on screen. */
  public void startRefreshing() {
    stopRefreshing();
    refresh();

    timer = new Timer(REFRESH_MILLIS, e -> refresh());
    timer.start();
  }

  public void stopRefreshing() {
    if (timer != null) {
      timer.stop();
      timer = null;
    }
  }

  public void setCancelAction(ActionListener action) {
    replaceListeners(cancelButton, action);
  }

  public void setBackgroundAction(ActionListener action) {
    replaceListeners(backgroundButton, action);
  }

  private static void replaceListeners(JButton button, ActionListener action) {
    for (ActionListener listener : button.getActionListeners()) {
      button.removeActionListener(listener);
    }

    button.addActionListener(action);
  }

  /** Redraws from the task's current state. Safe to call at any time on the event thread. */
  public void refresh() {
    final Snapshot snapshot = progress.snapshot();

    titleLabel.setText(snapshot.getTitle().isEmpty() ? " " : snapshot.getTitle());
    rebuildStepsIfNeeded(snapshot.getSteps().size());

    for (int i = 0; i < snapshot.getSteps().size(); i++) {
      final StepView step = snapshot.getSteps().get(i);
      final JLabel label = stepLabels.get(i);
      final JLabel note = noteLabels.get(i);

      label.setText(marker(step.getState()) + "  " + describe(step));
      label.setForeground(colorOf(step.getState()));
      label.setFont(
          label.getFont()
              .deriveFont(step.getState() == State.RUNNING ? Font.BOLD : Font.PLAIN));

      // the running step's detail is on the activity line; a skipped or failed one says why here
      final boolean hasNote =
          !step.getNote().isEmpty()
              && (step.getState() == State.SKIPPED || step.getState() == State.FAILED);

      note.setText(hasNote ? "     " + step.getNote() : " ");
      note.setForeground(step.getState() == State.FAILED ? FAILED_COLOR : MUTED_COLOR);
      // an empty note would still hold a line open, and the list would breathe unevenly
      if (note.isVisible() != hasNote) {
        note.setVisible(hasNote);
        stepsPanel.revalidate();
      }
    }

    final StepView running = snapshot.getRunning();

    activityText.setText(snapshot.getActivity());
    activityText.setToolTipText(snapshot.getActivity().isEmpty() ? null : snapshot.getActivity());

    if (running != null && running.hasCount()) {
      bar.setIndeterminate(false);
      bar.setMaximum(running.getTotal());
      bar.setValue(Math.min(running.getCurrent(), running.getTotal()));
      bar.setString(running.getCurrent() + " de " + running.getTotal());
    } else {
      bar.setIndeterminate(true);
      bar.setString(null);
    }

    elapsedLabel.setText("Tempo decorrido: " + formatElapsed(snapshot.getElapsedMillis()));
  }

  /** The step list is declared once, but the panel is built before the worker has declared it. */
  private void rebuildStepsIfNeeded(int count) {
    if (stepLabels.size() == count) {
      return;
    }

    stepsPanel.removeAll();
    stepLabels.clear();
    noteLabels.clear();

    for (int i = 0; i < count; i++) {
      final JLabel label = new JLabel(" ");
      final JLabel note = new JLabel(" ");
      note.setFont(note.getFont().deriveFont(Font.ITALIC, 11f));

      stepLabels.add(label);
      noteLabels.add(note);

      stepsPanel.add(label, at(0, i * 2, 1, 1.0, 0, 0, GridBagConstraints.HORIZONTAL));
      stepsPanel.add(note, at(0, i * 2 + 1, 1, 1.0, 0, 3, GridBagConstraints.HORIZONTAL));
    }

    // a filler that takes the slack, so the steps stay packed at the top
    final GridBagConstraints filler = at(0, count * 2, 1, 0, 1.0, 0, GridBagConstraints.VERTICAL);
    stepsPanel.add(new JLabel(), filler);

    stepsPanel.revalidate();
    stepsPanel.repaint();
  }

  /**
   * One glyph per state, all from the geometric-shapes block the old "Loading" spinner already
   * used. A check mark would read better, but it lives in a block the dialog's font may not have,
   * and a missing glyph shows as an empty box — worse than a plain dot.
   */
  static String marker(State state) {
    switch (state) {
      case DONE:
        return "●"; // ●
      case RUNNING:
        return "▶"; // ▶
      case FAILED:
        return "×"; // ×
      case SKIPPED:
        return "–"; // –
      case PENDING:
      default:
        return "○"; // ○
    }
  }

  private static Color colorOf(State state) {
    switch (state) {
      case DONE:
        return DONE_COLOR;
      case RUNNING:
        return RUNNING_COLOR;
      case FAILED:
        return FAILED_COLOR;
      case SKIPPED:
      case PENDING:
      default:
        return MUTED_COLOR;
    }
  }

  /** A step's label with its count, when it has one: "Leitura das fontes (3 de 12)". */
  static String describe(StepView step) {
    if (step.hasCount() && step.getState() == State.RUNNING) {
      return step.getLabel() + " (" + step.getCurrent() + " de " + step.getTotal() + ")";
    }

    if (step.hasCount() && step.getState() == State.DONE) {
      return step.getLabel() + " (" + step.getTotal() + ")";
    }

    return step.getLabel();
  }

  /** m:ss, or h:mm:ss past the hour. */
  static String formatElapsed(long millis) {
    final long total = Math.max(0, millis / 1000);
    final long hours = total / 3600;
    final long minutes = (total % 3600) / 60;
    final long seconds = total % 60;

    return hours > 0
        ? String.format("%d:%02d:%02d", hours, minutes, seconds)
        : String.format("%d:%02d", minutes, seconds);
  }
}
