package com.iv127.kotlin.starter.concurrency_in_practice;

import org.junit.jupiter.api.RepeatedTest;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SynchronizedSequenceTest {

    @RepeatedTest(200)
    void testGetNext() throws Exception {
        int countOfThreads = 400;
        CyclicBarrier barrier = new CyclicBarrier(countOfThreads, () -> {
            System.out.println("All threads reached the barrier. Proceeding...");
        });
        SynchronizedSequence sequence = new SynchronizedSequence();
        List<Callable<Void>> tasks = IntStream.range(0, countOfThreads)
                .mapToObj(i -> new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        barrier.await();
                        if (ThreadLocalRandom.current().nextInt(2) == 0) {
                            Thread.sleep(ThreadLocalRandom.current().nextInt(2));
                            System.out.println(sequence.getNext() + 90);
                        } else {
                            Thread.sleep(ThreadLocalRandom.current().nextInt(2));
                            System.out.println(sequence.getNext() + 1);
                        }
                        return null;
                    }
                })
                .collect(Collectors.toUnmodifiableList());

        ExecutorService executor = Executors.newFixedThreadPool(countOfThreads);
        executor.invokeAll(tasks);
        executor.close();

        int next = sequence.getNext();
        System.out.println(next);
        assertEquals(countOfThreads, next);
    }
}
