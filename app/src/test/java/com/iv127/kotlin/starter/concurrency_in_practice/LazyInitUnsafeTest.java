package com.iv127.kotlin.starter.concurrency_in_practice;

import org.junit.jupiter.api.RepeatedTest;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LazyInitUnsafeTest {

    @org.junit.jupiter.api.Disabled
    @RepeatedTest(200)
    public void testLazyInit() throws Exception {
        int countOfThreads = 400;
        CyclicBarrier barrier = new CyclicBarrier(countOfThreads, () -> {
            System.out.println("All threads reached the barrier. Proceeding...");
        });
        int[] mutableInt = {0};
        LazyInitUnsafe<Integer> initializer = new LazyInitUnsafe<>(
                () -> mutableInt[0]++);
        List<Callable<Void>> tasks = IntStream.range(0, countOfThreads)
                .mapToObj(i -> new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        barrier.await();
                        if (ThreadLocalRandom.current().nextInt(2) == 0) {
                            assertEquals(0, initializer.getInstance() - initializer.getInstance());
                        } else {
                            assertEquals(1, initializer.getInstance() - initializer.getInstance() + 1);
                        }
                        return null;
                    }
                })
                .collect(Collectors.toUnmodifiableList());

        ExecutorService executor = Executors.newFixedThreadPool(countOfThreads);
        executor.invokeAll(tasks);
        executor.shutdownNow();

        int number = initializer.getInstance();
        System.out.println(number);
        assertEquals(1, mutableInt[0]);
    }

}
