package com.example.bmikalkulacka;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BmiCalculatorTest {

    @Test
    public void calculate_usesHeightInMeters() {
        // 70 / (1.75 * 1.75) = 22.857...
        assertEquals(22.857, BmiCalculator.calculate(175, 70), 0.001);
        // 100 / (2 * 2) = 25
        assertEquals(25.0, BmiCalculator.calculate(200, 100), 1e-9);
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculate_rejectsZeroHeight() {
        BmiCalculator.calculate(0, 70);
    }

    @Test
    public void categoryIndex_boundaries() {
        assertEquals(0, BmiCalculator.categoryIndex(18.49));
        assertEquals(1, BmiCalculator.categoryIndex(18.5));
        assertEquals(1, BmiCalculator.categoryIndex(24.99));
        assertEquals(2, BmiCalculator.categoryIndex(25.0));
        assertEquals(3, BmiCalculator.categoryIndex(30.0));
        assertEquals(4, BmiCalculator.categoryIndex(35.0));
        assertEquals(5, BmiCalculator.categoryIndex(40.0));
        assertEquals(5, BmiCalculator.categoryIndex(55.0));
    }
}
