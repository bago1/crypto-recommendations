package com.xmdevs.crypto.service.impl;

import com.xmdevs.crypto.exception.Domain;
import com.xmdevs.crypto.exception.NotFoundException;
import com.xmdevs.crypto.model.CryptoStatistics;
import com.xmdevs.crypto.service.CryptoService;
import com.xmdevs.crypto.data.CyrptoData;
import com.xmdevs.crypto.model.Crypto;
import com.xmdevs.crypto.util.CryptoStatsCollector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.xmdevs.crypto.exception.Domain.CRYPTO_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CryptoServiceImpl implements CryptoService {

    private final CyrptoData data;

    @Override
    public List<Map<String, Object>> getAllCryptoStats() {
        Map<String, List<Crypto>> cryptoData = data.getCryptoData();
        List<Map<String, Object>> statsList = new ArrayList<>();

        for (String crypto : cryptoData.keySet()) {
            List<Crypto> dataList = cryptoData.get(crypto);

            if (dataList.isEmpty()) continue;

            CryptoStatistics stats = dataList.stream().collect(new CryptoStatsCollector());
            double normalizedRange = ((stats.getMax() - stats.getMin()) / stats.getMin());

            Map<String, Object> statsMap = generateMap(crypto, stats.getMin(), stats.getMax(), stats.getNewest(), stats.getOldest(), normalizedRange);
            statsList.add(statsMap);
        }

        statsList.sort((a, b) -> Double.compare((double) b.get("normalizedRange"), (double) a.get("normalizedRange")));
        return statsList;
    }


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


    public Map<String, Object> getHighestNormalizedRangeByDay(Long timestamp) {
        Map<String, List<Crypto>> cryptoData = data.getCryptoData();
        String highestCrypto = null;
        double highestNormalizedRange = Double.MIN_VALUE;

        for (Map.Entry<String, List<Crypto>> entry : cryptoData.entrySet()) {
            String crypto = entry.getKey();
            List<Crypto> data = entry.getValue();

            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            boolean found = false;

            for (Crypto cryptoOnDay : data) {
                if (cryptoOnDay.getTimestamp().equals(timestamp)) {
                    found = true;
                    double price = cryptoOnDay.getPrice();
                    if (price < min) min = price;
                    if (price > max) max = price;
                }
            }

            if (found) {
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

