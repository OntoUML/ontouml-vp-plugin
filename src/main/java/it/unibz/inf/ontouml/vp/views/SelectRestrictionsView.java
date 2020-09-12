package it.unibz.inf.ontouml.vp.views;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.utils.StereotypesManager;

public class SelectRestrictionsView implements IDialogHandler {

   private IDialog dialog;
   private JPanel selectionPanel;

   private boolean cancelledExit = true;
   private String initialSelection;
   private List<String> initialSelectionList;
   private List<String> selectableRestrictedTo;

   public SelectRestrictionsView(String initialSelection, List<String> selectableRestrictedTo) {
      super();

      this.initialSelection = initialSelection;
      this.initialSelectionList = Arrays.asList(initialSelection.split("\\s+"));
      this.selectableRestrictedTo = selectableRestrictedTo;

      this.selectionPanel = new JPanel();
      this.selectionPanel.setBorder(new EmptyBorder(7, 0, 0, 0));

      this.createDialogContents();
   }

   @Override
   public Component getComponent() {
      final JLabel line = new JLabel("Select the possible ontological natures of the type's instances.");
      final JButton cancelButton = new JButton("Cancel");
      final JButton applyButton = new JButton("Apply");

      applyButton.addActionListener( e -> {
         cancelledExit = false;
         dialog.close();
      });

      cancelButton.addActionListener( e -> {
         cancelledExit = true;
         dialog.close();
      });

      JPanel panel = new JPanel();

      panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

      panel.add(line);
      panel.add(this.selectionPanel);

      JPanel buttonsPanel = new JPanel();
      buttonsPanel.setLayout(new FlowLayout());
      buttonsPanel.add(applyButton);
      buttonsPanel.add(cancelButton);
      panel.add(buttonsPanel);

      line.setAlignmentX(Component.LEFT_ALIGNMENT);
      this.selectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

      return panel;
   }

   @Override
   public void prepare(IDialog dialog) {
      this.dialog = dialog;
      dialog.setTitle("Restrict allowed natures");
      dialog.pack();
   }

   @Override
   public boolean canClosed() {
      return true;
   }

   @Override
   public void shown() {
   }

   public String getSelectedValues() {
      if (this.cancelledExit) {
         return this.initialSelection;
      }

      final StringBuilder selectedValues = new StringBuilder();
      final Component[] checkBoxes = this.selectionPanel.getComponents();

      for (int i = 0; checkBoxes != null && i < checkBoxes.length; i++) {
         if (checkBoxes[i] instanceof JCheckBox) {
            final JCheckBox checkBox = (JCheckBox) checkBoxes[i];
            if (checkBox.isSelected()) {
               selectedValues.append(selectedValues.length() == 0 ? checkBox.getText() : " " + checkBox.getText());
            }
         }
      }

      return selectedValues.toString();
   }

   private void addCheckbox(String restrictedTo) {
      JCheckBox checkBox = new JCheckBox(restrictedTo);
      selectionPanel.add(checkBox);

      checkBox.setSelected(initialSelectionList.contains(restrictedTo));

      final boolean isSmartModelingEnabled =
              Configurations.getInstance().getProjectConfigurations().isSmartModellingEnabled();

      if(isSmartModelingEnabled)
         checkBox.setEnabled(selectableRestrictedTo.contains(restrictedTo));
   }

   public void createDialogContents() {
      this.selectionPanel.setLayout(new GridLayout(4, 3));

      // Line 1, Column 1
      addCheckbox(StereotypesManager.RESTRICTED_TO_COLLECTIVE);
      // Line 1, Column 2
      addCheckbox(StereotypesManager.RESTRICTED_TO_INTRINSIC_MODE);
      // Line 1, Column 3
      addCheckbox(StereotypesManager.RESTRICTED_TO_ABSTRACT);

      // Line 2, Column 1
      addCheckbox(StereotypesManager.RESTRICTED_TO_FUNCTIONAL_COMPLEX);
      // Line 2, Column 2
      addCheckbox(StereotypesManager.RESTRICTED_TO_EXTRINSIC_MODE);
      // Line 2, Column 3
      addCheckbox(StereotypesManager.RESTRICTED_TO_EVENT);

      // Line 3, Column 1
      addCheckbox(StereotypesManager.RESTRICTED_TO_QUANTITY);
      // Line 3, Column 2
      addCheckbox(StereotypesManager.RESTRICTED_TO_QUALITY);
      // Line 3, Column 3
      addCheckbox(StereotypesManager.RESTRICTED_TO_SITUATION);

      // Line 4, Column 1 (empty)
      this.selectionPanel.add(new JLabel());
      // Line 4, Column 2
      addCheckbox(StereotypesManager.RESTRICTED_TO_RELATOR);
      // Line 4, Column 3
      addCheckbox(StereotypesManager.RESTRICTED_TO_TYPE);
   }


}