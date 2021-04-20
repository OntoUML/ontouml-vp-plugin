/**
 * Graphical interface designed to collect data for the transformation of OntoUml to the relational
 * schema.
 *
 * <p>Author: Gustavo Ludovico Guidoni
 */
package it.unibz.inf.ontouml.vp.views;

import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.view.IDialog;
import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.controllers.DBExportServerRequest;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.DBMSSuported;
import it.unibz.inf.ontouml.vp.model.MappingStrategy;
import it.unibz.inf.ontouml.vp.model.ProjectConfigurations;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashSet;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class DBExportView extends javax.swing.JPanel {
  private static final long serialVersionUID = 1L;

  private HashSet<String> elementsDiagramTree = new HashSet<>();

  private IDialog parentContainer;
  private ProjectConfigurations configurations;
  private JCheckBoxTree diagramTree;

  private javax.swing.ButtonGroup btgMappingStrategy;
  private javax.swing.JButton btnCancel;
  private javax.swing.JButton btnGenerateSchema;
  private javax.swing.JComboBox<String> cbxDBMS;
  private javax.swing.JCheckBox cbxStandarizeNames;
  private javax.swing.JPanel diagramPanel;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JPanel jpanDiagram;
  private javax.swing.JRadioButton rbtOneTablePerClass;
  private javax.swing.JRadioButton rbtOntTablePerKind;

  public DBExportView(ProjectConfigurations configurations) {
    initComponents();

    this.configurations = configurations;

    updateComponentsValues(configurations);
  }

  private void initComponents() {

    // *************************************
    // Manually generated code.
    // *************************************
    setSize(new Dimension(500, 360));

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

    btgMappingStrategy = new javax.swing.ButtonGroup();
    jPanel3 = new javax.swing.JPanel();
    btnGenerateSchema = new javax.swing.JButton();
    btnCancel = new javax.swing.JButton();
    jPanel4 = new javax.swing.JPanel();
    jPanel2 = new javax.swing.JPanel();
    cbxDBMS = new javax.swing.JComboBox<>();
    jPanel1 = new javax.swing.JPanel();
    rbtOneTablePerClass = new javax.swing.JRadioButton();
    rbtOntTablePerKind = new javax.swing.JRadioButton();
    cbxStandarizeNames = new javax.swing.JCheckBox();
    jLabel1 = new javax.swing.JLabel();
    jpanDiagram = new javax.swing.JPanel();

    jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    btnGenerateSchema.setText("Generate Schema");

    btnCancel.setText("Cancel");
    btnCancel.setMaximumSize(new java.awt.Dimension(117, 23));
    btnCancel.setMinimumSize(new java.awt.Dimension(117, 23));
    btnCancel.setPreferredSize(new java.awt.Dimension(117, 23));

    javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
    jPanel3.setLayout(jPanel3Layout);
    jPanel3Layout.setHorizontalGroup(
        jPanel3Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                jPanel3Layout
                    .createSequentialGroup()
                    .addGap(48, 48, 48)
                    .addComponent(
                        btnGenerateSchema,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        118,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(
                        javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)
                    .addComponent(
                        btnCancel,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        122,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(50, 50, 50)));
    jPanel3Layout.setVerticalGroup(
        jPanel3Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                jPanel3Layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        jPanel3Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(
                                btnCancel,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                29,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(
                                btnGenerateSchema,
                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                29,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

    jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

    jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("DBMS: "));

    cbxDBMS.setModel(new DefaultComboBoxModel(DBMSSuported.values()));

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
        jPanel2Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                jPanel2Layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addComponent(cbxDBMS, 0, 142, Short.MAX_VALUE)
                    .addContainerGap()));
    jPanel2Layout.setVerticalGroup(
        jPanel2Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                jPanel2Layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addComponent(
                        cbxDBMS,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

    jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Mapping Strategy: "));

    btgMappingStrategy.add(rbtOneTablePerClass);
    rbtOneTablePerClass.setText("One Table per Class");

    btgMappingStrategy.add(rbtOntTablePerKind);
    rbtOntTablePerKind.setSelected(true);
    rbtOntTablePerKind.setText("One Table per Kind");

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
        jPanel1Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                jPanel1Layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        jPanel1Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbtOneTablePerClass)
                            .addComponent(rbtOntTablePerKind))
                    .addContainerGap(35, Short.MAX_VALUE)));
    jPanel1Layout.setVerticalGroup(
        jPanel1Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                jPanel1Layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addComponent(rbtOneTablePerClass)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(rbtOntTablePerKind)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

    cbxStandarizeNames.setSelected(true);
    cbxStandarizeNames.setText("Standardize the names for the database");

    jLabel1.setText("eg.: 'PersonalCustomer' to 'personal_customer'.");

    jpanDiagram.setBorder(javax.swing.BorderFactory.createTitledBorder("Diagram:"));

    javax.swing.GroupLayout diagramPanelLayout = new javax.swing.GroupLayout(diagramPanel);
    // diagramPanel.setLayout(diagramPanelLayout);
    diagramPanelLayout.setHorizontalGroup(
        diagramPanelLayout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE));
    diagramPanelLayout.setVerticalGroup(
        diagramPanelLayout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE));

    javax.swing.GroupLayout jpanDiagramLayout = new javax.swing.GroupLayout(jpanDiagram);
    jpanDiagram.setLayout(jpanDiagramLayout);
    jpanDiagramLayout.setHorizontalGroup(
        jpanDiagramLayout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(
                diagramPanel,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                Short.MAX_VALUE));
    jpanDiagramLayout.setVerticalGroup(
        jpanDiagramLayout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(
                diagramPanel,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                javax.swing.GroupLayout.DEFAULT_SIZE,
                Short.MAX_VALUE));

    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
    jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(
        jPanel4Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                jPanel4Layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        jPanel4Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(
                                cbxStandarizeNames,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                466,
                                Short.MAX_VALUE)
                            .addGroup(
                                jPanel4Layout
                                    .createSequentialGroup()
                                    .addGap(21, 21, 21)
                                    .addComponent(
                                        jLabel1,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        445,
                                        Short.MAX_VALUE))
                            .addGroup(
                                jPanel4Layout
                                    .createSequentialGroup()
                                    .addGroup(
                                        jPanel4Layout
                                            .createParallelGroup(
                                                javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(
                                                jPanel1,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(
                                                jPanel2,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(18, 18, 18)
                                    .addComponent(
                                        jpanDiagram,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        Short.MAX_VALUE)
                                    .addContainerGap()))));
    jPanel4Layout.setVerticalGroup(
        jPanel4Layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                jPanel4Layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        jPanel4Layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(
                                jPanel4Layout
                                    .createSequentialGroup()
                                    .addComponent(
                                        jPanel1,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(
                                        javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(
                                        jPanel2,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(
                                jpanDiagram,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(cbxStandarizeNames)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(
                        jLabel1,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        18,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
        layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addGroup(
                        layout
                            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(
                                jPanel4,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                            .addComponent(
                                jPanel3,
                                javax.swing.GroupLayout.Alignment.TRAILING,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE))
                    .addContainerGap()));
    layout.setVerticalGroup(
        layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(
                layout
                    .createSequentialGroup()
                    .addContainerGap()
                    .addComponent(
                        jPanel4,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(
                        jPanel3,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()));

    // *************************************
    // Manually generated code.
    // *************************************

    btnCancel.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnCancelActionPerformed(evt);
          }
        });

    btnGenerateSchema.addActionListener(
        new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnGenerateSchemaActionPerformed(evt);
          }
        });
  }

  /** Sets a direct reference to the container dialog after initialization. */
  public void setContainerDialog(IDialog dialog) {
    parentContainer = dialog;
  }

  private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {
    parentContainer.close();
    OntoUMLPlugin.setDBExportWindowOpen(false);
  }

  private void btnGenerateSchemaActionPerformed(java.awt.event.ActionEvent evt) {
    saveSelectedElements();
    updateConfigurationsValues(configurations);
    Configurations.getInstance().save();
    parentContainer.close();
    OntoUMLPlugin.setDBExportWindowOpen(false);
    // Thread thread = new Thread(request);
    // thread.start();
    (new DBExportServerRequest()).start();
  }

  /** Updates project configurations with components' information. */
  private void updateConfigurationsValues(ProjectConfigurations configurations) {

    configurations.setExportGUFOElementsDiagramTree(elementsDiagramTree);

    if (rbtOneTablePerClass.isSelected())
      configurations.setMappingStrategy(MappingStrategy.ONE_TABLE_PER_CLASS);
    else configurations.setMappingStrategy(MappingStrategy.ONE_TABLE_PER_KIND);

    configurations.setTargetDBMS((DBMSSuported) cbxDBMS.getSelectedItem());

    configurations.setStandardizeNames(cbxStandarizeNames.isSelected());
  }

  /**
   * Updates components with project configurations' information.
   *
   * @param configurations
   */
  private void updateComponentsValues(ProjectConfigurations configurations) {

    if (configurations.getExportGUFOElementsDiagramTree() != null)
      diagramTree.setNodesCheck(configurations.getExportGUFOElementsDiagramTree());

    if (configurations.getMappingStrategy() != null
        && !configurations.getMappingStrategy().equals("")) {
      if (configurations.getMappingStrategy() == MappingStrategy.ONE_TABLE_PER_CLASS)
        rbtOneTablePerClass.setSelected(true);
      else rbtOntTablePerKind.setSelected(true);
    }

    if (configurations.getTargetDBMS() != null && !configurations.getTargetDBMS().equals(""))
      cbxDBMS.setSelectedItem(configurations.getTargetDBMS());

    cbxStandarizeNames.setSelected(configurations.isStandardizeNames());
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
