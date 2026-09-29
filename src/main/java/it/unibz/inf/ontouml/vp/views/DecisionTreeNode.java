package it.unibz.inf.ontouml.vp.views;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Everything a row of {@link DecisionTreeBrowser} and the panels beside it need to show about one
 * node of the decision tree.
 */
public class DecisionTreeNode {

  public enum Kind {
    QUESTION("Question", new Color(0x1a3d6d)),
    OPTION("Answer option", new Color(0x333333)),
    RESULT("Result", new Color(0x0a7d28)),
    PROBLEM("Problem", new Color(0xa30000));

    private final String title;
    private final Color color;

    Kind(String title, Color color) {
      this.title = title;
      this.color = color;
    }

    public String getTitle() {
      return title;
    }

    public Color getColor() {
      return color;
    }
  }

  private final Kind kind;
  private final String label;

  private String nodeId;
  private String question;
  private String answer;
  private String stereotype;
  private String leadsTo;
  private String explanation;
  private String breadcrumb;
  private final List<String> answers = new ArrayList<>();

  public DecisionTreeNode(Kind kind, String label) {
    this.kind = kind;
    this.label = label;
  }

  public Kind getKind() {
    return kind;
  }

  public String getNodeId() {
    return nodeId;
  }

  public void setNodeId(String nodeId) {
    this.nodeId = nodeId;
  }

  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public String getStereotype() {
    return stereotype;
  }

  public void setStereotype(String stereotype) {
    this.stereotype = stereotype;
  }

  public String getLeadsTo() {
    return leadsTo;
  }

  public void setLeadsTo(String leadsTo) {
    this.leadsTo = leadsTo;
  }

  public String getExplanation() {
    return explanation;
  }

  public void setExplanation(String explanation) {
    this.explanation = explanation;
  }

  public String getBreadcrumb() {
    return breadcrumb;
  }

  public void setBreadcrumb(String breadcrumb) {
    this.breadcrumb = breadcrumb;
  }

  public List<String> getAnswers() {
    return answers;
  }

  /** Tooltip: the full text, since the label in the row is truncated. */
  public String getTooltip() {
    if (kind == Kind.QUESTION) {
      return question;
    }
    if (kind == Kind.RESULT) {
      return answer + " reaches the stereotype «" + stereotype + "»";
    }
    if (kind == Kind.PROBLEM) {
      return explanation;
    }

    return leadsTo == null ? answer : answer + " leads to: " + leadsTo;
  }

  @Override
  public String toString() {
    return label;
  }
}
