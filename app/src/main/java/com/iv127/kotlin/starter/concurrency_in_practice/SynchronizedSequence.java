package com.iv127.kotlin.starter.concurrency_in_practice;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class SynchronizedSequence {

    @GuardedBy("this")
    private int nextValue = 0;

    public synchronized int getNext() {
        return nextValue++;
    }

}
