package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibz.inf.ontouml.vp.model.ontouml.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.Property;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ClassifierDeserializerTest {

  static String json =
      "{\n"
          + "  \"id\" : \"c1\",\n"
          + "  \"type\" : \"Class\",\n"
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
          + "}";

  static ObjectMapper mapper;
  static Class clazz;
  static List<Property> properties;

  @BeforeAll
  static void setUp() throws JsonProcessingException {
    mapper = new ObjectMapper();
    clazz = mapper.readValue(json, Class.class);
    properties = clazz.getProperties();
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
  void shouldDeserializeAllProperties() {
    assertThat(properties).hasSize(2);
  }

  @Test
  void shouldDeserializePropertiesInTheCorrectOrder() {
    Property a1 = properties.get(0);
    assertThat(a1.getId()).isEqualTo("a1");
    Property a2 = properties.get(1);
    assertThat(a2.getId()).isEqualTo("a2");
  }

  @Test
  void shouldDeserializePropertyData() {
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
}
