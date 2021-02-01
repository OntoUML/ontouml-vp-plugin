package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibz.inf.ontouml.vp.model.ontouml.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.Literal;
import it.unibz.inf.ontouml.vp.model.ontouml.Nature;
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
          + "  \"properties\" : null,\n"
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
  void shouldDeserializeLiterals() {
    assertThat(clazz.getLiterals()).hasSize(1);

    Literal literal = clazz.getLiterals().get(0);
    assertThat(literal.getId()).isEqualTo("l1");
    assertThat(literal.getFirstName()).hasValue("red");
  }
}
