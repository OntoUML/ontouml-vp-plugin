package it.unibz.inf.ontouml.vp.utils;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Timestamp;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.vp.plugin.ApplicationManager;

import it.unibz.inf.ontouml.vp.OntoUMLPlugin;

/**
 * 
 * Class responsible for facilitating display of messages on Visual Paradigm's
 * log.
 * 
 * @author Claudenir Fonseca
 * @author Victor Viola
 *
 */
public class ViewUtils {

	public static final String SCOPE_PLUGIN = "OntoUML";
	// public static final String SCOPE_ALL_PLUGINS = "";
	public static final String SCOPE_VERIFICATION = "Verification Log";
	public static final String SCOPE_DEVELOPMENT_LOG = "DevLog";

	// images
	public static final String SIMPLE_LOGO = "simple_logo";
	public static final String SIMPLE_LOGO_FILENAME = "ontouml-simple-logo.png";

	public static void log(String message) {
		ApplicationManager.instance().getViewManager().showMessage(timestamp() + message);
	}

	public static void log(String message, String scope) {
		ApplicationManager.instance().getViewManager().showMessage(timestamp() + message, scope);
	}

	public static void simpleLog(String message) {
		ApplicationManager.instance().getViewManager().showMessage(message);
	}

	public static void simpleLog(String message, String scope) {
		ApplicationManager.instance().getViewManager().showMessage(message, scope);
	}

	public static void clearLog(String scope) {
		ApplicationManager.instance().getViewManager().clearMessages(scope);
	}

	private static String timestamp() {
		return "[" + (new Timestamp(System.currentTimeMillis())) + "] ";
	}

	private static String getFilePath(String imageName) {

		final File pluginDir = ApplicationManager.instance().getPluginInfo(OntoUMLPlugin.PLUGIN_ID).getPluginDir();

		switch (imageName) {
			case SIMPLE_LOGO:
				return Paths.get(pluginDir.getAbsolutePath(), "icons", "logo", SIMPLE_LOGO_FILENAME).toFile()
						.getAbsolutePath();
			default:
				return null;
		}

	}

	public static void logVerificationResponse(String responseMessage) {
		try {
			JsonArray response = (JsonArray) new JsonParser().parse(responseMessage).getAsJsonArray();

			if (response.size() == 0) {
				verificationConcludedDialog(0);
			} else {
				verificationConcludedDialog(response.size());

				ViewUtils.simpleLog("--------- Verification Service ---------", SCOPE_PLUGIN);
				for (JsonElement elem : response) {
					final JsonObject error = elem.getAsJsonObject();
					final String errorMessage = error.get("severity").getAsString() + ":" + " "
							+ error.get("title").getAsString() + " " + error.get("description").getAsString();

					ViewUtils.simpleLog(errorMessage, SCOPE_PLUGIN);
				}
				ViewUtils.simpleLog("-------------------------------------------", SCOPE_PLUGIN);
			}
		} catch (JsonSyntaxException e) {
			ViewUtils.log("The requested server might be down. See response below:", SCOPE_PLUGIN);
			ViewUtils.log(responseMessage, SCOPE_PLUGIN);
			ViewUtils.log(
					"Remote verification error. Please submit your Visual Paradigm's log and the time of the error our developers",
					SCOPE_PLUGIN);
			e.printStackTrace();
		}
	}

	public static void verificationConcludedDialog(int nIssues) {
		if (nIssues > 0) {
			ApplicationManager.instance().getViewManager().showConfirmDialog(null,
					"Verification found " + nIssues + " issue(s). \n"
							+ "Please check the log at the right bottom corner.",
					"Verification Service", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
					new ImageIcon(getFilePath(SIMPLE_LOGO)));
		} else {
			ApplicationManager.instance().getViewManager().showConfirmDialog(null,
					"The model was verified and no syntactical errors were found.", "Verification Service",
					JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
					new ImageIcon(getFilePath(SIMPLE_LOGO)));
		}
	}

	public static int smartPaintEnableDialog() {
		return ApplicationManager.instance().getViewManager().showConfirmDialog(null,
				"Smart Paint is disabled. Do you want to enable this feature?\n"
						+ "Warning: this feature will affect all diagrams within the project.",
				"Smart Paint", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
				new ImageIcon(getFilePath(SIMPLE_LOGO)));
	}

	public static int smartPaintConfirmationDialog() {
		return ApplicationManager.instance().getViewManager().showConfirmDialog(null,
				"Warning: this feature will affect all diagrams within the project.\n" + "Do you want to proceed?",
				"Smart Paint", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
				new ImageIcon(getFilePath(SIMPLE_LOGO)));
	}

}
