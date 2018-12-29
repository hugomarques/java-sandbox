/* Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved. */
package com.hugodesmarques.producers;

import com.codahale.metrics.MetricRegistry;
import com.hugodesmarques.ProjectionMetrics;
import com.hugodesmarques.consumers.ClientProjection;
import com.hugodesmarques.consumers.FailOnConcurrentModification;
import com.hugodesmarques.consumers.NaiveThreadPoolWithMonitoring;
import com.hugodesmarques.consumers.WaitOnConcurrentModification;

/**
 * Improvement on top of {@link NaiveThreadPoolWithMonitoring}.
 * We use {@link FailOnConcurrentModification} to prevent
 * multiple concurrent modifications by the same ClientId.
 */
public class ThreadPoolWithMonitoringClientLocksAndWaitOnLocks {

    public static void main(String[] args) {
        MetricRegistry metricRegistry = new MetricRegistry();
        ClientProjection clientProjection =
                new ClientProjection(new ProjectionMetrics(metricRegistry));
        WaitOnConcurrentModification waitOnConcurrentModification =
                new WaitOnConcurrentModification(clientProjection, metricRegistry);
        NaiveThreadPoolWithMonitoring naivePool =
                new NaiveThreadPoolWithMonitoring(10, waitOnConcurrentModification, metricRegistry);
        EventStream es = new EventStreamImpl();
        es.consume(naivePool);
    }
}
