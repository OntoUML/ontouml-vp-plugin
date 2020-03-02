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
//	public static final String SCOPE_ALL_PLUGINS = "";
	public static final String SCOPE_VERIFICATION = "Verification Log";
	public static final String SCOPE_DEVELOPMENT_LOG = "DevLog";
	public static final String ONTOUML_SIMPLE_LOGO_PATH = "plugins\\ontouml-vp-plugin\\icons\\logo\\ontuml-simple-logo.png";

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

	public static void logVerificationResponse(String responseMessage) {
		try {
			JsonObject response = (JsonObject) new JsonParser().parse(responseMessage).getAsJsonObject();

			if (response.has("valid") && response.get("valid").getAsBoolean()) {
				ViewUtils.simpleLog("The model was verified and no syntactical errors were found.\n", SCOPE_PLUGIN);
			} else {
				final JsonArray errors = response.get("meta").getAsJsonArray();

				for (JsonElement elem : errors) {
					final JsonObject error = elem.getAsJsonObject();
					final String line = '[' + error.get("title").getAsString() + "]\t " + error.get("detail")
							.getAsString().replaceAll("ontouml/1.0/", "").replaceAll("ontouml/2.0/", "");

					ViewUtils.simpleLog(line.trim(), SCOPE_PLUGIN);
				}
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

	public static int smartPaintEnableDialog() {
		final File pluginDir = ApplicationManager.instance().getPluginInfo(OntoUMLPlugin.PLUGIN_ID).getPluginDir();
		final File logoFile = Paths.get(pluginDir.getAbsolutePath(),"icons","logo","ontouml-simple-logo.png").toFile();

		return ApplicationManager.instance().getViewManager().showConfirmDialog(null,
				"Smart Paint is disabled. Do you want to enable this feature?\n"+
				"Warning: this feature will affect all diagrams within the project.", 
				"Smart Paint", JOptionPane.YES_NO_OPTION,
				JOptionPane.INFORMATION_MESSAGE, new ImageIcon(logoFile.getAbsolutePath()));
	}

	public static int smartPaintConfirmationDialog() {
		final File pluginDir = ApplicationManager.instance().getPluginInfo(OntoUMLPlugin.PLUGIN_ID).getPluginDir();
		final File logoFile = Paths.get(pluginDir.getAbsolutePath(),"icons","logo","ontouml-simple-logo.png").toFile();

		return ApplicationManager.instance().getViewManager().showConfirmDialog(null,
				"Warning: this feature will affect all diagrams within the project.\n" +
				"Do you want to proceed?",
				"Smart Paint", JOptionPane.YES_NO_OPTION,
				JOptionPane.INFORMATION_MESSAGE, new ImageIcon(logoFile.getAbsolutePath()));
	}

}
