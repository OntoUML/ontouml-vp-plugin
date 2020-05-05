package it.unibz.inf.ontouml.vp.views;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;

import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;
import it.unibz.inf.ontouml.vp.utils.ViewUtils;

public class SetOrderDialog implements IDialogHandler {

    private IDialog _dialog;
    private Component _component;
    private boolean cancelledExit = true;
    private String currentString = "";
    private JTextField _inputField;

    public SetOrderDialog(String current) {
        super();
        this.currentString = current;
    }

    @Override
    public Component getComponent() {
        final JLabel line = new JLabel("Set the type order (\"*\" for orderless)");
        line.setBorder(new EmptyBorder(0, 7, 0, 0));

        _inputField = new JTextField(this.currentString);
        _inputField.setColumns(10);

        ImageIcon addIcon = new ImageIcon(ViewUtils.getFilePath(ViewUtils.ADD_LOGO));
        addIcon.setImage(addIcon.getImage().getScaledInstance(17, 17, Image.SCALE_SMOOTH));
        ImageIcon subtractIcon = new ImageIcon(ViewUtils.getFilePath(ViewUtils.SUBTRACT_LOGO));
        subtractIcon.setImage(subtractIcon.getImage().getScaledInstance(17, 17, Image.SCALE_SMOOTH));
        ImageIcon asteriskIcon = new ImageIcon(ViewUtils.getFilePath(ViewUtils.ASTERISK_LOGO));
        asteriskIcon.setImage(asteriskIcon.getImage().getScaledInstance(17, 17, Image.SCALE_SMOOTH));

        final JButton addButton = new JButton(addIcon);
        final JButton subtractButton = new JButton(subtractIcon);
        final JButton asteriskButton = new JButton(asteriskIcon);

        addButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int input = Integer.parseInt(_inputField.getText()) + 1;
                    _inputField.setText(input < 2 ? "2" : input+"");
                } catch (NumberFormatException e1) {
                    _inputField.setText("2");
                }
            }
        });
        subtractButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int input = Integer.parseInt(_inputField.getText()) - 1;
                    _inputField.setText(input < 2 ? "2" : input+"");
                } catch (NumberFormatException e2) {
                    _inputField.setText("2");
                }
            }
        });
        asteriskButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                _inputField.setText("*");
            }
        });

        addButton.setSize(5, 5);
        subtractButton.setSize(5, 5);
        asteriskButton.setSize(5, 5);

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
        
        JPanel panel = new JPanel();

        panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
        panel.add(line);

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new FlowLayout());
        middlePanel.setAlignmentX(FlowLayout.LEFT);
        middlePanel.add(_inputField);
        middlePanel.add(addButton);
        middlePanel.add(subtractButton);
        middlePanel.add(asteriskButton);
        panel.add(middlePanel);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());
        bottomPanel.setAlignmentX(FlowLayout.RIGHT);
        bottomPanel.add(applyButton);
        bottomPanel.add(cancelButton);
        panel.add(bottomPanel);

        line.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottomPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        this._component = panel;
        
        return panel;
    }

    @Override
    public void prepare(IDialog dialog) {
        this._dialog = dialog;
        dialog.setTitle("Select allowed natures");
        dialog.pack();
    }

    @Override
    public boolean canClosed() {
        return true;
    }

    @Override
    public void shown() {}

    public String getOrder() {
        return this.cancelledExit ? this.currentString : this._inputField.getText();
    }

}