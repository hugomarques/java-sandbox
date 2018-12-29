/* Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved. */
package com.hugodesmarques.consumers;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.hugodesmarques.Event;
import com.hugodesmarques.ProjectionMetrics;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Same implementation as {@link NaiveThreadPool} but we explicitly pass {@link LinkedBlockingQueue}
 * to {@link ExecutorService} constructor. With this nice little trick we can monitor the queue size
 * using {@link Gauge} from DropWizardMetrics.
 */
public class NaiveThreadPoolWithMonitoring implements EventConsumer, AutoCloseable {

    private final EventConsumer downstream;
    private final ExecutorService executorService;

    public NaiveThreadPoolWithMonitoring(int size, EventConsumer downstream, MetricRegistry metricRegistry) {
        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        String name = MetricRegistry.name(ProjectionMetrics.class, "queue");
        Gauge<Integer> gauge = queue::size;
        metricRegistry.register(name, gauge);
        this.executorService =
                new ThreadPoolExecutor(
                        size, size, 0L, TimeUnit.MILLISECONDS, queue);
        this.downstream = downstream;
    }

    @Override
    public Event consume(Event event) {
        executorService.submit(() -> downstream.consume(event));
        return event;
    }

    @Override
    public void close() throws IOException {
        executorService.shutdown();
    }

}
