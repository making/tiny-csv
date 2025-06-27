package am.ik.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A tiny CSV library that provides RFC 4180 compliant CSV parsing and formatting.
 *
 * This class supports: - Splitting CSV text into list of string arrays (records) -
 * Joining string arrays into CSV text - Proper handling of quoted fields, escaped quotes,
 * and line breaks - Builder pattern for configuration
 */
public class Csv {

	private static final String CRLF = "\r\n";

	private static final String COMMA = ",";

	private static final String QUOTE = "\"";

	private static final String ESCAPED_QUOTE = "\"\"";

	private static final char QUOTE_CHAR = '"';

	private static final char CR_CHAR = '\r';

	private static final char LF_CHAR = '\n';

	private final Pattern needsQuoting;

	/**
	 * Default singleton instance with comma delimiter and CRLF line endings.
	 */
	public static final Csv DEFAULT = new Builder().build();

	private final String delimiter;

	private final String lineEnding;

	private Csv(Builder builder) {
		this.delimiter = builder.delimiter;
		this.lineEnding = builder.lineEnding;
		this.needsQuoting = Pattern.compile("[" + Pattern.quote(delimiter) + QUOTE_CHAR + CR_CHAR + LF_CHAR + "]");
	}

	/**
	 * Creates a default CSV instance with comma delimiter and CRLF line endings.
	 */
	public static Csv of() {
		return new Builder().build();
	}

	/**
	 * Creates a builder for configuring CSV options.
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Splits a single line of CSV text into a list of strings.
	 * @param csvLine the CSV line to parse
	 * @return list of strings representing the fields in the CSV line
	 */
	public List<String> splitLine(String csvLine) {
		if (csvLine == null || csvLine.isEmpty()) {
			return new ArrayList<>();
		}

		List<String> fields = new ArrayList<>();
		StringBuilder currentField = new StringBuilder();

		boolean inQuotes = false;

		for (int i = 0; i < csvLine.length(); i++) {
			char c = csvLine.charAt(i);

			if (c == QUOTE_CHAR) {
				if (inQuotes) {
					// Check if this is an escaped quote
					if (i + 1 < csvLine.length() && csvLine.charAt(i + 1) == QUOTE_CHAR) {
						currentField.append(QUOTE_CHAR);
						i++; // Skip the next quote
					}
					else {
						inQuotes = false;
					}
				}
				else {
					inQuotes = true;
				}
			}
			else if (!inQuotes && c == delimiter.charAt(0)) {
				fields.add(currentField.toString());
				currentField.setLength(0);
			}
			else if (!inQuotes && (c == CR_CHAR || c == LF_CHAR)) {
				// Line break found - end the line
				break;
			}
			else {
				currentField.append(c);
			}
		}

		// Add the last field
		fields.add(currentField.toString());

		return fields;
	}

	/**
	 * Splits a single line of CSV text into a string array.
	 * @param csvLine the CSV line to parse
	 * @return string array representing the fields in the CSV line
	 */
	public String[] splitLineToArray(String csvLine) {
		List<String> fields = splitLine(csvLine);
		return fields.toArray(new String[0]);
	}

	/**
	 * Splits CSV text into a list of lists of strings. Each inner list represents a
	 * record (row) in the CSV.
	 * @param csvText the CSV text to parse
	 * @return list of lists of strings representing CSV records
	 */
	public List<List<String>> split(String csvText) {
		if (csvText == null || csvText.isEmpty()) {
			return new ArrayList<>();
		}

		List<List<String>> records = new ArrayList<>();
		StringBuilder currentLine = new StringBuilder();
		boolean inQuotes = false;

		for (int i = 0; i < csvText.length(); i++) {
			char c = csvText.charAt(i);

			if (c == QUOTE_CHAR) {
				// Toggle quote state or handle escaped quotes
				if (inQuotes && i + 1 < csvText.length() && csvText.charAt(i + 1) == QUOTE_CHAR) {
					currentLine.append(ESCAPED_QUOTE); // Keep escaped quotes
					i++; // Skip the next quote
				}
				else {
					inQuotes = !inQuotes;
					currentLine.append(c);
				}
			}
			else if (!inQuotes && c == CR_CHAR) {
				// Handle CRLF
				if (i + 1 < csvText.length() && csvText.charAt(i + 1) == LF_CHAR) {
					i++; // Skip the \n
				}
				// Process the line
				if (currentLine.length() > 0 || records.isEmpty()) {
					records.add(splitLine(currentLine.toString()));
				}
				currentLine.setLength(0);
			}
			else if (!inQuotes && c == LF_CHAR) {
				// Handle standalone LF
				if (currentLine.length() > 0 || records.isEmpty()) {
					records.add(splitLine(currentLine.toString()));
				}
				currentLine.setLength(0);
			}
			else {
				currentLine.append(c);
			}
		}

		// Process the last line if any
		if (currentLine.length() > 0) {
			records.add(splitLine(currentLine.toString()));
		}

		return records;
	}

	/**
	 * Splits CSV text into a list of string arrays. Each array represents a record (row)
	 * in the CSV.
	 * @param csvText the CSV text to parse
	 * @return list of string arrays representing CSV records
	 */
	public List<String[]> splitToArrays(String csvText) {
		List<List<String>> records = split(csvText);
		return records.stream().map(record -> record.toArray(new String[0])).collect(Collectors.toList());
	}

	/**
	 * Joins a list of strings into a single CSV line.
	 * @param fields list of strings to join
	 * @return CSV formatted line
	 */
	public String joinLine(List<String> fields) {
		if (fields == null || fields.isEmpty()) {
			return "";
		}

		StringBuilder csv = new StringBuilder();
		for (int i = 0; i < fields.size(); i++) {
			if (i > 0) {
				csv.append(delimiter);
			}
			csv.append(formatField(fields.get(i)));
		}

		return csv.toString();
	}

	/**
	 * Joins a string array or variable arguments into a single CSV line.
	 * @param fields array or variable arguments of strings to join
	 * @return CSV formatted line
	 */
	public String joinLine(String... fields) {
		if (fields == null || fields.length == 0) {
			return "";
		}

		StringBuilder csv = new StringBuilder();
		for (int i = 0; i < fields.length; i++) {
			if (i > 0) {
				csv.append(delimiter);
			}
			csv.append(formatField(fields[i]));
		}

		return csv.toString();
	}

	/**
	 * Joins a list of lists of strings into CSV text. Each inner list represents a record
	 * (row) in the CSV.
	 * @param records list of lists of strings to join
	 * @return CSV formatted text
	 */
	public String join(List<List<String>> records) {
		if (records == null || records.isEmpty()) {
			return "";
		}

		StringBuilder csv = new StringBuilder();

		for (int i = 0; i < records.size(); i++) {
			List<String> record = records.get(i);
			csv.append(joinLine(record));
			if (i < records.size() - 1) {
				csv.append(lineEnding);
			}
		}

		return csv.toString();
	}

	/**
	 * Joins a list of string arrays into CSV text. Each array represents a record (row)
	 * in the CSV.
	 * @param records list of string arrays to join
	 * @return CSV formatted text
	 */
	public String joinArrays(List<String[]> records) {
		if (records == null || records.isEmpty()) {
			return "";
		}

		StringBuilder csv = new StringBuilder();

		for (int i = 0; i < records.size(); i++) {
			String[] record = records.get(i);
			csv.append(joinLine(record));
			if (i < records.size() - 1) {
				csv.append(lineEnding);
			}
		}

		return csv.toString();
	}

	/**
	 * Formats a single field for CSV output. Adds quotes if the field contains special
	 * characters and escapes internal quotes.
	 */
	private String formatField(String field) {
		if (field == null) {
			return "";
		}

		boolean needsQuoting = this.needsQuoting.matcher(field).find();
		if (needsQuoting) {
			return QUOTE + field.replace(QUOTE, ESCAPED_QUOTE) + QUOTE;
		}

		return field;
	}

	/**
	 * Builder class for configuring CSV options.
	 */
	public static class Builder {

		private String delimiter = COMMA;

		private String lineEnding = CRLF;

		/**
		 * Sets the field delimiter (default is comma).
		 */
		public Builder delimiter(String delimiter) {
			this.delimiter = delimiter;
			return this;
		}

		/**
		 * Sets the line ending (default is CRLF).
		 */
		public Builder lineEnding(String lineEnding) {
			this.lineEnding = lineEnding;
			return this;
		}

		/**
		 * Builds the CSV instance.
		 */
		public Csv build() {
			return new Csv(this);
		}

	}

}