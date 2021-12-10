package it.unibz.inf.ontouml.vp.utils;

import it.unibz.inf.ontouml.vp.model.Configurations;

public class ConfigurationsUtils {

  // TODO: check whether it makes more sense to send this methods to the Configurations class or to
  // keeping using the old style instead

  public static boolean isAutomaticColoringEnabled() {
    return Configurations.getInstance().getProjectConfigurations().isAutomaticColoringEnabled();
  }

  public static boolean isSmartModelingEnabled() {
    return Configurations.getInstance().getProjectConfigurations().isAutomaticColoringEnabled();
  }
}
