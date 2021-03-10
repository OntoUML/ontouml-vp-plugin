package it.unibz.inf.ontouml.vp.model;

import static com.google.common.truth.Truth.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GufoTransformationServiceResultDeserializationTest {

  static ObjectMapper mapper;

  @BeforeAll
  static void setUp() throws IOException {
    mapper = new ObjectMapper();
  }

  @Test
  void shouldDeserializeEmptyResult() throws IOException {
    final String emptyResult1 = "{\n" + "  \"result\": null,\n" + "  \"issues\": []\n" + "}";
    final String emptyResult2 = "{\n" + "  \"result\": null,\n" + "  \"issues\": null\n" + "}";
    final String emptyResult3 = "{\n" + "  \"result\": null\n" + "}";
    final List<String> emptyResults = Arrays.asList(emptyResult1, emptyResult2, emptyResult3);

    for (String emptyResult : emptyResults) {
      GufoTransformationServiceResult serviceResult =
          mapper.readValue(emptyResult, GufoTransformationServiceResult.class);

      assertThat(serviceResult).isNotEqualTo(null);
      assertThat(serviceResult.getResult()).isNull();
      assertThat(serviceResult.getIssues()).isEmpty();
    }
  }

  @Test
  void shouldDeserializeGufoOntology() throws IOException {
    final String json =
        "{\n"
            + "  \"result\": \"@prefix : <https://example.com#>.\\n"
            + "@prefix gufo: <http://purl.org/nemo/gufo#>.\\n"
            + "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.\\n"
            + "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.\\n"
            + "@prefix owl: <http://www.w3.org/2002/07/owl#>.\\n"
            + "@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.\\n"
            + "\\n"
            + "<https://example.com> rdf:type owl:Ontology;\\n"
            + "    owl:imports gufo:.\\n"
            + ":Class rdf:type owl:Class, gufo:Kind, owl:NamedIndividual;\\n"
            + "    rdfs:subClassOf gufo:FunctionalComplex;\\n"
            + "    rdfs:label \\\"Class\\\"@en.\\n"
            + ":Class2 rdf:type owl:Class, gufo:SubKind, owl:NamedIndividual;\\n"
            + "    rdfs:label \\\"Class2\\\"@en.\\n"
            + ":Class3 rdf:type owl:Class, gufo:Kind, owl:NamedIndividual;\\n"
            + "    rdfs:subClassOf gufo:Relator;\\n"
            + "    rdfs:label \\\"Class3\\\"@en.\\n"
            + ":Class2 rdfs:subClassOf :Class.\\n"
            + ":classMediatesClass3 rdf:type owl:ObjectProperty;\\n"
            + "    rdfs:domain :Class;\\n"
            + "    rdfs:range :Class3;\\n"
            + "    rdfs:subPropertyOf gufo:mediates.\\n"
            + ":Class rdfs:subClassOf [\\n"
            + "  rdf:type owl:Restriction;\\n"
            + "  owl:onProperty :classMediatesClass3;\\n"
            + "  owl:minQualifiedCardinality \\\"1\\\"^^xsd:nonNegativeInteger;\\n"
            + "  owl:onClass :Class3\\n"
            + "].\\n"
            + "\",\n"
            + "  \"issues\": [\n"
            + "    {\n"
            + "      \"id\": \"3yjrith5k0vkm3pi9zs\",\n"
            + "      \"code\": \"missing_relation_name\",\n"
            + "      \"title\": \"Missing relation name\",\n"
            + "      \"description\": \"Missing name on «mediation» relation between classes"
            + " \\\"[object Object]\\\" and \\\"[object Object]\\\".\",\n"
            + "      \"severity\": \"warning\",\n"
            + "      \"data\": {\n"
            + "        \"element\": {\n"
            + "          \"id\": \"7PKr2.6GAqACHQXQ\",\n"
            + "          \"name\": null\n"
            + "        }\n"
            + "      }\n"
            + "    }\n"
            + "  ]\n"
            + "}";
    final GufoTransformationServiceResult serviceResult =
        mapper.readValue(json, GufoTransformationServiceResult.class);

    assertThat(serviceResult).isNotNull();
    assertThat(serviceResult.getResult()).isInstanceOf(String.class);
    assertThat(serviceResult.getIssues()).isNotEmpty();
    assertThat(serviceResult.getIssues().get(0)).isInstanceOf(ServiceIssue.class);

    final ServiceIssue issue = serviceResult.getIssues().get(0);
    assertThat(issue.getId()).isEqualTo("3yjrith5k0vkm3pi9zs");
    assertThat(issue.getCode()).isEqualTo("missing_relation_name");
    assertThat(issue.getTitle()).isEqualTo("Missing relation name");
    assertThat(issue.getDescription())
        .isEqualTo(
            "Missing name on «mediation» relation between classes \"[object Object]\" and"
                + " \"[object Object]\".");
    assertThat(issue.getSeverity()).isEqualTo("warning");
    assertThat(issue.getSource()).isNull();
    assertThat(issue.getContext()).isEmpty();
  }
}
