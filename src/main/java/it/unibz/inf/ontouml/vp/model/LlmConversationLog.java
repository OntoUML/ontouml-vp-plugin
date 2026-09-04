package it.unibz.inf.ontouml.vp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * In-memory history of the LLM conversations of the current session, newest first.
 *
 * <p>It is deliberately not persisted: the prompts embed the user's model and the log is meant for
 * inspecting what was just asked, not for keeping a permanent record on disk.
 */
public class LlmConversationLog {

  private static final int MAX_ENTRIES = 25;

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

    while (exchanges.size() > MAX_ENTRIES) {
      exchanges.removeLast();
    }
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
