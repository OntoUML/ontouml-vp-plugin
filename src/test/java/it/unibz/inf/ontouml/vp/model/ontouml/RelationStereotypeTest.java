package it.unibz.inf.ontouml.vp.model.ontouml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth8.assertThat;

class RelationStereotypeTest {

   @Test
   @DisplayName("Should find relation stereotype by name: material")
   void findMode() {
      assertThat(RelationStereotype.findByName("material")).hasValue(RelationStereotype.MATERIAL);
   }

   @Test
   @DisplayName("Should find relation stereotype by name: externalDependence")
   void findRoleMixin() {
      assertThat(RelationStereotype.findByName("externalDependence")).hasValue(RelationStereotype.EXTERNAL_DEPENDENCE);
   }

   @Test
   @DisplayName("Should find relation stereotype by name: componentOf")
   void findEvent() {
      assertThat(RelationStereotype.findByName("componentOf")).hasValue(RelationStereotype.COMPONENT_OF);
   }

   @Test
   @DisplayName("Should not find relation stereotype if not correctly capitalized")
   void shouldNotFindRoleMixin() {
      assertThat(RelationStereotype.findByName("MATERIAL")).isEmpty();
   }

   @Test
   @DisplayName("Should not find relation stereotype if spaces are included")
   void shouldNotFindWithSpaces() {
      assertThat(RelationStereotype.findByName("component Of")).isEmpty();
   }
}