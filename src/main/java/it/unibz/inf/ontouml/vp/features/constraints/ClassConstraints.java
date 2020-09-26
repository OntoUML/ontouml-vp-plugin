package it.unibz.inf.ontouml.vp.features.constraints;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.VPPluginInfo;
import it.unibz.inf.ontouml.vp.OntoUMLPlugin;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClassConstraints {

  private static final Type mapOfListsType =
      new TypeToken<Map<String, List<String>>>() {}.getType();
  private static String filename = "generalization_constraints.json";
  private static String filepath;

  private static JsonObject constraints;
  private static Map<String, List<String>> allowedSuperClassesFor, allowedSubClassesFor;

  private static void loadConstraints() {
    try {
      if (filepath == null) {
        final ApplicationManager app = ApplicationManager.instance();
        final VPPluginInfo[] plugins = app.getPluginInfos();

        for (VPPluginInfo plugin : plugins) {
          if (plugin.getPluginId().equals(OntoUMLPlugin.PLUGIN_ID)) {
            File pluginDir = plugin.getPluginDir();
            filepath = pluginDir.getAbsolutePath() + File.separator + filename;
          }
        }
      }

      final JsonParser parser = new JsonParser();
      final JsonElement element = parser.parse(new FileReader(filepath));
      constraints = element.getAsJsonObject();

      final Gson gson = new Gson();
      allowedSubClassesFor =
          gson.fromJson(constraints.getAsJsonObject("allowedSubClassesFor"), mapOfListsType);
      allowedSuperClassesFor =
          gson.fromJson(constraints.getAsJsonObject("allowedSuperClassesFor"), mapOfListsType);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public static List<String> getAllowedActionIDsOnGeneral(String stereotype) {
    if (constraints == null) {
      loadConstraints();
    }

    List<String> allowedStereotypes = allowedSubClassesFor.get(stereotype);

    if (allowedStereotypes == null) {
      allowedStereotypes = new ArrayList<String>();
    }

    return allowedStereotypes.stream()
        .map(allowed -> ActionIds.classStereotypeToActionID(allowed))
        .collect(Collectors.toList());
  }

  public static List<String> getAllowedActionIDsOnSpecific(String stereotype) {
    if (constraints == null) {
      loadConstraints();
    }

    List<String> allowedStereotypes = allowedSuperClassesFor.get(stereotype);

    if (allowedStereotypes == null) {
      allowedStereotypes = new ArrayList<String>();
    }

    return allowedStereotypes.stream()
        .map(allowed -> ActionIds.classStereotypeToActionID(allowed))
        .collect(Collectors.toList());
  }
}
