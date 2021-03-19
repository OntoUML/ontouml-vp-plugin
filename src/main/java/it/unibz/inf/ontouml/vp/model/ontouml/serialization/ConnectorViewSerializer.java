package it.unibz.inf.ontouml.vp.model.ontouml.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import it.unibz.inf.ontouml.vp.model.ontouml.view.ConnectorView;
import java.io.IOException;

public class ConnectorViewSerializer extends JsonSerializer<ConnectorView<?>> {

  @Override
  public void serialize(
      ConnectorView connectorView, JsonGenerator jsonGen, SerializerProvider provider)
      throws IOException {
    jsonGen.writeStartObject();
    serializeFields(connectorView, jsonGen);
    jsonGen.writeEndObject();
  }

  static void serializeFields(ConnectorView<?> connectorView, JsonGenerator jsonGen)
      throws IOException {
    DiagramElementSerializer.serializeFields(connectorView, jsonGen);
    Serializer.writeNullableReferenceField("source", connectorView.getSource(), jsonGen);
    Serializer.writeNullableReferenceField("target", connectorView.getTarget(), jsonGen);
  }
}
