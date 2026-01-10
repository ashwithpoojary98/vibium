# Vibium Java Client

Browser automation for AI agents and humans.

## Installation

### Maven

```xml
<dependency>
    <groupId>io.github.ashwith</groupId>
    <artifactId>Vibium</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.github.ashwith:Vibium:1.0-SNAPSHOT'
```

### Clicker Binary

The clicker binary is required for browser automation. Install it manually:

**Option 1: Environment Variable**
```bash
# Download from https://github.com/AshwithJoymo/clicker/releases
export VIBIUM_CLICKER_PATH=/path/to/clicker
```

**Option 2: Add to PATH**
```bash
# Place clicker binary in a directory on your PATH
mv clicker /usr/local/bin/
```

**Option 3: Cache Directory**
Place the binary in the platform-specific cache directory:
- **Windows:** `%LOCALAPPDATA%\vibium\clicker.exe`
- **macOS:** `~/Library/Caches/vibium/clicker`
- **Linux:** `~/.cache/vibium/clicker`

### Chrome for Testing

Chrome downloads automatically on first use. To install ahead of time:

```bash
java -jar vibium.jar install
```

## Quick Start

```java
import io.github.ashwithpoojary98.*;

public class Example {
    public static void main(String[] args) throws Exception {
        VibeSync vibe = BrowserSync.launch();
        vibe.go("https://example.com");

        // Take a screenshot
        byte[] png = vibe.screenshot();
        java.nio.file.Files.write(
            java.nio.file.Path.of("screenshot.png"), png
        );

        // Find and click a link
        ElementSync link = vibe.find("a");
        System.out.println(link.text());
        link.click();

        vibe.quit();
    }
}
```

## Async API

```java
import io.github.ashwithpoojary98.*;

public class AsyncExample {
    public static void main(String[] args) {
        Browser.launch()
            .thenCompose(vibe ->
                vibe.go("https://example.com")
                    .thenCompose(v -> vibe.find("a"))
                    .thenCompose(link -> link.click())
                    .thenRun(vibe::close)
            )
            .join();
    }
}
```

Or with try-with-resources:

```java
try (Vibe vibe = Browser.launch().join()) {
    vibe.go("https://example.com").join();

    Element link = vibe.find("a").join();
    link.click().join();
}
```

## CLI

```bash
java -jar vibium.jar install   # Download Chrome for Testing
java -jar vibium.jar version   # Show version
```

## API Reference

### BrowserSync / Browser

| Method | Description |
|--------|-------------|
| `launch()` | Launch browser with default settings |
| `launch(headless)` | Launch browser in headless mode |
| `launch(headless, port)` | Launch with specific WebSocket port |
| `launch(headless, port, executablePath)` | Launch with custom clicker binary |

### VibeSync / Vibe

| Method | Description |
|--------|-------------|
| `go(url)` | Navigate to a URL |
| `screenshot()` | Capture viewport as PNG bytes |
| `find(selector)` | Find element by CSS selector |
| `find(selector, timeout)` | Find element with custom timeout |
| `isConnected()` | Check if browser is connected |
| `quit()` / `close()` | Close browser and cleanup |

### ElementSync / Element

| Method | Description |
|--------|-------------|
| `click()` | Click the element |
| `type(text)` | Type text into element |
| `clear()` | Clear element value |
| `fill(text)` | Clear and type text |
| `text()` | Get text content |
| `getAttribute(name)` | Get attribute value |
| `isVisible()` | Check if element is visible |
| `isEnabled()` | Check if element is enabled |
| `hover()` | Hover over element |
| `focus()` | Focus the element |
| `scrollIntoView()` | Scroll element into view |

## Requirements

- Java 21+

## License

Apache-2.0
