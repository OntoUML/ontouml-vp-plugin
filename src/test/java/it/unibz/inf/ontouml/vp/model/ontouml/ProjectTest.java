package it.unibz.inf.ontouml.vp.model.ontouml;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;

import it.unibz.inf.ontouml.vp.model.ontouml.model.*;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Package;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProjectTest {
  Project project = new Project();
  Package model = project.createModel();

  @Test
  @DisplayName("Upon construction, the project field of a Project instance is the instance itself.")
  void getProjectOnProejct() {
    assertThat(project.getProject()).hasValue(project);
  }

  @Test
  @DisplayName("createModel() should create and return a new Package.")
  void createModelShouldReturnNewPackage() {
    assertThat(model).isNotNull();
  }

  @Test
  @DisplayName("createModel() should create a new Package and set it as the project model.")
  void createModelShouldSetProjectModel() {
    assertThat(project.getModel()).hasValue(model);
  }

  @Test
  @DisplayName("createModel() should create a Package with the caller as its container.")
  void createdModelShouldHaveCallerAsContainer() {
    assertThat(model.getContainer()).hasValue(project);
  }

  @Test
  @DisplayName("createModel() should create a Package with the caller as its project.")
  void createdModelShouldHaveCallerAsProject() {
    assertThat(model.getProject()).hasValue(project);
  }

  @Test
  @DisplayName("setModel() should set the container of the argument.")
  void setModelShouldSetContainerOfArgument() {
    Package anotherModel = new Package();
    project.setModel(anotherModel);

    assertThat(anotherModel.getContainer()).hasValue(project);
  }

  @Test
  @DisplayName("setModel() should set the project of the argument.")
  void setModelShouldSetProjectOfArgument() {
    Package anotherModel = new Package();
    project.setModel(anotherModel);

    assertThat(anotherModel.getProject()).hasValue(project);
  }

  @Test
  @DisplayName(
      "setModel() should propagate the caller as the project of the all direct contents of the"
          + " argument.")
  void setModelShouldPropagateProject() {
    Package anotherModel = new Package();
    Package firstChild = anotherModel.createPackage();
    Package secondChild = anotherModel.createPackage();

    project.setModel(anotherModel);

    assertThat(firstChild.getProject()).hasValue(project);
    assertThat(secondChild.getProject()).hasValue(project);
  }

  @Test
  @DisplayName(
      "setModel() should propagate the caller as the project of the all direct/indirect contents"
          + " of the argument.")
  void setModelShouldPropagateProjectToAll() {
    Package anotherModel = new Package();
    Class c1 = anotherModel.createClass();
    Property p1 = c1.createAttribute("name", c1);

    Package pk1 = anotherModel.createPackage();
    Class c2 = pk1.createEnumeration(new String[] {"Red", "Blue", "Green"});
    Generalization g1 = pk1.createGeneralization(c1, c2);
    Relation r1 = pk1.createRelation(c1, c2);

    Package pk2 = pk1.createPackage();

    project.setModel(anotherModel);

    Stream.of(c1, c2, p1, g1, r1, pk2)
        .forEach(element -> assertThat(element.getProject()).hasValue(project));
  }

  @Test
  void getAllContentsShouldReturnClassAttributes() {
    Class person = model.createKind("c1", "Person");
    Property name = person.createAttribute("p1", "name", null);

    assertThat(project.getAllContents()).contains(name);
  }
}
