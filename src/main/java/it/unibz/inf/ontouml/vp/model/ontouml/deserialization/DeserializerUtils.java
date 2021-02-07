package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.*;
import it.unibz.inf.ontouml.vp.model.ontouml.model.*;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Package;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DeserializerUtils {

  public static boolean deserializeBooleanField(JsonNode containerNode, String fieldname) {
    JsonNode booleanNode = containerNode.get(fieldname);

    if (booleanNode != null && booleanNode.isBoolean()) {
      return booleanNode.asBoolean();
    }

    return false;
  }

  public static String deserializeNullableStringField(JsonNode containerNode, String fieldname) {
    JsonNode stringNode = containerNode.get(fieldname);

    if (stringNode != null && stringNode.isTextual()) {
      return stringNode.asText();
    }

    return null;
  }

  public static String[] deserializeNullableStringArrayField(
      JsonNode containerNode, String fieldname, ObjectCodec codec) throws IOException {
    JsonNode arrayNode = containerNode.get(fieldname);

    if (arrayNode != null && arrayNode.isArray()) {
      return arrayNode.traverse(codec).readValueAs(String[].class);
    }

    return new String[0];
  }

  public static Boolean deserializeNullableBooleanField(JsonNode containerNode, String fieldname) {
    JsonNode booleanNode = containerNode.get(fieldname);

    if (booleanNode != null && booleanNode.isBoolean()) {
      return booleanNode.asBoolean();
    }

    return null;
  }

  public static Integer deserializeNullableIntegerField(JsonNode containerNode, String fieldname) {
    JsonNode integerNode = containerNode.get(fieldname);

    if (integerNode != null && integerNode.canConvertToInt()) {
      return integerNode.asInt();
    }

    return null;
  }

  public static boolean isValidReference(JsonNode node) {
    if (!node.isObject()) return false;

    final List<String> types =
        List.of(
            "Class",
            "Relation",
            "Property",
            "Generalization",
            "GeneralizationSet",
            "Package",
            "Literal",
            "Project");

    String typeFieldValue = node.get("type").asText();
    return types.contains(typeFieldValue);
  }

  public static boolean isReferenceOf(JsonNode node, java.lang.Class<?> referenceType) {
    if (!node.isObject()) return false;

    String typeFieldValue = node.get("type").asText();

    if (referenceType.equals(Class.class)) return "Class".equals(typeFieldValue);
    if (referenceType.equals(Relation.class)) return "Relation".equals(typeFieldValue);
    if (referenceType.equals(Property.class)) return "Property".equals(typeFieldValue);
    if (referenceType.equals(Generalization.class)) return "Generalization".equals(typeFieldValue);
    if (referenceType.equals(GeneralizationSet.class))
      return "GeneralizationSet".equals(typeFieldValue);
    if (referenceType.equals(Package.class)) return "Package".equals(typeFieldValue);
    if (referenceType.equals(Literal.class)) return "Literal".equals(typeFieldValue);
    if (referenceType.equals(Project.class)) return "Project".equals(typeFieldValue);

    return false;
  }

  public static boolean isReferenceOfAny(JsonNode node, java.lang.Class<?>... referenceTypes) {

    for (java.lang.Class<?> type : referenceTypes) {
      if (isReferenceOf(node, type)) {
        return true;
      }
    }

    return false;
  }

  public static boolean isReferenceOfClassifier(JsonNode node) {
    return isReferenceOfAny(node, Class.class, Relation.class);
  }

  public static boolean isReferenceOfProperty(JsonNode node) {
    return isReferenceOfAny(node, Property.class);
  }

  public static boolean isReferenceOfGeneralization(JsonNode node) {
    return isReferenceOfAny(node, Property.class);
  }

  public static boolean isReferenceOfClass(JsonNode node) {
    return isReferenceOfAny(node, Class.class);
  }

  public static <T extends OntoumlElement> T deserializeObject(
      JsonNode containerNode, String fieldName, java.lang.Class<T> referenceType, ObjectCodec codec)
      throws IOException {

    JsonNode node = containerNode.get(fieldName);

    if (node == null) return null;

    if (isReferenceOf(node, referenceType)) {
      return node.traverse(codec).readValueAs(referenceType);
    }

    throw new JsonParseException(
        codec.treeAsTokens(node), "Cannot deserialize object! Wrong type.");
  }

  public static <T extends OntoumlElement> List<T> deserializeObjectArray(
      JsonNode containerNode, String fieldName, java.lang.Class<T> referenceType, ObjectCodec codec)
      throws IOException {

    JsonNode arrayNode = containerNode.get(fieldName);

    if (arrayNode == null || arrayNode.isNull()) {
      return List.of();
    }

    if (arrayNode.isArray()) {
      List<T> list = new ArrayList<>();
      Iterator<JsonNode> iterator = arrayNode.elements();
      while (iterator.hasNext()) {
        JsonNode memberNode = iterator.next();
        T member = memberNode.traverse(codec).readValueAs(referenceType);
        list.add(member);
      }
      return list;
    }

    throw new JsonParseException(codec.treeAsTokens(arrayNode), "Cannot deserialize object array!");
  }

  public static Classifier<? extends Classifier<?, ?>, ? extends Stereotype> deserializeClassifier(
      JsonNode containerNode, String fieldName, ObjectCodec codec, DeserializationContext context)
      throws IOException {

    JsonNode node = containerNode.get(fieldName);

    if (node == null || node.isNull()) {
      return null;
    }

    if (isReferenceOf(node, Class.class)) {
      return node.traverse(codec).readValueAs(Class.class);
    }

    if (isReferenceOf(node, Relation.class)) {
      return node.traverse(codec).readValueAs(Relation.class);
    }

    throw new JsonParseException(codec.treeAsTokens(node), "Cannot deserialize classifier.");
  }
}
