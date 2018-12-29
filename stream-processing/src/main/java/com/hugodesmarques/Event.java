package com.hugodesmarques;/* Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved. */

import lombok.Value;

import java.time.Instant;
import java.util.UUID;

/**
 * @since 12/28/18.
 */
@Value
public class Event {
    private Instant created = Instant.now();
    private final int clientId;
    private final UUID uuid;
}

