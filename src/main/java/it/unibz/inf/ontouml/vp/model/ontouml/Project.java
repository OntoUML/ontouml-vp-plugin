package it.unibz.inf.ontouml.vp.model.ontouml;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import it.unibz.inf.ontouml.vp.model.ontouml.deserialization.ProjectDeserializer;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Package;
import it.unibz.inf.ontouml.vp.model.ontouml.serialization.ProjectSerializer;
import it.unibz.inf.ontouml.vp.model.ontouml.view.Diagram;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@JsonSerialize(using = ProjectSerializer.class)
@JsonDeserialize(using = ProjectDeserializer.class)
public class Project extends OntoumlElement
    implements ModelElementContainer, DiagramElementContainer {
  private Package model;
  private List<Diagram> diagrams = new ArrayList<>();

  public Project(String id, MultilingualText name) {
    super(null, id, name);
    setProject(this);
  }

  public Project(String id, String name) {
    super(null, id, new MultilingualText(name));
    setProject(this);
  }

  public Project(MultilingualText name) {
    this(null, name);
  }

  public Project() {
    this(null, (MultilingualText) null);
  }

  @Override
  public String getType() {
    return "Project";
  }

  public Optional<Package> getModel() {
    return Optional.ofNullable(model);
  }

  public Package createModel() {
    return createModel(null, "Model");
  }

  public Package createModel(String modelName) {
    return createModel(null, modelName);
  }

  public Package createModel(String id, String modelName) {
    Package model = new Package(id, modelName);
    setModel(model);
    return model;
  }

  public void setModel(Package model) {
    this.model = model;

    if (model != null) model.setContainer(this);
  }

  public boolean hasModel() {
    return getModel().isPresent();
  }

  public List<Diagram> getDiagrams() {
    return new ArrayList<>(diagrams);
  }

  public void addDiagram(Diagram diagram) {
    if (diagram == null) return;

    diagram.setContainer(this);
    diagrams.add(diagram);
  }

  public void addDiagrams(List<Diagram> diagrams) {
    if (diagrams == null) return;

    diagrams.forEach(d -> addDiagram(d));
  }

  public void setDiagrams(List<Diagram> diagrams) {
    this.diagrams.clear();

    if (diagrams == null) return;

    addDiagrams(diagrams);
  }

  public boolean hasDiagram() {
    return diagrams != null && diagrams.size() > 0;
  }

  @Override
  public List<OntoumlElement> getContents() {
    List<OntoumlElement> contents = new ArrayList<>();

    contents.addAll(diagrams);

    if (getModel().isPresent()) contents.add(getModel().get());

    return contents;
  }
}
