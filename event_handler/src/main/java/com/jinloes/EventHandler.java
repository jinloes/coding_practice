package com.jinloes;

/**
 * Created by jinloes on 9/24/15.
 */
public interface EventHandler<T extends Event> {
    void handleEvent(T event);
}
