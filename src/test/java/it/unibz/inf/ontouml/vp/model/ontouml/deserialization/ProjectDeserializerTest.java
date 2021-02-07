package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.unibz.inf.ontouml.vp.model.ontouml.*;
import it.unibz.inf.ontouml.vp.model.ontouml.model.*;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Package;
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
  static void setUp() throws JsonProcessingException {
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
  void shouldResolvePropertyTypeReferences() throws JsonProcessingException {
    String json =
        "{\n"
            + "  \"type\": \"Project\",\n"
            + "  \"id\": \"pj1\",\n"
            + "  \"model\": {\n"
            + "    \"id\": \"pk2\",\n"
            + "    \"type\": \"Package\",\n"
            + "    \"contents\": [\n"
            + "      {\n"
            + "        \"id\": \"c1\",\n"
            + "        \"type\": \"Class\",\n"
            + "        \"properties\": [\n"
            + "          {\n"
            + "            \"id\": \"att1\",\n"
            + "            \"type\": \"Property\",\n"
            + "            \"propertyType\": { \"id\": \"c2\", \"type\": \"Class\" }\n"
            + "          }\n"
            + "        ]\n"
            + "      },\n"
            + "      { \"id\": \"c2\", \"type\": \"Class\" }\n"
            + "    ]\n"
            + "  }\n"
            + "}";

    Project project = mapper.readValue(json, Project.class);

    Class type = project.getClassById("c2").orElse(null);
    assertThat(type).isNotNull();

    Property prop = project.getPropertyById("att1").orElse(null);
    assertThat(prop).isNotNull();

    assertThat(prop.getPropertyType()).hasValue(type);
  }

  @Test
  void shouldResolveSubsettingRedefiningReferences() throws JsonProcessingException {
    String json =
        "{\n"
            + "  \"type\": \"Project\",\n"
            + "  \"id\": \"pj1\",\n"
            + "  \"model\": {\n"
            + "    \"id\": \"pk2\",\n"
            + "    \"type\": \"Package\",\n"
            + "    \"contents\": [\n"
            + "      {\n"
            + "        \"id\": \"c1\",\n"
            + "        \"type\": \"Class\",\n"
            + "        \"properties\": [\n"
            + "          {\n"
            + "            \"id\": \"att1\",\n"
            + "            \"type\": \"Property\",\n"
            + "            \"propertyType\": { \"id\": \"c2\", \"type\": \"Class\" }\n"
            + "          },\n"
            + "          {\n"
            + "            \"id\": \"att2\",\n"
            + "            \"type\": \"Property\",\n"
            + "            \"propertyType\": { \"id\": \"c2\", \"type\": \"Class\" },\n"
            + "            \"subsettedProperties\": [ {\"id\": \"att1\", \"type\": \"Property\"}"
            + " ],\n"
            + "            \"redefinedProperties\": [ {\"id\": \"att1\", \"type\": \"Property\"}"
            + " ]\n"
            + "          }\n"
            + "        ]\n"
            + "      },\n"
            + "      { \"id\": \"c2\", \"type\": \"Class\" }\n"
            + "    ]\n"
            + "  },\n"
            + "  \"diagrams\": null\n"
            + "}";

    Project project = mapper.readValue(json, Project.class);

    Property name = project.getPropertyById("att1").orElse(null);
    assertThat(name).isNotNull();

    Property nickname = project.getPropertyById("att2").orElse(null);
    assertThat(nickname).isNotNull();

    assertThat(nickname.getSubsettedProperties()).containsExactly(name);
    assertThat(nickname.getRedefinedProperties()).containsExactly(name);
  }

  @Test
  void shouldResolveGeneralSpecificReferences() throws JsonProcessingException {
    String json =
        "{\n"
            + "  \"id\": \"pj1\",\n"
            + "  \"type\": \"Project\",\n"
            + "  \"model\": {\n"
            + "    \"id\": \"pk1\",\n"
            + "    \"type\": \"Package\",\n"
            + "    \"contents\": [\n"
            + "      {\n"
            + "        \"id\": \"c1\",\n"
            + "        \"name\": \"Agent\",\n"
            + "        \"type\": \"Class\"\n"
            + "      },\n"
            + "      {\n"
            + "        \"id\": \"c2\",\n"
            + "        \"name\": \"Person\",\n"
            + "        \"type\": \"Class\"\n"
            + "      },\n"
            + "      {\n"
            + "        \"id\": \"g1\",\n"
            + "        \"type\": \"Generalization\",\n"
            + "        \"general\": {\n"
            + "          \"id\": \"c1\",\n"
            + "          \"type\": \"Class\"\n"
            + "        },\n"
            + "        \"specific\": {\n"
            + "          \"id\": \"c2\",\n"
            + "          \"type\": \"Class\"\n"
            + "        }\n"
            + "      }\n"
            + "    ]\n"
            + "  }\n"
            + "}";

    Project project = mapper.readValue(json, Project.class);

    Class agent = project.getClassById("c1").orElse(null);
    assertThat(agent).isNotNull();

    Class person = project.getClassById("c2").orElse(null);
    assertThat(person).isNotNull();

    Generalization gen = project.getGeneralizationById("g1").orElse(null);
    assertThat(gen).isNotNull();

    assertThat(gen.getGeneral()).hasValue(agent);
    assertThat(gen.getSpecific()).hasValue(person);
  }

  @Test
  void shouldResolveCategorizerReference() throws JsonProcessingException {
    String json =
        "{\n"
            + "  \"id\": \"pr1\",\n"
            + "  \"type\": \"Project\",\n"
            + "  \"model\": {\n"
            + "    \"id\": \"pk1\",\n"
            + "    \"type\": \"Package\",\n"
            + "    \"contents\": [\n"
            + "      { \"id\": \"c4\", \"name\": \"AgentType\", \"type\": \"Class\" },\n"
            + "      {\n"
            + "        \"id\": \"gs1\",\n"
            + "        \"type\": \"GeneralizationSet\",\n"
            + "        \"categorizer\": { \"id\": \"c4\", \"type\": \"Class\" }\n"
            + "      }\n"
            + "    ]\n"
            + "  }\n"
            + "}";

    Project project = mapper.readValue(json, Project.class);

    Class agentType = project.getClassById("c4").orElse(null);
    assertThat(agentType).isNotNull();

    GeneralizationSet gs = project.getGeneralizationSetById("gs1").orElse(null);
    assertThat(gs).isNotNull();

    assertThat(gs.getCategorizer()).hasValue(agentType);
  }

  @Test
  void shouldResolveGeneralizationsReferences() throws JsonProcessingException {
    String json =
        "{\n"
            + "  \"id\": \"pr1\",\n"
            + "  \"type\": \"Project\",\n"
            + "  \"model\": {\n"
            + "    \"id\": \"pk1\",\n"
            + "    \"type\": \"Package\",\n"
            + "    \"contents\": [\n"
            + "      { \"id\": \"c1\", \"name\": \"Agent\", \"type\": \"Class\" },\n"
            + "      { \"id\": \"c2\", \"name\": \"Person\", \"type\": \"Class\" },\n"
            + "      { \"id\": \"c3\", \"name\": \"Organization\", \"type\": \"Class\" },\n"
            + "      { \n"
            + "        \"id\": \"g1\",\n"
            + "        \"type\": \"Generalization\",\n"
            + "        \"general\": { \"id\": \"c1\", \"type\": \"Class\" },\n"
            + "        \"specific\": { \"id\": \"c2\", \"type\": \"Class\" }\n"
            + "      },\n"
            + "      {\n"
            + "        \"id\": \"g2\",\n"
            + "        \"type\": \"Generalization\",\n"
            + "        \"general\": { \"id\": \"c1\", \"type\": \"Class\" },\n"
            + "        \"specific\": { \"id\": \"c3\", \"type\": \"Class\" }\n"
            + "      },\n"
            + "      {\n"
            + "        \"id\": \"gs1\",\n"
            + "        \"type\": \"GeneralizationSet\",\n"
            + "        \"generalizations\": [\n"
            + "          { \"id\": \"g2\", \"type\": \"Generalization\" },\n"
            + "          { \"id\": \"g1\", \"type\": \"Generalization\" }\n"
            + "        ]\n"
            + "      }\n"
            + "    ]\n"
            + "  }\n"
            + "}";

    Project project = mapper.readValue(json, Project.class);

    Generalization g1 = project.getGeneralizationById("g1").orElse(null);
    assertThat(g1).isNotNull();

    Generalization g2 = project.getGeneralizationById("g2").orElse(null);
    assertThat(g2).isNotNull();

    GeneralizationSet gs = project.getGeneralizationSetById("gs1").orElse(null);
    assertThat(gs).isNotNull();

    assertThat(gs.getGeneralizations()).containsExactly(g1, g2);
  }

  @Test
  void shouldDeserializeDiagrams() {
    assertThat(project.getDiagrams()).isEmpty();
  }
}
