package com.xmdevs.crypto.web.rest;


import com.xmdevs.crypto.service.CryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/cryptos")
@RequiredArgsConstructor
public class CryptoController {
    private final CryptoService cryptoService;

    @GetMapping("/stats")
    public Map<String,List<Map<String, Object>>> getCryptoStatsByDateRange(@RequestParam String start, @RequestParam String end) {
        return cryptoService.getCryptoStatsByDateRange(start, end);
    }

    @GetMapping("/{crypto}/stats")
    public Map<String, Object> getCryptoStats(@PathVariable String crypto) {
        return cryptoService.getStatsByCrypto(crypto);
    }


    @GetMapping("stats/highest-normalized")
    public Map<String, Object> getHighestNormalizedRangeByDay(@RequestParam  String date) {
        return cryptoService.getHighestNormalizedRangeByDay(date);
    }


    //additional, not shown on requirements
    @GetMapping("/supported")
    public Set<String> getSupportedCryptos() {
        return cryptoService.getSupportedCryptos();
    }

    //additional, not shown on requirements
    @GetMapping("/{crypto}/stats/timeframe")
    public Map<String, Object> getCryptoStatsByTimeframe(@PathVariable String crypto, @RequestParam int months) {
        return cryptoService.getStatsByCryptoAndTimeframe(crypto, months);
    }

}
