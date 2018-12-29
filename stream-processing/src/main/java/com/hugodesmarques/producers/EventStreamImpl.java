/* Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved. */
package com.hugodesmarques.producers;

import com.hugodesmarques.Event;
import com.hugodesmarques.consumers.EventConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @since 12/29/18.
 */
@Slf4j
public class EventStreamImpl implements EventStream {

    public static List<Event> getEvents() {
        return new Random()
                       .ints()
                       .parallel()
                       .boxed()
                       .map(i -> new Event(RandomUtils.nextInt(1000, 1100), UUID.randomUUID()))
                       .limit(10_000)
                       .collect(Collectors.toList());
    }

    public static void consumeEvents(Consumer<Event> consumer) {
        for (int i = 0; i < 10; i++) {
            log.info("Process " + (i + 1) + "th thousand events");
            getEvents().subList(i * 1000, (i + 1) * 1000)
                  .stream()
                  .sequential()
                  .forEach(consumer::accept);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {

            }
        }
    }

    @Override
    public void consume(EventConsumer consumer) {
        consumeEvents(consumer::consume);
    }
}
