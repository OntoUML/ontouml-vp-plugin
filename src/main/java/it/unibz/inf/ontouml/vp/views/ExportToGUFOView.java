package it.unibz.inf.ontouml.vp.views;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;
import com.vp.plugin.view.IDialog;
import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.utils.ProjectConfigurations;
import it.unibz.inf.ontouml.vp.utils.ServerRequest;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

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

	private boolean isToExport;
	private boolean isOpen;

	private HashSet<String> elementsPackageTree = new HashSet<String>();
	private HashSet<String> elementsDiagramTree = new HashSet<String>();

	public ExportToGUFOView(ProjectConfigurations configurations, ServerRequest request) {
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

		String[] treeBoxString = { "Diagram Explorer", "Package Explorer" };

		treeBox = new JComboBox<String>(treeBoxString);
		treeBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				JComboBox<?> cb = (JComboBox<?>) e.getSource();
				String option = (String) cb.getSelectedItem();

				if (option.equals("Package Explorer")) {
					treePanelPackage.setVisible(true);
					treePanelDiagram.setVisible(false);
				} else {
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

		_btnApply = new JButton("Export");
		_btnApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (treeBox.getSelectedItem().equals("Package Explorer"))
					saveSelectedElements("Package Tree");
				else
					saveSelectedElements("Diagram Tree");

				updateConfigurationsValues(configurations);
				Configurations.getInstance().save();
				isToExport = true;
				isOpen = false;
				request.doStop();
				_dialog.close();
			}

		});
		GridBagConstraints gbc__btnApply = new GridBagConstraints();
		gbc__btnApply.fill = GridBagConstraints.HORIZONTAL;
		gbc__btnApply.insets = new Insets(0, 0, 0, 5);
		gbc__btnApply.gridx = 0;
		gbc__btnApply.gridy = 0;
		_controlButtonsPanel.add(_btnApply, gbc__btnApply);

		_btnResetDefaults = new JButton("Cancel");
		_btnResetDefaults.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isToExport = false;
				isOpen = false;
				request.doStop();
				_dialog.close();
			}

		});
		GridBagConstraints gbc__btnResetDefaults = new GridBagConstraints();
		gbc__btnResetDefaults.fill = GridBagConstraints.HORIZONTAL;
		gbc__btnResetDefaults.gridx = 1;
		gbc__btnResetDefaults.gridy = 0;
		_controlButtonsPanel.add(_btnResetDefaults, gbc__btnResetDefaults);

		isToExport = false;
		isOpen = true;
		updateComponentsValues(configurations);
	}

	public boolean getIsToExport() {
		return isToExport;
	}

	public boolean getIsOpen() {
		return isOpen;
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
		configurations.setExportGUFOIRI(IRItxt.getText());
		configurations.setExportGUFOFormat(formatBox.getSelectedItem().toString());
		configurations.setExportGUFOURIFormat(uriFormatBox.getSelectedItem().toString());

		if (treeBox.getSelectedItem().equals("Package Explorer"))
			configurations.setExportGUFOElementsPackageTree(elementsPackageTree);
		else
			configurations.setExportGUFOElementsDiagramTree(elementsDiagramTree);
	}

	/**
	 * 
	 * Updates components with project configurations' information.
	 * 
	 * @param configurations
	 * 
	 */
	private void updateComponentsValues(ProjectConfigurations configurations) {

		if (configurations.getExportGUFOIRI() != null && !configurations.getExportGUFOIRI().equals(""))
			IRItxt.setText(configurations.getExportGUFOIRI());

		if (configurations.getExportGUFOFormat() != null && !configurations.getExportGUFOFormat().equals(""))
			formatBox.setSelectedItem(configurations.getExportGUFOFormat());

		if (configurations.getExportGUFOURIFormat() != null && !configurations.getExportGUFOURIFormat().equals(""))
			uriFormatBox.setSelectedItem(configurations.getExportGUFOURIFormat());

		if (configurations.getExportGUFOElementsPackageTree() != null)
			packageCBT.setNodesCheck(configurations.getExportGUFOElementsPackageTree());

		if (configurations.getExportGUFOElementsDiagramTree() != null)
			diagramCBT.setNodesCheck(configurations.getExportGUFOElementsDiagramTree());
	}

	private void saveSelectedElements(String tree) {

		TreePath[] paths;
		HashSet<String> aux;

		if (tree.equals("Package Tree")) {
			paths = packageCBT.getCheckedPaths();
			aux = this.elementsPackageTree;
		} else {
			paths = diagramCBT.getCheckedPaths();
			aux = this.elementsDiagramTree;
		}

		for (TreePath path : paths) {
			Object[] object = path.getPath();
			for (int j = 0; j < object.length; j++) {

				if (object[j] instanceof DefaultMutableTreeNode) {

					DefaultMutableTreeNode node = (DefaultMutableTreeNode) object[j];

					if (node.getUserObject() instanceof IModelElement)
						aux.add(((IModelElement) node.getUserObject()).getId());
					//Diagrams are represented by its modelElement Parent
					if (node.getUserObject() instanceof IDiagramUIModel)
						aux.add(((IDiagramUIModel) node.getUserObject()).getParentModel().getId());
				}
			}

			final IProject project = ApplicationManager.instance().getProjectManager().getProject();
			final String[] datatypes = { IModelElementFactory.MODEL_TYPE_DATA_TYPE };

			for (IModelElement datatype : project.toModelElementArray(datatypes))
				aux.add(datatype.getId());
		}

	}

	public HashSet<String> getSavedElements() {

		if (treeBox.getSelectedItem().equals("Package Explorer"))
			return elementsPackageTree;
		else
			return elementsDiagramTree;
	}
}
