package it.unibz.inf.ontouml.vp.controllers;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sun.net.httpserver.HttpServer;
import it.unibz.inf.ontouml.vp.model.LlmConfigurations;
import it.unibz.inf.ontouml.vp.model.LlmProvider;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** {@link LlmModelsController} against a real HTTP server, one per response shape it must read. */
class LlmModelsControllerTest {

  private HttpServer server;
  private String status;
  private String body;
  private String contentType = "application/json";

  private LlmConfigurations config;

  @BeforeEach
  void startServer() throws IOException {
    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext(
        "/models",
        exchange -> {
          final byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
          exchange.getResponseHeaders().add("Content-Type", contentType);
          exchange.sendResponseHeaders(Integer.parseInt(status), bytes.length);
          try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
          }
        });
    server.start();

    config = new LlmConfigurations();
    config.setProvider(LlmProvider.OTHER);
    config.setCustomBaseUrl("http://127.0.0.1:" + server.getAddress().getPort());
    config.setApiKey("key");
  }

  @AfterEach
  void stopServer() {
    server.stop(0);
  }

  private void answer(int status, String body) {
    this.status = String.valueOf(status);
    this.body = body;
  }

  @Test
  @DisplayName("an OpenAI-shaped \"data\" array is read and sorted")
  void openAiShape() throws IOException {
    answer(200, "{\"data\":[{\"id\":\"gpt-4o-mini\"},{\"id\":\"gpt-4o\"}],\"object\":\"list\"}");

    assertThat(LlmModelsController.listModels(config)).containsExactly("gpt-4o", "gpt-4o-mini").inOrder();
  }

  @Test
  @DisplayName("a native Gemini \"models\" array is read, with the \"models/\" prefix stripped")
  void geminiNativeShape() throws IOException {
    answer(
        200,
        "{\"models\":[{\"name\":\"models/gemini-2.0-flash\"},{\"name\":\"models/gemini-1.5-pro\"}]}");

    assertThat(LlmModelsController.listModels(config))
        .containsExactly("gemini-1.5-pro", "gemini-2.0-flash")
        .inOrder();
  }

  @Test
  @DisplayName("duplicate ids across both shapes are folded into one")
  void deduplicates() throws IOException {
    answer(200, "{\"data\":[{\"id\":\"a\"},{\"id\":\"a\"},{\"id\":\"b\"}]}");

    assertThat(LlmModelsController.listModels(config)).containsExactly("a", "b").inOrder();
  }

  @Test
  @DisplayName("an unauthorized response names the API key as the problem")
  void unauthorized() {
    answer(401, "{\"error\":\"invalid api key\"}");

    final IOException e =
        assertThrows(IOException.class, () -> LlmModelsController.listModels(config));
    assertThat(e.getMessage()).contains("API key");
  }

  @Test
  @DisplayName("a 429 is reported as rate limiting, not a generic failure")
  void rateLimited() {
    answer(429, "{}");

    final IOException e =
        assertThrows(IOException.class, () -> LlmModelsController.listModels(config));
    assertThat(e.getMessage()).contains("rate-limiting");
  }

  @Test
  @DisplayName("a response with neither array is reported as an unsupported endpoint")
  void unrecognizedShape() {
    answer(200, "{\"unexpected\":true}");

    final IOException e =
        assertThrows(IOException.class, () -> LlmModelsController.listModels(config));
    assertThat(e.getMessage()).contains("did not return a model list");
  }

  @Test
  @DisplayName("no base URL fails locally, without a request")
  void noBaseUrl() {
    config.setProvider(LlmProvider.OTHER);
    config.setCustomBaseUrl("");

    final IOException e =
        assertThrows(IOException.class, () -> LlmModelsController.listModels(config));
    assertThat(e.getMessage()).contains("No base URL");
  }
}
