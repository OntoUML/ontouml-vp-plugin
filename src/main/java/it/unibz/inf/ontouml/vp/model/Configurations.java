package it.unibz.inf.ontouml.vp.model;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
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
 * OntoUML Plugin configuration utility, allows persisting user preferences on the functionalities
 * enabled by the plugin.
 *
 * @author Claudenir Fonseca
 */
public class Configurations {

  private static final String CONFIG_FILE_NAME = ".ontouml.config.json";

  private static Configurations instance;

  @Expose() private List<ProjectConfigurations> projects;

  @Expose() private List<GitHubRelease> releases;

  @Expose() private GitHubRelease latestRelease;

  @Expose() private GitHubRelease latestAlphaRelease;

  @Expose() private GitHubRelease installedRelease;

  // Since Gson shows problems with the default serialization of ZonedDateTime we rely on strings
  @Expose() private String lastUpdatesCheck;

  private Configurations() {
    this.projects = new ArrayList<ProjectConfigurations>();
    this.releases = new ArrayList<>();
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

  public List<GitHubRelease> getReleases() {
    return releases;
  }

  public void setReleases(List<GitHubRelease> releases) {
    if (releases == null) {
      return;
    }

    this.releases = releases;

    this.releases.forEach(
        release -> {
          if (release.isPrerelease()) {
            latestAlphaRelease =
                latestAlphaRelease == null
                        || latestAlphaRelease.getCreatedAt() == null
                        || release.getCreatedAt().isAfter(latestAlphaRelease.getCreatedAt())
                    ? release
                    : latestAlphaRelease;
          } else {
            latestRelease =
                latestRelease == null
                        || latestRelease.getCreatedAt() == null
                        || release.getCreatedAt().isAfter(latestRelease.getCreatedAt())
                    ? release
                    : latestRelease;
          }

          installedRelease =
              release.getTagName().equals(OntoUMLPlugin.PLUGIN_VERSION_RELEASE)
                  ? release
                  : installedRelease;
        });
  }

  public ZonedDateTime getLastUpdatesCheck() {
    return ZonedDateTime.parse(lastUpdatesCheck);
  }

  public void setLastUpdatesCheck(ZonedDateTime lastUpdatesCheck) {
    this.lastUpdatesCheck = lastUpdatesCheck.toString();
  }

  public GitHubRelease getLatestRelease() {
    return latestRelease;
  }

  public GitHubRelease getLatestAlphaRelease() {
    return latestAlphaRelease;
  }

  public GitHubRelease getInstalledRelease() {
    return installedRelease;
  }

  /**
   * Returns singleton instance of the class.
   *
   * @return configurations
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
            application
                .getViewManager()
                .showMessage(
                    "Unable to load configuration file (" + OntoUMLPlugin.PLUGIN_NAME + ").\n");
          else if (e instanceof JsonSyntaxException)
            application
                .getViewManager()
                .showMessage(
                    "Configuration file ill-formed (" + OntoUMLPlugin.PLUGIN_NAME + ").\n");
          else
            application
                .getViewManager()
                .showMessage(
                    "Unknown error while reading configuration file ("
                        + OntoUMLPlugin.PLUGIN_NAME
                        + ").\n");

          e.printStackTrace();
        }
      }

      if (instance == null) instance = new Configurations();
    }

    return instance;
  }

  /** Persists user preferences on Visual Paradigm's workspace. */
  public void save() {
    final ApplicationManager application = ApplicationManager.instance();
    final File workspace = application.getWorkspaceLocation();
    final File configurationsFile = new File(workspace, CONFIG_FILE_NAME);
    final FileWriter fw;
    final GsonBuilder builder = new GsonBuilder();

    builder.registerTypeAdapter(
        ZonedDateTime.class,
        (JsonDeserializer<ZonedDateTime>)
            (json, typeOfT, context) ->
                ZonedDateTime.parse(json.getAsJsonPrimitive().getAsString()));

    builder.setPrettyPrinting();

    final Gson gson = builder.create();
    final String json = gson.toJson(this);

    try {
      if (!configurationsFile.exists()) {
        configurationsFile.createNewFile();
      }

      fw = new FileWriter(configurationsFile);
      fw.write(json);
      fw.close();
    } catch (IOException e) {
      application
          .getViewManager()
          .showMessage("Unable save " + OntoUMLPlugin.PLUGIN_NAME + " configurations.");
      e.printStackTrace();
    }
  }

  /**
   * Retrieves the configurations of the current project.
   *
   * @return current project's configurations.
   */
  public ProjectConfigurations getProjectConfigurations() {
    final IProject current = ApplicationManager.instance().getProjectManager().getProject();

    return getProjectConfigurations(current.getId());
  }

  /**
   * Retrieves the project configurations given the provided ID. If no previous configurations is
   * present, a new instance of <code>ProjectConfigurations</code> is returned with default
   * settings.
   *
   * @param projectId - Interest project's ID.
   * @return current project's configurations.
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
