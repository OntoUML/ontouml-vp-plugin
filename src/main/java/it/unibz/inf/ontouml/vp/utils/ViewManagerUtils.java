package it.unibz.inf.ontouml.vp.utils;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.DiagramManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.diagram.IClassDiagramUIModel;
import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;

import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.listeners.IssueLogMenuListener;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.GitHubRelease;
import it.unibz.inf.ontouml.vp.model.ProjectConfigurations;
import it.unibz.inf.ontouml.vp.views.HTMLEnabledMessage;

/**
 * 
 * Class responsible for facilitating display of messages on Visual Paradigm's
 * log.
 * 
 * @author Claudenir Fonseca
 * @author Victor Viola
 *
 */
public class ViewManagerUtils {

	public static final String SCOPE_PLUGIN = "OntoUML";
	public static final String SCOPE_VERIFICATION = "Verification Log";
	public static final String SCOPE_DEVELOPMENT_LOG = "DevLog";

	public static final String SIMPLE_LOGO = "simple_logo";
	public static final String SIMPLE_LOGO_FILENAME = "ontouml-simple-logo.png";
	public static final String NAVIGATION_LOGO = "navigation";
	public static final String NAVIGATION_LOGO_FILENAME = "navigation.png";
	public static final String MORE_HORIZ_LOGO = "more_horiz";
	public static final String MORE_HORIZ_LOGO_FILENAME = "more_horiz.png";
	public static final String PACKAGE_LOGO = "package";
	public static final String PACKAGE_LOGO_FILENAME = "package.png";
	public static final String CLASS_LOGO = "class";
	public static final String CLASS_LOGO_FILENAME = "class.png";
	public static final String ASSOCIATION_LOGO = "association";
	public static final String ASSOCIATION_LOGO_FILENAME = "association.png";
	public static final String GENERALIZATION_LOGO = "generalization";
	public static final String GENERALIZATION_LOGO_FILENAME = "generalization.png";
	public static final String GENERALIZATION_SET_LOGO = "generalizationSet";
	public static final String GENERALIZATION_SET_LOGO_FILENAME = "generalizationset.png";
	public static final String DIAGRAM_LOGO = "diagram";
	public static final String DIAGRAM_LOGO_FILENAME = "diagram.png";
	public static final String DATATYPE_LOGO = "datatype";
	public static final String DATATYPE_LOGO_FILENAME = "datatype.png";
	public static final String ATTRIBUTE_LOGO = "attribute";
	public static final String ATTRIBUTE_LOGO_FILENAME = "attribute.png";

	public static void simpleLog(String message) {
		ApplicationManager.instance().getViewManager().showMessage(message);
	}

	public static void simpleDialog(String title, String message) {
		ApplicationManager.instance().getViewManager().showConfirmDialog(null, message, title,
				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getFilePath(SIMPLE_LOGO)));
	}

	public static void cleanAndShowMessage(String message) {
		ApplicationManager.instance().getViewManager().removeMessagePaneComponent(OntoUMLPlugin.PLUGIN_ID);

		ArrayList<String> messageList = new ArrayList<String>();
		messageList.add(timestamp() + message);
		JList<Object> list = new JList<>(messageList.toArray());
		JScrollPane parentContainer = new JScrollPane(list);
		ApplicationManager.instance().getViewManager().showMessagePaneComponent(OntoUMLPlugin.PLUGIN_ID,
				ViewManagerUtils.SCOPE_PLUGIN, parentContainer);

	}

	private static String timestamp() {
		return "[" + (new Timestamp(System.currentTimeMillis())) + "] ";
	}

	public static String getFilePath(String imageName) {

		final File pluginDir = ApplicationManager.instance().getPluginInfo(OntoUMLPlugin.PLUGIN_ID).getPluginDir();

		switch (imageName) {
		case SIMPLE_LOGO:
			return Paths.get(pluginDir.getAbsolutePath(), "icons", "logo", SIMPLE_LOGO_FILENAME).toFile()
					.getAbsolutePath();
		case NAVIGATION_LOGO:
			return Paths.get(pluginDir.getAbsolutePath(), "icons", NAVIGATION_LOGO_FILENAME).toFile().getAbsolutePath();
		case MORE_HORIZ_LOGO:
			return Paths.get(pluginDir.getAbsolutePath(), "icons", MORE_HORIZ_LOGO_FILENAME).toFile().getAbsolutePath();
		case PACKAGE_LOGO:
			return Paths.get(pluginDir.getAbsolutePath(), "icons", PACKAGE_LOGO_FILENAME).toFile().getAbsolutePath();
		case CLASS_LOGO:
			return Paths.get(pluginDir.getAbsolutePath(), "icons", CLASS_LOGO_FILENAME).toFile().getAbsolutePath();
		case ASSOCIATION_LOGO:
			return Paths.get(pluginDir.getAbsolutePath(), "icons", ASSOCIATION_LOGO_FILENAME).toFile()
					.getAbsolutePath();
		case GENERALIZATION_LOGO:
			return Paths.get(pluginDir.getAbsolutePath(), "icons", GENERALIZATION_LOGO_FILENAME).toFile()
					.getAbsolutePath();
		case GENERALIZATION_SET_LOGO:
			return Paths.get(pluginDir.getAbsolutePath(), "icons", GENERALIZATION_SET_LOGO_FILENAME).toFile()
					.getAbsolutePath();
		case DIAGRAM_LOGO:
			return Paths.get(pluginDir.getAbsolutePath(), "icons", DIAGRAM_LOGO_FILENAME).toFile().getAbsolutePath();
		case DATATYPE_LOGO:
			return Paths.get(pluginDir.getAbsolutePath(), "icons", DATATYPE_LOGO_FILENAME).toFile().getAbsolutePath();
		case ATTRIBUTE_LOGO:
			return Paths.get(pluginDir.getAbsolutePath(), "icons", ATTRIBUTE_LOGO_FILENAME).toFile().getAbsolutePath();
		default:
			return null;
		}
	}

	public static void logDiagramVerificationResponse(String responseMessage) {
		ArrayList<String> errorList = new ArrayList<String>();
		ArrayList<String> idModelElementList = new ArrayList<String>();

		try {
			JsonArray response = (JsonArray) new JsonParser().parse(responseMessage).getAsJsonArray();

			final int errorCount = errorCountInCurrentDiagram(responseMessage);
			final String diagramName = getCurrentClassDiagramName();

			verificationDiagramConcludedDialog(errorCount, diagramName);

			if (errorCount == 0)
				errorList.add("No issues were found in diagram \"" + diagramName + "\".");

			for (JsonElement elem : response) {
				final JsonObject error = elem.getAsJsonObject();
				final String id = error.getAsJsonObject("source").get("id").getAsString();

				if (isElementInCurrentDiagram(id)) {
					final StringBuilder errorMessage = new StringBuilder();
					errorMessage.append(
							!error.get("severity").isJsonNull() ? error.get("severity").getAsString().toUpperCase()
									: "");
					errorMessage.append(": ");
					errorMessage.append(!error.get("title").isJsonNull() ? error.get("title").getAsString() : "");
					errorMessage.append(" ");
					errorMessage.append(
							!error.get("description").isJsonNull() ? error.get("description").getAsString() : "");

					errorList.add(timestamp() + errorMessage.toString());
					idModelElementList.add(id);
				}
			}

			JList<Object> list = new JList<>(errorList.toArray());
			IssueLogMenuListener listener = new IssueLogMenuListener(idModelElementList, list);
			list.addMouseListener(listener);
			list.addMouseMotionListener(listener);

			JScrollPane parentContainer = new JScrollPane(list);
			ApplicationManager.instance().getViewManager().showMessagePaneComponent(OntoUMLPlugin.PLUGIN_ID,
					SCOPE_PLUGIN, parentContainer);

		} catch (JsonSyntaxException e) {
			verificationServerErrorDialog(responseMessage);
		}
	}

	public static void logVerificationResponse(String responseMessage) {
		ArrayList<String> errorList = new ArrayList<String>();
		ArrayList<String> idModelElementList = new ArrayList<String>();
		try {
			JsonArray response = (JsonArray) new JsonParser().parse(responseMessage).getAsJsonArray();
			final int errorCount = response.size();

			verificationConcludedDialog(errorCount);

			if (errorCount == 0)
				errorList.add("No issues were found in your project.");

			for (JsonElement elem : response) {
				final JsonObject error = elem.getAsJsonObject();
				final String id = error.getAsJsonObject("source").get("id").getAsString();

				final StringBuilder errorMessage = new StringBuilder();
				errorMessage.append(
						!error.get("severity").isJsonNull() ? error.get("severity").getAsString().toUpperCase() : "");
				errorMessage.append(": ");
				errorMessage.append(!error.get("title").isJsonNull() ? error.get("title").getAsString() : "");
				errorMessage.append(" ");
				errorMessage
						.append(!error.get("description").isJsonNull() ? error.get("description").getAsString() : "");

				errorList.add(timestamp() + errorMessage.toString());
				idModelElementList.add(id);

			}

			JList<Object> list = new JList<>(errorList.toArray());
			IssueLogMenuListener listener = new IssueLogMenuListener(idModelElementList, list);
			list.addMouseListener(listener);
			list.addMouseMotionListener(listener);

			JScrollPane parentContainer = new JScrollPane(list);
			ApplicationManager.instance().getViewManager().showMessagePaneComponent(OntoUMLPlugin.PLUGIN_ID,
					SCOPE_PLUGIN, parentContainer);

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
					"Issues found in your project: " + nIssues + ".\n"
							+ "For details, click on the \"Show Message\" icon on the bottom right corner of the app.",
					"Verification Service", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
					new ImageIcon(getFilePath(SIMPLE_LOGO)));
		} else {
			ApplicationManager.instance().getViewManager().showConfirmDialog(null,
					"No issues were found in your project.", "Verification Service", JOptionPane.DEFAULT_OPTION,
					JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getFilePath(SIMPLE_LOGO)));
		}
	}

	public static void verificationDiagramConcludedDialog(int nIssues, String diagramName) {
		if (nIssues > 0) {
			ApplicationManager.instance().getViewManager().showConfirmDialog(null,
					"Issues found in diagram \"" + diagramName + "\": " + nIssues + ".\n"
							+ "For details, click on the \"Show Message\" icon on the bottom right corner of the app.",
					"Verification Service", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
					new ImageIcon(getFilePath(SIMPLE_LOGO)));
		} else {
			ApplicationManager.instance().getViewManager().showConfirmDialog(null,
					"No issues were found in diagram \"" + diagramName + "\".\n"
							+ "Other issues may still exist in your project.",
					"Verification Service", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
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

	public static void saveFile(BufferedReader buffer) throws IOException {
		final Configurations configs = Configurations.getInstance();
		final ProjectConfigurations projectConfigurations = configs.getProjectConfigurations();
		final FileDialog fd = new FileDialog((Frame) ApplicationManager.instance().getViewManager().getRootFrame(),
				"Choose destination", FileDialog.SAVE);

		String suggestedFolderPath = projectConfigurations.getExportGUFOFolderPath();
		String suggestedFileName = projectConfigurations.getExportGUFOFilename();

		if (suggestedFileName.isEmpty()) {
			String projectName = ApplicationManager.instance().getProjectManager().getProject().getName();
			suggestedFileName = projectName + ".ttl";
		}

		fd.setDirectory(suggestedFolderPath);
		fd.setFile(suggestedFileName);
		fd.setVisible(true);

		if (fd.getDirectory() != null && fd.getFile() != null) {
			final String fileDirectory = fd.getDirectory();
			final String fileName = !fd.getFile().endsWith(".ttl") ? fd.getFile() + ".ttl" : fd.getFile();
			final String output = buffer.lines().collect(Collectors.joining("\n"));

			Files.write(Paths.get(fileDirectory, fileName), output.getBytes());
			projectConfigurations.setExportGUFOFolderPath(fileDirectory);
			projectConfigurations.setExportGUFOFilename(fileName);
			configs.save();
		}

		ViewManagerUtils.cleanAndShowMessage("File saved successfuly.");
	}

	public static String getCurrentClassDiagramName() {
		final IDiagramUIModel[] diagramArray = ApplicationManager.instance().getProjectManager().getProject()
				.toDiagramArray();

		if (diagramArray == null)
			return null;

		for (IDiagramUIModel diagram : diagramArray) {
			if (diagram instanceof IClassDiagramUIModel && diagram.isOpened())
				return diagram.getName();
		}

		return null;
	}

	public static String getCurrentClassDiagramId() {
		final IDiagramUIModel[] diagramArray = ApplicationManager.instance().getProjectManager().getProject()
				.toDiagramArray();

		if (diagramArray == null)
			return null;

		for (IDiagramUIModel diagram : diagramArray) {
			if (diagram instanceof IClassDiagramUIModel && diagram.isOpened())
				return diagram.getId();
		}

		return null;
	}

	public static IDiagramUIModel getCurrentClassDiagram() {

		return ApplicationManager.instance().getProjectManager().getProject()
				.getDiagramById(getCurrentClassDiagramId());
	}

	public static boolean isElementInCurrentDiagram(String id) {

		if (getCurrentClassDiagram() == null)
			return false;

		for (IDiagramElement element : getCurrentClassDiagram().toDiagramElementArray()) {
			if (element.getModelElement().getId().equals(id))
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

	public static boolean isElementInAnyDiagram(String elementId) {
		final IProject project = ApplicationManager.instance().getProjectManager().getProject();
		final IModelElement element = project.getModelElementById(elementId);
		final IDiagramElement[] diagramElements = element != null ? element.getDiagramElements() : null;

		return diagramElements != null && diagramElements.length > 0;
	}

	public static void openSpecDiagramElement(String elementId) {
		final ApplicationManager app = ApplicationManager.instance();
		final IModelElement element = app.getProjectManager().getProject().getModelElementById(elementId);
		final ViewManager viewManager = app.getViewManager();

		viewManager.openSpec(element, viewManager.getRootFrame());
	}

	public static void highlightDiagramElement(String modelElementId) {
		final ApplicationManager app = ApplicationManager.instance();
		final IProject project = app.getProjectManager().getProject();
		final IModelElement modelElement = project.getModelElementById(modelElementId);

		if (modelElement == null) {
			return;
		}

		final DiagramManager diagramManager = app.getDiagramManager();
		final IDiagramElement[] diagramElements = modelElement.getDiagramElements();
		IDiagramElement activeView = null;
		IDiagramElement masterView = null;
		IDiagramElement firstView = null;

		for (int i = 0; diagramElements != null && i < diagramElements.length; i++) {
			IDiagramElement diagramElement = diagramElements[i];

			if (diagramElement == null) {
				continue;
			}

			firstView = firstView == null ? diagramElement : firstView;
			activeView = diagramElement.getDiagramUIModel().isOpened() ? diagramElement : activeView;
			masterView = diagramElement.isMasterView() ? diagramElement : masterView;

			if (activeView != null) {
				break;
			}
		}

		if (activeView != null) {
			diagramManager.highlight(activeView);
		} else if (masterView != null) {
			diagramManager.highlight(masterView);
		} else if (firstView != null) {
			diagramManager.highlight(firstView);
		}
	}

	public static String setOrderDialog(String currentOrder) {
		final ViewManager vm = ApplicationManager.instance().getViewManager();
		final String msg = "Please enter a value for the type order,\n" + "where '*' represents an orderless type:";
		final String title = "Set type order";
		final Icon icon = new ImageIcon(getFilePath(SIMPLE_LOGO));

		return (String) vm.showInputDialog(vm.getRootFrame(), msg, title, JOptionPane.PLAIN_MESSAGE, icon, null,
				currentOrder);
	}

	public static void reportBugErrorDialog(boolean isOperationNotAllowed) {
		final ViewManager vm = ApplicationManager.instance().getViewManager();
		final String body = isOperationNotAllowed
				? "Unable to open the browser. Please visit <a href=\"" + OntoUMLPlugin.PLUGIN_REPO + "\">"
						+ OntoUMLPlugin.PLUGIN_REPO + "</a> to submit bugs."
				: "Something went wrong. Please visit <a href=\"" + OntoUMLPlugin.PLUGIN_REPO + "\">"
						+ OntoUMLPlugin.PLUGIN_REPO + "</a> to submit bugs.";

		vm.showConfirmDialog(null, new HTMLEnabledMessage(body), "Report Error", JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE, new ImageIcon(getFilePath(SIMPLE_LOGO)));
	}

	public static GitHubRelease updateDialog() {
		final Configurations config = Configurations.getInstance();
		final GitHubRelease lastestRelease = config.getLatestRelease();

		final boolean isUpToDate = lastestRelease.getTagName().equals(OntoUMLPlugin.PLUGIN_VERSION_RELEASE);
		String[] options;
		String initialSelection;
		StringBuilder msg = new StringBuilder();

		if (isUpToDate) {
			options = new String[2];
			options[0] = "Cancel";
			options[1] = "Select release";

			initialSelection = options[1];

			msg.append("Your plugin is up to date with the latest stable release.");
			msg.append("\nIf you desire to install a different version of the plugin, click on \"Select release\".");
			msg.append("\nBe aware that \"alpha\" releases provide experimental new features that may not be fully integrated to the plugin.");
			msg.append("\n\nThe procedure may take a couple of seconds.");
		} else {
			options = new String[3];
			options[0] = "Cancel";
			options[1] = "Select release";
			options[2] = "Install latest release";

			initialSelection = options[2];

			msg.append("The latest stable release of the plugin is the version " + lastestRelease.getTagName()
					+ ".");
			msg.append(
					"\nTo install this update, click on  \"Install latest release\", or click on \"Select a release\" to install a different version.");
			msg.append("\nBe aware that \"alpha\" releases are provide experimental new features that may present some issues.");
			msg.append("\n\nThe procedure may take a couple of seconds.");
		}

		final ViewManager vm = ApplicationManager.instance().getViewManager();
		int selectedOption = vm.showOptionDialog(vm.getRootFrame(), msg.toString(), "Plugin Update",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon(getFilePath(SIMPLE_LOGO)),
				options, initialSelection);

		switch (selectedOption) {
		case 2:
			return lastestRelease;
		case 1:
			return selectReleaseToInstall();
		default:
			return null;
		}
	}

	public static void updateSuccessDialog() {
		final ViewManager vm = ApplicationManager.instance().getViewManager();
		final String msg = "Plugin successfully updated.\nPlease reopen the application for the changes to take effect.";

		vm.showConfirmDialog(null, msg, "Plugin Update", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
				new ImageIcon(getFilePath(SIMPLE_LOGO)));
	}

	public static void updateErrorDialog() {
		final ViewManager vm = ApplicationManager.instance().getViewManager();
		final StringBuilder builder = new StringBuilder();
		builder.append("Something went wrong during the update. Please verify your connection.<br>");
		builder.append("In case your plugin becomes unavailable, you may find instructions at <a href=\""
				+ OntoUMLPlugin.PLUGIN_REPO + "\">" + OntoUMLPlugin.PLUGIN_REPO + "</a>.<br>");
		builder.append("In this page you can also report this error and help us to improve our plugin.");

		vm.showConfirmDialog(null, new HTMLEnabledMessage(builder.toString()), "Plugin Update Error",
				JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, new ImageIcon(getFilePath(SIMPLE_LOGO)));
	}

	public static GitHubRelease selectReleaseToInstall() {
		final Map<String, GitHubRelease> map = new HashMap<>();
		final Configurations config = Configurations.getInstance();
		final GitHubRelease installedRelease = config.getInstalledRelease();
		final String installedReleaseTagName = installedRelease != null
				? installedRelease.getTagName() : null;

		config.getReleases().forEach(release -> {
			String releaseTagName = release.getTagName();

			releaseTagName = installedReleaseTagName != null && releaseTagName.equals(installedReleaseTagName)
					? releaseTagName + " (installed version)" : releaseTagName;
			
			map.put(releaseTagName, release);
		});

		ViewManager vm = ApplicationManager.instance().getViewManager();
		List<String> keys = new ArrayList<String>(map.keySet());
		keys.sort(Comparator.reverseOrder());
		Object[] keysArray = new String[keys.size()];
		keys.toArray(keysArray);
		Object selectedValue = vm.showInputDialog(vm.getRootFrame(),
				"Select the desired version of the OntoUML Plugin:", "Plugin Versions", JOptionPane.QUESTION_MESSAGE,
				new ImageIcon(getFilePath(SIMPLE_LOGO)), keysArray, keysArray[0]);

		return map.get(selectedValue);
	}
}

