package com.xmdevs.crypto.model;

import java.util.DoubleSummaryStatistics;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class CryptoStatistics {
    private double min;
    private double max;
    private double newest;
    private double oldest;

    public CryptoStatistics(double min, double max, double newest, double oldest) {
        this.min = min;
        this.max = max;
        this.newest = newest;
        this.oldest = oldest;
    }

    // Getters and toString method
    public double getMin() { return min; }
    public double getMax() { return max; }
    public double getNewest() { return newest; }
    public double getOldest() { return oldest; }

    @Override
    public String toString() {
        return "CryptoStatistics{" +
                "min=" + min +
                "max=" + max +
                "newest=" + newest +
                "oldest=" + oldest +
                '}';
    }
}
