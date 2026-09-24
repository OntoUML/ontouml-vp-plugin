package it.unibz.inf.ontouml.vp.views;

import static com.google.common.truth.Truth.assertThat;

import it.unibz.inf.ontouml.vp.utils.TaskProgress;
import it.unibz.inf.ontouml.vp.utils.TaskProgress.State;
import it.unibz.inf.ontouml.vp.utils.TaskProgress.Step;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StepProgressPanelTest {

  @Test
  @DisplayName("elapsed time reads m:ss, and h:mm:ss past the hour")
  void formatsElapsedTime() {
    assertThat(StepProgressPanel.formatElapsed(0)).isEqualTo("0:00");
    assertThat(StepProgressPanel.formatElapsed(9_000)).isEqualTo("0:09");
    assertThat(StepProgressPanel.formatElapsed(83_000)).isEqualTo("1:23");
    assertThat(StepProgressPanel.formatElapsed(3_600_000 + 5 * 60_000 + 7_000))
        .isEqualTo("1:05:07");
    assertThat(StepProgressPanel.formatElapsed(-5_000)).isEqualTo("0:00");
  }

  @Test
  @DisplayName("a running step shows how far it is, a finished one how many it went through")
  void describesCounts() {
    final TaskProgress progress = new TaskProgress("t");
    final Step step = progress.step("Leitura das fontes");

    progress.start(step);
    progress.count(step, 3, 12);
    assertThat(StepProgressPanel.describe(progress.snapshot().getSteps().get(0)))
        .isEqualTo("Leitura das fontes (3 de 12)");

    progress.count(step, 7, 7);
    progress.finish(step);
    assertThat(StepProgressPanel.describe(progress.snapshot().getSteps().get(0)))
        .isEqualTo("Leitura das fontes (7)");
  }

  @Test
  @DisplayName("a step with no count is just its label")
  void describesUncounted() {
    final TaskProgress progress = new TaskProgress("t");
    progress.step("Contexto do modelo");

    assertThat(StepProgressPanel.describe(progress.snapshot().getSteps().get(0)))
        .isEqualTo("Contexto do modelo");
  }

  @Test
  @DisplayName("every state has its own marker, drawn from characters any dialog font has")
  void markersAreDistinctAndSafe() {
    final java.util.Set<String> seen = new java.util.HashSet<>();

    for (State state : State.values()) {
      final String marker = StepProgressPanel.marker(state);

      assertThat(marker).isNotEmpty();
      assertThat(seen.add(marker)).isTrue();

      // a glyph outside the Latin-1 and geometric-shapes blocks may render as an empty box
      final char c = marker.charAt(0);
      assertThat(c <= 0xFF || (c >= 0x2010 && c <= 0x2015) || (c >= 0x25A0 && c <= 0x25FF))
          .isTrue();
    }
  }

  @Test
  @DisplayName("the panel builds, refreshes as steps change, and stops, without a display")
  void refreshesHeadless() {
    System.setProperty("java.awt.headless", "true");

    final TaskProgress progress = new TaskProgress("Documentando \"X\"");
    final Step first = progress.step("Contexto");
    final Step second = progress.step("Fontes");

    final StepProgressPanel panel = new StepProgressPanel(progress);

    progress.start(first);
    panel.refresh();
    progress.finish(first);
    progress.start(second);
    progress.count(second, 1, 5);
    progress.activity("Convertendo com Docling");
    panel.refresh();
    progress.skip(second, "nada a ler");
    panel.refresh();

    panel.startRefreshing();
    panel.stopRefreshing();
    // stopping twice is what happens when a dialog is closed after its task finished
    panel.stopRefreshing();
  }
}
