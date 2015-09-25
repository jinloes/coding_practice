package com.jinloes;

import java.util.Random;

/**
 * Created by jinloes on 9/25/15.
 */
public class EventFactory {
    private static final Random random = new Random();
    public static Event create() {
        int nextInt = random.nextInt(3);
        switch (nextInt) {
            case 0:
                nextInt =random.nextInt(2);
                String tenant = nextInt == 0 ? "AAA" : "Ebay";
                return new Event1(tenant);
            case 1:
                return new Event2();
            case 2:
                return new Event3();
            default:
                return new Event1("");
        }
    }

    public static class Event1 implements Event {
        private final String tenant;

        public Event1(String tenant) {
            this.tenant = tenant;
        }

        public String getTenant() {
            return tenant;
        }
    }

    public static class Event2 implements Event {

    }

    public static class Event3 implements Event {

    }
}