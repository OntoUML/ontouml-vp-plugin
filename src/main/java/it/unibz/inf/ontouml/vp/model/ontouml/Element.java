package it.unibz.inf.ontouml.vp.model.ontouml;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import it.unibz.inf.ontouml.vp.model.ontouml.serialization.ElementSerializer;

import java.util.*;

@JsonSerialize(using = ElementSerializer.class)
public abstract class Element implements Comparable<Element> {
   String id;
   MultilingualText name;
   MultilingualText description;

   public Element(String id, MultilingualText name) {
      this.id = id != null ? id : UUID.randomUUID().toString();
      this.name = name != null ? name : new MultilingualText();
      this.description = new MultilingualText();
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      if(id==null)
         throw new NullPointerException("Cannot set null id.");

      this.id = id;
   }

   public MultilingualText getName() {
      return this.name;
   }

   public Optional<String> getNameIn(String language) {
      return name.getText(language);
   }

   public Optional<String> getFirstName() {
      return name.getText();
   }

   public void setName(MultilingualText name) {
      this.name = name;
   }

   public void addName(String languageTag, String value) {
      this.name.putText(languageTag, value);
   }

   public void addName(String value) {
      this.name.putText(value);
   }

   public void removeNameIn(String languageTag) {
      this.name.removeTextIn(languageTag);
   }

   public void removeAllNames() {
      this.name.removeAll();
   }

   public MultilingualText getDescription() {
      return this.description;
   }

   public Optional<String> getDescriptionIn(String language) {
      return description.getText(language);
   }

   public Optional<String> getFirstDescription() {
      return description.getText();
   }

   public void setDescription(MultilingualText description) {
      this.description = description;
   }

   public void addDescription(String languageTag, String value) {
      this.description.putText(languageTag, value);
   }

   public void addDescription(String value) {
      this.description.putText(value);
   }

   public void removeDescription(String languageTag) {
      this.description.removeTextIn(languageTag);
   }

   public void removeAllDescriptions() {
      this.description.removeAll();
   }

   @Override
   public int compareTo(Element element) {
      return this.getFirstName().orElse("").compareTo(element.getFirstName().orElse(""));
   }
}
