package com.hugodesmarques.consumers;/* Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved. */

import com.hugodesmarques.Event;
import com.hugodesmarques.ProjectionMetrics;
import com.hugodesmarques.Sleeper;
import com.hugodesmarques.consumers.EventConsumer;

import java.time.Duration;
import java.time.Instant;

/**
 * @since 12/28/18.
 */
public class ClientProjection implements EventConsumer {

    private final ProjectionMetrics metrics;

    public ClientProjection(ProjectionMetrics metrics) {
        this.metrics = metrics;
    }

    @Override
    public Event consume(Event event) {
        metrics.latency(Duration.between(event.getCreated(), Instant.now()));
        Sleeper.randSleep(10, 1);
        return event;
    }
}
