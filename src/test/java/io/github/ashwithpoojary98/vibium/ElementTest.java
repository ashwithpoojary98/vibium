package io.github.ashwithpoojary98.vibium;

import com.google.gson.JsonObject;
import io.github.ashwithpoojary98.vibium.internal.BiDiClient;
import io.github.ashwithpoojary98.vibium.model.Box;
import io.github.ashwithpoojary98.vibium.model.ElementInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link Element} and {@link ElementAsync}.
 */
@ExtendWith(MockitoExtension.class)
class ElementTest {

    @Mock
    private BiDiClient client;

    private ElementAsync asyncElement;
    private Element element;
    private Box box;
    private ElementInfo info;

    @BeforeEach
    void setUp() {
        box = new Box(100, 200, 50, 30);
        info = ElementInfo.builder()
                .tagName("button")
                .textContent("Click me")
                .box(box)
                .build();
        asyncElement = new ElementAsync(client, "context-123", "#submit-btn", info);
        element = new Element(asyncElement);
    }

    @Test
    void getTagName_returnsTagFromInfo() {
        assertEquals("button", element.getTagName());
    }

    @Test
    void getText_returnsTextContentFromInfo() {
        assertEquals("Click me", element.getText());
    }

    @Test
    void getBox_returnsBoxFromInfo() {
        assertSame(box, element.getBox());
    }

    @Test
    void async_returnsAsyncElement() {
        assertSame(asyncElement, element.async());
    }

    @Test
    void getSelector_returnsSelector() {
        assertEquals("#submit-btn", asyncElement.getSelector());
    }

    @Test
    void getContextId_returnsContextId() {
        assertEquals("context-123", asyncElement.getContextId());
    }

    @Test
    void getInfo_returnsElementInfo() {
        assertSame(info, element.getInfo());
    }

    @Test
    void click_sendsClickCommand() {
        when(client.sendCommand(eq("vibium:click"), any(JsonObject.class)))
                .thenReturn(CompletableFuture.completedFuture(new JsonObject()));

        element.click();

        verify(client).sendCommand(eq("vibium:click"), any(JsonObject.class));
    }

    @Test
    void type_sendsTypeCommand() {
        when(client.sendCommand(eq("vibium:type"), any(JsonObject.class)))
                .thenReturn(CompletableFuture.completedFuture(new JsonObject()));

        element.type("Hello World");

        verify(client).sendCommand(eq("vibium:type"), any(JsonObject.class));
    }

    @Test
    void type_includesTextInParams() {
        when(client.sendCommand(eq("vibium:type"), any(JsonObject.class)))
                .thenAnswer(invocation -> {
                    JsonObject params = invocation.getArgument(1);
                    assertEquals("Test input", params.get("text").getAsString());
                    assertEquals("#submit-btn", params.get("selector").getAsString());
                    assertEquals("context-123", params.get("context").getAsString());
                    return CompletableFuture.completedFuture(new JsonObject());
                });

        element.type("Test input");
    }

    @Test
    void clear_sendsClearCommand() {
        when(client.sendCommand(eq("vibium:clear"), any(JsonObject.class)))
                .thenReturn(CompletableFuture.completedFuture(new JsonObject()));

        element.clear();

        verify(client).sendCommand(eq("vibium:clear"), any(JsonObject.class));
    }

    @Test
    void getAttribute_sendsGetAttributeCommand() {
        JsonObject response = new JsonObject();
        response.addProperty("value", "btn-primary");

        when(client.sendCommand(eq("vibium:getAttribute"), any(JsonObject.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        String value = element.getAttribute("class");

        assertEquals("btn-primary", value);
        verify(client).sendCommand(eq("vibium:getAttribute"), any(JsonObject.class));
    }

    @Test
    void getAttribute_returnsNullForMissingAttribute() {
        JsonObject response = new JsonObject();
        response.add("value", null);

        when(client.sendCommand(eq("vibium:getAttribute"), any(JsonObject.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        String value = element.getAttribute("nonexistent");

        assertNull(value);
    }

    @Test
    void isVisible_returnsTrueForVisibleBox() {
        boolean visible = element.isVisible();

        assertTrue(visible);
    }

    @Test
    void isVisible_returnsFalseForZeroWidthBox() {
        Box zeroWidthBox = new Box(0, 0, 0, 50);
        ElementInfo zeroInfo = ElementInfo.builder()
                .tagName("div")
                .textContent("")
                .box(zeroWidthBox)
                .build();
        ElementAsync zeroAsyncElement = new ElementAsync(client, "ctx", "div", zeroInfo);
        Element zeroElement = new Element(zeroAsyncElement);

        boolean visible = zeroElement.isVisible();

        assertFalse(visible);
    }

    @Test
    void isVisible_returnsFalseForZeroHeightBox() {
        Box zeroHeightBox = new Box(0, 0, 50, 0);
        ElementInfo zeroInfo = ElementInfo.builder()
                .tagName("div")
                .textContent("")
                .box(zeroHeightBox)
                .build();
        ElementAsync zeroAsyncElement = new ElementAsync(client, "ctx", "div", zeroInfo);
        Element zeroElement = new Element(zeroAsyncElement);

        boolean visible = zeroElement.isVisible();

        assertFalse(visible);
    }

    @Test
    void toString_containsTagNameAndSelector() {
        String str = element.toString();

        assertTrue(str.contains("button"));
        assertTrue(str.contains("#submit-btn"));
        assertTrue(str.contains("Element"));
    }

    @Test
    void asyncElement_getClient_returnsClient() {
        assertSame(client, asyncElement.getClient());
    }
}
