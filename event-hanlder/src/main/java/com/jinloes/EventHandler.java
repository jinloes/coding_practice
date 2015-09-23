package com.jinloes;

import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by jinloes on 9/23/15.
 */
public class EventHandler {
    private static final Map<Integer, Event> EVENT_MAP =
            ImmutableMap.of(0, new Event1(), 1, new Event2(), 2, new Event3());

    public static void main(String[] args) throws InterruptedException {
        EventBus eventBus = new AsyncEventBus(Executors.newFixedThreadPool(25));
        eventBus.register(new Event1Handler());
        eventBus.register(new Event2Handler());
        eventBus.register(new Event3Handler());
        Random random = new Random();
        while (true) {
            int nextInt = random.nextInt(3);
            eventBus.post(EVENT_MAP.get(nextInt));
            Thread.sleep(TimeUnit.MILLISECONDS.toMillis(500));
        }
    }

    public static class Event1Handler {
        @Subscribe
        public void handleEvent(Event1 event1) throws InterruptedException {
            System.out.println("Event 1");
            Thread.sleep(1000);
        }
    }

    public static class Event2Handler {
        @Subscribe
        public void handleEvent(Event2 event2) {
            System.out.println("Event 2");
        }
    }

    public static class Event3Handler {
        @Subscribe
        public void handleEvent(Event3 event3) {
            System.out.println("Event 3");
        }
    }

    public interface Event {

    }

    public static class Event1 implements Event {

    }

    public static class Event2 implements Event {

    }

    public static class Event3 implements Event {

    }
}
