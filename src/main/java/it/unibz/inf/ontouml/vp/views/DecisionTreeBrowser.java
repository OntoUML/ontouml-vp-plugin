package it.unibz.inf.ontouml.vp.views;

import com.google.gson.Gson;
import it.unibz.inf.ontouml.vp.model.DecisionNode;
import it.unibz.inf.ontouml.vp.model.DecisionOption;
import it.unibz.inf.ontouml.vp.model.DecisionTree;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

/**
 * Renders a decision tree JSON as a navigable {@link JTree}, resolving each option's "next" into
 * the question it points at so the structure reads top-down.
 *
 * <p>Shared by the settings dialog, which browses the configured tree, and by the detailed
 * evaluation window, which walks it: nodes can be annotated with the answer the AI gave, which the
 * renderer marks so a run is visible on the tree itself.
 */
public class DecisionTreeBrowser extends JPanel {

  private static final long serialVersionUID = 1L;

  /** Questions are long, so a row shows a prefix and keeps the whole text in the tooltip. */
  private static final int MAX_LABEL_LENGTH = 110;

  /** Guards against a hand-edited tree that points back at itself. */
  private static final int MAX_TREE_DEPTH = 25;

  private static final Color CURRENT_BACKGROUND = new Color(0xfff3c4);
  private static final Color ANSWERED_BACKGROUND = new Color(0xe4f2e4);

  private final JTree tree;

  /**
   * Execution state per node id. A hand-edited tree that reaches one node from two branches would
   * light up both copies, which is acceptable for a viewer.
   */
  private final Map<String, String> answersByNodeId = new HashMap<>();

  private String currentNodeId;

  public DecisionTreeBrowser() {
    super(new BorderLayout());

    tree = new JTree(new DefaultMutableTreeNode());
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.setCellRenderer(new DecisionTreeCellRenderer());
    tree.setRootVisible(true);
    tree.setShowsRootHandles(true);
    ToolTipManager.sharedInstance().registerComponent(tree);

    add(new JScrollPane(tree), BorderLayout.CENTER);
  }

  // ---------------------------------------------------------------- public API

  /** Rebuilds the tree from the given JSON, dropping any execution annotations. */
  public void setJson(String json) {
    answersByNodeId.clear();
    currentNodeId = null;

    tree.setModel(new DefaultTreeModel(buildRootNode(json)));
    expandAll();
  }

  public void setSelectionListener(Consumer<DecisionTreeNode> listener) {
    tree.addTreeSelectionListener(e -> listener.accept(getSelectedNode()));
  }

  public DecisionTreeNode getSelectedNode() {
    return nodeDataOf(tree.getLastSelectedPathComponent());
  }

  /** Marks the node whose question is being asked right now. */
  public void markCurrent(String nodeId) {
    currentNodeId = nodeId;
    revealAndRepaint(nodeId);
  }

  /** Records the answer given for a node, so the row shows it was visited. */
  public void markAnswered(String nodeId, String answer) {
    answersByNodeId.put(nodeId, answer);

    if (nodeId != null && nodeId.equals(currentNodeId)) {
      currentNodeId = null;
    }

    revealAndRepaint(nodeId);
  }

  public void clearAnnotations() {
    answersByNodeId.clear();
    currentNodeId = null;
    repaint();
  }

  /** Selects the row of the given node id, if it is in the tree. */
  public void selectNode(String nodeId) {
    final DefaultMutableTreeNode match = findNode(nodeId);

    if (match != null) {
      tree.setSelectionPath(new javax.swing.tree.TreePath(match.getPath()));
    }
  }

  public void expandAll() {
    for (int row = 0; row < tree.getRowCount(); row++) {
      tree.expandRow(row);
    }
  }

  public void collapseAll() {
    for (int row = tree.getRowCount() - 1; row > 0; row--) {
      tree.collapseRow(row);
    }
  }

  private void revealAndRepaint(String nodeId) {
    final DefaultMutableTreeNode match = findNode(nodeId);

    if (match != null) {
      tree.scrollPathToVisible(new javax.swing.tree.TreePath(match.getPath()));
    }

    repaint();
  }

  private DefaultMutableTreeNode findNode(String nodeId) {
    if (nodeId == null) {
      return null;
    }

    final Object root = tree.getModel().getRoot();

    return root instanceof DefaultMutableTreeNode
        ? findNode((DefaultMutableTreeNode) root, nodeId)
        : null;
  }

  private DefaultMutableTreeNode findNode(DefaultMutableTreeNode from, String nodeId) {
    final DecisionTreeNode data = nodeDataOf(from);

    if (data != null && nodeId.equals(data.getNodeId())) {
      return from;
    }

    for (int i = 0; i < from.getChildCount(); i++) {
      final DefaultMutableTreeNode match =
          findNode((DefaultMutableTreeNode) from.getChildAt(i), nodeId);

      if (match != null) {
        return match;
      }
    }

    return null;
  }

  // ---------------------------------------------------------------- tree construction

  private DefaultMutableTreeNode buildRootNode(String json) {
    final DecisionTree decisionTree;

    try {
      decisionTree = new Gson().fromJson(json, DecisionTree.class);
    } catch (Exception e) {
      return problemNode("The JSON could not be parsed. Fix it in the JSON view.");
    }

    if (decisionTree == null || !decisionTree.isUsable()) {
      return problemNode("No usable tree: \"startNode\" is missing or points nowhere.");
    }

    return buildQuestionNode(decisionTree, decisionTree.getStartNodeId(), new Path(), 0);
  }

  private static DefaultMutableTreeNode problemNode(String explanation) {
    final DecisionTreeNode data =
        new DecisionTreeNode(DecisionTreeNode.Kind.PROBLEM, "! " + explanation);
    data.setExplanation(explanation);
    return new DefaultMutableTreeNode(data);
  }

  private DefaultMutableTreeNode buildQuestionNode(
      DecisionTree tree, String nodeId, Path path, int depth) {
    final DecisionNode node = tree.getNode(nodeId);

    if (node == null) {
      return problemNode("missing node: " + nodeId);
    }

    final String question = node.getQuestion() == null ? "(no question)" : node.getQuestion();

    final DecisionTreeNode data =
        new DecisionTreeNode(DecisionTreeNode.Kind.QUESTION, "Q · " + truncate(question));
    data.setNodeId(nodeId);
    data.setQuestion(question);
    data.setBreadcrumb(path.answersAsText());

    for (DecisionOption option : node.getOptions()) {
      data.getAnswers().add(describeOption(tree, option));
    }

    final DefaultMutableTreeNode questionNode = new DefaultMutableTreeNode(data);

    path.push(nodeId, null);

    for (DecisionOption option : node.getOptions()) {
      questionNode.add(buildOptionNode(tree, option, question, path, depth));
    }

    path.pop();

    return questionNode;
  }

  /** One line describing where an option goes, used in the question's list of answers. */
  private static String describeOption(DecisionTree tree, DecisionOption option) {
    final String answer = option.getAnswer() == null ? "(no answer)" : option.getAnswer();

    if (option.isLeaf()) {
      return answer + "  →  «" + option.getStereotype() + "»";
    }

    final DecisionNode next = tree.getNode(option.getNext());

    return answer
        + "  →  "
        + (next == null
            ? "missing node \"" + option.getNext() + "\""
            : truncate(next.getQuestion()));
  }

  private DefaultMutableTreeNode buildOptionNode(
      DecisionTree tree, DecisionOption option, String parentQuestion, Path path, int depth) {
    final String answer = option.getAnswer() == null ? "(no answer)" : option.getAnswer();

    if (option.isLeaf()) {
      // an option that reaches a stereotype ends the branch, so it is drawn as a highlighted leaf
      final DecisionTreeNode data =
          new DecisionTreeNode(
              DecisionTreeNode.Kind.RESULT,
              "A · " + answer + "  →  «" + option.getStereotype() + "»");
      data.setAnswer(answer);
      data.setQuestion(parentQuestion);
      data.setStereotype(option.getStereotype());
      data.setBreadcrumb(path.answersAsTextWith(answer));

      return new DefaultMutableTreeNode(data);
    }

    final DecisionTreeNode data =
        new DecisionTreeNode(DecisionTreeNode.Kind.OPTION, "A · " + answer);
    data.setAnswer(answer);
    data.setQuestion(parentQuestion);
    data.setBreadcrumb(path.answersAsTextWith(answer));

    final DefaultMutableTreeNode optionNode = new DefaultMutableTreeNode(data);

    if (option.getNext() == null) {
      optionNode.add(problemNode("this option has neither \"next\" nor \"stereotype\""));
      return optionNode;
    }

    if (path.contains(option.getNext())) {
      optionNode.add(problemNode("loops back to \"" + option.getNext() + "\""));
      return optionNode;
    }

    if (depth >= MAX_TREE_DEPTH) {
      optionNode.add(problemNode("tree is too deep to render"));
      return optionNode;
    }

    final DecisionNode next = tree.getNode(option.getNext());
    data.setLeadsTo(next == null ? null : next.getQuestion());

    path.push(null, answer);
    optionNode.add(buildQuestionNode(tree, option.getNext(), path, depth + 1));
    path.pop();

    return optionNode;
  }

  /** The chain of node ids and answers taken to reach the node being built. */
  private static class Path {

    private final List<String> nodeIds = new ArrayList<>();
    private final List<String> answers = new ArrayList<>();

    void push(String nodeId, String answer) {
      nodeIds.add(nodeId);
      answers.add(answer);
    }

    void pop() {
      nodeIds.remove(nodeIds.size() - 1);
      answers.remove(answers.size() - 1);
    }

    boolean contains(String nodeId) {
      return nodeIds.contains(nodeId);
    }

    String answersAsText() {
      final StringBuilder text = new StringBuilder();

      for (String answer : answers) {
        if (answer != null) {
          text.append(text.length() == 0 ? "" : "  →  ").append(answer);
        }
      }

      return text.length() == 0 ? "(root)" : text.toString();
    }

    String answersAsTextWith(String answer) {
      final String soFar = answersAsText();
      return "(root)".equals(soFar) ? answer : soFar + "  →  " + answer;
    }
  }

  // ---------------------------------------------------------------- rendering

  private class DecisionTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 1L;

    @Override
    public Component getTreeCellRendererComponent(
        JTree tree,
        Object value,
        boolean selected,
        boolean expanded,
        boolean leaf,
        int row,
        boolean hasFocus) {
      super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

      // labels are plain text and styled here: HTML labels wrap mid-text inside a tree row
      setIcon(null);
      setToolTipText(null);
      setOpaque(false);

      final DecisionTreeNode data = nodeDataOf(value);

      if (data == null) {
        return this;
      }

      if (!selected) {
        setForeground(data.getKind().getColor());
      }

      setFont(
          getFont()
              .deriveFont(
                  data.getKind() == DecisionTreeNode.Kind.QUESTION ? Font.PLAIN : Font.BOLD));

      final String nodeId = data.getNodeId();

      if (nodeId != null && !selected) {
        if (nodeId.equals(currentNodeId)) {
          setOpaque(true);
          setBackground(CURRENT_BACKGROUND);
        } else if (answersByNodeId.containsKey(nodeId)) {
          setOpaque(true);
          setBackground(ANSWERED_BACKGROUND);
        }
      }

      String tooltip = data.getTooltip();
      final String answered = nodeId == null ? null : answersByNodeId.get(nodeId);

      if (answered != null) {
        setText(getText() + "    ✔ " + answered);
        tooltip = "Answered: " + answered + (tooltip == null ? "" : "\n\n" + tooltip);
      }

      if (tooltip != null) {
        setToolTipText("<html><body style='width: 380px'>" + escape(tooltip) + "</body></html>");
      }

      return this;
    }
  }

  private static DecisionTreeNode nodeDataOf(Object value) {
    if (!(value instanceof DefaultMutableTreeNode)) {
      return null;
    }

    final Object userObject = ((DefaultMutableTreeNode) value).getUserObject();

    return userObject instanceof DecisionTreeNode ? (DecisionTreeNode) userObject : null;
  }

  // ---------------------------------------------------------------- shared formatting helpers

  public static String truncate(String text) {
    final String singleLine = (text == null ? "" : text).replaceAll("\\s+", " ").trim();
    return singleLine.length() <= MAX_LABEL_LENGTH
        ? singleLine
        : singleLine.substring(0, MAX_LABEL_LENGTH) + "...";
  }

  public static String escape(String text) {
    return text == null
        ? ""
        : text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
  }

  public static void field(StringBuilder body, String label, String value) {
    rawField(body, label, value == null ? null : escape(value));
  }

  public static void rawField(StringBuilder body, String label, String value) {
    if (value == null || value.trim().isEmpty()) {
      return;
    }

    body.append("<p style='margin:0 0 10px 0'><span style='color:#777; font-size:9px'>")
        .append(label.toUpperCase())
        .append("</span><br>")
        .append(value)
        .append("</p>");
  }

  public static void list(StringBuilder body, String label, List<String> values) {
    if (values.isEmpty()) {
      return;
    }

    body.append("<p style='margin:0 0 4px 0'><span style='color:#777; font-size:9px'>")
        .append(label.toUpperCase())
        .append("</span></p><ul style='margin:0 0 10px 0; padding-left:16px'>");

    for (String value : values) {
      body.append("<li>").append(escape(value)).append("</li>");
    }

    body.append("</ul>");
  }

  public static String html(String body) {
    return "<html><body style='font-family:sans-serif; font-size:11px; margin:0'>"
        + body
        + "</body></html>";
  }

  public static String toHex(Color color) {
    return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
  }

  /** The coloured type badge both side panels put at the top of a node's details. */
  public static String badge(DecisionTreeNode.Kind kind) {
    return "<div style='background:"
        + toHex(kind.getColor())
        + "; color:#ffffff; padding:4px 8px; font-weight:bold'>"
        + kind.getTitle()
        + "</div>";
  }
}
