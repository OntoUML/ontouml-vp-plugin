package it.unibz.inf.ontouml.vp.model.ontouml.view;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import it.unibz.inf.ontouml.vp.model.ontouml.DiagramElementContainer;
import it.unibz.inf.ontouml.vp.model.ontouml.MultilingualText;
import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;
import it.unibz.inf.ontouml.vp.model.ontouml.deserialization.DiagramDeserializer;
import it.unibz.inf.ontouml.vp.model.ontouml.model.ModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.serialization.DiagramSerializer;
import java.util.*;

/** A set of {@link DiagramElement} instances use to describe a perspective of the OntoUML model. */
@JsonSerialize(using = DiagramSerializer.class)
@JsonDeserialize(using = DiagramDeserializer.class)
public class Diagram extends ViewElement implements DiagramElementContainer {

  private ModelElement owner;
  private Set<DiagramElement<?, ?>> contents = new HashSet<>();

  @Override
  public String getType() {
    return "Diagram";
  }

  public Diagram(String id, MultilingualText name) {
    super(id, name);
  }

  public Diagram(String id, String name) {
    super(id, new MultilingualText(name));
  }

  public Diagram() {
    this(null, (MultilingualText) null);
  }

  @Override
  public List<OntoumlElement> getContents() {
    return new ArrayList<>(contents);
  }

  public List<DiagramElement> getDiagramElements() {
    return new ArrayList<>(contents);
  }

  public void addElement(DiagramElement<?, ?> diagramElement) {
    if (diagramElement == null) return;

    diagramElement.setContainer(this);
    contents.add(diagramElement);
  }

  public void addElements(Collection<? extends DiagramElement<?, ?>> diagramElements) {
    if (diagramElements == null) return;
    diagramElements.stream().filter(Objects::nonNull).forEach(e -> addElement(e));
  }

  public void addElements(DiagramElement<?, ?>[] diagramElements) {
    if (diagramElements == null) return;
    addElements(List.of(diagramElements));
  }

  public void setContents(Collection<? extends DiagramElement<?, ?>> diagramElements) {
    this.contents.clear();
    if (diagramElements != null) addElements(diagramElements);
  }

  public void setContents(DiagramElement<?, ?>[] diagramElements) {
    if (diagramElements == null) return;
    setContents(List.of(diagramElements));
  }

  public void setOwner(ModelElement owner) {
    this.owner = owner;
  }

  public ModelElement getOwner() {
    return owner;
  }
}
