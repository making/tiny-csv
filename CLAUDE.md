# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this
repository.

**Build Commands:**

```bash
./mvnw clean spring-javaformat:apply compile                    # Compile application
./mvnw spring-javaformat:apply test                             # Run all tests
```

## Design Requirements
- **Package**: `am.ik.csv` - Main package
- Implement all functionalities in a single Java file - `Csv.java`
- Provide CSV splitting from text to string list/array and joining string list/array to CSV
- Compliant with RFC 4180
- Support both List-based and array-based APIs for flexibility
- Include singleton instance (`Csv.DEFAULT`) for convenience
- Support single-line processing methods (`splitLine`, `joinLine`)
- Support varargs in `joinLine` method for ease of use

## Development Requirements

### Prerequisites

- Java 17 runtime (changed from Java 21 for broader compatibility)

### Code Standards

- No external dependencies except for testing libraries
- Use builder pattern if the number of arguments is more than two
- Write javadoc and comments in English
- Spring Java Format enforced via Maven plugin
- All code must pass formatting validation before commit
- Use Java 17 compatible features (avoid Java 21+ specific APIs like `.toList()`)
- Hardcoded special characters should be defined as constants (QUOTE_CHAR, CR_CHAR, LF_CHAR)
- Dynamic pattern generation for custom delimiters to ensure proper quoting

### Testing Strategy

- JUnit 5 with AssertJ
- Comprehensive test coverage including:
  - Basic split/join operations
  - RFC 4180 edge cases (quotes, commas, line breaks)
  - Single-line processing
  - Varargs functionality
  - List-based API
  - Custom delimiter handling
  - Round-trip data integrity
  - Singleton functionality
- All tests must pass before completing tasks

## Implementation Notes

### Performance Considerations
- `splitLine` is the base method to avoid unnecessary ArrayList creation
- Minimal object allocation for single-line processing
- Efficient string building for CSV generation

### API Design
- Primary API returns `List<String>` for modern Java usage
- Alternative methods ending with "ToArray"/"Arrays" for array-based operations
- Varargs support in `joinLine` for convenience
- Builder pattern for configuration options

### RFC 4180 Compliance Details
- Fields containing delimiter, quotes, or line breaks are quoted
- Internal quotes are escaped by doubling them
- Both CRLF and LF line endings supported for parsing
- Configurable line endings for output

### After Task completion

- Ensure all code is formatted using `./mvnw spring-javaformat:apply`
- Run full test suite with `./mvnw test`
- Notify that the task is complete and ready for review by the following command:

```
osascript -e 'display notification "<Message Body>" with title "<Message Title>"’
```
