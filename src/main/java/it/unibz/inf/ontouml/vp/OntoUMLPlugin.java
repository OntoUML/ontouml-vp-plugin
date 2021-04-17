package it.unibz.inf.ontouml.vp;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ProjectManager;
import com.vp.plugin.VPPlugin;
import com.vp.plugin.VPPluginInfo;
import com.vp.plugin.model.IProject;
import it.unibz.inf.ontouml.vp.listeners.ProjectListener;

/**
 * Implementation of VPPlugin responsible for configuring OntoUML Plugin's behaviour when loading
 * and unload.
 *
 * @author Victor Viola
 * @author Claudenir Fonseca
 */
public class OntoUMLPlugin implements VPPlugin {

  public static final String PLUGIN_VERSION_RELEASE = "0.5.0";
  public static final String PLUGIN_ID = "it.unibz.inf.ontouml.vp";
  public static final String PLUGIN_NAME = "OntoUML Plugin";
  public static final String PLUGIN_REPO = "https://github.com/OntoUML/ontouml-vp-plugin/";
  public static final String PLUGIN_REPO_OWNER = "OntoUML";
  public static final String PLUGIN_REPO_NAME = "ontouml-vp-plugin";

  private static boolean isExportToGUFOWindowOpen;
  private static boolean isDBExportWindowOpen;
  private static boolean isOBDAExportWindowOpen;
  private static boolean isConfigWindowOpen;
  private static ProjectListener projectListener;

  /** OntoUMLPlugin constructor. Declared to make explicit Open API requirements. */
  public OntoUMLPlugin() {
    // The constructor of a VPPlugin MUST NOT have parameters.
    isExportToGUFOWindowOpen = false;
    isDBExportWindowOpen = false;
    isOBDAExportWindowOpen = false;
    isConfigWindowOpen = false;
    System.out.println("OntoUML Plugin (v" + PLUGIN_VERSION_RELEASE + ") loaded successfully.");
  }

  /**
   * Called by Visual Paradigm when the plugin is loaded.
   *
   * @param pluginInfo
   */
  @Override
  public void loaded(VPPluginInfo pluginInfo) {
    final ProjectManager pm = ApplicationManager.instance().getProjectManager();
    final IProject p = pm.getProject();

    projectListener = new ProjectListener();
    p.addProjectListener(projectListener);
  }

  /**
   * Called by Visual Paradigm when the plugin is unloaded (i.e., Visual Paradigm will be exited).
   * This method is not called when the plugin is reloaded.
   */
  @Override
  public void unloaded() {}

  public static void setExportToGUFOWindowOpen(boolean open) {
    isExportToGUFOWindowOpen = open;
  }

  public static boolean getExportToGUFOWindowOpen() {
    return isExportToGUFOWindowOpen;
  }

  public static void setConfigWindowOpen(boolean open) {
    isConfigWindowOpen = open;
  }

  public static boolean getConfigWindowOpen() {
    return isConfigWindowOpen;
  }
  
  public static boolean getDBExportWindowOpen() {
	  return isDBExportWindowOpen;
  }
  
  public static void setDBExportWindowOpen(boolean open) {
	  isDBExportWindowOpen = open;
  }
  
  public static boolean getOBDAExportWindowOpen() {
	  return isOBDAExportWindowOpen;
  }
  
  public static void setOBDAExportWindowOpen(boolean open) {
	  isOBDAExportWindowOpen = open;
  }
}
