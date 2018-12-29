/* Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved. */
package com.hugodesmarques.producers;

import com.codahale.metrics.MetricRegistry;
import com.hugodesmarques.ProjectionMetrics;
import com.hugodesmarques.consumers.ClientProjection;
import com.hugodesmarques.consumers.NaiveThreadPool;

/**
 * Creates a ThreadPool of size 10 to consume events in parallel. Will this be enough?
 * Latency is still increasing. Why?
 */
public class ThreadPoolStream {

    public static void main(String[] args) {
        MetricRegistry metricRegistry =
                new MetricRegistry();
        ProjectionMetrics metrics =
                new ProjectionMetrics(metricRegistry);
        ClientProjection clientProjection =
                new ClientProjection(metrics);
        NaiveThreadPool naivePool =
                new NaiveThreadPool(10, clientProjection);
        EventStream es = new EventStreamImpl();
        es.consume(naivePool);
    }

}
