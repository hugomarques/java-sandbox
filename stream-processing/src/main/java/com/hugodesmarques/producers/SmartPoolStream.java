/* Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved. */
package com.hugodesmarques.producers;

import com.codahale.metrics.MetricRegistry;
import com.hugodesmarques.ProjectionMetrics;
import com.hugodesmarques.consumers.ClientProjection;
import com.hugodesmarques.consumers.FailOnConcurrentModification;
import com.hugodesmarques.consumers.SmartPool;

/**
 * No locking is necessary when using {@link SmartPool}
 */
public class SmartPoolStream {

    public static void main(String[] args) {
        MetricRegistry metricRegistry =
                new MetricRegistry();
        ProjectionMetrics metrics =
                new ProjectionMetrics(metricRegistry);
        ClientProjection clientProjection =
                new ClientProjection(metrics);
        SmartPool pool = new SmartPool(20, clientProjection, metricRegistry);
        EventStream es = new EventStreamImpl();
        es.consume(pool);
    }
}
