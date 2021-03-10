package it.unibz.inf.ontouml.vp.model;

import static com.google.common.truth.Truth.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class VerificationServiceResultDeserializationTest {

  static ObjectMapper mapper;

  @BeforeAll
  static void setUp() throws IOException {
    mapper = new ObjectMapper();
  }

  @Test
  void shouldDeserializeEmptyResult() throws IOException {
    final String emptyResult1 = "{\n" + "  \"result\": [],\n" + "  \"issues\": []\n" + "}";
    final String emptyResult2 = "{\n" + "  \"result\": null,\n" + "  \"issues\": null\n" + "}";
    final String emptyResult3 = "{\n" + "  \"result\": []\n" + "}";
    final String emptyResult4 = "{\n" + "  \"issues\": []\n" + "}";
    final List<String> emptyResults =
        Arrays.asList(emptyResult1, emptyResult2, emptyResult3, emptyResult4);

    for (String emptyResult : emptyResults) {
      VerificationServiceResult serviceResult =
          mapper.readValue(emptyResult, VerificationServiceResult.class);

      assertThat(serviceResult).isNotEqualTo(null);

      assertThat(serviceResult.getResult()).isEmpty();
      assertThat(serviceResult.getIssues()).isEmpty();
    }
  }

  @Test
  void shouldDeserializeVerificationIssues() throws IOException {
    final String json =
        "{\n"
            + "  \"result\": [\n"
            + "    {\n"
            + "      \"code\": \"class_invalid_ontouml_stereotype\",\n"
            + "      \"title\": \"No valid OntoUML stereotype\",\n"
            + "      \"description\": \"The class Class must have a unique OntoUML stereotype.\",\n"
            + "      \"severity\": \"error\",\n"
            + "      \"data\": {\n"
            + "        \"source\": {\n"
            + "          \"stereotype\": \"kindy\",\n"
            + "          \"restrictedTo\": [\n"
            + "            \"functional-complex\"\n"
            + "          ],\n"
            + "          \"properties\": null,\n"
            + "          \"literals\": null,\n"
            + "          \"isAbstract\": false,\n"
            + "          \"isDerived\": false,\n"
            + "          \"isExtensional\": false,\n"
            + "          \"isPowertype\": false,\n"
            + "          \"order\": \"1\",\n"
            + "          \"propertyAssignments\": {},\n"
            + "          \"type\": \"Class\",\n"
            + "          \"id\": \"leM15.6GAqACHQWX\",\n"
            + "          \"name\": \"Class\",\n"
            + "          \"description\": null\n"
            + "        },\n"
            + "        \"context\": []\n"
            + "      }\n"
            + "    }\n"
            + "  ]\n"
            + "}";
    final VerificationServiceResult serviceResult =
        mapper.readValue(json, VerificationServiceResult.class);

    assertThat(serviceResult).isNotEqualTo(null);
    assertThat(serviceResult.getResult()).isNotEmpty();
    assertThat(serviceResult.getResult().get(0)).isInstanceOf(ServiceIssue.class);

    final ServiceIssue issue = serviceResult.getResult().get(0);
    assertThat(issue.getId()).isNull();
    assertThat(issue.getCode()).isEqualTo("class_invalid_ontouml_stereotype");
    assertThat(issue.getTitle()).isEqualTo("No valid OntoUML stereotype");
    assertThat(issue.getDescription())
        .isEqualTo("The class Class must have a unique OntoUML stereotype.");
    assertThat(issue.getSeverity()).isEqualTo("error");
    assertThat(issue.getSource().getId()).isEqualTo("leM15.6GAqACHQWX");
    assertThat(issue.getContext()).isEmpty();
  }
}
