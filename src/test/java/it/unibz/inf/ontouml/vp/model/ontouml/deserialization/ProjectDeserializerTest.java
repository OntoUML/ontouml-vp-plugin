package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.unibz.inf.ontouml.vp.model.ontouml.*;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Package;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ProjectDeserializerTest {

  static String json =
      "{\n"
          + "  \"id\" : \"proj1\",\n"
          + "  \"name\" : {\n"
          + "    \"en\" : \"My Project\",\n"
          + "    \"pt\" : \"Meu Projeto\"\n"
          + "  },\n"
          + "  \"description\" : {\n"
          + "    \"it\" : \"Il miglior progetto in modellazione concettuale.\",\n"
          + "    \"en\" : \"The best conceptual modeling project.\"\n"
          + "  },  \"type\" : \"Project\",  \"model\": {\n"
          + "    \"id\": \"pk1\",\n"
          + "    \"name\": \"Model\",\n"
          + "    \"type\": \"Package\",\n"
          + "    \"contents\": [\n"
          + "      { \"id\": \"c1\", \"type\": \"Class\", \"name\": \"Agent\" },\n"
          + "      { \"id\": \"c2\", \"type\": \"Class\", \"name\": \"Person\" },\n"
          + "      { \"id\": \"r1\", \"type\": \"Relation\", \"name\": \"knows\" },\n"
          + "      { \"id\": \"g1\", \"type\": \"Generalization\", \"name\": \"PersonToAgent\""
          + " },\n"
          + "      { \"id\": \"gs1\", \"type\": \"GeneralizationSet\", \"name\": \"AgentNature\""
          + " }\n"
          + "    ]\n"
          + "  },  \"diagrams\": null}";

  static ObjectMapper mapper;
  static Project project;

  @BeforeAll
  static void setUp() throws IOException {
    mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    project = mapper.readValue(json, Project.class);
  }

  @Test
  void shouldDeserializeId() {
    assertThat(project.getId()).isEqualTo("proj1");
  }

  @Test
  void shouldDeserializeName() {
    assertThat(project.getNameIn("en")).hasValue("My Project");
    assertThat(project.getNameIn("pt")).hasValue("Meu Projeto");
  }

  @Test
  void shouldDeserializeDescription() {
    assertThat(project.getDescriptionIn("en")).hasValue("The best conceptual modeling project.");
    assertThat(project.getDescriptionIn("it"))
        .hasValue("Il miglior progetto in modellazione concettuale.");
  }

  @Test
  void shouldDeserializeModel() {
    Optional<Package> model = project.getModel();
    assertThat(model).isPresent();
    assertThat(model.get().getId()).isEqualTo("pk1");
    assertThat(model.get().getFirstName()).hasValue("Model");
  }

  @Test
  void shouldDeserializeModelContents() {
    Package model = project.getModel().orElse(new Package());
    Stream<String> idStream = model.getContents().stream().map(Element::getId);
    assertThat(idStream).containsExactly("c1", "c2", "r1", "g1", "gs1");
  }

  @Test
  void shouldDeserializeDiagrams() {
    assertThat(project.getDiagrams()).isEmpty();
  }
}
