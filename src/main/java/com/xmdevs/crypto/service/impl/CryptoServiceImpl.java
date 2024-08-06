package com.xmdevs.crypto.service.impl;

import com.xmdevs.crypto.data.CryptoData;
import com.xmdevs.crypto.exception.NotFoundException;
import com.xmdevs.crypto.model.Crypto;
import com.xmdevs.crypto.model.CryptoStatistics;
import com.xmdevs.crypto.service.CryptoService;
import com.xmdevs.crypto.service.FinancialCalculationsService;
import com.xmdevs.crypto.util.CryptoStatsCollector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.xmdevs.crypto.exception.Domain.CRYPTO_NOT_FOUND;
import static com.xmdevs.crypto.util.DateUtils.parseDateEndOfDay;
import static com.xmdevs.crypto.util.DateUtils.parseDateStartOfDay;

@Service
@RequiredArgsConstructor
public class CryptoServiceImpl implements CryptoService {

    private final CryptoData data;
    private final FinancialCalculationsService financialCalculationsService;

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
            List<Crypto> filteredList = value.stream().filter(c -> c.getTimestamp() >= startOfDay && c.getTimestamp() <= endOfDay).collect(Collectors.toList());
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
            List<Crypto> dataList = cryptoData.get(crypto).stream().filter(c -> c.getTimestamp() >= startTime && c.getTimestamp() < endTime).collect(Collectors.toList());

            if (dataList.isEmpty()) continue;

            CryptoStatistics stats = dataList.stream().collect(new CryptoStatsCollector());
            double normalizedRange = financialCalculationsService.calculateNormalizedRange(stats.getMax(), stats.getMin());

            Map<String, Object> statsMap = generateMap(crypto, stats.getMin(), stats.getMax(), stats.getNewest(), stats.getOldest(), normalizedRange);
            statsList.add(statsMap);
        }

        statsList.sort((a, b) -> Double.compare((double) b.get("normalizedRange"), (double) a.get("normalizedRange")));
        String intervalKey = start + " " + end;
        return Map.of(intervalKey, statsList);
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

