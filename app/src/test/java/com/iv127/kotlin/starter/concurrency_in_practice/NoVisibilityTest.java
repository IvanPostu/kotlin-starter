package com.iv127.kotlin.starter.concurrency_in_practice;

import org.junit.jupiter.api.RepeatedTest;

public class NoVisibilityTest {
    static boolean flag = false; // Not volatile

    @org.junit.jupiter.api.Disabled
    @RepeatedTest(1)
    public void testNoVisibility() throws Exception {
        Thread threadA = new Thread(() -> {
            System.out.println("Thread A: waiting for flag to become true...");
            while (!flag) {
                // Busy-waiting
            }
            System.out.println("Thread A: detected flag is true!");
        });

        Thread threadB = new Thread(() -> {
            try {
                Thread.sleep(110); // Ensure Thread A starts waiting
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            flag = true;
            System.out.println("Thread B: set flag to true");
        });

        threadA.start();
        threadB.start();

        threadA.join();
        threadB.join();
    }

    private static void delay(long delayMs) {
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
