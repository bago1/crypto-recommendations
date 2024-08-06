package com.xmdevs.crypto.unit;


import com.xmdevs.crypto.data.CryptoData;
import com.xmdevs.crypto.exception.Domain;
import com.xmdevs.crypto.exception.NotFoundException;
import com.xmdevs.crypto.model.Crypto;
import com.xmdevs.crypto.service.FinancialCalculationsService;
import com.xmdevs.crypto.service.impl.CryptoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CryptoServiceImplTest {

    @Mock
    private CryptoData data;

    @Mock
    private FinancialCalculationsService financialCalculationsService;

    @InjectMocks
    private CryptoServiceImpl cryptoService;

    private List<Crypto> sampleData;

    @BeforeEach
    public void setUp() {
        sampleData = Arrays.asList(
                new Crypto(1641009600000L, "BTC", 46813.21),
                new Crypto(1641013200000L, "BTC", 47000.00),
                new Crypto(1641016800000L, "BTC", 45000.00)
        );

        Map<String, List<Crypto>> cryptoData = new HashMap<>();
        cryptoData.put("BTC", sampleData);

        when(data.getCryptoData()).thenReturn(cryptoData);
    }

    @Test
    public void testGetStatsByCrypto() {
        Map<String, Object> stats = cryptoService.getStatsByCrypto("BTC");

        assertEquals("BTC", stats.get("crypto"));
        assertEquals(45000.00, stats.get("min"));
        assertEquals(47000.00, stats.get("max"));
        assertEquals(45000.00, stats.get("newest"));
        assertEquals(46813.21, stats.get("oldest"));
    }

    @Test
    public void testGetStatsByCrypto_NotFound() {
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            cryptoService.getStatsByCrypto("ETH");
        });

        assertEquals(Domain.CRYPTO_NOT_FOUND, exception.getDomain());
    }

    @Test
    public void testGetHighestNormalizedRangeByDay() {
        String date = "2022-01-01";

        Map<String, Object> result = cryptoService.getHighestNormalizedRangeByDay(date);

        assertEquals("BTC", result.get("crypto"));
        assertEquals(0.044, (double) result.get("normalizedRange"), 0.001);
    }

    @Test
    public void testGetCryptoStatsByDateRange() {
        String start = "2022-01-01";
        String end = "2022-02-01";

        when(financialCalculationsService.calculateNormalizedRange(47000.00, 45000.00)).thenReturn(0.044);
        Map<String, List<Map<String, Object>>> stats = cryptoService.getCryptoStatsByDateRange(start, end);

        assertTrue(stats.containsKey("2022-01-01 2022-02-01"));
        List<Map<String, Object>> statsList = stats.get("2022-01-01 2022-02-01");

        assertFalse(statsList.isEmpty());
        Map<String, Object> btcStats = statsList.get(0);

        assertEquals("BTC", btcStats.get("crypto"));
        assertEquals(45000.00, btcStats.get("min"));
        assertEquals(47000.00, btcStats.get("max"));
        assertEquals(45000.00, btcStats.get("newest"));
        assertEquals(46813.21, btcStats.get("oldest"));
        assertEquals(0.044, (double) btcStats.get("normalizedRange"), 0.001);
    }

    @Test
    public void testGetSupportedCryptos() {
        Set<String> supportedCryptos = cryptoService.getSupportedCryptos();

        assertEquals(1, supportedCryptos.size());
        assertTrue(supportedCryptos.contains("BTC"));
    }


}