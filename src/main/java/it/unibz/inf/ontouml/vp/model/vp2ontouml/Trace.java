package it.unibz.inf.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.model.IModelElement;
import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Trace {

  private static Trace instance = null;

  public Map<String, Correspondence> map;

  private Trace() {
    initialize();
  }

  public void initialize() {
    map = new HashMap<>();
  }

  public void put(String id, Object source, OntoumlElement target) {
    map.put(id, new Correspondence(source, target));
  }

  public Object getSource(String id) {
    Correspondence correspondence = map.get(id);
    return correspondence != null ? correspondence.getSource() : null;
  }

  public Object getSource(OntoumlElement target) {
    return target != null ? getSource(target.getId()) : null;
  }

  public OntoumlElement getTarget(String id) {
    Correspondence correspondence = map.get(id);
    return correspondence != null ? correspondence.getTarget() : null;
  }

  public OntoumlElement getTarget(IModelElement source) {
    return source != null ? getTarget(source.getId()) : null;
  }

  public static Trace getInstance() {
    if (instance == null) instance = new Trace();

    return instance;
  }

  @Override
  public String toString() {
    return map.entrySet().stream()
        .map(
            x ->
                x.getKey()
                    + ": Source: "
                    + x.getValue().getSource().hashCode()
                    + " - Target: "
                    + x.getValue().getTarget().getType()
                    + " "
                    + x.getValue().getTarget().getFirstName().orElse("Unnamed")
                    + "\n")
        .collect(Collectors.joining());
  }

  private static class Correspondence {
    Object source;
    OntoumlElement target;

    public Correspondence(Object source, OntoumlElement target) {
      this.source = source;
      this.target = target;
    }

    public Object getSource() {
      return source;
    }

    public OntoumlElement getTarget() {
      return target;
    }
  }
}
