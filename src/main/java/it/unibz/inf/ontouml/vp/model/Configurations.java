package it.unibz.inf.ontouml.vp.model;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IProject;
import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.utils.TextFiles;
import java.io.File;
import java.io.IOException;
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

  @Expose() private LlmConfigurations llm;

  private Configurations() {
    this.projects = new ArrayList<ProjectConfigurations>();
    this.releases = new ArrayList<>();
    this.latestRelease = null;
    this.latestAlphaRelease = null;
    this.installedRelease = null;
    this.lastUpdatesCheck = ZonedDateTime.now().toString();
    this.llm = new LlmConfigurations();
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
   * Returns the LLM metaclass suggestion configurations, creating default ones if the
   * workspace configuration file predates this feature.
   *
   * @return llm configurations
   */
  public LlmConfigurations getLlmConfigurations() {
    if (llm == null) {
      llm = new LlmConfigurations();
    }

    return llm;
  }

  public static synchronized Configurations getInstance() {
    if (instance == null) {
      instance = load();
    }

    return instance;
  }

  /**
   * Reads the settings file, or starts from the defaults when there is none.
   *
   * <p>A file that cannot be read is not thrown away: it holds API keys and the user's own
   * prompts, and the very next save would overwrite it with the defaults. A copy is kept beside
   * it first, and the user is told where.
   */
  private static Configurations load() {
    final ApplicationManager application = ApplicationManager.instance();
    final File configurationsFile = new File(application.getWorkspaceLocation(), CONFIG_FILE_NAME);

    if (!configurationsFile.exists()) {
      return new Configurations();
    }

    try {
      final String json = TextFiles.readUtf8OrLegacy(configurationsFile.toPath());
      final Configurations loaded = json.trim().isEmpty() ? null : new Gson().fromJson(json, Configurations.class);

      return loaded == null ? new Configurations() : loaded;
    } catch (Exception e) {
      final String problem =
          e instanceof IOException
              ? "Unable to load configuration file"
              : e instanceof JsonSyntaxException
                  ? "Configuration file ill-formed"
                  : "Unknown error while reading configuration file";

      application
          .getViewManager()
          .showMessage(
              problem
                  + " ("
                  + OntoUMLPlugin.PLUGIN_NAME
                  + "). Default settings are in use. "
                  + keepACopyOf(configurationsFile)
                  + "\n");

      e.printStackTrace();

      return new Configurations();
    }
  }

  private static String keepACopyOf(File file) {
    try {
      return "The original was kept as " + TextFiles.backUp(file.toPath()).getFileName() + ".";
    } catch (IOException e) {
      return "It could not be copied (" + e.getMessage() + "), so saving will overwrite it.";
    }
  }

  /**
   * Persists user preferences on Visual Paradigm's workspace. The file is written beside the old one
   * and swapped in, so a failure midway leaves the previous settings untouched.
   */
  public synchronized void save() {
    final ApplicationManager application = ApplicationManager.instance();
    final File workspace = application.getWorkspaceLocation();
    final File configurationsFile = new File(workspace, CONFIG_FILE_NAME);
    final GsonBuilder builder = new GsonBuilder();

    builder.registerTypeAdapter(
        ZonedDateTime.class,
        (JsonDeserializer<ZonedDateTime>)
            (json, typeOfT, context) ->
                ZonedDateTime.parse(json.getAsJsonPrimitive().getAsString()));

    builder.setPrettyPrinting();

    final String json = builder.create().toJson(this);

    try {
      TextFiles.writeAtomically(configurationsFile.toPath(), json);
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
