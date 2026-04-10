package com.jinloes.concurrency;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Node {

    private final Lock lock = new ReentrantLock();

    public void propagateUpdate(String update, Node backup) {
        boolean done = false;
        while (!done) {
            boolean acquired = false;
            try {
                acquired = lock.tryLock(1, TimeUnit.SECONDS);
                if (acquired) {
                    log.info("Update received {}", update);
                    done = tryConfirmUpdate(backup, update);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                if (acquired) {
                    lock.unlock();
                }
            }
            if (!done) {
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(1));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private boolean tryConfirmUpdate(Node other, String update) {
        boolean acquired = false;
        try {
            acquired = lock.tryLock(1, TimeUnit.SECONDS);
            if (acquired) {
                log.info("Received confirm of update {} from {}", update, other);
                return true;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (acquired) {
                lock.unlock();
            }
        }
        return false;
    }
}