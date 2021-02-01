package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibz.inf.ontouml.vp.model.ontouml.AggregationKind;
import it.unibz.inf.ontouml.vp.model.ontouml.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.Property;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PropertyDeserializerTest {

  static String json =
      "{\n"
          + "   \"id\" : \"p1\",\n"
          + "   \"name\" : \"birthName\",\n"
          + "   \"description\" : null,\n"
          + "   \"type\" : \"Property\",\n"
          + "   \"propertyAssignments\" : null,\n"
          + "   \"stereotype\" : null,\n"
          + "   \"isDerived\" : true,\n"
          + "   \"isReadOnly\" : true,\n"
          + "   \"isOrdered\" : true,\n"
          + "   \"cardinality\" : \"1..*\",\n"
          + "   \"propertyType\" : {\n"
          + "     \"id\" : \"c1\",\n"
          + "     \"type\" : \"Class\"\n"
          + "   },\n"
          + "   \"subsettedProperties\" : [ {\n"
          + "     \"id\" : \"p2\",\n"
          + "     \"type\" : \"Property\"\n"
          + "   } ],\n"
          + "   \"redefinedProperties\" : [ {\n"
          + "     \"id\" : \"p3\",\n"
          + "     \"type\" : \"Property\"\n"
          + "   } ],\n"
          + "   \"aggregationKind\" : \"COMPOSITE\"\n"
          + "}";

  static ObjectMapper mapper;
  static Property property;

  @BeforeAll
  static void setUp() throws JsonProcessingException {
    mapper = new ObjectMapper();
    property = mapper.readValue(json, Property.class);
  }

  @Test
  void shouldDeserializeReference() throws JsonProcessingException {
    String jsonReference = "{ \"id\": \"p1\", \"type\":\"Property\"}";
    Property reference = mapper.readValue(jsonReference, Property.class);

    assertThat(reference.getId()).isEqualTo("p1");
    assertThat(reference.getFirstName()).isEmpty();
    assertThat(reference.getStereotype()).isEmpty();
  }

  @Test
  void shouldDeserializeId() {
    assertThat(property.getId()).isEqualTo("p1");
  }

  @Test
  void shouldDeserializeIsDerived() {
    assertThat(property.isDerived()).isTrue();
  }

  @Test
  void shouldDeserializeIsReadOnly() {
    assertThat(property.isReadOnly()).isTrue();
  }

  @Test
  void shouldDeserializeIsOrdered() {
    assertThat(property.isOrdered()).isTrue();
  }

  @Test
  void shouldDeserializeCardinality() {
    assertThat(property.getCardinalityValue()).hasValue("1..*");
  }

  @Test
  void shouldDeserializePropertyType() {
    assertThat(property.getPropertyType()).isPresent();
    assertThat(property.getPropertyType().get()).isInstanceOf(Class.class);
    assertThat(property.getPropertyType().get().getId()).isEqualTo("c1");
  }

  @Test
  void shouldDeserializeSubsettedProperties() {
    assertThat(property.getSubsettedProperties()).hasSize(1);

    Property subsetted = property.getSubsettedProperties().get(0);
    assertThat(subsetted.getId()).isEqualTo("p2");
  }

  @Test
  void shouldDeserializeRedefinedProperties() {
    assertThat(property.getRedefinedProperties()).hasSize(1);

    Property redefined = property.getRedefinedProperties().get(0);
    assertThat(redefined.getId()).isEqualTo("p3");
  }

  @Test
  void shouldDeserializeAggregationKind() {
    assertThat(property.getAggregationKind()).hasValue(AggregationKind.COMPOSITE);
  }
}
