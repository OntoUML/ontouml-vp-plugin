package it.unibz.inf.ontouml.vp.utils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * Class that captures user preferences for a given project and enables JSON serialization.
 * 
 * @author Claudenir Fonseca
 * @author Victor Viola
 *
 */
public class ProjectConfigurations {
	
	public static final boolean DEFAULT_IS_PLUGIN_ENABLED = true;
	public static final boolean DEFAULT_IS_CUSTOM_SERVER_ENABLED = false;
	public static final boolean DEFAULT_IS_EXPORT_ENABLED = true;
	public static final boolean DEFAULT_IS_AUTOMATIC_COLORING_ENABLED = true;
	public static final String DEFAULT_SERVER_URL = "https://api.ontouml.org";
	public static final String DEFAULT_EXPORT_PATH = System.getProperty("user.home");
	public static final String DEFAULT_EXPORT_FILENAME = "";
	public static final String DEFAULT_GUFO_EXPORT_PATH = System.getProperty("user.home");
	public static final String DEFAULT_GUFO_EXPORT_FILENAME = "";

	@SerializedName("projectId")
	@Expose()
	private String id;
	
	@SerializedName("isOntoUMLPluginEnabled")
	@Expose()
	private boolean isOntoUMLPluginEnabled;

	@SerializedName("serverURL")
	@Expose()
	private String serverURL;
	
	@SerializedName("isCustomServerEnabled")
	@Expose()
	private boolean isCustomServerEnabled;
	
	@SerializedName("exportFolderPath")
	@Expose()
	private String exportFolderPath;
	
	@SerializedName("exportFileName")
	@Expose()
	private String exportFileName;

	@SerializedName("exportGUFOFolderPath")
	@Expose()
	private String exportGUFOFolderPath;
	
	@SerializedName("exportGUFOFileName")
	@Expose()
	private String exportGUFOFileName;
	
	@SerializedName("isExportEnabled")
	@Expose()
	private boolean isModelExportEnabled;
	
	@SerializedName("isAutomaticColoringEnabled")
	@Expose()
	private boolean isAutomaticColoringEnabled;

	/**
	 *
	 * Constructor without args to be called when deserializing project settings.
	 *
	 */
	public ProjectConfigurations() {
		this.id = "";
		this.setDefaultValues();
	}

	/**
	 * 
	 * Initializes an instance of ProjectConfigurations with default settings.
	 * 
	 * @param projectId - String containing the ID of the project related to initialized configuration.
	 * 
	 */
	public ProjectConfigurations(String projectId) {
		this.id = projectId;
		this.setDefaultValues();
	}
	
	/** 
	 * 
	 * Resets default project configurations.
	 * By default, none of the options are enabled and the server's URL is the plugin's defaults.
	 * 
	 */
	public void setDefaultValues() {
		this.isOntoUMLPluginEnabled = ProjectConfigurations.DEFAULT_IS_PLUGIN_ENABLED;
		
		this.isCustomServerEnabled = ProjectConfigurations.DEFAULT_IS_CUSTOM_SERVER_ENABLED;
		this.serverURL = ProjectConfigurations.DEFAULT_SERVER_URL;
		
		this.isModelExportEnabled = ProjectConfigurations.DEFAULT_IS_EXPORT_ENABLED;
		this.exportFolderPath = ProjectConfigurations.DEFAULT_EXPORT_PATH;
		this.exportFileName = ProjectConfigurations.DEFAULT_EXPORT_FILENAME;
		this.exportGUFOFolderPath = ProjectConfigurations.DEFAULT_GUFO_EXPORT_PATH;
		this.exportGUFOFileName = ProjectConfigurations.DEFAULT_GUFO_EXPORT_FILENAME;
		
		this.isAutomaticColoringEnabled = ProjectConfigurations.DEFAULT_IS_AUTOMATIC_COLORING_ENABLED;;
	}
	
	/**
	 * 
	 * Returns the related project's ID.
	 * 
	 * @return project's ID.
	 * 
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * 
	 * Checks if OntoUMLPlugin is enabled for the related project.
	 * 
	 * @return <code>true</code> if plugin is enabled.
	 * 
	 */
	public boolean isOntoUMLPluginEnabled() {
		return isOntoUMLPluginEnabled;
	}
	
	/**
	 * 
	 * Sets if OntoUMLPlugin is enabled for the related project.
	 * 
	 * @param isOntoUMLPluginEnabled
	 * 
	 */
	public void setOntoUMLPluginEnabled(boolean isOntoUMLPluginEnabled) {
		this.isOntoUMLPluginEnabled = isOntoUMLPluginEnabled;
	}
	
	/**
	 * 
	 * Returns OntoUML Server URL.
	 * 
	 * @return serverURL
	 * 
	 */
	public String getServerURL() {
		return serverURL;
	}
	
	/**
	 * 
	 * Sets OntoUML Server URL.
	 * 
	 * @param serverURL
	 * 
	 */
	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}
	
	/**
	 * 
	 * Checks if a custom server URL must be used.
	 * 
	 * @return <code>true</code> if plugin is enabled <b>and</b> a custom server is enabled.
	 * 
	 * @see <code>{@link #isOntoUMLPluginEnabled()}</code>
	 * 
	 */
	public boolean isCustomServerEnabled() {
		return isOntoUMLPluginEnabled() && isCustomServerEnabled;
	}
	
	/**
	 * 
	 * Sets if a custom server URL must be used.
	 * 
	 * @param isCustomServerEnabled
	 * 
	 */
	public void setCustomServerEnabled(boolean isCustomServerEnabled) {
		this.isCustomServerEnabled = isCustomServerEnabled;
	}
	
	/**
	 * 
	 * Returns automatic export folders path as a String.
	 * 
	 * @return exportFolderPath
	 * 
	 */
	public String getExportFolderPath() {
		return exportFolderPath;
	}
	

	/**
	 * 
	 * Sets automatic export folders path from a String.
	 * 
	 * @param exportFolderPath
	 * 
	 */
	public void setExportFolderPath(String exportFolderPath) {
		this.exportFolderPath = exportFolderPath;
	}
	
	
	/**
	 * 
	 * Returns automatic export filename as a String.
	 * 
	 * @return exportFileName
	 * 
	 */
	public String getExportFilename() {
		return exportFileName;
	}

	/**
	 * 
	 * Sets automatic export filename from a String.
	 * 
	 * @param exportFileName
	 * 
	 */
	public void setExportFilename(String exportFileName) {
		this.exportFileName = exportFileName;
	}
	
	public void setExportGUFOFolderPath(String exportFolderPath) {
		this.exportGUFOFolderPath = exportFolderPath;
	}
	
	public String getExportGUFOFolderPath() {
		return exportGUFOFolderPath;
	}

	public void setExportGUFOFilename(String exportFilename) {
		this.exportGUFOFileName = exportFilename;
	}
	
	public String getGUFOExportFilename() {
		return exportGUFOFileName;
	}
	
	
	/**
	 * 
	 * Checks if an export folder is set for automatic model export.
	 * 
	 * @return <code>true</code> if plugin is enabled <b>and</b> automatic model export is enabled.
	 * 
	 * @see <code>{@link #isOntoUMLPluginEnabled()}</code>
	 * 
	 */
	public boolean isModelExportEnabled() {
		return isOntoUMLPluginEnabled() && isModelExportEnabled;
	}
	
	/**
	 * 
	 * Sets if automatic model export is enabled.
	 * 
	 * param isModelExportEnabled
	 * 
	 */
	public void setModelExportEnabled(boolean isModelExportEnabled) {
		this.isModelExportEnabled = isModelExportEnabled;
	}
	
	/**
	 * 
	 * Checks if model elements should be automatically painted with the default color profile.
	 * 
	 * @return <code>true</code> if plugin is enabled <b>and</b> automatic model element coloring is enabled.
	 * 
	 * @see <code>{@link #isOntoUMLPluginEnabled()}</code>
	 * 
	 */
	public boolean isAutomaticColoringEnabled() {
		return isOntoUMLPluginEnabled() && isAutomaticColoringEnabled;
	}
	
	/**
	 * 
	 * Sets if model elements should be automatically painted with the default color profile.
	 * 
	 * @param isAutomaticColoringEnabled
	 * 
	 */
	public void setAutomaticColoringEnabled(boolean isAutomaticColoringEnabled) {
		this.isAutomaticColoringEnabled = isAutomaticColoringEnabled;
	}
	
	
}