package it.unibz.inf.ontouml.vp.views;

import com.vp.plugin.view.IDialog;
import it.unibz.inf.ontouml.vp.model.ServerRequest;
import it.unibz.inf.ontouml.vp.utils.ViewManagerUtils;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class ProgressPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  private final JLabel label;
  private final JProgressBar progressBar;
  private JButton btnCancel;
  private IDialog _dialog;

  private ActionListener btnCancelActionListener = null;

  public ProgressPanel() {
    super();
    setSize(200, 200);
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    label = new JLabel("Loading");
    label.setAlignmentX(Component.CENTER_ALIGNMENT);
    add(label);

    progressBar = new JProgressBar();
    //    progressBar.setIndeterminate(true);
    progressBar.setStringPainted(true);
    progressBar.setString("...");
    progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
    add(progressBar);

    btnCancel = new JButton("Cancel");
    btnCancel.setAlignmentX(Component.CENTER_ALIGNMENT);
    add(btnCancel);
  }

  public void startUpdatingProgressBarString(Supplier<Boolean> shouldStop) {
    new SwingWorker<List<String>, String>() {
      protected void process(List<String> chunks) {
        String latest = chunks.get(chunks.size() - 1);
        progressBar.setString(latest);
      }

      protected List<String> doInBackground() throws Exception {
        List<String> circles =
            new ArrayList<>(Arrays.asList("\u25F4", "\u25F7", "\u25F6", "\u25F5"));
        int index = 0;
        int times = 0;

        while (!shouldStop.get() && times < 300) {
          publish(circles.get(index));
          index = (index + 1) % circles.size();
          times++;
          Thread.sleep(100);
        }

        return circles;
      }
    }.execute();
  }

  public void setValue(int value) {
    progressBar.setValue(value);
    progressBar.setString("Completed " + value + "%");
  }

  public void setCancelAction(ActionListener cancelAction) {
    if (btnCancelActionListener != null) btnCancel.removeActionListener(btnCancelActionListener);

    btnCancel.addActionListener(cancelAction);
    btnCancelActionListener = cancelAction;
  }

  public ProgressPanel(String text) {
    setSize(new Dimension(200, 100));

    label = new JLabel();
    label.setText(text);

    progressBar = new JProgressBar();
    progressBar.setIndeterminate(true);

    btnCancel = new JButton("Cancel");

    GridBagConstraints constraints = new GridBagConstraints();
    constraints.anchor = GridBagConstraints.CENTER;

    add(label, constraints);
    add(progressBar, constraints);
    add(btnCancel);
  }

  public ProgressPanel(ServerRequest request) {
    this("Contacting Server...");

    btnCancel = new JButton("Cancel");
    btnCancel.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            request.doStop();
            _dialog.close();
            ViewManagerUtils.cleanAndShowMessage("Request cancelled by the user.");
          }
        });

    add(btnCancel);
  }

  public void setContainerDialog(IDialog dialog) {
    this._dialog = dialog;
  }
}
