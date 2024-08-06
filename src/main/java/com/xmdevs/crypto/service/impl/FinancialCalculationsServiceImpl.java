package com.xmdevs.crypto.service.impl;

import com.xmdevs.crypto.service.FinancialCalculationsService;
import org.springframework.stereotype.Service;

@Service
public class FinancialCalculationsServiceImpl implements FinancialCalculationsService {

    @Override
    public Double calculateNormalizedRange(double max, double min) {
        double result = (max - min) / min;
        return Double.parseDouble(String.format("%.3f", result));
    }
}
