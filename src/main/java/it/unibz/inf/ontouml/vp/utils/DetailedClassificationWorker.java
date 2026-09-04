package it.unibz.inf.ontouml.vp.utils;

import com.vp.plugin.model.IClass;
import it.unibz.inf.ontouml.vp.controllers.LlmAccessController;
import it.unibz.inf.ontouml.vp.controllers.LlmAccessController.NodeAnswer;
import it.unibz.inf.ontouml.vp.model.Configurations;
import it.unibz.inf.ontouml.vp.model.DecisionNode;
import it.unibz.inf.ontouml.vp.model.DecisionOption;
import it.unibz.inf.ontouml.vp.model.DecisionStep;
import it.unibz.inf.ontouml.vp.model.DecisionTree;
import it.unibz.inf.ontouml.vp.model.LlmConfigurations;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.swing.SwingWorker;

/**
 * Walks the UFO decision tree one node at a time, asking the LLM a single question per node.
 *
 * <p>Every answered question is published as soon as it arrives, so the window can show the
 * reasoning unfolding instead of only its conclusion. The walk stops at the first answer that
 * reaches a stereotype, or when the tree is exhausted or an answer cannot be matched.
 */
public class DetailedClassificationWorker extends SwingWorker<String, DecisionStep> {

  /** Guards against a malformed tree sending the walk around in circles. */
  private static final int MAX_STEPS = 15;

  private final IClass targetClass;
  private final Consumer<String> onNodeEntered;
  private final Consumer<DecisionStep> onStep;
  private final BiConsumer<String, String> onFinished;

  private final List<DecisionStep> steps = new ArrayList<>();

  /**
   * @param onNodeEntered called with the id of the node whose question is about to be asked.
   * @param onStep called on the event dispatch thread for each answered question.
   * @param onFinished called with the reached stereotype (null when none) and an error message
   *     (null when the walk completed).
   */
  public DetailedClassificationWorker(
      IClass targetClass,
      Consumer<String> onNodeEntered,
      Consumer<DecisionStep> onStep,
      BiConsumer<String, String> onFinished) {
    this.targetClass = targetClass;
    this.onNodeEntered = onNodeEntered;
    this.onStep = onStep;
    this.onFinished = onFinished;
  }

  @Override
  protected String doInBackground() throws Exception {
    final DecisionTree tree = LlmPromptResources.getDecisionTree();

    if (tree == null) {
      throw new IllegalStateException(
          "The decision tree could not be read. Check it in the OntoUML Plugin Settings.");
    }

    final LlmConfigurations config = Configurations.getInstance().getLlmConfigurations();
    final String classSummary = LlmPromptContextBuilder.buildClassSummary(targetClass);
    final StringBuilder path = new StringBuilder();

    String nodeId = tree.getStartNodeId();
    DecisionNode node = tree.getNode(nodeId);
    int stepNumber = 0;

    while (node != null && stepNumber < MAX_STEPS) {
      if (isCancelled()) {
        return null;
      }

      stepNumber++;
      publishNodeEntered(nodeId);

      final String prompt =
          LlmPromptResources.getNodePrompt()
              .replace("{class}", classSummary)
              .replace("{path}", path.length() == 0 ? "(this is the first question)" : path.toString())
              .replace("{question}", node.getQuestion())
              .replace("{options}", node.getOptionsAsText());

      final NodeAnswer answer =
          LlmAccessController.askDecisionNode(
              config, "Decision step " + stepNumber + " — \"" + targetClass.getName() + "\"", prompt);

      final DecisionOption option = node.match(answer.getAnswer());

      if (option == null) {
        publishStep(
            new DecisionStep(
                stepNumber,
                nodeId,
                node.getQuestion(),
                node.getOptionsAsText(),
                answer.getAnswer() + "  (not one of the allowed answers — walk stopped)",
                answer.getJustification(),
                null,
                answer.getExchange()));
        return null;
      }

      publishStep(
          new DecisionStep(
              stepNumber,
              nodeId,
              node.getQuestion(),
              node.getOptionsAsText(),
              option.getAnswer(),
              answer.getJustification(),
              option.getStereotype(),
              answer.getExchange()));

      path.append(stepNumber)
          .append(". ")
          .append(node.getQuestion())
          .append("\n   → ")
          .append(option.getAnswer())
          .append("\n");

      if (option.isLeaf()) {
        return option.getStereotype();
      }

      nodeId = option.getNext();
      node = tree.getNode(nodeId);
    }

    return null;
  }

  private void publishNodeEntered(String nodeId) {
    javax.swing.SwingUtilities.invokeLater(() -> onNodeEntered.accept(nodeId));
  }

  private void publishStep(DecisionStep step) {
    steps.add(step);
    publish(step);
  }

  @Override
  protected void process(List<DecisionStep> chunks) {
    chunks.forEach(onStep);
  }

  @Override
  protected void done() {
    if (isCancelled()) {
      return;
    }

    try {
      final String stereotype = get();

      if (stereotype != null && !Stereotype.getOntoUMLClassStereotypeNames().contains(stereotype)) {
        onFinished.accept(null, "The tree reached \"" + stereotype + "\", which is not a valid OntoUML class stereotype.");
        return;
      }

      onFinished.accept(
          stereotype,
          stereotype == null ? "The walk ended without reaching a stereotype." : null);
    } catch (Exception e) {
      final Throwable cause = e.getCause() != null ? e.getCause() : e;
      final String message =
          cause.getMessage() == null ? "The detailed evaluation failed." : cause.getMessage();

      onFinished.accept(null, message);
      e.printStackTrace();
    }
  }

  public List<DecisionStep> getSteps() {
    return steps;
  }
}
