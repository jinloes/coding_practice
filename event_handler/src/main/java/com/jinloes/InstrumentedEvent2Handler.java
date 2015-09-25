package com.jinloes;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.RatioGauge;
import com.google.common.eventbus.Subscribe;

/**
 * Created by jinloes on 9/24/15.
 */
public class InstrumentedEvent2Handler implements EventHandler<EventFactory.Event2> {
    private final Counter totalCount = EventHandlerRunner.METRIC_REGISTRY.counter(
            MetricRegistry.name(InstrumentedEvent2Handler.class, "total_count"));
    private final Counter successCount = EventHandlerRunner.METRIC_REGISTRY.counter(MetricRegistry.name(
            InstrumentedEvent2Handler.class, "success_count"));
    private final Counter failedCount = EventHandlerRunner.METRIC_REGISTRY.counter(MetricRegistry.name(
            InstrumentedEvent2Handler.class, "failed_count"));
    private final EventHandlerRunner.Event2Handler event2Handler;

    public InstrumentedEvent2Handler(EventHandlerRunner.Event2Handler event2Handler) {
        this.event2Handler = event2Handler;
        EventHandlerRunner.METRIC_REGISTRY.register(MetricRegistry.name(InstrumentedEvent2Handler.class,
                        "success rate "),
                new RatioGauge() {
                    @Override
                    protected Ratio getRatio() {
                        return Ratio.of(successCount.getCount(), totalCount.getCount());
                    }
                });
    }

    @Subscribe
    public void handleEvent(EventFactory.Event2 event) {
        totalCount.inc();
        try {
            event2Handler.handleEvent(event);
            successCount.inc();
        } catch (Exception e) {
            failedCount.inc();
        }
    }

}