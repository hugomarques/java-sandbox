/* Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved. */
package com.hugodesmarques.producers;

import com.codahale.metrics.MetricRegistry;
import com.hugodesmarques.ProjectionMetrics;
import com.hugodesmarques.consumers.ClientProjection;
import com.hugodesmarques.consumers.IgnoreDuplicates;
import com.hugodesmarques.consumers.SmartPool;

/**
 * Finally we have all the pieces to build our solution. The idea is to compose pipeline from EventConsumer instances
 * wrapping each other:
 * 1. First we apply IgnoreDuplicates to reject duplicates
 * 2. Then we call SmartPool that always pins given clientId to the same thread and executes next stage in that thread
 * 3. Finally ClientProjection is invoked that does the real business logic.
 */
public class FinalSmartPool {

    public static void main(String[] args) {
        MetricRegistry metricRegistry =
                new MetricRegistry();
        ProjectionMetrics metrics =
                new ProjectionMetrics(metricRegistry);
        ClientProjection clientProjection =
                new ClientProjection(metrics);
        SmartPool pool = new SmartPool(20, clientProjection, metricRegistry);
        IgnoreDuplicates ignoreDuplicates = new IgnoreDuplicates(pool, metricRegistry);
        EventStream es = new EventStreamImpl();
        es.consume(ignoreDuplicates);
    }

}
