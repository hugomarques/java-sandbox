/* Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved. */
package com.hugodesmarques.consumers;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.hugodesmarques.Event;
import com.hugodesmarques.ProjectionMetrics;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * This implementation creates a fixed-size pool of threads.
 * Each thread responsible for a well-known subset of clientIds.
 * This way two different clientIds may end up on the same thread but the same clientId will always be handled by
 * the same thread. If two events for the same clientId appear, they will both be routed to the same thread, thus
 * avoiding concurrent processing.
 * No locking is necessary, because each ClientId will always use the same ExecutorService. Thus, each ExecutorService
 * will be single threaded and execute each tasks sequential preventing parallel execution for the same ClientId.
 */
public class SmartPool implements EventConsumer, AutoCloseable {
    private final List<LinkedBlockingQueue<Runnable>> queues;
    private final List<ExecutorService> threadPools;
    private final EventConsumer downstream;

    public SmartPool(int size,
                     EventConsumer downstream,
                     MetricRegistry metricRegistry) {
        this.downstream = downstream;
        this.queues = IntStream.range(0, size)
                               .mapToObj(i -> new LinkedBlockingQueue<Runnable>())
                               .collect(toList());
        List<ExecutorService> list = queues.stream()
                                           .map(queue -> new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, queue))
                                           .collect(toList());
        this.threadPools = new CopyOnWriteArrayList<>(list);
        metricRegistry.register(MetricRegistry.name(SmartPool.class, "queue"), (Gauge<Double>) this::averageQueueLength);
    }

    private double averageQueueLength() {
        double totalLength =
                queues
                        .stream()
                        .mapToDouble(LinkedBlockingQueue::size)
                        .sum();
        return totalLength / queues.size();
    }

    @Override
    public void close() {
        threadPools.forEach(ExecutorService::shutdown);
    }

    @Override
    public Event consume(Event event) {
        final int threadIdx = event.getClientId() % threadPools.size();
        final ExecutorService executor = threadPools.get(threadIdx);
        executor.submit(() -> downstream.consume(event));
        return event;
    }
}
