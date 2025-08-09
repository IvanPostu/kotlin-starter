package com.iv127.kotlin.starter.concurrency_in_practice;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
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

    // remove synchronized methods from BoundedBuffer to really test
    @Test
    @Disabled
    public void testPutTakeConcurrently() throws Exception {
        int tpt = 100000; // trials per thread
        for (int cap = 1; cap <= 1000; cap *= 10) {
            System.out.println("Capacity: " + cap);
            for (int pairs = 1; pairs <= 128; pairs *= 2) {
                PutTakeTest t = new PutTakeTest(cap, pairs, tpt);
                System.out.print("Pairs: " + pairs + "\t");
                t.test();
                System.out.print("\t");
                Thread.sleep(1000);
                t.test();
                System.out.println();
                Thread.sleep(1000);
            }
        }
        PutTakeTest.pool.shutdown();
    }

    @Test
    @Disabled
    public void testPoolExpansion() throws InterruptedException {
        int MAX_SIZE = 10;
        TestingThreadFactory threadFactory = new TestingThreadFactory();
        ExecutorService exec = Executors.newFixedThreadPool(MAX_SIZE, threadFactory);
        for (int i = 0; i < 10 * MAX_SIZE; i++)
            exec.execute(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(Long.MAX_VALUE);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        for (int i = 0; i < 20 && threadFactory.numCreated.get() < MAX_SIZE; i++)
            Thread.sleep(100);
        assertEquals(threadFactory.numCreated.get(), MAX_SIZE);
        exec.shutdownNow();
    }

    private static int xorShift(int y) {
        y ^= (y << 6);
        y ^= (y >>> 21);
        y ^= (y << 7);
        return y;
    }

    private static class PutTakeTest {
        private static final ExecutorService pool = Executors.newCachedThreadPool(new TestingThreadFactory());
        private final AtomicInteger putSum = new AtomicInteger(0);
        private final AtomicInteger takeSum = new AtomicInteger(0);
        private final BarrierTimer barrierTimer;
        private final CyclicBarrier barrier;
        private final BoundedBuffer<Integer> bb;
        private final int nTrials, nPairs;


        PutTakeTest(int capacity, int npairs, int ntrials) {
            this.bb = new BoundedBuffer<Integer>(capacity);
            this.nTrials = ntrials;
            this.nPairs = npairs;
            this.barrierTimer = new BarrierTimer();
            this.barrier = new CyclicBarrier(npairs * 2 + 1, barrierTimer);
        }

        public void test() {
            try {
                barrierTimer.clear();
                for (int i = 0; i < nPairs; i++) {
                    pool.execute(new Producer());
                    pool.execute(new Consumer());
                }
                barrier.await();
                barrier.await();
                long nsPerItem = barrierTimer.getTime() / (nPairs * (long) nTrials);
                System.out.print("Throughput: " + nsPerItem + " ns/item");
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

    private static class TestingThreadFactory implements ThreadFactory {
        public final AtomicInteger numCreated = new AtomicInteger();
        private final ThreadFactory factory
                = Executors.defaultThreadFactory();

        public Thread newThread(Runnable r) {
            numCreated.incrementAndGet();
            return factory.newThread(r);
        }
    }

    private static class BarrierTimer implements Runnable {
        private boolean started;
        private long startTime, endTime;

        public synchronized void run() {
            long t = System.nanoTime();
            if (!started) {
                started = true;
                startTime = t;
            } else
                endTime = t;
        }

        public synchronized void clear() {
            started = false;
        }

        public synchronized long getTime() {
            return endTime - startTime;
        }
    }

}
