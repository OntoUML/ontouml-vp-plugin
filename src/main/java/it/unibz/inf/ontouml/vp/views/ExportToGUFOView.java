package it.unibz.inf.ontouml.vp.views;

import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.view.IDialog;

import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.utils.ProjectConfigurations;
import it.unibz.inf.ontouml.vp.utils.ServerRequest;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashSet;

public class ExportToGUFOView extends JPanel {
	private static final long serialVersionUID = 1L;

	private JTextField IRItxt;
	private JComboBox<String> formatBox;
	private JComboBox<String> uriFormatBox;

	private JTabbedPane tabbedPane;
	private JCheckBoxTree packageTree;
	private JCheckBoxTree diagramTree;

	JPanel treePanelPackage;
	JPanel treePanelDiagram;

	private JButton _btnApply;
	private JButton _btnCancel;

	private IDialog _dialog;

	private boolean isToExport;
	private boolean isOpen;

	private HashSet<String> elementsPackageTree = new HashSet<String>();
	private HashSet<String> elementsDiagramTree = new HashSet<String>();
	
	public ExportToGUFOView(ProjectConfigurations configurations, ServerRequest request) {
		setSize(new Dimension(600, 515));
		setLayout(new GridBagLayout());
		
		JPanel optionsPanel = new JPanel();
		
		JPanel treePanel = new JPanel();
		
		JPanel packagePanel = new JPanel();
		JPanel diagramPanel = new JPanel();
		
		tabbedPane = new JTabbedPane();
		
		packageTree = new JCheckBoxTree("package");		
		diagramTree = new JCheckBoxTree("diagram");
		
		JScrollPane scrollableTextAreaPackage = new JScrollPane(packageTree);
		JScrollPane scrollableTextAreaDiagram = new JScrollPane(diagramTree);
		
		IRItxt = new JTextField();
		
		String[] formatStrings = { "N-Triples", "N-Quads", "Turtle" };
		formatBox = new JComboBox<String>(formatStrings);
		((JLabel) formatBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
		
		String[] uriFormatBoxString = { "name", "id" };
		uriFormatBox = new JComboBox<String>(uriFormatBoxString);
		((JLabel) uriFormatBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
		
		scrollableTextAreaPackage.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollableTextAreaPackage.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		scrollableTextAreaDiagram.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollableTextAreaDiagram.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		packagePanel.setLayout(new GridLayout(1, 1));
		packagePanel.setPreferredSize(new Dimension(550, 300));
		
		diagramPanel.setLayout(new GridLayout(1, 1));
		diagramPanel.setPreferredSize(new Dimension(550, 300));
		
		packagePanel.add(scrollableTextAreaPackage);
		
		diagramPanel.add(scrollableTextAreaDiagram);
		
		tabbedPane.addTab("Package Explorer", packagePanel);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		
		tabbedPane.addTab("Diagram Explorer", diagramPanel);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		treePanel.add(tabbedPane);
		treePanel.setLayout(new GridLayout(1,1));
		
		GridLayout gl_optionsPanel = new GridLayout(3,2);
		gl_optionsPanel.setVgap(5);
		gl_optionsPanel.setHgap(5);
		optionsPanel.setLayout(gl_optionsPanel);
	
		optionsPanel.add(new JLabel("Base IRI:"));
		optionsPanel.add(IRItxt);
		
		optionsPanel.add(new JLabel("Format:"));
		optionsPanel.add(formatBox);
		optionsPanel.add(new JLabel("URI Format:"));
		optionsPanel.add(uriFormatBox);
		
		GridBagConstraints gbc_optionsPanel = new GridBagConstraints();
		gbc_optionsPanel.insets = new Insets(5, 5, 5, 5);
		gbc_optionsPanel.anchor = GridBagConstraints.WEST;
		gbc_optionsPanel.gridx = 0;
		gbc_optionsPanel.gridy = 0;
		gbc_optionsPanel.ipadx = 30;
		
		add(optionsPanel, gbc_optionsPanel);
		
		GridBagConstraints gbc_treePanel = new GridBagConstraints();
		gbc_treePanel.gridx = 0;
		gbc_treePanel.gridy = 1;
		add(treePanel, gbc_treePanel);
		
		JPanel controlButtonsPanel = new JPanel();
		
		GridLayout glcontrolButtonsPanel = new GridLayout(1,2);
		gl_optionsPanel.setVgap(5);
		gl_optionsPanel.setHgap(5);
		controlButtonsPanel.setLayout(glcontrolButtonsPanel);

		_btnApply = new JButton("Export");
		_btnApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (tabbedPane.getSelectedIndex() == 0)
					saveSelectedElements("Package Tree");
				else
					saveSelectedElements("Diagram Tree");

				updateConfigurationsValues(configurations);
				Configurations.getInstance().save();
				isToExport = true;
				isOpen = false;
				_dialog.close();
				Thread thread = new Thread(request);
				thread.start();
			}

		});
		
		

		_btnCancel = new JButton("Cancel");
		_btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isToExport = false;
				isOpen = false;
				request.doStop();
				_dialog.close();
				OntoUMLPlugin.setExportToGUFOWindowOpen(false);
			}

		});
		
		controlButtonsPanel.add(_btnApply);
		controlButtonsPanel.add(_btnCancel);
		
		GridBagConstraints gbc_controlButtonsPanel = new GridBagConstraints();
		gbc_controlButtonsPanel.anchor = GridBagConstraints.SOUTHEAST;
		gbc_controlButtonsPanel.insets = new Insets(5, 5, 5, 0);
		gbc_controlButtonsPanel.gridx = 0;
		gbc_controlButtonsPanel.gridy = 2;
		gbc_controlButtonsPanel.ipadx = 130;
		add(controlButtonsPanel, gbc_controlButtonsPanel);
		
		isToExport = false;
		isOpen = true;
		updateComponentsValues(configurations);
	}

	protected JComponent makeTextPanel(String text) {
		JPanel panel = new JPanel(false);
		JLabel filler = new JLabel(text);
		filler.setHorizontalAlignment(JLabel.CENTER);
		panel.setLayout(new GridLayout(1, 1));
		panel.add(filler);
		return panel;
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

		if (tabbedPane.getSelectedIndex() == 0)
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
			packageTree.setNodesCheck(configurations.getExportGUFOElementsPackageTree());

		if (configurations.getExportGUFOElementsDiagramTree() != null)
			diagramTree.setNodesCheck(configurations.getExportGUFOElementsDiagramTree());
	}

	private void saveSelectedElements(String tree) {

		TreePath[] paths;
		HashSet<String> aux;

		if (tree.equals("Package Tree")) {
			paths = packageTree.getCheckedPaths();
			aux = this.elementsPackageTree;
		} else {
			paths = diagramTree.getCheckedPaths();
			aux = this.elementsDiagramTree;
		}

		for (TreePath path : paths) {
			Object[] object = path.getPath();
			for (int j = 0; j < object.length; j++) {

				if (object[j] instanceof DefaultMutableTreeNode) {

					DefaultMutableTreeNode node = (DefaultMutableTreeNode) object[j];

					if (node.getUserObject() instanceof IModelElement)
						aux.add(((IModelElement) node.getUserObject()).getId());
					
					if (node.getUserObject() instanceof IDiagramUIModel)
						aux.add(((IDiagramUIModel) node.getUserObject()).getId());
				}
			}
			/*
			 * final IProject project =
			 * ApplicationManager.instance().getProjectManager().getProject(); final
			 * String[] datatypes = { IModelElementFactory.MODEL_TYPE_DATA_TYPE };
			 * 
			 * for (IModelElement datatype : project.toModelElementArray(datatypes))
			 * aux.add(datatype.getId());
			 */
		}

	}

	public HashSet<String> getSavedElements() {

		if (tabbedPane.getSelectedIndex() == 0)
			return elementsPackageTree;
		else
			return elementsDiagramTree;
	}
}