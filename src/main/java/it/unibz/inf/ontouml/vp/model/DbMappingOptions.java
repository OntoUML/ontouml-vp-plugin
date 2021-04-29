package it.unibz.inf.ontouml.vp.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class DbMappingOptions {

  private MappingStrategy mappingStrategy;
  private DbmsSupported targetDBMS;
  private boolean isStandardizeNames;
  private String baseIri;
  private boolean generateSchema;
  private boolean generateConnection;
  private String hostName;
  private String databaseName;
  private String userConnection;
  private String passwordConnection;
  private boolean enumFieldToLookupTable;
  private JsonNode customElementMapping;

  public DbMappingOptions() {}

  public DbMappingOptions(ProjectConfigurations projectConfigurations) {

    this.mappingStrategy = projectConfigurations.getMappingStrategy();
    this.targetDBMS = projectConfigurations.getTargetDBMS();
    this.isStandardizeNames = projectConfigurations.isStandardizeNames();
    this.baseIri = projectConfigurations.getExportGUFOIRI();
    this.generateSchema = projectConfigurations.isGenerateSchema();
    this.generateConnection = projectConfigurations.isGenerateConnection();
    this.hostName = projectConfigurations.getHostNameConnection();
    this.databaseName = projectConfigurations.getDatabaseNameConnection();
    this.userConnection = projectConfigurations.getUserNameConnection();
    this.passwordConnection = projectConfigurations.getPassword();
    this.enumFieldToLookupTable = projectConfigurations.isEnumFieldToLookupTable();

    ObjectMapper mapper = new ObjectMapper();
    try {
      customElementMapping = mapper.readTree(projectConfigurations.getExportGUFOElementMapping());
    } catch (IOException e) {
      System.err.println("Unable to read project configurations.");
      e.printStackTrace();
    }
  }

  public MappingStrategy getMappingStrategy() {
    return mappingStrategy;
  }

  public void setMappingStrategy(MappingStrategy mappingStrategy) {
    this.mappingStrategy = mappingStrategy;
  }

  public DbmsSupported getTargetDBMS() {
    return targetDBMS;
  }

  public void setTargetDBMS(DbmsSupported targetDBMS) {
    this.targetDBMS = targetDBMS;
  }

  public boolean isStandardizeNames() {
    return isStandardizeNames;
  }

  public void setStandardizeNames(boolean isStandardizeNames) {
    this.isStandardizeNames = isStandardizeNames;
  }

  public String getBaseIri() {
    return baseIri;
  }

  public void setBaseIri(String baseIri) {
    this.baseIri = baseIri;
  }

  public boolean isGenerateSchema() {
    return generateSchema;
  }

  public void setGenerateSchema(boolean generateSchema) {
    this.generateSchema = generateSchema;
  }

  public boolean isGenerateConnection() {
    return generateConnection;
  }

  public void setGenerateConnection(boolean generateConnection) {
    this.generateConnection = generateConnection;
  }

  public String getHostName() {
    return hostName;
  }

  public void setHostName(String hostName) {
    this.hostName = hostName;
  }

  public String getDatabaseName() {
    return databaseName;
  }

  public void setDatabaseName(String databaseName) {
    this.databaseName = databaseName;
  }

  public String getUserConnection() {
    return userConnection;
  }

  public void setUserConnection(String userConnection) {
    this.userConnection = userConnection;
  }

  public String getPasswordConnection() {
    return passwordConnection;
  }

  public void setPasswordConnection(String passwordConnection) {
    this.passwordConnection = passwordConnection;
  }

  public boolean isEnumFieldToLookupTable() {
    return enumFieldToLookupTable;
  }

  public void setEnumFieldToLookupTable(boolean enumFieldToLookupTable) {
    this.enumFieldToLookupTable = enumFieldToLookupTable;
  }

  public JsonNode getCustomElementMapping() {
    return customElementMapping;
  }

  public void setCustomElementMapping(JsonNode customElementMapping) {
    this.customElementMapping = customElementMapping;
  }

  public String toJson() throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(this);
  }
}
