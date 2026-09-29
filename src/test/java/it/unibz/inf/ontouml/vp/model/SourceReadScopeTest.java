package it.unibz.inf.ontouml.vp.model;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SourceReadScopeTest {

  @Test
  @DisplayName("\"Don't retrieve\" includes nothing")
  void none() {
    for (SourceScope scope : SourceScope.values()) {
      assertThat(SourceReadScope.NONE.includes(scope)).isFalse();
    }
  }

  @Test
  @DisplayName("\"only the diagram\" includes only the diagram origin")
  void diagramOnly() {
    assertThat(SourceReadScope.DIAGRAM_ONLY.includes(SourceScope.DIAGRAM)).isTrue();
    assertThat(SourceReadScope.DIAGRAM_ONLY.includes(SourceScope.OWN_CLASS)).isFalse();
    assertThat(SourceReadScope.DIAGRAM_ONLY.includes(SourceScope.RELATED_CLASS)).isFalse();
    assertThat(SourceReadScope.DIAGRAM_ONLY.includes(SourceScope.MODEL)).isFalse();
  }

  @Test
  @DisplayName("\"only the current class\" includes only the class' own origin")
  void currentClassOnly() {
    assertThat(SourceReadScope.CURRENT_CLASS_ONLY.includes(SourceScope.OWN_CLASS)).isTrue();
    assertThat(SourceReadScope.CURRENT_CLASS_ONLY.includes(SourceScope.DIAGRAM)).isFalse();
    assertThat(SourceReadScope.CURRENT_CLASS_ONLY.includes(SourceScope.RELATED_CLASS)).isFalse();
    assertThat(SourceReadScope.CURRENT_CLASS_ONLY.includes(SourceScope.MODEL)).isFalse();
  }

  @Test
  @DisplayName("\"all neighbours\" includes every origin")
  void allNeighbours() {
    for (SourceScope scope : SourceScope.values()) {
      assertThat(SourceReadScope.ALL_NEIGHBOURS.includes(scope)).isTrue();
    }
  }

  @Test
  @DisplayName("an unknown persisted name falls back to the default, widest scope")
  void unknownNameFallsBack() {
    assertThat(SourceReadScope.fromName("something_removed")).isEqualTo(SourceReadScope.ALL_NEIGHBOURS);
  }
}
