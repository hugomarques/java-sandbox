package com.hugodesmarques.producers;

import com.codahale.metrics.MetricRegistry;
import com.hugodesmarques.consumers.ClientProjection;
import com.hugodesmarques.ProjectionMetrics;
import lombok.extern.slf4j.Slf4j;

/**
 * This one creates a single thread consumer that spends some microseconds to consume a message.
 * Latency will always scale up here due to high input and low throughput.
 */
@Slf4j
public class SingleThreadStream {

    public static void main(String[] args) {
        EventStream es = new EventStreamImpl();
        MetricRegistry metricsRegistry = new MetricRegistry();
        final ProjectionMetrics projectionMetrics = new ProjectionMetrics(metricsRegistry);
        es.consume(new ClientProjection(projectionMetrics));
    }
}
