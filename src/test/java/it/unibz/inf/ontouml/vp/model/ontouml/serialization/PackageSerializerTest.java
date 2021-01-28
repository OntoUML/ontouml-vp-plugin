package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.unibz.inf.ontouml.vp.model.ontouml.Package;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

public class PackageSerializerTest {
   ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
   Package pkg = new Package("pk1", "Package");

   @Test
   void shouldSerializeId() throws JsonProcessingException {
      String json = mapper.writeValueAsString(pkg);
      assertThat(json).contains("\"id\" : \"pk1\"");
   }

   @Test
   void shouldSerializeType() throws JsonProcessingException {
      String json = mapper.writeValueAsString(pkg);
      assertThat(json).contains("\"type\" : \"Package\"");
   }

   @Test
   void shouldSerializeName() throws JsonProcessingException {
      pkg.addName("en", "My Reference Ontology");
      pkg.addName("es", "Mi ontología de referencia");

      String json = mapper.writeValueAsString(pkg);

      assertThat(json).contains("\"name\" : {");
      assertThat(json).contains("\"en\" : \"My Reference Ontology\"");
      assertThat(json).contains("\"es\" : \"Mi ontología de referencia\"");
   }

   @Test
   void shouldSerializeDescription() throws JsonProcessingException {
      pkg.addDescription("en", "The ontology about all things that could exist.");
      pkg.addDescription("es", "La ontología sobre todas las cosas que podrían existir.");

      String json = mapper.writeValueAsString(pkg);

      assertThat(json).contains("\"description\" : {");
      assertThat(json).contains("\"en\" : \"The ontology about all things that could exist.\"");
      assertThat(json).contains("\"es\" : \"La ontología sobre todas las cosas que podrían existir.\"");
   }

   @Test
   void shouldSerializeEmptyDescriptionAsNull() throws JsonProcessingException {
      String json = mapper.writeValueAsString(pkg);
      assertThat(json).contains("\"description\" : null");
   }

   @Test
   void shouldSerializePropertyAssignments() throws JsonProcessingException {
      pkg.addPropertyAssignment("acronym", "UFO-X");
      pkg.addPropertyAssignment("numberOfContributors", 100);

      String json = mapper.writeValueAsString(pkg);

      assertThat(json).contains("\"propertyAssignments\" : {");
      assertThat(json).contains("\"acronym\" : \"UFO-X\"");
      assertThat(json).contains("\"numberOfContributors\" : 100");
   }

   @Test
   void shouldSerializeEmptyPropertyAssignmentsAsNull() throws JsonProcessingException {
      String json = mapper.writeValueAsString(pkg);
      assertThat(json).contains("\"propertyAssignments\" : null");
   }

   @Test
   void shouldSerializeContents() throws JsonProcessingException {
      pkg.createClass("c1", "Person", (String) null);
      String json = mapper.writeValueAsString(pkg);
      assertThat(json).contains("\"contents\" : [");
      assertThat(json).contains("\"id\" : \"c1\"");
      assertThat(json).contains("\"name\" : \"Person\"");
   }

   @Test
   void shouldSerializeEmptyContentsAsNull() throws JsonProcessingException {
      String json = mapper.writeValueAsString(pkg);
      assertThat(json).contains("\"contents\" : null");
   }
}
