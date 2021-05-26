package it.unibz.inf.ontouml.vp.model;

import java.util.List;

public class Ontouml2DbServiceResult extends ServiceResult<Ontouml2DbResult> {

  public Ontouml2DbServiceResult(Ontouml2DbResult result, List<ServiceIssue> issues) {
    super(result, issues);
  }

  public Ontouml2DbServiceResult() {
    super();
  }

  @Override
  public String getMessage() {
    return "The mapping to database was concluded";
  }
}
