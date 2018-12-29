/* Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved. */
package com.hugodesmarques;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @since 12/29/18.
 */
@Slf4j
public class ProjectionMetrics {

    private final Histogram latencyHist;

    public ProjectionMetrics(MetricRegistry metricRegistry) {
        final Slf4jReporter reporter = Slf4jReporter.forRegistry(metricRegistry)
                                                    .outputTo(log)
                                                    .convertRatesTo(TimeUnit.SECONDS)
                                                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                                                    .build();
        reporter.start(1, TimeUnit.SECONDS);
        latencyHist = metricRegistry.histogram(MetricRegistry.name(ProjectionMetrics.class, "latency"));
    }

    public void latency(final Duration duration) {
        latencyHist.update(duration.toMillis());
    }
}
