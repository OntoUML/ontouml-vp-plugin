package it.unibz.inf.ontouml.vp.views;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import it.unibz.inf.ontouml.vp.ProjectConfigurations;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class ConfigurationsMenuView extends JPanel {
	
	private JCheckBox _chckbxEnableOntoumlFeatures;
	
	private JCheckBox _chckbxEnableCustomServer;
	private JTextField _txtServerAddress;
	
	private JCheckBox _chckbxEnableAutomaticExport;
	private JTextField _txtExportFolder;
	private JButton _btnSelectExportFolder;
	
	private JCheckBox _chckbxEnableAutoColoring;
	
	private JButton _btnApply;
	private JButton _btnResetDefaults;

	/**
	 * Create the panel.
	 */
	public ConfigurationsMenuView(ProjectConfigurations project) {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {670};
		gridBagLayout.rowHeights = new int[]{26, 97, 25, 0};
		gridBagLayout.columnWeights = new double[]{1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		_chckbxEnableOntoumlFeatures = new JCheckBox("Enable OntoUML features.");
		_chckbxEnableOntoumlFeatures.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(_chckbxEnableOntoumlFeatures.isSelected()) {
					_chckbxEnableCustomServer.setEnabled(true);
					_txtServerAddress.setEnabled(true);
					_chckbxEnableAutomaticExport.setEnabled(true);
					_txtExportFolder.setEnabled(true);
					_btnSelectExportFolder.setEnabled(true);
					_chckbxEnableAutoColoring.setEnabled(true);
				}
				else {
					_chckbxEnableCustomServer.setEnabled(false);
					_txtServerAddress.setEnabled(false);
					_chckbxEnableAutomaticExport.setEnabled(false);
					_txtExportFolder.setEnabled(false);
					_btnSelectExportFolder.setEnabled(false);
					_chckbxEnableAutoColoring.setEnabled(false);
				}
			}
		});
		GridBagConstraints gbc__chckbxEnableOntoumlFeatures = new GridBagConstraints();
		gbc__chckbxEnableOntoumlFeatures.fill = GridBagConstraints.BOTH;
		gbc__chckbxEnableOntoumlFeatures.gridx = 0;
		gbc__chckbxEnableOntoumlFeatures.gridy = 0;
		add(_chckbxEnableOntoumlFeatures, gbc__chckbxEnableOntoumlFeatures);
		
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
		gbl__ontoUMLServerPanel.columnWidths = new int[]{290, 370, 0};
		gbl__ontoUMLServerPanel.rowHeights = new int[]{26, 0};
		gbl__ontoUMLServerPanel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl__ontoUMLServerPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		_ontoUMLServerPanel.setLayout(gbl__ontoUMLServerPanel);
		
		_chckbxEnableCustomServer = new JCheckBox("Use custom OntoUML Server instance.");
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
		_txtServerAddress.setText("https://ontouml.herokuapp.com/v1/verification");
		_txtServerAddress.setColumns(10);
		
		JPanel _exportPanel = new JPanel();
		_optionsPanel.add(_exportPanel);
		GridBagLayout gbl__exportPanel = new GridBagLayout();
		gbl__exportPanel.columnWidths = new int[]{290, 247, 0, 0};
		gbl__exportPanel.rowHeights = new int[]{0, 0};
		gbl__exportPanel.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl__exportPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		_exportPanel.setLayout(gbl__exportPanel);
		
		_chckbxEnableAutomaticExport = new JCheckBox("Enable automatic model export.");
		GridBagConstraints gbc__chckbxEnableAutomaticExport = new GridBagConstraints();
		gbc__chckbxEnableAutomaticExport.anchor = GridBagConstraints.WEST;
		gbc__chckbxEnableAutomaticExport.insets = new Insets(0, 0, 0, 5);
		gbc__chckbxEnableAutomaticExport.gridx = 0;
		gbc__chckbxEnableAutomaticExport.gridy = 0;
		_exportPanel.add(_chckbxEnableAutomaticExport, gbc__chckbxEnableAutomaticExport);
		_chckbxEnableAutomaticExport.setToolTipText("Enables the selection of a folder to export XMI and OntoUML Schema version of the project, facilitating archive and collaboration through CVS tools.");
		
		_txtExportFolder = new JTextField();
		GridBagConstraints gbc__txtExportFolder = new GridBagConstraints();
		gbc__txtExportFolder.fill = GridBagConstraints.HORIZONTAL;
		gbc__txtExportFolder.insets = new Insets(0, 0, 0, 5);
		gbc__txtExportFolder.gridx = 1;
		gbc__txtExportFolder.gridy = 0;
		_exportPanel.add(_txtExportFolder, gbc__txtExportFolder);
		_txtExportFolder.setColumns(10);
		
		_btnSelectExportFolder = new JButton("Select folder");
		GridBagConstraints gbc__btnSelectExportFolder = new GridBagConstraints();
		gbc__btnSelectExportFolder.fill = GridBagConstraints.HORIZONTAL;
		gbc__btnSelectExportFolder.gridx = 2;
		gbc__btnSelectExportFolder.gridy = 0;
		_exportPanel.add(_btnSelectExportFolder, gbc__btnSelectExportFolder);
		
		_chckbxEnableAutoColoring = new JCheckBox("Enable automatic coloring");
		_optionsPanel.add(_chckbxEnableAutoColoring);
		
		JPanel _controlButtonsPanel = new JPanel();
		GridBagConstraints gbc__controlButtonsPanel = new GridBagConstraints();
		gbc__controlButtonsPanel.anchor = GridBagConstraints.SOUTHEAST;
		gbc__controlButtonsPanel.gridx = 0;
		gbc__controlButtonsPanel.gridy = 2;
		add(_controlButtonsPanel, gbc__controlButtonsPanel);
		GridBagLayout gbl__controlButtonsPanel = new GridBagLayout();
		gbl__controlButtonsPanel.columnWidths = new int[]{135, 135, 0};
		gbl__controlButtonsPanel.rowHeights = new int[]{0, 0};
		gbl__controlButtonsPanel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl__controlButtonsPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		_controlButtonsPanel.setLayout(gbl__controlButtonsPanel);
		
		_btnApply = new JButton("Apply");
		GridBagConstraints gbc__btnApply = new GridBagConstraints();
		gbc__btnApply.fill = GridBagConstraints.HORIZONTAL;
		gbc__btnApply.insets = new Insets(0, 0, 0, 5);
		gbc__btnApply.gridx = 0;
		gbc__btnApply.gridy = 0;
		_controlButtonsPanel.add(_btnApply, gbc__btnApply);
		
		_btnResetDefaults = new JButton("Reset Defaults");
		GridBagConstraints gbc__btnResetDefaults = new GridBagConstraints();
		gbc__btnResetDefaults.fill = GridBagConstraints.HORIZONTAL;
		gbc__btnResetDefaults.gridx = 1;
		gbc__btnResetDefaults.gridy = 0;
		_controlButtonsPanel.add(_btnResetDefaults, gbc__btnResetDefaults);

	}

}
