package com.xmdevs.crypto.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CryptoService {

    Map<String, Object> getStatsByCrypto(String crypto);

    Map<String, Object> getHighestNormalizedRangeByDay(String timestamp);

    Set<String> getSupportedCryptos();

    Map<String, List<Map<String, Object>>> getCryptoStatsByDateRange(String start, String end);

    Double calculateNormalizedRange(double max, double min);
}
