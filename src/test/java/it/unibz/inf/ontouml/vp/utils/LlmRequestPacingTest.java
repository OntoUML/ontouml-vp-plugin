package it.unibz.inf.ontouml.vp.utils;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import it.unibz.inf.ontouml.vp.model.LlmConfigurations;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Nothing here waits: the clock is fake and the sleep only advances it. */
class LlmRequestPacingTest {

  private final AtomicLong now = new AtomicLong(1_000_000);
  private final List<Long> slept = new ArrayList<>();

  @BeforeEach
  void useFakeTime() {
    LlmRequestPacing.useForTesting(
        millis -> {
          slept.add(millis);
          now.addAndGet(millis);
        },
        now::get);
  }

  @AfterEach
  void restoreRealTime() {
    LlmRequestPacing.useRealTime();
    LlmRequestPacing.unbind();
  }

  private long totalSlept() {
    return slept.stream().mapToLong(Long::longValue).sum();
  }

  private static LlmConfigurations delayed(int seconds) {
    final LlmConfigurations config = new LlmConfigurations();
    config.setDelayBetweenCalls(true);
    config.setDelayBetweenCallsSeconds(seconds);

    return config;
  }

  // ---- how long to wait after a 429

  @Test
  @DisplayName("a 429 with no Retry-After waits the thirty seconds it was asked to")
  void defaultWait() {
    assertThat(LlmRequestPacing.rateLimitWaitSeconds(null)).isEqualTo(30);
  }

  @Test
  @DisplayName("a Retry-After shorter than thirty is not a reason to retry sooner")
  void shortRetryAfterStillWaitsThirty() {
    assertThat(LlmRequestPacing.rateLimitWaitSeconds("5")).isEqualTo(30);
    assertThat(LlmRequestPacing.rateLimitWaitSeconds("0")).isEqualTo(30);
  }

  @Test
  @DisplayName("a Retry-After longer than thirty is honoured, since retrying earlier is refused again")
  void longerRetryAfterIsHonoured() {
    assertThat(LlmRequestPacing.rateLimitWaitSeconds("45")).isEqualTo(45);
    assertThat(LlmRequestPacing.rateLimitWaitSeconds(" 90 ")).isEqualTo(90);
  }

  @Test
  @DisplayName("a Retry-After beyond two minutes means the quota is spent: do not wait")
  void hugeRetryAfterMeansGiveUp() {
    assertThat(LlmRequestPacing.rateLimitWaitSeconds("121")).isEqualTo(-1);
    assertThat(LlmRequestPacing.rateLimitWaitSeconds("86400")).isEqualTo(-1);
  }

  @Test
  @DisplayName("a Retry-After that is an HTTP date, or garbage, falls back to thirty")
  void unparseableRetryAfter() {
    assertThat(LlmRequestPacing.rateLimitWaitSeconds("Wed, 21 Oct 2026 07:28:00 GMT"))
        .isEqualTo(30);
    assertThat(LlmRequestPacing.rateLimitWaitSeconds("soon")).isEqualTo(30);
  }

  // ---- the pause between calls

  @Test
  @DisplayName("with the delay off, nothing is ever waited")
  void disabledNeverWaits() throws IOException {
    final LlmConfigurations config = new LlmConfigurations();

    LlmRequestPacing.beforeCall(config);
    LlmRequestPacing.afterCall(config);
    LlmRequestPacing.beforeCall(config);

    assertThat(slept).isEmpty();
  }

  @Test
  @DisplayName("the first call goes straight through")
  void firstCallIsNotDelayed() throws IOException {
    LlmRequestPacing.beforeCall(delayed(5));

    assertThat(slept).isEmpty();
  }

  @Test
  @DisplayName("the next call waits out the configured seconds after the previous one")
  void secondCallWaits() throws IOException {
    final LlmConfigurations config = delayed(5);

    LlmRequestPacing.beforeCall(config);
    LlmRequestPacing.afterCall(config);
    LlmRequestPacing.beforeCall(config);

    assertThat(totalSlept()).isEqualTo(5_000);
  }

  @Test
  @DisplayName("calls that overlap each get their own slot, a delay apart")
  void overlappingCallsAreSpacedOut() throws IOException {
    final LlmConfigurations config = delayed(5);

    // three tasks ask before any of them has finished a call
    LlmRequestPacing.beforeCall(config);
    LlmRequestPacing.beforeCall(config);
    LlmRequestPacing.beforeCall(config);

    assertThat(slept).isNotEmpty();
    assertThat(totalSlept()).isEqualTo(10_000);
  }

  @Test
  @DisplayName("a call that ends later than the reserved slot still pushes the next one back")
  void aLongCallDelaysTheNext() throws IOException {
    final LlmConfigurations config = delayed(5);

    LlmRequestPacing.beforeCall(config);
    now.addAndGet(20_000); // the call itself took twenty seconds
    LlmRequestPacing.afterCall(config);
    LlmRequestPacing.beforeCall(config);

    assertThat(totalSlept()).isEqualTo(5_000);
  }

  @Test
  @DisplayName("time already spent since the last call counts towards the pause")
  void elapsedTimeIsCredited() throws IOException {
    final LlmConfigurations config = delayed(5);

    LlmRequestPacing.afterCall(config);
    now.addAndGet(3_000); // the caller was busy for three seconds on its own
    LlmRequestPacing.beforeCall(config);

    assertThat(totalSlept()).isEqualTo(2_000);
  }

  @Test
  @DisplayName("a call that comes after the pause has passed is not delayed at all")
  void noWaitOnceItHasPassed() throws IOException {
    final LlmConfigurations config = delayed(5);

    LlmRequestPacing.afterCall(config);
    now.addAndGet(6_000);
    LlmRequestPacing.beforeCall(config);

    assertThat(slept).isEmpty();
  }

  @Test
  @DisplayName("the configured seconds are what is waited, not a fixed number")
  void secondsAreConfigurable() throws IOException {
    final LlmConfigurations config = delayed(12);

    LlmRequestPacing.afterCall(config);
    LlmRequestPacing.beforeCall(config);

    assertThat(totalSlept()).isEqualTo(12_000);
  }

  @Test
  @DisplayName("the wait is sliced to a second, so cancelling is noticed within one")
  void slicedToOneSecond() throws IOException {
    final LlmConfigurations config = delayed(4);

    LlmRequestPacing.afterCall(config);
    LlmRequestPacing.beforeCall(config);

    assertThat(slept).containsExactly(1000L, 1000L, 1000L, 1000L);
  }

  // ---- waiting after a 429

  @Test
  @DisplayName("waiting after a 429 sleeps the full time")
  void waitsThirtySeconds() throws IOException {
    LlmRequestPacing.waitAfterRateLimit(new LlmConfigurations(), "http://x", 1, 3, 30);

    assertThat(totalSlept()).isEqualTo(30_000);
  }

  @Test
  @DisplayName("a wait counts down in the task's progress window, then puts back what it replaced")
  void reportsToTheProgressWindow() throws IOException {
    final TaskProgress progress = new TaskProgress("t");
    final List<String> shown = new ArrayList<>();

    // record what the window would have shown at every tick
    LlmRequestPacing.useForTesting(
        millis -> {
          shown.add(progress.snapshot().getActivity());
          now.addAndGet(millis);
        },
        now::get);

    LlmRequestPacing.bind(progress);
    progress.activity("Resumindo \"Lei 14.133\"");

    LlmRequestPacing.waitAfterRateLimit(new LlmConfigurations(), "http://x", 2, 3, 3);

    assertThat(shown).hasSize(3);
    assertThat(shown.get(0)).contains("HTTP 429");
    assertThat(shown.get(0)).contains("nova tentativa 2 de 3");
    assertThat(shown.get(0)).endsWith("3s");
    assertThat(shown.get(2)).endsWith("1s");

    // and once the wait is over the countdown is gone, not left stale
    assertThat(progress.snapshot().getActivity()).isEqualTo("Resumindo \"Lei 14.133\"");
  }

  @Test
  @DisplayName("cancelling during the wait ends it at once, as an error, keeping the interrupt")
  void cancellingInterrupts() {
    LlmRequestPacing.useForTesting(
        millis -> {
          throw new InterruptedException();
        },
        now::get);

    final IOException e =
        assertThrows(
            IOException.class,
            () ->
                LlmRequestPacing.waitAfterRateLimit(
                    new LlmConfigurations(), "http://x", 1, 3, 30));

    assertThat(e).hasMessageThat().contains("Cancelled");
    assertThat(Thread.currentThread().isInterrupted()).isTrue();

    Thread.interrupted(); // do not leak the flag into the next test
  }
}
