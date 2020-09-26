package it.unibz.inf.ontouml.vp.views;

import com.vp.plugin.view.IDialog;
import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.utils.ProjectConfigurations;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * View for the configurations menu to be embedded on an instance of <code>ConfigurationsDialog
 * </code>.
 *
 * @author Claudenir Fonseca
 */
public class ConfigurationsView extends JPanel {

  private static final long serialVersionUID = 1L;

  private JCheckBox _chckbxEnableCustomServer;
  private JTextField _txtServerAddress;

  private JCheckBox _chckbxEnableAutoColoring;
  private JCheckBox _chckbxEnableSmartModelling;

  private JButton _btnApply;
  private JButton _btnResetDefaults;

  private JLabel pluginVersion;

  private IDialog _dialog;

  /** ConfigurationsView constructor. */
  public ConfigurationsView(ProjectConfigurations configurations) {
    setSize(new Dimension(670, 180));
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] {670};
    gridBagLayout.rowHeights = new int[] {26, 82, 25, 0};
    gridBagLayout.columnWeights = new double[] {1.0};
    gridBagLayout.rowWeights = new double[] {0.0, 0.0, 1.0, Double.MIN_VALUE};
    setLayout(gridBagLayout);

    JPanel _optionsPanel = new JPanel();
    GridBagConstraints gbc__optionsPanel = new GridBagConstraints();
    gbc__optionsPanel.fill = GridBagConstraints.VERTICAL;
    gbc__optionsPanel.gridx = 0;
    gbc__optionsPanel.gridy = 1;
    add(_optionsPanel, gbc__optionsPanel);
    _optionsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

    JPanel _ontoUMLServerPanel = new JPanel();
    _optionsPanel.add(_ontoUMLServerPanel);
    GridBagLayout gbl__ontoUMLServerPanel = new GridBagLayout();
    gbl__ontoUMLServerPanel.columnWidths = new int[] {290, 370, 0};
    gbl__ontoUMLServerPanel.rowHeights = new int[] {26, 0};
    gbl__ontoUMLServerPanel.columnWeights = new double[] {0.0, 0.0, Double.MIN_VALUE};
    gbl__ontoUMLServerPanel.rowWeights = new double[] {0.0, Double.MIN_VALUE};
    _ontoUMLServerPanel.setLayout(gbl__ontoUMLServerPanel);

    _chckbxEnableCustomServer = new JCheckBox("Use custom OntoUML Server instance.");
    _chckbxEnableCustomServer.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            updateComponentsStatus();
          }
        });
    GridBagConstraints gbc__chckbxEnableCustomServer = new GridBagConstraints();
    gbc__chckbxEnableCustomServer.anchor = GridBagConstraints.WEST;
    gbc__chckbxEnableCustomServer.insets = new Insets(0, 0, 0, 5);
    gbc__chckbxEnableCustomServer.gridx = 0;
    gbc__chckbxEnableCustomServer.gridy = 0;
    _ontoUMLServerPanel.add(_chckbxEnableCustomServer, gbc__chckbxEnableCustomServer);

    _txtServerAddress = new JTextField();
    GridBagConstraints gbc__txtServerAddress = new GridBagConstraints();
    gbc__txtServerAddress.fill = GridBagConstraints.HORIZONTAL;
    gbc__txtServerAddress.anchor = GridBagConstraints.NORTH;
    gbc__txtServerAddress.gridx = 1;
    gbc__txtServerAddress.gridy = 0;
    _ontoUMLServerPanel.add(_txtServerAddress, gbc__txtServerAddress);
    _txtServerAddress.setText("");
    _txtServerAddress.setColumns(10);

    JPanel _exportPanel = new JPanel();
    _optionsPanel.add(_exportPanel);
    GridBagLayout gbl__exportPanel = new GridBagLayout();
    gbl__exportPanel.columnWidths = new int[] {290, 247, 0, 0};
    gbl__exportPanel.rowHeights = new int[] {0, 0};
    gbl__exportPanel.columnWeights = new double[] {0.0, 0.0, 0.0, Double.MIN_VALUE};
    gbl__exportPanel.rowWeights = new double[] {0.0, Double.MIN_VALUE};
    _exportPanel.setLayout(gbl__exportPanel);

    JPanel _smartPanel = new JPanel();
    _optionsPanel.add(_smartPanel);
    GridBagLayout gbl__smartPanel = new GridBagLayout();
    gbl__smartPanel.columnWidths = new int[] {290, 247, 0, 0};
    gbl__smartPanel.rowHeights = new int[] {0, 0};
    gbl__smartPanel.columnWeights = new double[] {0.0, 0.0, 0.0, Double.MIN_VALUE};
    gbl__smartPanel.rowWeights = new double[] {0.0, Double.MIN_VALUE};
    _smartPanel.setLayout(gbl__smartPanel);

    _chckbxEnableAutoColoring = new JCheckBox("Enable Smart Paint");
    GridBagConstraints gbc__chckbxEnableAutoColoring = new GridBagConstraints();
    gbc__chckbxEnableAutoColoring.anchor = GridBagConstraints.WEST;
    gbc__chckbxEnableAutoColoring.insets = new Insets(0, 0, 0, 5);
    gbc__chckbxEnableAutoColoring.gridx = 0;
    gbc__chckbxEnableAutoColoring.gridy = 0;
    _smartPanel.add(_chckbxEnableAutoColoring, gbc__chckbxEnableAutoColoring);

    _chckbxEnableSmartModelling = new JCheckBox("Enable Smart Modelling");
    GridBagConstraints gbc__chckbxEnableSmartModelling = new GridBagConstraints();
    gbc__chckbxEnableSmartModelling.anchor = GridBagConstraints.WEST;
    gbc__chckbxEnableSmartModelling.insets = new Insets(0, 0, 0, 5);
    gbc__chckbxEnableSmartModelling.gridx = 0;
    gbc__chckbxEnableSmartModelling.gridy = 1;
    _smartPanel.add(_chckbxEnableSmartModelling, gbc__chckbxEnableSmartModelling);

    JPanel _controlButtonsPanel = new JPanel();
    GridBagConstraints gbc__controlButtonsPanel = new GridBagConstraints();
    gbc__controlButtonsPanel.anchor = GridBagConstraints.SOUTHEAST;
    gbc__controlButtonsPanel.gridx = 0;
    gbc__controlButtonsPanel.gridy = 2;
    add(_controlButtonsPanel, gbc__controlButtonsPanel);
    GridBagLayout gbl__controlButtonsPanel = new GridBagLayout();
    gbl__controlButtonsPanel.columnWidths = new int[] {135, 135, 0};
    gbl__controlButtonsPanel.rowHeights = new int[] {0, 0};
    gbl__controlButtonsPanel.columnWeights = new double[] {0.0, 0.0, Double.MIN_VALUE};
    gbl__controlButtonsPanel.rowWeights = new double[] {0.0, Double.MIN_VALUE};
    _controlButtonsPanel.setLayout(gbl__controlButtonsPanel);

    _btnApply = new JButton("Save");
    _btnApply.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            updateConfigurationsValues(configurations);
            Configurations.getInstance().save();
            _dialog.close();
            OntoUMLPlugin.setConfigWindowOpen(false);
          }
        });
    GridBagConstraints gbc__btnApply = new GridBagConstraints();
    gbc__btnApply.fill = GridBagConstraints.HORIZONTAL;
    gbc__btnApply.insets = new Insets(0, 0, 0, 5);
    gbc__btnApply.gridx = 0;
    gbc__btnApply.gridy = 0;
    _controlButtonsPanel.add(_btnApply, gbc__btnApply);

    _btnResetDefaults = new JButton("Reset Defaults");
    _btnResetDefaults.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            resetComponentsValues();
            updateComponentsStatus();
          }
        });
    GridBagConstraints gbc__btnResetDefaults = new GridBagConstraints();
    gbc__btnResetDefaults.fill = GridBagConstraints.HORIZONTAL;
    gbc__btnResetDefaults.gridx = 1;
    gbc__btnResetDefaults.gridy = 0;
    _controlButtonsPanel.add(_btnResetDefaults, gbc__btnResetDefaults);

    pluginVersion = new JLabel("plugin version: " + OntoUMLPlugin.PLUGIN_VERSION_RELEASE);
    pluginVersion.setEnabled(false);

    GridBagConstraints gbc__versionLabel = new GridBagConstraints();
    gbc__versionLabel.anchor = GridBagConstraints.SOUTHWEST;
    gbc__versionLabel.gridx = 0;
    gbc__versionLabel.gridy = 2;

    add(pluginVersion, gbc__versionLabel);

    updateComponentsValues(configurations);
    updateComponentsStatus();
  }

  /**
   * Sets a direct reference to the container dialog after initialization.
   *
   * @param dialog
   */
  public void setContainerDialog(IDialog dialog) {
    this._dialog = dialog;
  }

  /**
   * Updates project configurations with components' information.
   *
   * @param configurations
   */
  private void updateConfigurationsValues(ProjectConfigurations configurations) {
    configurations.setCustomServerEnabled(_chckbxEnableCustomServer.isSelected());
    configurations.setServerURL(_txtServerAddress.getText());

    configurations.setAutomaticColoringEnabled(_chckbxEnableAutoColoring.isSelected());
    configurations.setSmartModellingEnabled(_chckbxEnableSmartModelling.isSelected());
  }

  /**
   * Updates components with project configurations' information.
   *
   * @param configurations
   */
  private void updateComponentsValues(ProjectConfigurations configurations) {
    _chckbxEnableCustomServer.setSelected(configurations.isCustomServerEnabled());
    _txtServerAddress.setText(configurations.getServerURL());

    _chckbxEnableAutoColoring.setSelected(configurations.isAutomaticColoringEnabled());
    _chckbxEnableSmartModelling.setSelected(configurations.isSmartModellingEnabled());
  }

  /** Updates components with default values. */
  private void resetComponentsValues() {
    _chckbxEnableCustomServer.setSelected(ProjectConfigurations.DEFAULT_IS_CUSTOM_SERVER_ENABLED);
    _txtServerAddress.setText(ProjectConfigurations.DEFAULT_SERVER_URL);

    _chckbxEnableAutoColoring.setSelected(
        ProjectConfigurations.DEFAULT_IS_AUTOMATIC_COLORING_ENABLED);
    _chckbxEnableSmartModelling.setSelected(
        ProjectConfigurations.DEFAULT_IS_AUTOMATIC_COLORING_ENABLED);
  }

  /** Updates enable/editable status of components based on their information. */
  private void updateComponentsStatus() {
    _chckbxEnableCustomServer.setEnabled(true);
    _txtServerAddress.setEnabled(_chckbxEnableCustomServer.isSelected());
    _chckbxEnableAutoColoring.setEnabled(true);
    _chckbxEnableSmartModelling.setEnabled(true);
  }
}
