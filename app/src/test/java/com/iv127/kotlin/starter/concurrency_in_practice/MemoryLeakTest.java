package com.iv127.kotlin.starter.concurrency_in_practice;

import java.util.Scanner;

public class MemoryLeakTest {

    private static final int CAPACITY = 1024;

    // comment items[i] = null; in BoundedBuffer to emulate memory leak
    public static void main(String[] args) throws Exception {
        BoundedBuffer<BigObject> bb = new BoundedBuffer<BigObject>(CAPACITY);
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < CAPACITY; i++)
            bb.put(new BigObject());
        for (int i = 0; i < CAPACITY; i++)
            bb.take();
        scanner.nextLine();
    }

    private static class BigObject {
        double[] data = new double[100000];
    }


}
