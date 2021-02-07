package it.unibz.inf.ontouml.vp.model.ontouml.view;

import it.unibz.inf.ontouml.vp.model.ontouml.model.Package;

public class PackageView extends NodeView<Package, Rectangle> {
  public PackageView(String id, Package pkg) {
    super(id, pkg);
  }

  public PackageView(Package clazz) {
    this(null, clazz);
  }

  public PackageView() {
    this(null, null);
  }

  @Override
  public String getType() {
    return "PackageView";
  }

  @Override
  Rectangle createShape() {
    return new Rectangle();
  }
}
