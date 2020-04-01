package it.unibz.inf.ontouml.vp.views;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.vp.plugin.view.IDialog;

public class ProgressPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JLabel label;
	private JProgressBar progressBar;
	private JButton btnCancel;
	private IDialog _dialog;

	public ProgressPanel() {
		setSize(new Dimension(200, 100));

		label = new JLabel();
		label.setText("Contacting Server...");

		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_dialog.close();
			}

		});
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;

		add(label, constraints);
		add(progressBar, constraints);
		add(btnCancel, constraints);
		
	}

	public void setContainerDialog(IDialog dialog) {
		this._dialog = dialog;
	}
}