package it.unibz.inf.ontouml.vp.model;

import com.google.gson.annotations.SerializedName;

/** One allowed answer of a decision node: it either ends the walk or leads to the next node. */
public class DecisionOption {

  @SerializedName("answer")
  private String answer;

  /** Id of the node to visit next; null when this answer settles the classification. */
  @SerializedName("next")
  private String next;

  /** OntoUML class stereotype reached by this answer; null when the walk continues. */
  @SerializedName("stereotype")
  private String stereotype;

  public String getAnswer() {
    return answer;
  }

  public String getNext() {
    return next;
  }

  public String getStereotype() {
    return stereotype;
  }

  public boolean isLeaf() {
    return stereotype != null && !stereotype.trim().isEmpty();
  }
}
