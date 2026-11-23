package com.jinloes.system_design.saga;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SagaOrchestratorTest {
    @Test
    void completesAllStepsWhenNoStepFails() {
        List<String> events = new ArrayList<>();
        SagaOrchestrator<List<String>> saga = new SagaOrchestrator<>(List.of(
            new SagaOrchestrator.Step<>("reserve-inventory", log -> log.add("reserve"), log -> log.add("release")),
            new SagaOrchestrator.Step<>("authorize-payment", log -> log.add("authorize"), log -> log.add("void")),
            new SagaOrchestrator.Step<>("confirm-booking", log -> log.add("confirm"), log -> log.add("cancel"))
        ));

        var result = saga.execute(events);
        assertThat(result.status()).isEqualTo(SagaOrchestrator.Status.COMPLETED);
        assertThat(result.completedSteps())
            .containsExactly("reserve-inventory", "authorize-payment", "confirm-booking");
        assertThat(result.compensatedSteps()).isEmpty();
        assertThat(events).containsExactly("reserve", "authorize", "confirm");
    }

    @Test
    void compensatesCompletedStepsInReverseOrderOnStepFailure() {
        List<String> events = new ArrayList<>();
        SagaOrchestrator<List<String>> saga = new SagaOrchestrator<>(List.of(
            new SagaOrchestrator.Step<>("reserve-seat", log -> log.add("reserve-seat"), log -> log.add("release-seat")),
            new SagaOrchestrator.Step<>("hold-funds", log -> log.add("hold-funds"), log -> log.add("void-hold")),
            new SagaOrchestrator.Step<>(
                "issue-ticket",
                log -> {
                    throw new IllegalStateException("carrier gateway rejected issuance");
                },
                log -> log.add("revoke-ticket")
            )
        ));

        var result = saga.execute(events);
        assertThat(result.status()).isEqualTo(SagaOrchestrator.Status.COMPENSATED);
        assertThat(result.completedSteps()).containsExactly("reserve-seat", "hold-funds");
        assertThat(result.compensatedSteps()).containsExactly("hold-funds", "reserve-seat");
        assertThat(result.failureCause()).hasMessageContaining("carrier gateway rejected issuance");
        assertThat(events).containsExactly("reserve-seat", "hold-funds", "void-hold", "release-seat");
    }

    @Test
    void surfacesCompensationFailureWhileContinuingRemainingCompensations() {
        List<String> events = new ArrayList<>();
        SagaOrchestrator<List<String>> saga = new SagaOrchestrator<>(List.of(
            new SagaOrchestrator.Step<>("step-1", log -> log.add("do-1"), log -> log.add("undo-1")),
            new SagaOrchestrator.Step<>(
                "step-2",
                log -> log.add("do-2"),
                log -> {
                    throw new RuntimeException("compensation timeout on step-2");
                }
            ),
            new SagaOrchestrator.Step<>(
                "step-3",
                log -> {
                    throw new RuntimeException("step-3 failed");
                },
                log -> log.add("undo-3")
            )
        ));

        var result = saga.execute(events);
        assertThat(result.status()).isEqualTo(SagaOrchestrator.Status.COMPENSATION_FAILED);
        assertThat(result.compensatedSteps()).containsExactly("step-1");
        assertThat(result.compensationFailures()).hasSize(1);
        assertThat(events).containsExactly("do-1", "do-2", "undo-1");
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> new SagaOrchestrator<>(List.of()))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
