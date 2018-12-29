/* Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved. */
package com.hugodesmarques.consumers;

import com.hugodesmarques.Event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @since 12/29/18.
 */
public class NaiveThreadPool implements EventConsumer, AutoCloseable{

    private final EventConsumer downstream;
    private final ExecutorService executorService;

    public NaiveThreadPool(int size, EventConsumer downstream) {
        this.executorService = Executors.newFixedThreadPool(size);
        this.downstream = downstream;
    }

    @Override
    public Event consume(Event event) {
        executorService.submit(() -> downstream.consume(event));
        return event;
    }

    @Override
    public void close() throws Exception {
        executorService.shutdownNow();
    }
}
