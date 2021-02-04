package it.unibz.inf.ontouml.vp.model.uml2ontouml;

import com.vp.plugin.model.*;
import it.unibz.inf.ontouml.vp.model.ontouml.*;
import it.unibz.inf.ontouml.vp.model.ontouml.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.Package;

public class ReferenceTransformer {
  public static OntoumlElement transformStub(IModelElement source) {
    OntoumlElement stub = null;

    if (source instanceof IClass || source instanceof IDataType) {
      stub = new Class();
    } else if (source instanceof IAssociation || source instanceof IAssociationClass) {
      stub = new Relation();
    } else if (source instanceof IPackage) {
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
}
