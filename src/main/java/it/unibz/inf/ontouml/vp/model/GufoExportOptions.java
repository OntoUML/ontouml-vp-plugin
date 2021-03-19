package it.unibz.inf.ontouml.vp.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class GufoExportOptions {

  private String baseIRI;
  private String format;
  private String uriFormatBy;
  private boolean createInverses;
  private boolean createObjectProperty;
  private boolean preAnalysis;
  private boolean prefixPackages;
  private JsonNode customElementMapping;
  private JsonNode customPackageMapping;

  public GufoExportOptions() {}

  public GufoExportOptions(ProjectConfigurations projectConfigurations) {
    baseIRI = projectConfigurations.getExportGUFOIRI();
    format = projectConfigurations.getExportGUFOFormat();
    uriFormatBy = projectConfigurations.getExportGUFOURIFormat();
    createInverses = projectConfigurations.getExportGUFOInverseBox();
    createObjectProperty = projectConfigurations.getExportGUFOObjectBox();
    preAnalysis = projectConfigurations.getExportGUFOAnalysisBox();
    prefixPackages = projectConfigurations.getExportGUFOPackagesBox();

    ObjectMapper mapper = new ObjectMapper();
    try {
      customElementMapping = mapper.readTree(projectConfigurations.getExportGUFOElementMapping());
      customPackageMapping = mapper.readTree(projectConfigurations.getExportGUFOPackageMapping());
    } catch (IOException e) {
      System.err.println("Unable to read project configurations.");
      e.printStackTrace();
    }
  }

  public String getBaseIRI() {
    return baseIRI;
  }

  public void setBaseIRI(String baseIRI) {
    this.baseIRI = baseIRI;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public String getUriFormatBy() {
    return uriFormatBy;
  }

  public void setUriFormatBy(String uriFormatBy) {
    this.uriFormatBy = uriFormatBy;
  }

  public boolean isCreateInverses() {
    return createInverses;
  }

  public void setCreateInverses(boolean createInverses) {
    this.createInverses = createInverses;
  }

  public boolean isCreateObjectProperty() {
    return createObjectProperty;
  }

  public void setCreateObjectProperty(boolean createObjectProperty) {
    this.createObjectProperty = createObjectProperty;
  }

  public boolean isPreAnalysis() {
    return preAnalysis;
  }

  public void setPreAnalysis(boolean preAnalysis) {
    this.preAnalysis = preAnalysis;
  }

  public boolean isPrefixPackages() {
    return prefixPackages;
  }

  public void setPrefixPackages(boolean prefixPackages) {
    this.prefixPackages = prefixPackages;
  }

  public JsonNode getCustomElementMapping() {
    return customElementMapping;
  }

  public void setCustomElementMapping(JsonNode customElementMapping) {
    this.customElementMapping = customElementMapping;
  }

  public JsonNode getCustomPackageMapping() {
    return customPackageMapping;
  }

  public void setCustomPackageMapping(JsonNode customPackageMapping) {
    this.customPackageMapping = customPackageMapping;
  }

  public String toJson() throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(this);
  }
}
