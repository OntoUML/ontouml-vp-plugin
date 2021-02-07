package it.unibz.inf.ontouml.vp.model.ontouml.view;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;
import it.unibz.inf.ontouml.vp.model.ontouml.deserialization.ClassViewDeserializer;
import it.unibz.inf.ontouml.vp.model.ontouml.deserialization.RelationViewDeserializer;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Relation;
import it.unibz.inf.ontouml.vp.model.ontouml.serialization.RelationViewSerializer;
import java.util.List;

@JsonSerialize(using = RelationViewSerializer.class)
@JsonDeserialize(using = RelationViewDeserializer.class)
public class RelationView extends ConnectorView<Relation> {

  //  private Text nameText = new Text();
  //  private Text stereotypeText = new Text();
  //  private Text sourceRoleText = new Text();
  //  private Text sourceCardinalityText = new Text();
  //  private Text targetRoleText = new Text();
  //  private Text targetCardinalityText = new Text();

  public RelationView(String id, Relation relation) {
    super(id, relation);
  }

  public RelationView(Relation relation) {
    this(null, relation);
  }

  public RelationView() {
    super(null, null);
  }

  @Override
  public List<OntoumlElement> getContents() {
    List<OntoumlElement> contents = super.getContents();

    //    contents.addAll(
    //        List.of(
    //            nameText,
    //            stereotypeText,
    //            sourceRoleText,
    //            sourceCardinalityText,
    //            targetRoleText,
    //            targetCardinalityText));

    return contents;
  }

  @Override
  public String getType() {
    return "RelationView";
  }
}
