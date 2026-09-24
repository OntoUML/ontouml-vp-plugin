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
import java.util.Collections;
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
 *
 * <p>A walk can also start part-way down the tree, carrying the steps already taken. That is what
 * machine teaching uses: the modeller fixes the answer of one question, and the walk resumes from
 * the branch that answer opens, with the earlier answers given to the model as settled.
 */
public class DetailedClassificationWorker extends SwingWorker<String, DecisionStep> {

  /** Guards against a malformed tree sending the walk around in circles. */
  private static final int MAX_STEPS = 15;

  /** Marks an answer the model must take as given rather than reconsider. */
  private static final String TAUGHT_MARKER = "  (decided by the modeller — a fixed answer)";

  private final IClass targetClass;
  private final List<DecisionStep> priorSteps;
  private final String startNodeId;
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
    this(targetClass, Collections.emptyList(), null, onNodeEntered, onStep, onFinished);
  }

  /**
   * @param priorSteps the questions already settled, in order. They are not asked again: they only
   *     seed the context given to the model and the step numbering.
   * @param startNodeId the node to ask first; null starts the walk at the root of the tree.
   */
  public DetailedClassificationWorker(
      IClass targetClass,
      List<DecisionStep> priorSteps,
      String startNodeId,
      Consumer<String> onNodeEntered,
      Consumer<DecisionStep> onStep,
      BiConsumer<String, String> onFinished) {
    this.targetClass = targetClass;
    this.priorSteps = new ArrayList<>(priorSteps);
    this.startNodeId = startNodeId;
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

    for (DecisionStep prior : priorSteps) {
      appendToPath(path, prior.getNumber(), prior.getQuestion(), prior.getAnswer(), prior.isTaught());
    }

    String nodeId = startNodeId == null ? tree.getStartNodeId() : startNodeId;
    DecisionNode node = tree.getNode(nodeId);
    int stepNumber = priorSteps.size();

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
                answer,
                null));
        return null;
      }

      publishStep(
          new DecisionStep(
              stepNumber,
              nodeId,
              node.getQuestion(),
              node.getOptionsAsText(),
              option.getAnswer(),
              answer,
              option.getStereotype()));

      appendToPath(path, stepNumber, node.getQuestion(), option.getAnswer(), false);

      if (option.isLeaf()) {
        return option.getStereotype();
      }

      nodeId = option.getNext();
      node = tree.getNode(nodeId);
    }

    return null;
  }

  /** One entry of the "questions already answered" block handed to the model. */
  private static void appendToPath(
      StringBuilder path, int number, String question, String answer, boolean taught) {
    path.append(number)
        .append(". ")
        .append(question)
        .append("\n   → ")
        .append(answer)
        .append(taught ? TAUGHT_MARKER : "")
        .append("\n");
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

  /** Every step of the walk, the ones it started from included. */
  public List<DecisionStep> getSteps() {
    final List<DecisionStep> all = new ArrayList<>(priorSteps);
    all.addAll(steps);
    return all;
  }
}
