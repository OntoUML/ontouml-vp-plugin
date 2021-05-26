package it.unibz.inf.ontouml.vp.model;

public class Ontouml2DbResult {

  private String schema;
  private String obda;
  private String connection;

  Ontouml2DbResult() {
    this.schema = null;
    this.obda = null;
    this.connection = null;
  }

  Ontouml2DbResult(String schema, String odba, String connection) {
    this.schema = schema;
    this.obda = odba;
    this.connection = connection;
  }

  public String getSchema() {
    return schema;
  }

  public void setSchema(String schema) {
    this.schema = schema;
  }

  public String getObda() {
    return obda;
  }

  public void setObda(String obda) {
    this.obda = obda;
  }

  public String getConnection() {
    return connection;
  }

  public void setConnection(String connection) {
    this.connection = connection;
  }
}
