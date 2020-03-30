package it.unibz.inf.ontouml.vp.views;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
 
public class ProgressBar extends JPanel {
 
	private static final long serialVersionUID = 1L;
	private JProgressBar progressBar;
    private JLabel label;
    private JFrame mainFrame;
 
    public ProgressBar(String text) {
        super(new BorderLayout());
        
        if(text==null)
        	text="Task Progress";
 
        label = new JLabel();
        label.setText(text);
 
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
 
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(10, 10, 10, 10);
         
        // add components to the panel
        constraints.gridx = 0;
        constraints.gridy = 0;     
        panel.add(label, constraints);
       
        constraints.gridy = 1;  
        panel.add(progressBar, constraints);
       
        add(panel, BorderLayout.PAGE_START);
        
        mainFrame = new JFrame();
        mainFrame.setContentPane(panel);
        mainFrame.setUndecorated(true);
        //Display the window.
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        
    }

	public void setValueProgress(int value){
		this.progressBar.setValue(value);
	}
	
	public void openFrame(){
		this.mainFrame.setVisible(true);
	}
	
	public void closeFrame(){
		 this.mainFrame.setVisible(true);
		 this.mainFrame.dispose();
	}
	
	public void setLabel(String text){
		this.label.setText(text);
	}
}