# Contributing to Vibium Java Client

Thank you for your interest in contributing to Vibium Java Client! This document provides guidelines and instructions for contributing.

## Code of Conduct

Please be respectful and constructive in all interactions. We welcome contributors of all experience levels.

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.6+
- Git

### Setting Up the Development Environment

1. Fork the repository on GitHub

2. Clone your fork:
   ```bash
   git clone https://github.com/YOUR_USERNAME/vibium.git
   cd vibium
   ```

3. Build the project:
   ```bash
   mvn clean install
   ```

4. Run tests:
   ```bash
   mvn test
   ```

## How to Contribute

### Reporting Bugs

Before creating a bug report, please check existing issues to avoid duplicates.

When filing a bug report, include:

- **Clear title** describing the issue
- **Steps to reproduce** the problem
- **Expected behavior** vs **actual behavior**
- **Environment details**: OS, Java version, Vibium version
- **Code snippets** or minimal reproducible example
- **Stack traces** if applicable

[Open a bug report](https://github.com/ashwithpoojary98/vibium/issues/new?labels=bug)

### Suggesting Features

We welcome feature suggestions! Please include:

- **Clear description** of the feature
- **Use case** explaining why it's needed
- **Possible implementation** approach (optional)

[Open a feature request](https://github.com/ashwithpoojary98/vibium/issues/new?labels=enhancement)

### Submitting Pull Requests

1. **Create a branch** from `master`:
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes** following our coding standards

3. **Add tests** for new functionality

4. **Run all tests** to ensure nothing is broken:
   ```bash
   mvn test
   ```

5. **Commit your changes** with a clear message:
   ```bash
   git commit -m "Add feature: description of what you added"
   ```

6. **Push to your fork**:
   ```bash
   git push origin feature/your-feature-name
   ```

7. **Open a Pull Request** against the `master` branch

### Pull Request Guidelines

- Keep PRs focused on a single change
- Update documentation if needed
- Add tests for new features
- Ensure all tests pass
- Follow existing code style

## Coding Standards

### Code Style

- Use 4 spaces for indentation (no tabs)
- Follow standard Java naming conventions
- Keep methods focused and small
- Add Javadoc for public APIs

### Project Structure

```
src/
├── main/java/io/github/ashwithpoojary98/vibium/
│   ├── Browser.java          # Sync browser factory
│   ├── BrowserAsync.java     # Async browser factory
│   ├── Vibe.java             # Sync browser automation
│   ├── VibeAsync.java        # Async browser automation
│   ├── Element.java          # Sync element wrapper
│   ├── ElementAsync.java     # Async element wrapper
│   ├── exception/            # Custom exceptions
│   ├── internal/             # Internal classes (not public API)
│   ├── model/                # Data models
│   └── options/              # Configuration options
└── test/java/                # Test classes
```

### Testing

- Write unit tests for new functionality
- Use JUnit 5 and Mockito
- Place tests in the corresponding package under `src/test/java`
- Name test classes with `Test` suffix (e.g., `ElementTest.java`)

Example test:
```java
@Test
void find_returnsElement_whenSelectorMatches() {
    // Arrange
    when(client.sendCommand(eq("vibium:find"), any()))
        .thenReturn(CompletableFuture.completedFuture(response));

    // Act
    Element element = vibe.find("h1");

    // Assert
    assertNotNull(element);
    assertEquals("h1", element.getTagName());
}
```

## Development Workflow

### Building

```bash
# Clean build
mvn clean install

# Skip tests
mvn clean install -DskipTests

# Build with specific profile
mvn clean install -P release
```

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ElementTest

# Run specific test method
mvn test -Dtest=ElementTest#click_sendsClickCommand
```

### Code Quality

```bash
# Check code style (if configured)
mvn checkstyle:check

# Generate Javadoc
mvn javadoc:javadoc
```

## Release Process

Releases are managed by maintainers. The process:

1. Update version in `pom.xml`
2. Update CHANGELOG (if exists)
3. Create a git tag
4. Deploy to Maven Central

## Getting Help

- **Questions**: Open a [GitHub Discussion](https://github.com/ashwithpoojary98/vibium/discussions)
- **Bugs**: Open an [Issue](https://github.com/ashwithpoojary98/vibium/issues)
- **Main Vibium Project**: [VibiumDev/vibium](https://github.com/VibiumDev/vibium)

## License

By contributing, you agree that your contributions will be licensed under the Apache License 2.0.
