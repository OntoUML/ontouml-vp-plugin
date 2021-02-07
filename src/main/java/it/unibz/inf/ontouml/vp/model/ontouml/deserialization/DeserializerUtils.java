package it.unibz.inf.ontouml.vp.model.ontouml.deserialization;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import it.unibz.inf.ontouml.vp.model.ontouml.OntoumlElement;
import it.unibz.inf.ontouml.vp.model.ontouml.Project;
import it.unibz.inf.ontouml.vp.model.ontouml.model.*;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Class;
import it.unibz.inf.ontouml.vp.model.ontouml.model.Package;
import it.unibz.inf.ontouml.vp.model.ontouml.view.*;
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

  public static Double deserializeNullableDoubleField(JsonNode containerNode, String fieldname) {
    JsonNode doubleNode = containerNode.get(fieldname);

    if (doubleNode != null && doubleNode.isNumber()) {
      return doubleNode.asDouble();
    }

    return null;
  }

  public static boolean isReferenceOf(JsonNode node, java.lang.Class<?> referenceType) {
    if (!node.isObject()) return false;

    String typeFieldValue = node.get("type").asText();

    if (referenceType.equals(Class.class)) return "Class".equals(typeFieldValue);
    else if (referenceType.equals(Relation.class)) return "Relation".equals(typeFieldValue);
    else if (referenceType.equals(Property.class)) return "Property".equals(typeFieldValue);
    else if (referenceType.equals(Generalization.class))
      return "Generalization".equals(typeFieldValue);
    else if (referenceType.equals(GeneralizationSet.class))
      return "GeneralizationSet".equals(typeFieldValue);
    else if (referenceType.equals(Package.class)) return "Package".equals(typeFieldValue);
    else if (referenceType.equals(Literal.class)) return "Literal".equals(typeFieldValue);
    else if (referenceType.equals(Project.class)) return "Project".equals(typeFieldValue);
    else if (referenceType.equals(ClassView.class)) return "ClassView".equals(typeFieldValue);
    else if (referenceType.equals(RelationView.class)) return "RelationView".equals(typeFieldValue);
    else if (referenceType.equals(GeneralizationView.class))
      return "GeneralizationView".equals(typeFieldValue);
    else if (referenceType.equals(GeneralizationSetView.class))
      return "GeneralizationSetView".equals(typeFieldValue);
    else if (referenceType.equals(PackageView.class)) return "PackageView".equals(typeFieldValue);
    else if (referenceType.equals(Path.class)) return "Path".equals(typeFieldValue);
    else if (referenceType.equals(Rectangle.class)) return "Rectangle".equals(typeFieldValue);
    else if (referenceType.equals(Text.class)) return "Text".equals(typeFieldValue);

    return false;
  }

  public static java.lang.Class<? extends OntoumlElement> getClass(String typeName) {
    switch (typeName) {
      case "ClassView":
        return ClassView.class;
      case "RelationView":
        return RelationView.class;
      case "GeneralizationView":
        return GeneralizationView.class;
      case "GeneralizationSetView":
        return GeneralizationSetView.class;
      case "PackageView":
        return PackageView.class;
      case "Path":
        return Path.class;
      case "Rectangle":
        return Rectangle.class;
      case "Text":
        return Text.class;
      case "Class":
        return Class.class;
      case "Relation":
        return Relation.class;
      case "Generalization":
        return Generalization.class;
      case "GeneralizationSet":
        return GeneralizationSet.class;
      case "Package":
        return Package.class;
      case "Property":
        return Property.class;
      case "Literal":
        return Literal.class;
      case "Project":
        return Project.class;
      case "Diagram":
        return Diagram.class;
    }

    return null;
  }

  /**
   * Deserializes a node into the subclass of {@link OntoumlElement} that matches the "type" field
   * of the JSON object
   */
  private static OntoumlElement deserializeObject(
      JsonNode node,
      List<java.lang.Class<? extends OntoumlElement>> allowedTypes,
      ObjectCodec codec)
      throws IOException {

    if (node == null || !node.isObject()) return null;

    JsonNode typeNode = node.get("type");
    if (typeNode == null || !typeNode.isTextual()) return null;

    String typeNodeValue = typeNode.asText();
    java.lang.Class<? extends OntoumlElement> type = getClass(typeNodeValue);

    if (allowedTypes.contains(type)) {
      return node.traverse(codec).readValueAs(type);
    }

    throw new JsonParseException(
        codec.treeAsTokens(node), "Cannot deserialize object! Wrong type.");
  }

  private static <T extends OntoumlElement> T deserializeObject(
      JsonNode node, java.lang.Class<T> allowedType, ObjectCodec codec) throws IOException {

    OntoumlElement element = deserializeObject(node, List.of(allowedType), codec);
    return castOrNull(element, allowedType);
  }

  public static OntoumlElement deserializeObjectField(
      JsonNode containerNode,
      String fieldName,
      List<java.lang.Class<? extends OntoumlElement>> allowedTypes,
      ObjectCodec codec)
      throws IOException {

    JsonNode node = containerNode.get(fieldName);
    return deserializeObject(node, allowedTypes, codec);
  }

  public static <T extends OntoumlElement> T deserializeObjectField(
      JsonNode containerNode, String fieldName, java.lang.Class<T> allowedType, ObjectCodec codec)
      throws IOException {

    JsonNode node = containerNode.get(fieldName);
    return deserializeObject(node, allowedType, codec);
  }

  public static List<OntoumlElement> deserializeArrayField(
      JsonNode containerNode,
      String fieldName,
      List<java.lang.Class<? extends OntoumlElement>> allowedTypes,
      ObjectCodec codec)
      throws IOException {

    JsonNode arrayNode = containerNode.get(fieldName);

    if (arrayNode == null || arrayNode.isNull()) {
      return List.of();
    }

    if (arrayNode.isArray()) {
      List<OntoumlElement> list = new ArrayList<>();
      Iterator<JsonNode> iterator = arrayNode.elements();

      while (iterator.hasNext()) {
        JsonNode memberNode = iterator.next();
        OntoumlElement member = deserializeObject(memberNode, allowedTypes, codec);
        if (member != null) list.add(member);
      }

      return list;
    }

    throw new JsonParseException(codec.treeAsTokens(arrayNode), "Cannot deserialize object array!");
  }

  public static <T extends OntoumlElement> List<T> deserializeArrayField(
      JsonNode containerNode, String fieldName, java.lang.Class<T> allowedType, ObjectCodec codec)
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
        T member = deserializeObject(memberNode, allowedType, codec);
        if (member != null) list.add(member);
      }

      return list;
    }

    throw new JsonParseException(codec.treeAsTokens(arrayNode), "Cannot deserialize object array!");
  }

  public static Classifier<?, ?> deserializeClassifierField(
      JsonNode containerNode, String fieldName, ObjectCodec codec) throws IOException {

    OntoumlElement classifier =
        deserializeObjectField(
            containerNode, fieldName, List.of(Class.class, Relation.class), codec);

    return castOrNull(classifier, Classifier.class);
  }

  private static <T> T castOrNull(Object object, java.lang.Class<T> type) {
    return (type.isInstance(object)) ? type.cast(object) : null;
  }
}
