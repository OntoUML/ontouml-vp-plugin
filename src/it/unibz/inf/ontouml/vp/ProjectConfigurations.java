package it.unibz.inf.ontouml.vp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.IProject;

public class ProjectConfigurations {
	
	private IProject source;
	
	@SerializedName("projectId")
	@Expose()
	private String id;
	
	@SerializedName("isOntoUMLPluginEnabled")
	@Expose()
	private boolean isOntoUMLPluginEnabled;

	@SerializedName("serverURL")
	@Expose()
	private String serverURL = OntoUMLPluginForVP.DEFAULT_SERVER_URL;
	
	@SerializedName("isCustomServerEnabled")
	@Expose()
	private boolean isCustomServerEnabled;
	
	@SerializedName("exportFolderPath")
	@Expose()
	private String exportFolderPath;
	
	@SerializedName("isAutomaticExportEnabled")
	@Expose()
	private boolean isAutomaticExportEnabled;
	
	@SerializedName("isAutomaticColoringEnabled")
	@Expose()
	private boolean isAutomaticColoringEnabled;
	
	public ProjectConfigurations(IProject source) {
		this.source = source;
		this.id = source.getId();
		this.resetDefaults();
	}
	
	public void resetDefaults() {
		this.isOntoUMLPluginEnabled = false;
		
		this.isCustomServerEnabled = false;
		this.serverURL = OntoUMLPluginForVP.DEFAULT_SERVER_URL;
		
		this.isAutomaticExportEnabled = false;
		this.exportFolderPath = "";
		
		this.isAutomaticColoringEnabled = false;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String projectId) {
		this.id = projectId;
	}
	
	public boolean isOntoUMLPluginEnabled() {
		return isCustomServerEnabled;
	}
	
	public void setOntoUMLPluginEnabled(boolean isOntoUMLPluginEnabled) {
		this.isOntoUMLPluginEnabled = isOntoUMLPluginEnabled;
	}
	
	public String getServerURL() {
		return serverURL;
	}
	
	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}
	
	public boolean isCustomServerEnabled() {
		return isOntoUMLPluginEnabled() && isCustomServerEnabled;
	}
	
	public void setCustomServerEnabled(boolean isCustomServerEnabled) {
		this.isCustomServerEnabled = isCustomServerEnabled;
	}
	
	public String getExportFolderPath() {
		return exportFolderPath;
	}
	
	public void setExportFolderPath(String exportFolderPath) {
		this.exportFolderPath = exportFolderPath;
	}
	
	public boolean isAutomaticExportEnabled() {
		return isOntoUMLPluginEnabled() && isAutomaticExportEnabled;
	}
	
	public void setAutomaticExportEnabled(boolean isAutomaticExportEnabled) {
		this.isAutomaticExportEnabled = isAutomaticExportEnabled;
	}
	
	public boolean isAutomaticColoringEnabled() {
		return isOntoUMLPluginEnabled() && isAutomaticColoringEnabled;
	}
	
	public void setAutomaticColoringEnabled(boolean isAutomaticColoringEnabled) {
		this.isAutomaticColoringEnabled = isAutomaticColoringEnabled;
	}
	
	
}