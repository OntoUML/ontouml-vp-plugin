package it.unibz.inf.ontouml.vp.model;

/** One answered question of a detailed classification, kept so the user can review the reasoning. */
public class DecisionStep {

  private final int number;
  private final String nodeId;
  private final String question;
  private final String options;
  private final String answer;
  private final String justification;
  private final String reachedStereotype;
  private final LlmExchange exchange;

  public DecisionStep(
      int number,
      String nodeId,
      String question,
      String options,
      String answer,
      String justification,
      String reachedStereotype,
      LlmExchange exchange) {
    this.number = number;
    this.nodeId = nodeId;
    this.question = question;
    this.options = options;
    this.answer = answer;
    this.justification = justification;
    this.reachedStereotype = reachedStereotype;
    this.exchange = exchange;
  }

  public int getNumber() {
    return number;
  }

  public String getNodeId() {
    return nodeId;
  }

  public String getQuestion() {
    return question;
  }

  public String getOptions() {
    return options;
  }

  public String getJustification() {
    return justification;
  }

  public String getAnswer() {
    return answer;
  }

  public String getReachedStereotype() {
    return reachedStereotype;
  }

  public LlmExchange getExchange() {
    return exchange;
  }

  /** Compact rendering appended to the window as the walk progresses. */
  public String toDisplayText() {
    final StringBuilder text = new StringBuilder();

    text.append("── Step ").append(number).append(" ──\n");
    text.append("Q: ").append(question).append("\n");
    text.append("Options: ").append(options).append("\n");
    text.append("A: ").append(answer).append("\n");

    if (justification != null && !justification.trim().isEmpty()) {
      text.append("Why: ").append(justification.trim()).append("\n");
    }

    if (reachedStereotype != null) {
      text.append("→ Reached «").append(reachedStereotype).append("»\n");
    }

    text.append("\n");

    return text.toString();
  }
}
