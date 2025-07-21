package com.iv127.kotlin.starter.concurrency_in_practice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ThreadLocalHolderTest {

    @Test
    public void testThreadLocalHolder() throws Exception {

        ThreadLocal<ExpensiveObject> expensiveObjectThreadLocalHolder = new ThreadLocal<>() {
            @Override
            protected ExpensiveObject initialValue() {
                return new ExpensiveObject();
            }
        };

        assertEquals(0, ExpensiveObject.COUNT_OF_OBJECTS);
        ExpensiveObject ref1 = expensiveObjectThreadLocalHolder.get();
        assertEquals(1, ExpensiveObject.COUNT_OF_OBJECTS);
        assertNotNull(ref1);
        ExpensiveObject ref2 = expensiveObjectThreadLocalHolder.get();
        assertNotNull(ref2);
        ExpensiveObject ref3 = expensiveObjectThreadLocalHolder.get();
        assertNotNull(ref3);

        assertEquals(ref1, ref2);
        assertEquals(ref2, ref3);
        assertEquals(ref3, ref1);

        assertEquals(1, ExpensiveObject.COUNT_OF_OBJECTS);
    }

    private static final class ExpensiveObject {

        private static int COUNT_OF_OBJECTS = 0;

        private ExpensiveObject() {
            COUNT_OF_OBJECTS++;
        }

    }

}
