package it.unibz.inf.ontouml.vp.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/** The UFO decision tree in machine-readable form, walked one node at a time. */
public class DecisionTree {

  @SerializedName("startNode")
  private String startNode;

  @SerializedName("nodes")
  private Map<String, DecisionNode> nodes;

  public String getStartNodeId() {
    return startNode;
  }

  public DecisionNode getNode(String id) {
    return nodes == null || id == null ? null : nodes.get(id);
  }

  public boolean isUsable() {
    return getNode(startNode) != null;
  }

  /**
   * Everything that would make the walk stop, or end on something that cannot be applied, found
   * without running it: a missing start node, a node without options, an option that neither leads
   * on nor ends, one that does both, a {@code next} pointing at no node, and a stereotype outside
   * {@code validStereotypes}. Empty when the tree is sound.
   */
  public List<String> findProblems(Collection<String> validStereotypes) {
    final List<String> problems = new ArrayList<>();

    if (getNode(startNode) == null) {
      problems.add("startNode \"" + startNode + "\" is not one of the nodes.");
    }

    if (nodes == null) {
      return problems;
    }

    for (Map.Entry<String, DecisionNode> entry : nodes.entrySet()) {
      final String id = entry.getKey();
      final DecisionNode node = entry.getValue();

      if (node == null || node.getOptions().isEmpty()) {
        problems.add("Node \"" + id + "\" has no options.");
        continue;
      }

      for (DecisionOption option : node.getOptions()) {
        final String where = "Node \"" + id + "\", answer \"" + option.getAnswer() + "\": ";
        final boolean leads = option.getNext() != null && !option.getNext().trim().isEmpty();

        if (option.getAnswer() == null || option.getAnswer().trim().isEmpty()) {
          problems.add("Node \"" + id + "\" has an option without an answer.");
        }

        if (leads == option.isLeaf()) {
          problems.add(
              where
                  + (leads
                      ? "has both \"next\" and \"stereotype\"; keep only one."
                      : "has neither \"next\" nor \"stereotype\"."));
        } else if (leads && getNode(option.getNext()) == null) {
          problems.add(
              where + "\"next\" points to \"" + option.getNext() + "\", which does not exist.");
        } else if (option.isLeaf() && !validStereotypes.contains(option.getStereotype())) {
          problems.add(
              where
                  + "stereotype \""
                  + option.getStereotype()
                  + "\" is not an OntoUML class stereotype.");
        }
      }
    }

    return problems;
  }
}
