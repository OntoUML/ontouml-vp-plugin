package it.unibz.inf.ontouml.vp.views;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IClass;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;
import it.unibz.inf.ontouml.vp.model.LlmExchange;
import it.unibz.inf.ontouml.vp.model.MetaclassSuggestion;
import it.unibz.inf.ontouml.vp.utils.OntoUMLConstraintsManager;
import it.unibz.inf.ontouml.vp.utils.Stereotype;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

/**
 * Result dialog for the "Suggest Metaclass (AI)" feature: presents the AI's suggested
 * stereotype, the decision path it followed, and its rationale, and allows the user to
 * apply the suggestion (or override it with any other valid class stereotype) or cancel.
 */
public class SuggestMetaclassView implements IDialogHandler {

  private final IClass targetClass;
  private final MetaclassSuggestion suggestion;
  private final LlmExchange exchange;

  private IDialog dialog;
  private boolean cancelledExit = true;

  private JComboBox<String> stereotypeComboBox;
  private JLabel warningLabel;
  private JButton applyButton;

  private JButton detailedButton;
  private JLabel detailedStatusLabel;

  public SuggestMetaclassView(
      IClass targetClass, MetaclassSuggestion suggestion, LlmExchange exchange) {
    this.targetClass = targetClass;
    this.suggestion = suggestion;
    this.exchange = exchange;
  }

  @Override
  public Component getComponent() {
    final JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    panel.setBorder(new EmptyBorder(10, 10, 10, 10));

    final String currentStereotype = currentStereotype();

    final JLabel titleLabel =
        new JLabel("Suggested metaclass for \"" + targetClass.getName() + "\"");
    titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
    titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    panel.add(titleLabel);

    final JLabel suggestedLabel =
        new JLabel(
            currentStereotype == null
                ? "«" + suggestion.getStereotype() + "»"
                : "«" + currentStereotype + "»  →  «" + suggestion.getStereotype() + "»");
    suggestedLabel.setFont(suggestedLabel.getFont().deriveFont(Font.BOLD, 16f));
    suggestedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    suggestedLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
    panel.add(suggestedLabel);

    if (isCurrentStereotypeContested(currentStereotype)) {
      panel.add(sectionLabel("Why «" + currentStereotype + "» does not hold:"));

      final JTextArea issueArea = new JTextArea(suggestion.getCurrentStereotypeIssue());
      issueArea.setEditable(false);
      issueArea.setLineWrap(true);
      issueArea.setWrapStyleWord(true);
      issueArea.setBackground(panel.getBackground());
      issueArea.setForeground(Color.RED.darker());
      issueArea.setBorder(BorderFactory.createEmptyBorder());
      issueArea.setAlignmentX(Component.LEFT_ALIGNMENT);
      panel.add(issueArea);
    }

    if (suggestion.getDecisionPath() != null && !suggestion.getDecisionPath().isEmpty()) {
      panel.add(sectionLabel("Decision path:"));
      final JList<String> decisionPathList =
          new JList<>(suggestion.getDecisionPath().toArray(new String[0]));
      decisionPathList.setEnabled(false);
      final JScrollPane decisionPathScroll = new JScrollPane(decisionPathList);
      decisionPathScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
      decisionPathScroll.setPreferredSize(new Dimension(480, 100));
      decisionPathScroll.setMaximumSize(new Dimension(480, 100));
      panel.add(decisionPathScroll);
    }

    panel.add(sectionLabel("Rationale:"));
    final JTextArea rationaleArea = new JTextArea(suggestion.getRationale());
    rationaleArea.setEditable(false);
    rationaleArea.setLineWrap(true);
    rationaleArea.setWrapStyleWord(true);
    rationaleArea.setBackground(panel.getBackground());
    rationaleArea.setBorder(BorderFactory.createEmptyBorder());
    rationaleArea.setAlignmentX(Component.LEFT_ALIGNMENT);
    panel.add(rationaleArea);

    panel.add(buildDetailedEvaluationSection());

    panel.add(sectionLabel("Apply stereotype:"));
    stereotypeComboBox =
        new JComboBox<>(Stereotype.getOntoUMLClassStereotypeNames().toArray(new String[0]));
    stereotypeComboBox.setSelectedItem(suggestion.getStereotype());
    stereotypeComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    stereotypeComboBox.setMaximumSize(new Dimension(480, 30));
    stereotypeComboBox.addActionListener(e -> updateWarning());
    panel.add(stereotypeComboBox);

    warningLabel = new JLabel(" ");
    warningLabel.setForeground(Color.RED.darker());
    warningLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    panel.add(warningLabel);

    final JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

    final JButton logButton = new JButton("Show full log...");
    logButton.setToolTipText("Shows the whole prompt sent to the AI and its raw answer.");
    logButton.addActionListener(
        e ->
            ApplicationManager.instance()
                .getViewManager()
                .showDialog(new LlmConversationLogView(exchange)));
    buttonsPanel.add(logButton);

    applyButton = new JButton("Apply");
    applyButton.addActionListener(
        e -> {
          cancelledExit = false;
          dialog.close();
        });

    final JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(
        e -> {
          cancelledExit = true;
          dialog.close();
        });

    buttonsPanel.add(applyButton);
    buttonsPanel.add(cancelButton);
    panel.add(buttonsPanel);

    updateWarning();

    return panel;
  }

  /** The OntoUML class stereotype currently on the class, or null when it carries none. */
  private String currentStereotype() {
    final String[] stereotypes = targetClass.toStereotypeArray();

    if (stereotypes == null || stereotypes.length != 1) {
      return null;
    }

    return Stereotype.getOntoUMLClassStereotypeNames().contains(stereotypes[0])
        ? stereotypes[0]
        : null;
  }

  /** True when the class already has a stereotype that the suggestion disagrees with. */
  private boolean isCurrentStereotypeContested(String currentStereotype) {
    return currentStereotype != null
        && !currentStereotype.equals(suggestion.getStereotype())
        && suggestion.hasCurrentStereotypeIssue();
  }


  /**
   * The opt-in, node-by-node strategy: instead of one request for the whole classification, it
   * opens a window that walks the decision tree, asking the AI each question separately and showing
   * the run on the tree itself. Applying stays here, so there is a single apply path.
   */
  private JPanel buildDetailedEvaluationSection() {
    final JPanel section = new JPanel();
    section.setLayout(new BoxLayout(section, BoxLayout.PAGE_AXIS));
    section.setAlignmentX(Component.LEFT_ALIGNMENT);

    final JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    header.setAlignmentX(Component.LEFT_ALIGNMENT);

    detailedButton = new JButton("Run detailed evaluation...");
    detailedButton.setToolTipText(
        "Opens a window that walks the decision tree, asking the AI one question per node.");
    detailedButton.addActionListener(e -> openDetailedEvaluation());
    header.add(detailedButton);

    detailedStatusLabel = new JLabel(" ");
    detailedStatusLabel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
    header.add(detailedStatusLabel);

    section.add(sectionLabel("Detailed evaluation (one question per decision-tree node):"));
    section.add(header);

    return section;
  }

  private void openDetailedEvaluation() {
    final DetailedEvaluationView view = new DetailedEvaluationView(targetClass);
    ApplicationManager.instance().getViewManager().showDialog(view);

    final String stereotype = view.getReachedStereotype();

    if (stereotype == null) {
      detailedStatusLabel.setForeground(Color.GRAY);
      detailedStatusLabel.setText("No stereotype reached by the detailed walk.");
      return;
    }

    detailedStatusLabel.setForeground(new Color(0, 128, 0));
    detailedStatusLabel.setText("Detailed walk reached «" + stereotype + "» — selected below.");
    stereotypeComboBox.setSelectedItem(stereotype);
  }

  private JLabel sectionLabel(String text) {
    final JLabel label = new JLabel(text);
    label.setFont(label.getFont().deriveFont(Font.BOLD));
    label.setBorder(new EmptyBorder(8, 0, 2, 0));
    label.setAlignmentX(Component.LEFT_ALIGNMENT);
    return label;
  }

  private void updateWarning() {
    final String selected = getSelectedStereotype();
    final boolean isAllowed = OntoUMLConstraintsManager.isStereotypeAllowed(targetClass, selected);

    if (isAllowed) {
      warningLabel.setText(" ");
      applyButton.setEnabled(true);
    } else {
      warningLabel.setText(
          "⚠ «"
              + selected
              + "» is not structurally allowed given this class's current generalizations.");
      applyButton.setEnabled(false);
    }
  }

  @Override
  public void prepare(IDialog dialog) {
    this.dialog = dialog;
    dialog.setTitle("Suggest Metaclass (AI)");
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

  public boolean wasApplied() {
    return !cancelledExit;
  }

  public String getSelectedStereotype() {
    return (String) stereotypeComboBox.getSelectedItem();
  }
}
