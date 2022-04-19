package org.keliu.common.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoneyTest {
    @Test
    public void moneyMultiplyTest(){
        Money m1 = new Money(2);

        Money m2 = m1.multiply(3);

        assertEquals(6, m2.asLong());
    }
}
