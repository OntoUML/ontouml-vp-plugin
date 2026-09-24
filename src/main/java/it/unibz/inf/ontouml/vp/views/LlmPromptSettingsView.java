package it.unibz.inf.ontouml.vp.views;

import com.google.gson.Gson;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.DecisionNode;
import it.unibz.inf.ontouml.vp.model.DecisionOption;
import it.unibz.inf.ontouml.vp.model.DecisionTree;
import it.unibz.inf.ontouml.vp.model.LlmConfigurations;
import it.unibz.inf.ontouml.vp.utils.LlmPromptResources;
import it.unibz.inf.ontouml.vp.utils.Stereotype;
import static it.unibz.inf.ontouml.vp.views.DecisionTreeBrowser.badge;
import static it.unibz.inf.ontouml.vp.views.DecisionTreeBrowser.escape;
import static it.unibz.inf.ontouml.vp.views.DecisionTreeBrowser.field;
import static it.unibz.inf.ontouml.vp.views.DecisionTreeBrowser.html;
import static it.unibz.inf.ontouml.vp.views.DecisionTreeBrowser.list;
import static it.unibz.inf.ontouml.vp.views.DecisionTreeBrowser.rawField;
import static it.unibz.inf.ontouml.vp.views.DecisionTreeBrowser.toHex;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

/**
 * Lets the user read and edit the texts driving the AI classification: the guide used by the quick
 * strategy, the per-question prompt used by the detailed one, and the decision tree itself.
 *
 * <p>The decision tree is shown as a navigable tree by default, with the raw JSON available behind
 * a toggle for editing. Saving an area exactly as it ships stores no override, so the plugin keeps
 * following its own defaults as they evolve; "Restore defaults" puts every tab back to the bundled
 * text.
 */
public class LlmPromptSettingsView implements IDialogHandler {

  private static final String CARD_TREE = "tree";
  private static final String CARD_JSON = "json";
  private static final String CARD_TEMPLATE = "template";
  private static final String CARD_PROMPT = "prompt";

  /** Questions are long, so the tree shows a prefix and keeps the whole text in the tooltip. */
  private static final int MAX_LABEL_LENGTH = 110;

  /** Guards against a hand-edited tree that points back at itself. */
  private static final int MAX_TREE_DEPTH = 25;

  /** Enough to fix a tree in one go without the dialog outgrowing the screen. */
  private static final int MAX_REPORTED_PROBLEMS = 12;

  private IDialog dialog;

  private JTextArea systemPromptArea;
  private JTextArea nodePromptArea;
  private JTextArea decisionTreeArea;
  private JTextArea documentationTemplateArea;
  private JTextArea documentationPromptArea;
  private CardLayout documentationCards;
  private JPanel documentationCardPanel;

  private DecisionTreeBrowser treeBrowser;
  private JEditorPane detailPane;
  private CardLayout decisionTreeCards;
  private JPanel decisionTreeCardPanel;

  @Override
  public Component getComponent() {
    systemPromptArea = buildEditor(LlmPromptResources.getSystemPrompt());
    nodePromptArea = buildEditor(LlmPromptResources.getNodePrompt());
    decisionTreeArea = buildEditor(LlmPromptResources.getDecisionTreeJson());

    final JTabbedPane tabs = new JTabbedPane();
    tabs.addTab(
        "Quick classification prompt",
        wrap(systemPromptArea, "Sent as the system prompt of the single-request classification."));
    tabs.addTab(
        "Decision node prompt",
        wrap(
            nodePromptArea,
            "Used once per question by the detailed evaluation."
                + " Placeholders: {class}, {path}, {question}, {options}."));
    tabs.addTab("Decision tree", buildDecisionTreeTab());
    tabs.addTab("Documentation", buildDocumentationTab());

    final JPanel panel = new JPanel(new BorderLayout(0, 8));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    panel.add(tabs, BorderLayout.CENTER);
    panel.add(buildButtonsPanel(), BorderLayout.SOUTH);

    return panel;
  }

  private JTextArea buildEditor(String content) {
    final JTextArea area = new JTextArea(content);
    area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    area.setCaretPosition(0);
    return area;
  }

  private JPanel wrap(JTextArea area, String hint) {
    final JPanel panel = new JPanel(new BorderLayout(0, 6));
    panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

    final JLabel hintLabel = new JLabel(hint);
    hintLabel.setEnabled(false);
    panel.add(hintLabel, BorderLayout.NORTH);

    final JScrollPane scroll = new JScrollPane(area);
    scroll.setPreferredSize(new Dimension(720, 420));
    panel.add(scroll, BorderLayout.CENTER);

    return panel;
  }

  // ---------------------------------------------------------------- decision tree tab

  private JPanel buildDecisionTreeTab() {
    final JPanel panel = new JPanel(new BorderLayout(0, 6));
    panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

    treeBrowser = new DecisionTreeBrowser();
    treeBrowser.setSelectionListener(this::showSelectedNodeDetails);

    detailPane = new JEditorPane("text/html", "");
    detailPane.setEditable(false);
    detailPane.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

    final JScrollPane detailScroll = new JScrollPane(detailPane);
    detailScroll.setPreferredSize(new Dimension(300, 420));

    final JSplitPane treeSplit =
        new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeBrowser, detailScroll);
    treeSplit.setResizeWeight(0.6);
    treeSplit.setDividerLocation(430);
    treeSplit.setPreferredSize(new Dimension(740, 420));

    final JScrollPane jsonScroll = new JScrollPane(decisionTreeArea);
    jsonScroll.setPreferredSize(new Dimension(740, 420));

    decisionTreeCards = new CardLayout();
    decisionTreeCardPanel = new JPanel(decisionTreeCards);
    decisionTreeCardPanel.add(treeSplit, CARD_TREE);
    decisionTreeCardPanel.add(jsonScroll, CARD_JSON);

    panel.add(buildDecisionTreeHeader(), BorderLayout.NORTH);
    panel.add(decisionTreeCardPanel, BorderLayout.CENTER);

    rebuildDecisionTreeView();

    return panel;
  }

  private JPanel buildDocumentationTab() {
    documentationTemplateArea = buildEditor(LlmPromptResources.getDocumentationTemplate());
    documentationPromptArea = buildEditor(LlmPromptResources.getDocumentationPrompt());

    final JPanel panel = new JPanel(new BorderLayout(0, 6));
    panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

    final JLabel hint =
        new JLabel(
            "Used by \"Document or improve documentation...\" on a class. The template is the"
                + " structure the AI fills; the prompt is how it is told to fill it.");
    hint.setEnabled(false);

    final JToggleButton templateButton = new JToggleButton("Template", true);
    final JToggleButton promptButton = new JToggleButton("Prompt");

    final ButtonGroup group = new ButtonGroup();
    group.add(templateButton);
    group.add(promptButton);

    documentationCards = new CardLayout();
    documentationCardPanel = new JPanel(documentationCards);
    documentationCardPanel.add(scrolled(documentationTemplateArea), CARD_TEMPLATE);
    documentationCardPanel.add(scrolled(documentationPromptArea), CARD_PROMPT);

    templateButton.addActionListener(
        e -> documentationCards.show(documentationCardPanel, CARD_TEMPLATE));
    promptButton.addActionListener(
        e -> documentationCards.show(documentationCardPanel, CARD_PROMPT));

    final JPanel toggles = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
    toggles.add(templateButton);
    toggles.add(promptButton);

    final JPanel header = new JPanel(new BorderLayout(0, 4));
    header.add(hint, BorderLayout.NORTH);
    header.add(toggles, BorderLayout.SOUTH);

    panel.add(header, BorderLayout.NORTH);
    panel.add(documentationCardPanel, BorderLayout.CENTER);

    return panel;
  }

  private JScrollPane scrolled(JTextArea area) {
    final JScrollPane scroll = new JScrollPane(area);
    scroll.setPreferredSize(new Dimension(740, 420));
    return scroll;
  }

  private JPanel buildDecisionTreeHeader() {
    final JPanel header = new JPanel(new BorderLayout(0, 4));

    final JLabel hint =
        new JLabel(
            "The tree walked by the detailed evaluation. Each option either leads to the next"
                + " question or reaches a stereotype.");
    hint.setEnabled(false);

    final JToggleButton visualButton = new JToggleButton("Visual", true);
    visualButton.setToolTipText("Navigate the tree. Read-only.");
    final JToggleButton jsonButton = new JToggleButton("JSON (advanced)");
    jsonButton.setToolTipText("Edit the raw structure.");

    final ButtonGroup group = new ButtonGroup();
    group.add(visualButton);
    group.add(jsonButton);

    // switching back to the tree re-reads whatever was typed in the JSON view
    visualButton.addActionListener(
        e -> {
          rebuildDecisionTreeView();
          decisionTreeCards.show(decisionTreeCardPanel, CARD_TREE);
        });
    jsonButton.addActionListener(e -> decisionTreeCards.show(decisionTreeCardPanel, CARD_JSON));

    final JButton expandButton = new JButton("Expand all");
    expandButton.addActionListener(e -> treeBrowser.expandAll());

    final JButton collapseButton = new JButton("Collapse all");
    collapseButton.addActionListener(e -> treeBrowser.collapseAll());

    final JPanel toggles = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
    toggles.add(visualButton);
    toggles.add(jsonButton);
    toggles.add(Box.createHorizontalStrut(16));
    toggles.add(expandButton);
    toggles.add(collapseButton);

    header.add(hint, BorderLayout.NORTH);
    header.add(toggles, BorderLayout.SOUTH);

    return header;
  }

  /** Rebuilds the visual tree from the JSON currently in the editor. */
  private void rebuildDecisionTreeView() {
    treeBrowser.setJson(decisionTreeArea.getText());
    showSelectedNodeDetails(null);
  }

  // ---------------------------------------------------------------- side panel

  /** Renders the node picked in the tree; null clears the panel back to its hint. */
  private void showSelectedNodeDetails(DecisionTreeNode data) {
    if (data == null) {
      detailPane.setText(html("<p style='color:#777'>Select a node to see its details.</p>"));
      detailPane.setCaretPosition(0);
      return;
    }

    final StringBuilder body = new StringBuilder();

    body.append(badge(data.getKind())).append("<div style='padding:8px 2px'>");

    if (data.getKind() == DecisionTreeNode.Kind.QUESTION) {
      field(body, "Node id", data.getNodeId());
      field(body, "Question", data.getQuestion());
      list(body, "Answers", data.getAnswers());
    } else if (data.getKind() == DecisionTreeNode.Kind.RESULT) {
      field(body, "Answer", data.getAnswer());
      rawField(
          body,
          "Reaches stereotype",
          "<span style='color:"
              + toHex(DecisionTreeNode.Kind.RESULT.getColor())
              + "; font-weight:bold'>\u00ab"
              + escape(data.getStereotype())
              + "\u00bb</span>");
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

    detailPane.setText(html(body.toString()));
    detailPane.setCaretPosition(0);
  }

  // ---------------------------------------------------------------- buttons

  private JPanel buildButtonsPanel() {
    final JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    final JButton restoreButton = new JButton("Restore defaults");
    restoreButton.setToolTipText("Puts the three tabs back to the texts shipped with the plugin.");
    restoreButton.addActionListener(e -> restoreDefaults());

    final JButton saveButton = new JButton("Save");
    saveButton.addActionListener(e -> save());

    final JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> dialog.close());

    buttonsPanel.add(restoreButton);
    buttonsPanel.add(saveButton);
    buttonsPanel.add(cancelButton);

    return buttonsPanel;
  }

  private void restoreDefaults() {
    systemPromptArea.setText(LlmPromptResources.getDefaultSystemPrompt());
    nodePromptArea.setText(LlmPromptResources.getDefaultNodePrompt());
    decisionTreeArea.setText(LlmPromptResources.getDefaultDecisionTreeJson());

    systemPromptArea.setCaretPosition(0);
    nodePromptArea.setCaretPosition(0);
    decisionTreeArea.setCaretPosition(0);

    documentationTemplateArea.setText(LlmPromptResources.getDefaultDocumentationTemplate());
    documentationPromptArea.setText(LlmPromptResources.getDefaultDocumentationPrompt());
    documentationTemplateArea.setCaretPosition(0);
    documentationPromptArea.setCaretPosition(0);

    rebuildDecisionTreeView();
  }

  private void save() {
    final String tree = decisionTreeArea.getText();
    final List<String> problems = decisionTreeProblems(tree);

    if (!problems.isEmpty()) {
      final StringBuilder message = new StringBuilder("The decision tree has problems:\n\n");

      for (int i = 0; i < Math.min(problems.size(), MAX_REPORTED_PROBLEMS); i++) {
        message.append("• ").append(problems.get(i)).append("\n");
      }

      if (problems.size() > MAX_REPORTED_PROBLEMS) {
        message
            .append("• ... and ")
            .append(problems.size() - MAX_REPORTED_PROBLEMS)
            .append(" more\n");
      }

      message.append("\nFix them or press \"Restore defaults\" before saving.");

      JOptionPane.showMessageDialog(
          null, message.toString(), "Invalid decision tree", JOptionPane.ERROR_MESSAGE);
      return;
    }

    final LlmConfigurations config = Configurations.getInstance().getLlmConfigurations();

    // storing an unchanged text as an override would freeze it against future plugin updates
    config.setCustomSystemPrompt(
        overrideOrEmpty(systemPromptArea.getText(), LlmPromptResources.getDefaultSystemPrompt()));
    config.setCustomNodePrompt(
        overrideOrEmpty(nodePromptArea.getText(), LlmPromptResources.getDefaultNodePrompt()));
    config.setCustomDecisionTree(
        overrideOrEmpty(tree, LlmPromptResources.getDefaultDecisionTreeJson()));
    config.setCustomDocumentationTemplate(
        overrideOrEmpty(
            documentationTemplateArea.getText(),
            LlmPromptResources.getDefaultDocumentationTemplate()));
    config.setCustomDocumentationPrompt(
        overrideOrEmpty(
            documentationPromptArea.getText(), LlmPromptResources.getDefaultDocumentationPrompt()));

    Configurations.getInstance().save();
    dialog.close();
  }

  private static String overrideOrEmpty(String edited, String bundled) {
    return edited == null || edited.trim().equals(bundled.trim()) ? "" : edited;
  }

  /** Why the tree cannot be saved; a single entry when it is not even parseable. */
  private static List<String> decisionTreeProblems(String json) {
    try {
      final DecisionTree parsed = new Gson().fromJson(json, DecisionTree.class);

      if (parsed == null) {
        return Collections.singletonList("The text is empty.");
      }

      return parsed.findProblems(Stereotype.getOntoUMLClassStereotypeNames());
    } catch (Exception e) {
      return Collections.singletonList("It is not valid JSON: " + e.getMessage());
    }
  }

  @Override
  public void prepare(IDialog dialog) {
    this.dialog = dialog;
    dialog.setTitle("AI Prompts & Decision Tree");
    dialog.setModal(true);
    dialog.setResizable(true);
    dialog.pack();
  }

  @Override
  public boolean canClosed() {
    return true;
  }

  @Override
  public void shown() {}
}
