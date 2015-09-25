package com.jinloes;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.eventbus.Subscribe;

/**
 * Created by jinloes on 9/24/15.
 */
public class InstrumentedEvent1Handler implements EventHandler<EventFactory.Event1> {
    private final Timer timer = EventHandlerRunner.METRIC_REGISTRY.timer(MetricRegistry.name(
            InstrumentedEvent1Handler.class));
    private final EventHandlerRunner.Event1Handler handler;

    public InstrumentedEvent1Handler(EventHandlerRunner.Event1Handler handler) {
        this.handler = handler;
    }

    @Override
    @Subscribe
    public void handleEvent(EventFactory.Event1 event) {
        Timer.Context context = timer.time();
        Timer tenantTimer = EventHandlerRunner.METRIC_REGISTRY.timer(MetricRegistry.name(
                InstrumentedEvent1Handler.class, event.getTenant()));
        Timer.Context tenantContext = tenantTimer.time();
        try {
            handler.handleEvent(event);
        } finally {
            context.stop();
            tenantContext.stop();
        }
    }
}
