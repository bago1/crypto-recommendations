package com.xmdevs.crypto.util;

import com.xmdevs.crypto.model.Crypto;
import com.xmdevs.crypto.model.CryptoStatistics;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class CryptoStatsCollector implements Collector<Crypto, CryptoStatsCollector.Accumulator, CryptoStatistics> {

    static class Accumulator {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double newest;
        double oldest;
        boolean isFirst = true;
    }

    @Override
    public Supplier<Accumulator> supplier() {
        return Accumulator::new;
    }

    @Override
    public BiConsumer<Accumulator, Crypto> accumulator() {
        return (acc, crypto) -> {
            double price = crypto.getPrice();
            if (price < acc.min) acc.min = price;
            if (price > acc.max) acc.max = price;

            if (acc.isFirst) {
                acc.oldest = price;
                acc.isFirst = false;
            }
            acc.newest = price;
        };
    }

    @Override
    public BinaryOperator<Accumulator> combiner() {
        return (acc1, acc2) -> {
            acc1.min = Math.min(acc1.min, acc2.min);
            acc1.max = Math.max(acc1.max, acc2.max);
            acc1.oldest = acc1.oldest;
            acc1.newest = acc2.newest;
            return acc1;
        };
    }

    @Override
    public Function<Accumulator, CryptoStatistics> finisher() {
        return acc -> new CryptoStatistics(acc.min, acc.max, acc.newest, acc.oldest);
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.UNORDERED);
    }
}
