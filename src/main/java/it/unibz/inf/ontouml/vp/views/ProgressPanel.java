package it.unibz.inf.ontouml.vp.views;

import it.unibz.inf.ontouml.vp.model.ServerRequest;
import it.unibz.inf.ontouml.vp.utils.ViewUtils;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

	public ProgressPanel(String text) {
		setSize(new Dimension(200, 100));

		label = new JLabel();
		label.setText(text);

		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;

		add(label, constraints);
		add(progressBar, constraints);
	}
	
	public ProgressPanel(ServerRequest request) {
		this("Contacting Server...");

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				request.doStop();
				_dialog.close();
				ViewUtils.cleanAndShowMessage("Request cancelled by the user.");
			}
		});
		
		add(btnCancel);
	}

	public void setContainerDialog(IDialog dialog) {
		this._dialog = dialog;
	}
}