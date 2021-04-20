package it.unibz.inf.ontouml.vp.views;

import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;
import it.unibz.inf.ontouml.vp.model.ServerRequest;
import java.awt.Component;

public class ProgressDialogStandard implements IDialogHandler {

  private IDialog mainDialog;
  private ProgressPanel progressPanel;
  private String title;
  private int width;
  private int height;

  public ProgressDialogStandard() {
    progressPanel = new ProgressPanel("Contacting Server...");
    title = "Working...";
    width = progressPanel.getWidth();
    height = progressPanel.getHeight();
  }

  public ProgressDialogStandard(ServerRequest serverRequest) {
    progressPanel = new ProgressPanel(serverRequest);
    title = "Working...";
    width = progressPanel.getWidth();
    height = progressPanel.getHeight();
  }

  public void setTitle(String txt) {
    title = txt;
  }

  public void setWidth(int w) {
    width = w;
  }

  public void setHeight(int h) {
    height = h;
  }

  @Override
  public boolean canClosed() {
    mainDialog.close();
    return true;
  }

  @Override
  public Component getComponent() {
    return progressPanel;
  }

  @Override
  public void prepare(IDialog dialog) {
    mainDialog = dialog;
    mainDialog.setModal(false);
    mainDialog.setResizable(false);
    mainDialog.setTitle(title);
    dialog.setSize(width, height);
    progressPanel.setContainerDialog(mainDialog);
  }

  @Override
  public void shown() {
    // TODO Auto-generated method stub

  }
}
