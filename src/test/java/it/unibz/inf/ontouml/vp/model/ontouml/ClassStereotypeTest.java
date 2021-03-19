package it.unibz.inf.ontouml.vp.model.ontouml;

import static com.google.common.truth.Truth8.assertThat;

import it.unibz.inf.ontouml.vp.model.ontouml.model.ClassStereotype;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ClassStereotypeTest {

  @Test
  @DisplayName("Should find class stereotype by name: mode")
  void findMode() {
    assertThat(ClassStereotype.findByName("mode")).hasValue(ClassStereotype.MODE);
  }

  @Test
  @DisplayName("Should find class stereotype by name: roleMixin")
  void findRoleMixin() {
    assertThat(ClassStereotype.findByName("roleMixin")).hasValue(ClassStereotype.ROLE_MIXIN);
  }

  @Test
  @DisplayName("Should find class stereotype by name: event")
  void findEvent() {
    assertThat(ClassStereotype.findByName("event")).hasValue(ClassStereotype.EVENT);
  }

  @Test
  @DisplayName("Should not find stereotype if not correctly capitalied")
  void shouldNotFindRoleMixin() {
    assertThat(ClassStereotype.findByName("RoleMixin")).isEmpty();
  }

  @Test
  @DisplayName("Should not find stereotype names with spaces")
  void shouldNotFindWithSpaces() {
    assertThat(ClassStereotype.findByName("phase Mixin")).isEmpty();
  }
}
