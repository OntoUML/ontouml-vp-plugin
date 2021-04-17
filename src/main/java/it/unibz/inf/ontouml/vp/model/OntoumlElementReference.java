package it.unibz.inf.ontouml.vp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OntoumlElementReference {

  private String id;
  private String type;

  public OntoumlElementReference() {
    this.id = null;
    this.type = null;
  }

  public OntoumlElementReference(String id, String type) {
    this.id = id;
    this.type = type;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
