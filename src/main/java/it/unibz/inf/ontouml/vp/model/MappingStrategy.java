/** Author: Gustavo Ludovico Guidoni */
package it.unibz.inf.ontouml.vp.model;

public enum MappingStrategy {
  ONE_TABLE_PER_CLASS("One Table per Class"),
  ONE_TABLE_PER_KIND("One Table per Kind");

  private final String display;

  private MappingStrategy(String s) {
    display = s;
  }

  @Override
  public String toString() {
    return display;
  }
}
