package it.unibz.inf.ontouml.vp.views;

import it.unibz.inf.ontouml.vp.model.DbmsSupported;
import it.unibz.inf.ontouml.vp.model.MappingStrategy;
import it.unibz.inf.ontouml.vp.model.ProjectConfigurations;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

public class DbMappingView extends javax.swing.JPanel {

  private static final long serialVersionUID = 1L;

  private javax.swing.JComboBox<String> cbxDBMS;
  private javax.swing.JCheckBox cbxStandarizeNames;
  private javax.swing.JCheckBox cxbCreateIndex;
  private javax.swing.JCheckBox cxbEnumToLookupTable;
  private javax.swing.JCheckBox cxbGenerateConnection;
  private javax.swing.JCheckBox cxbGenerateObda;
  private javax.swing.JCheckBox cxbGenerateSchema;
  private javax.swing.ButtonGroup groupStrategy;
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
  private javax.swing.JPanel jPanel6;
  private javax.swing.JTabbedPane jTabbedPane1;
  private javax.swing.JButton jbtnCancel;
  private javax.swing.JButton jbtnOk;
  private javax.swing.JTextField jtfBaseIRI;
  private javax.swing.JTextField jtfDatabase;
  private javax.swing.JTextField jtfHost;
  private javax.swing.JTextField jtfPassword;
  private javax.swing.JTextField jtfUser;
  private javax.swing.JRadioButton rbtOneTablePerClass;
  private javax.swing.JRadioButton rbtOneTablePerConcreteClass;
  private javax.swing.JRadioButton rbtOneTablePerKind;

  public DbMappingView(ProjectConfigurations configurations) {
    initComponents();

    setComponentsValues(configurations);
  }

  private void initComponents() {

    // *************************************
    // Manually generated code.
    // *************************************
    setSize(new Dimension(500, 360));

    
    // *************************************
    // Automatically generated code.
    // *************************************

    groupStrategy = new javax.swing.ButtonGroup();
    jTabbedPane1 = new javax.swing.JTabbedPane();
    jPanel2 = new javax.swing.JPanel();
    cxbGenerateSchema = new javax.swing.JCheckBox();
    jPanel1 = new javax.swing.JPanel();
    rbtOneTablePerClass = new javax.swing.JRadioButton();
    rbtOneTablePerConcreteClass = new javax.swing.JRadioButton();
    rbtOneTablePerKind = new javax.swing.JRadioButton();
    jLabel3 = new javax.swing.JLabel();
    cbxDBMS = new javax.swing.JComboBox<>();
    cbxStandarizeNames = new javax.swing.JCheckBox();
    jLabel1 = new javax.swing.JLabel();
    cxbEnumToLookupTable = new javax.swing.JCheckBox();
    cxbCreateIndex = new javax.swing.JCheckBox();
    jPanel6 = new javax.swing.JPanel();
    jLabel2 = new javax.swing.JLabel();
    jtfBaseIRI = new javax.swing.JTextField();
    cxbGenerateObda = new javax.swing.JCheckBox();
    jPanel5 = new javax.swing.JPanel();
    jLabel4 = new javax.swing.JLabel();
    jLabel5 = new javax.swing.JLabel();
    jLabel6 = new javax.swing.JLabel();
    jLabel7 = new javax.swing.JLabel();
    jtfHost = new javax.swing.JTextField();
    jtfUser = new javax.swing.JTextField();
    jtfDatabase = new javax.swing.JTextField();
    jtfPassword = new javax.swing.JTextField();
    cxbGenerateConnection = new javax.swing.JCheckBox();
    jPanel4 = new javax.swing.JPanel();
    jbtnCancel = new javax.swing.JButton();
    jbtnOk = new javax.swing.JButton();

    cxbGenerateSchema.setSelected(true);
    cxbGenerateSchema.setText("Generate relational schema");
    cxbGenerateSchema.setActionCommand("Generate database script");

    jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Mapping Strategy: "));

    groupStrategy.add(rbtOneTablePerClass);
    rbtOneTablePerClass.setText("One Table per Class");

    groupStrategy.add(rbtOneTablePerConcreteClass);
    rbtOneTablePerConcreteClass.setText("One Table per Concrete Class");

    groupStrategy.add(rbtOneTablePerKind);
    rbtOneTablePerKind.setSelected(true);
    rbtOneTablePerKind.setText("One Table per Kind");
    rbtOneTablePerKind.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(java.awt.event.ItemEvent evt) {
            rbtOneTablePerKindItemStateChanged(evt);
        }
    });

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(rbtOneTablePerClass)
                .addComponent(rbtOneTablePerConcreteClass)
                .addComponent(rbtOneTablePerKind))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );
    jPanel1Layout.setVerticalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(rbtOneTablePerClass, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addComponent(rbtOneTablePerConcreteClass, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(18, 18, 18)
            .addComponent(rbtOneTablePerKind)
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    jLabel3.setText("DBMS");

    cbxDBMS.setModel(new DefaultComboBoxModel(DbmsSupported.values()));

    cbxStandarizeNames.setSelected(true);
    cbxStandarizeNames.setText("Standardize database names");

    jLabel1.setText("eg.: 'BirthDate' to 'birth_date'");

    cxbEnumToLookupTable.setSelected(true);
    cxbEnumToLookupTable.setText("Enumeration field to lookup table");

    cxbCreateIndex.setSelected(false);
    cxbCreateIndex.setText("Create indexes");

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(cxbGenerateSchema))
            .addGap(18, 18, 18)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addComponent(cbxDBMS, 0, 158, Short.MAX_VALUE)
                    .addGap(26, 26, 26))
                .addComponent(cxbEnumToLookupTable, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                .addComponent(cbxStandarizeNames, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cxbCreateIndex, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel3)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGap(21, 21, 21)
                            .addComponent(jLabel1)))
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGap(18, 18, 18))
    );
    jPanel2Layout.setVerticalGroup(
        jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel2Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(cxbGenerateSchema)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addComponent(jLabel3)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(cbxDBMS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(cxbEnumToLookupTable)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(cbxStandarizeNames)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel1)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 5, Short.MAX_VALUE)
                    .addComponent(cxbCreateIndex)
                    .addContainerGap())
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
    );

    jTabbedPane1.addTab("Relational Schema", jPanel2);

    jLabel2.setText("Base IRI:");

    jtfBaseIRI.setText("http://example.com");

    cxbGenerateObda.setSelected(true);
    cxbGenerateObda.setText("Generate OBDA file");

    javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
    jPanel6.setLayout(jPanel6Layout);
    jPanel6Layout.setHorizontalGroup(
        jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel6Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addComponent(cxbGenerateObda)
                    .addGap(0, 0, Short.MAX_VALUE))
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addComponent(jLabel2)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jtfBaseIRI, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)))
            .addContainerGap())
    );
    jPanel6Layout.setVerticalGroup(
        jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(cxbGenerateObda)
            .addGap(18, 18, 18)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel2)
                .addComponent(jtfBaseIRI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(118, Short.MAX_VALUE))
    );

    jTabbedPane1.addTab("OBDA", jPanel6);

    jLabel4.setText("Host :");

    jLabel5.setText("Database :");
    jLabel5.setToolTipText("");

    jLabel6.setText("User :");

    jLabel7.setText("Passoword :");

    cxbGenerateConnection.setSelected(true);
    cxbGenerateConnection.setText("Generate the connection");

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
                    .addComponent(cxbGenerateConnection)
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
            .addComponent(cxbGenerateConnection)
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

    jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    jbtnCancel.setText("Cancel");
    jbtnCancel.setMaximumSize(new java.awt.Dimension(135, 29));
    jbtnCancel.setMinimumSize(new java.awt.Dimension(135, 29));
    jbtnCancel.setPreferredSize(new java.awt.Dimension(135, 29));

    jbtnOk.setText("Do Mapping");
    jbtnOk.setMaximumSize(new java.awt.Dimension(135, 29));
    jbtnOk.setMinimumSize(new java.awt.Dimension(135, 29));
    jbtnOk.setPreferredSize(new java.awt.Dimension(135, 29));

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
            .addGap(36, 36, 36)
            .addComponent(jbtnOk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGap(61, 61, 61)
            .addComponent(jbtnCancel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGap(57, 57, 57))
    );
    jPanel4Layout.setVerticalGroup(
        jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel4Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jbtnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jbtnOk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jTabbedPane1)
                .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addContainerGap())
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jTabbedPane1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
    );
  }

  
  private void rbtOneTablePerKindItemStateChanged(java.awt.event.ItemEvent evt) {                                                    
      if(rbtOneTablePerKind.isSelected()) {
    	  cxbCreateIndex.setEnabled(true);
    	  cxbCreateIndex.setSelected(true);
      }else {
    	  cxbCreateIndex.setSelected(false);
    	  cxbCreateIndex.setEnabled(false);
      }
  }  
  
  /**
   * Updates components with project configurations' information.
   *
   * @param configurations
   */
  private void setComponentsValues(ProjectConfigurations configurations) {
	  cxbGenerateSchema.setSelected(configurations.isGenerateSchema());
	  cxbGenerateObda.setSelected(configurations.isGenerateObda());
	  cxbGenerateConnection.setSelected(configurations.isGenerateConnection());

	  if (configurations.getMappingStrategy() != null) {
	      if (configurations.getMappingStrategy() == MappingStrategy.ONE_TABLE_PER_CLASS)
	    	  rbtOneTablePerClass.setSelected(true);
	      else if (configurations.getMappingStrategy() == MappingStrategy.ONE_TABLE_PER_CONCRETE_CLASS)
	    	  rbtOneTablePerConcreteClass.setSelected(true);
	      else rbtOneTablePerKind.setSelected(true);
	  }

    cbxDBMS.setSelectedItem(configurations.getTargetDBMS());
    cbxStandarizeNames.setSelected(configurations.isStandardizeNames());
    jtfBaseIRI.setText(configurations.getExportGUFOIRI());
    cxbCreateIndex.setSelected(configurations.isGenerateIndex());
    jtfHost.setText(configurations.getHostNameConnection());
    jtfDatabase.setText(configurations.getDatabaseNameConnection());
    jtfUser.setText(configurations.getUserNameConnection());
    jtfPassword.setText(configurations.getPassword());
    cxbEnumToLookupTable.setSelected(configurations.isEnumFieldToLookupTable());
  }

  /** Updates project configurations with components' information. */
  
  public void updateConfigurationsValues(ProjectConfigurations configurations) {
    configurations.setGenerateSchema(cxbGenerateSchema.isSelected());
    configurations.setGenerateObda(cxbGenerateObda.isSelected());
    configurations.setGenerateConnection(cxbGenerateConnection.isSelected());
    
    if (rbtOneTablePerClass.isSelected())
      configurations.setMappingStrategy(MappingStrategy.ONE_TABLE_PER_CLASS);
    else if (rbtOneTablePerConcreteClass.isSelected())
      configurations.setMappingStrategy(MappingStrategy.ONE_TABLE_PER_CONCRETE_CLASS);
    else configurations.setMappingStrategy(MappingStrategy.ONE_TABLE_PER_KIND);

    configurations.setTargetDBMS((DbmsSupported) cbxDBMS.getSelectedItem());
    configurations.setStandardizeNames(cbxStandarizeNames.isSelected());
    configurations.setExportGUFOIRI(jtfBaseIRI.getText().trim());
    configurations.setGenerateIndexes(cxbCreateIndex.isSelected());
    configurations.setHostNameConnection(jtfHost.getText().trim());
    configurations.setDatabaseNameConnection(jtfDatabase.getText().trim());
    configurations.setUserNameConnection(jtfUser.getText().trim());
    configurations.setPassword(jtfPassword.getText().trim());
    configurations.setEnumFieldToLookupTable(cxbEnumToLookupTable.isSelected());
  }

  public void onExport(ActionListener onExportAction) {
    ActionListener[] currentListeners = jbtnOk.getActionListeners();

    for (int i = 0; currentListeners != null && i < currentListeners.length; i++) {
      jbtnOk.removeActionListener(currentListeners[i]);
    }

    jbtnOk.addActionListener(onExportAction);
  }

  public void onCancel(ActionListener onCancelAction) {
    ActionListener[] currentListeners = jbtnCancel.getActionListeners();

    for (int i = 0; currentListeners != null && i < currentListeners.length; i++) {
      jbtnCancel.removeActionListener(currentListeners[i]);
    }

    jbtnCancel.addActionListener(onCancelAction);
  }
  
  public boolean checkValidParamters() {
	  if( (DbmsSupported) cbxDBMS.getSelectedItem() == DbmsSupported.GENERIC_SCHEMA && !cxbEnumToLookupTable.isSelected()) {
		  JOptionPane.showMessageDialog(this, "You can not create an enumeration field to a generic database.", "Error", JOptionPane.ERROR_MESSAGE);
		  return false;
	  }
	  
	  if(!cxbGenerateSchema.isSelected() && !cxbGenerateObda.isSelected() && !cxbGenerateConnection.isSelected()) {
		  JOptionPane.showMessageDialog(this, "You must select at least one file to be generated (schema, OBDA or connection).", "Error", JOptionPane.ERROR_MESSAGE);
		  return false;
	  }
	  
	  return true;
  }
}
