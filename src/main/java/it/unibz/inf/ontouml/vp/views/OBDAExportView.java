package it.unibz.inf.ontouml.vp.views;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.view.IDialog;

import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.controllers.OBDAExportServerReques;
import it.unibz.inf.ontouml.vp.model.DBMSSuported;
import it.unibz.inf.ontouml.vp.model.MappingStrategy;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.ProjectConfigurations;

public class OBDAExportView extends javax.swing.JPanel {

	private static final long serialVersionUID = 1L;

	private ProjectConfigurations configurations;
	private JCheckBoxTree diagramTree;
	private IDialog parentContainer;
	private HashSet<String> elementsDiagramTree = new HashSet<>();

	private javax.swing.JComboBox<String> cbxDBMS;
    private javax.swing.JCheckBox jcbGenerateRelationalSchema;
    private javax.swing.JCheckBox jcbStandarizeNames;
    private javax.swing.JPanel diagramPanel;
    private javax.swing.ButtonGroup groupStrategy;
    private javax.swing.JCheckBox jcbGenerateConnection;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton jbtnCancel;
    private javax.swing.JButton jbtnGenerateFiles;
    private javax.swing.JTextField jtfBaseIRI;
    private javax.swing.JTextField jtfDatabase;
    private javax.swing.JTextField jtfHost;
    private javax.swing.JTextField jtfPassword;
    private javax.swing.JTextField jtfUser;
    private javax.swing.JPanel panSchemaParams;
    private javax.swing.JRadioButton rbtOneTablePerClass;
    private javax.swing.JRadioButton rbtOntTablePerKind;

	

	public OBDAExportView(ProjectConfigurations configurations) {
		initComponents();

		this.configurations = configurations;

		updateComponentsValues();
	}

	private void initComponents() {
		// *************************************
		// Manually generated code.
		// *************************************
		setSize(new Dimension(465, 500));

		diagramPanel = new javax.swing.JPanel();
		diagramTree = new JCheckBoxTree("diagram");

		JScrollPane scrollableTextAreaDiagram = new JScrollPane(diagramTree);
		scrollableTextAreaDiagram.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollableTextAreaDiagram.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		diagramPanel.add(scrollableTextAreaDiagram);

		diagramPanel.setLayout(new GridLayout(1, 1));
		diagramPanel.setPreferredSize(new Dimension(100, 100));

		// *************************************
		// Automatically generated code.
		// *************************************
		groupStrategy = new javax.swing.ButtonGroup();
        panSchemaParams = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        rbtOneTablePerClass = new javax.swing.JRadioButton();
        rbtOntTablePerKind = new javax.swing.JRadioButton();
        jcbStandarizeNames = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jcbGenerateRelationalSchema = new javax.swing.JCheckBox();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        cbxDBMS = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jtfHost = new javax.swing.JTextField();
        jtfUser = new javax.swing.JTextField();
        jtfDatabase = new javax.swing.JTextField();
        jtfPassword = new javax.swing.JTextField();
        jcbGenerateConnection = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jtfBaseIRI = new javax.swing.JTextField();
        jbtnGenerateFiles = new javax.swing.JButton();
        jbtnCancel = new javax.swing.JButton();

        panSchemaParams.setBorder(javax.swing.BorderFactory.createTitledBorder("Parameters for relational schema: "));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Mapping Strategy: "));

        groupStrategy.add(rbtOneTablePerClass);
        rbtOneTablePerClass.setText("One Table per Class");

        groupStrategy.add(rbtOntTablePerKind);
        rbtOntTablePerKind.setSelected(true);
        rbtOntTablePerKind.setText("One Table per Kind");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbtOneTablePerClass)
                    .addComponent(rbtOntTablePerKind))
                .addContainerGap(52, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbtOneTablePerClass)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rbtOntTablePerKind)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jcbStandarizeNames.setSelected(true);
        jcbStandarizeNames.setText("Standardize database names");

        jLabel1.setText("eg.: 'BirthDate' to 'birth_date'");

        jcbGenerateRelationalSchema.setSelected(true);
        jcbGenerateRelationalSchema.setText("Generate database script");

        cbxDBMS.setModel(new DefaultComboBoxModel(DBMSSuported.values()));

        jLabel3.setText("Name");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(cbxDBMS, 0, 145, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxDBMS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(90, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("DBMS", jPanel2);

        jLabel4.setText("Host :");

        jLabel5.setText("Database :");
        jLabel5.setToolTipText("");

        jLabel6.setText("User :");

        jLabel7.setText("Passoword :");

        jcbGenerateConnection.setSelected(false);
        jcbGenerateConnection.setText("Generate the connection");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtfUser)
                            .addComponent(jtfPassword)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(34, 34, 34)
                        .addComponent(jtfHost))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jcbGenerateConnection)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jtfDatabase)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jcbGenerateConnection)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jtfHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jtfDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jtfUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jtfPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Conection", jPanel5);

        javax.swing.GroupLayout panSchemaParamsLayout = new javax.swing.GroupLayout(panSchemaParams);
        panSchemaParams.setLayout(panSchemaParamsLayout);
        panSchemaParamsLayout.setHorizontalGroup(
            panSchemaParamsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panSchemaParamsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panSchemaParamsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panSchemaParamsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jcbStandarizeNames, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panSchemaParamsLayout.createSequentialGroup()
                            .addComponent(jcbGenerateRelationalSchema)
                            .addGap(16, 16, 16)))
                    .addGroup(panSchemaParamsLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        panSchemaParamsLayout.setVerticalGroup(
            panSchemaParamsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panSchemaParamsLayout.createSequentialGroup()
                .addGroup(panSchemaParamsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panSchemaParamsLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(jcbStandarizeNames)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jcbGenerateRelationalSchema))
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Paramters for OBDA file: "));

        jLabel2.setText("Base IRI:");

        jtfBaseIRI.setText("http://example.com");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(diagramPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtfBaseIRI)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jtfBaseIRI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(diagramPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                .addContainerGap())
        );

        jbtnGenerateFiles.setText("Generate Ontop Files");
        jbtnGenerateFiles.setMaximumSize(new java.awt.Dimension(135, 29));
        jbtnGenerateFiles.setMinimumSize(new java.awt.Dimension(135, 29));
        jbtnGenerateFiles.setPreferredSize(new java.awt.Dimension(135, 29));

        jbtnCancel.setText("Cancel");
        jbtnCancel.setMaximumSize(new java.awt.Dimension(135, 29));
        jbtnCancel.setMinimumSize(new java.awt.Dimension(135, 29));
        jbtnCancel.setPreferredSize(new java.awt.Dimension(135, 29));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jbtnGenerateFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panSchemaParams, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panSchemaParams, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbtnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnGenerateFiles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

		// *************************************
		// Manually generated code.
		// *************************************

		jbtnGenerateFiles.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jbtnGenerateFilesActionPerformed(evt);
			}
		});

		jbtnCancel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jbtnCancelActionPerformed(evt);
			}
		});

	}

	private void jbtnGenerateFilesActionPerformed(java.awt.event.ActionEvent evt) {
		
		if(jcbGenerateConnection.isSelected() &&
				(
				jtfHost.getText().trim().equals("") ||
				jtfDatabase.getText().trim().equals("") ||
				jtfUser.getText().trim().equals("") ||
				jtfPassword.getText().trim().equals("")
				)
			) {
			JOptionPane.showMessageDialog(null ,"Host, database, user and password must be filled in.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if( (DBMSSuported) cbxDBMS.getSelectedItem() == DBMSSuported.GENERIC_SCHEMA &&  jcbGenerateConnection.isSelected() ) {
			JOptionPane.showMessageDialog(null ,"It is not possible to generate the connection file for the generic database.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		saveSelectedElements();
		updateConfigurationsValues();
		Configurations.getInstance().save();
		parentContainer.close();
		OntoUMLPlugin.setOBDAExportWindowOpen(false);
		( new OBDAExportServerReques() ).start();
	}

	private void jbtnCancelActionPerformed(java.awt.event.ActionEvent evt) {
		parentContainer.close();
		OntoUMLPlugin.setOBDAExportWindowOpen(false);
	}

	/** Sets a direct reference to the container dialog after initialization. */
	public void setContainerDialog(IDialog dialog) {
		this.parentContainer = dialog;
	}

	/** Updates project configurations with components' information. */
	private void updateConfigurationsValues() {
		configurations.setExportGUFOElementsDiagramTree(elementsDiagramTree);
		
		if( rbtOntTablePerKind.isSelected() )
			configurations.setMappingStrategy(MappingStrategy.ONE_TABLE_PER_KIND);
		else configurations.setMappingStrategy(MappingStrategy.ONE_TABLE_PER_CLASS);
		
		configurations.setTargetDBMS( (DBMSSuported) cbxDBMS.getSelectedItem() );
		
		configurations.setExportGUFOIRI(jtfBaseIRI.getText().trim());
		
		configurations.setStandardizeNames(jcbStandarizeNames.isSelected());
		
		configurations.setGenerateSchema(jcbGenerateRelationalSchema.isSelected());
		
		configurations.setGenerateConnection(jcbGenerateConnection.isSelected());
		
		configurations.setHostNameConnection(jtfHost.getText().trim());
		
		configurations.setDatabaseNameConnection(jtfDatabase.getText().trim());
		
		configurations.setUserNameConnection(jtfUser.getText().trim());
		
		configurations.setPassword(jtfPassword.getText().trim());
	}

	/**
	 * Updates components with project configurations' information.
	 *
	 * @param configurations
	 */
	private void updateComponentsValues() {

		if(configurations.getMappingStrategy() == MappingStrategy.ONE_TABLE_PER_KIND)
			rbtOntTablePerKind.setSelected(true);
		else
			rbtOneTablePerClass.setSelected(true);

		if(configurations.getTargetDBMS() != null)
			cbxDBMS.setSelectedItem(configurations.getTargetDBMS());

		if(configurations.getExportGUFOIRI() != null && !configurations.getExportGUFOIRI().equals(""))
			jtfBaseIRI.setText(configurations.getExportGUFOIRI());

		if(configurations.getExportGUFOElementsDiagramTree() != null)
			diagramTree.setNodesCheck(configurations.getExportGUFOElementsDiagramTree());
		
		jcbStandarizeNames.setSelected(configurations.isStandardizeNames());
		
		jcbGenerateRelationalSchema.setSelected(configurations.isGenerateSchema());
		
		jcbGenerateConnection.setSelected(configurations.isGenerateConnection());
		
		jtfHost.setText(configurations.getHostNameConnection());
		
		jtfDatabase.setText(configurations.getDatabaseNameConnection());
		
		jtfUser.setText(configurations.getUserNameConnection());
		
		jtfPassword.setText(configurations.getPassword());
	}

	private void saveSelectedElements() {
		TreePath[] paths;

		paths = diagramTree.getCheckedPaths();

		for (TreePath path : paths) {
			Object[] object = path.getPath();
			for (Object o : object) {

				if (o instanceof DefaultMutableTreeNode) {

					DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;

					if (node.getUserObject() instanceof IModelElement)
						this.elementsDiagramTree.add(((IModelElement) node.getUserObject()).getId());

					if (node.getUserObject() instanceof IDiagramUIModel)
						this.elementsDiagramTree.add(((IDiagramUIModel) node.getUserObject()).getId());
				}
			}
		}

	}

}
