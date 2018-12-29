/* Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved. */
package com.hugodesmarques.consumers;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hugodesmarques.Event;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @since 12/29/18.
 */
@Slf4j
public class IgnoreDuplicates implements EventConsumer {

    private final EventConsumer downstream;
    private final Meter duplicates;

    private Cache<UUID, UUID> seenUuids = CacheBuilder.newBuilder()
                                                      .expireAfterWrite(10, TimeUnit.SECONDS)
                                                      .build();

    public IgnoreDuplicates(EventConsumer downstream, MetricRegistry metricRegistry) {
        this.downstream = downstream;
        duplicates = metricRegistry.meter(MetricRegistry.name(IgnoreDuplicates.class, "duplicates"));
        metricRegistry.register(MetricRegistry.name(IgnoreDuplicates.class, "cacheSize"), (Gauge<Long>) seenUuids::size);
    }

    @Override
    public Event consume(Event event) {
        final UUID uuid = event.getUuid();
        if (seenUuids.asMap().putIfAbsent(uuid, uuid) == null) {
            return downstream.consume(event);
        } else {
            duplicates.mark();
            return event;
        }
    }
}
