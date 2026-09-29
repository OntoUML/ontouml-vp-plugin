package it.unibz.inf.ontouml.vp.utils;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sun.net.httpserver.HttpServer;
import it.unibz.inf.ontouml.vp.controllers.LlmAccessController;
import it.unibz.inf.ontouml.vp.model.LlmConfigurations;
import it.unibz.inf.ontouml.vp.model.LlmProvider;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * The retry loop against a real HTTP server that answers 429. The sleep is replaced, so the thirty
 * seconds are asserted rather than waited.
 */
class LlmRateLimitRetryTest {

  private static final String OK_BODY =
      "{\"choices\":[{\"message\":{\"content\":\"Resumo curto.\"}}]}";

  /** One scripted answer of the server. */
  private static final class Reply {
    private final int status;
    private final String retryAfter;
    private final String body;

    private Reply(int status, String retryAfter, String body) {
      this.status = status;
      this.retryAfter = retryAfter;
      this.body = body;
    }
  }

  private static Reply ok() {
    return new Reply(200, null, OK_BODY);
  }

  private static Reply rateLimited() {
    return new Reply(429, null, "{\"error\":{\"message\":\"Resource has been exhausted\"}}");
  }

  private static Reply rateLimited(String retryAfter) {
    return new Reply(429, retryAfter, "{\"error\":{\"message\":\"Resource has been exhausted\"}}");
  }

  private HttpServer server;
  private final List<Reply> script = new ArrayList<>();
  private final AtomicInteger requests = new AtomicInteger();

  private final AtomicLong now = new AtomicLong(1_000_000);
  private final List<Long> slept = new ArrayList<>();

  private LlmConfigurations config;

  @BeforeEach
  void startServer() throws IOException {
    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext(
        "/chat/completions",
        exchange -> {
          final int index = requests.getAndIncrement();
          // past the end of the script, the last answer repeats: a provider that never relents
          final Reply reply = script.get(Math.min(index, script.size() - 1));
          final byte[] body = reply.body.getBytes(StandardCharsets.UTF_8);

          exchange.getResponseHeaders().add("Content-Type", "application/json");

          if (reply.retryAfter != null) {
            exchange.getResponseHeaders().add("Retry-After", reply.retryAfter);
          }

          exchange.sendResponseHeaders(reply.status, body.length);

          try (OutputStream out = exchange.getResponseBody()) {
            out.write(body);
          }
        });
    server.start();

    config = new LlmConfigurations();
    config.setProvider(LlmProvider.OTHER);
    config.setCustomBaseUrl("http://127.0.0.1:" + server.getAddress().getPort());
    config.setApiKey("key");
    config.setModel("model");

    LlmRequestPacing.useForTesting(
        millis -> {
          slept.add(millis);
          now.addAndGet(millis);
        },
        now::get);
  }

  @AfterEach
  void stopServer() {
    server.stop(0);
    LlmRequestPacing.useRealTime();
    Thread.interrupted();
  }

  private long totalSlept() {
    return slept.stream().mapToLong(Long::longValue).sum();
  }

  private String summarise() throws IOException {
    return LlmAccessController.requestSourceSummary(config, "Conceito", "Fonte", "http://f", "texto");
  }

  @Test
  @DisplayName("a 429 is waited out for thirty seconds and the same request is sent again")
  void retriesAfterThirtySeconds() throws IOException {
    script.add(rateLimited());
    script.add(ok());

    assertThat(summarise()).isEqualTo("Resumo curto.");

    assertThat(requests.get()).isEqualTo(2);
    assertThat(totalSlept()).isEqualTo(30_000);
  }

  @Test
  @DisplayName("it keeps waiting through several 429s in a row")
  void retriesSeveralTimes() throws IOException {
    script.add(rateLimited());
    script.add(rateLimited());
    script.add(ok());

    assertThat(summarise()).isEqualTo("Resumo curto.");

    assertThat(requests.get()).isEqualTo(3);
    assertThat(totalSlept()).isEqualTo(60_000);
  }

  @Test
  @DisplayName("after three retries a provider that never relents is reported, not retried forever")
  void givesUpAfterThreeRetries() {
    script.add(rateLimited());

    final IOException e = assertThrows(IOException.class, this::summarise);

    // the first request and three retries
    assertThat(requests.get()).isEqualTo(4);
    assertThat(totalSlept()).isEqualTo(90_000);
    assertThat(e).hasMessageThat().contains("HTTP 429");
    assertThat(e).hasMessageThat().contains("waited 30 seconds and tried again 3 times");
  }

  @Test
  @DisplayName("a Retry-After longer than thirty seconds is honoured")
  void honoursALongerRetryAfter() throws IOException {
    script.add(rateLimited("45"));
    script.add(ok());

    summarise();

    assertThat(totalSlept()).isEqualTo(45_000);
  }

  @Test
  @DisplayName("a Retry-After of minutes means a spent quota: fail at once, wait for nothing")
  void doesNotWaitOutASpentQuota() {
    script.add(rateLimited("3600"));

    final IOException e = assertThrows(IOException.class, this::summarise);

    assertThat(requests.get()).isEqualTo(1);
    assertThat(slept).isEmpty();
    assertThat(e).hasMessageThat().contains("spent quota");
  }

  @Test
  @DisplayName("an error that is not a 429 is reported at once and never retried")
  void otherErrorsAreNotRetried() {
    script.add(new Reply(401, null, "{\"error\":{\"message\":\"bad key\"}}"));

    assertThrows(IOException.class, this::summarise);

    assertThat(requests.get()).isEqualTo(1);
    assertThat(slept).isEmpty();
  }

  @Test
  @DisplayName("the Test button reports a 429 as it is, without a minute and a half of waiting")
  void testConnectionDoesNotRetry() {
    script.add(rateLimited());

    assertThrows(IOException.class, () -> LlmAccessController.testConnection(config));

    assertThat(requests.get()).isEqualTo(1);
    assertThat(slept).isEmpty();
  }

  @Test
  @DisplayName("cancelling while a 429 is being waited out stops it, without another request")
  void cancellingStopsTheRetry() {
    script.add(rateLimited());
    script.add(ok());

    LlmRequestPacing.useForTesting(
        millis -> {
          throw new InterruptedException();
        },
        now::get);

    final IOException e = assertThrows(IOException.class, this::summarise);

    assertThat(requests.get()).isEqualTo(1);
    assertThat(e).hasMessageThat().contains("Cancelled");
  }

  @Test
  @DisplayName("with a delay configured, the second call waits those seconds and the first does not")
  void delayBetweenCalls() throws IOException {
    script.add(ok());
    config.setDelayBetweenCalls(true);
    config.setDelayBetweenCallsSeconds(7);

    summarise();
    assertThat(slept).isEmpty();

    summarise();
    assertThat(totalSlept()).isEqualTo(7_000);
    assertThat(requests.get()).isEqualTo(2);
  }

  @Test
  @DisplayName("without the delay, calls follow one another with no wait")
  void noDelayByDefault() throws IOException {
    script.add(ok());

    summarise();
    summarise();
    summarise();

    assertThat(slept).isEmpty();
  }

  @Test
  @DisplayName("the delay and the 429 wait are independent: both apply when both are needed")
  void delayAndRateLimitCombine() throws IOException {
    script.add(ok());
    script.add(rateLimited());
    script.add(ok());
    config.setDelayBetweenCalls(true);
    config.setDelayBetweenCallsSeconds(5);

    summarise(); // ok
    summarise(); // waits 5s, is refused, waits 30s, succeeds

    assertThat(requests.get()).isEqualTo(3);
    assertThat(totalSlept()).isAtLeast(35_000);
  }
}
