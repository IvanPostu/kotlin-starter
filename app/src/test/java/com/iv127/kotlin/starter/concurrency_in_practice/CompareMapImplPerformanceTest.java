package com.iv127.kotlin.starter.concurrency_in_practice;

import com.google.gson.Gson;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CompareMapImplPerformanceTest {
    private static final Logger LOG = LoggerFactory.getLogger(CompareMapImplPerformanceTest.class);

    //    Comparing scalability of Map implementations
    @TestFactory
//    @Disabled
    public Stream<DynamicTest> testCompareMapImplPerformance() throws Exception {
        DataArgument[] inputData = new DataArgument[]{
                new DataArgument(Collections.synchronizedMap(new HashMap<>()), "synchronized HashMap", 100, 1_000L, 2),
                new DataArgument(Collections.synchronizedMap(new TreeMap<>()), "synchronized TreeMap", 100, 1_000L, 2),
                new DataArgument(new ConcurrentHashMap<>(), "ConcurrentHashMap", 100, 1_000L, 2),
                new DataArgument(new ConcurrentSkipListMap<>(), "ConcurrentSkipListMap", 100, 1_000L, 2),

                new DataArgument(Collections.synchronizedMap(new HashMap<>()), "synchronized HashMap", 100, 1_000L, 4),
                new DataArgument(Collections.synchronizedMap(new TreeMap<>()), "synchronized TreeMap", 100, 1_000L, 4),
                new DataArgument(new ConcurrentHashMap<>(), "ConcurrentHashMap", 100, 1_000L, 4),
                new DataArgument(new ConcurrentSkipListMap<>(), "ConcurrentSkipListMap", 100, 1_000L, 4),

                new DataArgument(Collections.synchronizedMap(new HashMap<>()), "synchronized HashMap", 100, 1_000L, 8),
                new DataArgument(Collections.synchronizedMap(new TreeMap<>()), "synchronized TreeMap", 100, 1_000L, 8),
                new DataArgument(new ConcurrentHashMap<>(), "ConcurrentHashMap", 100, 1_000L, 8),
                new DataArgument(new ConcurrentSkipListMap<>(), "ConcurrentSkipListMap", 100, 1_000L, 8),

                new DataArgument(Collections.synchronizedMap(new HashMap<>()), "synchronized HashMap", 100, 1_000L, 12),
                new DataArgument(Collections.synchronizedMap(new TreeMap<>()), "synchronized TreeMap", 100, 1_000L, 12),
                new DataArgument(new ConcurrentHashMap<>(), "ConcurrentHashMap", 100, 1_000L, 12),
                new DataArgument(new ConcurrentSkipListMap<>(), "ConcurrentSkipListMap", 100, 1_000L, 12),

                new DataArgument(Collections.synchronizedMap(new HashMap<>()), "synchronized HashMap", 100, 1_000L, 16),
                new DataArgument(Collections.synchronizedMap(new TreeMap<>()), "synchronized TreeMap", 100, 1_000L, 16),
                new DataArgument(new ConcurrentHashMap<>(), "ConcurrentHashMap", 100, 1_000L, 16),
                new DataArgument(new ConcurrentSkipListMap<>(), "ConcurrentSkipListMap", 100, 1_000L, 16),

                new DataArgument(Collections.synchronizedMap(new HashMap<>()), "synchronized HashMap", 100, 1_000L, 20),
                new DataArgument(Collections.synchronizedMap(new TreeMap<>()), "synchronized TreeMap", 100, 1_000L, 20),
                new DataArgument(new ConcurrentHashMap<>(), "ConcurrentHashMap", 100, 1_000L, 20),
                new DataArgument(new ConcurrentSkipListMap<>(), "ConcurrentSkipListMap", 100, 1_000L, 20),

                new DataArgument(Collections.synchronizedMap(new HashMap<>()), "synchronized HashMap", 100, 1_000L, 24),
                new DataArgument(Collections.synchronizedMap(new TreeMap<>()), "synchronized TreeMap", 100, 1_000L, 24),
                new DataArgument(new ConcurrentHashMap<>(), "ConcurrentHashMap", 100, 1_000L, 24),
                new DataArgument(new ConcurrentSkipListMap<>(), "ConcurrentSkipListMap", 100, 1_000L, 24),

                new DataArgument(Collections.synchronizedMap(new HashMap<>()), "synchronized HashMap", 100, 1_000L, 28),
                new DataArgument(Collections.synchronizedMap(new TreeMap<>()), "synchronized TreeMap", 100, 1_000L, 28),
                new DataArgument(new ConcurrentHashMap<>(), "ConcurrentHashMap", 100, 1_000L, 28),
                new DataArgument(new ConcurrentSkipListMap<>(), "ConcurrentSkipListMap", 100, 1_000L, 28),

                new DataArgument(Collections.synchronizedMap(new HashMap<>()), "synchronized HashMap", 100, 1_000L, 32),
                new DataArgument(Collections.synchronizedMap(new TreeMap<>()), "synchronized TreeMap", 100, 1_000L, 32),
                new DataArgument(new ConcurrentHashMap<>(), "ConcurrentHashMap", 100, 1_000L, 32),
                new DataArgument(new ConcurrentSkipListMap<>(), "ConcurrentSkipListMap", 100, 1_000L, 32),
        };


        return Stream.of(inputData).map(dataArgument -> {
                    populateMap(80, dataArgument.map());
                    return DynamicTest.dynamicTest(dataArgument.name(), () -> {
                        Map<String, Object> result = testThroughputForMapAndGetResult(dataArgument.map(),
                                dataArgument.selectionRange(), dataArgument.timeFrameInMs(), dataArgument.numberOfThreads());
                        result.put("name", dataArgument.name());
                        System.out.println(new Gson().toJson(result));
                    });
                }
        );
    }

    private static void populateMap(int numberOfEntries, Map<Integer, Integer> map) {
        for (int i = 0; i < numberOfEntries; i++) {
            Integer keyAndValue = Integer.valueOf(i);
            map.put(keyAndValue, keyAndValue);
        }
    }

    private static Map<String, Object> testThroughputForMapAndGetResult(Map<Integer, Integer> map,
                                                                        int selectionRange, long timeFrameInMs, int numberOfThreads) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        ((ThreadPoolExecutor) executorService).prestartAllCoreThreads();
        CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads);

        List<Callable<BigDecimal>> callables = IntStream.range(0, numberOfThreads)
                .mapToObj(i -> (Callable<BigDecimal>) () -> execute(() -> {
                    countDownLatch.countDown();
                    countDownLatch.await();
                    Random random = new Random();

                    long untilAsNano = System.nanoTime() + timeFrameInMs * 1_000_000;
                    BigDecimal count = BigDecimal.ZERO;

                    while (System.nanoTime() < untilAsNano) {
                        int randomIndex = random.nextInt(selectionRange);
                        int num = map.getOrDefault(randomIndex, 0);
                        noOp(num);

                        count = count.add(BigDecimal.ONE);
                    }

                    return count;
                })).toList();

        List<Future<BigDecimal>> futures = executorService.invokeAll(callables);

        BigDecimal readCount = BigDecimal.ZERO;
        for (Future<BigDecimal> future : futures) {
            readCount = readCount.add(future.get());
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("readCount", readCount);
        result.put("timeFrameInMs", timeFrameInMs);
        result.put("numberOfThreads", numberOfThreads);

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        executorService.close();

        return result;
    }

    private static void noOp(int i) {
        if (i > 1000) {
            throw new IllegalStateException();
        }
    }

    private static <T> T execute(ThrowingSupplier<T> runnable) {
        try {
            return runnable.get();
        } catch (InterruptedException e) {
            if (!Thread.currentThread().isInterrupted()) {
                Thread.currentThread().interrupt();
            }
            throw new IllegalStateException(e);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private interface ThrowingSupplier<T> {
        T get() throws Exception;
    }


    private record DataArgument(Map<Integer, Integer> map, String name, int selectionRange, long timeFrameInMs,
                                int numberOfThreads) {
    }


}
