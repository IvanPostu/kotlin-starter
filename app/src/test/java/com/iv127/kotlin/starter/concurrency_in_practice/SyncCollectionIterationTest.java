package com.iv127.kotlin.starter.concurrency_in_practice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SyncCollectionIterationTest {

    @Test
    public void testRemoveCollectionElementUsingIterator() {
        List<Integer> integers = IntStream.range(0, 10)
                .mapToObj(Integer::valueOf)
                .collect(Collectors.toCollection(() -> new ArrayList<>()));
        List<Integer> syncIntegers = Collections.synchronizedList(integers);

        Iterator<Integer> itr = syncIntegers.iterator();
        while (itr.hasNext()) {
            Integer element = itr.next();
            System.out.println(element);
            if (element == 5) {
                itr.remove();
            }
        }

        Integer[] expectedArray = {0, 1, 2, 3, 4, 6, 7, 8, 9};
        Assertions.assertArrayEquals(expectedArray, integers.toArray());
        Assertions.assertArrayEquals(expectedArray, syncIntegers.toArray());
    }

    @Test
    public void testRemoveCollectionElementUsingCollectionOnIteration() {
        List<Integer> integers = IntStream.range(0, 10)
                .mapToObj(Integer::valueOf)
                .collect(Collectors.toCollection(() -> new ArrayList<>()));
        List<Integer> syncIntegers = Collections.synchronizedList(integers);


        Assertions.assertThrowsExactly(ConcurrentModificationException.class,
                () -> {
                    Iterator<Integer> itr = syncIntegers.iterator();
                    while (itr.hasNext()) {
                        Integer element = itr.next();
                        System.out.println(element);
                        if (element == 5) {
                            // exception is thrown here, works pretty similar
                            // in case of concurrency
                            integers.remove(element);
                        }
                    }
                });
    }

}
