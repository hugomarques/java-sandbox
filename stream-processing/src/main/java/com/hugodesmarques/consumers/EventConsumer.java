package com.hugodesmarques.consumers;/* Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved. */

import com.hugodesmarques.Event;

/**
 * @since 12/28/18.
 */
@FunctionalInterface
public interface EventConsumer {
    Event consume(Event event);
}
