package com.wwwjf.wanandroidkt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IntegerTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
        Integer a  =  100;
        Integer b = 100;
        assertTrue(a==b);
        Integer a1 = 200;
        Integer b1 = 200;
        assertFalse(a1==b1);//超过127不等
        Integer a2 = 200;
        int b2 = 200;
        assertTrue(a2==b2);//自动拆箱成int
    }
}
