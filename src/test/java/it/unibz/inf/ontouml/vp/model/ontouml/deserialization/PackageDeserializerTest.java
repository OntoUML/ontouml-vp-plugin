package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibz.inf.ontouml.vp.model.ontouml.*;
import it.unibz.inf.ontouml.vp.model.ontouml.model.*;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Package;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PackageDeserializerTest {

  static String json =
      "{\n"
          + "  \"id\": \"pk1\",\n"
          + "  \"name\": \"My Package\",\n"
          + "  \"description\": \"My description.\",\n"
          + "  \"type\": \"Package\",\n"
          + "  \"propertyAssignments\": {\n"
          + "    \"uri\": \"http://example.com/MyPackage\"\n"
          + "  },\n"
          + "  \"contents\": [\n"
          + "    { \"id\": \"c1\", \"type\": \"Class\", \"name\": \"Agent\" },\n"
          + "    { \"id\": \"r1\", \"type\": \"Relation\", \"name\": \"knows\" },\n"
          + "    { \"id\": \"g1\", \"type\": \"Generalization\", \"name\": \"PersonToAgent\" },\n"
          + "    { \"id\": \"gs1\", \"type\": \"GeneralizationSet\", \"name\": \"AgentNature\" },\n"
          + "    { "
          + "      \"id\": \"pk2\","
          + "      \"type\": \"Package\","
          + "      \"name\": \"Subpackage\","
          + "      \"contents\": [\n"
          + "        { \"id\": \"c2\", \"type\": \"Class\", \"name\": \"Person\" }"
          + "      ]\n"
          + "    }\n"
          + "  ]\n"
          + "}";

  static ObjectMapper mapper;
  static Package pkg;

  @BeforeAll
  static void setUp() throws JsonProcessingException {
    mapper = new ObjectMapper();
    pkg = mapper.readValue(json, Package.class);
  }

  @Test
  void shouldDeserializeId() {
    assertThat(pkg.getId()).isEqualTo("pk1");
  }

  @Test
  void shouldDeserializeName() {
    assertThat(pkg.getNameIn("en")).hasValue("My Package");
  }

  @Test
  void shouldDeserializeDescription() {
    assertThat(pkg.getDescriptionIn("en")).hasValue("My description.");
  }

  @Test
  void shouldDeserializePropertyAssignments() {
    Optional<Object> pAssignment = pkg.getPropertyAssignment("uri");
    assertThat(pAssignment).hasValue("http://example.com/MyPackage");
  }

  @Test
  void shouldDeserializeContents() {
    assertThat(pkg.getContents()).hasSize(5);
  }

  @Test
  void shouldDeserializeClassInContents() {
    OntoumlElement element = pkg.getContents().get(0);
    assertThat(element).isInstanceOf(Class.class);
    assertThat(element.getId()).isEqualTo("c1");
    assertThat(element.getFirstName()).hasValue("Agent");
  }

  @Test
  void shouldDeserializeRelationInContents() {
    OntoumlElement element = pkg.getContents().get(1);
    assertThat(element).isInstanceOf(Relation.class);
    assertThat(element.getId()).isEqualTo("r1");
    assertThat(element.getFirstName()).hasValue("knows");
  }

  @Test
  void shouldDeserializeGeneralizationInContents() {
    OntoumlElement element = pkg.getContents().get(2);
    assertThat(element).isInstanceOf(Generalization.class);
    assertThat(element.getId()).isEqualTo("g1");
    assertThat(element.getFirstName()).hasValue("PersonToAgent");
  }

  @Test
  void shouldDeserializeGeneralizationSetInContents() {
    OntoumlElement element = pkg.getContents().get(3);
    assertThat(element).isInstanceOf(GeneralizationSet.class);
    assertThat(element.getId()).isEqualTo("gs1");
    assertThat(element.getFirstName()).hasValue("AgentNature");
  }

  @Test
  void shouldDeserializePackageInContents() {
    OntoumlElement element = pkg.getContents().get(4);
    assertThat(element).isInstanceOf(Package.class);
    assertThat(element.getId()).isEqualTo("pk2");
    assertThat(element.getFirstName()).hasValue("Subpackage");
  }

  @Test
  void shouldDeserializeSubPackageContents() {
    OntoumlElement element = pkg.getContents().get(4);
    assertThat(element).isInstanceOf(Package.class);

    List<OntoumlElement> contents = element.getContents();
    assertThat(contents).hasSize(1);
    assertThat(contents.get(0)).isInstanceOf(Class.class);
    assertThat(contents.get(0).getId()).isEqualTo("c2");
  }
}
