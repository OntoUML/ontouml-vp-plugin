package it.unibz.inf.ontouml.vp.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class ServiceIssueDeserializer extends JsonDeserializer<ServiceIssue> {

  @Override
  public ServiceIssue deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    final ObjectCodec oc = jsonParser.getCodec();
    final JsonNode node = oc.readTree(jsonParser);
    final JsonNode dataNode = node.get("data");
    final ServiceIssue issue = new ServiceIssue();

    final String id = node.get("id") != null ? node.get("id").asText() : null;
    final String code = node.get("code") != null ? node.get("code").asText() : null;
    final String title = node.get("title") != null ? node.get("title").asText() : null;
    final String description =
        node.get("description") != null ? node.get("description").asText() : null;
    final String severity = node.get("severity") != null ? node.get("severity").asText() : null;

    issue.setId(id);
    issue.setCode(code);
    issue.setTitle(title);
    issue.setDescription(description);
    issue.setSeverity(severity);

    if (dataNode != null) {
      // TODO: process context field from verification issues
      // TODO: process context element from GUFO transformation issues
      final JsonNode sourceNode = dataNode.get("source");
      final ObjectMapper mapper = new ObjectMapper();

      if (sourceNode != null) {
        final String sourceId = sourceNode.get("id") != null ? sourceNode.get("id").asText() : null;
        final String type = sourceNode.get("type") != null ? sourceNode.get("type").asText() : null;
        final OntoumlElementReference source = new OntoumlElementReference(sourceId, type);
        issue.setSource(source);
      }
    }

    return issue;
  }
}
