package it.unibz.inf.ontouml.vp.model;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.Gson;
import it.unibz.inf.ontouml.vp.utils.Stereotype;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class DecisionTreeTest {

  private static List<String> problemsOf(String json) {
    return new Gson()
        .fromJson(json, DecisionTree.class)
        .findProblems(Stereotype.getOntoUMLClassStereotypeNames());
  }

  @Test
  void bundledTreeHasNoProblems() throws Exception {
    try (InputStream in = getClass().getResourceAsStream("/prompts/ufo_decision_tree.json")) {
      final DecisionTree tree =
          new Gson()
              .fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), DecisionTree.class);

      assertThat(tree.findProblems(Stereotype.getOntoUMLClassStereotypeNames())).isEmpty();
    }
  }

  @Test
  void acceptsANewNodeThatEndsInAValidStereotype() {
    final String json =
        "{\"startNode\":\"a\",\"nodes\":{"
            + "\"a\":{\"question\":\"q\",\"options\":["
            + "{\"answer\":\"x\",\"next\":\"b\"},{\"answer\":\"y\",\"stereotype\":\"kind\"}]},"
            + "\"b\":{\"question\":\"q\",\"options\":[{\"answer\":\"z\",\"stereotype\":\"role\"}]}}}";

    assertThat(problemsOf(json)).isEmpty();
  }

  @Test
  void rejectsAStereotypeOutsideOntoUML() {
    final String json =
        "{\"startNode\":\"a\",\"nodes\":{\"a\":{\"question\":\"q\",\"options\":["
            + "{\"answer\":\"x\",\"stereotype\":\"Role\"}]}}}";

    assertThat(problemsOf(json)).hasSize(1);
    assertThat(problemsOf(json).get(0)).contains("not an OntoUML class stereotype");
  }

  @Test
  void rejectsANextThatPointsToNoNode() {
    final String json =
        "{\"startNode\":\"a\",\"nodes\":{\"a\":{\"question\":\"q\",\"options\":["
            + "{\"answer\":\"x\",\"next\":\"missing\"}]}}}";

    assertThat(problemsOf(json).get(0)).contains("does not exist");
  }

  @Test
  void rejectsOptionsThatNeitherLeadNorEndOrDoBoth() {
    final String json =
        "{\"startNode\":\"a\",\"nodes\":{\"a\":{\"question\":\"q\",\"options\":["
            + "{\"answer\":\"x\"},"
            + "{\"answer\":\"y\",\"next\":\"a\",\"stereotype\":\"kind\"}]}}}";

    assertThat(problemsOf(json)).hasSize(2);
  }

  @Test
  void rejectsMissingStartNodeAndEmptyNodes() {
    final String json = "{\"startNode\":\"nope\",\"nodes\":{\"a\":{\"question\":\"q\"}}}";

    assertThat(problemsOf(json)).hasSize(2);
  }
}
