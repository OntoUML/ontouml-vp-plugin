package it.unibz.inf.ontouml.vp.model.ontouml;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unibz.inf.ontouml.vp.model.ontouml.model.Cardinality;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CardinalityTest {

  @Test
  @DisplayName("A cardinality of \"1\" should be valid")
  void OneShouldBeValid() {
    Cardinality c = new Cardinality("1");
    assertThat(c.isValid()).isTrue();
  }

  @Test
  @DisplayName("A cardinality of \"1\" sets lowerBound and upperBound to 1")
  void oneShouldSetLowerUpperBounds() {
    Cardinality c = new Cardinality("1");
    assertThat(c.getLowerBound()).hasValue("1");
    assertThat(c.getUpperBound()).hasValue("1");
    assertThat(c.getLowerBoundAsInt()).isEqualTo(1);
    assertThat(c.getUpperBoundAsInt()).isEqualTo(1);
  }

  @Test
  @DisplayName("A cardinality of \"1..1\" should be valid")
  void oneToOneShouldBeValid() {
    Cardinality c = new Cardinality("1..1");
    assertThat(c.isValid()).isTrue();
  }

  @Test
  @DisplayName("A cardinality of \"1..1\" sets lowerBound and upperBound to 1")
  void oneToOneShouldSetLowerUpperBounds() {
    Cardinality c = new Cardinality("1..1");
    assertThat(c.getLowerBound()).hasValue("1");
    assertThat(c.getUpperBound()).hasValue("1");
    assertThat(c.getLowerBoundAsInt()).isEqualTo(1);
    assertThat(c.getUpperBoundAsInt()).isEqualTo(1);
  }

  @Test
  @DisplayName("A cardinality of \"1..*\" should be valid")
  void oneToManyShouldBeValid() {
    Cardinality c = new Cardinality("1..*");
    assertThat(c.isValid()).isTrue();
  }

  @Test
  @DisplayName(
      "A cardinality of \"1..*\" sets lowerBound to 1 and upperBound to */Integer.MAX_VALUE")
  void oneToManyShouldSetLowerUpperBounds() {
    Cardinality c = new Cardinality("1..*");
    assertThat(c.getLowerBound()).hasValue("1");
    assertThat(c.getUpperBound()).hasValue("*");
    assertThat(c.getLowerBoundAsInt()).isEqualTo(1);
    assertThat(c.getUpperBoundAsInt()).isEqualTo(Integer.MAX_VALUE);
  }

  @Test
  @DisplayName("A cardinality of \"0..*\" should be valid")
  void zeroToManyShouldBeValid() {
    Cardinality c = new Cardinality("0..*");
    assertThat(c.isValid()).isTrue();
  }

  @Test
  @DisplayName("A cardinality of \"0..*\" sets lowerBound to 0 and upperBound to Integer.MAX_VALUE")
  void zeroToManyShouldSetLowerUpperBounds() {
    Cardinality c = new Cardinality("0..*");
    assertThat(c.getLowerBound()).hasValue("0");
    assertThat(c.getUpperBound()).hasValue("*");
    assertThat(c.getLowerBoundAsInt()).isEqualTo(0);
    assertThat(c.getUpperBoundAsInt()).isEqualTo(Integer.MAX_VALUE);
  }

  @Test
  @DisplayName("A cardinality of \"1..3\" should be valid")
  void zeroToThreeShouldBeValid() {
    Cardinality c = new Cardinality("1..3");
    assertThat(c.isValid()).isTrue();
  }

  @Test
  @DisplayName("A cardinality of \"1..3\" sets lowerBound to 1 and upperBound to 3")
  void oneToThreeShouldSetLowerUpperBounds() {
    Cardinality c = new Cardinality("1..3");
    assertThat(c.getLowerBound()).hasValue("1");
    assertThat(c.getUpperBound()).hasValue("3");
    assertThat(c.getLowerBoundAsInt()).isEqualTo(1);
    assertThat(c.getUpperBoundAsInt()).isEqualTo(3);
  }

  @Test
  @DisplayName("A cardinality of \"a..b\" should NOT be valid")
  void cardinalitiesWithLettersAreInvalid() {
    Cardinality c = new Cardinality("a..b");
    assertThat(c.isValid()).isFalse();
  }

  @Test
  @DisplayName(
      "An invalid cardinality of \"a..b\" should still set lower and upper if in the correct"
          + " pattern")
  void invalidCardinalitySetsLowerUpper() {
    Cardinality c = new Cardinality("a..b");
    assertThat(c.getLowerBound()).hasValue("a");
    assertThat(c.getUpperBound()).hasValue("b");
  }

  @Test
  @DisplayName(
      "An invalid cardinality of \"a..b\" should throw exceptions on getLowerBoundAsInt() and"
          + " getUpperBoundAsInt()")
  void invalidCardinalityShouldThrowExceptionOnGetAsInt() {
    Cardinality c = new Cardinality("a..b");
    assertThrows(NumberFormatException.class, () -> c.getLowerBoundAsInt());
    assertThrows(NumberFormatException.class, () -> c.getUpperBoundAsInt());
  }
}
