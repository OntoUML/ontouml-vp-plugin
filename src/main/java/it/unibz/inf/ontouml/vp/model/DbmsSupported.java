package it.unibz.inf.ontouml.vp.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DbmsSupported {
  GENERIC_SCHEMA("Generic Schema"),
  MYSQL("MySql"),
  H2("H2"),
  SQLSERVER("SqlServer"),
  ORACLE("Oracle"),
  POSTGREE("Postgree");

  private final String display;

  private DbmsSupported(String s) {
    display = s;
  }

  @JsonValue
  @Override
  public String toString() {
    return display;
  }
}
