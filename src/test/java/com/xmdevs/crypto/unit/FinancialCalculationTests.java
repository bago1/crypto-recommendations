package com.xmdevs.crypto.unit;

import com.xmdevs.crypto.service.impl.FinancialCalculationsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class FinancialCalculationTests {
    @InjectMocks
    private FinancialCalculationsServiceImpl financialCalculationsService;

    @Test
    public void testCalculateNormalizedRange() {
        double max = 47000.00;
        double min = 45000.00;
        double expected = 0.044;

        double result = financialCalculationsService.calculateNormalizedRange(max, min);

        assertEquals(expected, result, 0.001);
    }
}
