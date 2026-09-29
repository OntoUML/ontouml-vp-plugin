package it.unibz.inf.ontouml.vp.views;

import com.google.gson.Gson;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.DecisionNode;
import it.unibz.inf.ontouml.vp.model.DecisionOption;
import it.unibz.inf.ontouml.vp.model.DecisionTree;
import it.unibz.inf.ontouml.vp.model.DocumentationLanguage;
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
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
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

/**
 * Lets the user read and edit every text driving the AI features: the two prompts of the
 * metaclass classification (quick and detailed, the latter with its own short system prompt), the
 * decision tree itself, the three texts around documenting a class (template, prompt and its
 * system prompt), and the one used to summarise a source cited in the model.
 *
 * <p>The decision tree is shown as a navigable tree by default, with the raw JSON available behind
 * a toggle for editing. Saving a text exactly as it ships stores no override, so the plugin keeps
 * following its own defaults as they evolve; "Restore defaults" puts every tab back to the bundled
 * text. The toolbar's Export bundles every one of these as a zip a maintainer can drop straight
 * into the plugin's {@code prompts/} folder in the repository; Import reads one back.
 */
public class LlmPromptSettingsView implements IDialogHandler {

  private static final String CARD_TREE = "tree";
  private static final String CARD_JSON = "json";

  /** Questions are long, so the tree shows a prefix and keeps the whole text in the tooltip. */
  private static final int MAX_LABEL_LENGTH = 110;

  /** Guards against a hand-edited tree that points back at itself. */
  private static final int MAX_TREE_DEPTH = 25;

  /** Enough to fix a tree in one go without the dialog outgrowing the screen. */
  private static final int MAX_REPORTED_PROBLEMS = 12;

  private IDialog dialog;

  /**
   * Every plain-text prompt but the decision tree, which needs its own JSON validation and so is
   * kept apart. This is what "Restore defaults", "Save", "Export" and "Import" all iterate over,
   * instead of each repeating the same handful of lines eight times.
   */
  private final List<PromptField> promptFields = new ArrayList<>();

  private JTextArea systemPromptArea;
  private JTextArea nodeSystemPromptArea;
  private JTextArea nodePromptArea;
  private JTextArea decisionTreeArea;
  private JTextArea documentationSystemPromptArea;
  private JTextArea documentationTemplateArea;
  private JTextArea documentationPromptArea;
  private JTextArea sourceSummaryPromptArea;
  private JComboBox<Object> documentationLanguageField;

  private DecisionTreeBrowser treeBrowser;
  private JEditorPane detailPane;
  private CardLayout decisionTreeCards;
  private JPanel decisionTreeCardPanel;

  /** One bundled text: the editor showing it, its default, and where an override is saved. */
  private static final class PromptField {

    private final String filename;
    private final JTextArea area;
    private final Supplier<String> defaultText;
    private final BiConsumer<LlmConfigurations, String> apply;

    private PromptField(
        String filename,
        JTextArea area,
        Supplier<String> defaultText,
        BiConsumer<LlmConfigurations, String> apply) {
      this.filename = filename;
      this.area = area;
      this.defaultText = defaultText;
      this.apply = apply;
    }

    private void restoreDefault() {
      area.setText(defaultText.get());
      area.setCaretPosition(0);
    }

    private void applyTo(LlmConfigurations config) {
      apply.accept(config, overrideOrEmpty(area.getText(), defaultText.get()));
    }
  }

  @Override
  public Component getComponent() {
    systemPromptArea = buildEditor(LlmPromptResources.getSystemPrompt());
    promptFields.add(
        new PromptField(
            LlmPromptResources.SYSTEM_PROMPT_FILENAME,
            systemPromptArea,
            LlmPromptResources::getDefaultSystemPrompt,
            LlmConfigurations::setCustomSystemPrompt));

    nodeSystemPromptArea = buildEditor(LlmPromptResources.getNodeSystemPrompt());
    promptFields.add(
        new PromptField(
            LlmPromptResources.NODE_SYSTEM_PROMPT_FILENAME,
            nodeSystemPromptArea,
            LlmPromptResources::getDefaultNodeSystemPrompt,
            LlmConfigurations::setCustomNodeSystemPrompt));

    nodePromptArea = buildEditor(LlmPromptResources.getNodePrompt());
    promptFields.add(
        new PromptField(
            LlmPromptResources.NODE_PROMPT_FILENAME,
            nodePromptArea,
            LlmPromptResources::getDefaultNodePrompt,
            LlmConfigurations::setCustomNodePrompt));

    decisionTreeArea = buildEditor(LlmPromptResources.getDecisionTreeJson());

    documentationSystemPromptArea = buildEditor(LlmPromptResources.getDocumentationSystemPrompt());
    promptFields.add(
        new PromptField(
            LlmPromptResources.DOCUMENTATION_SYSTEM_PROMPT_FILENAME,
            documentationSystemPromptArea,
            LlmPromptResources::getDefaultDocumentationSystemPrompt,
            LlmConfigurations::setCustomDocumentationSystemPrompt));

    documentationTemplateArea = buildEditor(LlmPromptResources.getDocumentationTemplate());
    promptFields.add(
        new PromptField(
            LlmPromptResources.DOCUMENTATION_TEMPLATE_FILENAME,
            documentationTemplateArea,
            LlmPromptResources::getDefaultDocumentationTemplate,
            LlmConfigurations::setCustomDocumentationTemplate));

    documentationPromptArea = buildEditor(LlmPromptResources.getDocumentationPrompt());
    promptFields.add(
        new PromptField(
            LlmPromptResources.DOCUMENTATION_PROMPT_FILENAME,
            documentationPromptArea,
            LlmPromptResources::getDefaultDocumentationPrompt,
            LlmConfigurations::setCustomDocumentationPrompt));

    sourceSummaryPromptArea = buildEditor(LlmPromptResources.getSourceSummaryPrompt());
    promptFields.add(
        new PromptField(
            LlmPromptResources.SOURCE_SUMMARY_PROMPT_FILENAME,
            sourceSummaryPromptArea,
            LlmPromptResources::getDefaultSourceSummaryPrompt,
            LlmConfigurations::setCustomSourceSummaryPrompt));

    documentationLanguageField = new JComboBox<>(DocumentationLanguage.values());
    documentationLanguageField.setMaximumRowCount(16);
    selectDocumentationLanguage(
        Configurations.getInstance().getLlmConfigurations().getDocumentationLanguage());
    documentationLanguageField.setToolTipText(
        "The language the model writes the documentation in, with its ISO 639-1 code. Only the"
            + " text under each heading is affected — the template's own section headings stay in"
            + " English.");

    final JTabbedPane tabs = new JTabbedPane();
    tabs.addTab(
        "Quick classification prompt",
        wrap(systemPromptArea, "Sent as the system prompt of the single-request classification."));
    tabs.addTab("Decision node prompt", buildDecisionNodeTab());
    tabs.addTab("Decision tree", buildDecisionTreeTab());
    tabs.addTab("Documentation", buildDocumentationTab());

    final JPanel panel = new JPanel(new BorderLayout(0, 8));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    panel.add(buildToolbar(), BorderLayout.NORTH);
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
    scroll.setPreferredSize(new Dimension(720, 410));
    panel.add(scroll, BorderLayout.CENTER);

    return panel;
  }

  // ---------------------------------------------------------------- toolbar

  /** Export bundles every bundled/overridden text into one zip; Import reads one back in. */
  private JToolBar buildToolbar() {
    final JToolBar toolbar = new JToolBar();
    toolbar.setFloatable(false);

    final JButton exportButton = new JButton("Export prompts...");
    exportButton.setToolTipText(
        "Saves every tab's current text as a .zip, one file per tab, named exactly as they are in"
            + " the plugin's own prompts/ folder — so the zip can be unpacked straight over that"
            + " folder in the repository to make these the new defaults.");
    exportButton.addActionListener(e -> exportPrompts());

    final JButton importButton = new JButton("Import prompts...");
    importButton.setToolTipText(
        "Reads a .zip exported this way (or built by hand with the same file names) and loads"
            + " whichever of its files match a tab here. Nothing is saved until \"Save\" is"
            + " pressed.");
    importButton.addActionListener(e -> importPrompts());

    toolbar.add(exportButton);
    toolbar.add(importButton);

    return toolbar;
  }

  private void exportPrompts() {
    final Path target = choosePath(FileDialog.SAVE, "Export prompts as...", "ontouml-prompts.zip");

    if (target == null) {
      return;
    }

    try (ZipOutputStream zip =
        new ZipOutputStream(Files.newOutputStream(target), StandardCharsets.UTF_8)) {
      for (PromptField field : promptFields) {
        writeEntry(zip, field.filename, field.area.getText());
      }

      writeEntry(zip, LlmPromptResources.DECISION_TREE_FILENAME, decisionTreeArea.getText());
    } catch (IOException e) {
      JOptionPane.showMessageDialog(
          null,
          "Could not write the zip file: " + e.getMessage(),
          "Export failed",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    JOptionPane.showMessageDialog(
        null,
        "Exported " + (promptFields.size() + 1) + " file(s) to " + target + ".",
        "Prompts exported",
        JOptionPane.INFORMATION_MESSAGE);
  }

  private static void writeEntry(ZipOutputStream zip, String name, String content)
      throws IOException {
    zip.putNextEntry(new ZipEntry(name));
    zip.write((content == null ? "" : content).getBytes(StandardCharsets.UTF_8));
    zip.closeEntry();
  }

  private void importPrompts() {
    final Path source = choosePath(FileDialog.LOAD, "Import prompts from...", null);

    if (source == null) {
      return;
    }

    final Map<String, String> byFilename = new LinkedHashMap<>();

    try (ZipInputStream zip = new ZipInputStream(Files.newInputStream(source))) {
      ZipEntry entry;

      while ((entry = zip.getNextEntry()) != null) {
        if (!entry.isDirectory()) {
          byFilename.put(entry.getName(), new String(readAll(zip), StandardCharsets.UTF_8));
        }

        zip.closeEntry();
      }
    } catch (IOException e) {
      JOptionPane.showMessageDialog(
          null,
          "Could not read the zip file: " + e.getMessage(),
          "Import failed",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    int applied = 0;

    for (PromptField field : promptFields) {
      if (byFilename.containsKey(field.filename)) {
        field.area.setText(byFilename.remove(field.filename));
        field.area.setCaretPosition(0);
        applied++;
      }
    }

    if (byFilename.containsKey(LlmPromptResources.DECISION_TREE_FILENAME)) {
      decisionTreeArea.setText(byFilename.remove(LlmPromptResources.DECISION_TREE_FILENAME));
      decisionTreeArea.setCaretPosition(0);
      rebuildDecisionTreeView();
      applied++;
    }

    final StringBuilder message = new StringBuilder();
    message.append("Loaded ").append(applied).append(" file(s) into their tabs.\n");

    if (!byFilename.isEmpty()) {
      message
          .append("Not recognised, and ignored: ")
          .append(String.join(", ", byFilename.keySet()))
          .append("\n");
    }

    message.append("\nNothing is saved yet — press \"Save\" below to keep these changes.");

    JOptionPane.showMessageDialog(
        null, message.toString(), "Prompts imported", JOptionPane.INFORMATION_MESSAGE);
  }

  private static byte[] readAll(InputStream in) throws IOException {
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final byte[] buffer = new byte[8192];
    int read;

    while ((read = in.read(buffer)) != -1) {
      out.write(buffer, 0, read);
    }

    return out.toByteArray();
  }

  /** The same {@link FileDialog} pattern the model export controllers already use. */
  private static Path choosePath(int mode, String title, String suggestedName) {
    final Frame rootFrame = (Frame) ApplicationManager.instance().getViewManager().getRootFrame();
    final FileDialog fileDialog = new FileDialog(rootFrame, title, mode);

    if (suggestedName != null) {
      fileDialog.setFile(suggestedName);
    }

    fileDialog.setFilenameFilter((dir, name) -> name != null && name.endsWith(".zip"));
    fileDialog.setVisible(true);

    final String directory = fileDialog.getDirectory();
    final String name = fileDialog.getFile();

    if (directory == null || name == null) {
      return null;
    }

    return Paths.get(directory, name.endsWith(".zip") ? name : name + ".zip");
  }

  // ---------------------------------------------------------------- decision node tab

  /** The short system prompt on top, the long per-question one below — one request needs both. */
  private JPanel buildDecisionNodeTab() {
    final JPanel panel = new JPanel(new BorderLayout(0, 6));
    panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

    final JPanel top = new JPanel(new BorderLayout(0, 4));
    final JLabel systemHint = new JLabel("System prompt, sent unchanged with every question:");
    systemHint.setEnabled(false);
    top.add(systemHint, BorderLayout.NORTH);

    final JScrollPane systemScroll = new JScrollPane(nodeSystemPromptArea);
    systemScroll.setPreferredSize(new Dimension(720, 70));
    top.add(systemScroll, BorderLayout.CENTER);

    final JPanel bottom = new JPanel(new BorderLayout(0, 4));
    final JLabel promptHint =
        new JLabel(
            "Used once per question by the detailed evaluation."
                + " Placeholders: {class}, {path}, {question}, {options}.");
    promptHint.setEnabled(false);
    bottom.add(promptHint, BorderLayout.NORTH);

    final JScrollPane promptScroll = new JScrollPane(nodePromptArea);
    promptScroll.setPreferredSize(new Dimension(720, 330));
    bottom.add(promptScroll, BorderLayout.CENTER);

    panel.add(top, BorderLayout.NORTH);
    panel.add(bottom, BorderLayout.CENTER);

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
    detailScroll.setPreferredSize(new Dimension(300, 410));

    final JSplitPane treeSplit =
        new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeBrowser, detailScroll);
    treeSplit.setResizeWeight(0.6);
    treeSplit.setDividerLocation(430);
    treeSplit.setPreferredSize(new Dimension(740, 410));

    final JScrollPane jsonScroll = new JScrollPane(decisionTreeArea);
    jsonScroll.setPreferredSize(new Dimension(740, 410));

    decisionTreeCards = new CardLayout();
    decisionTreeCardPanel = new JPanel(decisionTreeCards);
    decisionTreeCardPanel.add(treeSplit, CARD_TREE);
    decisionTreeCardPanel.add(jsonScroll, CARD_JSON);

    panel.add(buildDecisionTreeHeader(), BorderLayout.NORTH);
    panel.add(decisionTreeCardPanel, BorderLayout.CENTER);

    rebuildDecisionTreeView();

    return panel;
  }

  /**
   * Template, prompt, system prompt and source summaries, as sub-tabs of their own rather than a
   * pair of toggle buttons — there being four of them now is exactly why, and source summaries
   * belongs here rather than as a tab of its own because nothing outside documenting a class ever
   * reads it. All four are in English; {@code Documentation language} decides only what language
   * the model writes the class's actual documentation in, via the {@code {language}} placeholder
   * in the prompt — it does not change these bundled texts themselves.
   */
  private JPanel buildDocumentationTab() {
    final JPanel panel = new JPanel(new BorderLayout(0, 6));
    panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

    final JLabel hint =
        new JLabel(
            "Used by \"Document or improve documentation...\" on a class. The template is the"
                + " structure the AI fills; the prompt is how it is told to fill it; the system"
                + " prompt is sent alongside the prompt on every request; source summaries is the"
                + " system prompt used while reading the sources the class cites.");
    hint.setEnabled(false);

    final JPanel languageRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
    final JLabel languageLabel = new JLabel("Documentation language:");
    languageRow.add(languageLabel);
    languageRow.add(documentationLanguageField);

    final JPanel header = new JPanel(new BorderLayout(0, 4));
    header.add(hint, BorderLayout.NORTH);
    header.add(languageRow, BorderLayout.SOUTH);

    final JTabbedPane subTabs = new JTabbedPane();
    subTabs.addTab("Template", scrolled(documentationTemplateArea));
    subTabs.addTab("Prompt", scrolled(documentationPromptArea));
    subTabs.addTab("System prompt", scrolled(documentationSystemPromptArea));
    subTabs.addTab("Source summaries", scrolled(sourceSummaryPromptArea));

    panel.add(header, BorderLayout.NORTH);
    panel.add(subTabs, BorderLayout.CENTER);

    return panel;
  }

  /**
   * Selects the language a saved setting names. The setting used to be free text, so one that
   * names none of the listed languages is kept as an extra entry at the top rather than silently
   * replaced by a different language on the next save.
   */
  private void selectDocumentationLanguage(String setting) {
    final DocumentationLanguage language = DocumentationLanguage.fromSetting(setting);

    for (int i = documentationLanguageField.getItemCount() - 1; i >= 0; i--) {
      if (!(documentationLanguageField.getItemAt(i) instanceof DocumentationLanguage)) {
        documentationLanguageField.removeItemAt(i);
      }
    }

    if (language != null) {
      documentationLanguageField.setSelectedItem(language);
    } else {
      documentationLanguageField.insertItemAt(setting.trim(), 0);
      documentationLanguageField.setSelectedIndex(0);
    }
  }

  private JScrollPane scrolled(JTextArea area) {
    final JScrollPane scroll = new JScrollPane(area);
    scroll.setPreferredSize(new Dimension(740, 410));
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

    detailPane.setText(html(body.toString()));
    detailPane.setCaretPosition(0);
  }

  // ---------------------------------------------------------------- buttons

  private JPanel buildButtonsPanel() {
    final JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    final JButton restoreButton = new JButton("Restore defaults");
    restoreButton.setToolTipText("Puts every tab back to the text shipped with the plugin.");
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
    for (PromptField field : promptFields) {
      field.restoreDefault();
    }

    decisionTreeArea.setText(LlmPromptResources.getDefaultDecisionTreeJson());
    decisionTreeArea.setCaretPosition(0);

    selectDocumentationLanguage(LlmConfigurations.DEFAULT_DOCUMENTATION_LANGUAGE);

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
    for (PromptField field : promptFields) {
      field.applyTo(config);
    }

    config.setCustomDecisionTree(
        overrideOrEmpty(tree, LlmPromptResources.getDefaultDecisionTreeJson()));
    final Object language = documentationLanguageField.getSelectedItem();
    config.setDocumentationLanguage(
        language instanceof DocumentationLanguage
            ? ((DocumentationLanguage) language).getPromptValue()
            : String.valueOf(language));

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
