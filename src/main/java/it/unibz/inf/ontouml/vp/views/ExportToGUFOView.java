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
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
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
	private JComboBox<String> inverseBox;
	private JComboBox<String> objectBox;
	private JComboBox<String> analysisBox;
	private JComboBox<String> packagesBox;

	private JTabbedPane tabbedPane;
	private JCheckBoxTree packageTree;
	private JCheckBoxTree diagramTree;

	JPanel treePanelPackage;
	JPanel treePanelDiagram;

	private JButton btnExport;
	private JButton btnCancel;

	private IDialog _dialog;

	private boolean isToExport;
	private boolean isOpen;

	private HashSet<String> elementsPackageTree = new HashSet<String>();
	private HashSet<String> elementsDiagramTree = new HashSet<String>();

	public ExportToGUFOView(ProjectConfigurations configurations, ServerRequest request) {
		setSize(new Dimension(610, 550));
		setLayout(new GridLayout(1,1));

		JTabbedPane mainTab = new JTabbedPane();
		JPanel mainPanel = new JPanel();
		JPanel optionsPanelLeft = new JPanel();
		JPanel optionsPanelRight = new JPanel();
		JPanel treePanel = new JPanel();
		JPanel buttonsPanel = new JPanel();
		
		
		
		GridBagLayout gbl_main = new GridBagLayout();
		gbl_main.rowWeights = new double[] { 0.3, 0.7, 0.1 };

		mainPanel.setLayout(gbl_main);
		optionsPanelLeft.setLayout(new GridBagLayout());
		optionsPanelRight.setLayout(new GridBagLayout());
		treePanel.setLayout(new GridBagLayout());
		buttonsPanel.setLayout(new GridBagLayout());

		mainPanel.setPreferredSize(new Dimension(600, 540));
	    optionsPanelLeft.setPreferredSize(new Dimension(280, 100));
	    optionsPanelRight.setPreferredSize(new Dimension(280,120));
	    treePanel.setPreferredSize(new Dimension(560,300));
	    buttonsPanel.setPreferredSize(new Dimension(560,40));
	  

		GridBagConstraints gbc_main = new GridBagConstraints();
		gbc_main.insets = new Insets(1, 1, 1, 1);
		gbc_main.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc_main.weightx = 0.5;
		gbc_main.weighty = 0.5;
		gbc_main.gridx = 0;
		gbc_main.gridy = 0;
		mainPanel.add(optionsPanelLeft, gbc_main);
		gbc_main.anchor = GridBagConstraints.FIRST_LINE_END;
		gbc_main.weightx = 0.5;
		gbc_main.weighty = 0.5;
		gbc_main.gridx = 1;
		gbc_main.gridy = 0;
		mainPanel.add(optionsPanelRight, gbc_main);
		gbc_main.anchor = GridBagConstraints.PAGE_START;
		gbc_main.weightx = 1.0;
		gbc_main.weighty = 0.8;
		gbc_main.gridwidth = 4;
		gbc_main.gridx = 0;
		gbc_main.gridy = 1;
		mainPanel.add(treePanel, gbc_main);
		gbc_main.anchor = GridBagConstraints.FIRST_LINE_END;
		gbc_main.weightx = 0.5;
		gbc_main.weighty = 0.5;
		gbc_main.gridx = 0;
		gbc_main.gridy = 2;
		mainPanel.add(buttonsPanel, gbc_main);

		JLabel iriLabel = new JLabel("Base IRI:");
		JLabel formatLabel = new JLabel("Format:");
		JLabel URIformatLabel = new JLabel("URI Format:");

		IRItxt = new JTextField();

		String[] formatStrings = { "Turtle", "N-Triples", "N-Quads" };
		formatBox = new JComboBox<String>(formatStrings);
		((JLabel) formatBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);

		String[] uriFormatBoxString = { "name", "id" };
		uriFormatBox = new JComboBox<String>(uriFormatBoxString);
		((JLabel) uriFormatBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);

		iriLabel.setPreferredSize(new Dimension(80, 20));
		formatLabel.setPreferredSize(new Dimension(80, 20));
		URIformatLabel.setPreferredSize(new Dimension(80, 20));
		IRItxt.setPreferredSize(new Dimension(120, 20));
		formatBox.setPreferredSize(new Dimension(80, 20));
		uriFormatBox.setPreferredSize(new Dimension(80, 20));

		GridBagConstraints gbc_insidePanelLeft = new GridBagConstraints();
		// gbc_insidePanelLeft.fill = GridBagConstraints.HORIZONTAL;
		gbc_insidePanelLeft.insets = new Insets(5, 5, 5, 5);
		gbc_insidePanelLeft.weightx = 0.5;
		gbc_insidePanelLeft.gridx = 0;
		gbc_insidePanelLeft.gridy = 0;
		gbc_insidePanelLeft.anchor = GridBagConstraints.WEST;
		optionsPanelLeft.add(iriLabel, gbc_insidePanelLeft);
		gbc_insidePanelLeft.weightx = 0.5;
		gbc_insidePanelLeft.gridx = 1;
		gbc_insidePanelLeft.gridy = 0;
		optionsPanelLeft.add(IRItxt, gbc_insidePanelLeft);
		gbc_insidePanelLeft.weightx = 0.5;
		gbc_insidePanelLeft.gridx = 0;
		gbc_insidePanelLeft.gridy = 1;
		optionsPanelLeft.add(formatLabel, gbc_insidePanelLeft);
		gbc_insidePanelLeft.weightx = 0.5;
		gbc_insidePanelLeft.gridx = 1;
		gbc_insidePanelLeft.gridy = 1;
		optionsPanelLeft.add(formatBox, gbc_insidePanelLeft);
		gbc_insidePanelLeft.weightx = 0.5;
		gbc_insidePanelLeft.gridx = 0;
		gbc_insidePanelLeft.gridy = 2;
		optionsPanelLeft.add(URIformatLabel, gbc_insidePanelLeft);
		gbc_insidePanelLeft.weightx = 0.5;
		gbc_insidePanelLeft.gridx = 1;
		gbc_insidePanelLeft.gridy = 2;
		optionsPanelLeft.add(uriFormatBox, gbc_insidePanelLeft);

		JLabel inverseLabel = new JLabel("Inverse:");
		JLabel objectLabel = new JLabel("Object Property:");
		JLabel analysisLabel = new JLabel("Pre Analysis:");
		JLabel prefixPackageLabel = new JLabel("Prefix Package:");

		String[] inverseBoxString = { "false", "true" };
		inverseBox = new JComboBox<String>(inverseBoxString);
		((JLabel) inverseBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);

		String[] objectBoxString = { "false", "true" };
		objectBox = new JComboBox<String>(objectBoxString);
		((JLabel) objectBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);

		String[] analysisBoxString = { "true", "false" };
		analysisBox = new JComboBox<String>(analysisBoxString);
		((JLabel) analysisBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);

		String[] packagesBoxString = { "false", "true" };
		packagesBox = new JComboBox<String>(packagesBoxString);
		((JLabel) packagesBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);

		inverseLabel.setPreferredSize(new Dimension(80, 20));
		objectLabel.setPreferredSize(new Dimension(80, 20));
		analysisLabel.setPreferredSize(new Dimension(80, 20));
		prefixPackageLabel.setPreferredSize(new Dimension(80, 20));
		inverseBox.setPreferredSize(new Dimension(80, 20));
		objectBox.setPreferredSize(new Dimension(80, 20));
		analysisBox.setPreferredSize(new Dimension(80, 20));
		packagesBox.setPreferredSize(new Dimension(80, 20));

		GridBagConstraints gbc_insidePanelRight = new GridBagConstraints();
		// gbc_insidePanelLeft.fill = GridBagConstraints.HORIZONTAL;
		gbc_insidePanelRight.insets = new Insets(5, 5, 5, 15);
		gbc_insidePanelRight.weightx = 0.5;
		gbc_insidePanelRight.gridx = 0;
		gbc_insidePanelRight.gridy = 0;
		gbc_insidePanelRight.anchor = GridBagConstraints.EAST;
		optionsPanelRight.add(inverseLabel, gbc_insidePanelRight);
		gbc_insidePanelRight.weightx = 0.5;
		gbc_insidePanelRight.gridx = 1;
		gbc_insidePanelRight.gridy = 0;
		optionsPanelRight.add(inverseBox, gbc_insidePanelRight);
		gbc_insidePanelRight.weightx = 0.5;
		gbc_insidePanelRight.gridx = 0;
		gbc_insidePanelRight.gridy = 1;
		optionsPanelRight.add(objectLabel, gbc_insidePanelRight);
		gbc_insidePanelRight.weightx = 0.5;
		gbc_insidePanelRight.gridx = 1;
		gbc_insidePanelRight.gridy = 1;
		optionsPanelRight.add(objectBox, gbc_insidePanelRight);
		gbc_insidePanelRight.weightx = 0.5;
		gbc_insidePanelRight.gridx = 0;
		gbc_insidePanelRight.gridy = 2;
		optionsPanelRight.add(analysisLabel, gbc_insidePanelRight);
		gbc_insidePanelRight.weightx = 0.5;
		gbc_insidePanelRight.gridx = 1;
		gbc_insidePanelRight.gridy = 2;
		optionsPanelRight.add(analysisBox, gbc_insidePanelRight);
		gbc_insidePanelRight.weightx = 0.5;
		gbc_insidePanelRight.gridx = 0;
		gbc_insidePanelRight.gridy = 3;
		optionsPanelRight.add(prefixPackageLabel, gbc_insidePanelRight);
		gbc_insidePanelRight.weightx = 0.5;
		gbc_insidePanelRight.gridx = 1;
		gbc_insidePanelRight.gridy = 3;
		optionsPanelRight.add(packagesBox, gbc_insidePanelRight);
		
		JPanel packagePanel = new JPanel();
		JPanel diagramPanel = new JPanel();
		
		tabbedPane = new JTabbedPane();
		packageTree = new JCheckBoxTree("package");		
		diagramTree = new JCheckBoxTree("diagram");
		
		JScrollPane scrollableTextAreaPackage = new JScrollPane(packageTree);
		JScrollPane scrollableTextAreaDiagram = new JScrollPane(diagramTree);
		
		scrollableTextAreaPackage.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollableTextAreaPackage.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		scrollableTextAreaDiagram.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollableTextAreaDiagram.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		packagePanel.setLayout(new GridLayout(1, 1));
		packagePanel.setPreferredSize(new Dimension(550, 270));
		
		diagramPanel.setLayout(new GridLayout(1, 1));
		diagramPanel.setPreferredSize(new Dimension(550, 270));
		
		packagePanel.add(scrollableTextAreaPackage);
		
		diagramPanel.add(scrollableTextAreaDiagram);
		
		tabbedPane.addTab("Package Explorer", packagePanel);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		
		tabbedPane.addTab("Diagram Explorer", diagramPanel);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		treePanel.add(tabbedPane);
		treePanel.setLayout(new GridLayout(1,1));
		
		
		btnExport = new JButton("Export");
		btnCancel = new JButton("Cancel");
		
		btnExport.setPreferredSize(new Dimension(80, 35));
		btnCancel.setPreferredSize(new Dimension(80, 35));
		
		btnExport.addActionListener(new ActionListener() {
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
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isToExport = false;
				isOpen = false;
				request.doStop();
				_dialog.close();
				OntoUMLPlugin.setExportToGUFOWindowOpen(false);
			}

		});
		
		GridBagConstraints gbc_buttonsPanel = new GridBagConstraints();
		gbc_buttonsPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_buttonsPanel.insets = new Insets(2, 2, 2, 15);
		gbc_buttonsPanel.weightx = 0.5;
		gbc_buttonsPanel.gridx = 0;
		gbc_buttonsPanel.gridy = 0;
		gbc_buttonsPanel.anchor = GridBagConstraints.PAGE_END;
		buttonsPanel.add(btnExport, gbc_buttonsPanel);
		gbc_buttonsPanel.weightx = 0.5;
		gbc_buttonsPanel.gridx = 1;
		gbc_buttonsPanel.gridy = 0;
		buttonsPanel.add(btnCancel, gbc_buttonsPanel);
		
		
	
		add(mainPanel);

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