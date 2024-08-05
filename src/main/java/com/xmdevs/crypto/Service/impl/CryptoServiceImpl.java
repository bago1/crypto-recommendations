package com.xmdevs.crypto.Service.impl;

import com.xmdevs.crypto.Service.CryptoService;
import com.xmdevs.crypto.data.CyrptoData;
import com.xmdevs.crypto.model.Crypto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CryptoServiceImpl implements CryptoService {

    private final CyrptoData data;

    @Override
    public List<Map<String, Object>> getAllCryptoStats() {
        Map<String, List<Crypto>> cryptoData = data.getCryptoData();
        List<Map<String, Object>> statsList = new ArrayList<>();

        for (String crypto : cryptoData.keySet()) {
            List<Crypto> data = cryptoData.get(crypto);
            double min = findMinimum(data);
            double max = findMaximum(data);
            double newest = findNewest(data);
            double oldest = findOldest(data);
            double normalizedRange = (max - min) / min;

            Map<String, Object> stats = generateMap(crypto, min, max, newest, oldest, normalizedRange);
            statsList.add(stats);
        }

        statsList.sort((a, b) -> Double.compare((double) b.get("normalizedRange"), (double) a.get("normalizedRange")));

        return statsList;
    }

    private static Map<String, Object> generateMap(String crypto, double min, double max, double newest, double oldest, double normalizedRange) {
        Map<String, Object> stats = generateMap(crypto, min, max, newest, oldest);
        stats.put("normalizedRange", normalizedRange);
        return stats;
    }

    private static Double findNewest(List<Crypto> data) {
        return data.get(data.size() - 1).getPrice();
    }

    @Override
    public Map<String, Object> getStatsByCrypto(String crypto) {
        Map<String, List<Crypto>> cryptoData = data.getCryptoData();
        if (!isCryptoSupported(crypto)) {
            throw new NoSuchElementException("Crypto not supported");
        }
        List<Crypto> data = cryptoData.get(crypto);

        double min = findMinimum(data);
        double max = findMaximum(data);
        double newest = findNewest(data);
        double oldest = findOldest(data);

        Map<String, Object> stats = generateMap(crypto, min, max, newest, oldest);

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

    private static Double findOldest(List<Crypto> data) {
        return data.get(0).getPrice();
    }

    private boolean isCryptoSupported(String crypto) {

        return data.getCryptoData().containsKey(crypto);
    }

    public Map<String, Object> getHighestNormalizedRangeByDay(Long timestamp) {
        Map<String, List<Crypto>> cryptoData = data.getCryptoData();
        String highestCrypto = null;
        double highestNormalizedRange = Double.MIN_VALUE;

        for (String crypto : cryptoData.keySet()) {
            List<Crypto> data = cryptoData.get(crypto);
            Optional<Crypto> cryptoOnDay = data.stream().filter(c -> c.getTimestamp().equals(timestamp)).findFirst();

            if (cryptoOnDay.isPresent()) {
                double min = data.stream().min(Comparator.comparingDouble(Crypto::getPrice)).get().getPrice();
                double max = data.stream().max(Comparator.comparingDouble(Crypto::getPrice)).get().getPrice();
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
    public Set<String> getSupportedCryptos() {
        return data.getCryptoData().keySet();
    }

    private static double findMaximum(List<Crypto> data) {
        return data.stream().max(Comparator.comparingDouble(Crypto::getPrice)).get().getPrice();
    }

    private static double findMinimum(List<Crypto> data) {
        return data.stream().min(Comparator.comparingDouble(Crypto::getPrice)).get().getPrice();
    }
    @Override
    public Map<String, Object> getStatsByCryptoAndTimeframe(String crypto, int months) {
        long currentTime = System.currentTimeMillis();
        long startTime = currentTime - (long) months * 30 * 24 * 60 * 60 * 1000;

        List<Crypto> dataList = data.getCryptoData(crypto).stream()
                .filter(c -> c.getTimestamp() >= startTime)
                .collect(Collectors.toList());

        if (dataList.isEmpty()) {
            throw new NoSuchElementException("No data available for the given timeframe");
        }

        double min = findMinimum(dataList);
        double max = findMaximum(dataList);
        double newest = dataList.get(dataList.size() - 1).getPrice();
        double oldest = dataList.get(0).getPrice();

        Map<String, Object> stats = new HashMap<>();
        stats.put("crypto", crypto);
        stats.put("min", min);
        stats.put("max", max);
        stats.put("newest", newest);
        stats.put("oldest", oldest);

        return stats;
    }

}
