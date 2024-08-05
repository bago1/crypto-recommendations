package com.xmdevs.crypto.service.impl;

import com.xmdevs.crypto.exception.Domain;
import com.xmdevs.crypto.exception.NotFoundException;
import com.xmdevs.crypto.model.CryptoStatistics;
import com.xmdevs.crypto.service.CryptoService;
import com.xmdevs.crypto.data.CyrptoData;
import com.xmdevs.crypto.model.Crypto;
import com.xmdevs.crypto.util.CryptoStatsCollector;
import com.xmdevs.crypto.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.xmdevs.crypto.exception.Domain.CRYPTO_NOT_FOUND;
import static com.xmdevs.crypto.util.DateUtils.*;

@Service
@RequiredArgsConstructor
public class CryptoServiceImpl implements CryptoService {

    private final CyrptoData data;

    @Override
    public Map<String, Object> getStatsByCrypto(String crypto) {
        Map<String, List<Crypto>> cryptoData = data.getCryptoData();
        if (!isCryptoSupported(crypto)) {
            throw new NotFoundException(CRYPTO_NOT_FOUND);
        }
        List<Crypto> data = cryptoData.get(crypto);

        CryptoStatistics stats = data.stream().collect(new CryptoStatsCollector());

        return generateMap(crypto, stats.getMin(), stats.getMax(), stats.getNewest(), stats.getOldest());

    }


    public Map<String, Object> getHighestNormalizedRangeByDay(String date) {
        long startOfDay = parseDateStartOfDay(date);
        long endOfDay = parseDateEndOfDay(date);

        Map<String, List<Crypto>> filteredDataByDay = new ConcurrentHashMap<>();
        data.getCryptoData().forEach((key, value) -> {
            List<Crypto> filteredList = value.stream()
                    .filter(c -> c.getTimestamp() >= startOfDay && c.getTimestamp() <= endOfDay)
                    .collect(Collectors.toList());
            if (!filteredList.isEmpty()) {
                filteredDataByDay.put(key, filteredList);
            }
        });

        String highestCrypto = null;
        double highestNormalizedRange = Double.MIN_VALUE;

        for (Map.Entry<String, List<Crypto>> entry : filteredDataByDay.entrySet()) {
            String crypto = entry.getKey();
            List<Crypto> data = entry.getValue();

            double min = data.stream().mapToDouble(Crypto::getPrice).min().orElse(Double.MAX_VALUE);
            double max = data.stream().mapToDouble(Crypto::getPrice).max().orElse(Double.MIN_VALUE);

            if (min != Double.MAX_VALUE && max != Double.MIN_VALUE) {
                double normalizedRange = (max - min) / min;
                if (normalizedRange > highestNormalizedRange) {
                    highestNormalizedRange = normalizedRange;
                    highestCrypto = crypto;
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("crypto", highestCrypto);
        result.put("normalizedRange", highestNormalizedRange);

        return result;
    }

    @Override
    public Map<String, List<Map<String, Object>>> getCryptoStatsByDateRange(String start, String end) {
        long startTime = parseDateStartOfDay(start);
        long endTime = parseDateEndOfDay(end);

        Map<String, List<Crypto>> cryptoData = data.getCryptoData();
        List<Map<String, Object>> statsList = new ArrayList<>();

        for (String crypto : cryptoData.keySet()) {
            List<Crypto> dataList = cryptoData.get(crypto).stream()
                    .filter(c -> c.getTimestamp() >= startTime && c.getTimestamp() < endTime)
                    .collect(Collectors.toList());

            if (dataList.isEmpty()) continue;

            CryptoStatistics stats = dataList.stream().collect(new CryptoStatsCollector());
            double normalizedRange = calculateNormalizedRange(stats.getMax(), stats.getMin());

            Map<String, Object> statsMap = generateMap(crypto, stats.getMin(), stats.getMax(), stats.getNewest(), stats.getOldest(), normalizedRange);
            statsList.add(statsMap);
        }

        statsList.sort((a, b) -> Double.compare((double) b.get("normalizedRange"), (double) a.get("normalizedRange")));
        String intervalKey = start + " " + end;
        return Map.of(intervalKey, statsList);
    }

    private static double calculateNormalizedRange(double max, double min) {
        double result = (max - min) / min;
        return Double.parseDouble(String.format("%.3f", result));
    }


    @Override
    public Map<String, Object> getStatsByCryptoAndTimeframe(String crypto, int months) {
        long currentTime = System.currentTimeMillis();
        long startTime = currentTime - (long) months * 30 * 24 * 60 * 60 * 1000;

        List<Crypto> dataList = data.getCryptoData(crypto).stream().filter(c -> c.getTimestamp() >= startTime).collect(Collectors.toList());

        if (dataList.isEmpty()) {
            throw new NoSuchElementException("No data available for the given timeframe");
        }

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double newest = Double.MIN_VALUE;
        double oldest = Double.MAX_VALUE;

        for (Crypto cryptoData : dataList) {
            double price = cryptoData.getPrice();
            if (price < min) min = price;
            if (price > max) max = price;

            if (cryptoData.getTimestamp() > newest) {
                newest = cryptoData.getPrice();
            }

            if (cryptoData.getTimestamp() < oldest) {
                oldest = cryptoData.getPrice();
            }
        }

        return generateMap(crypto, min, max, newest, oldest);

    }

    @Override
    public Set<String> getSupportedCryptos() {
        return data.getCryptoData().keySet();
    }


    private static Map<String, Object> generateMap(String crypto, double min, double max, double newest, double oldest, double normalizedRange) {
        Map<String, Object> stats = generateMap(crypto, min, max, newest, oldest);
        stats.put("normalizedRange", normalizedRange);
        return stats;
    }

    private static Map<String, Object> generateMap(String crypto, double min, double max, double newest, double oldest) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("crypto", crypto);
        stats.put("min", min);
        stats.put("max", max);
        stats.put("newest", newest);
        stats.put("oldest", oldest);
        return stats;
    }

    private boolean isCryptoSupported(String crypto) {

        return data.getCryptoData().containsKey(crypto);
    }
}

