package it.unibz.inf.ontouml.vp.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IProject;
import it.unibz.inf.ontouml.vp.OntoUMLPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

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
	
	@SerializedName("lastCheckForReleases")
	@Expose()
	private ZonedDateTime lastCheckForReleases;

	private Configurations() {
		this.projects = new ArrayList<ProjectConfigurations>();
		this.releases = new JsonArray();
		this.latestRelease = null;
		this.latestAlphaRelease = null;
		this.lastCheckForReleases = ZonedDateTime.now();
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

		final JsonElement latestRelease_created_at = latestRelease != null
				? this.latestRelease.getAsJsonObject().get(GitHubUtils.PROP_CREATED_AT)
				: null;
		final JsonElement latestAlphaRelease_created_at = latestAlphaRelease != null
				? this.latestAlphaRelease.getAsJsonObject().get(GitHubUtils.PROP_CREATED_AT)
				: null;

		final ZonedDateTime latestReleaseCreation = latestRelease_created_at != null
				? ZonedDateTime.parse(latestRelease_created_at.getAsString())
				: null;
		final ZonedDateTime latestAlphaReleaseCreation = latestAlphaRelease_created_at != null
				? ZonedDateTime.parse(latestAlphaRelease_created_at.getAsString())
				: null;

		this.releases.forEach(item -> {
			final JsonObject release = item.getAsJsonObject();
			final JsonElement created_at = release.get(GitHubUtils.PROP_CREATED_AT);
			final JsonElement prerelease = release.get(GitHubUtils.PROP_PRERELEASE);

			if (created_at != null && created_at.isJsonPrimitive()) {
				ZonedDateTime releaseCreation = ZonedDateTime.parse(created_at.getAsString());

				if (prerelease.getAsBoolean()) {
					latestAlphaRelease = latestAlphaReleaseCreation == null
							|| releaseCreation.isAfter(latestAlphaReleaseCreation) ? release : latestAlphaRelease;
				} else {
					latestRelease = latestReleaseCreation == null || releaseCreation.isAfter(latestReleaseCreation)
							? release
							: latestRelease;
				}

			}
		});
	}

	public JsonObject getLatestRelease() {
		return this.latestRelease;
	}
	
	public JsonObject getLatestAlphaRelease() {
		return this.latestRelease;
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
