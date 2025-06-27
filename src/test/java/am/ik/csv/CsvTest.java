/*
 * Copyright (C) 2025 Toshiaki Maki <makingx@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package am.ik.csv;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CsvTest {

	@Nested
	@DisplayName("CSV splitting tests (List-based)")
	class SplitTests {

		@Test
		@DisplayName("should split simple CSV with comma delimiter")
		void splitSimpleCsv() {
			Csv csv = Csv.of();
			String input = "name,age,city\r\nJohn,30,Tokyo\r\nJane,25,Osaka";

			List<String[]> result = csv.splitToArrays(input);

			assertThat(result).hasSize(3);
			assertThat(result.get(0)).containsExactly("name", "age", "city");
			assertThat(result.get(1)).containsExactly("John", "30", "Tokyo");
			assertThat(result.get(2)).containsExactly("Jane", "25", "Osaka");
		}

		@Test
		@DisplayName("should handle quoted fields with commas")
		void splitQuotedFieldsWithCommas() {
			Csv csv = Csv.of();
			String input = "name,description\r\n\"John Doe\",\"A person, who lives in Tokyo\"\r\n\"Jane Smith\",\"Another person, from Osaka\"";

			List<String[]> result = csv.splitToArrays(input);

			assertThat(result).hasSize(3);
			assertThat(result.get(0)).containsExactly("name", "description");
			assertThat(result.get(1)).containsExactly("John Doe", "A person, who lives in Tokyo");
			assertThat(result.get(2)).containsExactly("Jane Smith", "Another person, from Osaka");
		}

		@Test
		@DisplayName("should handle escaped quotes within quoted fields")
		void splitEscapedQuotes() {
			Csv csv = Csv.of();
			String input = "name,quote\r\n\"John\",\"He said \"\"Hello\"\" to me\"\r\n\"Jane\",\"She replied \"\"Hi there\"\"\"";

			List<String[]> result = csv.splitToArrays(input);

			assertThat(result).hasSize(3);
			assertThat(result.get(0)).containsExactly("name", "quote");
			assertThat(result.get(1)).containsExactly("John", "He said \"Hello\" to me");
			assertThat(result.get(2)).containsExactly("Jane", "She replied \"Hi there\"");
		}

		@Test
		@DisplayName("should handle line breaks within quoted fields")
		void splitLineBreaksInQuotedFields() {
			Csv csv = Csv.of();
			String input = "name,address\r\n\"John\",\"123 Main St\r\nTokyo, Japan\"\r\n\"Jane\",\"456 Oak Ave\r\nOsaka, Japan\"";

			List<String[]> result = csv.splitToArrays(input);

			assertThat(result).hasSize(3);
			assertThat(result.get(0)).containsExactly("name", "address");
			assertThat(result.get(1)).containsExactly("John", "123 Main St\r\nTokyo, Japan");
			assertThat(result.get(2)).containsExactly("Jane", "456 Oak Ave\r\nOsaka, Japan");
		}

		@Test
		@DisplayName("should handle empty fields")
		void splitEmptyFields() {
			Csv csv = Csv.of();
			String input = "name,,city\r\nJohn,,Tokyo\r\n,25,";

			List<String[]> result = csv.splitToArrays(input);

			assertThat(result).hasSize(3);
			assertThat(result.get(0)).containsExactly("name", "", "city");
			assertThat(result.get(1)).containsExactly("John", "", "Tokyo");
			assertThat(result.get(2)).containsExactly("", "25", "");
		}

		@Test
		@DisplayName("should handle single line without trailing line break")
		void splitSingleLineNoTrailingBreak() {
			Csv csv = Csv.of();
			String input = "name,age,city";

			List<String[]> result = csv.splitToArrays(input);

			assertThat(result).hasSize(1);
			assertThat(result.get(0)).containsExactly("name", "age", "city");
		}

		@Test
		@DisplayName("should handle empty input")
		void splitEmptyInput() {
			Csv csv = Csv.of();

			List<String[]> result = csv.splitToArrays("");

			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("should handle null input")
		void splitNullInput() {
			Csv csv = Csv.of();

			List<String[]> result = csv.splitToArrays(null);

			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("should handle LF only line endings")
		void splitLfLineEndings() {
			Csv csv = Csv.of();
			String input = "name,age\nJohn,30\nJane,25";

			List<String[]> result = csv.splitToArrays(input);

			assertThat(result).hasSize(3);
			assertThat(result.get(0)).containsExactly("name", "age");
			assertThat(result.get(1)).containsExactly("John", "30");
			assertThat(result.get(2)).containsExactly("Jane", "25");
		}

	}

	@Nested
	@DisplayName("CSV joining tests")
	class JoinTests {

		@Test
		@DisplayName("should join simple records")
		void joinSimpleRecords() {
			Csv csv = Csv.of();
			List<String[]> records = Arrays.asList(new String[] { "name", "age", "city" },
					new String[] { "John", "30", "Tokyo" }, new String[] { "Jane", "25", "Osaka" });

			String result = csv.joinArrays(records);

			assertThat(result).isEqualTo("name,age,city\r\nJohn,30,Tokyo\r\nJane,25,Osaka");
		}

		@Test
		@DisplayName("should quote fields containing commas")
		void joinFieldsWithCommas() {
			Csv csv = Csv.of();
			List<String[]> records = Arrays.asList(new String[] { "name", "description" },
					new String[] { "John Doe", "A person, who lives in Tokyo" },
					new String[] { "Jane Smith", "Another person, from Osaka" });

			String result = csv.joinArrays(records);

			assertThat(result).isEqualTo(
					"name,description\r\nJohn Doe,\"A person, who lives in Tokyo\"\r\nJane Smith,\"Another person, from Osaka\"");
		}

		@Test
		@DisplayName("should escape quotes in quoted fields")
		void joinFieldsWithQuotes() {
			Csv csv = Csv.of();
			List<String[]> records = Arrays.asList(new String[] { "name", "quote" },
					new String[] { "John", "He said \"Hello\" to me" },
					new String[] { "Jane", "She replied \"Hi there\"" });

			String result = csv.joinArrays(records);

			assertThat(result).isEqualTo(
					"name,quote\r\nJohn,\"He said \"\"Hello\"\" to me\"\r\nJane,\"She replied \"\"Hi there\"\"\"");
		}

		@Test
		@DisplayName("should quote fields containing line breaks")
		void joinFieldsWithLineBreaks() {
			Csv csv = Csv.of();
			List<String[]> records = Arrays.asList(new String[] { "name", "address" },
					new String[] { "John", "123 Main St\r\nTokyo, Japan" },
					new String[] { "Jane", "456 Oak Ave\r\nOsaka, Japan" });

			String result = csv.joinArrays(records);

			assertThat(result).isEqualTo(
					"name,address\r\nJohn,\"123 Main St\r\nTokyo, Japan\"\r\nJane,\"456 Oak Ave\r\nOsaka, Japan\"");
		}

		@Test
		@DisplayName("should handle empty fields")
		void joinEmptyFields() {
			Csv csv = Csv.of();
			List<String[]> records = Arrays.asList(new String[] { "name", "", "city" },
					new String[] { "John", "", "Tokyo" }, new String[] { "", "25", "" });

			String result = csv.joinArrays(records);

			assertThat(result).isEqualTo("name,,city\r\nJohn,,Tokyo\r\n,25,");
		}

		@Test
		@DisplayName("should handle null fields")
		void joinNullFields() {
			Csv csv = Csv.of();
			List<String[]> records = Arrays.asList(new String[] { "name", null, "city" },
					new String[] { null, "30", null });

			String result = csv.joinArrays(records);

			assertThat(result).isEqualTo("name,,city\r\n,30,");
		}

		@Test
		@DisplayName("should handle empty record list")
		void joinEmptyRecords() {
			Csv csv = Csv.of();

			String result = csv.joinArrays(Arrays.asList());

			assertThat(result).isEqualTo("");
		}

		@Test
		@DisplayName("should handle null record list")
		void joinNullRecords() {
			Csv csv = Csv.of();

			String result = csv.joinArrays(null);

			assertThat(result).isEqualTo("");
		}

		@Test
		@DisplayName("should handle single record")
		void joinSingleRecord() {
			Csv csv = Csv.of();
			List<String[]> records = Arrays.<String[]>asList(new String[] { "name", "age", "city" });

			String result = csv.joinArrays(records);

			assertThat(result).isEqualTo("name,age,city");
		}

	}

	@Nested
	@DisplayName("Round-trip tests")
	class RoundTripTests {

		@Test
		@DisplayName("should preserve data through split and join operations")
		void roundTripPreservesData() {
			Csv csv = Csv.of();
			List<String[]> originalRecords = Arrays.asList(new String[] { "name", "description", "notes" },
					new String[] { "John Doe", "A person, \"nice guy\"", "Lives in\r\nTokyo" },
					new String[] { "Jane Smith", "Another person", "From \"Osaka\"" });

			String csvText = csv.joinArrays(originalRecords);
			List<String[]> parsedRecords = csv.splitToArrays(csvText);

			assertThat(parsedRecords).hasSize(originalRecords.size());
			for (int i = 0; i < originalRecords.size(); i++) {
				assertThat(parsedRecords.get(i)).containsExactly(originalRecords.get(i));
			}
		}

	}

	@Nested
	@DisplayName("List-based API tests")
	class ListApiTests {

		@Test
		@DisplayName("should split line to list")
		void splitLineToList() {
			Csv csv = Csv.of();
			String line = "name,age,city";

			List<String> result = csv.splitLine(line);

			assertThat(result).containsExactly("name", "age", "city");
		}

		@Test
		@DisplayName("should split line to array using splitLineToArray")
		void splitLineToArray() {
			Csv csv = Csv.of();
			String line = "name,age,city";

			String[] result = csv.splitLineToArray(line);

			assertThat(result).containsExactly("name", "age", "city");
		}

		@Test
		@DisplayName("should split CSV to list of lists")
		void splitToListOfLists() {
			Csv csv = Csv.of();
			String input = "name,age\r\nJohn,30\r\nJane,25";

			List<List<String>> result = csv.split(input);

			assertThat(result).hasSize(3);
			assertThat(result.get(0)).containsExactly("name", "age");
			assertThat(result.get(1)).containsExactly("John", "30");
			assertThat(result.get(2)).containsExactly("Jane", "25");
		}

		@Test
		@DisplayName("should split CSV to arrays using splitToArrays")
		void splitToArrays() {
			Csv csv = Csv.of();
			String input = "name,age\r\nJohn,30\r\nJane,25";

			List<String[]> result = csv.splitToArrays(input);

			assertThat(result).hasSize(3);
			assertThat(result.get(0)).containsExactly("name", "age");
			assertThat(result.get(1)).containsExactly("John", "30");
			assertThat(result.get(2)).containsExactly("Jane", "25");
		}

		@Test
		@DisplayName("should join line from list")
		void joinLineFromList() {
			Csv csv = Csv.of();
			List<String> fields = List.of("name", "age", "city");

			String result = csv.joinLine(fields);

			assertThat(result).isEqualTo("name,age,city");
		}

		@Test
		@DisplayName("should join line from list with quoted fields")
		void joinLineFromListWithQuotes() {
			Csv csv = Csv.of();
			List<String> fields = List.of("John Doe", "30", "Tokyo, Japan");

			String result = csv.joinLine(fields);

			assertThat(result).isEqualTo("John Doe,30,\"Tokyo, Japan\"");
		}

		@Test
		@DisplayName("should join CSV from list of lists")
		void joinFromListOfLists() {
			Csv csv = Csv.of();
			List<List<String>> records = List.of(List.of("name", "age", "city"), List.of("John", "30", "Tokyo"),
					List.of("Jane", "25", "Osaka"));

			String result = csv.join(records);

			assertThat(result).isEqualTo("name,age,city\r\nJohn,30,Tokyo\r\nJane,25,Osaka");
		}

		@Test
		@DisplayName("should join CSV from arrays using joinArrays")
		void joinFromArrays() {
			Csv csv = Csv.of();
			List<String[]> records = Arrays.asList(new String[] { "name", "age", "city" },
					new String[] { "John", "30", "Tokyo" }, new String[] { "Jane", "25", "Osaka" });

			String result = csv.joinArrays(records);

			assertThat(result).isEqualTo("name,age,city\r\nJohn,30,Tokyo\r\nJane,25,Osaka");
		}

		@Test
		@DisplayName("should handle list-based round-trip")
		void listBasedRoundTrip() {
			Csv csv = Csv.of();
			List<List<String>> originalRecords = List.of(List.of("name", "description", "notes"),
					List.of("John Doe", "A person, \"nice guy\"", "Lives in Tokyo"),
					List.of("Jane Smith", "Another person", "From \"Osaka\""));

			String csvText = csv.join(originalRecords);
			List<List<String>> parsedRecords = csv.split(csvText);

			assertThat(parsedRecords).hasSize(originalRecords.size());
			for (int i = 0; i < originalRecords.size(); i++) {
				assertThat(parsedRecords.get(i)).containsExactlyElementsOf(originalRecords.get(i));
			}
		}

	}

	@Nested
	@DisplayName("Single line processing tests")
	class SingleLineTests {

		@Test
		@DisplayName("should split single line CSV")
		void splitSingleLine() {
			Csv csv = Csv.of();
			String line = "name,age,city";

			String[] result = csv.splitLineToArray(line);

			assertThat(result).containsExactly("name", "age", "city");
		}

		@Test
		@DisplayName("should split single line with quoted fields")
		void splitSingleLineWithQuotes() {
			Csv csv = Csv.of();
			String line = "\"John Doe\",30,\"Tokyo, Japan\"";

			String[] result = csv.splitLineToArray(line);

			assertThat(result).containsExactly("John Doe", "30", "Tokyo, Japan");
		}

		@Test
		@DisplayName("should split single line with escaped quotes")
		void splitSingleLineWithEscapedQuotes() {
			Csv csv = Csv.of();
			String line = "\"John\",\"He said \"\"Hello\"\" to me\"";

			String[] result = csv.splitLineToArray(line);

			assertThat(result).containsExactly("John", "He said \"Hello\" to me");
		}

		@Test
		@DisplayName("should split empty line")
		void splitEmptyLine() {
			Csv csv = Csv.of();

			String[] result = csv.splitLineToArray("");

			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("should split null line")
		void splitNullLine() {
			Csv csv = Csv.of();

			String[] result = csv.splitLineToArray(null);

			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("should join single line")
		void joinSingleLine() {
			Csv csv = Csv.of();
			String[] fields = { "name", "age", "city" };

			String result = csv.joinLine(fields);

			assertThat(result).isEqualTo("name,age,city");
		}

		@Test
		@DisplayName("should join single line with fields requiring quotes")
		void joinSingleLineWithQuotes() {
			Csv csv = Csv.of();
			String[] fields = { "John Doe", "30", "Tokyo, Japan" };

			String result = csv.joinLine(fields);

			assertThat(result).isEqualTo("John Doe,30,\"Tokyo, Japan\"");
		}

		@Test
		@DisplayName("should join single line with fields containing quotes")
		void joinSingleLineWithEscapedQuotes() {
			Csv csv = Csv.of();
			String[] fields = { "John", "He said \"Hello\" to me" };

			String result = csv.joinLine(fields);

			assertThat(result).isEqualTo("John,\"He said \"\"Hello\"\" to me\"");
		}

		@Test
		@DisplayName("should join empty array")
		void joinEmptyArray() {
			Csv csv = Csv.of();
			String[] fields = {};

			String result = csv.joinLine(fields);

			assertThat(result).isEqualTo("");
		}

		@Test
		@DisplayName("should join null array")
		void joinNullArray() {
			Csv csv = Csv.of();

			String result = csv.joinLine((String[]) null);

			assertThat(result).isEqualTo("");
		}

		@Test
		@DisplayName("should handle round-trip for single line")
		void singleLineRoundTrip() {
			Csv csv = Csv.of();
			String[] originalFields = { "John Doe", "A person, \"nice guy\"", "Lives in Tokyo" };

			String csvLine = csv.joinLine(originalFields);
			String[] parsedFields = csv.splitLineToArray(csvLine);

			assertThat(parsedFields).containsExactly(originalFields);
		}

		@Test
		@DisplayName("should work with custom delimiter")
		void singleLineWithCustomDelimiter() {
			Csv csv = Csv.builder().delimiter(";").build();
			String[] fields = { "name", "age", "city" };

			String result = csv.joinLine(fields);
			String[] parsed = csv.splitLineToArray(result);

			assertThat(result).isEqualTo("name;age;city");
			assertThat(parsed).containsExactly("name", "age", "city");
		}

		@Test
		@DisplayName("should join using varargs")
		void joinLineWithVarargs() {
			Csv csv = Csv.of();

			String result = csv.joinLine("name", "age", "city");

			assertThat(result).isEqualTo("name,age,city");
		}

		@Test
		@DisplayName("should join varargs with quoted fields")
		void joinLineVarargsWithQuotes() {
			Csv csv = Csv.of();

			String result = csv.joinLine("John Doe", "30", "Tokyo, Japan");

			assertThat(result).isEqualTo("John Doe,30,\"Tokyo, Japan\"");
		}

		@Test
		@DisplayName("should join varargs with escaped quotes")
		void joinLineVarargsWithEscapedQuotes() {
			Csv csv = Csv.of();

			String result = csv.joinLine("John", "He said \"Hello\" to me");

			assertThat(result).isEqualTo("John,\"He said \"\"Hello\"\" to me\"");
		}

		@Test
		@DisplayName("should handle empty varargs")
		void joinLineEmptyVarargs() {
			Csv csv = Csv.of();

			String result = csv.joinLine();

			assertThat(result).isEqualTo("");
		}

		@Test
		@DisplayName("should handle single argument in varargs")
		void joinLineSingleVararg() {
			Csv csv = Csv.of();

			String result = csv.joinLine("single");

			assertThat(result).isEqualTo("single");
		}

		@Test
		@DisplayName("should handle null values in varargs")
		void joinLineVarargsWithNulls() {
			Csv csv = Csv.of();

			String result = csv.joinLine("name", null, "city");

			assertThat(result).isEqualTo("name,,city");
		}

		@Test
		@DisplayName("should handle varargs round-trip")
		void varargsRoundTrip() {
			Csv csv = Csv.of();

			String csvLine = csv.joinLine("John Doe", "A person, \"nice guy\"", "Lives in Tokyo");
			String[] parsedFields = csv.splitLineToArray(csvLine);

			assertThat(parsedFields).containsExactly("John Doe", "A person, \"nice guy\"", "Lives in Tokyo");
		}

	}

	@Nested
	@DisplayName("Singleton tests")
	class SingletonTests {

		@Test
		@DisplayName("should provide default singleton instance")
		void defaultSingleton() {
			assertThat(Csv.DEFAULT).isNotNull();
		}

		@Test
		@DisplayName("should return same instance on multiple accesses")
		void singletonConsistency() {
			Csv instance1 = Csv.DEFAULT;
			Csv instance2 = Csv.DEFAULT;

			assertThat(instance1).isSameAs(instance2);
		}

		@Test
		@DisplayName("should work with default configuration")
		void defaultConfiguration() {
			String input = "name,age,city\r\nJohn,30,Tokyo";

			List<List<String>> result = Csv.DEFAULT.split(input);

			assertThat(result).hasSize(2);
			assertThat(result.get(0)).containsExactly("name", "age", "city");
			assertThat(result.get(1)).containsExactly("John", "30", "Tokyo");
		}

		@Test
		@DisplayName("should join with default configuration")
		void joinWithDefault() {
			List<List<String>> records = List.of(List.of("name", "age"), List.of("John", "30"));

			String result = Csv.DEFAULT.join(records);

			assertThat(result).isEqualTo("name,age\r\nJohn,30");
		}

		@Test
		@DisplayName("should be different from factory method instance")
		void differentFromFactory() {
			Csv factoryInstance = Csv.of();

			assertThat(Csv.DEFAULT).isNotSameAs(factoryInstance);
		}

		@Test
		@DisplayName("should be functionally equivalent to factory method instance")
		void functionallyEquivalent() {
			Csv factoryInstance = Csv.of();
			String input = "name,age\r\nJohn,30";

			List<List<String>> defaultResult = Csv.DEFAULT.split(input);
			List<List<String>> factoryResult = factoryInstance.split(input);

			assertThat(defaultResult).isEqualTo(factoryResult);
		}

	}

	@Nested
	@DisplayName("Builder pattern tests")
	class BuilderTests {

		@Test
		@DisplayName("should allow custom delimiter")
		void customDelimiter() {
			Csv csv = Csv.builder().delimiter(";").build();
			String input = "name;age;city\r\nJohn;30;Tokyo";

			List<String[]> result = csv.splitToArrays(input);

			assertThat(result).hasSize(2);
			assertThat(result.get(0)).containsExactly("name", "age", "city");
			assertThat(result.get(1)).containsExactly("John", "30", "Tokyo");
		}

		@Test
		@DisplayName("should allow custom line ending")
		void customLineEnding() {
			Csv csv = Csv.builder().lineEnding("\n").build();
			List<String[]> records = Arrays.asList(new String[] { "name", "age" }, new String[] { "John", "30" });

			String result = csv.joinArrays(records);

			assertThat(result).isEqualTo("name,age\nJohn,30");
		}

		@Test
		@DisplayName("should quote fields containing custom delimiter")
		void customDelimiterQuoting() {
			Csv csv = Csv.builder().delimiter(";").build();
			List<String[]> records = Arrays.asList(new String[] { "name", "description" },
					new String[] { "John", "A person; who lives in Tokyo" },
					new String[] { "Jane", "Another person; from Osaka" });

			String result = csv.joinArrays(records);

			assertThat(result).isEqualTo(
					"name;description\r\nJohn;\"A person; who lives in Tokyo\"\r\nJane;\"Another person; from Osaka\"");
		}

		@Test
		@DisplayName("should parse fields with custom delimiter properly")
		void customDelimiterParsing() {
			Csv csv = Csv.builder().delimiter(";").build();
			String input = "name;description\r\nJohn;\"A person; who lives in Tokyo\"\r\nJane;\"Another person; from Osaka\"";

			List<String[]> result = csv.splitToArrays(input);

			assertThat(result).hasSize(3);
			assertThat(result.get(0)).containsExactly("name", "description");
			assertThat(result.get(1)).containsExactly("John", "A person; who lives in Tokyo");
			assertThat(result.get(2)).containsExactly("Jane", "Another person; from Osaka");
		}

		@Test
		@DisplayName("should handle round-trip with custom delimiter")
		void customDelimiterRoundTrip() {
			Csv csv = Csv.builder().delimiter("|").build();
			List<String[]> originalRecords = Arrays.asList(new String[] { "name", "notes", "city" },
					new String[] { "John", "A person| with pipes", "Tokyo" },
					new String[] { "Jane", "Another| person", "Osaka" });

			String csvText = csv.joinArrays(originalRecords);
			List<String[]> parsedRecords = csv.splitToArrays(csvText);

			assertThat(parsedRecords).hasSize(originalRecords.size());
			for (int i = 0; i < originalRecords.size(); i++) {
				assertThat(parsedRecords.get(i)).containsExactly(originalRecords.get(i));
			}
		}

	}

}