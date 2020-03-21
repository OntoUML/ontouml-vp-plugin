package it.unibz.inf.ontouml.vp.utils;

import java.io.File;
import java.net.HttpURLConnection;
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
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;

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
	
	public static void simpleDialog(String title, String message) {
		ApplicationManager.instance().getViewManager().showConfirmDialog(null, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getFilePath(SIMPLE_LOGO)));
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

	public static void logDiagramVerificationResponse(String responseMessage) {
		try {
			JsonArray response = (JsonArray) new JsonParser().parse(responseMessage).getAsJsonArray();
			final int errorCount = errorCountInCurrentDiagram(responseMessage);

			if (errorCount == 0) {
				verificationConcludedDialog(0);
			} else {
				verificationDiagramConcludedDialog(errorCount, getCurrentClassDiagramName());

				ViewUtils.simpleLog("--------- Diagram Verification Service ---------", SCOPE_PLUGIN);
				for (JsonElement elem : response) {
					final JsonObject error = elem.getAsJsonObject();
					final String id = error.getAsJsonObject("source").get("id").getAsString();
					if (isElementInCurrentDiagram(id)) {
						final String errorMessage = error.get("severity").getAsString() + ":" + " " + error.get("title").getAsString() + " " + error.get("description").getAsString();

						ViewUtils.simpleLog(errorMessage, SCOPE_PLUGIN);
					}
				}
				ViewUtils.simpleLog("---------------------------------------------------", SCOPE_PLUGIN);
			}
		} catch (JsonSyntaxException e) {
			verificationServerErrorDialog(responseMessage);
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
			verificationServerErrorDialog(responseMessage);
		}
	}

	public static void verificationServerErrorDialog(String userMessage) {
		ApplicationManager.instance().getViewManager().showConfirmDialog(null, userMessage, "Verification Service",
				JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, new ImageIcon(getFilePath(SIMPLE_LOGO)));
	}

	public static void verificationConcludedDialog(int nIssues) {
		if (nIssues > 0) {
			ApplicationManager.instance().getViewManager().showConfirmDialog(null,
					"Verification found " + nIssues + " issue(s) in this project. \n"
							+ "Please check the log at the right bottom corner.",
					"Verification Service", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
					new ImageIcon(getFilePath(SIMPLE_LOGO)));
		} else {
			ApplicationManager.instance().getViewManager().showConfirmDialog(null,
					"No syntatical errors found!\nAll diagrams verified and no syntatical issues were found.", "Verification Service",
					JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
					new ImageIcon(getFilePath(SIMPLE_LOGO)));
		}
	}
	
	public static void verificationDiagramConcludedDialog(int nIssues, String diagramName) {
		if (nIssues > 0) {
			ApplicationManager.instance().getViewManager().showConfirmDialog(null,
					"Verification found " + nIssues + " issue(s) in the " + diagramName + " diagram.\n"
							+ "Please check the log at the right bottom corner.",
					"Verification Service", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
					new ImageIcon(getFilePath(SIMPLE_LOGO)));
		} else {
			ApplicationManager.instance().getViewManager().showConfirmDialog(null,
					"No syntatical errors found!\n" + "Diagram " + diagramName + " was verified and no syntatical issues were found.", "Verification Service",
					JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
					new ImageIcon(getFilePath(SIMPLE_LOGO)));
		}
	}

	public static void verificationFailedDialog(String msg) {
		ApplicationManager.instance().getViewManager().showConfirmDialog(null, msg, "Verification Service",
				JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, new ImageIcon(getFilePath(SIMPLE_LOGO)));
	}

	public static boolean verificationFailedDialogWithOption(String msg, int httpCode) {
		final ProjectConfigurations configurations = Configurations.getInstance().getProjectConfigurations();

		if (configurations.isCustomServerEnabled() && (httpCode == HttpURLConnection.HTTP_NOT_FOUND
				|| httpCode == HttpURLConnection.HTTP_INTERNAL_ERROR)) {

			int option = ApplicationManager.instance().getViewManager().showConfirmDialog(null,
					msg + "\nDo you want to retry using the default server?", "Verification Service",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
					new ImageIcon(getFilePath(SIMPLE_LOGO)));

			if (option == JOptionPane.OK_OPTION) {
				configurations.setCustomServerEnabled(false);
				return true;
			} else {
				return false;
			}

		} else {
			verificationFailedDialog(msg);
			return false;
		}
	}

	public static void exportToGUFOIssueDialog(String msg) {
		ApplicationManager.instance().getViewManager().showConfirmDialog(null, msg, "Export to gUFO",
				JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, new ImageIcon(getFilePath(SIMPLE_LOGO)));
	}

	public static boolean exportToGUFOIssueDialogWithOption(String msg, int httpCode) {
		final ProjectConfigurations configurations = Configurations.getInstance().getProjectConfigurations();

		if (configurations.isCustomServerEnabled() && (httpCode == HttpURLConnection.HTTP_NOT_FOUND
				|| httpCode == HttpURLConnection.HTTP_INTERNAL_ERROR)) {

			int option = ApplicationManager.instance().getViewManager().showConfirmDialog(null,
					msg + "\nDo you want to retry using the default server?", "Export to gUFO",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
					new ImageIcon(getFilePath(SIMPLE_LOGO)));

			if (option == JOptionPane.OK_OPTION) {
				configurations.setCustomServerEnabled(false);
				return true;
			} else {
				return false;
			}

		} else {
			exportToGUFOIssueDialog(msg);
			return false;
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
	
	public static String getCurrentClassDiagramName() {
		final IDiagramUIModel[] diagramArray = ApplicationManager.instance().getProjectManager().getProject().toDiagramArray();

		if (diagramArray == null)
			return null;

		for (IDiagramUIModel diagram : diagramArray) {
			if (diagram instanceof IClassDiagramUIModel && diagram.isOpened())
				return diagram.getName();
		}

		return null;
	}
	
	public static String getCurrentClassDiagramId() {
		final IDiagramUIModel[] diagramArray = ApplicationManager.instance().getProjectManager().getProject().toDiagramArray();

		if (diagramArray == null)
			return null;

		for (IDiagramUIModel diagram : diagramArray) {
			if (diagram instanceof IClassDiagramUIModel && diagram.isOpened())
				return diagram.getId();
		}

		return null;
	}
	
	public static IDiagramUIModel getCurrentClassDiagram() {

		return ApplicationManager.instance().getProjectManager().getProject().getDiagramById(getCurrentClassDiagramId());
	}
	
	public static boolean isElementInCurrentDiagram(String id) {
		
		if(getCurrentClassDiagram() == null)
			return false;
		
		
		for(IDiagramElement element : getCurrentClassDiagram().toDiagramElementArray()){
			if(element.getModelElement().getId().equals(id))
				return true;
		}

		return false;
	}
	
	public static int errorCountInCurrentDiagram(String responseMessage) {
		int errorCount = 0;

		try {
			JsonArray response = (JsonArray) new JsonParser().parse(responseMessage).getAsJsonArray();

			for (JsonElement elem : response) {
				if (isElementInCurrentDiagram(elem.getAsJsonObject().getAsJsonObject("source").get("id").getAsString()))
					errorCount++;
			}
		} catch (JsonSyntaxException e) {
			return 0;
		}
		
		return errorCount;
	}
}
