package it.unibz.inf.ontouml.vp.views;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class JTableMap extends JPanel
{
	public JPanel mainFrame ;
	private JTable table;
	private JCheckBox col1;
	private JCheckBox col2;
	private JCheckBox col3;
	private TableColumnModel tableColumnModel;
	private JPanel panel;
	private String[] columnNamesArr;
	private ArrayList<String> columnNamesList;
	private JScrollPane scrollPane;
	private String[][] data;
	private DefaultTableModel defaultTableModel;
	private JButton addButton;
	private JButton deleteButton;
	private JPanel panelButton;
	
	public JTableMap()
	{
		mainFrame = new JPanel();

		col1 = new JCheckBox("col1");
		col2 = new JCheckBox("col2");
		col3 = new JCheckBox("col3");
		col1.setSelected(true);
		col2.setSelected(true);
		col3.setSelected(true);
		
		columnNamesList = new ArrayList<String>();
		columnNamesList.add("col0");
		columnNamesList.add("col1");
		columnNamesList.add("col2");
		columnNamesList.add("col3");
		
		
		data = new String[1][columnNamesList.size()];
		
		columnNamesArr = new String[columnNamesList.size()];
		for(int i=0;i<columnNamesList.size();i++)
		{
			columnNamesArr[i] = columnNamesList.get(i);
			data[0][i]="";
		}
		
		defaultTableModel = new DefaultTableModel(data, columnNamesArr);
	
		table = new JTable(defaultTableModel);
		tableColumnModel = table.getColumnModel();
		
		for(int i=0;i<columnNamesList.size();i++)
		{
			tableColumnModel.getColumn(i).setPreferredWidth(columnNamesList.get(i).length());
		}
		table.setPreferredScrollableViewportSize(table.getPreferredSize()); 
		scrollPane = new JScrollPane(table);
		
		col1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				if(!col1.isSelected())
				{
					TableColumn toRemove = table.getColumn("col1");
					table.removeColumn(toRemove); 
					table.validate();   
				}
				else
				{
					TableColumn toAdd = new TableColumn();
					toAdd.setHeaderValue("col1");
					toAdd.setIdentifier("col1");
					toAdd.setPreferredWidth("col1".length());
					table.addColumn(toAdd);
					table.validate();   
				}
			}
		});
		
		col2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				if(!col2.isSelected())
				{
					TableColumn toRemove = table.getColumn("col2");
					table.removeColumn(toRemove); 
					table.validate();   
				}
				else
				{
					TableColumn toAdd = new TableColumn();
					toAdd.setHeaderValue("col2");
					toAdd.setIdentifier("col2");
					toAdd.setPreferredWidth("col2".length());
					table.addColumn(toAdd);
					table.validate();   
				}
			}
		});
		
		col3.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				if(!col3.isSelected())
				{
					TableColumn toRemove = table.getColumn("col3");
					table.removeColumn(toRemove); 
					table.validate();   
				}
				else
				{
					TableColumn toAdd = new TableColumn();
					toAdd.setHeaderValue("col3");
					toAdd.setIdentifier("col3");
					toAdd.setPreferredWidth("col3".length());
					table.addColumn(toAdd);
					table.validate();   
				}
			}
		});
		
		addButton = new JButton("Add");
		deleteButton = new JButton("Delete");
		
		addButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				Vector rowData = null;
				defaultTableModel.addRow(rowData);
				table.validate();
			}
		});
		
		deleteButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				Vector rowData = null;
				int rowCount = defaultTableModel.getRowCount();
				if(rowCount>0)
				{
					defaultTableModel.removeRow(rowCount-1);
					table.validate();
				}
			}
		});
		
		
		panel = new JPanel();
		panel.add(col1);
		panel.add(col2);
		panel.add(col3);
		
		panelButton = new JPanel();
		panelButton.add(addButton);
		panelButton.add(deleteButton);
		
		//mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);   
		mainFrame.add(scrollPane, BorderLayout.CENTER);   
		mainFrame.add(panel, BorderLayout.NORTH); 
		mainFrame.add(panelButton,BorderLayout.SOUTH);
		//mainFrame.pack();   
		mainFrame.setLocation(150, 150);   
		mainFrame.setVisible(true);   
	}
	/*
	 public static void main(String[] args) {   
	        EventQueue.invokeLater(new Runnable() {   
	  
	            public void run() {   
	                JTableMap jts = new JTableMap();   
	            }   
	        });   
	    }  */
}