package it.unibz.inf.ontouml.vp.utils;

import com.google.gson.Gson;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.VPPluginInfo;
import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.DecisionTree;
import it.unibz.inf.ontouml.vp.model.LlmConfigurations;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Resolves the texts that drive the AI features: the guide used by the quick classification, the
 * per-question prompt used by the detailed one, and the decision tree itself.
 *
 * <p>Each of them ships with the plugin and can be overridden by the user in the settings; an empty
 * override means "use the bundled default", which is what the "Restore defaults" button leaves
 * behind. Bundled files live in the {@code prompts/} folder (see its README) and are cached since
 * they cannot change while the plugin runs; overrides are read from the configuration on every call
 * so an edit takes effect immediately.
 */
public class LlmPromptResources {

  /** Folder, inside the plugin directory, that holds every bundled prompt and template. */
  public static final String PROMPTS_DIRECTORY = "prompts";

  public static final String SYSTEM_PROMPT_FILENAME = "ufo_decision_tree_prompt.md";
  public static final String NODE_PROMPT_FILENAME = "ufo_node_prompt.md";
  public static final String DECISION_TREE_FILENAME = "ufo_decision_tree.json";
  public static final String DOCUMENTATION_PROMPT_FILENAME = "documentation_prompt.md";
  public static final String DOCUMENTATION_TEMPLATE_FILENAME = "documentation_template.md";

  private static final Map<String, String> bundledCache = new HashMap<>();

  private static LlmConfigurations config() {
    return Configurations.getInstance().getLlmConfigurations();
  }

  private static String orDefault(String custom, String filename) {
    return custom == null || custom.trim().isEmpty() ? readBundled(filename) : custom;
  }

  public static String getSystemPrompt() {
    return orDefault(config().getCustomSystemPrompt(), SYSTEM_PROMPT_FILENAME);
  }

  public static String getNodePrompt() {
    return orDefault(config().getCustomNodePrompt(), NODE_PROMPT_FILENAME);
  }

  public static String getDecisionTreeJson() {
    return orDefault(config().getCustomDecisionTree(), DECISION_TREE_FILENAME);
  }

  public static String getDocumentationPrompt() {
    return orDefault(config().getCustomDocumentationPrompt(), DOCUMENTATION_PROMPT_FILENAME);
  }

  public static String getDocumentationTemplate() {
    return orDefault(config().getCustomDocumentationTemplate(), DOCUMENTATION_TEMPLATE_FILENAME);
  }

  public static String getDefaultDocumentationPrompt() {
    return readBundled(DOCUMENTATION_PROMPT_FILENAME);
  }

  public static String getDefaultDocumentationTemplate() {
    return readBundled(DOCUMENTATION_TEMPLATE_FILENAME);
  }

  public static String getDefaultSystemPrompt() {
    return readBundled(SYSTEM_PROMPT_FILENAME);
  }

  public static String getDefaultNodePrompt() {
    return readBundled(NODE_PROMPT_FILENAME);
  }

  public static String getDefaultDecisionTreeJson() {
    return readBundled(DECISION_TREE_FILENAME);
  }

  /**
   * The decision tree currently in force, or null when it cannot be parsed — which is possible
   * once the user is allowed to edit it, so callers must handle it.
   */
  public static DecisionTree getDecisionTree() {
    try {
      final DecisionTree tree = new Gson().fromJson(getDecisionTreeJson(), DecisionTree.class);
      return tree != null && tree.isUsable() ? tree : null;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private static synchronized String readBundled(String filename) {
    if (bundledCache.containsKey(filename)) {
      return bundledCache.get(filename);
    }

    String content = "";

    try {
      final VPPluginInfo pluginInfo =
          ApplicationManager.instance().getPluginInfo(OntoUMLPlugin.PLUGIN_ID);
      final File promptsDirectory = new File(pluginInfo.getPluginDir(), PROMPTS_DIRECTORY);
      content = TextFiles.readUtf8(new File(promptsDirectory, filename).toPath());
    } catch (IOException e) {
      e.printStackTrace();
    }

    bundledCache.put(filename, content);

    return content;
  }
}
