package it.unibz.inf.ontouml.vp.model.ontouml;

import com.google.common.truth.Correspondence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import static it.unibz.inf.ontouml.vp.model.ontouml.OntoumlUtils.*;

class PackageTest {
   Project project = new Project();
   Package model = project.createModel();
   Package pkg = model.createPackage();
   Class clas = model.createClass();
   Relation relation = model.createRelation(clas, clas);
   Generalization generalization = model.createGeneralization(clas, clas);
   GeneralizationSet genSet = model.createGeneralizationSet(generalization);

   @Test
   @DisplayName("createClass() should set the caller as the container of the new class.")
   void shouldPropagateCallerAsContainer() {
      assertThat(clas.getContainer()).hasValue(model);
   }

   @Test
   @DisplayName("createClass() should set propagate the caller's project to the new class.")
   void shouldPropagateCallerAsProject() {
      assertThat(clas.getProject()).hasValue(project);
   }

   @Test
   @DisplayName("createClass() should create a class without attributes.")
   void shouldHaveNoAttributes() {
      assertThat(clas.getAttributes()).hasSize(0);
   }

   @Test
   @DisplayName("createEnumeration() should create a class with the Enumeration stereotype.")
   void shouldHaveEnumerationStereotype() {
      Class clas = model.createEnumeration(new String[]{"Red", "Blue", "Green"});
      assertThat(clas.getOntoumlStereotype()).hasValue(ClassStereotype.ENUMERATION);
   }

   @Test
   @DisplayName("createEnumeration() should create a class with the Enumeration stereotype.")
   void shouldCreateLiterals() {
      Class clas = model.createEnumeration(new String[]{"Red", "Blue", "Green"});
      List<Literal> literals = clas.getLiterals();

      assertThat(clas.getLiterals()).hasSize(3);
      assertThat(literals).containsNoDuplicates();
//      assertThat(literals).comparingElementsUsing( Correspondence.(l -> l.getfi ))

//      assertTrue(contains(literals, l -> "Red".equals(l.getFirstName().get())));
//      assertTrue(contains(literals, l -> "Blue".equals(l.getFirstName().get())));
//      assertTrue(contains(literals, l -> "Green".equals(l.getFirstName().get())));
   }

   @Test
   @DisplayName("createRelation() should set the caller as the container of the new class.")
   void shouldPropagateCallerAsContainerOfRelation() {
      assertThat(relation.getContainer()).hasValue(model);
      ;
   }

   @Test
   @DisplayName("createRelation() should set propagate the caller's project to the new class.")
   void shouldPropagateCallerAsProjectOfRelation() {
      assertThat(relation.getProject()).hasValue(project);
      ;
   }

   @Test
   @DisplayName("createRelation() should create a class without attributes.")
   void shouldCreateBinaryRelation() {
      assertThat(relation.getProperties()).hasSize(2);
   }

   @Test
   @DisplayName("createPackage() should set the caller as the container of the new class.")
   void shouldPropagateCallerAsContainerOfPackage() {
      assertThat(pkg.getContainer()).hasValue(model);
   }

   @Test
   @DisplayName("createPackage() should set propagate the caller's project to the new class.")
   void shouldPropagateCallerAsProjectOfPackage() {
      assertThat(pkg.getProject()).hasValue(project);
   }

   @Test
   @DisplayName("createGeneralization() should set the caller as the container of the new class.")
   void shouldPropagateCallerAsContainerOfGeneralization() {
      assertThat(generalization.getContainer()).hasValue(model);
   }

   @Test
   @DisplayName("createGeneralization() should set propagate the caller's project to the new class.")
   void shouldPropagateCallerAsProjectOfGeneralization() {
      assertThat(generalization.getProject()).hasValue(project);
   }

   @Test
   @DisplayName("createGeneralizationSet() should set the caller as the container of the new class.")
   void shouldPropagateCallerAsContainerOfGeneralizationSet() {
      assertThat(genSet.getContainer()).hasValue(model);
   }

   @Test
   @DisplayName("createGeneralizationSet() should set propagate the caller's project to the new class.")
   void shouldPropagateCallerAsProjectOfGeneralizationSet() {
      assertThat(genSet.getProject()).hasValue(project);
   }

}