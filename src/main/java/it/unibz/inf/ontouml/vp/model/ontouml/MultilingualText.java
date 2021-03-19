package it.unibz.inf.ontouml.vp.model.ontouml;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import it.unibz.inf.ontouml.vp.model.ontouml.deserialization.MultilingualTextDeserializer;
import it.unibz.inf.ontouml.vp.model.ontouml.serialization.MultilingualTextSerializer;
import java.util.*;

@JsonSerialize(using = MultilingualTextSerializer.class)
@JsonDeserialize(using = MultilingualTextDeserializer.class)
public class MultilingualText {
  private static String defaultLanguage = "en";
  private static String[] languagePreference = new String[] {defaultLanguage};

  private SortedMap<String, String> textMap = new TreeMap<>();

  public MultilingualText(MultilingualText base) {
    textMap.putAll(base.textMap);
  }

  public MultilingualText(String languageTag, String value) {
    this.putText(languageTag, value);
  }

  public MultilingualText(String value) {
    this.putText(value);
  }

  public MultilingualText(Map<String, String> multilingualTextMap) {
    textMap.putAll(multilingualTextMap);
  }

  public MultilingualText() {}

  public Map<String, String> getMap() {
    return Map.copyOf(textMap);
  }

  public Optional<String> getText() {
    for (String language : languagePreference) {
      if (textMap.containsKey(language)) return Optional.ofNullable(textMap.get(language));
    }

    if (textMap.size() > 0) return Optional.ofNullable(textMap.get(textMap.firstKey()));

    return Optional.empty();
  }

  public Optional<String> getText(String language) {
    if (textMap.containsKey(language)) return Optional.ofNullable(textMap.get(language));

    return getText();
  }

  public boolean isEmpty() {
    return this.textMap.isEmpty();
  }

  public int size() {
    return this.textMap.size();
  }

  public void putText(String languageTag, String value) {
    textMap.put(languageTag, value);
  }

  public void putText(String value) {
    putText(defaultLanguage, value);
  }

  public Collection<String> getTexts() {
    return textMap.values();
  }

  public Collection<String> getLanguages() {

    return textMap.keySet();
  }

  public boolean containsLanguage(String languageTag) {
    return textMap.containsKey(languageTag);
  }

  public void removeTextIn(String languageTag) {
    textMap.remove(languageTag);
  }

  public void removeAll() {
    textMap.clear();
  }

  public static void setDefaultLanguage(String language) {
    if (language == null)
      throw new NullPointerException("Cannot set a null value as the default language.");

    MultilingualText.defaultLanguage = language;
  }

  public static void setLanguagePreference(String[] languagePreference) {
    if (languagePreference == null)
      throw new NullPointerException("Cannot set a null preference array.");

    MultilingualText.languagePreference = languagePreference;
  }

  @Override
  public String toString() {
    if (textMap.size() <= 1) {
      return getText().orElse(null);
    }

    return textMap.toString();
  }
}
