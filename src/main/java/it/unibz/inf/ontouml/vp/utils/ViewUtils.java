package it.unibz.inf.ontouml.vp.utils;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
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

import it.unibz.inf.ontouml.vp.OntoUMLPlugin;

/**
 * 
 * Class responsible for facilitating display of messages on Visual Paradigm's log.
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

	public static void cleanAndShowMessage(String message) {

		ApplicationManager.instance().getViewManager().removeMessagePaneComponent(OntoUMLPlugin.PLUGIN_ID);

		ArrayList<String> messageList = new ArrayList<String>();
		messageList.add(timestamp() + message);
		JList<Object> list = new JList<>(messageList.toArray());
		JScrollPane parentContainer = new JScrollPane(list);
		ApplicationManager.instance().getViewManager().showMessagePaneComponent(OntoUMLPlugin.PLUGIN_ID, ViewUtils.SCOPE_PLUGIN, parentContainer);

	}

	private static String timestamp() {
		return "[" + (new Timestamp(System.currentTimeMillis())) + "] ";
	}

	public static String getFilePath(String imageName) {

		final File pluginDir = ApplicationManager.instance().getPluginInfo(OntoUMLPlugin.PLUGIN_ID).getPluginDir();

		switch (imageName) {
		case SIMPLE_LOGO:
			return Paths.get(pluginDir.getAbsolutePath(), "icons", "logo", SIMPLE_LOGO_FILENAME).toFile().getAbsolutePath();
		case NAVIGATION_LOGO:
			return Paths.get(pluginDir.getAbsolutePath(), "icons", NAVIGATION_LOGO_FILENAME).toFile().getAbsolutePath();
		case MORE_HORIZ_LOGO:
			return Paths.get(pluginDir.getAbsolutePath(), "icons", MORE_HORIZ_LOGO_FILENAME).toFile().getAbsolutePath();
		case PACKAGE_LOGO:
			return Paths.get(pluginDir.getAbsolutePath(), "icons", PACKAGE_LOGO_FILENAME).toFile().getAbsolutePath();
		case CLASS_LOGO:
			return Paths.get(pluginDir.getAbsolutePath(), "icons", CLASS_LOGO_FILENAME).toFile().getAbsolutePath();
		case ASSOCIATION_LOGO:
			return Paths.get(pluginDir.getAbsolutePath(), "icons", ASSOCIATION_LOGO_FILENAME).toFile().getAbsolutePath();
		case GENERALIZATION_LOGO:
			return Paths.get(pluginDir.getAbsolutePath(), "icons", GENERALIZATION_LOGO_FILENAME).toFile().getAbsolutePath();
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
					final String errorMessage = error.get("severity").getAsString() + ":" + " " + error.get("title").getAsString() + " " + error.get("description").getAsString();
					errorList.add(timestamp() + errorMessage);
					idModelElementList.add(id);
				}
			}

			JList<Object> list = new JList<>(errorList.toArray());
			ContextMenuListener listener = new ContextMenuListener(idModelElementList, list);
			list.addMouseListener(listener);
			list.addMouseMotionListener(listener);

			JScrollPane parentContainer = new JScrollPane(list);
			ApplicationManager.instance().getViewManager().showMessagePaneComponent(OntoUMLPlugin.PLUGIN_ID, SCOPE_PLUGIN, parentContainer);

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

				final String errorMessage = error.get("severity").getAsString() + ":" + " " + error.get("title").getAsString() + " " + error.get("description").getAsString();

				errorList.add(timestamp() + errorMessage);
				idModelElementList.add(id);
			}

			JList<Object> list = new JList<>(errorList.toArray());
			ContextMenuListener listener = new ContextMenuListener(idModelElementList, list);
			list.addMouseListener(listener);
			list.addMouseMotionListener(listener);

			JScrollPane parentContainer = new JScrollPane(list);
			ApplicationManager.instance().getViewManager().showMessagePaneComponent(OntoUMLPlugin.PLUGIN_ID, SCOPE_PLUGIN, parentContainer);

		} catch (JsonSyntaxException e) {
			verificationServerErrorDialog(responseMessage);
		}
	}

	public static void verificationServerErrorDialog(String userMessage) {
		ApplicationManager.instance().getViewManager()
				.showConfirmDialog(null, userMessage, "Verification Service", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, new ImageIcon(getFilePath(SIMPLE_LOGO)));
	}

	public static void verificationConcludedDialog(int nIssues) {
		if (nIssues > 0) {
			ApplicationManager
					.instance()
					.getViewManager()
					.showConfirmDialog(null, "Issues found in your project: " + nIssues + ".\n" + "For details, click on the \"Show Message\" icon on the bottom right corner of the app.",
							"Verification Service", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, new ImageIcon(getFilePath(SIMPLE_LOGO)));
		} else {
			ApplicationManager
					.instance()
					.getViewManager()
					.showConfirmDialog(null, "No issues were found in your project.", "Verification Service", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
							new ImageIcon(getFilePath(SIMPLE_LOGO)));
		}
	}

	public static void verificationDiagramConcludedDialog(int nIssues, String diagramName) {
		if (nIssues > 0) {
			ApplicationManager
					.instance()
					.getViewManager()
					.showConfirmDialog(null,
							"Issues found in diagram \"" + diagramName + "\": " + nIssues + ".\n" + "For details, click on the \"Show Message\" icon on the bottom right corner of the app.",
							"Verification Service", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, new ImageIcon(getFilePath(SIMPLE_LOGO)));
		} else {
			ApplicationManager
					.instance()
					.getViewManager()
					.showConfirmDialog(null, "No issues were found in diagram \"" + diagramName + "\".\n" + "Other issues may still exist in your project.", "Verification Service",
							JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getFilePath(SIMPLE_LOGO)));
		}
	}

	public static void verificationFailedDialog(String msg) {
		ApplicationManager.instance().getViewManager()
				.showConfirmDialog(null, msg, "Verification Service", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, new ImageIcon(getFilePath(SIMPLE_LOGO)));
	}

	public static boolean verificationFailedDialogWithOption(String msg, int httpCode) {
		final ProjectConfigurations configurations = Configurations.getInstance().getProjectConfigurations();

		if (configurations.isCustomServerEnabled() && (httpCode == HttpURLConnection.HTTP_NOT_FOUND || httpCode == HttpURLConnection.HTTP_INTERNAL_ERROR)) {

			int option = ApplicationManager
					.instance()
					.getViewManager()
					.showConfirmDialog(null, msg + "\nDo you want to retry using the default server?", "Verification Service", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
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
		ApplicationManager.instance().getViewManager().showConfirmDialog(null, msg, "Export to gUFO", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, new ImageIcon(getFilePath(SIMPLE_LOGO)));
	}

	public static boolean exportToGUFOIssueDialogWithOption(String msg, int httpCode) {
		final ProjectConfigurations configurations = Configurations.getInstance().getProjectConfigurations();

		if (configurations.isCustomServerEnabled() && (httpCode == HttpURLConnection.HTTP_NOT_FOUND || httpCode == HttpURLConnection.HTTP_INTERNAL_ERROR)) {

			int option = ApplicationManager
					.instance()
					.getViewManager()
					.showConfirmDialog(null, msg + "\nDo you want to retry using the default server?", "Export to gUFO", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
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
		return ApplicationManager
				.instance()
				.getViewManager()
				.showConfirmDialog(null, "Smart Paint is disabled. Do you want to enable this feature?\n" + "Warning: this feature will affect all diagrams within the project.", "Smart Paint",
						JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getFilePath(SIMPLE_LOGO)));
	}

	public static int smartPaintConfirmationDialog() {
		return ApplicationManager
				.instance()
				.getViewManager()
				.showConfirmDialog(null, "Warning: this feature will affect all diagrams within the project.\n" + "Do you want to proceed?", "Smart Paint", JOptionPane.YES_NO_OPTION,
						JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getFilePath(SIMPLE_LOGO)));
	}

	public static void saveFile(BufferedReader buffer) throws IOException {
		final Configurations configs = Configurations.getInstance();
		final ProjectConfigurations projectConfigurations = configs.getProjectConfigurations();
		final FileDialog fd = new FileDialog((Frame) ApplicationManager.instance().getViewManager().getRootFrame(), "Choose destination", FileDialog.SAVE);

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
		
		ViewUtils.cleanAndShowMessage("File saved successfuly.");
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
		final IDiagramUIModel[] diagramArray = ApplicationManager.instance().getProjectManager().getProject().toDiagramArray();

		if (diagramArray == null)
			return false;

		for (IDiagramUIModel diagram : diagramArray) {
			if (diagram instanceof IClassDiagramUIModel) {
				for (IDiagramElement diagramElement : diagram.toDiagramElementArray()) {
					IModelElement modelElement = diagramElement.getMetaModelElement();
					if (modelElement.getId() != null && modelElement.getId().equals(elementId)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public static void openSpecDiagramElement(String elementId) {
		final ApplicationManager app = ApplicationManager.instance();
		final IModelElement element = app.getProjectManager().getProject().getModelElementById(elementId);
		final ViewManager viewManager = app.getViewManager();

		viewManager.openSpec(element, viewManager.getRootFrame());
	}

	public static void highlightDiagramElement(String elementId) {
		final ApplicationManager app = ApplicationManager.instance();
		final IModelElement element = app.getProjectManager().getProject().getModelElementById(elementId);
		final DiagramManager diagramManager = app.getDiagramManager();
		IDiagramElement diagramElement = null;

		// Checks if the active diagram contains the element
		if (diagramManager.getActiveDiagram() != null) {
			final Iterator<?> iter = diagramManager.getActiveDiagram().diagramElementIterator();
			while (iter.hasNext()) {
				final IDiagramElement current = (IDiagramElement) iter.next();
				if (current.getModelElement().equals(element)) {
					diagramElement = current;
					continue;
				}
			}
		}

		// In case the active diagram does not contain it, get the master view or the first diagram element for that element
		if (diagramElement == null) {
			if (element.getMasterView() != null) {
				diagramElement = element.getMasterView();
			} else {
				final IDiagramElement[] diagramElements = element.getDiagramElements();
				diagramElement = diagramElements != null ? diagramElements[0] : null;
			}
		}

		// Highlights the diagram element if it is not null
		if (diagramElement != null) {
			diagramManager.highlight(diagramElement);
		}
	}
}

@SuppressWarnings("serial")
final class ContextMenu extends JPopupMenu {
	private JMenuItem takeMeThere;
	private JMenuItem openSpec;
	private ActionListener menuListener;

	public ContextMenu() {

	}

	public ContextMenu(String idModelElement) {

		menuListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (e.getActionCommand()) {
				case "Take me there!":
					System.out.println("Firing 'Highlight'");
					ViewUtils.highlightDiagramElement(idModelElement);
					break;
				case "Open Specification":
					System.out.println("Firing 'Open Specification'");
					ViewUtils.openSpecDiagramElement(idModelElement);
					break;
				default:
					break;
				}
			}
		};

		takeMeThere = new JMenuItem("Take me there!", new ImageIcon(ViewUtils.getFilePath(ViewUtils.NAVIGATION_LOGO)));
		takeMeThere.addActionListener(menuListener);
		openSpec = new JMenuItem("Open Specification", new ImageIcon(ViewUtils.getFilePath(ViewUtils.MORE_HORIZ_LOGO)));
		openSpec.addActionListener(menuListener);
		add(takeMeThere);
		add(openSpec);
	}

	public void disableItem(String item) {
		switch (item) {
		case "Take me there!":
			takeMeThere.setEnabled(false);
			break;
		case "Open Specification":
			openSpec.setEnabled(false);
			break;
		default:
			break;
		}
	}
}

final class ContextMenuListener extends MouseAdapter {
	private ArrayList<String> idModelElementList;
	private JList<Object> messageList;

	ContextMenuListener(ArrayList<String> list, JList<Object> messages) {
		super();
		idModelElementList = list;
		messageList = messages;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		final Point p = e.getPoint();
		final int index = messageList.locationToIndex(p);

		messageList.setSelectedIndex(index);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		messageList.clearSelection();
	}

	public void mouseReleased(MouseEvent e) {
		doPop(e);
	}

	private void doPop(MouseEvent e) {
		ContextMenu menu;
		String idModelElement = idModelElementList.get(messageList.locationToIndex(e.getPoint()));

		if (idModelElement == null) {
			menu = new ContextMenu();
		} else {
			menu = new ContextMenu(idModelElement);
			if (!ViewUtils.isElementInAnyDiagram(idModelElement))
				menu.disableItem("Take me there!");
		}

		menu.show(e.getComponent(), e.getX(), e.getY());
	}
}