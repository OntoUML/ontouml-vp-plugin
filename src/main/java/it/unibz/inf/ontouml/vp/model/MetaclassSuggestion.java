package it.unibz.inf.ontouml.vp.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Result of an LLM-based OntoUML class stereotype suggestion, deserialized from the JSON
 * object the LLM is instructed to return (see prompts/ufo_decision_tree_prompt.md).
 */
public class MetaclassSuggestion {

  @SerializedName("stereotype")
  private String stereotype;

  @SerializedName("decisionPath")
  private List<String> decisionPath;

  @SerializedName("rationale")
  private String rationale;

  @SerializedName("alternativeStereotype")
  private String alternativeStereotype;

  /** Why the stereotype already on the class does not hold; null when there is none or it fits. */
  @SerializedName("currentStereotypeIssue")
  private String currentStereotypeIssue;

  public String getStereotype() {
    return stereotype;
  }

  public List<String> getDecisionPath() {
    return decisionPath;
  }

  public String getRationale() {
    return rationale;
  }

  public String getAlternativeStereotype() {
    return alternativeStereotype;
  }

  public String getCurrentStereotypeIssue() {
    return currentStereotypeIssue;
  }

  public boolean hasCurrentStereotypeIssue() {
    return currentStereotypeIssue != null && !currentStereotypeIssue.trim().isEmpty();
  }
}
