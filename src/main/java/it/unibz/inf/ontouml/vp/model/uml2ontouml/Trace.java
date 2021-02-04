package it.unibz.inf.ontouml.vp.model.uml2ontouml;

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
    return map.get(id).getSource();
  }

  public Object getSource(OntoumlElement target) {
    return getSource(target.getId());
  }

  public OntoumlElement getTarget(String id) {
    return map.get(id).getTarget();
  }

  public OntoumlElement getTarget(IModelElement source) {
    return getTarget(source.getId());
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
