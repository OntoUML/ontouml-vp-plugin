package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Classifier;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Generalization;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class GeneralizationDeserializerTest {

  static String json =
      "{\n"
          + "  \"id\" : \"g1\",\n"
          + "  \"type\" : \"Generalization\",\n"
          + "  \"name\" : {\n"
          + "    \"en\" : \"My Generalization\",\n"
          + "    \"pt\" : \"Minha Generalization\"\n"
          + "  },\n"
          + "  \"description\" : {\n"
          + "    \"pt\" : \"Minha descrição.\",\n"
          + "    \"en\" : \"My description.\"\n"
          + "  },\n"
          + "  \"general\" : {\n"
          + "    \"id\" : \"c1\",\n"
          + "    \"type\" : \"Class\"\n"
          + "  },\n"
          + "  \"specific\" : {\n"
          + "    \"id\" : \"c2\",\n"
          + "    \"type\" : \"Class\"\n"
          + "  }\n"
          + "}";

  static ObjectMapper mapper;
  static Generalization gen;

  @BeforeAll
  static void setUp() throws JsonProcessingException {
    mapper = new ObjectMapper();
    gen = mapper.readValue(json, Generalization.class);
  }

  @Test
  void shouldDeserializeId() {
    assertThat(gen.getId()).isEqualTo("g1");
  }

  @Test
  void shouldDeserializeName() {
    assertThat(gen.getNameIn("en")).hasValue("My Generalization");
    assertThat(gen.getNameIn("pt")).hasValue("Minha Generalization");
  }

  @Test
  void shouldDeserializeDescription() {
    assertThat(gen.getDescriptionIn("en")).hasValue("My description.");
    assertThat(gen.getDescriptionIn("pt")).hasValue("Minha descrição.");
  }

  @Test
  void shouldDeserializeGeneral() {
    Optional<Classifier<?, ?>> general = gen.getGeneral();

    assertThat(general).isPresent();
    assertThat(general.get().getId()).isEqualTo("c1");
    assertThat(general.get().getFirstName()).isEmpty();
  }

  @Test
  void shouldDeserializeSpecific() {
    Optional<Classifier<?, ?>> specific = gen.getSpecific();

    assertThat(specific).isPresent();
    assertThat(specific.get().getId()).isEqualTo("c2");
    assertThat(specific.get().getFirstName()).isEmpty();
  }
}
