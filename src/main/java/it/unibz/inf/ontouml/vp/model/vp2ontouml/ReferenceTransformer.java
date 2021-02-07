package it.unibz.inf.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.connector.IAssociationClassUIModel;
import com.vp.plugin.diagram.connector.IAssociationUIModel;
import com.vp.plugin.diagram.connector.IGeneralizationUIModel;
import com.vp.plugin.diagram.shape.IClassUIModel;
import com.vp.plugin.diagram.shape.IGeneralizationSetUIModel;
import com.vp.plugin.diagram.shape.IModelUIModel;
import com.vp.plugin.diagram.shape.IPackageUIModel;
import com.vp.plugin.model.*;
import it.unibz.inf.ontouml.vp.model.ontouml.*;
import it.unibz.inf.ontouml.vp.model.ontouml.model.*;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Package;
import it.unibz.inf.ontouml.vp.model.ontouml.view.*;

public class ReferenceTransformer {
  public static ModelElement transformStub(IModelElement source) {
    ModelElement stub = null;

    if (source instanceof IClass || source instanceof IDataType) {
      stub = new Class();
    } else if (source instanceof IAssociation || source instanceof IAssociationClass) {
      stub = new Relation();
    } else if (source instanceof IPackage || source instanceof IModel) {
      stub = new Package();
    } else if (source instanceof IAttribute || source instanceof IAssociationEnd) {
      stub = new Property();
    } else if (source instanceof IGeneralization) {
      stub = new Generalization();
    } else if (source instanceof IGeneralizationSet) {
      stub = new GeneralizationSet();
    }

    if (stub != null) {
      stub.setId(source.getId());
    }

    return stub;
  }

  public static DiagramElement<?, ?> transformStub(IDiagramElement source) {
    DiagramElement<?, ?> stub = null;

    if (source instanceof IClassUIModel) {
      stub = new ClassView();
    } else if (source instanceof IAssociationUIModel
        || source instanceof IAssociationClassUIModel) {
      stub = new RelationView();
    } else if (source instanceof IPackageUIModel || source instanceof IModelUIModel) {
      stub = new PackageView();
    } else if (source instanceof IGeneralizationUIModel) {
      stub = new GeneralizationView();
    } else if (source instanceof IGeneralizationSetUIModel) {
      stub = new GeneralizationSetView();
    }

    if (stub != null) {
      stub.setId(source.getId());
    }

    return stub;
  }
}
