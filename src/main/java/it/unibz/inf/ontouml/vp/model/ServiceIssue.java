package it.unibz.inf.ontouml.vp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = ServiceIssueDeserializer.class)
public class ServiceIssue {

  private String id;
  private String code;
  private String severity;
  private String title;
  private String description;
  private OntoumlElementReference source;
  private List<OntoumlElementReference> context;

  public ServiceIssue() {
    this.id = null;
    this.code = null;
    this.severity = null;
    this.title = null;
    this.description = null;
    this.source = null;
    this.context = new ArrayList<>();
  }

  public ServiceIssue(
      String id,
      String code,
      String severity,
      String title,
      String description,
      OntoumlElementReference source,
      List<OntoumlElementReference> context) {
    this.id = id;
    this.code = code;
    this.severity = severity;
    this.title = title;
    this.description = description;
    this.source = source;
    this.context = context;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getSeverity() {
    return severity;
  }

  public void setSeverity(String severity) {
    this.severity = severity;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public OntoumlElementReference getSource() {
    return source;
  }

  public void setSource(OntoumlElementReference source) {
    this.source = source;
  }

  public List<OntoumlElementReference> getContext() {
    return context;
  }

  public void setContext(List<OntoumlElementReference> context) {
    this.context = context != null ? context : new ArrayList<>();
  }
}
