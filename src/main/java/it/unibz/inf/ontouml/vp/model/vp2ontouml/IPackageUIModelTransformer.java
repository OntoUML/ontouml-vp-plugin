package it.unibz.inf.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.diagram.IDiagramElement;
import com.vp.plugin.diagram.shape.IBasePackageUIModel;
import com.vp.plugin.diagram.shape.IModelUIModel;
import com.vp.plugin.diagram.shape.IPackageUIModel;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Package;
import it.unibz.inf.ontouml.vp.model.ontouml.view.PackageView;

public class IPackageUIModelTransformer {
  public static PackageView transform(IDiagramElement sourceElement) {
    if (!(sourceElement instanceof IPackageUIModel) && !(sourceElement instanceof IModelUIModel))
      return null;

    IBasePackageUIModel source = (IBasePackageUIModel) sourceElement;
    PackageView target = new PackageView();

    IDiagramElementTransformer.transform(source, target, Package.class);
    IShapeTransformer.transform(source, target);

    return target;
  }
}
