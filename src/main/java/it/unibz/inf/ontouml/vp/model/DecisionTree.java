package it.unibz.inf.ontouml.vp.model;

import com.google.gson.annotations.SerializedName;
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
}
