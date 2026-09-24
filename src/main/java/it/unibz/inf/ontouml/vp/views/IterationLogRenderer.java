package it.unibz.inf.ontouml.vp.views;

import static it.unibz.inf.ontouml.vp.views.DecisionTreeBrowser.escape;
import static it.unibz.inf.ontouml.vp.views.DecisionTreeBrowser.field;
import static it.unibz.inf.ontouml.vp.views.DecisionTreeBrowser.html;
import static it.unibz.inf.ontouml.vp.views.DecisionTreeBrowser.rawField;
import static it.unibz.inf.ontouml.vp.views.DecisionTreeBrowser.toHex;

import it.unibz.inf.ontouml.vp.model.DecisionStep;
import java.awt.Color;
import java.util.List;

/**
 * Renders the iteration log of a detailed evaluation as HTML, in the same idiom as the node detail
 * panels: a coloured header per step and the laid-out fields of {@link DecisionTreeBrowser}.
 *
 * <p>A step carries an anchor, so the window can scroll the log straight to the question the user
 * clicked, and a single step renders on its own for the panel beside the tree.
 */
public class IterationLogRenderer {

  private static final Color RESULT_COLOR = DecisionTreeNode.Kind.RESULT.getColor();
  private static final Color QUESTION_COLOR = DecisionTreeNode.Kind.QUESTION.getColor();
  private static final Color PROBLEM_COLOR = DecisionTreeNode.Kind.PROBLEM.getColor();

  private IterationLogRenderer() {}

  /** The anchor name of a step, for {@link javax.swing.JEditorPane#scrollToReference(String)}. */
  public static String anchorOf(DecisionStep step) {
    return "step-" + step.getNumber();
  }

  /** The whole log, plus the outcome once the walk has ended. */
  public static String renderLog(List<DecisionStep> steps, String stereotype, String error) {
    if (steps.isEmpty()) {
      return html(
          "<p style='color:#777'>The log of each question fills in here as the walk"
              + " progresses. Press <b>Run</b> to start.</p>");
    }

    final StringBuilder body = new StringBuilder();

    for (DecisionStep step : steps) {
      appendStep(body, step);
    }

    appendOutcome(body, stereotype, error);

    return html(body.toString());
  }

  /** One step on its own, shown beside the tree for the question that was clicked. */
  public static String renderStep(DecisionStep step) {
    final StringBuilder body = new StringBuilder();
    appendStep(body, step);
    return body.toString();
  }

  private static void appendStep(StringBuilder body, DecisionStep step) {
    final Color color = step.isTaught() ? RESULT_COLOR : QUESTION_COLOR;

    body.append("<a name='").append(anchorOf(step)).append("'></a>");
    body.append("<div style='background:")
        .append(toHex(color))
        .append("; color:#ffffff; padding:4px 8px; font-weight:bold'>Step ")
        .append(step.getNumber())
        .append("<span style='font-weight:normal'> &nbsp;·&nbsp; ")
        .append(escape(step.getSource().getLabel()))
        .append("</span></div>");

    body.append("<div style='padding:8px 2px'>");

    field(body, "Question", step.getQuestion());
    appendOptions(body, step);

    rawField(
        body,
        "Answer",
        "<span style='color:"
            + toHex(RESULT_COLOR)
            + "; font-weight:bold'>"
            + escape(step.getAnswer())
            + "</span>");

    field(body, "Justification", step.getJustification());

    if (step.getConfidence() > 0) {
      field(body, "Confidence", Math.round(step.getConfidence() * 100) + "%");
    }

    if (step.getReachedStereotype() != null) {
      rawField(
          body,
          "Reaches stereotype",
          "<span style='color:"
              + toHex(RESULT_COLOR)
              + "; font-weight:bold'>«"
              + escape(step.getReachedStereotype())
              + "»</span>");
    }

    body.append("</div>");
  }

  /** The allowed answers of the question, with the one that was given picked out. */
  private static void appendOptions(StringBuilder body, DecisionStep step) {
    final String options = step.getOptions();

    if (options == null || options.trim().isEmpty()) {
      return;
    }

    // the label and the list are siblings, not nested: a <ul> inside a <p> loses its bottom margin
    body.append("<p style='margin:0 0 4px 0'><span style='color:#777; font-size:9px'>ALLOWED"
        + " ANSWERS</span></p><ul style='margin:0 0 10px 0; padding-left:16px'>");

    for (String option : options.split("\\|")) {
      final String trimmed = option.trim();

      if (trimmed.isEmpty()) {
        continue;
      }

      final boolean chosen = trimmed.equalsIgnoreCase(step.getAnswer().trim());

      body.append("<li>")
          .append(chosen ? "<b>" + escape(trimmed) + "</b>  ✔" : escape(trimmed))
          .append("</li>");
    }

    body.append("</ul>");
  }

  private static void appendOutcome(StringBuilder body, String stereotype, String error) {
    if (stereotype != null) {
      body.append("<div style='background:")
          .append(toHex(RESULT_COLOR))
          .append("; color:#ffffff; padding:4px 8px; font-weight:bold'>Final result: «")
          .append(escape(stereotype))
          .append("»</div>");
      return;
    }

    if (error != null) {
      body.append("<div style='background:")
          .append(toHex(PROBLEM_COLOR))
          .append("; color:#ffffff; padding:4px 8px; font-weight:bold'>")
          .append(escape(error))
          .append("</div>");
    }
  }
}
