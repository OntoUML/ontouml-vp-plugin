package it.unibz.inf.ontouml.vp.controllers;

import java.awt.Desktop;
import java.net.URI;

import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

import it.unibz.inf.ontouml.vp.utils.ViewManagerUtils;

public class ReportErrorController implements VPActionController {
	
	final private static String REPORT_URL = "https://forms.gle/btx7CDSy9kn5yb3WA";

	@Override
	public void performAction(VPAction arg0) {
		try {
			final Desktop desktop = Desktop.getDesktop();
			final URI uri = new URI(REPORT_URL);
			desktop.browse(uri);
		} catch (UnsupportedOperationException unsupportedException) {
			ViewManagerUtils.reportBugErrorDialog(true);
			unsupportedException.printStackTrace();
		} catch (SecurityException securityException) {
			ViewManagerUtils.reportBugErrorDialog(true);
			securityException.printStackTrace();
		} catch(Exception e) {
			ViewManagerUtils.reportBugErrorDialog(false);
			e.printStackTrace();
		}
	}

	@Override
	public void update(VPAction arg0) {}

}
