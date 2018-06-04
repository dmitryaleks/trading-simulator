package com.rest;
import java.util.concurrent.atomic.AtomicInteger;

public class Counter {

    private final AtomicInteger counter = new AtomicInteger();

    public int getNext() {
        return counter.incrementAndGet();
    }
}
