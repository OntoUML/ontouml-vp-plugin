package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibz.inf.ontouml.vp.model.ontouml.*;
import it.unibz.inf.ontouml.vp.model.ontouml.Class;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ClassDeserializerTest {

  static String json =
      "{\n"
          + "  \"id\" : \"c1\",\n"
          + "  \"name\" : \n{"
          + "    \"en\" : \"My Class\",\n"
          + "    \"pt\" : \"Minha Classe\"\n"
          + "  },\n"
          + "  \"description\" : \n{"
          + "    \"en\" : \"My description.\",\n"
          + "    \"pt\" : \"Minha descrição.\"\n"
          + "  },\n"
          + "  \"type\" : \"Class\",\n"
          + "  \"propertyAssignments\" : null,\n"
          + "  \"stereotype\" : \"category\",\n"
          + "  \"isAbstract\" : true,\n"
          + "  \"isDerived\" : true,\n"
          + "  \"properties\" : [ {\n"
          + "    \"id\" : \"a1\",\n"
          + "    \"name\" : \"name\",\n"
          + "    \"description\" : null,\n"
          + "    \"type\" : \"Property\",\n"
          + "    \"propertyAssignments\" : null,\n"
          + "    \"stereotype\" : null,\n"
          + "    \"isDerived\" : true,\n"
          + "    \"isReadOnly\" : true,\n"
          + "    \"isOrdered\" : true,\n"
          + "    \"cardinality\" : \"1..*\",\n"
          + "    \"propertyType\" : {\n"
          + "      \"id\" : \"ref1\",\n"
          + "      \"type\" : \"Class\"\n"
          + "    },\n"
          + "    \"subsettedProperties\" : null,\n"
          + "    \"redefinedProperties\" : null,\n"
          + "    \"aggregationKind\" : null\n"
          + "  },{\n"
          + "    \"id\" : \"a2\",\n"
          + "    \"name\" : \"age\",\n"
          + "    \"description\" : null,\n"
          + "    \"type\" : \"Property\",\n"
          + "    \"propertyAssignments\" : null,\n"
          + "    \"stereotype\" : null,\n"
          + "    \"isDerived\" : false,\n"
          + "    \"isReadOnly\" : false,\n"
          + "    \"isOrdered\" : false,\n"
          + "    \"cardinality\" : \"1\",\n"
          + "    \"propertyType\" : {\n"
          + "      \"id\" : \"ref2\",\n"
          + "      \"type\" : \"Class\"\n"
          + "    },\n"
          + "    \"subsettedProperties\" : null,\n"
          + "    \"redefinedProperties\" : null,\n"
          + "    \"aggregationKind\" : null\n"
          + "  } ]\n"
          + "  \"isExtensional\" : true,\n"
          + "  \"isPowertype\" : true,\n"
          + "  \"order\" : 1,\n"
          + "  \"literals\" : [{"
          + "    \"id\": \"l1\","
          + "    \"type\": \"Literal\","
          + "    \"name\": \"red\","
          + "    \"description\": \"null\","
          + "    \"propertyAssignments\": \"null\""
          + "  }],\n"
          + "  \"restrictedTo\" : [ \"functional-complex\", \"collective\", \"quantity\" ]\n"
          + "}";

  static ObjectMapper mapper;
  static Class clazz;

  @BeforeAll
  static void setUp() throws JsonProcessingException {
    mapper = new ObjectMapper();
    clazz = mapper.readValue(json, Class.class);
  }

  @Test
  void shouldDeserializeReference() throws JsonProcessingException {
    String jsonReference = "{ \"id\": \"c1\", \"type\":\"Class\"}";
    Class clazz = mapper.readValue(jsonReference, Class.class);

    assertThat(clazz.getId()).isEqualTo("c1");
    assertThat(clazz.getFirstName()).isEmpty();
    assertThat(clazz.getStereotype()).isEmpty();
  }

  @Test
  void shouldDeserializeId() {
    assertThat(clazz.getId()).isEqualTo("c1");
  }

  @Test
  void shouldDeserializeName() {
    assertThat(clazz.getNameIn("en")).hasValue("My Class");
    assertThat(clazz.getNameIn("pt")).hasValue("Minha Classe");
  }

  @Test
  void shouldDeserializeDescription() {
    assertThat(clazz.getDescriptionIn("pt")).hasValue("Minha descrição.");
    assertThat(clazz.getDescriptionIn("en")).hasValue("My description.");
  }

  @Test
  void shouldDeserializeIsAbstract() {
    assertThat(clazz.isAbstract()).isTrue();
  }

  @Test
  void shouldDeserializeIsDerived() {
    assertThat(clazz.isDerived()).isTrue();
  }

  @Test
  void shouldDeserializeIsExtensional() {
    assertThat(clazz.isExtensional()).hasValue(true);
  }

  @Test
  void shouldDeserializeIsPowertype() {
    assertThat(clazz.isPowertype()).hasValue(true);
  }

  @Test
  void shouldDeserializeOrder() {
    assertThat(clazz.getOrder()).hasValue(1);
  }

  @Test
  void shouldDeserializeRestrictedTo() {
    assertThat(clazz.getRestrictedTo())
        .containsExactly(Nature.FUNCTIONAL_COMPLEX, Nature.COLLECTIVE, Nature.QUANTITY);
  }

  @Test
  void shouldDeserializeAllProperties() {
    List<Property> properties = clazz.getProperties();
    assertThat(properties).hasSize(2);
  }

  @Test
  void shouldDeserializePropertiesInTheCorrectOrder() {
    List<Property> properties = clazz.getProperties();
    Property a1 = properties.get(0);
    assertThat(a1.getId()).isEqualTo("a1");
    Property a2 = properties.get(1);
    assertThat(a2.getId()).isEqualTo("a2");
  }

  @Test
  void shouldDeserializePropertyData() {
    List<Property> properties = clazz.getProperties();
    Property a1 = properties.get(0);
    assertThat(a1.getId()).isEqualTo("a1");
    assertThat(a1.isDerived()).isTrue();
    assertThat(a1.isOrdered()).isTrue();
    assertThat(a1.isReadOnly()).isTrue();
    assertThat(a1.getFirstName()).hasValue("name");
    assertThat(a1.getCardinality().getValue()).hasValue("1..*");
    assertThat(a1.getPropertyType()).isPresent();
    assertThat(a1.getPropertyType().get().getId()).isEqualTo("ref1");
  }

  @Test
  void shouldDeserializeLiterals() {
    assertThat(clazz.getLiterals()).hasSize(1);

    Literal literal = clazz.getLiterals().get(0);
    assertThat(literal.getId()).isEqualTo("l1");
    assertThat(literal.getFirstName()).hasValue("red");
  }

  @Test
  void shouldDeserializeOntoumlStereotype() throws JsonProcessingException {
    String json =
        "{\n"
            + "  \"id\": \"c1\",\n"
            + "  \"type\": \"Class\",\n"
            + "  \"stereotype\": \"kind\"\n"
            + "}";

    Class clazz = mapper.readValue(json, Class.class);
    assertThat(clazz.getOntoumlStereotype()).hasValue(ClassStereotype.KIND);
    assertThat(clazz.getCustomStereotype()).isEmpty();
  }

  @Test
  void shouldDeserializeCustomStereotype() throws JsonProcessingException {
    String json =
        "{\n"
            + "  \"id\": \"c1\",\n"
            + "  \"type\": \"Class\",\n"
            + "  \"stereotype\": \"custom\"\n"
            + "}";

    Class clazz = mapper.readValue(json, Class.class);
    assertThat(clazz.getOntoumlStereotype()).isEmpty();
    assertThat(clazz.getCustomStereotype()).hasValue("custom");
  }
}
