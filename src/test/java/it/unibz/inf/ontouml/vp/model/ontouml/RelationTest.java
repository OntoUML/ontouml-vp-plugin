package it.unibz.inf.ontouml.vp.model.ontouml;

import static com.google.common.truth.Truth.assertThat;

import it.unibz.inf.ontouml.vp.model.ontouml.model.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Property;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Relation;
import java.util.List;
import org.junit.jupiter.api.Test;

public class RelationTest {

  @Test
  void shouldReturnEndsAsContent() {
    Class person = Class.createKind("c1", "Person");
    Relation knows = Relation.createMaterial("r1", "knows", person, person);

    List<OntoumlElement> contents = knows.getContents();
    assertThat(contents).hasSize(2);

    contents.forEach(
        element -> {
          assertThat(element).isInstanceOf(Property.class);
        });
  }
}
