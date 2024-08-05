package com.xmdevs.crypto.web.rest;


import com.xmdevs.crypto.Service.CryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/cryptos")
@RequiredArgsConstructor
public class CryptoController {
    private final CryptoService cryptoService;

    @GetMapping("/stats")
    public List<Map<String, Object>> getCryptoStats() {
        return cryptoService.getAllCryptoStats();
    }

    @GetMapping("/{crypto}/stats")
    public Map<String, Object> getCryptoStats(@PathVariable String crypto) {
        return cryptoService.getStatsByCrypto(crypto);
    }


    @GetMapping("stats/highest-normalized/{timestamp}")
    public Map<String, Object> getHighestNormalizedRangeByDay(@PathVariable Long timestamp) {
        return cryptoService.getHighestNormalizedRangeByDay(timestamp);
    }


    @GetMapping("/supported")
    public Set<String> getSupportedCryptos() {
        return cryptoService.getSupportedCryptos();
    }

}
