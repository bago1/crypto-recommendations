package com.xmdevs.crypto.Service.impl;

import com.xmdevs.crypto.Service.CryptoService;
import com.xmdevs.crypto.data.CyrptoData;
import com.xmdevs.crypto.model.Crypto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CryptoServiceImpl implements CryptoService {

    private final CyrptoData data;

    @Override
    public List<Map<String, Object>> getAllCryptoStats  () {
        Map<String, List<Crypto>> cryptoData = data.getCryptoData();
        List<Map<String, Object>> statsList = new ArrayList<>();

        for (String crypto : cryptoData.keySet()) {
            List<Crypto> data = cryptoData.get(crypto);
            double min = findMimimum(data);
            double max = findMaximum(data);
            double newest = data.get(data.size() - 1).getPrice();
            double oldest = data.get(0).getPrice();
            double normalizedRange = (max - min) / min;

            Map<String, Object> stats = new HashMap<>();
            stats.put("crypto", crypto);
            stats.put("min", min);
            stats.put("max", max);
            stats.put("newest", newest);
            stats.put("oldest", oldest);
            stats.put("normalizedRange", normalizedRange);
            statsList.add(stats);
        }

        statsList.sort((a, b) -> Double.compare((double) b.get("normalizedRange"), (double) a.get("normalizedRange")));

        return statsList;
    }

    @Override
    public Map<String, Object> getStatsByCrypto(String crypto) {
        Map<String, List<Crypto>> cryptoData = data.getCryptoData();
        List<Crypto> data = cryptoData.get(crypto);

        if (data == null) {
            throw new NoSuchElementException("Crypto not found");
        }

        double min = data.stream().min(Comparator.comparingDouble(Crypto::getPrice)).get().getPrice();
        double max = data.stream().max(Comparator.comparingDouble(Crypto::getPrice)).get().getPrice();
        double newest = data.get(data.size() - 1).getPrice();
        double oldest = data.get(0).getPrice();

        Map<String, Object> stats = new HashMap<>();
        stats.put("crypto", crypto);
        stats.put("min", min);
        stats.put("max", max);
        stats.put("newest", newest);
        stats.put("oldest", oldest);

        return stats;
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

    private static double findMaximum(List<Crypto> data) {
        return data.stream().max(Comparator.comparingDouble(Crypto::getPrice)).get().getPrice();
    }

    private static double findMimimum(List<Crypto> data) {
        return data.stream().min(Comparator.comparingDouble(Crypto::getPrice)).get().getPrice();
    }
}
