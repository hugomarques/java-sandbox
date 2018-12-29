/* Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved. */
package com.hugodesmarques.producers;

import com.codahale.metrics.MetricRegistry;
import com.hugodesmarques.ProjectionMetrics;
import com.hugodesmarques.consumers.ClientProjection;
import com.hugodesmarques.consumers.NaiveThreadPoolWithMonitoring;

/**
 * Based on what we found at {@link ThreadPoolStream} we can monitor the queue size now.
 * It seems our queue is growing faster than the executors can consume. Thus, we should increase
 * the number of threads from 10 to 20 to drain the queue faster.
 * With that, our results are:
 * 11:21:51.323 [metrics-logger-reporter-1-thread-1] INFO  com.hugodesmarques.ProjectionMetrics - type=HISTOGRAM,
 * name=com.hugodesmarques.ProjectionMetrics.latency, count=10000, min=1, max=580, mean=302.1225304252851,
 * stddev=163.09867271877212, median=300.0, p75=445.0, p95=555.0, p98=568.0, p99=573.0, p999=580.0
 */
public class ThreadPoolWithMonitoringStream {

    public static void main(String[] args) {
        MetricRegistry metricRegistry =
                new MetricRegistry();
        ProjectionMetrics metrics =
                new ProjectionMetrics(metricRegistry);
        ClientProjection clientProjection =
                new ClientProjection(metrics);
        NaiveThreadPoolWithMonitoring naivePool =
                new NaiveThreadPoolWithMonitoring(20, clientProjection, metricRegistry);
        EventStream es = new EventStreamImpl();
        es.consume(naivePool);
    }

}
