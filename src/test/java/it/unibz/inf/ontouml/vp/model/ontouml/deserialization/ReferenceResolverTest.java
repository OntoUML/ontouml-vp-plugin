package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.truth.Truth8;
import it.unibz.inf.ontouml.vp.model.ontouml.Project;
import it.unibz.inf.ontouml.vp.model.ontouml.model.*;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.view.ClassView;
import it.unibz.inf.ontouml.vp.model.ontouml.view.GeneralizationView;
import it.unibz.inf.ontouml.vp.model.ontouml.view.RelationView;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class ReferenceResolverTest {
  ObjectMapper mapper = new ObjectMapper();

  @Test
  void shouldResolvePropertyTypeReferences() throws IOException {
    String json =
        "{\n"
            + "  \"type\": \"Project\",\n"
            + "  \"id\": \"pj1\",\n"
            + "  \"model\": {\n"
            + "    \"id\": \"pk2\",\n"
            + "    \"type\": \"Package\",\n"
            + "    \"contents\": [\n"
            + "      {\n"
            + "        \"id\": \"c1\",\n"
            + "        \"type\": \"Class\",\n"
            + "        \"properties\": [\n"
            + "          {\n"
            + "            \"id\": \"att1\",\n"
            + "            \"type\": \"Property\",\n"
            + "            \"propertyType\": { \"id\": \"c2\", \"type\": \"Class\" }\n"
            + "          }\n"
            + "        ]\n"
            + "      },\n"
            + "      { \"id\": \"c2\", \"type\": \"Class\" }\n"
            + "    ]\n"
            + "  }\n"
            + "}";

    Project project = mapper.readValue(json, Project.class);

    Class type = project.getClassById("c2").orElse(null);
    assertThat(type).isNotNull();

    Property prop = project.getPropertyById("att1").orElse(null);
    assertThat(prop).isNotNull();

    Truth8.assertThat(prop.getPropertyType()).hasValue(type);
  }

  @Test
  void shouldResolveSubsettingRedefiningReferences() throws IOException {
    String json =
        "{\n"
            + "  \"type\": \"Project\",\n"
            + "  \"id\": \"pj1\",\n"
            + "  \"model\": {\n"
            + "    \"id\": \"pk2\",\n"
            + "    \"type\": \"Package\",\n"
            + "    \"contents\": [\n"
            + "      {\n"
            + "        \"id\": \"c1\",\n"
            + "        \"type\": \"Class\",\n"
            + "        \"properties\": [\n"
            + "          {\n"
            + "            \"id\": \"att1\",\n"
            + "            \"type\": \"Property\",\n"
            + "            \"propertyType\": { \"id\": \"c2\", \"type\": \"Class\" }\n"
            + "          },\n"
            + "          {\n"
            + "            \"id\": \"att2\",\n"
            + "            \"type\": \"Property\",\n"
            + "            \"propertyType\": { \"id\": \"c2\", \"type\": \"Class\" },\n"
            + "            \"subsettedProperties\": [ {\"id\": \"att1\", \"type\": \"Property\"}"
            + " ],\n"
            + "            \"redefinedProperties\": [ {\"id\": \"att1\", \"type\": \"Property\"}"
            + " ]\n"
            + "          }\n"
            + "        ]\n"
            + "      },\n"
            + "      { \"id\": \"c2\", \"type\": \"Class\" }\n"
            + "    ]\n"
            + "  },\n"
            + "  \"diagrams\": null\n"
            + "}";

    Project project = mapper.readValue(json, Project.class);

    Property name = project.getPropertyById("att1").orElse(null);
    assertThat(name).isNotNull();

    Property nickname = project.getPropertyById("att2").orElse(null);
    assertThat(nickname).isNotNull();

    assertThat(nickname.getSubsettedProperties()).containsExactly(name);
    assertThat(nickname.getRedefinedProperties()).containsExactly(name);
  }

  @Test
  void shouldResolveGeneralSpecificReferences() throws IOException {
    String json =
        "{\n"
            + "  \"id\": \"pj1\",\n"
            + "  \"type\": \"Project\",\n"
            + "  \"model\": {\n"
            + "    \"id\": \"pk1\",\n"
            + "    \"type\": \"Package\",\n"
            + "    \"contents\": [\n"
            + "      {\n"
            + "        \"id\": \"c1\",\n"
            + "        \"name\": \"Agent\",\n"
            + "        \"type\": \"Class\"\n"
            + "      },\n"
            + "      {\n"
            + "        \"id\": \"c2\",\n"
            + "        \"name\": \"Person\",\n"
            + "        \"type\": \"Class\"\n"
            + "      },\n"
            + "      {\n"
            + "        \"id\": \"g1\",\n"
            + "        \"type\": \"Generalization\",\n"
            + "        \"general\": {\n"
            + "          \"id\": \"c1\",\n"
            + "          \"type\": \"Class\"\n"
            + "        },\n"
            + "        \"specific\": {\n"
            + "          \"id\": \"c2\",\n"
            + "          \"type\": \"Class\"\n"
            + "        }\n"
            + "      }\n"
            + "    ]\n"
            + "  }\n"
            + "}";

    Project project = mapper.readValue(json, Project.class);

    Class agent = project.getClassById("c1").orElse(null);
    assertThat(agent).isNotNull();

    Class person = project.getClassById("c2").orElse(null);
    assertThat(person).isNotNull();

    Generalization gen = project.getGeneralizationById("g1").orElse(null);
    assertThat(gen).isNotNull();

    Truth8.assertThat(gen.getGeneral()).hasValue(agent);
    Truth8.assertThat(gen.getSpecific()).hasValue(person);
  }

  @Test
  void shouldResolveCategorizerReference() throws IOException {
    String json =
        "{\n"
            + "  \"id\": \"pr1\",\n"
            + "  \"type\": \"Project\",\n"
            + "  \"model\": {\n"
            + "    \"id\": \"pk1\",\n"
            + "    \"type\": \"Package\",\n"
            + "    \"contents\": [\n"
            + "      { \"id\": \"c4\", \"name\": \"AgentType\", \"type\": \"Class\" },\n"
            + "      {\n"
            + "        \"id\": \"gs1\",\n"
            + "        \"type\": \"GeneralizationSet\",\n"
            + "        \"categorizer\": { \"id\": \"c4\", \"type\": \"Class\" }\n"
            + "      }\n"
            + "    ]\n"
            + "  }\n"
            + "}";

    Project project = mapper.readValue(json, Project.class);

    Class agentType = project.getClassById("c4").orElse(null);
    assertThat(agentType).isNotNull();

    GeneralizationSet gs = project.getGeneralizationSetById("gs1").orElse(null);
    assertThat(gs).isNotNull();

    Truth8.assertThat(gs.getCategorizer()).hasValue(agentType);
  }

  @Test
  void shouldResolveGeneralizationsReferences() throws IOException {
    String json =
        "{\n"
            + "  \"id\": \"pr1\",\n"
            + "  \"type\": \"Project\",\n"
            + "  \"model\": {\n"
            + "    \"id\": \"pk1\",\n"
            + "    \"type\": \"Package\",\n"
            + "    \"contents\": [\n"
            + "      { \"id\": \"c1\", \"name\": \"Agent\", \"type\": \"Class\" },\n"
            + "      { \"id\": \"c2\", \"name\": \"Person\", \"type\": \"Class\" },\n"
            + "      { \"id\": \"c3\", \"name\": \"Organization\", \"type\": \"Class\" },\n"
            + "      { \n"
            + "        \"id\": \"g1\",\n"
            + "        \"type\": \"Generalization\",\n"
            + "        \"general\": { \"id\": \"c1\", \"type\": \"Class\" },\n"
            + "        \"specific\": { \"id\": \"c2\", \"type\": \"Class\" }\n"
            + "      },\n"
            + "      {\n"
            + "        \"id\": \"g2\",\n"
            + "        \"type\": \"Generalization\",\n"
            + "        \"general\": { \"id\": \"c1\", \"type\": \"Class\" },\n"
            + "        \"specific\": { \"id\": \"c3\", \"type\": \"Class\" }\n"
            + "      },\n"
            + "      {\n"
            + "        \"id\": \"gs1\",\n"
            + "        \"type\": \"GeneralizationSet\",\n"
            + "        \"generalizations\": [\n"
            + "          { \"id\": \"g2\", \"type\": \"Generalization\" },\n"
            + "          { \"id\": \"g1\", \"type\": \"Generalization\" }\n"
            + "        ]\n"
            + "      }\n"
            + "    ]\n"
            + "  }\n"
            + "}";

    Project project = mapper.readValue(json, Project.class);

    Generalization g1 = project.getGeneralizationById("g1").orElse(null);
    assertThat(g1).isNotNull();

    Generalization g2 = project.getGeneralizationById("g2").orElse(null);
    assertThat(g2).isNotNull();

    GeneralizationSet gs = project.getGeneralizationSetById("gs1").orElse(null);
    assertThat(gs).isNotNull();

    assertThat(gs.getGeneralizations()).containsExactly(g1, g2);
  }

  @Test
  void shouldResolveClassViewReference() throws IOException {
    String json =
        "{\n"
            + "  \"id\": \"pj1\",\n"
            + "  \"type\": \"Project\",\n"
            + "  \"model\": {\n"
            + "    \"id\": \"pk1\",\n"
            + "    \"type\": \"Package\",\n"
            + "    \"contents\": [ { \"id\": \"c1\", \"name\": \"Agent\", \"type\": \"Class\"} ]\n"
            + "  },\n"
            + "  \"diagrams\": [ { \n"
            + "    \"id\": \"dg1\", \n"
            + "    \"name\": \"Class Diagram1\", \n"
            + "    \"type\": \"Diagram\",\n"
            + "    \"contents\": [ {\n"
            + "      \"id\": \"cv1\",\n"
            + "      \"type\": \"ClassView\",\n"
            + "      \"modelElement\": { \"id\": \"c1\", \"type\": \"Class\" }\n"
            + "    } ]\n"
            + "  } ]\n"
            + "}";

    Project project = mapper.readValue(json, Project.class);

    ClassView cv1 = project.getClassViewById("cv1").orElse(null);
    assertThat(cv1).isNotNull();

    Class c1 = project.getClassById("c1").orElse(null);
    assertThat(c1).isNotNull();

    assertThat(cv1.getModelElement()).isEqualTo(c1);
  }

  @Test
  void shouldResolveRelationViewReferences() throws IOException {
    String json =
        "{\n"
            + "  \"id\": \"pj1\",\n"
            + "  \"type\": \"Project\",\n"
            + "  \"model\": {\n"
            + "    \"id\": \"pk1\",\n"
            + "    \"type\": \"Package\",\n"
            + "    \"contents\": [\n"
            + "      { \"id\": \"c1\", \"name\": \"Person\", \"type\": \"Class\" },\n"
            + "      { \"id\": \"re1\", \"name\": \"knows\", \"type\": \"Relation\",\n"
            + "        \"properties\": [\n"
            + "          { \"id\": \"pr1\", \"name\": \"knower\", \"type\": \"Property\",\n"
            + "            \"propertyType\": { \"id\": \"c1\", \"type\": \"Class\" } },\n"
            + "          { \"id\": \"pr2\", \"name\": \"known\", \"type\": \"Property\",\n"
            + "            \"propertyType\": { \"id\": \"c1\", \"type\": \"Class\" } } ] }\n"
            + "    ]\n"
            + "  },\n"
            + "  \"diagrams\": [\n"
            + "    { \"id\": \"dg1\", \"name\": \"Class Diagram1\", \"type\": \"Diagram\",\n"
            + "      \"contents\": [\n"
            + "        { \"id\": \"cv1\", \"type\": \"ClassView\",\n"
            + "          \"modelElement\": { \"id\": \"c1\", \"type\": \"Class\" } },\n"
            + "        { \"id\": \"rv1\", \"type\": \"RelationView\",\n"
            + "          \"modelElement\": { \"id\": \"re1\", \"type\": \"Relation\" },\n"
            + "          \"source\": { \"id\": \"cv1\", \"type\": \"ClassView\" },\n"
            + "          \"target\": { \"id\": \"cv1\", \"type\": \"ClassView\" } }\n"
            + "      ]\n"
            + "    }\n"
            + "  ]\n"
            + "}";

    Project project = mapper.readValue(json, Project.class);

    RelationView rv1 = project.getRelationViewById("rv1").orElse(null);
    assertThat(rv1).isNotNull();
    assertThat(rv1.getSource().getModelElement().getFirstName()).hasValue("Person");
    assertThat(rv1.getTarget().getModelElement().getFirstName()).hasValue("Person");

    ClassView cv1 = project.getClassViewById("cv1").orElse(null);
    assertThat(cv1).isNotNull();

    assertThat(rv1.getSource()).isEqualTo(cv1);
    assertThat(rv1.getTarget()).isEqualTo(cv1);

    Relation r1 = project.getRelationById("re1").orElse(null);
    assertThat(r1).isNotNull();

    assertThat(rv1.getModelElement()).isEqualTo(r1);
  }

  @Test
  void shouldResolveGeneralizationViewReferences() throws IOException {
    String json =
        "{\n"
            + "  \"id\": \"pj1\",\n"
            + "  \"type\": \"Project\",\n"
            + "  \"model\": {\n"
            + "    \"id\": \"pk1\",\n"
            + "    \"type\": \"Package\",\n"
            + "    \"contents\": [\n"
            + "      { \"id\": \"c1\", \"name\": \"Agent\", \"type\": \"Class\" },\n"
            + "      { \"id\": \"c2\", \"name\": \"Person\", \"type\": \"Class\" },\n"
            + "      { \"id\": \"g1\", \"type\": \"Generalization\",\n"
            + "        \"general\": { \"id\": \"c1\", \"type\": \"Class\" },\n"
            + "        \"specific\": { \"id\": \"c2\", \"type\": \"Class\" } }\n"
            + "    ]\n"
            + "  },\n"
            + "  \"diagrams\": [\n"
            + "    { \"id\": \"dg1\", \"type\": \"Diagram\",\n"
            + "      \"contents\": [\n"
            + "        { \"id\": \"cv1\", \"type\": \"ClassView\",\n"
            + "          \"modelElement\": { \"id\": \"c1\", \"type\": \"Class\" } },\n"
            + "        { \"id\": \"cv2\", \"type\": \"ClassView\",\n"
            + "          \"modelElement\": { \"id\": \"c2\", \"type\": \"Class\" } },\n"
            + "        { \"id\": \"gv1\", \"type\": \"GeneralizationView\",\n"
            + "          \"modelElement\": { \"id\": \"g1\", \"type\": \"Generalization\" },\n"
            + "          \"source\": { \"id\": \"cv1\", \"type\": \"ClassView\" },\n"
            + "          \"target\": { \"id\": \"cv2\", \"type\": \"ClassView\" } }\n"
            + "      ]\n"
            + "    }\n"
            + "  ]\n"
            + "}";

    Project project = mapper.readValue(json, Project.class);

    GeneralizationView gv = project.getGeneralizationViewById("gv1").orElse(null);
    assertThat(gv).isNotNull();
    assertThat(gv.getSource().getModelElement().getFirstName()).hasValue("Agent");
    assertThat(gv.getTarget().getModelElement().getFirstName()).hasValue("Person");

    ClassView cv1 = project.getClassViewById("cv1").orElse(null);
    assertThat(cv1).isNotNull();
    ClassView cv2 = project.getClassViewById("cv2").orElse(null);
    assertThat(cv2).isNotNull();

    assertThat(gv.getSource()).isEqualTo(cv1);
    assertThat(gv.getTarget()).isEqualTo(cv2);

    Generalization g1 = project.getGeneralizationById("g1").orElse(null);
    assertThat(g1).isNotNull();
    assertThat(gv.getModelElement()).isEqualTo(g1);
  }
}
