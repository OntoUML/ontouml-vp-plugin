package it.unibz.inf.ontouml.vp.model;

import java.util.ArrayList;
import java.util.List;

public abstract class ServiceResult<T> {

  private T result;
  private List<ServiceIssue> issues;

  public ServiceResult() {
    setResult(null);
    setIssues(new ArrayList<>());
  }

  public ServiceResult(T result, List<ServiceIssue> issues) {
    this.result = result;
    this.issues = issues;
  }

  public T getResult() {
    return result;
  }

  public void setResult(T result) {
    this.result = result;
  }

  public List<ServiceIssue> getIssues() {
    return issues;
  }

  public void setIssues(List<ServiceIssue> issues) {
    this.issues = issues != null ? issues : new ArrayList<>();
  }
}
