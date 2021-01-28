package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.unibz.inf.ontouml.vp.model.ontouml.*;
import it.unibz.inf.ontouml.vp.model.ontouml.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.Package;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

public class SerializationTest {
   ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

   @Test
   void name() throws IOException {
      Project project = new Project();
      project.addName("pt", "Meu Project");
      project.addName("en", "My Project");
      project.addDescription("it", "Il miglior progetto in modellazione concettuale.");
      project.addDescription("en", "The best conceptual modeling project.");

      Package model = project.createModel("pk1", "My Model");
      Package pkg = model.createPackage("pk2", "My Module");
      Class person = pkg.createKind("Person");
      Class organization = pkg.createKind("organization");
      Class car = pkg.createKind("Car");
      Class agent = pkg.createCategory("Agent");
      Relation owns = model.createRelation("owns", person, car);
      owns.getSourceEnd().setCardinality("1..*");
      owns.getTargetEnd().setCardinality("0..*");
      Generalization g1 = pkg.createGeneralization(person, agent);
      Generalization g2 = pkg.createGeneralization(organization, agent);
      GeneralizationSet genSet = model.createGeneralizationSet(g1, g2);
      genSet.setDisjoint(true);
      genSet.setComplete(true);

      mapper.writeValue(new File("src/test/resources/model.json"), project);
   }

}
