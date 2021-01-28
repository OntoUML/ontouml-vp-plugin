package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.unibz.inf.ontouml.vp.model.ontouml.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.ClassStereotype;
import it.unibz.inf.ontouml.vp.model.ontouml.Nature;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

public class ClassSerializerTest {

   ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
   Class cl = new Class("1", "Person", (String) null);

   @Test
   void shouldSerializeId() throws JsonProcessingException {
      String json = mapper.writeValueAsString(cl);
      assertThat(json).contains("\"id\" : \"1\"");
   }

   @Test
   void shouldSerializeType() throws JsonProcessingException {
      String json = mapper.writeValueAsString(cl);
      assertThat(json).contains("\"type\" : \"Class\"");
   }

   @Test
   void shouldSerializeName() throws JsonProcessingException {
      cl.addName("pt", "Pessoa");
      cl.addName("en", "Person");

      String json = mapper.writeValueAsString(cl);
      assertThat(json).contains("\"name\" : {");
      assertThat(json).contains("\"pt\" : \"Pessoa\"");
      assertThat(json).contains("\"en\" : \"Person\"");
   }

   @Test
   void shouldSerializeDescription() throws JsonProcessingException {
      cl.addDescription("pt", "Única espécie animal de primata bípede do género Homo ainda viva.");
      cl.addDescription("en", "Only bipedal primate animal species of the genus Homo still alive.");

      String json = mapper.writeValueAsString(cl);

      assertThat(json).contains("\"description\" : {");
      assertThat(json).contains("\"pt\" : \"Única espécie animal de primata bípede do género Homo ainda viva.\"");
      assertThat(json).contains("\"en\" : \"Only bipedal primate animal species of the genus Homo still alive.\"");
   }

   @Test
   void shouldSerializeOntoumlStereotype() throws JsonProcessingException {
      cl.setOntoumlStereotype(ClassStereotype.KIND);
      String json = mapper.writeValueAsString(cl);
      assertThat(json).contains("\"stereotype\" : \"kind\"");
   }

   @Test
   void shouldSerializeCustomStereotype() throws JsonProcessingException {
      cl.setCustomStereotype("custom");
      String json = mapper.writeValueAsString(cl);
      assertThat(json).contains("\"stereotype\" : \"custom\"");
   }

   @Test
   void shouldSerializeIsAbstract() throws JsonProcessingException {
      cl.setAbstract(true);
      String json = mapper.writeValueAsString(cl);
      assertThat(json).contains("\"isAbstract\" : true");
   }

   @Test
   void shouldSerializeIsDerived() throws JsonProcessingException {
      cl.setDerived(true);
      String json = mapper.writeValueAsString(cl);
      assertThat(json).contains("\"isDerived\" : true");
   }

   @Test
   void shouldSerializeEmptyAttributesAsNull() throws JsonProcessingException {
      String json = mapper.writeValueAsString(cl);
      assertThat(json).contains("\"properties\" : null");
   }

   @Test
   void shouldSerializeAttributes() throws JsonProcessingException {
      cl.createAttribute("a1", "father", cl);
      String json = mapper.writeValueAsString(cl);
      assertThat(json).contains("\"properties\" : [ {");
      assertThat(json).contains("\"id\" : \"a1\"");
      assertThat(json).contains("\"name\" : \"father\"");
   }

   @Test
   void shouldSerializeEmptyLiteralsAsNull() throws JsonProcessingException {
      String json = mapper.writeValueAsString(cl);
      assertThat(json).contains("\"literals\" : null");
   }

   @Test
   void shouldSerializeLiterals() throws JsonProcessingException {
      cl = Class.createEnumeration("1", "Color", "red", "green", "blue");
      String json = mapper.writeValueAsString(cl);
      assertThat(json).contains("\"literals\" : [ {");
      assertThat(json).contains("\"type\" : \"Literal\"");
      assertThat(json).contains("\"name\" : \"red\"");
      assertThat(json).contains("\"name\" : \"green\"");
      assertThat(json).contains("\"name\" : \"blue\"");
   }


   @Test
   void shouldSerializeEmptyRestrictedToAsNull() throws JsonProcessingException {
      String json = mapper.writeValueAsString(cl);
      assertThat(json).contains("\"restrictedTo\" : null");
   }

   @Test
   void shouldSerializeRestrictedTo() throws JsonProcessingException {
      cl = new Class("1", "Person");
      cl.setRestrictedTo(Nature.FUNCTIONAL_COMPLEX);
      String json = mapper.writeValueAsString(cl);
      assertThat(json).contains("\"restrictedTo\" : [ \"functional-complex\" ]");
   }

}
