package it.unibz.inf.ontouml.vp.listeners;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IProjectModelListener;
import com.vp.plugin.model.factory.IModelElementFactory;
import java.util.Iterator;

public class ProjectModelListener implements IProjectModelListener {

  private ModelListener modelListener;

  ProjectModelListener() {
    modelListener = new ModelListener();
  }

  @Override
  public void modelAdded(IProject project, IModelElement modelElement) {
    try {
      addListenerToModelElement(modelElement);
    } catch (Exception e) {
      System.err.println("An error ocurred while adding model element.");
      e.printStackTrace();
    }
  }

  @Override
  public void modelRemoved(IProject project, IModelElement modelElement) {
    try {
      removeListenerFromModelElement(modelElement);
    } catch (Exception e) {
      System.err.println("An error ocurred while removing model element.");
      e.printStackTrace();
    }
  }

  private void addListenerToModelElement(IModelElement modelElement) {
    if (modelElement instanceof IClass || modelElement instanceof IGeneralization) {
      modelElement.addPropertyChangeListener(modelListener);
    }
  }

  private void removeListenerFromModelElement(IModelElement modelElement) {
    if (modelElement instanceof IClass || modelElement instanceof IGeneralization) {
      modelElement.removePropertyChangeListener(modelListener);
    }
  }

  public void addListenersToModelElements() {
    final IProject project = ApplicationManager.instance().getProjectManager().getProject();
    final String[] desiredElements = {
      IModelElementFactory.MODEL_TYPE_CLASS, IModelElementFactory.MODEL_TYPE_GENERALIZATION
    };
    final Iterator<?> iter = project.allLevelModelElementIterator(desiredElements);

    while (iter != null && iter.hasNext()) {
      final IModelElement modelElement = (IModelElement) iter.next();
      modelElement.addPropertyChangeListener(modelListener);
    }
  }
}
