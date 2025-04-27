package com.grizz;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CalculatorTest {

    @Mock
    Calculator calculator;

    @DisplayName("a와 b를 더했을 때, 그 결과는 3이다")
    @Test
    void test1() {
        // given
        Integer a = 1;
        Integer b = 2;

        given(calculator.add(anyInt(), anyInt()))
                .willAnswer(invocation -> {
                    Integer a1 = invocation.getArgument(0);
                    Integer b1 = invocation.getArgument(1);
                    return a1 + b1;
                });

        // when
        Integer result = calculator.add(a, b);

        // then
        int expected = a + b;
        assertEquals(expected, result);
    }

}