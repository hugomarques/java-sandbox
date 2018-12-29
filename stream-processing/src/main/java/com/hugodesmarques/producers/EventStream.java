package com.hugodesmarques.producers;/* Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved. */

import com.hugodesmarques.consumers.EventConsumer;

/**
 * @since 12/28/18.
 */
public interface EventStream {
    void consume(EventConsumer consumer);
}
