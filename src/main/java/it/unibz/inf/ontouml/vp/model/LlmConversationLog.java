package it.unibz.inf.ontouml.vp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * In-memory history of the LLM conversations of the current session, newest first.
 *
 * <p>Not persisted by default: the prompts embed the user's model, and the log is meant for
 * inspecting what was just asked rather than for keeping a permanent record on disk. Turning on
 * debug mode in the settings lifts the cap and mirrors every exchange to a file — see {@link
 * it.unibz.inf.ontouml.vp.utils.DebugLogWriter}.
 */
public class LlmConversationLog {

  private static final int MAX_ENTRIES = 25;

  /**
   * Still bounded while debugging, because this list lives in memory and backs a window — the full
   * record is the file on disk, which has no cap.
   */
  private static final int MAX_ENTRIES_WHEN_DEBUGGING = 500;

  private static LlmConversationLog instance;

  private final Deque<LlmExchange> exchanges = new LinkedList<>();

  private LlmConversationLog() {}

  public static synchronized LlmConversationLog getInstance() {
    if (instance == null) {
      instance = new LlmConversationLog();
    }

    return instance;
  }

  public synchronized void record(LlmExchange exchange) {
    exchanges.addFirst(exchange);

    // Debug mode keeps everything. Documenting one class can produce a dozen exchanges — one per
    // source retrieved, one per part of a long document — so with the cap on, the ones worth
    // reading scroll out of the window before anyone opens it.
    final boolean debug = it.unibz.inf.ontouml.vp.utils.DebugLogWriter.isEnabled();

    while (exchanges.size() > (debug ? MAX_ENTRIES_WHEN_DEBUGGING : MAX_ENTRIES)) {
      exchanges.removeLast();
    }

    it.unibz.inf.ontouml.vp.utils.DebugLogWriter.write(exchange);
  }

  /** The recorded conversations, newest first. */
  public synchronized List<LlmExchange> getExchanges() {
    return Collections.unmodifiableList(new ArrayList<>(exchanges));
  }

  public synchronized boolean isEmpty() {
    return exchanges.isEmpty();
  }

  public synchronized void clear() {
    exchanges.clear();
  }
}
