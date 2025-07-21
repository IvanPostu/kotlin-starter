package com.iv127.kotlin.starter.concurrency_in_practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ArrayCopyTest {

    @Test
    public void testArrayCopy() throws Exception {
        int[] arr1 = new int[]{1, 3, 4};
        Short[] arr2 = new Short[]{1, 2, 3};

        int[] arr3 = Arrays.copyOf(arr1, arr1.length);
        Short[] arr4 = arr2.clone();
        arr3[0] = 10;
        arr4[0] = 11;

        int[] arr5 = arr3.clone();
        Short[] arr6 = Arrays.copyOf(arr4, arr4.length);
        arr5[0] = 12;
        arr6[0] = 13;

        assertArrayEquals(new int[]{1, 3, 4}, arr1);
        assertArrayEquals(new Short[]{1, 2, 3}, arr2);

        assertArrayEquals(new int[]{10, 3, 4}, arr3);
        assertArrayEquals(new Short[]{11, 2, 3}, arr4);

        assertArrayEquals(new int[]{12, 3, 4}, arr5);
        assertArrayEquals(new Short[]{13, 2, 3}, arr6);
    }

}
