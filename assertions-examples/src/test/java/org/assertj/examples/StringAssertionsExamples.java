/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2016 the original author or authors.
 */
package org.assertj.examples;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.presentation.StandardRepresentation.STANDARD_REPRESENTATION;
import static org.assertj.core.util.Arrays.array;
import static org.assertj.core.util.Lists.newArrayList;

import java.io.File;
import java.util.Comparator;
import java.util.regex.Pattern;

import org.junit.Test;

/**
 * String assertions specific examples
 *
 * @author Joel Costigliola
 */
public class StringAssertionsExamples extends AbstractAssertionsExamples {

  @Test
  public void string_assertions_examples() {
    assertThat("Frodo").startsWith("Fro").endsWith("do").hasSize(5);
    assertThat("Frodo").contains("rod").doesNotContain("fro");
    assertThat("Frodo").containsOnlyOnce("do");
    assertThat("Frodo").isSubstringOf("Frodon");
    try {
      assertThat("Frodo").containsOnlyOnce("o");
    } catch (AssertionError e) {
      logAssertionErrorMessage("String containsOnlyOnce", e);
    }
    // contains accepts multiple String to avoid chaining contains several times.
    assertThat("Gandalf the grey").contains("alf", "grey");
    try {
      assertThat("Gandalf the grey").contains("alf", "grey", "wizard", "ring");
    } catch (AssertionError e) {
      logAssertionErrorMessage("String contains with several values", e);
    }

    String bookDescription = "{ 'title':'Games of Thrones', 'author':'George Martin'}";
    assertThat(bookDescription).containsSequence("{", "title", "Games of Thrones", "}");

    try {
      assertThat(bookDescription).containsSequence("{", "title", "author", "Games of Thrones", "}");
    } catch (AssertionError e) {
      logAssertionErrorMessage("String containsSequence with incorrect order", e);
    }

    // you can ignore case for equals check
    assertThat("Frodo").isEqualToIgnoringCase("FROdO").hasSameSizeAs("12345");
    assertThat("Frodo".length()).isGreaterThan("Sam".length());

    // using regex
    assertThat("Frodo").matches("..o.o").doesNotMatch(".*d");

    // using regex - check that a substring matching a pattern exists or doesn't exist
    assertThat("Jerry Elaine George Kramer")
                        .containsPattern("Je.{2}y")
                        .doesNotContainPattern("Newm.n");
    // you can use precompiled patterns too
    assertThat("Jerry Elaine George Kramer")
                        .containsPattern(Pattern.compile("je.{2}y", Pattern.CASE_INSENSITIVE))
                        .doesNotContainPattern(Pattern.compile("newm.n", Pattern.CASE_INSENSITIVE));

    // check for empty string, or not.
    assertThat("").isEmpty();
    assertThat("").isNullOrEmpty();
    assertThat("not empty").isNotEmpty();

    // check size.
    assertThat("C-3PO").hasSameSizeAs("R2-D2").hasSize(5);

    assertThat("3210").containsOnlyDigits();

    //
    assertThat("Frodo").doesNotStartWith("fro")
                       .doesNotEndWith("don");
  }

  @Test
  public void string_assertions_with_custom_comparison_examples() {

    // standard assertion are based on equals() method, but what if you want to ignore String case ?
    // one way is to use isEqualToIgnoringCase
    assertThat("Frodo").isEqualToIgnoringCase("FROdO");
    // this is good but it doest not work with the other assertions : contains, startsWith, ...
    // if you want consistent ignoring case comparison, you must use a case iNsenSiTIve comparison strategy :
    // We see that assertions succeeds even though case is not the same - ok that was the point ;-)
    assertThat("Frodo").usingComparator(caseInsensitiveStringComparator).startsWith("fr").endsWith("ODO");

    // All assertions made after usingComparator(caseInsensitiveStringComparator) are based on the given comparator ...
    assertThat("Frodo").usingComparator(caseInsensitiveStringComparator).contains("fro").doesNotContain("don");
    // ... but a new assertions is not
    // assertThat("Frodo").startsWith("fr").endsWith("ODO"); // FAILS !!!

    // Last thing : matches or doesNotMatch don't use the given comparator
    assertThat("Frodo").usingComparator(caseInsensitiveStringComparator).doesNotMatch("..O.O");
    try {
      // failed assertion with specific comparator mention the comparison strategy to help interpreting the error
      assertThat("Frodo").usingComparator(caseInsensitiveStringComparator).startsWith("FROO");
    } catch (AssertionError e) {
      logAssertionErrorMessage("startsWith with custom comparator", e);
    }
  }

  @Test
  public void charSequence_subclasses_assertions_examples() {
    // StringBuilder have same assertions as String
    StringBuilder stringBuilder = new StringBuilder("Frodo");
    assertThat(stringBuilder).startsWith("Fro").endsWith("do").hasSize(5);
    assertThat(stringBuilder).contains("rod").doesNotContain("fro");
    assertThat(stringBuilder).containsOnlyOnce("do");

    Comparator<CharSequence> caseInsensitiveComparator = new Comparator<CharSequence>() {
      @Override
      public int compare(CharSequence s1, CharSequence s2) {
        return s1.toString().toLowerCase().compareTo(s2.toString().toLowerCase());
      }
    };
    assertThat(stringBuilder).usingComparator(caseInsensitiveComparator).contains("fro").doesNotContain("don");

    // StringBuilder have same assertions as String
    StringBuffer stringBuffer = new StringBuffer("Frodo");
    assertThat(stringBuffer).startsWith("Fro").endsWith("do").hasSize(5);
    assertThat(stringBuffer).contains("rod").doesNotContain("fro");
    assertThat(stringBuffer).containsOnlyOnce("do");
    assertThat(stringBuffer).usingComparator(caseInsensitiveComparator).contains("fro").doesNotContain("don");
  }

  @Test
  public void xml_assertions_examples() {

    String expectedXml = "<rings>\n" +
                         "  <bearer>\n" +
                         "    <name>Frodo</name>\n" +
                         "    <ring>\n" +
                         "      <name>one ring</name>\n" +
                         "      <createdBy>Sauron</createdBy>\n" +
                         "    </ring>\n" +
                         "  </bearer>\n" +
                         "</rings>";

    // XML String don't need to be formatted, AssertJ will format both actual and expected string before comparison.
    // Whatever how formatted your xml string is, isXmlEqualTo assertion is able to compare it with another xml String.
    String oneLineXml = "<rings><bearer><name>Frodo</name><ring><name>one ring</name><createdBy>Sauron</createdBy></ring></bearer></rings>";
    assertThat(oneLineXml).isXmlEqualTo(expectedXml);

    String xmlWithNewLine = "<rings>\n" +
                            "<bearer>   \n" +
                            "  <name>Frodo</name>\n" +
                            "  <ring>\n" +
                            "    <name>one ring</name>\n" +
                            "    <createdBy>Sauron</createdBy>\n" +
                            "  </ring>\n" +
                            "</bearer>\n" +
                            "</rings>";
    assertThat(xmlWithNewLine).isXmlEqualTo(expectedXml);
    assertThat(xmlWithNewLine).isXmlEqualTo(oneLineXml);

    // You can easily compare your XML String to the content of an XML file.
    assertThat(oneLineXml).isXmlEqualToContentOf(new File("src/test/resources/formatted.xml"));

    // Since both actual and expected xml string are formatted as XML document, it is easy to see what were the
    // differences.
    // here : bearer is not Sauron but should Frodo !
    String unexpectedXml = "<rings>\n" +
                           "<bearer>   \n" +
                           "  <name>Sauron</name>\n" +
                           "  <ring><name>one ring</name><createdBy>Sauron</createdBy></ring>\n" +
                           "</bearer>\n" +
                           "</rings>";
    // uncomment to see how IDE compares the two XML string :
    // assertThat(unexpectedXml).isXmlEqualTo(expectedXml);

    // Log the error message, which is not "beautiful" since it is made for IDE comparison.
    try {
      assertThat(unexpectedXml).isXmlEqualTo(expectedXml);
    } catch (AssertionError e) {
      logAssertionErrorMessage("XML string comparison (isXmlEqualTo)", e);
    }

  }

  @Test
  public void use_hexadecimal_representation_in_error_messages() {
    try {
      assertThat("µµµ").inHexadecimal().contains("μμμ");
    } catch (AssertionError e) {
      logAssertionErrorMessage("asHexadecimal() for String", e);
    }
  }

  @Test
  public void use_unicode_representation_in_error_messages() {
    try {
      assertThat("µµµ").inUnicode().contains("μμμ");
    } catch (AssertionError e) {
      logAssertionErrorMessage("asUnicode() for String", e);
    }
  }

  @Test
  public void switch_to_String_assertion() {
    Object title = "Game of Thrones";
    assertThat(title).asString().endsWith("ones");
  }

  @Test
  public void containsPattern_assertion_example() {
    assertThat("Frodo").containsPattern("Fr.d");
    assertThat("Frodo").containsPattern(Pattern.compile("Fr.d"));
  }
  
  @Test
  public void multine_collection_formatting() {
    String[] greatBooks = array("A Game of Thrones", "The Lord of the Rings", "Assassin's Apprentice  ....", "Disc World");
    String smartFormat = STANDARD_REPRESENTATION.toStringOf(newArrayList(greatBooks));
    log.info(smartFormat);
  }

}
