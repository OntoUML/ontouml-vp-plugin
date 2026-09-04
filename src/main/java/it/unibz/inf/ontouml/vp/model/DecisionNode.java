package it.unibz.inf.ontouml.vp.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** A single question of the decision tree, with the answers it accepts. */
public class DecisionNode {

  @SerializedName("question")
  private String question;

  @SerializedName("options")
  private List<DecisionOption> options;

  public String getQuestion() {
    return question;
  }

  public List<DecisionOption> getOptions() {
    return options == null ? new ArrayList<>() : options;
  }

  /** The allowed answers, as offered to the model and shown to the user. */
  public String getOptionsAsText() {
    return getOptions().stream().map(DecisionOption::getAnswer).collect(Collectors.joining(" | "));
  }

  /**
   * Matches what the model answered against the allowed answers: exact first, then a lenient
   * containment match, since models tend to reply with a slightly reworded option.
   */
  public DecisionOption match(String answer) {
    if (answer == null) {
      return null;
    }

    final String normalized = answer.trim().toLowerCase();

    for (DecisionOption option : getOptions()) {
      if (option.getAnswer().toLowerCase().equals(normalized)) {
        return option;
      }
    }

    for (DecisionOption option : getOptions()) {
      final String candidate = option.getAnswer().toLowerCase();
      if (normalized.contains(candidate) || candidate.contains(normalized)) {
        return option;
      }
    }

    return null;
  }
}
