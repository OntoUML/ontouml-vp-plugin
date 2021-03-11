package it.unibz.inf.ontouml.vp.model;

import it.unibz.inf.ontouml.vp.model.ontouml.Project;
import java.util.List;

public class ModularizationServiceResult extends ServiceResult<Project> {

  public ModularizationServiceResult() {
    super();
  }

  public ModularizationServiceResult(Project result, List<ServiceIssue> issues) {
    super(result, issues);
  }

  @Override
  public String getMessage() {
    return "The modularization request has concluded";
  }
}
