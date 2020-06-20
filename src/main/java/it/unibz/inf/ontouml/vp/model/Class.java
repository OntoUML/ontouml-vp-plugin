package it.unibz.inf.ontouml.vp.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vp.plugin.model.*;
import com.vp.plugin.model.factory.IModelElementFactory;

import it.unibz.inf.ontouml.vp.utils.StereotypeUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of ModelElement to handle IClass and IDataType objects to be
 * serialized as ontouml-schema/Class
 *
 * @author Claudenir Fonseca
 * @author Tiago Prince Sales
 * @author Victor Viola
 */

public class Class implements ModelElement {

   private final IModelElement sourceModelElement;

   @SerializedName("type")
   @Expose
   private final String type;

   @SerializedName("id")
   @Expose
   private final String id;

   @SerializedName("name")
   @Expose
   private String name;

   @SerializedName("description")
   @Expose
   private String description;

   @SerializedName("properties")
   @Expose
   private Set<Property> properties;

   @SerializedName("literals")
   @Expose
   private LinkedList<Literal> literals;

   @SerializedName("propertyAssignments")
   @Expose
   private JsonObject propertyAssignments;

   @SerializedName("stereotypes")
   @Expose
   private List<String> stereotypes;

   @SerializedName("isAbstract")
   @Expose
   private boolean isAbstract;

   @SerializedName("isDerived")
   @Expose
   private boolean isDerived;

   @SerializedName("allowed")
   @Expose
   private JsonArray restrictedTo;

   @SerializedName("isExtensional")
   @Expose
   private JsonElement isExtensional;

   @SerializedName("isPowertype")
   @Expose
   private JsonElement isPowertype;

   @SerializedName("order")
   @Expose
   private String order;

   private Class(IModelElement source) {
      this.sourceModelElement = source;
      this.type = ModelElement.TYPE_CLASS;
      this.id = source.getId();
      this.properties = null;
      this.stereotypes = null;
      this.literals = null;
   }

   public Class(IClass source) {
      this((IModelElement) source);

      final IAttribute[] attributes = source.toAttributeArray();
      for (int i = 0; attributes != null && i < attributes.length; i++) {
         addProperties(new Property(attributes[i]));
      }

      final String[] stereotypes = source.toStereotypeArray();
      for (int i = 0; stereotypes != null && i < stereotypes.length; i++) {
         addStereotype(stereotypes[i]);
      }

      if (this.stereotypes != null && this.stereotypes.contains(StereotypeUtils.STR_ENUMERATION)) {
         IEnumerationLiteral[] literalArray = source.toEnumerationLiteralArray();
         for (int i = 0; literalArray != null && i < literalArray.length; i++)
            addLiteral(new Literal(literalArray[i]));
      }

      setAbstract(source.isAbstract());
      setPropertyAssignments(ModelElement.transformPropertyAssignments(source));

      if (source.getName().trim().startsWith("/")) {
         setName(source.getName().substring(1));
         this.isDerived = true;
      } else {
         setName(source.getName().trim());
      }

      setDescription(source.getDescription());

      loadTags(source);
   }

   public Class(IDataType source) {
      this((IModelElement) source);

      addStereotype(StereotypeUtils.STR_DATATYPE);
      setAbstract(false);
      setDerived(false);

      setPropertyAssignments(ModelElement.transformPropertyAssignments(source));

      if (source.getName().trim().startsWith("/")) {
         setName(source.getName().substring(1));
         this.isDerived = true;
      } else {
         setName(source.getName().trim());
      }

      if (restrictedTo == null) {
         restrictedTo = new JsonArray();
         restrictedTo.add("abstract");
      }

   }

   public Class(IClass source, HashSet<String> modelElements) {
      this((IModelElement) source);

      final IAttribute[] attributes = source.toAttributeArray();
      for (int i = 0; attributes != null && i < attributes.length; i++) {
         if (modelElements.contains(attributes[i].getId()))
            addProperties(new Property(attributes[i]));
      }

      final String[] stereotypes = source.toStereotypeArray();
      for (int i = 0; stereotypes != null && i < stereotypes.length; i++) {
         addStereotype(stereotypes[i]);
      }

      if (this.stereotypes != null && this.stereotypes.contains(StereotypeUtils.STR_ENUMERATION)) {
         IEnumerationLiteral[] literalArray = source.toEnumerationLiteralArray();
         for (int i = 0; literalArray != null && i < literalArray.length; i++)
            addLiteral(new Literal(literalArray[i]));
      }

      setAbstract(source.isAbstract());
      setPropertyAssignments(ModelElement.transformPropertyAssignments(source));

      if (source.getName().trim().startsWith("/")) {
         setName(source.getName().substring(1));
         this.isDerived = true;
      } else {
         setName(source.getName().trim());
      }

      setDescription(source.getDescription());

      loadTags(source);
   }

   private void loadTags(IClass source) {
      if (source.getTaggedValues() != null) {
         final Iterator<?> values = source.getTaggedValues().taggedValueIterator();

         while (values.hasNext()) {
            try {
               final ITaggedValue value = (ITaggedValue) values.next();
               final JsonParser parser = new JsonParser();

               if (value.getName().equals(StereotypeUtils.PROPERTY_RESTRICTED_TO)) {
                  String valueString = value.getValueAsString();
                  valueString = valueString.trim()
                          .replaceAll(" +", "")
                          .replaceAll(",", "\",\"");

                  final JsonElement restrictedTo = !valueString.equals("") ? parser.parse("[\"" + valueString + "\"]") : parser.parse("[]");
                  this.restrictedTo = restrictedTo.isJsonArray() && ((JsonArray) restrictedTo).size() > 0 ?
                          (JsonArray) restrictedTo : null;
               }

               if (value.getName().equals(StereotypeUtils.PROPERTY_IS_EXTENSIONAL)) {
                  if (source.hasStereotype(StereotypeUtils.STR_COLLECTIVE))
                     this.isExtensional = parser.parse(Boolean.valueOf(value.getValueAsString()).toString());
                  else
                     this.isExtensional = parser.parse("null");
               }

               if (value.getName().equals(StereotypeUtils.PROPERTY_IS_POWERTYPE)) {
                  if (source.hasStereotype(StereotypeUtils.STR_TYPE))
                     this.isPowertype = parser.parse(Boolean.valueOf(value.getValueAsString()).toString());
                  else
                     this.isPowertype = parser.parse("null");
               }

               if (value.getName().equals(StereotypeUtils.PROPERTY_ORDER))
                  this.order = value.getValueAsString();

            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      }
   }

   @Override
   public IModelElement getSourceModelElement() {
      return this.sourceModelElement;
   }

   @Override
   public String getId() {
      return getSourceModelElement().getId();
   }

   @Override
   public String getOntoUMLType() {
      return this.type;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = ModelElement.safeGetString(name);
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = ModelElement.safeGetString(description);
      ;
   }

   public Set<Property> getProperties() {
      return properties;
   }

   public void addProperties(Property property) {
      if (this.properties == null)
         this.properties = new HashSet<Property>();

      this.properties.add(property);
   }

   public void removeProperties(Property property) {
      if (this.properties != null && this.properties.contains(property))
         this.properties.remove(property);
   }

   public JsonObject getPropertyAssignments() {
      return propertyAssignments;
   }

   public void setPropertyAssignments(JsonObject propertyAssignments) {
      this.propertyAssignments = propertyAssignments;
   }

   public List<String> getStereotypes() {
      return this.stereotypes;
   }

   public void setStereotypes(List<String> stereotypes) {
      this.stereotypes = stereotypes;
   }

   public String getStereotype(int position) {
      return this.stereotypes.get(position);
   }

   public void addStereotype(String name) {
      if (this.stereotypes == null)
         this.stereotypes = new ArrayList<String>();

      this.stereotypes.add(name);
   }

   public void removeStereotype(String name) {
      if (this.stereotypes != null && this.stereotypes.contains(name))
         this.stereotypes.remove(name);
   }

   public boolean isAbstract() {
      return this.isAbstract;
   }

   public void setAbstract(boolean isAbstract) {
      this.isAbstract = isAbstract;
   }

   public boolean isDerived() {
      return this.isDerived;
   }

   public void setDerived(boolean isDerived) {
      this.isDerived = isDerived;
   }

   public LinkedList<Literal> getLiterals() {
      return literals;
   }

   public void setLiterals(LinkedList<Literal> literals) {
      this.literals = literals;
   }

   public void addLiteral(Literal literal) {
      if (getLiterals() == null) {
         setLiterals(new LinkedList<Literal>());
      }

      this.literals.add(literal);
   }

   public static Set<IClass> getParents(IClass _class) {
		final Set<IClass> parents = new HashSet<IClass>();
		final ISimpleRelationship[] relationships = _class.toToRelationshipArray();

		for (int i = 0; relationships != null && i < relationships.length; i++) {
			if(relationships[i] instanceof IGeneralization) {
				final IGeneralization generalization = (IGeneralization) relationships[i];
				final IModelElement parent = Generalization.getGeneral(generalization);

				if (parent instanceof IClass) {
					parents.add((IClass) parent);
				}
			}
		}

		return parents;
	}

	public static Set<IClass> getChildren(IClass _class) {
		final Set<IClass> children = new HashSet<IClass>();
		final ISimpleRelationship[] relationships = _class.toFromRelationshipArray();

		for (int i = 0; relationships != null && i < relationships.length; i++) {
			if(relationships[i] instanceof IGeneralization) {
				final IGeneralization generalization = (IGeneralization) relationships[i];
				final IModelElement child = Generalization.getSpecific(generalization);

				if (child instanceof IClass) {
					children.add((IClass) child);
				}
			}
		}

		return children;
	}

	public static Set<IClass> getAncestors(IClass _class) {
		final Set<IClass> ancestors = new HashSet<IClass>();
		final Set<IClass> parents = getChildren(_class);

		for (IClass parent : parents) {
			ancestors.addAll(getDescendants(parent));
			ancestors.add(parent);
		}

		return ancestors;
	}

	public static Set<IClass> getDescendants(IClass _class) {
		final Set<IClass> descendants = new HashSet<IClass>();
		final Set<IClass> children = getChildren(_class);

		for (IClass child : children) {
			descendants.addAll(getDescendants(child));
			descendants.add(child);
		}

		return descendants;
	}

	public static void applyOnChildren(IClass _class, Consumer<IClass> function) {
		final Set<IClass> children = getChildren(_class);

		for (IClass child : children) {
			function.accept(child);
		}
	}

	public static void applyOnParents(IClass _class, Consumer<IClass>  function) {
		final Set<IClass> parents = getParents(_class);

		for (IClass parent : parents) {
			function.accept(parent);
		}
	}

	public static void applyOnDescendants(IClass _class, Function<IClass,Boolean> function) {
		final Set<IClass> children = getChildren(_class);

		for (IClass child : children) {
			final boolean shouldContinue = function.apply(child);
			if(shouldContinue) {
				applyOnDescendants(child, function);
			}
		}
	}

	public static void applyOnAncestors(IClass _class, Function<IClass,Boolean> function) {
		final Set<IClass> parents = getParents(_class);

		for (IClass parent : parents) {
			final boolean shouldContinue = function.apply(parent);
			if(shouldContinue) {
				applyOnAncestors(parent, function);
			}
		}
   }

   // public static ITaggedValue getRestrictedToValue(IClass _class) {
      // final String propertyName = StereotypeUtils.PROPERTY_RESTRICTED_TO;
      // ITaggedValueContainer container = _class.getTaggedValues();
      // ITaggedValue restrictedTo = container != null ?
      //       container.getTaggedValueByName(propertyName) :
      //       null ;
      
      // restrictedTo = container.createTaggedValue();
      // container.removeTaggedValue(arg0);
      // restrictedTo.setTagDefinition(arg0);

      
      // if(container == null) { return null; }

      // IStereotype stereotype = StereotypeUtils.getUniqueStereotype(_class);
      // ITaggedValueDefinitionContainer definitionContainer =
      //       stereotype.getTaggedValueDefinitions();
      // ITaggedValueDefinition definition = 
      //       definitionContainer.getTaggedValueDefinitionByName(propertyName);
      
      // definition.cre

      // if(
      //    stereotype == null ||
      //    StereotypeUtils.STEREOTYPE_ELEMENTS.containsKey(stereotype.getName())
      // ) {
      //    return null;
      // }
   // }

   public static void setRestrictedTo(IClass _class, String restrictions) {
		if(_class.getTaggedValues() == null) { 
			return ; 
		}

		Iterator<?> values = _class.getTaggedValues().taggedValueIterator();
			
		while(values != null && values.hasNext()) {
			final ITaggedValue value = (ITaggedValue) values.next();
			
			if(value.getName().equals(StereotypeUtils.PROPERTY_RESTRICTED_TO)) {
				final List<String> sortList = Arrays.asList(restrictions.split("\\s+"));
				Collections.sort(sortList);
				final Set<String> noDuplicates = new LinkedHashSet<String>(sortList);
				final String newRestrictions = noDuplicates.toString()
						.replaceAll("[\\[\\],]", "").trim();
				final String currentRestrictions = value.getValueAsString() != null ?
						value.getValueAsString() : "" ;
				
				if(!currentRestrictions.equals(newRestrictions)) {
					value.setValue(newRestrictions);
				}

				return ;
			}
      }


      
      // final ITaggedValueContainer container = _class.getTaggedValues();
      // ITaggedValue restrictedTo = container != null ? 
      //       container.getTaggedValueByName(StereotypeUtils.PROPERTY_RESTRICTED_TO) :
      //       null;
      
      // if(restrictedTo == null) {
      //    final IStereotype stereotype = StereotypeUtils.getUniqueStereotype(_class);
         
      //    if (StereotypeUtils.STEREOTYPE_ELEMENTS.containsValue(stereotype)) {
      //       IModelElementFactory.instance().createTaggedValue();
      //       ITaggedValueDefinitionContainer stereotype.getTaggedValueDefinitions();
      //    } else {
      //       return ;
      //    }
      // }

      // final String oldRestrictions = restrictedTo != null ?
      //       restrictedTo.getValueAsString() : null;
      // final String newRestrictions = Arrays.stream(restrictions.split("\\s+"))
      //       .distinct().collect(Collectors.joining(" "));
      
      // if(oldRestrictions == null || !oldRestrictions.equals(newRestrictions)) {
      //    restrictedTo.setValue(newRestrictions);
      // }
	}

	private static void setDefaultRestrictedTo(IClass element, String stereotypeName) {
		final String defaultNature = StereotypeUtils.getDefaultNature(stereotypeName);
		setRestrictedTo(element, defaultNature);
	}

	public static void setDefaultRestrictedTo(IClass _class) {
      setDefaultRestrictedTo(_class, 
         StereotypeUtils.getUniqueStereotypeName(_class));
	}

	public static String getRestrictedTo(IClass _class) {
		// final ITaggedValueContainer container = _class.getTaggedValues();
		// final Iterator<?> values = container != null ? container.taggedValueIterator() : null;

		// while(values != null && values.hasNext()) {
		// 	final ITaggedValue value = (ITaggedValue) values.next();
		// 	if(value.getName().equals(StereotypeUtils.PROPERTY_RESTRICTED_TO)) {
		// 		return value.getValueAsString();
		// 	}
		// }

      // return null;
      
      final ITaggedValueContainer container = _class.getTaggedValues();
      final ITaggedValue restrictedTo = container != null ? 
            container.getTaggedValueByName(StereotypeUtils.PROPERTY_RESTRICTED_TO) :
            null;
      
      return restrictedTo != null ? restrictedTo.getValueAsString() : null;
	}

	// private static Set<String> getRestrictedToAsSet(IClass _class) {
	// 	String restrictionsString = getRestrictedTo(_class);
	// 	restrictionsString = restrictionsString == null ? "" : restrictionsString;

	// 	final List<String> sortList = Arrays.asList(restrictionsString.split("\\s+"));
	// 	Collections.sort(sortList);

	// 	final Set<String> noDuplicates = new LinkedHashSet<String>(sortList);

	// 	return noDuplicates;
	// }

	public static String getRestrictedTo(Set<IClass> classes) {
		// final Set<String> classesRestrictions = new LinkedHashSet<>();

		// for (IClass _class : classes) {
		// 	classesRestrictions.addAll(getRestrictedToAsSet(_class));
		// }

      // return classesRestrictions.toString().replaceAll("[\\[\\],]", "").trim();
      String classesRestrictions = "";

      for (IClass _class : classes) {
         final String _classRestrictions = getRestrictedTo(_class);
         if (_classRestrictions != null) {
            classesRestrictions += _classRestrictions;
         }
      }

      classesRestrictions = Arrays.stream(classesRestrictions.split("\\s+"))
            .distinct().collect(Collectors.joining(" "));
      
      return !classesRestrictions.equals("") ? classesRestrictions : null;
	}

	public static void addRestrictedTo(IClass _class, String additionalNatures) {
		final String allowedNatures = getRestrictedTo(_class);
		final String newRestrictions = String.join(" ", allowedNatures, additionalNatures)
				.replaceAll("null", "");;
		setRestrictedTo(_class, newRestrictions);
   }
   
   public static boolean hasRestrictedTo(IClass _class) {
      final ITaggedValueContainer container = _class.getTaggedValues();
      final ITaggedValue restrictedTo = container != null ?
            container.getTaggedValueByName(StereotypeUtils.PROPERTY_RESTRICTED_TO) :
            null;
      
      return restrictedTo != null;
   }
   
   public static String getOrder(IClass _class) {
      final ITaggedValueContainer container = _class.getTaggedValues();
      final ITaggedValue order = container != null ?
            container.getTaggedValueByName(StereotypeUtils.PROPERTY_ORDER) :
            null;
      
      return order != null ? order.getValueAsString() : null ;
   }

   public static void setOrder(IClass _class, String newOrder) {
      final ITaggedValueContainer container = _class.getTaggedValues();
      final ITaggedValue order = container != null ?
            container.getTaggedValueByName(StereotypeUtils.PROPERTY_ORDER) :
            null;
      
      if (order != null) {
         order.setValue(newOrder);
      }
   }

   public static boolean hasOrder(IClass _class) {
      final ITaggedValueContainer container = _class.getTaggedValues();
      final ITaggedValue order = container != null ?
            container.getTaggedValueByName(StereotypeUtils.PROPERTY_ORDER) :
            null;
      
      return order != null;
   }

   public static boolean getIsPowertype(IClass _class) {
      final ITaggedValueContainer container = _class.getTaggedValues();
      final ITaggedValue isPowertype = container != null ?
            container.getTaggedValueByName(StereotypeUtils.PROPERTY_IS_POWERTYPE) :
            null;
      final String isPowertypeValue = isPowertype != null ?
            isPowertype.getValueAsString() : "" ;
      
      return Boolean.parseBoolean(isPowertypeValue);
   }

   public static void setIsPowertype(IClass _class, boolean isPowertypeValue) {
      final ITaggedValueContainer container = _class.getTaggedValues();
      final ITaggedValue isPowertype = container != null ?
            container.getTaggedValueByName(StereotypeUtils.PROPERTY_IS_POWERTYPE) :
            null;
      
      if (isPowertype != null) {
         isPowertype.setValue(isPowertypeValue);
      }
   }

   public static boolean hasIsPowertype(IClass _class) {
      final ITaggedValueContainer container = _class.getTaggedValues();
      final ITaggedValue isPowertype = container != null ?
            container.getTaggedValueByName(StereotypeUtils.PROPERTY_IS_POWERTYPE) :
            null;
      
      return isPowertype != null;
   }

   public static boolean getIsExtenstional(IClass _class) {
      final ITaggedValueContainer container = _class.getTaggedValues();
      final ITaggedValue isExtenstional = container != null ?
            container.getTaggedValueByName(StereotypeUtils.PROPERTY_IS_EXTENSIONAL) :
            null;
      final String isExtenstionalValue = isExtenstional != null ?
            isExtenstional.getValueAsString() : "" ;
      
      return Boolean.parseBoolean(isExtenstionalValue);
   }

   public static void setIsExtenstional(IClass _class, boolean isExtenstionalValue) {
      final ITaggedValueContainer container = _class.getTaggedValues();
      final ITaggedValue isExtenstional = container != null ?
            container.getTaggedValueByName(StereotypeUtils.PROPERTY_IS_EXTENSIONAL) :
            null;
      
      if (isExtenstional != null) {
         isExtenstional.setValue(isExtenstionalValue);
      }
   }

   public static boolean hasIsExtenstional(IClass _class) {
      final ITaggedValueContainer container = _class.getTaggedValues();
      final ITaggedValue isExtenstional = container != null ?
            container.getTaggedValueByName(StereotypeUtils.PROPERTY_IS_EXTENSIONAL) :
            null;
      
      return isExtenstional != null;
   }

   public static boolean getIsDerived(IClass _class) {
      return _class.getName().trim().startsWith("/");
   }

   public static void setIsDerived(IClass _class, boolean isDerived) {
      String currentName = _class.getName().trim();

      if (getIsDerived(_class)) {
         _class.setName(currentName.substring(1));
      }

      if (getIsDerived(_class)) {
         _class.setName("/" + currentName);
      }
   }

}