package com.xmdevs.crypto.service;

import java.util.*;

public interface CryptoService {

    Map<String, Object> getStatsByCrypto(String crypto);

    Map<String, Object> getHighestNormalizedRangeByDay(String timestamp);

    Set<String> getSupportedCryptos();

    Map<String, Object> getStatsByCryptoAndTimeframe(String crypto, int months);

    Map<String,List<Map<String, Object>>> getCryptoStatsByDateRange(String start, String end);
}
