package com.iv127.kotlin.starter.concurrency_in_practice;

import org.junit.jupiter.api.RepeatedTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReorderingCaseTest {
    private static final Logger LOG = LoggerFactory.getLogger(ReorderingCaseTest.class);

    @RepeatedTest(5)
    public void testReordering() throws Exception {
        PossibleReordering.run();
        LOG.info("x={}, y={}, a={}, b={}",
                PossibleReordering.x,
                PossibleReordering.y,
                PossibleReordering.a,
                PossibleReordering.b
        );
    }

    private static class PossibleReordering {
        static int x = 0, y = 0;
        static int a = 0, b = 0;

        public static void run()
                throws InterruptedException {
            Thread one = new Thread(new Runnable() {
                public void run() {
                    a = 1;
                    x = b;
                }
            });
            Thread other = new Thread(new Runnable() {
                public void run() {
                    b = 1;
                    y = a;
                }
            });
            one.start();
            other.start();
            one.join();
            other.join();
        }
    }
}
