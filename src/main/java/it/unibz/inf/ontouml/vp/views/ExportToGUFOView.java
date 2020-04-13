package it.unibz.inf.ontouml.vp.views;

import com.vp.plugin.view.IDialog;
import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.utils.ProjectConfigurations;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 * @author Vcitor Viola
 *
 */
public class ExportToGUFOView extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel IRILabel;
	private JTextField IRItxt;
	private JComboBox<String> formatBox;
	private JComboBox<String> uriFormatBox;
	private JComboBox<String> treeBox;
	private JCheckBoxTree packageCBT;
	private JCheckBoxTree diagramCBT;

	JPanel treePanelPackage;
	JPanel treePanelDiagram;

	private JButton _btnApply;
	private JButton _btnResetDefaults;

	private IDialog _dialog;

	public ExportToGUFOView(ProjectConfigurations configurations) {
		setSize(new Dimension(850, 600));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 670 };
		gridBagLayout.rowHeights = new int[] { 26, 82, 25, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JPanel _optionsPanel = new JPanel();
		GridBagConstraints gbc__optionsPanel = new GridBagConstraints();
		gbc__optionsPanel.fill = GridBagConstraints.VERTICAL;
		gbc__optionsPanel.gridx = 0;
		gbc__optionsPanel.gridy = 1;
		add(_optionsPanel, gbc__optionsPanel);
		_optionsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		JPanel mainPanel = new JPanel();
		_optionsPanel.add(mainPanel);
		GridBagLayout gbl_iriPanel = new GridBagLayout();
		gbl_iriPanel.columnWidths = new int[] { 30, 250, 0 };
		gbl_iriPanel.rowHeights = new int[] { 26, 0 };
		gbl_iriPanel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_iriPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		mainPanel.setLayout(gbl_iriPanel);

		IRILabel = new JLabel("Base IRI:");
		GridBagConstraints gbc_IRILabel = new GridBagConstraints();
		gbc_IRILabel.anchor = GridBagConstraints.WEST;
		gbc_IRILabel.insets = new Insets(0, 0, 0, 5);
		gbc_IRILabel.gridx = 0;
		gbc_IRILabel.gridy = 0;
		mainPanel.add(IRILabel, gbc_IRILabel);

		IRItxt = new JTextField();
		GridBagConstraints gbc_IRItxt = new GridBagConstraints();
		gbc_IRItxt.fill = GridBagConstraints.HORIZONTAL;
		gbc_IRItxt.anchor = GridBagConstraints.CENTER;
		gbc_IRItxt.gridx = 1;
		gbc_IRItxt.gridy = 0;
		mainPanel.add(IRItxt, gbc_IRItxt);
		IRItxt.setText("");
		IRItxt.setColumns(10);

		String[] formatStrings = { "N-Triples", "N-Quads", "Turtle" };

		formatBox = new JComboBox<String>(formatStrings);
		GridBagConstraints gbc_formatBox = new GridBagConstraints();
		gbc_formatBox.anchor = GridBagConstraints.WEST;
		gbc_formatBox.gridx = 0;
		gbc_formatBox.gridy = 1;
		mainPanel.add(formatBox, gbc_formatBox);

		String[] uriFormatBoxString = { "name", "id" };

		uriFormatBox = new JComboBox<String>(uriFormatBoxString);
		GridBagConstraints gbc_uriFormatBox = new GridBagConstraints();
		gbc_uriFormatBox.anchor = GridBagConstraints.WEST;
		gbc_uriFormatBox.gridx = 0;
		gbc_uriFormatBox.gridy = 2;
		mainPanel.add(uriFormatBox, gbc_uriFormatBox);

		String[] treeBoxString = { "Diagram Explorer" , "Package Explorer" };

		treeBox = new JComboBox<String>(treeBoxString);
		treeBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				JComboBox<?> cb = (JComboBox<?>) e.getSource();
				String option = (String) cb.getSelectedItem();
				
				if(option.equals("Package Explorer")) {
					treePanelPackage.setVisible(true);
					treePanelDiagram.setVisible(false);
				}else {
					treePanelPackage.setVisible(false);
					treePanelDiagram.setVisible(true);
				}
			}
		});

		GridBagConstraints gbc_treeBox = new GridBagConstraints();
		gbc_treeBox.anchor = GridBagConstraints.WEST;
		gbc_treeBox.gridx = 0;
		gbc_treeBox.gridy = 3;
		mainPanel.add(treeBox, gbc_treeBox);

		treePanelPackage = new JPanel();
		treePanelPackage.setVisible(false);

		GridBagConstraints gbc__treePanelPackage = new GridBagConstraints();
		gbc__treePanelPackage.fill = GridBagConstraints.WEST;
		gbc__treePanelPackage.gridx = 0;
		gbc__treePanelPackage.gridy = 4;
		mainPanel.add(treePanelPackage, gbc__treePanelPackage);

		packageCBT = new JCheckBoxTree("package");

		JScrollPane scrollableTextAreaPackage = new JScrollPane(packageCBT);
		scrollableTextAreaPackage.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollableTextAreaPackage.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		treePanelPackage.add(scrollableTextAreaPackage);

		treePanelDiagram = new JPanel();

		GridBagConstraints gbc__treePanelDiagram = new GridBagConstraints();
		gbc__treePanelDiagram.fill = GridBagConstraints.WEST;
		gbc__treePanelDiagram.gridx = 0;
		gbc__treePanelDiagram.gridy = 4;
		mainPanel.add(treePanelDiagram, gbc__treePanelDiagram);

		diagramCBT = new JCheckBoxTree("diagram");

		JScrollPane scrollableTextAreaDiagram = new JScrollPane(diagramCBT);
		scrollableTextAreaDiagram.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollableTextAreaDiagram.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		treePanelDiagram.add(scrollableTextAreaDiagram);

		JPanel _controlButtonsPanel = new JPanel();
		GridBagConstraints gbc__controlButtonsPanel = new GridBagConstraints();
		gbc__controlButtonsPanel.anchor = GridBagConstraints.SOUTHEAST;
		gbc__controlButtonsPanel.gridx = 0;
		gbc__controlButtonsPanel.gridy = 2;
		add(_controlButtonsPanel, gbc__controlButtonsPanel);
		GridBagLayout gbl__controlButtonsPanel = new GridBagLayout();
		gbl__controlButtonsPanel.columnWidths = new int[] { 135, 135, 0 };
		gbl__controlButtonsPanel.rowHeights = new int[] { 0, 0 };
		gbl__controlButtonsPanel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl__controlButtonsPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
		_controlButtonsPanel.setLayout(gbl__controlButtonsPanel);

		_btnApply = new JButton("Save");
		_btnApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateConfigurationsValues(configurations);
				Configurations.getInstance().save();
				_dialog.close();
			}

		});
		GridBagConstraints gbc__btnApply = new GridBagConstraints();
		gbc__btnApply.fill = GridBagConstraints.HORIZONTAL;
		gbc__btnApply.insets = new Insets(0, 0, 0, 5);
		gbc__btnApply.gridx = 0;
		gbc__btnApply.gridy = 0;
		_controlButtonsPanel.add(_btnApply, gbc__btnApply);

		_btnResetDefaults = new JButton("Reset Defaults");
		_btnResetDefaults.addActionListener(new ActionListener() {
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

		updateComponentsValues(configurations);
		updateComponentsStatus();
	}

	/**
	 * 
	 * Sets a direct reference to the container dialog after initialization.
	 * 
	 * @param dialog
	 * 
	 */
	public void setContainerDialog(IDialog dialog) {
		this._dialog = dialog;
	}

	/**
	 * 
	 * Updates project configurations with components' information.
	 * 
	 * @param configurations
	 * 
	 */
	private void updateConfigurationsValues(ProjectConfigurations configurations) {
	}

	/**
	 * 
	 * Updates components with project configurations' information.
	 * 
	 * @param configurations
	 * 
	 */
	private void updateComponentsValues(ProjectConfigurations configurations) {
	}

	/**
	 * 
	 * Updates components with default values.
	 * 
	 */
	private void resetComponentsValues() {
	}

	/**
	 * 
	 * Updates enable/editable status of components based on their information.
	 * 
	 */
	private void updateComponentsStatus() {
	}

}
