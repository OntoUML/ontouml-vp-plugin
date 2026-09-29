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
  public static final String NODE_SYSTEM_PROMPT_FILENAME = "node_system_prompt.md";
  public static final String NODE_PROMPT_FILENAME = "ufo_node_prompt.md";
  public static final String DECISION_TREE_FILENAME = "ufo_decision_tree.json";
  public static final String DOCUMENTATION_SYSTEM_PROMPT_FILENAME = "documentation_system_prompt.md";
  public static final String DOCUMENTATION_PROMPT_FILENAME = "documentation_prompt.md";
  public static final String DOCUMENTATION_TEMPLATE_FILENAME = "documentation_template.md";
  public static final String SOURCE_SUMMARY_PROMPT_FILENAME = "source_summary_prompt.md";

  private static final Map<String, String> bundledCache = new HashMap<>();

  /**
   * The active configuration, or a fresh (no-override) one when there is no Visual Paradigm host to
   * read it from — a plain unit test, most likely, exercising a controller method that reads a
   * prompt through this class along the way. Every override this returns "none" for is one that
   * production, running inside Visual Paradigm, would have found.
   */
  private static LlmConfigurations config() {
    try {
      return Configurations.getInstance().getLlmConfigurations();
    } catch (RuntimeException | NoClassDefFoundError e) {
      return new LlmConfigurations();
    }
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

  /**
   * The system message sent alongside the per-question user prompt ({@link #getNodePrompt()}) in
   * the detailed evaluation. Short and generic on purpose: the instructions that actually vary from
   * question to question live in the user prompt, not here.
   */
  public static String getNodeSystemPrompt() {
    return orDefault(config().getCustomNodeSystemPrompt(), NODE_SYSTEM_PROMPT_FILENAME);
  }

  /** The system message sent alongside the documentation prompt when writing a class's documentation. */
  public static String getDocumentationSystemPrompt() {
    return orDefault(
        config().getCustomDocumentationSystemPrompt(), DOCUMENTATION_SYSTEM_PROMPT_FILENAME);
  }

  /**
   * The system message sent when condensing a source cited in the model — one call per source (or
   * per part of a long one) while reading the sources cited in the model, before documenting a
   * class; see {@link it.unibz.inf.ontouml.vp.utils.SourceSummarizer}.
   */
  public static String getSourceSummaryPrompt() {
    return orDefault(config().getCustomSourceSummaryPrompt(), SOURCE_SUMMARY_PROMPT_FILENAME);
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

  public static String getDefaultNodeSystemPrompt() {
    return readBundled(NODE_SYSTEM_PROMPT_FILENAME);
  }

  public static String getDefaultDocumentationSystemPrompt() {
    return readBundled(DOCUMENTATION_SYSTEM_PROMPT_FILENAME);
  }

  public static String getDefaultSourceSummaryPrompt() {
    return readBundled(SOURCE_SUMMARY_PROMPT_FILENAME);
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
    } catch (RuntimeException | NoClassDefFoundError e) {
      // no Visual Paradigm host to ask for the plugin directory — a unit test, most likely
    }

    bundledCache.put(filename, content);

    return content;
  }
}
