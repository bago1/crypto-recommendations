package com.xmdevs.crypto.service;

import java.util.*;

public interface CryptoService {
    List<Map<String, Object>> getAllCryptoStats();

    Map<String, Object> getStatsByCrypto(String crypto);

    Map<String, Object> getHighestNormalizedRangeByDay(Long timestamp);

    Set<String> getSupportedCryptos();

    Map<String, Object> getStatsByCryptoAndTimeframe(String crypto, int months);

}