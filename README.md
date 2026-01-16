# Vibium Java Client

A modern Java browser automation library using WebDriver BiDi protocol with Playwright-like auto-wait capabilities.

This is the official Java client for [Vibium](https://github.com/VibiumDev/vibium) - browser automation without the drama.

## Features

- **Zero Configuration** - Automatic browser and driver downloads
- **Sync & Async APIs** - Choose between blocking and non-blocking operations
- **Auto-Wait** - Built-in polling before element interaction
- **WebDriver BiDi** - Standards-based protocol, no proprietary hacks
- **Cross-Platform** - Windows, macOS (Intel & Apple Silicon), Linux
- **Automatic Cleanup** - Processes are cleaned up on JVM shutdown

## Installation

### Maven

```xml
<dependency>
    <groupId>io.github.ashwithpoojary98</groupId>
    <artifactId>vibium</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.github.ashwithpoojary98:vibium:1.0.0'
```

## Quick Start

### Synchronous API

```java
import io.github.ashwithpoojary98.vibium.Browser;
import io.github.ashwithpoojary98.vibium.Vibe;
import io.github.ashwithpoojary98.vibium.Element;

// Launch browser
Vibe vibe = new Browser().launch();

// Navigate to a page
vibe.go("https://example.com");

// Find and interact with elements
Element heading = vibe.find("h1");
System.out.println(heading.getText());

Element link = vibe.find("a");
link.click();

// Take screenshot
byte[] screenshot = vibe.screenshot();
Files.write(Path.of("screenshot.png"), screenshot);

// Close browser
vibe.quit();
```

### Asynchronous API

```java
import io.github.ashwithpoojary98.vibium.BrowserAsync;
import io.github.ashwithpoojary98.vibium.VibeAsync;
import io.github.ashwithpoojary98.vibium.ElementAsync;

// Launch browser asynchronously
BrowserAsync browser = new BrowserAsync();
VibeAsync vibe = browser.launch().join();

// Navigate
vibe.go("https://example.com").join();

// Find element with CompletableFuture
vibe.find("h1")
    .thenAccept(el -> System.out.println(el.getText()))
    .join();

// Close
vibe.quit();
```

### Launch Options

```java
import io.github.ashwithpoojary98.vibium.options.LaunchOptions;

LaunchOptions options = LaunchOptions.builder()
    .headless(true)           // Run in headless mode
    .port(9515)               // Custom port
    .executablePath("/path/to/clicker")  // Custom clicker binary
    .build();

Vibe vibe = new Browser().launch(options);
```

### Connect to Existing Browser

```java
// Connect to a browser already running on a specific WebSocket URL
Vibe vibe = new Browser().connect("ws://localhost:9515");
```

## API Reference

### Browser / BrowserAsync

| Method | Description |
|--------|-------------|
| `launch()` | Launch browser with default options |
| `launch(LaunchOptions)` | Launch browser with custom options |
| `connect(String wsUrl)` | Connect to existing browser |

### Vibe / VibeAsync

| Method | Description |
|--------|-------------|
| `go(String url)` | Navigate to URL |
| `find(String selector)` | Find element by CSS selector |
| `find(String selector, Duration timeout)` | Find element with custom timeout |
| `screenshot()` | Capture viewport screenshot as PNG bytes |
| `evaluate(String script, Class<T>)` | Execute JavaScript and return result |
| `quit()` | Close browser and cleanup |

### Element / ElementAsync

| Method | Description |
|--------|-------------|
| `click()` | Click the element |
| `type(String text)` | Type text into the element |
| `clear()` | Clear input field |
| `getText()` | Get element text content |
| `getTagName()` | Get element tag name |
| `getBox()` | Get element bounding box |
| `isVisible()` | Check if element is visible |

## CLI

```bash
java -jar vibium-1.0.0-cli.jar install   # Download Chrome for Testing
java -jar vibium-1.0.0-cli.jar version   # Show version
```

## Requirements

- Java 11 or higher
- No external browser installation required (auto-downloaded)

## Platform Support

| Platform | Architecture | Status |
|----------|--------------|--------|
| Windows  | x64          | Supported |
| macOS    | x64 (Intel)  | Supported |
| macOS    | arm64 (M1/M2)| Supported |
| Linux    | x64          | Supported |
| Linux    | arm64        | Supported |

## How It Works

Vibium Java client communicates with the [Clicker](https://github.com/VibiumDev/vibium) binary via WebSocket using the WebDriver BiDi protocol. The clicker binary:

1. Automatically downloads Chrome for Testing if not present
2. Manages browser lifecycle (launch, connect, close)
3. Provides BiDi protocol proxy on port 9515

The clicker binary is bundled with the JAR and automatically extracted to the platform cache directory on first run:
- **Windows**: `%LOCALAPPDATA%\vibium`
- **macOS**: `~/Library/Caches/vibium`
- **Linux**: `~/.cache/vibium`

## Contributing

We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

### Quick Links

- [Report a Bug](https://github.com/ashwithpoojary98/vibium/issues/new?labels=bug)
- [Request a Feature](https://github.com/ashwithpoojary98/vibium/issues/new?labels=enhancement)
- [Open Issues](https://github.com/ashwithpoojary98/vibium/issues)
- [Pull Requests](https://github.com/ashwithpoojary98/vibium/pulls)

## Related Projects

- [Vibium](https://github.com/VibiumDev/vibium) - Main project (Go binary + JS/Python clients)
- [vibium (npm)](https://www.npmjs.com/package/vibium) - JavaScript/TypeScript client
- [vibium (PyPI)](https://pypi.org/project/vibium/) - Python client

## License

Apache License 2.0 - see [LICENSE](LICENSE) for details.
