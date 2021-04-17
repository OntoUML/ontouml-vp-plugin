package it.unibz.inf.ontouml.vp.views;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;

public class ProgressDialogHandler implements IDialogHandler {

  private ProgressPanel _progressPanel;
  private IDialog _dialog;
  private boolean wasShown = false;
  private boolean wasClosed = false;

  public ProgressDialogHandler() {
    _progressPanel = new ProgressPanel();
    onCancel(e -> System.out.println("Cancelling dialog with default behaviour."));
  }

  @Override
  public Component getComponent() {
    return _progressPanel;
  }

  @Override
  public void prepare(IDialog dialog) {
    _dialog = dialog;

    _dialog.setSize(_progressPanel.getSize());
    _dialog.setModal(true);
    _dialog.setResizable(false);
    _dialog.pack();
  }

  @Override
  public void shown() {
    wasShown = true;
    _progressPanel.startUpdatingProgressBarString(() -> wasClosed);
  }

  @Override
  public boolean canClosed() {
    return false;
  }

  public void showDialog() {
    if (wasClosed)
      System.out.println("The progress dialog was already closed and cannot be shown.");
    else if (wasShown)
      System.out.println("The progress dialog is already visible and cannot be shown twice.");
    else {
      SwingUtilities.invokeLater(
          () -> ApplicationManager.instance().getViewManager().showDialog(this));
    }
  }

  public void closeDialog() {
    if (wasShown && !wasClosed) {
      _dialog.close();
    }
    wasClosed = true;
  }

  public void onCancel(Consumer<ActionEvent> cancelAction) {
    _progressPanel.setCancelAction(
        e -> {
          cancelAction.accept(e);
          closeDialog();
        });
  }
}
