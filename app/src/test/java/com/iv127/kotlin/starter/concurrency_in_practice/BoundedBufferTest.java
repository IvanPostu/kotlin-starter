package com.iv127.kotlin.starter.concurrency_in_practice;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class BoundedBufferTest {

    private static final Duration LOCKUP_DETECT_TIMEOUT = Duration.ofMillis(200L);

    @Test
    public void testIsEmptyWhenConstructed() {
        BoundedBuffer<Integer> bb = new BoundedBuffer<>(10);
        assertTrue(bb.isEmpty());
        assertFalse(bb.isFull());
    }

    @Test
    public void testIsFullAfterPuts() throws InterruptedException {
        BoundedBuffer<Integer> bb = new BoundedBuffer<>(10);
        for (int i = 0; i < 10; i++)
            bb.put(i);
        assertTrue(bb.isFull());
        assertFalse(bb.isEmpty());
    }

    @Test
    public void testTakeBlocksWhenEmpty() {
        final BoundedBuffer<Integer> bb = new BoundedBuffer<Integer>(10);
        Thread taker = new Thread() {
            public void run() {
                try {
                    int unused = bb.take();
                    fail();
                } catch (InterruptedException success) {
                    // ignore
                }
            }
        };
        try {
            taker.start();
            Thread.sleep(LOCKUP_DETECT_TIMEOUT);
            taker.interrupt();
            taker.join(LOCKUP_DETECT_TIMEOUT);
            assertFalse(taker.isAlive());
        } catch (Exception unexpected) {
            fail();
        }
    }

    @Test
    @Disabled
    public void testPutTakeConcurrently() {
        PutTakeTest putTakeTest = new PutTakeTest(10, 10, 100000);
        putTakeTest.test();
        putTakeTest.pool.shutdown();
    }

    private static int xorShift(int y) {
        y ^= (y << 6);
        y ^= (y >>> 21);
        y ^= (y << 7);
        return y;
    }

    private static class PutTakeTest {
        private final ExecutorService pool
                = Executors.newCachedThreadPool();
        private final AtomicInteger putSum = new AtomicInteger(0);
        private final AtomicInteger takeSum = new AtomicInteger(0);
        private final CyclicBarrier barrier;
        private final BoundedBuffer<Integer> bb;
        private final int nTrials, nPairs;


        PutTakeTest(int capacity, int npairs, int ntrials) {
            this.bb = new BoundedBuffer<Integer>(capacity);
            this.nTrials = ntrials;
            this.nPairs = npairs;
            this.barrier = new CyclicBarrier(npairs * 2 + 1);
        }

        void test() {
            try {
                for (int i = 0; i < nPairs; i++) {
                    pool.execute(new Producer());
                    pool.execute(new Consumer());
                }
                barrier.await(); // wait for all threads to be ready
                barrier.await(); // wait for all threads to finish
                assertEquals(putSum.get(), takeSum.get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        class Producer implements Runnable {
            public void run() {
                try {
                    int seed = (this.hashCode() ^ (int) System.nanoTime());
                    int sum = 0;
                    barrier.await();
                    for (int i = nTrials; i > 0; --i) {
                        bb.put(seed);
                        sum += seed;
                        seed = xorShift(seed);
                    }
                    putSum.getAndAdd(sum);
                    barrier.await();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        class Consumer implements Runnable {
            public void run() {
                try {
                    barrier.await();
                    int sum = 0;
                    for (int i = nTrials; i > 0; --i) {
                        sum += bb.take();
                    }
                    takeSum.getAndAdd(sum);
                    barrier.await();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
