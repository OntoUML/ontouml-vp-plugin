package it.unibz.inf.ontouml.vp.controllers;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import it.unibz.inf.ontouml.vp.utils.ViewUtils;
import java.awt.Desktop;
import java.net.URI;

public class ReportErrorAction implements VPActionController {

  private static final String REPORT_URL = "https://forms.gle/btx7CDSy9kn5yb3WA";

  @Override
  public void performAction(VPAction arg0) {
    try {
      final Desktop desktop = Desktop.getDesktop();
      final URI uri = new URI(REPORT_URL);
      desktop.browse(uri);
    } catch (UnsupportedOperationException unsupportedException) {
      ViewUtils.reportBugErrorDialog(true);
      unsupportedException.printStackTrace();
    } catch (SecurityException securityException) {
      ViewUtils.reportBugErrorDialog(true);
      securityException.printStackTrace();
    } catch (Exception e) {
      ViewUtils.reportBugErrorDialog(false);
      e.printStackTrace();
    }
  }

  @Override
  public void update(VPAction arg0) {}
}
