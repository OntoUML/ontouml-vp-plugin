package it.unibz.inf.ontouml.vp.views;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

public class SelectMultipleOptionsDialog implements IDialogHandler {

    private IDialog _dialog;
    // private Component _component;
    private JPanel _selectionsPane;
    private boolean cancelledExit = true;
    private String initialSelecteion;

    public SelectMultipleOptionsDialog(String selectedValues) {
        super();

        this.initialSelecteion = selectedValues;
        this._selectionsPane = new JPanel();
        this.setSelectedValues(selectedValues);
        this._selectionsPane.setBorder(new EmptyBorder(7, 0, 0, 0));
    }

    @Override
    public Component getComponent() {
        final JLabel line = new JLabel("Select the possible ontological natures of the type's instances.");
        final JButton cancelButton = new JButton("Cancel");
        final JButton applyButton = new JButton("Apply");
        
        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelledExit = false;
                _dialog.close();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelledExit = true;
                _dialog.close();
            }
        });
        
        // JPanel panel = new JPanel();
        JPanel panel = new JPanel();

        panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));

        panel.add(line);
        panel.add(this._selectionsPane);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());
        buttonsPanel.add(applyButton);
        buttonsPanel.add(cancelButton);
        panel.add(buttonsPanel);

        line.setAlignmentX(Component.LEFT_ALIGNMENT);
        this._selectionsPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // this._component = panel;
        
        return panel;
    }

    @Override
    public void prepare(IDialog dialog) {
        this._dialog = dialog;
        dialog.setTitle("Restrict allowed natures");
        dialog.pack();
    }

    @Override
    public boolean canClosed() {
        return true;
    }

    @Override
    public void shown() {}

    public String getSelectedValues() {
        if(this.cancelledExit) {
            return this.initialSelecteion;
        }

        final StringBuilder selectedValues = new StringBuilder("");
        final Component[] checkBoxes = this._selectionsPane.getComponents();

        for (int i = 0; checkBoxes != null && i < checkBoxes.length; i++) {
            if(checkBoxes[i] instanceof JCheckBox) {
                final JCheckBox checkBox = (JCheckBox) checkBoxes[i];
                if(checkBox.isSelected()) {
                    selectedValues.append(selectedValues.length() == 0 ? 
                        checkBox.getText() : " " + checkBox.getText());
                }
            }
        }

        return selectedValues.toString();
    }

    public void setSelectedValues(String selectedValuesString) {
        final List<String> selectedList = 
                Arrays.asList(selectedValuesString.split("\\s+"));
        final List<String> restrictedNatures = StereotypeUtils.getRestrictionsList();
        Collections.sort(restrictedNatures);
        final boolean isSmartModelingEnabled = Configurations.getInstance()
        		.getProjectConfigurations().isSmartModellingEnabled();

        this._selectionsPane.setLayout(new GridLayout(3,3));

        JCheckBox checkBox;

        // Line 1, Column 1
        checkBox = new JCheckBox(StereotypeUtils.RESTRICTED_TO_COLLECTIVE);
        this._selectionsPane.add(checkBox);
        checkBox.setSelected(selectedList.contains(StereotypeUtils.RESTRICTED_TO_COLLECTIVE));
        // Line 1, Column 2
        checkBox = new JCheckBox(StereotypeUtils.RESTRICTED_TO_MODE);
        this._selectionsPane.add(checkBox);
        checkBox.setSelected(selectedList.contains(StereotypeUtils.RESTRICTED_TO_MODE));
        // Line 1, Column 3
        checkBox = new JCheckBox(StereotypeUtils.RESTRICTED_TO_ABSTRACT);
        this._selectionsPane.add(checkBox);
        if(isSmartModelingEnabled) {
        	checkBox.setSelected(false);
        	checkBox.setEnabled(false);
        } else {
        	checkBox.setSelected(selectedList.contains(StereotypeUtils.RESTRICTED_TO_ABSTRACT));
        }
        
        // Line 2, Column 1
        checkBox = new JCheckBox(StereotypeUtils.RESTRICTED_TO_FUNCTIONAL_COMPLEX);
        this._selectionsPane.add(checkBox);
        checkBox.setSelected(selectedList.contains(StereotypeUtils.RESTRICTED_TO_FUNCTIONAL_COMPLEX));
        // Line 2, Column 2
        checkBox = new JCheckBox(StereotypeUtils.RESTRICTED_TO_QUALITY);
        this._selectionsPane.add(checkBox);
        checkBox.setSelected(selectedList.contains(StereotypeUtils.RESTRICTED_TO_QUALITY));
        // Line 2, Column 3
        checkBox = new JCheckBox(StereotypeUtils.RESTRICTED_TO_EVENT);
        this._selectionsPane.add(checkBox);
        if(isSmartModelingEnabled) {
        	checkBox.setSelected(false);
        	checkBox.setEnabled(false);
        } else {
        	checkBox.setSelected(selectedList.contains(StereotypeUtils.RESTRICTED_TO_EVENT));
        }
        
        // Line 3, Column 1
        checkBox = new JCheckBox(StereotypeUtils.RESTRICTED_TO_QUANTITY);
        this._selectionsPane.add(checkBox);
        checkBox.setSelected(selectedList.contains(StereotypeUtils.RESTRICTED_TO_QUANTITY));
        // Line 3, Column 2
        checkBox = new JCheckBox(StereotypeUtils.RESTRICTED_TO_RELATOR);
        this._selectionsPane.add(checkBox);
        checkBox.setSelected(selectedList.contains(StereotypeUtils.RESTRICTED_TO_RELATOR));
        // Line 3, Column 3
        checkBox = new JCheckBox(StereotypeUtils.RESTRICTED_TO_TYPE);
        this._selectionsPane.add(checkBox);
        if(isSmartModelingEnabled) {
        	checkBox.setSelected(false);
        	checkBox.setEnabled(false);
        } else {
        	checkBox.setSelected(selectedList.contains(StereotypeUtils.RESTRICTED_TO_TYPE));
        }
    }

}