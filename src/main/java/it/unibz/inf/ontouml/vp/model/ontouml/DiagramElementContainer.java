package it.unibz.inf.ontouml.vp.model.ontouml;

import it.unibz.inf.ontouml.vp.model.ontouml.view.*;
import java.util.List;
import java.util.Optional;

public interface DiagramElementContainer extends ElementContainer {

  default List<ElementView> getAllDiagramElements() {
    return getAllContentsByType(ElementView.class);
  }

  default List<ConnectorView> getAllConnectorViews() {
    return getAllContentsByType(ConnectorView.class);
  }

  default List<ClassView> getAllClassViews() {
    return getAllContentsByType(ClassView.class);
  }

  default List<PackageView> getAllPackageViews() {
    return getAllContentsByType(PackageView.class);
  }

  default List<RelationView> getAllRelationViews() {
    return getAllContentsByType(RelationView.class);
  }

  default List<GeneralizationView> getAllGeneralizationViews() {
    return getAllContentsByType(GeneralizationView.class);
  }

  default List<GeneralizationSetView> getAllGeneralizationSetViews() {
    return getAllContentsByType(GeneralizationSetView.class);
  }

  default Optional<ClassView> getClassViewById(String id) {
    return getElementById(id, ClassView.class);
  }

  default Optional<RelationView> getRelationViewById(String id) {
    return getElementById(id, RelationView.class);
  }

  default Optional<GeneralizationView> getGeneralizationViewById(String id) {
    return getElementById(id, GeneralizationView.class);
  }

  default Optional<GeneralizationSetView> getGeneralizationSetViewById(String id) {
    return getElementById(id, GeneralizationSetView.class);
  }
}
