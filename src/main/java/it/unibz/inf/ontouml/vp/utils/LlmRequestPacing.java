package it.unibz.inf.ontouml.vp.utils;

import it.unibz.inf.ontouml.vp.model.LlmConfigurations;
import it.unibz.inf.ontouml.vp.model.LlmConversationLog;
import it.unibz.inf.ontouml.vp.model.LlmExchange;
import java.io.IOException;
import java.util.function.LongSupplier;

/**
 * Keeps the calls to the model under a provider's rate limit: a pause the user can ask for between
 * calls, and a wait-and-retry when the provider answers HTTP 429 anyway.
 *
 * <p>Documenting one class makes a request per source read, one per part of a long document, and
 * the documentation itself. On a free tier that allows a few requests a minute, that alone is
 * enough to be refused — and refusing the whole run because the ninth request came a second too
 * early throws away every source already read.
 *
 * <p>Every wait is interruptible, since it happens on the worker's thread and the user can cancel;
 * and it is reported to the task's progress window, because a dialog that sits silent for thirty
 * seconds looks exactly like one that has hung.
 */
public class LlmRequestPacing {

  /** How long to wait after a 429 before trying again. */
  public static final int RATE_LIMIT_WAIT_SECONDS = 30;

  /** How many times a refused request is retried before the refusal is reported. */
  public static final int MAX_RATE_LIMIT_RETRIES = 3;

  /**
   * A server that asks for longer than this is not rate-limiting, it is out of quota — a daily
   * limit answers 429 too. Waiting minutes for a quota that resets tomorrow helps nobody.
   */
  static final int MAX_RETRY_AFTER_SECONDS = 120;

  /** Sleeps, and is replaceable so a test does not have to wait thirty real seconds. */
  interface Sleeper {
    void sleep(long millis) throws InterruptedException;
  }

  private static Sleeper sleeper = Thread::sleep;
  private static LongSupplier clock = System::currentTimeMillis;

  private static final Object LOCK = new Object();

  /** The earliest the next call may start, when a delay between calls is configured. */
  private static long nextCallAllowedAt = 0;

  /**
   * Where waits are reported. A thread-local rather than a parameter because the calls are made
   * from a handful of places several layers below the worker that owns the progress window, and
   * every one of them would otherwise have to carry it.
   */
  private static final ThreadLocal<TaskProgress> PROGRESS = new ThreadLocal<>();

  private LlmRequestPacing() {}

  /** Makes this thread's waits show up in {@code progress}. Undo with {@link #unbind()}. */
  public static void bind(TaskProgress progress) {
    PROGRESS.set(progress);
  }

  public static void unbind() {
    PROGRESS.remove();
  }

  /**
   * Waits out the configured pause since the previous call, if there is one to wait for.
   *
   * @throws IOException when the task is cancelled while waiting.
   */
  public static void beforeCall(LlmConfigurations config) throws IOException {
    if (!config.isDelayBetweenCalls()) {
      return;
    }

    final long wait;

    // The slot is taken in the same step that it is looked at. Reading the time of the previous call
    // and only recording this one afterwards let two tasks running at once both find the way clear
    // and start together; with the slot reserved here, each gets its own, a delay apart.
    synchronized (LOCK) {
      final long now = clock.getAsLong();
      final long slot = Math.max(now, nextCallAllowedAt);

      wait = slot - now;
      nextCallAllowedAt = slot + config.getDelayBetweenCallsSeconds() * 1000L;
    }

    if (wait > 0) {
      pause(
          wait,
          "Pausa de " + config.getDelayBetweenCallsSeconds() + "s entre chamadas ao modelo");
    }
  }

  /** Starts the pause: the next call may not begin until the configured time has passed. */
  public static void afterCall(LlmConfigurations config) {
    if (!config.isDelayBetweenCalls()) {
      return;
    }

    synchronized (LOCK) {
      // never earlier than what was already promised to the calls waiting behind this one
      nextCallAllowedAt =
          Math.max(
              nextCallAllowedAt,
              clock.getAsLong() + config.getDelayBetweenCallsSeconds() * 1000L);
    }
  }

  /**
   * How long to wait after a 429, in seconds.
   *
   * <p>Thirty, unless the provider says it needs longer: a {@code Retry-After} above that is
   * honoured, because retrying earlier would only be refused again. One beyond
   * {@link #MAX_RETRY_AFTER_SECONDS} means the quota is spent, not that the pace is too fast.
   *
   * @param retryAfter the {@code Retry-After} header, or null.
   * @return the seconds to wait, or -1 when waiting would not help.
   */
  public static int rateLimitWaitSeconds(String retryAfter) {
    int wait = RATE_LIMIT_WAIT_SECONDS;

    if (retryAfter != null) {
      try {
        final int asked = Integer.parseInt(retryAfter.trim());

        if (asked > MAX_RETRY_AFTER_SECONDS) {
          return -1;
        }

        wait = Math.max(wait, asked);
      } catch (NumberFormatException e) {
        // the header may also be an HTTP date; the fixed wait is a sound answer to that too
      }
    }

    return wait;
  }

  /**
   * Waits after a 429 and records that it did, so the conversation log shows why a run took a
   * minute longer than it should have.
   *
   * @throws IOException when the task is cancelled while waiting.
   */
  public static void waitAfterRateLimit(
      LlmConfigurations config, String url, int retry, int maxRetries, int seconds)
      throws IOException {
    final LlmExchange exchange =
        new LlmExchange(
            "Rate limit (HTTP 429) — waiting " + seconds + "s, retry " + retry + " of " + maxRetries,
            config,
            url,
            "Rate-limit handling",
            "The provider answered HTTP 429 (too many requests). The request is retried after a"
                + " wait, up to "
                + maxRetries
                + " times.");
    final long startedAt = clock.getAsLong();

    try {
      pause(
          seconds * 1000L,
          "O provedor limitou as requisições (HTTP 429) — nova tentativa "
              + retry
              + " de "
              + maxRetries);
      exchange.succeeded("Waited " + seconds + "s, then tried again.", clock.getAsLong() - startedAt);
    } catch (IOException e) {
      exchange.failed(e.getMessage(), clock.getAsLong() - startedAt);
      throw e;
    } finally {
      LlmConversationLog.getInstance().record(exchange);
    }
  }

  /**
   * Sleeps for the given time in one-second slices, so the progress window can count it down and a
   * cancellation is noticed within a second.
   */
  private static void pause(long millis, String label) throws IOException {
    final TaskProgress progress = PROGRESS.get();
    final String previous = progress == null ? "" : progress.snapshot().getActivity();
    final long end = clock.getAsLong() + millis;

    try {
      while (true) {
        final long left = end - clock.getAsLong();

        if (left <= 0) {
          break;
        }

        if (progress != null) {
          progress.activity(label + " — " + ((left + 999) / 1000) + "s");
        }

        sleeper.sleep(Math.min(left, 1000));
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IOException("Cancelled while waiting for the provider's rate limit.");
    } finally {
      // the wait is over: put back what the step was doing, instead of leaving a stale countdown
      if (progress != null) {
        progress.activity(previous);
      }
    }
  }

  /** For tests: replaces the sleep and the clock, and clears the pause between calls. */
  static void useForTesting(Sleeper testSleeper, LongSupplier testClock) {
    sleeper = testSleeper;
    clock = testClock;

    synchronized (LOCK) {
      nextCallAllowedAt = 0;
    }
  }

  /** For tests: back to the real clock. */
  static void useRealTime() {
    sleeper = Thread::sleep;
    clock = System::currentTimeMillis;

    synchronized (LOCK) {
      nextCallAllowedAt = 0;
    }
  }
}
