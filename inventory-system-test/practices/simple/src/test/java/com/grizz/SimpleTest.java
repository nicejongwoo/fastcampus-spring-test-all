package com.grizz;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleTest {

    @Test
    void addOneAndTwoThenCheckItIsThree() {
        // given
        Integer a = 1;
        Integer b = 2;

        // when
        Integer c = a + b;

        // then
        Integer expected = 3;
        assertEquals(expected, c);
    }

}
