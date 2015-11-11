package com.jinloes;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by jinloes on 11/11/15.
 */
public class Node {
    private Lock lock = new ReentrantLock();

    public void propagateUpdate(String update, Node backup) {
        boolean acquired = false;
        boolean done = false;
        // while the update hasn't been propagated
        while (!done) {
            try {
                // try to get lock
                acquired = lock.tryLock(TimeUnit.SECONDS.toSeconds(1), TimeUnit.SECONDS);
                // if get lock continue
                if (acquired) {
                    System.out.println("Update received " + update);
                    done = tryConfirmUpdate(backup, update);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();

            } finally {
                // unlock no matter the result
                if (acquired) {
                    lock.unlock();
                }
            }
            // if not done release the lock and wait to try later
            if (!done) {
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(1));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean tryConfirmUpdate(Node other, String update) {
        boolean acquired = false;
        try {
            // try to get lock
            acquired = lock.tryLock(TimeUnit.SECONDS.toSeconds(1), TimeUnit.SECONDS);
            if (acquired) {
                System.out.println("Received confirm of update " + update + " from " + other);
                return true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // if any error occur unlock
            if (acquired) {
                lock.unlock();
            }
        }
        return false;
    }
}
