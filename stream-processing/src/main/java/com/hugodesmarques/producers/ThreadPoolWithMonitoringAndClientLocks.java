/* Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved. */
package com.hugodesmarques.producers;

import com.codahale.metrics.MetricRegistry;
import com.hugodesmarques.ProjectionMetrics;
import com.hugodesmarques.consumers.ClientProjection;
import com.hugodesmarques.consumers.FailOnConcurrentModification;
import com.hugodesmarques.consumers.NaiveThreadPoolWithMonitoring;

/**
 * Improvement on top of {@link com.hugodesmarques.consumers.NaiveThreadPoolWithMonitoring}.
 * We use {@link com.hugodesmarques.consumers.FailOnConcurrentModification} to prevent
 * multiple concurrent modifications by the same ClientId.
 */
public class ThreadPoolWithMonitoringAndClientLocks {

    public static void main(String[] args) {
        MetricRegistry metricRegistry = new MetricRegistry();
        ClientProjection clientProjection =
                new ClientProjection(new ProjectionMetrics(metricRegistry));
        FailOnConcurrentModification failOnConcurrentModification =
                new FailOnConcurrentModification(clientProjection);
        NaiveThreadPoolWithMonitoring naivePool =
                new NaiveThreadPoolWithMonitoring(10, failOnConcurrentModification, metricRegistry);
        EventStream es = new EventStreamImpl();
        es.consume(naivePool);
    }
}
