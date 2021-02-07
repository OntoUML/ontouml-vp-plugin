package it.unibz.inf.ontouml.vp.model.ontouml;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;

import it.unibz.inf.ontouml.vp.model.ontouml.model.*;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Package;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PackageTest {
  Project project = new Project();
  it.unibz.inf.ontouml.vp.model.ontouml.model.Package model = project.createModel();
  Package pkg = model.createPackage();
  Class clazz = model.createClass();
  Relation relation = model.createRelation(clazz, clazz);
  Generalization generalization = model.createGeneralization(clazz, clazz);
  GeneralizationSet genSet = model.createGeneralizationSet(generalization);

  @Test
  @DisplayName("createClass() should set the caller as the container of the new class.")
  void shouldPropagateCallerAsContainer() {
    assertThat(clazz.getContainer()).hasValue(model);
  }

  @Test
  @DisplayName("createClass() should set propagate the caller's project to the new class.")
  void shouldPropagateCallerAsProject() {
    assertThat(clazz.getProject()).hasValue(project);
  }

  @Test
  @DisplayName("createClass() should create a class without attributes.")
  void shouldHaveNoAttributes() {
    assertThat(clazz.getAttributes()).hasSize(0);
  }

  @Test
  @DisplayName("createEnumeration() should create a class with the Enumeration stereotype.")
  void shouldHaveEnumerationStereotype() {
    Class clazz = model.createEnumeration(new String[] {"Red", "Blue", "Green"});
    assertThat(clazz.getOntoumlStereotype()).hasValue(ClassStereotype.ENUMERATION);
  }

  @Test
  @DisplayName("createEnumeration() should create a class with the Enumeration stereotype.")
  void shouldCreateLiterals() {
    Class clazz = model.createEnumeration(new String[] {"Red", "Blue", "Green"});
    List<Literal> literals = clazz.getLiterals();

    assertThat(literals).hasSize(3);
    assertThat(literals).containsNoDuplicates();

    assertThat(literals.stream().map(l -> l.getFirstName().orElse("")))
        .containsExactly("Red", "Blue", "Green");
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
  @DisplayName(
      "createGeneralizationSet() should set propagate the caller's project to the new class.")
  void shouldPropagateCallerAsProjectOfGeneralizationSet() {
    assertThat(genSet.getProject()).hasValue(project);
  }

  @Test
  void getContentsShouldReturnChildren() {
    assertThat(model.getContents()).containsExactly(pkg, clazz, relation, generalization, genSet);
  }
}
