package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.unibz.inf.ontouml.vp.model.ontouml.*;
import it.unibz.inf.ontouml.vp.model.ontouml.model.*;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Package;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class SerializationTest {
  ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

  @Test
  void buildAndSerializeModel() throws IOException {
    Project project = new Project();
    project.addName("pt", "Meu Project");
    project.addName("en", "My Project");
    project.addDescription("it", "Il miglior progetto in modellazione concettuale.");
    project.addDescription("en", "The best conceptual modeling project.");

    Package model = project.createModel("pk1", "My Model");
    Package pkg = model.createPackage("pk2", "My Module");

    Class agent = model.createCategory("c1", "Agent");
    Class person = model.createKind("c2", "Person");
    Class organization = model.createKind("c3", "Organization");
    Class agentType = model.createType("c4", "AgentType");

    Class car = pkg.createKind("Car");
    Class string = pkg.createDatatype("string");
    Class number = pkg.createDatatype("number");

    Property name = agent.createAttribute("name", string);
    name.setCardinality("1..*");

    Property birthName = person.createAttribute("birthName", string);
    birthName.addRedefinedProperty(name);

    Property nickname = person.createAttribute("nickname", string);
    nickname.addSubsettedProperty(name);

    Property age = person.createAttribute("age", number);
    age.setCardinality("1");

    car.addPropertyAssignment("uri", "https://schema.org/Car");
    car.addPropertyAssignment("score", 10);
    car.addPropertyAssignment("value", 8.9);
    car.addPropertyAssignment("isLiked", false);
    car.addPropertyAssignment("author", new String[] {"Tiago", "Davi"});
    car.addPropertyAssignment("source", null);

    Relation owns = model.createRelation("owns", person, car);
    owns.getSourceEnd().setCardinality("1..*");
    owns.getTargetEnd().setCardinality("0..*");

    Generalization g1 = model.createGeneralization("g1", person, agent);
    Generalization g2 = model.createGeneralization("g2", organization, agent);

    GeneralizationSet genSet = model.createGeneralizationSet(g1, g2);
    genSet.setCategorizer(agentType);
    genSet.setDisjoint(true);
    genSet.setComplete(true);

    mapper.writeValue(new File("target/serializedModel.json"), project);
  }
}
