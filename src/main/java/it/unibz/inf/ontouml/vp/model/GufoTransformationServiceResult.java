package it.unibz.inf.ontouml.vp.model;

import java.util.List;

public class GufoTransformationServiceResult extends ServiceResult<String> {

  public GufoTransformationServiceResult(String result, List<ServiceIssue> issues) {
    super(result, issues);
  }

  public GufoTransformationServiceResult() {
    super();
  }
}
