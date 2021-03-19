package it.unibz.inf.ontouml.vp.model;

import java.util.ArrayList;
import java.util.List;

public class VerificationServiceResult extends ServiceResult<List<ServiceIssue>> {

  public VerificationServiceResult() {
    setResult(new ArrayList<>());
  }

  public VerificationServiceResult(List<ServiceIssue> result, List<ServiceIssue> issues) {
    super(result, issues);
  }

  @Override
  public List<ServiceIssue> getResult() {
    return super.getResult();
  }

  @Override
  public void setResult(List<ServiceIssue> result) {
    super.setResult(result != null ? result : new ArrayList<>());
  }

  @Override
  public List<ServiceIssue> getIssues() {
    return super.getIssues();
  }

  @Override
  public void setIssues(List<ServiceIssue> issues) {
    super.setIssues(issues);
  }

  public String getMessage() {
    int numberOfVerificationIssues = getResult().size();
    return numberOfVerificationIssues == 0
        ? "No syntactical issues were found."
        : numberOfVerificationIssues + " syntactical issues were found.";
  }
}
