# Tiny CSV

Tiny CSV is a lightweight, single-file Java library that provides robust CSV parsing and formatting capabilities. It is designed to be simple, efficient, and fully compliant with RFC 4180 standards for Java 17+.

## Quick Start

### Basic Usage

```java
import am.ik.csv.Csv;

// Using the default instance
Csv csv = Csv.DEFAULT;

// Parse CSV text
String csvText = "name,age,city\r\nJohn,30,Tokyo\r\nJane,25,Osaka";
List<List<String>> records = csv.split(csvText);

// Format data to CSV
List<List<String>> data = List.of(
    List.of("name", "age", "city"),
    List.of("John", "30", "Tokyo")
);
String result = csv.join(data);
```

### Single Line Processing

```java
// Parse a single CSV line
String line = "John,30,\"Tokyo, Japan\"";
List<String> fields = csv.splitLine(line);

// Format fields to CSV line
String csvLine = csv.joinLine("John", "30", "Tokyo, Japan");
```

### Custom Configuration

```java
// Custom delimiter and line ending
Csv csv = Csv.builder()
    .delimiter(";")
    .lineEnding("\n")
    .build();

String data = "name;age\nJohn;30";
List<List<String>> records = csv.split(data);
```

## API Reference

### Factory Methods

- `Csv.of()` - Creates a default CSV instance
- `Csv.DEFAULT` - Singleton instance with default settings
- `Csv.builder()` - Returns a builder for custom configuration

### Parsing Methods

- `split(String csvText)` - Parse CSV text into List of Lists
- `splitToArrays(String csvText)` - Parse CSV text into List of arrays
- `splitLine(String csvLine)` - Parse single line into List
- `splitLineToArray(String csvLine)` - Parse single line into array

### Formatting Methods

- `join(List<List<String>> records)` - Format List of Lists to CSV
- `joinArrays(List<String[]> records)` - Format List of arrays to CSV
- `joinLine(List<String> fields)` - Format List to single CSV line
- `joinLine(String... fields)` - Format varargs to single CSV line

### Configuration Options

- `delimiter(String)` - Set field delimiter (default: comma)
- `lineEnding(String)` - Set line ending (default: CRLF)

## RFC 4180 Compliance

This library fully implements RFC 4180 specifications:

- Fields containing commas, quotes, or line breaks are enclosed in double quotes
- Double quotes within fields are escaped by doubling them
- Both CRLF and LF line endings are supported for parsing
- Configurable line endings for output formatting

## Examples

### Handling Special Characters

```java
// Data with commas, quotes, and line breaks
List<List<String>> data = List.of(
    List.of("name", "description"),
    List.of("John", "A person, who said \"Hello\""),
    List.of("Jane", "Lives in\r\nTokyo")
);

String csv = Csv.DEFAULT.join(data);
// Result: name,description\r\nJohn,"A person, who said ""Hello"""\r\nJane,"Lives in\r\nTokyo"
```

### Custom Delimiter

```java
Csv csv = Csv.builder().delimiter(";").build();

// Parse semicolon-separated values
String input = "name;age;city\r\nJohn;30;Tokyo";
List<List<String>> records = csv.split(input);

// Fields containing semicolons will be quoted
String output = csv.joinLine("John", "Age: 30; Location: Tokyo");
// Result: John;"Age: 30; Location: Tokyo"
```

### Round-trip Processing

```java
// Original data
List<List<String>> original = List.of(
    List.of("name", "notes"),
    List.of("John", "Person with \"special\" characters, and commas")
);

// Convert to CSV and back
Csv csv = Csv.DEFAULT;
String csvText = csv.join(original);
List<List<String>> parsed = csv.split(csvText);

// Data integrity is preserved
assert original.equals(parsed);
```

## Building

Compile the library:

```bash
./mvnw clean compile
```

Run tests:

```bash
./mvnw test
```

Apply code formatting:

```bash
./mvnw spring-javaformat:apply
```

## Requirements

- Java 17 or higher
- Maven 3.6+ (for building)

## License

Licensed under the Apache License, Version 2.0.

## Contributing

This project follows Spring Java Format conventions. All contributions must pass the existing test suite and maintain RFC 4180 compliance.