package it.unibz.inf.ontouml.vp.utils;

import static com.google.common.truth.Truth.assertThat;

import it.unibz.inf.ontouml.vp.utils.TaskProgress.Snapshot;
import it.unibz.inf.ontouml.vp.utils.TaskProgress.State;
import it.unibz.inf.ontouml.vp.utils.TaskProgress.Step;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TaskProgressTest {

  @Test
  @DisplayName("steps keep the order they were declared in, all pending")
  void declaredInOrder() {
    final TaskProgress progress = new TaskProgress("Documentando \"X\"");
    progress.step("Contexto");
    progress.step("Fontes");
    progress.step("Geração");

    final Snapshot snapshot = progress.snapshot();

    assertThat(snapshot.getTitle()).isEqualTo("Documentando \"X\"");
    assertThat(snapshot.getSteps()).hasSize(3);
    assertThat(snapshot.getSteps().get(0).getLabel()).isEqualTo("Contexto");
    assertThat(snapshot.getSteps().get(2).getLabel()).isEqualTo("Geração");

    for (TaskProgress.StepView step : snapshot.getSteps()) {
      assertThat(step.getState()).isEqualTo(State.PENDING);
    }
  }

  @Test
  @DisplayName("a step moves from pending to running to done")
  void lifecycle() {
    final TaskProgress progress = new TaskProgress("t");
    final Step step = progress.step("Fontes");

    progress.start(step);
    assertThat(progress.snapshot().getSteps().get(0).getState()).isEqualTo(State.RUNNING);
    assertThat(progress.snapshot().getRunning().getLabel()).isEqualTo("Fontes");

    progress.finish(step);
    assertThat(progress.snapshot().getSteps().get(0).getState()).isEqualTo(State.DONE);
    assertThat(progress.snapshot().getRunning()).isNull();
  }

  @Test
  @DisplayName("a step that does not run says so, instead of staying pending forever")
  void skippedCarriesItsReason() {
    final TaskProgress progress = new TaskProgress("t");
    final Step step = progress.step("Pesquisa na web");

    progress.skip(step, "desligada nas configurações");

    final TaskProgress.StepView view = progress.snapshot().getSteps().get(0);
    assertThat(view.getState()).isEqualTo(State.SKIPPED);
    assertThat(view.getNote()).isEqualTo("desligada nas configurações");
  }

  @Test
  @DisplayName("a failed step keeps the reason")
  void failedCarriesItsReason() {
    final TaskProgress progress = new TaskProgress("t");
    final Step step = progress.step("Geração");

    progress.start(step);
    progress.fail(step, "chave de API rejeitada");

    final TaskProgress.StepView view = progress.snapshot().getSteps().get(0);
    assertThat(view.getState()).isEqualTo(State.FAILED);
    assertThat(view.getNote()).isEqualTo("chave de API rejeitada");
  }

  @Test
  @DisplayName("failRunning fails the running step and leaves the others alone")
  void failRunningOnlyTouchesTheRunningStep() {
    final TaskProgress progress = new TaskProgress("t");
    final Step done = progress.step("a");
    final Step running = progress.step("b");
    progress.step("c");

    progress.start(done);
    progress.finish(done);
    progress.start(running);
    progress.failRunning("caiu");

    assertThat(progress.snapshot().getSteps().get(0).getState()).isEqualTo(State.DONE);
    assertThat(progress.snapshot().getSteps().get(1).getState()).isEqualTo(State.FAILED);
    assertThat(progress.snapshot().getSteps().get(2).getState()).isEqualTo(State.PENDING);
  }

  @Test
  @DisplayName("a count gives the step a denominator, which is what makes its bar determinate")
  void counts() {
    final TaskProgress progress = new TaskProgress("t");
    final Step step = progress.step("Fontes");

    assertThat(progress.snapshot().getSteps().get(0).hasCount()).isFalse();

    progress.start(step);
    progress.count(step, 3, 12);

    final TaskProgress.StepView view = progress.snapshot().getSteps().get(0);
    assertThat(view.hasCount()).isTrue();
    assertThat(view.getCurrent()).isEqualTo(3);
    assertThat(view.getTotal()).isEqualTo(12);
  }

  @Test
  @DisplayName("activity is cleared when a step starts or finishes")
  void activityDoesNotOutliveItsStep() {
    final TaskProgress progress = new TaskProgress("t");
    final Step first = progress.step("a");
    final Step second = progress.step("b");

    progress.start(first);
    progress.activity("Convertendo PDF com Docling");
    assertThat(progress.snapshot().getActivity()).isEqualTo("Convertendo PDF com Docling");

    progress.finish(first);
    assertThat(progress.snapshot().getActivity()).isEmpty();

    progress.start(second);
    assertThat(progress.snapshot().getActivity()).isEmpty();
  }

  @Test
  @DisplayName("the one-line summary says the step, its count and what it is doing")
  void summarises() {
    final TaskProgress progress = new TaskProgress("Documentando \"X\"");
    final Step step = progress.step("Leitura das fontes");

    // between steps there is nothing running, so the title is all there is to say
    assertThat(progress.snapshot().summarise()).isEqualTo("Documentando \"X\"");

    progress.start(step);
    progress.count(step, 3, 12);
    progress.activity("Convertendo com Docling");

    assertThat(progress.snapshot().summarise())
        .isEqualTo("Leitura das fontes (3 de 12) — Convertendo com Docling");
  }

  @Test
  @DisplayName("a snapshot is frozen: later changes do not reach a reader that already has one")
  void snapshotsAreFrozen() {
    final TaskProgress progress = new TaskProgress("t");
    final Step step = progress.step("a");

    final Snapshot before = progress.snapshot();
    progress.start(step);

    assertThat(before.getSteps().get(0).getState()).isEqualTo(State.PENDING);
    assertThat(progress.snapshot().getSteps().get(0).getState()).isEqualTo(State.RUNNING);
  }

  @Test
  @DisplayName("the silent progress accepts everything and shows nothing")
  void silentIsHarmless() {
    final TaskProgress silent = TaskProgress.silent();

    silent.activity("Baixando com curl");
    silent.activity(null);

    assertThat(silent.snapshot().getSteps()).isEmpty();
    assertThat(silent.snapshot().getActivity()).isEmpty();
  }

  @Test
  @DisplayName("readers on another thread never see a torn state while the worker writes")
  void safeAcrossThreads() throws Exception {
    final TaskProgress progress = new TaskProgress("t");
    final Step step = progress.step("Fontes");
    final boolean[] failed = {false};

    final Thread reader =
        new Thread(
            () -> {
              for (int i = 0; i < 2000; i++) {
                try {
                  final Snapshot snapshot = progress.snapshot();
                  snapshot.summarise();
                } catch (RuntimeException e) {
                  failed[0] = true;
                }
              }
            });

    reader.start();

    progress.start(step);

    for (int i = 0; i < 2000; i++) {
      progress.count(step, i, 2000);
      progress.activity("fonte " + i);
    }

    reader.join();

    assertThat(failed[0]).isFalse();
  }
}
