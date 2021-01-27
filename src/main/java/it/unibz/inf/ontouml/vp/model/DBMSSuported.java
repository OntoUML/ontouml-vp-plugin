package it.unibz.inf.ontouml.vp.model;

public enum DBMSSuported {
  GENERIC_SCHEMA("Generic Schema"),
  MYSQL("MySql"),
  H2("H2"),
  SQLSERVER("SqlServer"),
  ORACLE("Oracle"),
  POSTGREE("Postgree");

  private final String display;

  private DBMSSuported(String s) {
    display = s;
  }

  @Override
  public String toString() {
    return display;
  }
}
