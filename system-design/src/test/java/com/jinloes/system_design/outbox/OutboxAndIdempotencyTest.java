package com.jinloes.system_design.outbox;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OutboxAndIdempotencyTest {
    @Test
    void commitsStateAndOutboxAtomicallyAndRollsBackBothOnFailure() {
        Clock clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        TransactionalOutbox outbox = new TransactionalOutbox(clock);

        outbox.executeAtomically(tx -> {
            tx.putState("payment:p1", "CAPTURED");
            tx.recordOutbox(new TransactionalOutbox.PendingEvent(
                "p1",
                "payment.captured",
                "{\"amount_minor\":2500}"
            ));
        });

        assertThatThrownBy(() -> outbox.executeAtomically(tx -> {
            tx.putState("payment:p2", "CAPTURED");
            tx.recordOutbox(new TransactionalOutbox.PendingEvent("p2", "payment.captured", "{}"));
            throw new IllegalStateException("simulated db constraint violation");
        })).isInstanceOf(IllegalStateException.class);

        assertThat(outbox.readState("payment:p1")).isEqualTo("CAPTURED");
        assertThat(outbox.readState("payment:p2")).isNull();
        assertThat(outbox.snapshotOutbox()).hasSize(1);
    }

    @Test
    void atLeastOnceRelayPairsWithIdempotentInboxToGuaranteeSingleSideEffect() {
        Clock clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        TransactionalOutbox outbox = new TransactionalOutbox(clock);
        IdempotentInbox<String> consumerInbox = new IdempotentInbox<>();

        outbox.executeAtomically(tx -> {
            tx.putState("order:o-100", "PAID");
            tx.recordOutbox(new TransactionalOutbox.PendingEvent(
                "o-100",
                "order.paid",
                "{\"orderId\":\"o-100\",\"amount\":4999}"
            ));
        });

        AtomicBoolean failAckAfterBrokerDelivery = new AtomicBoolean(true);
        AtomicInteger downstreamLedgerWrites = new AtomicInteger();
        List<IdempotentInbox.Outcome<String>> consumerOutcomes = new ArrayList<>();

        // First relay delivers the event to the consumer, then crashes before marking published.
        assertThatThrownBy(() -> outbox.relayPending(10, event -> {
            var outcome = consumerInbox.executeOnce(
                "ledger-consumer",
                event.eventId(),
                event.payload(),
                () -> "ledger-entry-" + downstreamLedgerWrites.incrementAndGet()
            );
            consumerOutcomes.add(outcome);
            if (failAckAfterBrokerDelivery.getAndSet(false)) {
                throw new RuntimeException("ack lost after broker accepted message");
            }
        })).isInstanceOf(RuntimeException.class);

        assertThat(outbox.snapshotOutbox().get(0).isPublished()).isFalse();

        // Second relay re-sends the same event; IdempotentInbox replays without double-writing.
        int relayed = outbox.relayPending(10, event -> {
            var outcome = consumerInbox.executeOnce(
                "ledger-consumer",
                event.eventId(),
                event.payload(),
                () -> "ledger-entry-" + downstreamLedgerWrites.incrementAndGet()
            );
            consumerOutcomes.add(outcome);
        });

        assertThat(relayed).isEqualTo(1);
        assertThat(outbox.snapshotOutbox().get(0).isPublished()).isTrue();
        assertThat(downstreamLedgerWrites.get()).isEqualTo(1);
        assertThat(consumerOutcomes).containsExactly(
            new IdempotentInbox.Outcome<>("ledger-entry-1", false),
            new IdempotentInbox.Outcome<>("ledger-entry-1", true)
        );
    }

    @Test
    void rejectsIdempotencyKeyReuseWhenPayloadChangesWithinSameScope() {
        IdempotentInbox<String> inbox = new IdempotentInbox<>();

        var first = inbox.executeOnce("merchant-1", "idem-99", "{\"amount\":1000}", () -> "pay_1");
        assertThat(first).isEqualTo(new IdempotentInbox.Outcome<>("pay_1", false));

        // Same key in a different merchant scope is isolated.
        var otherMerchant = inbox.executeOnce("merchant-2", "idem-99", "{\"amount\":5000}", () -> "pay_2");
        assertThat(otherMerchant).isEqualTo(new IdempotentInbox.Outcome<>("pay_2", false));

        // Reusing the same key in the same scope with a mutated amount fails fast.
        assertThatThrownBy(() -> inbox.executeOnce(
            "merchant-1",
            "idem-99",
            "{\"amount\":9999}",
            () -> "pay_3"
        )).isInstanceOf(IdempotentInbox.IdempotencyConflictException.class);
    }
}
