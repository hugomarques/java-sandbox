package com.hugodesmarques;/* Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved. */

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @since 12/28/18.
 */
public class Sleeper {

    private static final Random RANDOM = new Random();

    public static void randSleep(double mean, double stdDev) {
        final double micros = 1_000 * (mean + RANDOM.nextGaussian() * stdDev);
        try {
            TimeUnit.MICROSECONDS.sleep((long) micros);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
