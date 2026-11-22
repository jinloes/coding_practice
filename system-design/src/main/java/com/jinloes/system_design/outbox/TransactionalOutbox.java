package com.jinloes.system_design.outbox;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * In-memory model of the transactional outbox + relay pattern.
 *
 * <p>Domain state and outbox records commit atomically inside {@link #executeAtomically}; a separate
 * {@link #relayPending} pass publishes pending records with at-least-once semantics, marking rows
 * published only after broker acknowledgment.
 *
 * <p>See {@code docs/patterns/outbox-and-cdc.md}.
 */
public final class TransactionalOutbox {
    public record OutboxEvent(
        String eventId,
        String aggregateId,
        String eventType,
        String payload,
        Instant createdAt,
        Instant publishedAt
    ) {
        public boolean isPublished() {
            return publishedAt != null;
        }
    }

    public record PendingEvent(String aggregateId, String eventType, String payload) {
        public PendingEvent {
            Objects.requireNonNull(aggregateId, "aggregateId must not be null");
            Objects.requireNonNull(eventType, "eventType must not be null");
            Objects.requireNonNull(payload, "payload must not be null");
        }
    }

    private final Clock clock;
    private final Map<String, String> domainStore = new LinkedHashMap<>();
    private final List<OutboxEvent> outboxTable = new ArrayList<>();

    public TransactionalOutbox(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    /**
     * Commits domain writes and outbox appends as a single atomic unit; if {@code unitOfWork} throws,
     * neither the domain store nor the outbox table is mutated.
     */
    public synchronized void executeAtomically(Consumer<UnitOfWork> unitOfWork) {
        Objects.requireNonNull(unitOfWork, "unitOfWork must not be null");
        Map<String, String> stagedDomain = new LinkedHashMap<>(domainStore);
        List<PendingEvent> stagedEvents = new ArrayList<>();

        unitOfWork.accept(new UnitOfWork() {
            @Override
            public void putState(String key, String value) {
                stagedDomain.put(
                    Objects.requireNonNull(key, "key must not be null"),
                    Objects.requireNonNull(value, "value must not be null")
                );
            }

            @Override
            public void recordOutbox(PendingEvent event) {
                stagedEvents.add(Objects.requireNonNull(event, "event must not be null"));
            }
        });

        Instant now = clock.instant();
        domainStore.clear();
        domainStore.putAll(stagedDomain);
        for (PendingEvent pending : stagedEvents) {
            outboxTable.add(new OutboxEvent(
                UUID.randomUUID().toString(),
                pending.aggregateId(),
                pending.eventType(),
                pending.payload(),
                now,
                null
            ));
        }
    }

    /**
     * Publishes up to {@code batchSize} unpublished outbox records through {@code brokerPublisher},
     * marking each record published only after {@code brokerPublisher} returns normally.
     */
    public synchronized int relayPending(int batchSize, Consumer<OutboxEvent> brokerPublisher) {
        if (batchSize <= 0) {
            throw new IllegalArgumentException("batchSize must be positive");
        }
        Objects.requireNonNull(brokerPublisher, "brokerPublisher must not be null");
        int publishedCount = 0;
        for (int i = 0; i < outboxTable.size() && publishedCount < batchSize; i++) {
            OutboxEvent current = outboxTable.get(i);
            if (current.isPublished()) {
                continue;
            }
            brokerPublisher.accept(current);
            outboxTable.set(i, new OutboxEvent(
                current.eventId(),
                current.aggregateId(),
                current.eventType(),
                current.payload(),
                current.createdAt(),
                clock.instant()
            ));
            publishedCount++;
        }
        return publishedCount;
    }

    public synchronized String readState(String key) {
        return domainStore.get(key);
    }

    public synchronized List<OutboxEvent> snapshotOutbox() {
        return List.copyOf(outboxTable);
    }

    public interface UnitOfWork {
        void putState(String key, String value);

        void recordOutbox(PendingEvent event);
    }
}
