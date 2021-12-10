package it.unibz.inf.ontouml.vp.listeners;

import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IProjectModelListener;
import com.vp.plugin.model.factory.IModelElementFactory;
import it.unibz.inf.ontouml.vp.utils.ApplicationManagerUtils;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class ProjectModelListener implements IProjectModelListener {

  private static final Set<String> typesOfModelElementOfInterest = Set.of(
      IModelElementFactory.MODEL_TYPE_ASSOCIATION,
      IModelElementFactory.MODEL_TYPE_CLASS,
      IModelElementFactory.MODEL_TYPE_GENERALIZATION);

  private final ModelListener modelListener;

  ProjectModelListener() {
    modelListener = new ModelListener();
  }

  @Override
  public void modelAdded(IProject project, IModelElement modelElement) {
    try {
      addListenerToModelElement(modelElement);
    } catch (Exception e) {
      System.err.println("An error occurred while adding model element.");
      e.printStackTrace();
    }
  }

  @Override
  public void modelRemoved(IProject project, IModelElement modelElement) {
    try {
      removeListenerFromModelElement(modelElement);
    } catch (Exception e) {
      System.err.println("An error occurred while removing model element.");
      e.printStackTrace();
    }
  }

  private void addListenerToModelElement(IModelElement modelElement) {
    if (isModelElementOfInterest(modelElement)) {
      modelElement.addPropertyChangeListener(modelListener);
    }
  }

  private void removeListenerFromModelElement(IModelElement modelElement) {
    if (isModelElementOfInterest(modelElement)) {
      modelElement.removePropertyChangeListener(modelListener);
    }
  }

  public void addListenersToModelElements() {
    getAllLevelInterestModelElements(ApplicationManagerUtils.getCurrentProject())
        .forEach(element -> element.addPropertyChangeListener(modelListener));
  }

  private boolean isModelElementOfInterest(IModelElement element) {
    return element != null && typesOfModelElementOfInterest.contains(element.getModelType());
  }

  private Set<IModelElement> getAllLevelInterestModelElements(IProject project) {
    return !(project.allLevelModelElementCount() > 0) ? Collections.emptySet() :
        Set.of(project.toAllLevelModelElementArray()).stream()
            .filter(this::isModelElementOfInterest)
            .collect(Collectors.toSet());
  }
}
