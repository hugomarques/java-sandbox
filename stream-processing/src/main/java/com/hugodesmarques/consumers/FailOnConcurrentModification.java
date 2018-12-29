/* Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved. */
package com.hugodesmarques.consumers;

import com.hugodesmarques.Event;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * If two events come very quickly one after another, both related to the same clientId,
 * NaivePool will pick both of them and start processing them concurrently.
 * First we'll at least discover such situation by having a Lock for each clientId:
 */
@Slf4j
public class FailOnConcurrentModification implements EventConsumer {

    private final ConcurrentMap<Integer, Lock> clientLocks = new ConcurrentHashMap<>();
    private final EventConsumer downstream;

    public FailOnConcurrentModification(EventConsumer downstream) {
        this.downstream = downstream;
    }

    @Override
    public Event consume(Event event) {
        Lock lock = findClientLock(event);
        if (lock.tryLock()) {
            try {
                downstream.consume(event);
            } finally {
                lock.unlock();
            }
        } else {
            log.error("Client {} already being modified by another thread", event.getClientId());
        }
        return event;
    }

    private Lock findClientLock(Event event) {
        return clientLocks.computeIfAbsent(
                event.getClientId(),
                clientId -> new ReentrantLock());
    }
}
