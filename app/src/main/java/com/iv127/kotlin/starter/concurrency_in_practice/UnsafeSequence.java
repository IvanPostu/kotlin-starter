package com.iv127.kotlin.starter.concurrency_in_practice;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class UnsafeSequence {

    private int nextValue = 0;

    public int getNext() {
        return nextValue++;
    }

}
