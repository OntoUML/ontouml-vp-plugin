package it.unibz.inf.ontouml.vp.model;

import it.unibz.inf.ontouml.vp.controllers.LlmAccessController.NodeAnswer;

/** One answered question of a detailed classification, kept so the user can review the reasoning. */
public class DecisionStep {

  /** Where the answer of a step came from. */
  public enum Source {
    AI("answered by the AI"),
    TAUGHT("taught by the modeller");

    private final String label;

    Source(String label) {
      this.label = label;
    }

    public String getLabel() {
      return label;
    }
  }

  private final int number;
  private final String nodeId;
  private final String question;
  private final String options;
  private final String answer;
  private final String justification;
  private final double confidence;
  private final Source source;
  private final String reachedStereotype;
  private final LlmExchange exchange;

  /**
   * Builds a step from an answer in the internal format, whoever produced it: the walk only has to
   * say which question was asked and where the answer led.
   *
   * @param answer the answer as matched against the node's allowed answers, which may differ in
   *     wording from what {@code nodeAnswer} carries.
   */
  public DecisionStep(
      int number,
      String nodeId,
      String question,
      String options,
      String answer,
      NodeAnswer nodeAnswer,
      String reachedStereotype) {
    this(
        number,
        nodeId,
        question,
        options,
        answer,
        nodeAnswer.getJustification(),
        nodeAnswer.getConfidence(),
        nodeAnswer.isTaughtByUser() ? Source.TAUGHT : Source.AI,
        reachedStereotype,
        nodeAnswer.getExchange());
  }

  public DecisionStep(
      int number,
      String nodeId,
      String question,
      String options,
      String answer,
      String justification,
      double confidence,
      Source source,
      String reachedStereotype,
      LlmExchange exchange) {
    this.number = number;
    this.nodeId = nodeId;
    this.question = question;
    this.options = options;
    this.answer = answer;
    this.justification = justification;
    this.confidence = confidence;
    this.source = source;
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

  /** Between 0.0 and 1.0; 0.0 when the model reported none. Always 1.0 for a taught answer. */
  public double getConfidence() {
    return confidence;
  }

  public Source getSource() {
    return source;
  }

  public boolean isTaught() {
    return source == Source.TAUGHT;
  }

  public String getAnswer() {
    return answer;
  }

  public String getReachedStereotype() {
    return reachedStereotype;
  }

  /** Null for a step the modeller decided, since no question was put to the model. */
  public LlmExchange getExchange() {
    return exchange;
  }

  /** Compact plain-text rendering, for copying a step out of the window. */
  public String toDisplayText() {
    final StringBuilder text = new StringBuilder();

    text.append("── Step ").append(number).append(" · ").append(source.getLabel()).append(" ──\n");
    text.append("Q: ").append(question).append("\n");
    text.append("Options: ").append(options).append("\n");
    text.append("A: ").append(answer).append("\n");

    if (justification != null && !justification.trim().isEmpty()) {
      text.append("Why: ").append(justification.trim()).append("\n");
    }

    if (confidence > 0) {
      text.append("Confidence: ").append(Math.round(confidence * 100)).append("%\n");
    }

    if (reachedStereotype != null) {
      text.append("→ Reached «").append(reachedStereotype).append("»\n");
    }

    text.append("\n");

    return text.toString();
  }
}
