package it.unibz.inf.ontouml.vp.views;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IClass;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;
import it.unibz.inf.ontouml.vp.model.LlmExchange;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

/**
 * Shows the documentation proposed for a class next to the one it already has, so nothing is
 * overwritten unseen. The proposal is editable before being applied.
 */
public class DocumentClassView implements IDialogHandler {

  private final IClass targetClass;
  private final String proposal;
  private final LlmExchange exchange;

  private IDialog dialog;
  private JTextArea proposalArea;
  private JCheckBox addReferencesBox;
  private boolean cancelledExit = true;

  public DocumentClassView(IClass targetClass, String proposal, LlmExchange exchange) {
    this.targetClass = targetClass;
    this.proposal = proposal;
    this.exchange = exchange;
  }

  public boolean wasApplied() {
    return !cancelledExit;
  }

  /** The text to write onto the class, after any edit the user made. */
  public String getDocumentation() {
    return proposalArea.getText();
  }

  @Override
  public Component getComponent() {
    final JPanel panel = new JPanel(new BorderLayout(0, 8));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    final JLabel title = new JLabel("Documentation for \"" + targetClass.getName() + "\"");
    title.setFont(title.getFont().deriveFont(Font.BOLD));
    panel.add(title, BorderLayout.NORTH);

    panel.add(buildComparison(), BorderLayout.CENTER);
    panel.add(buildButtons(), BorderLayout.SOUTH);

    return panel;
  }

  private JSplitPane buildComparison() {
    final String current = targetClass.getDescription();

    final JTextArea currentArea =
        buildArea(
            current == null || current.trim().isEmpty()
                ? "(this class has no documentation yet)"
                : current);
    currentArea.setEditable(false);
    currentArea.setForeground(Color.DARK_GRAY);

    proposalArea = buildArea(proposal);

    final JSplitPane split =
        new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            titled("Current documentation", currentArea),
            titled("Proposed documentation (editable)", proposalArea));
    split.setResizeWeight(0.5);
    split.setDividerLocation(400);
    split.setPreferredSize(new Dimension(820, 460));

    return split;
  }

  private JTextArea buildArea(String content) {
    final JTextArea area = new JTextArea(content);
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    area.setCaretPosition(0);
    return area;
  }

  private JPanel titled(String label, JTextArea area) {
    final JPanel panel = new JPanel(new BorderLayout(0, 4));

    final JLabel heading = new JLabel(label);
    heading.setEnabled(false);

    panel.add(heading, BorderLayout.NORTH);
    panel.add(new JScrollPane(area), BorderLayout.CENTER);

    return panel;
  }

  /** Whether the cited sources should also be written to the class' References tab. */
  public boolean shouldAddReferences() {
    return addReferencesBox.isSelected();
  }

  private JPanel buildButtons() {
    final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    addReferencesBox = new JCheckBox("Also add sources to the References tab", true);
    addReferencesBox.setToolTipText(
        "Creates one URL reference per cited source. Sources without a URL stay in the text only.");
    buttons.add(addReferencesBox);

    final JButton logButton = new JButton("Show full log...");
    logButton.setToolTipText("Shows the whole prompt sent to the AI and its raw answer.");
    logButton.addActionListener(
        e ->
            ApplicationManager.instance()
                .getViewManager()
                .showDialog(new LlmConversationLogView(exchange)));

    final JButton applyButton = new JButton("Apply");
    applyButton.setToolTipText("Writes the text on the right into the class' Description.");
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

    buttons.add(logButton);
    buttons.add(applyButton);
    buttons.add(cancelButton);

    return buttons;
  }

  @Override
  public void prepare(IDialog dialog) {
    this.dialog = dialog;
    dialog.setTitle("Document class");
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
