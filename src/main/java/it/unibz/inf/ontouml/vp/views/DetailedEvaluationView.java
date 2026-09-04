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
import it.unibz.inf.ontouml.vp.model.DecisionStep;
import it.unibz.inf.ontouml.vp.utils.DetailedClassificationWorker;
import it.unibz.inf.ontouml.vp.utils.LlmPromptResources;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

/**
 * Runs the node-by-node classification in its own window, laid out like the settings tree screen:
 * the decision tree on the left, and on the right the log of each iteration plus the details of
 * whichever node is clicked.
 *
 * <p>The window only reports the outcome — applying the stereotype stays with the suggestion window
 * that opened it, so there is a single apply path.
 */
public class DetailedEvaluationView implements IDialogHandler {

  private static final int TAB_LOG = 0;
  private static final int TAB_NODE = 1;

  private final IClass targetClass;

  /** Answered steps by node id, so clicking a node can show what the AI replied there. */
  private final Map<String, DecisionStep> stepsByNodeId = new LinkedHashMap<>();

  private IDialog dialog;
  private DecisionTreeBrowser treeBrowser;
  private JTabbedPane rightTabs;
  private JTextArea logArea;
  private JEditorPane nodePane;
  private JButton showExchangeButton;
  private JButton runButton;
  private JLabel statusLabel;
  private JLabel resultLabel;
  private JLabel pathLabel;

  private DecisionStep selectedStep;
  private String reachedStereotype;
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

    logArea = new JTextArea();
    logArea.setEditable(false);
    logArea.setLineWrap(true);
    logArea.setWrapStyleWord(true);
    logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
    logArea.setText("The log of each question fills in here as the walk progresses.\n");

    nodePane = new JEditorPane("text/html", "");
    nodePane.setEditable(false);
    nodePane.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

    showExchangeButton = new JButton("Show full prompt & answer...");
    showExchangeButton.setEnabled(false);
    showExchangeButton.addActionListener(e -> showSelectedExchange());

    final JPanel nodeTab = new JPanel(new BorderLayout(0, 4));
    nodeTab.add(new JScrollPane(nodePane), BorderLayout.CENTER);

    final JPanel nodeButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    nodeButtons.add(showExchangeButton);
    nodeTab.add(nodeButtons, BorderLayout.SOUTH);

    rightTabs = new JTabbedPane();
    rightTabs.addTab("Iteration log", new JScrollPane(logArea));
    rightTabs.addTab("Selected node", nodeTab);

    final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeBrowser, rightTabs);
    split.setResizeWeight(0.55);
    split.setDividerLocation(430);
    split.setPreferredSize(new Dimension(840, 430));

    return split;
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

    running = true;
    reachedStereotype = null;
    stepsByNodeId.clear();

    runButton.setEnabled(false);
    runButton.setText("Running...");
    statusLabel.setForeground(Color.GRAY);
    statusLabel.setText("Starting...");
    logArea.setText("");
    resultLabel.setText("Final result: —");
    pathLabel.setText(" ");

    treeBrowser.setJson(LlmPromptResources.getDecisionTreeJson());
    rightTabs.setSelectedIndex(TAB_LOG);

    new DetailedClassificationWorker(targetClass, this::onNodeEntered, this::onStep, this::onFinished)
        .execute();
  }

  private void onNodeEntered(String nodeId) {
    treeBrowser.markCurrent(nodeId);
    statusLabel.setText("Asking the question of node \"" + nodeId + "\"...");
  }

  private void onStep(DecisionStep step) {
    if (step.getNodeId() != null) {
      stepsByNodeId.put(step.getNodeId(), step);
      treeBrowser.markAnswered(step.getNodeId(), step.getAnswer());
    }

    logArea.append(step.toDisplayText());
    logArea.setCaretPosition(logArea.getDocument().getLength());

    statusLabel.setText("Answered " + stepsByNodeId.size() + " question(s)...");
  }

  private void onFinished(String stereotype, String error) {
    running = false;
    reachedStereotype = stereotype;

    runButton.setEnabled(true);
    runButton.setText("Re-run");

    if (stereotype == null) {
      statusLabel.setForeground(new Color(0xa30000));
      statusLabel.setText(error == null ? "No stereotype reached." : error);
      resultLabel.setForeground(new Color(0xa30000));
      resultLabel.setText("Final result: not reached");
      return;
    }

    statusLabel.setForeground(new Color(0x0a7d28));
    statusLabel.setText("Finished.");

    resultLabel.setForeground(new Color(0x0a7d28));
    resultLabel.setText("Final result:  «" + stereotype + "»");

    pathLabel.setText("Path taken:  " + traversalSummary());

    logArea.append("Final result: «" + stereotype + "»\n");
    logArea.setCaretPosition(logArea.getDocument().getLength());
  }

  /** The chain of answers that led to the result, e.g. "endurant → substantial → concrete". */
  private String traversalSummary() {
    final StringBuilder summary = new StringBuilder();

    for (DecisionStep step : stepsByNodeId.values()) {
      summary.append(summary.length() == 0 ? "" : "  →  ").append(step.getAnswer());
    }

    return summary.length() == 0 ? "(none)" : summary.toString();
  }

  // ---------------------------------------------------------------- side panel

  private void showNodeDetails(DecisionTreeNode data) {
    if (data == null) {
      selectedStep = null;
      showExchangeButton.setEnabled(false);
      nodePane.setText(
          html("<p style='color:#777'>Select a node in the tree to see its question and the"
              + " answer given for it.</p>"));
      nodePane.setCaretPosition(0);
      return;
    }

    final StringBuilder body = new StringBuilder();
    body.append(badge(data.getKind())).append("<div style='padding:8px 2px'>");

    final DecisionStep step = data.getNodeId() == null ? null : stepsByNodeId.get(data.getNodeId());

    selectedStep = step;
    showExchangeButton.setEnabled(step != null);

    if (step != null) {
      rawField(
          body,
          "Answer given by the AI",
          "<span style='color:"
              + toHex(DecisionTreeNode.Kind.RESULT.getColor())
              + "; font-weight:bold'>"
              + escape(step.getAnswer())
              + "</span>");
      field(body, "Justification", step.getJustification());
      field(body, "Step", "#" + step.getNumber());
    } else if (data.getKind() == DecisionTreeNode.Kind.QUESTION) {
      body.append("<p style='color:#777; margin:0 0 10px 0'>This question was not reached by the"
          + " walk.</p>");
    }

    if (data.getKind() == DecisionTreeNode.Kind.QUESTION) {
      field(body, "Question", data.getQuestion());
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

    if (step != null) {
      rightTabs.setSelectedIndex(TAB_NODE);
    }
  }

  /** Opens the full prompt and raw answer of the selected node in the conversation log. */
  private void showSelectedExchange() {
    if (selectedStep != null) {
      ApplicationManager.instance()
          .getViewManager()
          .showDialog(new LlmConversationLogView(selectedStep.getExchange()));
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
