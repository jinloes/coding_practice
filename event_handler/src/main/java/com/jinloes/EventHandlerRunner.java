package com.jinloes;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.JvmAttributeGaugeSet;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.ClassLoadingGaugeSet;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.codahale.metrics.log4j.InstrumentedAppender;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by jinloes on 9/23/15.
 */
public class EventHandlerRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final MetricRegistry METRIC_REGISTRY = new MetricRegistry();

    public static void main(String[] args) throws InterruptedException {
        InstrumentedAppender appender = new InstrumentedAppender(METRIC_REGISTRY);
        appender.activateOptions();
        LogManager.getRootLogger().addAppender(appender);
        METRIC_REGISTRY.registerAll(new MemoryUsageGaugeSet());
        METRIC_REGISTRY.registerAll(new ClassLoadingGaugeSet());
        METRIC_REGISTRY.registerAll(new JvmAttributeGaugeSet());
        METRIC_REGISTRY.registerAll(new GarbageCollectorMetricSet());
        METRIC_REGISTRY.registerAll(new ThreadStatesGaugeSet());
        ConsoleReporter reporter = ConsoleReporter.forRegistry(METRIC_REGISTRY)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(10, TimeUnit.SECONDS);
        EventBus eventBus = new AsyncEventBus(Executors.newFixedThreadPool(25));
        eventBus.register(new InstrumentedEvent1Handler(new Event1Handler()));
        eventBus.register(new InstrumentedEvent2Handler(new Event2Handler()));
        eventBus.register(new Event3Handler());

        while (true) {
            eventBus.post(EventFactory.create());
            Thread.sleep(TimeUnit.MILLISECONDS.toMillis(500));
        }
    }

    public static class Event1Handler implements EventHandler<EventFactory.Event1> {
        private Random random = new Random();

        @Override
        @Subscribe
        public void handleEvent(EventFactory.Event1 event1) {
            System.out.println("Event 1");
            try {
                LOGGER.info("Sleeping");
                Thread.sleep((random.nextInt(5) + 1) * 100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Event2Handler implements EventHandler<EventFactory.Event2> {
        private int count = 0;

        @Override
        @Subscribe
        public void handleEvent(EventFactory.Event2 event) {
            count++;
            if (count % 2 == 0) {
                LOGGER.error("Some error happened throwing exception");
                throw new RuntimeException();
            }
            System.out.println("Event 2");
        }
    }

    public static class Event3Handler implements EventHandler<EventFactory.Event3> {
        private final Meter meter = METRIC_REGISTRY.meter(MetricRegistry.name(Event3Handler.class));

        @Override
        @Subscribe
        public void handleEvent(EventFactory.Event3 event) {
            meter.mark();
            LOGGER.debug("Doing event 3");
            System.out.println("Event 3");
        }
    }
}
