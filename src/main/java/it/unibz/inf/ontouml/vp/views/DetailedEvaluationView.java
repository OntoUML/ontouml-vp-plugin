package it.unibz.inf.ontouml.vp.views;

import static it.unibz.inf.ontouml.vp.views.DecisionTreeBrowser.badge;
import static it.unibz.inf.ontouml.vp.views.DecisionTreeBrowser.escape;
import static it.unibz.inf.ontouml.vp.views.DecisionTreeBrowser.field;
import static it.unibz.inf.ontouml.vp.views.DecisionTreeBrowser.html;
import static it.unibz.inf.ontouml.vp.views.DecisionTreeBrowser.rawField;
import static it.unibz.inf.ontouml.vp.views.DecisionTreeBrowser.toHex;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IClass;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;
import it.unibz.inf.ontouml.vp.controllers.LlmAccessController.NodeAnswer;
import it.unibz.inf.ontouml.vp.model.DecisionNode;
import it.unibz.inf.ontouml.vp.model.DecisionOption;
import it.unibz.inf.ontouml.vp.model.DecisionStep;
import it.unibz.inf.ontouml.vp.model.DecisionTree;
import it.unibz.inf.ontouml.vp.utils.DetailedClassificationWorker;
import it.unibz.inf.ontouml.vp.utils.LlmPromptResources;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Runs the node-by-node classification in its own window, laid out like the settings tree screen:
 * the decision tree on the left, and on the right the log of each iteration plus the details of
 * whichever node is clicked.
 *
 * <p>Clicking an answered question leads with that question's own entry of the iteration log, and
 * offers machine teaching: the modeller picks one of the answers the question allows, adjusts the
 * justification, and the walk resumes from there, dropping whatever it had concluded after it.
 *
 * <p>The window only reports the outcome — applying the stereotype stays with the suggestion window
 * that opened it, so there is a single apply path.
 */
public class DetailedEvaluationView implements IDialogHandler {

  private static final int TAB_LOG = 0;
  private static final int TAB_QUESTION = 1;

  private final IClass targetClass;

  /** Every answered question, in the order the walk settled them. */
  private final List<DecisionStep> steps = new ArrayList<>();

  /** The same steps by node id, so clicking a node can show what was answered there. */
  private final Map<String, DecisionStep> stepsByNodeId = new LinkedHashMap<>();

  private IDialog dialog;
  private DecisionTreeBrowser treeBrowser;
  private JTabbedPane rightTabs;
  private JEditorPane logPane;
  private JEditorPane nodePane;
  private JButton showExchangeButton;
  private JButton runButton;
  private JLabel statusLabel;
  private JLabel resultLabel;
  private JLabel pathLabel;

  private JPanel teachPanel;
  private JComboBox<String> teachAnswerBox;
  private JTextArea teachJustificationArea;
  private JButton teachButton;

  private DecisionStep selectedStep;
  private DecisionNode selectedNode;
  private String reachedStereotype;
  private String lastError;
  private boolean running;

  public DetailedEvaluationView(IClass targetClass) {
    this.targetClass = targetClass;
  }

  /** The stereotype the walk reached, or null when it did not finish or failed. */
  public String getReachedStereotype() {
    return reachedStereotype;
  }

  @Override
  public Component getComponent() {
    final JPanel panel = new JPanel(new BorderLayout(0, 8));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    panel.add(buildHeader(), BorderLayout.NORTH);
    panel.add(buildCentre(), BorderLayout.CENTER);
    panel.add(buildResultBand(), BorderLayout.SOUTH);

    treeBrowser.setJson(LlmPromptResources.getDecisionTreeJson());
    refreshLog();
    showNodeDetails(null);

    return panel;
  }

  private JPanel buildHeader() {
    final JPanel header = new JPanel(new BorderLayout(0, 4));

    final JLabel title = new JLabel("Detailed evaluation of \"" + targetClass.getName() + "\"");
    title.setFont(title.getFont().deriveFont(Font.BOLD));

    final JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));

    runButton = new JButton("Run");
    runButton.setToolTipText("Asks the AI one question per node of the decision tree.");
    runButton.addActionListener(e -> run());
    controls.add(runButton);

    statusLabel = new JLabel("Not started. Press Run to walk the tree.");
    statusLabel.setForeground(Color.GRAY);
    statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
    controls.add(statusLabel);

    header.add(title, BorderLayout.NORTH);
    header.add(controls, BorderLayout.SOUTH);

    return header;
  }

  private JSplitPane buildCentre() {
    treeBrowser = new DecisionTreeBrowser();
    treeBrowser.setSelectionListener(this::showNodeDetails);

    logPane = new JEditorPane("text/html", "");
    logPane.setEditable(false);
    logPane.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

    nodePane = new JEditorPane("text/html", "");
    nodePane.setEditable(false);
    nodePane.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

    final JPanel questionTab = new JPanel(new BorderLayout(0, 4));
    questionTab.add(new JScrollPane(nodePane), BorderLayout.CENTER);
    questionTab.add(buildQuestionActions(), BorderLayout.SOUTH);

    rightTabs = new JTabbedPane();
    rightTabs.addTab("Iteration log", buildLogTab());
    rightTabs.addTab("Selected question", questionTab);

    final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeBrowser, rightTabs);
    split.setResizeWeight(0.55);
    split.setDividerLocation(430);
    split.setPreferredSize(new Dimension(940, 470));

    return split;
  }

  private JPanel buildLogTab() {
    final JPanel tab = new JPanel(new BorderLayout(0, 4));
    tab.add(new JScrollPane(logPane), BorderLayout.CENTER);

    final JButton copyButton = new JButton("Copy log");
    copyButton.setToolTipText("Copies the whole iteration log to the clipboard as plain text.");
    copyButton.addActionListener(e -> copyLog());

    final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    buttons.add(copyButton);
    tab.add(buttons, BorderLayout.SOUTH);

    return tab;
  }

  /** The teaching controls and the link to the raw exchange, under the selected question. */
  private JPanel buildQuestionActions() {
    final JPanel actions = new JPanel(new BorderLayout(0, 4));

    actions.add(buildTeachPanel(), BorderLayout.NORTH);

    showExchangeButton = new JButton("Show full prompt & answer...");
    showExchangeButton.setEnabled(false);
    showExchangeButton.addActionListener(e -> showSelectedExchange());

    final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    buttons.add(showExchangeButton);
    actions.add(buttons, BorderLayout.SOUTH);

    return actions;
  }

  private JPanel buildTeachPanel() {
    teachPanel = new JPanel(new BorderLayout(0, 4));
    teachPanel.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Machine teaching — correct this answer"),
            BorderFactory.createEmptyBorder(2, 4, 4, 4)));
    teachPanel.setVisible(false);

    teachAnswerBox = new JComboBox<>();
    teachAnswerBox.setToolTipText("One of the answers this question allows.");

    final JPanel answerRow = new JPanel(new BorderLayout(6, 0));
    answerRow.add(new JLabel("Answer:"), BorderLayout.WEST);
    answerRow.add(teachAnswerBox, BorderLayout.CENTER);

    teachJustificationArea = new JTextArea(3, 20);
    teachJustificationArea.setLineWrap(true);
    teachJustificationArea.setWrapStyleWord(true);
    teachJustificationArea.setToolTipText(
        "The justification carried with this answer. It is handed to the AI as settled reasoning.");

    final JScrollPane justificationScroll = new JScrollPane(teachJustificationArea);
    justificationScroll.setPreferredSize(new Dimension(240, 58));

    final JPanel justification = new JPanel(new BorderLayout(0, 2));
    justification.add(new JLabel("Justification:"), BorderLayout.NORTH);
    justification.add(justificationScroll, BorderLayout.CENTER);

    teachButton = new JButton("Teach & continue");
    teachButton.setToolTipText(
        "Fixes this answer and asks the AI to carry on from here, dropping the steps after it.");
    teachButton.addActionListener(e -> teachAndContinue());

    final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    buttons.add(teachButton);

    final JPanel body = new JPanel(new BorderLayout(0, 4));
    body.add(answerRow, BorderLayout.NORTH);
    body.add(justification, BorderLayout.CENTER);

    teachPanel.add(body, BorderLayout.CENTER);
    teachPanel.add(buttons, BorderLayout.SOUTH);

    return teachPanel;
  }

  private JPanel buildResultBand() {
    final JPanel band = new JPanel(new BorderLayout(0, 4));
    band.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
        BorderFactory.createEmptyBorder(8, 0, 0, 0)));

    resultLabel = new JLabel("Final result: —");
    resultLabel.setFont(resultLabel.getFont().deriveFont(Font.BOLD, 15f));

    pathLabel = new JLabel(" ");
    pathLabel.setForeground(Color.GRAY);

    final JButton closeButton = new JButton("Close");
    closeButton.addActionListener(e -> dialog.close());

    final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    buttons.add(closeButton);

    final JPanel texts = new JPanel(new BorderLayout());
    texts.add(resultLabel, BorderLayout.NORTH);
    texts.add(pathLabel, BorderLayout.SOUTH);

    band.add(texts, BorderLayout.CENTER);
    band.add(buttons, BorderLayout.EAST);

    return band;
  }

  // ---------------------------------------------------------------- the walk

  private void run() {
    if (running) {
      return;
    }

    steps.clear();
    stepsByNodeId.clear();

    treeBrowser.setJson(LlmPromptResources.getDecisionTreeJson());
    showNodeDetails(null);

    startWalk(new DetailedClassificationWorker(
        targetClass, this::onNodeEntered, this::onStep, this::onFinished));
  }

  /** Puts the window into "running" state and hands the walk over to the worker. */
  private void startWalk(DetailedClassificationWorker worker) {
    running = true;
    reachedStereotype = null;
    lastError = null;

    runButton.setEnabled(false);
    runButton.setText("Running...");
    updateTeachEnablement();

    statusLabel.setForeground(Color.GRAY);
    statusLabel.setText("Starting...");
    resultLabel.setForeground(Color.BLACK);
    resultLabel.setText("Final result: —");
    pathLabel.setText(" ");

    refreshLog();
    rightTabs.setSelectedIndex(TAB_LOG);

    worker.execute();
  }

  private void onNodeEntered(String nodeId) {
    treeBrowser.markCurrent(nodeId);
    statusLabel.setText("Asking the question of node \"" + nodeId + "\"...");
  }

  private void onStep(DecisionStep step) {
    recordStep(step);

    refreshLog();
    scrollLogTo(step);

    statusLabel.setText("Answered " + steps.size() + " question(s)...");
  }

  private void recordStep(DecisionStep step) {
    steps.add(step);

    if (step.getNodeId() != null) {
      stepsByNodeId.put(step.getNodeId(), step);
      treeBrowser.markAnswered(step.getNodeId(), step.getAnswer());
    }
  }

  private void onFinished(String stereotype, String error) {
    running = false;
    reachedStereotype = stereotype;
    lastError = stereotype == null ? error : null;

    runButton.setEnabled(true);
    runButton.setText("Re-run from the top");
    updateTeachEnablement();

    if (stereotype == null) {
      statusLabel.setForeground(new Color(0xa30000));
      statusLabel.setText(error == null ? "No stereotype reached." : error);
      resultLabel.setForeground(new Color(0xa30000));
      resultLabel.setText("Final result: not reached");
      refreshLog();
      return;
    }

    statusLabel.setForeground(new Color(0x0a7d28));
    statusLabel.setText("Finished.");

    resultLabel.setForeground(new Color(0x0a7d28));
    resultLabel.setText("Final result:  «" + stereotype + "»");

    pathLabel.setText("Path taken:  " + traversalSummary());

    refreshLog();
  }

  /** The chain of answers that led to the result, e.g. "endurant → substantial → concrete". */
  private String traversalSummary() {
    final StringBuilder summary = new StringBuilder();

    for (DecisionStep step : steps) {
      summary.append(summary.length() == 0 ? "" : "  →  ").append(step.getAnswer());
    }

    return summary.length() == 0 ? "(none)" : summary.toString();
  }

  // ---------------------------------------------------------------- iteration log

  private void refreshLog() {
    logPane.setText(IterationLogRenderer.renderLog(steps, reachedStereotype, lastError));
    logPane.setCaretPosition(0);
  }

  private void copyLog() {
    if (steps.isEmpty()) {
      return;
    }

    final StringBuilder text = new StringBuilder();

    for (DecisionStep step : steps) {
      text.append(step.toDisplayText());
    }

    if (reachedStereotype != null) {
      text.append("Final result: «").append(reachedStereotype).append("»\n");
    }

    Toolkit.getDefaultToolkit()
        .getSystemClipboard()
        .setContents(new StringSelection(text.toString()), null);
  }

  /** Brings the log to the entry of the given step, so the two panels stay in step. */
  private void scrollLogTo(DecisionStep step) {
    final String anchor = IterationLogRenderer.anchorOf(step);
    SwingUtilities.invokeLater(() -> logPane.scrollToReference(anchor));
  }

  // ---------------------------------------------------------------- side panel

  private void showNodeDetails(DecisionTreeNode data) {
    if (data == null) {
      selectedStep = null;
      selectedNode = null;
      showExchangeButton.setEnabled(false);
      teachPanel.setVisible(false);
      nodePane.setText(
          html("<p style='color:#777'>Select a node in the tree to see its question and the"
              + " answer given for it.</p>"));
      nodePane.setCaretPosition(0);
      return;
    }

    final DecisionStep step = data.getNodeId() == null ? null : stepsByNodeId.get(data.getNodeId());

    selectedStep = step;
    selectedNode = step == null ? null : nodeOf(data.getNodeId());
    showExchangeButton.setEnabled(step != null && step.getExchange() != null);

    final StringBuilder body = new StringBuilder();

    // the question's own entry of the iteration log comes first: it is what the user asked to see
    if (step != null) {
      body.append(IterationLogRenderer.renderStep(step));
      scrollLogTo(step);
    }

    body.append(badge(data.getKind())).append("<div style='padding:8px 2px'>");

    if (step == null && data.getKind() == DecisionTreeNode.Kind.QUESTION) {
      body.append("<p style='color:#777; margin:0 0 10px 0'>This question was not reached by the"
          + " walk.</p>");
    }

    if (data.getKind() == DecisionTreeNode.Kind.QUESTION) {
      // the log entry above already carries the question, so it is not repeated here
      if (step == null) {
        field(body, "Question", data.getQuestion());
      }

      field(body, "Node id", data.getNodeId());
    } else if (data.getKind() == DecisionTreeNode.Kind.RESULT) {
      field(body, "Answer", data.getAnswer());
      rawField(
          body,
          "Reaches stereotype",
          "<span style='color:"
              + toHex(DecisionTreeNode.Kind.RESULT.getColor())
              + "; font-weight:bold'>«"
              + escape(data.getStereotype())
              + "»</span>");
      field(body, "Answer to", data.getQuestion());
    } else if (data.getKind() == DecisionTreeNode.Kind.OPTION) {
      field(body, "Answer", data.getAnswer());
      field(body, "Answer to", data.getQuestion());
      field(body, "Leads to", data.getLeadsTo());
    } else {
      field(body, "Problem", data.getExplanation());
    }

    field(body, "Path from the root", data.getBreadcrumb());
    body.append("</div>");

    nodePane.setText(html(body.toString()));
    nodePane.setCaretPosition(0);

    fillTeachPanel(step);

    if (step != null) {
      rightTabs.setSelectedIndex(TAB_QUESTION);
    }
  }

  /** Opens the full prompt and raw answer of the selected node in the conversation log. */
  private void showSelectedExchange() {
    if (selectedStep != null && selectedStep.getExchange() != null) {
      ApplicationManager.instance()
          .getViewManager()
          .showDialog(new LlmConversationLogView(selectedStep.getExchange()));
    }
  }

  // ---------------------------------------------------------------- machine teaching

  /** The question behind a node id, read from the tree in force. */
  private DecisionNode nodeOf(String nodeId) {
    final DecisionTree tree = LlmPromptResources.getDecisionTree();
    return tree == null ? null : tree.getNode(nodeId);
  }

  /** Offers the answers the selected question allows, starting from the one it was given. */
  private void fillTeachPanel(DecisionStep step) {
    if (step == null || selectedNode == null || selectedNode.getOptions().isEmpty()) {
      teachPanel.setVisible(false);
      return;
    }

    teachAnswerBox.removeAllItems();

    for (DecisionOption option : selectedNode.getOptions()) {
      teachAnswerBox.addItem(option.getAnswer());
    }

    teachAnswerBox.setSelectedItem(step.getAnswer());
    teachJustificationArea.setText(step.getJustification() == null ? "" : step.getJustification());
    teachJustificationArea.setCaretPosition(0);

    teachPanel.setVisible(true);
    updateTeachEnablement();
  }

  private void updateTeachEnablement() {
    if (teachButton != null) {
      teachButton.setEnabled(!running);
    }
  }

  /**
   * Fixes the answer of the selected question and resumes the walk from the branch it opens. The
   * taught answer travels in the same format as one the model gave, so everything downstream — the
   * log, the path handed to the next prompts, the outcome — treats it alike.
   */
  private void teachAndContinue() {
    if (running || selectedStep == null || selectedNode == null) {
      return;
    }

    final String answer = (String) teachAnswerBox.getSelectedItem();
    final DecisionOption option = answer == null ? null : selectedNode.match(answer);

    if (option == null) {
      JOptionPane.showMessageDialog(
          nodePane,
          "Pick one of the answers this question allows.",
          "Machine teaching",
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    final int taughtIndex = steps.indexOf(selectedStep);
    final int droppedSteps = steps.size() - taughtIndex - 1;

    if (droppedSteps > 0 && !confirmDrop(droppedSteps)) {
      return;
    }

    final DecisionStep taughtStep =
        new DecisionStep(
            selectedStep.getNumber(),
            selectedStep.getNodeId(),
            selectedStep.getQuestion(),
            selectedStep.getOptions(),
            option.getAnswer(),
            NodeAnswer.taughtByUser(option.getAnswer(), teachJustificationArea.getText().trim()),
            option.getStereotype());

    keepStepsBefore(taughtIndex);
    recordStep(taughtStep);

    if (option.isLeaf()) {
      reachedStereotype = null;
      lastError = null;
      onFinished(option.getStereotype(), null);
      treeBrowser.selectNode(taughtStep.getNodeId());
      return;
    }

    startWalk(new DetailedClassificationWorker(
        targetClass,
        new ArrayList<>(steps),
        option.getNext(),
        this::onNodeEntered,
        this::onStep,
        this::onFinished));
  }

  private boolean confirmDrop(int droppedSteps) {
    final String message =
        droppedSteps == 1
            ? "The step taken after this question will be dropped and asked again."
            : "The " + droppedSteps + " steps taken after this question will be dropped and asked"
                + " again.";

    return JOptionPane.showConfirmDialog(
            nodePane,
            message + "\n\nCarry on?",
            "Machine teaching",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE)
        == JOptionPane.OK_OPTION;
  }

  /** Drops the taught step and everything after it, and redraws the tree over what is left. */
  private void keepStepsBefore(int taughtIndex) {
    final List<DecisionStep> kept = new ArrayList<>(steps.subList(0, Math.max(taughtIndex, 0)));

    steps.clear();
    stepsByNodeId.clear();

    // setJson is the only way to clear the row annotations, so the kept ones are re-applied after
    treeBrowser.setJson(LlmPromptResources.getDecisionTreeJson());

    for (DecisionStep step : kept) {
      recordStep(step);
    }
  }

  @Override
  public void prepare(IDialog dialog) {
    this.dialog = dialog;
    dialog.setTitle("Detailed evaluation — decision tree walk");
    dialog.setModal(true);
    dialog.setResizable(true);
    dialog.pack();
  }

  @Override
  public boolean canClosed() {
    return !running;
  }

  @Override
  public void shown() {}
}
