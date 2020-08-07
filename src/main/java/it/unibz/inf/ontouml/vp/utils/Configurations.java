package it.unibz.inf.ontouml.vp.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IProject;

import it.unibz.inf.ontouml.vp.OntoUMLPlugin;

/**
 * 
 * OntoUML Plugin configuration utility, allows persisting user preferences on
 * the functionalities enabled by the plugin.
 * 
 * @author Claudenir Fonseca
 *
 */
public class Configurations {

	private static final String CONFIG_FILE_NAME = ".ontouml.config.json";

	private static Configurations instance;

	@SerializedName("projects")
	@Expose()
	private List<ProjectConfigurations> projects;

	@SerializedName("releases")
	@Expose()
	private JsonArray releases;

	@SerializedName("latestRelease")
	@Expose()
	private JsonObject latestRelease;

	@SerializedName("latestAlphaRelease")
	@Expose()
	private JsonObject latestAlphaRelease;

	@SerializedName("installedRelease")
	@Expose()
	private JsonObject installedRelease;

	@SerializedName("lastCheckForReleases")
	@Expose()
	private String lastUpdatesCheck;

	private Configurations() {
		this.projects = new ArrayList<ProjectConfigurations>();
		this.releases = new JsonArray();
		this.latestRelease = null;
		this.latestAlphaRelease = null;
		this.installedRelease = null;
		this.lastUpdatesCheck = ZonedDateTime.now().toString();
	}

	private List<ProjectConfigurations> getProjectConfigurationsList() {
		return projects;
	}

	private boolean addProjectConfigurations(ProjectConfigurations projectConfigurations) {
		return getProjectConfigurationsList().add(projectConfigurations);
	}

	public JsonArray getReleases() {
		return releases;
	}

	public void setReleases(JsonArray releases) {
		if (releases == null) {
			return;
		}

		this.releases = releases;

		final GitHubRelease latestGitHubRelease = getLatestGitHubRelease();
		final GitHubRelease latestAlphaGitHubRelease = getLatestAlphaGitHubRelease();

		this.releases.forEach(item -> {
			final GitHubRelease release = new GitHubRelease(item.getAsJsonObject());

			if (release.isPrerelease()) {
				latestAlphaRelease = latestAlphaGitHubRelease == null
						|| release.getCreatedAt().isAfter(latestAlphaGitHubRelease.getCreatedAt()) 
							? release.source : latestAlphaRelease;
			} else {
				latestRelease = latestGitHubRelease == null
						|| release.getCreatedAt().isAfter(latestGitHubRelease.getCreatedAt())
							? release.source : latestRelease;
			}

			installedRelease = release.getTagName().equals(OntoUMLPlugin.PLUGIN_VERSION_RELEASE) ? release.source
					: installedRelease;
		});
	}
	
	public String getLastUpdatesCheck() {
		return lastUpdatesCheck;
	}
	
	public void setLastUpdatesCheck(String lastUpdatesCheck) {
		this.lastUpdatesCheck = lastUpdatesCheck;
	}
	
	public List<GitHubRelease> getGitHubReleases() {
		List<GitHubRelease> list = new ArrayList<>();
		
		if(releases != null) {
			releases.forEach(release -> {
				if(release != null && release.isJsonObject()) {
					list.add(new GitHubRelease(release.getAsJsonObject()));
				}
			});
		}
		
		return list;
	}

	public GitHubRelease getLatestGitHubRelease() {
		return latestRelease != null ? new GitHubRelease(latestRelease) : null;
	}

	public GitHubRelease getLatestAlphaGitHubRelease() {
		return latestAlphaRelease != null ? new GitHubRelease(latestAlphaRelease) : null;
	}

	public GitHubRelease getInstalledGitHubRelease() {
		return installedRelease != null ? new GitHubRelease(installedRelease) : null;
	}

	/**
	 * 
	 * Returns singleton instance of the class.
	 * 
	 * @return configurations
	 * 
	 */
	public static Configurations getInstance() {
		if (instance == null) {
			final ApplicationManager application = ApplicationManager.instance();
			final File workspace = application.getWorkspaceLocation();
			final File configurationsFile = new File(workspace, CONFIG_FILE_NAME);

			if (configurationsFile.exists()) {
				String json = "";
				try {
					json = new String(Files.readAllBytes(configurationsFile.toPath()));
					Gson gson = new Gson();
					instance = gson.fromJson(json, Configurations.class);
				} catch (Exception e) {
					if (e instanceof IOException)
						application.getViewManager().showMessage(
								"Unable to load configuration file (" + OntoUMLPlugin.PLUGIN_NAME + ").\n");
					else if (e instanceof JsonSyntaxException)
						application.getViewManager()
								.showMessage("Configuration file ill-formed (" + OntoUMLPlugin.PLUGIN_NAME + ").\n");
					else
						application.getViewManager().showMessage("Unknown error while reading configuration file ("
								+ OntoUMLPlugin.PLUGIN_NAME + ").\n");

					e.printStackTrace();
				}
			}

			if (instance == null)
				instance = new Configurations();
		}

		return instance;
	}

	/**
	 * 
	 * Persists user preferences on Visual Paradigm's workspace.
	 * 
	 */
	public void save() {
		final ApplicationManager application = ApplicationManager.instance();
		final File workspace = application.getWorkspaceLocation();
		final File configurationsFile = new File(workspace, CONFIG_FILE_NAME);
		final FileWriter fw;
		final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
		final String json = gson.toJson(this);

		try {
			if (!configurationsFile.exists()) {
				configurationsFile.createNewFile();
			}

			fw = new FileWriter(configurationsFile);
			fw.write(json);
			fw.close();
		} catch (IOException e) {
			application.getViewManager().showMessage("Unable save " + OntoUMLPlugin.PLUGIN_NAME + " configurations.");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Retrieves the configurations of the current project.
	 * 
	 * @return current project's configurations.
	 * 
	 */
	public ProjectConfigurations getProjectConfigurations() {
		final IProject current = ApplicationManager.instance().getProjectManager().getProject();

		return getProjectConfigurations(current.getId());
	}

	/**
	 * 
	 * Retrieves the project configurations given the provided ID. If no previous
	 * configurations is present, a new instance of
	 * <code>ProjectConfigurations</code> is returned with default settings.
	 * 
	 * @param projectId - Interest project's ID.
	 * 
	 * @return current project's configurations.
	 * 
	 * 
	 */
	public ProjectConfigurations getProjectConfigurations(String projectId) {
		for (ProjectConfigurations projectConfigurations : getProjectConfigurationsList()) {
			if (projectConfigurations.getId().equals(projectId)) {
				return projectConfigurations;
			}
		}

		ProjectConfigurations projectConfigurations = new ProjectConfigurations(projectId);
		addProjectConfigurations(projectConfigurations);

		return projectConfigurations;
	}

}
