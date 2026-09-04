package it.unibz.inf.ontouml.vp.views;

import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;
import it.unibz.inf.ontouml.vp.model.LlmConversationLog;
import it.unibz.inf.ontouml.vp.model.LlmExchange;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

/**
 * Shows the LLM conversations of the current session: the prompts that were sent and the answers
 * that came back, so a suggestion can be inspected or a failure diagnosed.
 */
public class LlmConversationLogView implements IDialogHandler {

  private final LlmExchange preselected;

  private IDialog dialog;
  private JList<LlmExchange> exchangeList;
  private JTextArea detailArea;

  public LlmConversationLogView() {
    this(null);
  }

  /** Opens with the given conversation already selected, e.g. the one behind a suggestion. */
  public LlmConversationLogView(LlmExchange preselected) {
    this.preselected = preselected;
  }

  @Override
  public Component getComponent() {
    final List<LlmExchange> exchanges = LlmConversationLog.getInstance().getExchanges();

    final DefaultListModel<LlmExchange> listModel = new DefaultListModel<>();
    exchanges.forEach(listModel::addElement);

    exchangeList = new JList<>(listModel);
    exchangeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    exchangeList.addListSelectionListener(e -> showSelectedExchange());

    detailArea = new JTextArea();
    detailArea.setEditable(false);
    detailArea.setLineWrap(true);
    detailArea.setWrapStyleWord(true);
    detailArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

    final JScrollPane listScroll = new JScrollPane(exchangeList);
    listScroll.setPreferredSize(new Dimension(320, 420));

    final JScrollPane detailScroll = new JScrollPane(detailArea);
    detailScroll.setPreferredSize(new Dimension(620, 420));

    final JSplitPane splitPane =
        new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScroll, detailScroll);
    splitPane.setDividerLocation(320);

    final JPanel panel = new JPanel(new BorderLayout(0, 8));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    if (exchanges.isEmpty()) {
      panel.add(
          new JLabel("No AI conversation has been made in this session yet."), BorderLayout.NORTH);
    } else {
      panel.add(new JLabel("Conversations of this session, newest first:"), BorderLayout.NORTH);
    }

    panel.add(splitPane, BorderLayout.CENTER);
    panel.add(buildButtonsPanel(), BorderLayout.SOUTH);

    selectInitialExchange(exchanges);

    return panel;
  }

  private JPanel buildButtonsPanel() {
    final JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    final JButton copyButton = new JButton("Copy");
    copyButton.setToolTipText("Copies the selected conversation to the clipboard.");
    copyButton.addActionListener(
        e -> {
          final String text = detailArea.getText();
          if (!text.isEmpty()) {
            Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(text), null);
          }
        });

    final JButton closeButton = new JButton("Close");
    closeButton.addActionListener(e -> dialog.close());

    buttonsPanel.add(copyButton);
    buttonsPanel.add(closeButton);

    return buttonsPanel;
  }

  private void selectInitialExchange(List<LlmExchange> exchanges) {
    if (exchanges.isEmpty()) {
      return;
    }

    final int index = preselected == null ? 0 : exchanges.indexOf(preselected);
    exchangeList.setSelectedIndex(index < 0 ? 0 : index);
  }

  private void showSelectedExchange() {
    final LlmExchange selected = exchangeList.getSelectedValue();

    detailArea.setText(selected == null ? "" : selected.toDetailedText());
    detailArea.setCaretPosition(0);
  }

  @Override
  public void prepare(IDialog dialog) {
    this.dialog = dialog;
    dialog.setTitle("AI Conversation Log");
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
