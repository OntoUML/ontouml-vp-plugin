package it.unibz.inf.ontouml.vp.views;

import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;
import com.vp.plugin.view.IDialog;

import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.utils.Configurations;
import it.unibz.inf.ontouml.vp.utils.ProjectConfigurations;
import it.unibz.inf.ontouml.vp.utils.ServerRequest;

import java.awt.BorderLayout;
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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;

public class ExportToGUFOView extends JPanel {
	private static final long serialVersionUID = 1L;

	private JTextField IRItxt;
	private JComboBox<String> formatBox;
	private JComboBox<String> uriFormatBox;
	private JComboBox<String> inverseBox;
	private JComboBox<String> objectBox;
	private JComboBox<String> analysisBox;
	private JComboBox<String> packagesBox;

	public JComboBox<String> namesBox;
	public JComboBox<String> languagesBox;
	public JComboBox<String> packagesIdBox;
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
		setLayout(new GridLayout(1, 1));

		JTabbedPane mainTabbedPane = new JTabbedPane();

		JPanel mainPanel = new JPanel();
		JPanel elementMappingPanel = new JPanel();
		JPanel packageMappingPanel = new JPanel();

		mainTabbedPane.add(mainPanel, "Basic Settings");
		mainTabbedPane.add(elementMappingPanel, "Element Mapping");
		mainTabbedPane.add(packageMappingPanel, "Package Mapping");

		JPanel optionsPanelLeft = new JPanel();
		JPanel optionsPanelRight = new JPanel();
		JPanel treePanel = new JPanel();
		JPanel buttonsPanel = new JPanel();
		JPanel buttonsPanelTable1 = new JPanel();
		JPanel buttonsPanelTable2 = new JPanel();

		mainPanel.setLayout(new GridBagLayout());
		elementMappingPanel.setLayout(new GridBagLayout());
		packageMappingPanel.setLayout(new GridBagLayout());
		optionsPanelLeft.setLayout(new GridBagLayout());
		optionsPanelRight.setLayout(new GridBagLayout());
		treePanel.setLayout(new GridBagLayout());
		buttonsPanel.setLayout(new GridBagLayout());
		buttonsPanelTable1.setLayout(new GridBagLayout());
		buttonsPanelTable2.setLayout(new GridBagLayout());

		mainPanel.setPreferredSize(new Dimension(600, 540));
		elementMappingPanel.setPreferredSize(new Dimension(600, 480));
		packageMappingPanel.setPreferredSize(new Dimension(600, 540));
		optionsPanelLeft.setPreferredSize(new Dimension(280, 100));
		optionsPanelRight.setPreferredSize(new Dimension(280, 120));
		treePanel.setPreferredSize(new Dimension(560, 300));
		buttonsPanel.setPreferredSize(new Dimension(560, 40));
		buttonsPanelTable1.setPreferredSize(new Dimension(250, 40));

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
		JLabel formatLabel = new JLabel("Output format:");
		JLabel URIformatLabel = new JLabel("Create URIs using:");

		IRItxt = new JTextField();

		String[] formatStrings = { "Turtle", "N-Triples", "N-Quads" };
		formatBox = new JComboBox<String>(formatStrings);
		((JLabel) formatBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);

		String[] uriFormatBoxString = { "name", "id" };
		uriFormatBox = new JComboBox<String>(uriFormatBoxString);
		((JLabel) uriFormatBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);

		iriLabel.setPreferredSize(new Dimension(120, 20));
		formatLabel.setPreferredSize(new Dimension(120, 20));
		URIformatLabel.setPreferredSize(new Dimension(120, 20));
		IRItxt.setPreferredSize(new Dimension(120, 20));
		formatBox.setPreferredSize(new Dimension(120, 20));
		uriFormatBox.setPreferredSize(new Dimension(120, 20));

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
		gbc_insidePanelLeft.anchor = GridBagConstraints.EAST;
		optionsPanelLeft.add(IRItxt, gbc_insidePanelLeft);
		gbc_insidePanelLeft.weightx = 0.5;
		gbc_insidePanelLeft.gridx = 0;
		gbc_insidePanelLeft.gridy = 1;
		gbc_insidePanelLeft.anchor = GridBagConstraints.WEST;
		optionsPanelLeft.add(formatLabel, gbc_insidePanelLeft);
		gbc_insidePanelLeft.weightx = 0.5;
		gbc_insidePanelLeft.gridx = 1;
		gbc_insidePanelLeft.gridy = 1;
		gbc_insidePanelLeft.anchor = GridBagConstraints.EAST;
		optionsPanelLeft.add(formatBox, gbc_insidePanelLeft);
		gbc_insidePanelLeft.weightx = 0.5;
		gbc_insidePanelLeft.gridx = 0;
		gbc_insidePanelLeft.gridy = 2;
		gbc_insidePanelLeft.anchor = GridBagConstraints.WEST;
		optionsPanelLeft.add(URIformatLabel, gbc_insidePanelLeft);
		gbc_insidePanelLeft.weightx = 0.5;
		gbc_insidePanelLeft.gridx = 1;
		gbc_insidePanelLeft.gridy = 2;
		gbc_insidePanelLeft.anchor = GridBagConstraints.EAST;
		optionsPanelLeft.add(uriFormatBox, gbc_insidePanelLeft);

		JLabel inverseLabel = new JLabel("Create inverse object properties:");
		JLabel objectLabel = new JLabel("Minimize property creation:");
		JLabel analysisLabel = new JLabel("Run pre analysis:");
		JLabel prefixPackageLabel = new JLabel("Add prefix per package:");

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

		inverseLabel.setPreferredSize(new Dimension(160, 20));
		objectLabel.setPreferredSize(new Dimension(160, 20));
		analysisLabel.setPreferredSize(new Dimension(160, 20));
		prefixPackageLabel.setPreferredSize(new Dimension(160, 20));
		inverseBox.setPreferredSize(new Dimension(120, 20));
		objectBox.setPreferredSize(new Dimension(120, 20));
		analysisBox.setPreferredSize(new Dimension(120, 20));
		packagesBox.setPreferredSize(new Dimension(120, 20));

		GridBagConstraints gbc_insidePanelRight = new GridBagConstraints();
	    gbc_insidePanelLeft.fill = GridBagConstraints.HORIZONTAL;
		gbc_insidePanelRight.insets = new Insets(5, 5, 5, 5);
		gbc_insidePanelRight.weightx = 0.5;
		gbc_insidePanelRight.gridx = 0;
		gbc_insidePanelRight.gridy = 0;
		gbc_insidePanelRight.anchor = GridBagConstraints.WEST;
		optionsPanelRight.add(objectLabel, gbc_insidePanelRight);
		gbc_insidePanelRight.weightx = 0.5;
		gbc_insidePanelRight.gridx = 1;
		gbc_insidePanelRight.gridy = 0;
		gbc_insidePanelRight.anchor = GridBagConstraints.EAST;
		optionsPanelRight.add(objectBox, gbc_insidePanelRight);
		gbc_insidePanelRight.weightx = 0.5;
		gbc_insidePanelRight.gridx = 0;
		gbc_insidePanelRight.gridy = 1;
		gbc_insidePanelRight.anchor = GridBagConstraints.WEST;
		optionsPanelRight.add(inverseLabel, gbc_insidePanelRight);
		gbc_insidePanelRight.weightx = 0.5;
		gbc_insidePanelRight.gridx = 1;
		gbc_insidePanelRight.gridy = 1;
		gbc_insidePanelRight.anchor = GridBagConstraints.EAST;
		optionsPanelRight.add(inverseBox, gbc_insidePanelRight);
		gbc_insidePanelRight.weightx = 0.5;
		gbc_insidePanelRight.gridx = 0;
		gbc_insidePanelRight.gridy = 2;
		gbc_insidePanelRight.anchor = GridBagConstraints.WEST;
		optionsPanelRight.add(prefixPackageLabel, gbc_insidePanelRight);
		gbc_insidePanelRight.weightx = 0.5;
		gbc_insidePanelRight.gridx = 1;
		gbc_insidePanelRight.gridy = 2;
		gbc_insidePanelRight.anchor = GridBagConstraints.EAST;
		optionsPanelRight.add(packagesBox, gbc_insidePanelRight);
		gbc_insidePanelRight.weightx = 0.5;
		gbc_insidePanelRight.gridx = 0;
		gbc_insidePanelRight.gridy = 3;
		gbc_insidePanelRight.anchor = GridBagConstraints.WEST;
		optionsPanelRight.add(analysisLabel, gbc_insidePanelRight);
		gbc_insidePanelRight.weightx = 0.5;
		gbc_insidePanelRight.gridx = 1;
		gbc_insidePanelRight.gridy = 3;
		gbc_insidePanelRight.anchor = GridBagConstraints.EAST;
		optionsPanelRight.add(analysisBox, gbc_insidePanelRight);

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
		treePanel.setLayout(new GridLayout(1, 1));

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

		// TABLE 1

		final IProject project = ApplicationManager.instance().getProjectManager().getProject();

		final String[] anyLevelElements = { IModelElementFactory.MODEL_TYPE_CLASS,
				IModelElementFactory.MODEL_TYPE_ATTRIBUTE, IModelElementFactory.MODEL_TYPE_ASSOCIATION,
				IModelElementFactory.MODEL_TYPE_ASSOCIATION_END, };

		IModelElement[] elements = project.toAllLevelModelElementArray(anyLevelElements);
		String[] nameElements = new String[elements.length];

		for (int i = 0; i < elements.length; i++) {
			if (elements[i].getName() == null || elements[i].getName().equals(""))
				nameElements[i] = elements[i].getId();
			else
				nameElements[i] = elements[i].getName();
		}

		namesBox = new JComboBox<String>(nameElements);
		((JLabel) namesBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);

		languagesBox = new JComboBox<String>(getLanguagesCode());
		((JLabel) languagesBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);

		String[][] data;
		String[] columnNamesArr;
		ArrayList<String> columnNamesList;
		DefaultTableModel defaultTableModel;
		JTable table;
		TableColumnModel tableColumnModel;
		JScrollPane scrollPane;
		JButton addButton;
		JButton deleteButton;

		columnNamesList = new ArrayList<String>();
		columnNamesList.add("Element");
		columnNamesList.add("Language");
		columnNamesList.add("Text");

		data = new String[1][columnNamesList.size()];

		columnNamesArr = new String[columnNamesList.size()];
		for (int i = 0; i < columnNamesList.size(); i++) {
			columnNamesArr[i] = columnNamesList.get(i);
			data[0][i] = "";
		}

		defaultTableModel = new DefaultTableModel(data, columnNamesArr);

		table = new JTable(defaultTableModel);
		tableColumnModel = table.getColumnModel();

		for (int i = 0; i < columnNamesList.size(); i++) {
			tableColumnModel.getColumn(i).setPreferredWidth(columnNamesList.get(i).length());
		}
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		scrollPane = new JScrollPane(table);

		namesBox.setEditable(false);
		languagesBox.setEditable(false);
		table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(namesBox));
		table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(languagesBox));
		table.setRowHeight(20);

		addButton = new JButton("Add");
		deleteButton = new JButton("Delete");

		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Vector rowData = null;
				defaultTableModel.addRow(rowData);
				table.validate();
			}
		});

		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Vector rowData = null;
				int rowCount = defaultTableModel.getRowCount();
				if (rowCount > 0) {
					defaultTableModel.removeRow(rowCount - 1);
					table.validate();
				}
			}
		});

		GridBagConstraints gbc_buttonsPanelTable1 = new GridBagConstraints();
		gbc_buttonsPanelTable1.fill = GridBagConstraints.BOTH;
		gbc_buttonsPanelTable1.insets = new Insets(1, 1, 1, 1);
		gbc_buttonsPanelTable1.weightx = 0.5;
		gbc_buttonsPanelTable1.gridx = 0;
		gbc_buttonsPanelTable1.gridy = 0;
		gbc_buttonsPanelTable1.anchor = GridBagConstraints.PAGE_END;
		buttonsPanelTable1.add(addButton, gbc_buttonsPanelTable1);
		gbc_buttonsPanelTable1.weightx = 0.5;
		gbc_buttonsPanelTable1.gridx = 1;
		gbc_buttonsPanelTable1.gridy = 0;
		buttonsPanelTable1.add(deleteButton, gbc_buttonsPanelTable1);

		scrollPane.setPreferredSize(new Dimension(550, 400));
		buttonsPanelTable1.setPreferredSize(new Dimension(400, 30));

		GridBagConstraints gbc_mappingTable1 = new GridBagConstraints();
		// gbc_insidePanelLeft.fill = GridBagConstraints.HORIZONTAL;
		gbc_mappingTable1.insets = new Insets(1, 1, 1, 1);
		gbc_mappingTable1.gridx = 0;
		gbc_mappingTable1.gridy = 0;
		elementMappingPanel.add(scrollPane, gbc_mappingTable1);
		gbc_mappingTable1.gridx = 0;
		gbc_mappingTable1.gridy = 1;
		gbc_mappingTable1.anchor = GridBagConstraints.EAST;
		elementMappingPanel.add(buttonsPanelTable1, gbc_mappingTable1);

		// TABLE 2

		String[] packages = { IModelElementFactory.MODEL_TYPE_PACKAGE };

		IModelElement[] elements_package = project.toAllLevelModelElementArray(anyLevelElements);
		String[] idElements = new String[elements_package.length + 1];
		idElements[0] = project.getId();
		for (int i = 1; i < elements.length; i++) {
			if (elements[i].getId() == null)
				idElements[i] = elements[i].getId();
		}

		packagesIdBox = new JComboBox<String>(idElements);
		((JLabel) packagesIdBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);

		String[][] data_table2;
		String[] columnNamesArr_table2;
		ArrayList<String> columnNamesList_table2;
		DefaultTableModel defaultTableModel_table2;
		JTable table2;
		TableColumnModel tableColumnModel_table2;
		JScrollPane scrollPane_table2;
		JButton addButton_table2;
		JButton deleteButton_table2;

		columnNamesList_table2 = new ArrayList<String>();
		columnNamesList_table2.add("Packages");
		columnNamesList_table2.add("Prefix");
		columnNamesList_table2.add("URI");

		data_table2 = new String[1][columnNamesList_table2.size()];

		columnNamesArr_table2 = new String[columnNamesList_table2.size()];
		for (int i = 0; i < columnNamesList_table2.size(); i++) {
			columnNamesArr_table2[i] = columnNamesList_table2.get(i);
			data_table2[0][i] = "";
		}

		defaultTableModel_table2 = new DefaultTableModel(data_table2, columnNamesArr_table2);

		table2 = new JTable(defaultTableModel_table2);
		tableColumnModel_table2 = table2.getColumnModel();

		for (int i = 0; i < columnNamesList_table2.size(); i++) {
			tableColumnModel_table2.getColumn(i).setPreferredWidth(columnNamesList_table2.get(i).length());
		}
		table2.setPreferredScrollableViewportSize(table2.getPreferredSize());	
		scrollPane_table2 = new JScrollPane(table2);

		packagesIdBox.setEditable(false);
		table2.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(packagesIdBox));
		table2.setRowHeight(20);

		addButton_table2 = new JButton("Add");
		deleteButton_table2 = new JButton("Delete");

		addButton_table2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Vector rowData = null;
				defaultTableModel_table2.addRow(rowData);
				table2.validate();
			}
		});

		deleteButton_table2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Vector rowData = null;
				int rowCount = defaultTableModel_table2.getRowCount();
				if (rowCount > 0) {
					defaultTableModel_table2.removeRow(rowCount - 1);
					table2.validate();
				}
			}
		});

		GridBagConstraints gbc_buttonsPanelTable2 = new GridBagConstraints();
		gbc_buttonsPanelTable2.fill = GridBagConstraints.BOTH;
		gbc_buttonsPanelTable2.insets = new Insets(1, 1, 1, 1);
		gbc_buttonsPanelTable2.weightx = 0.5;
		gbc_buttonsPanelTable2.gridx = 0;
		gbc_buttonsPanelTable2.gridy = 0;
		gbc_buttonsPanelTable2.anchor = GridBagConstraints.PAGE_END;
		buttonsPanelTable2.add(addButton_table2, gbc_buttonsPanelTable2);
		gbc_buttonsPanelTable2.weightx = 0.5;
		gbc_buttonsPanelTable2.gridx = 1;
		gbc_buttonsPanelTable2.gridy = 0;
		buttonsPanelTable2.add(deleteButton_table2, gbc_buttonsPanelTable2);

		scrollPane_table2.setPreferredSize(new Dimension(550, 400));
		buttonsPanelTable2.setPreferredSize(new Dimension(400, 30));

		GridBagConstraints gbc_mappingTable2 = new GridBagConstraints();
		// gbc_insidePanelLeft.fill = GridBagConstraints.HORIZONTAL;
		gbc_mappingTable2.insets = new Insets(1, 1, 1, 1);
		gbc_mappingTable2.gridx = 0;
		gbc_mappingTable2.gridy = 0;
		packageMappingPanel.add(scrollPane_table2, gbc_mappingTable2);
		gbc_mappingTable2.gridx = 0;
		gbc_mappingTable2.gridy = 1;
		gbc_mappingTable2.anchor = GridBagConstraints.EAST;
		packageMappingPanel.add(buttonsPanelTable2, gbc_mappingTable2);

		add(mainTabbedPane);

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

	private static String[] getLanguagesCode() {
		String[] languages = { "default", "aa", "ab", "ae", "af", "ak", "am", "an", "ar", "as", "av", "ay", "az", "ba", "be", "bg",
				"bh", "bm", "bi", "bn", "bo", "br", "bs", "ca", "ce", "ch", "co", "cr", "cs", "cu", "cv", "cy", "da",
				"de", "dv", "dz", "ee", "el", "en", "eo", "es", "et", "eu", "fa", "ff", "fi", "fj", "fo", "fr", "fy",
				"ga", "gd", "gl", "gn", "gu", "gv", "ha", "he", "hi", "ho", "hr", "ht", "hu", "hy", "hz", "ia", "id",
				"ie", "ig", "ii", "ik", "io", "is", "it", "iu", "ja", "jv", "ka", "kg", "ki", "kj", "kk", "kl", "km",
				"kn", "ko", "kr", "ks", "ku", "kv", "kw", "ky", "la", "lb", "lg", "li", "ln", "lo", "lt", "lu", "lv",
				"mg", "mh", "mi", "mk", "ml", "mn", "mr", "ms", "mt", "my", "na", "nb", "nd", "ne", "ng", "nl", "nn",
				"no", "nr", "nv", "ny", "oc", "oj", "om", "or", "os", "pa", "pi", "pl", "ps", "pt", "qu", "rm", "rn",
				"ro", "ru", "rw", "sa", "sc", "sd", "se", "sg", "si", "sk", "sl", "sm", "sn", "so", "sq", "sr", "ss",
				"st", "su", "sv", "sw", "ta", "te", "tg", "th", "ti", "tk", "tl", "tn", "to", "tr", "ts", "tt", "tw",
				"ty", "ug", "uk", "ur", "uz", "ve", "vi", "vo", "wa", "wo", "xh", "yi", "yo", "za", "zh", "zu" };

		return languages;
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
