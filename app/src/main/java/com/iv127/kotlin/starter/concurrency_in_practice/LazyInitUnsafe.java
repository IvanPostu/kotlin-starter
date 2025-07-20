package com.iv127.kotlin.starter.concurrency_in_practice;

import java.util.function.Supplier;

public final class LazyInitUnsafe<T> {

    private final Supplier<T> creator;
    private T cachedObject = null;

    public LazyInitUnsafe(Supplier<T> creator) {
        this.creator = creator;
    }

    public T getInstance() {
        if (cachedObject == null) {
            cachedObject = creator.get();
        }
        return cachedObject;
    }
}
