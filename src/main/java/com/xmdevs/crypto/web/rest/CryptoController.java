package com.xmdevs.crypto.web.rest;


import com.xmdevs.crypto.service.CryptoService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/cryptos")
@RequiredArgsConstructor
public class CryptoController {
    private final CryptoService cryptoService;

    @Operation(summary = "Get crypto stats by date range", description = "Returns crypto statistics within a specified date range")
    @GetMapping("/stats")
    public Map<String, List<Map<String, Object>>> getCryptoStatsByDateRange(
            @Parameter(description = "Start date in the format yyyy-MM-dd", example = "2022-01-01")
            @RequestParam String start,
            @Parameter(description = "End date in the format yyyy-MM-dd", example = "2022-02-01")
            @RequestParam String end) {
        return cryptoService.getCryptoStatsByDateRange(start, end);
    }

    @Operation(summary = "Get crypto stats", description = "Returns statistics for a specified crypto")
    @GetMapping("/{crypto}/stats")
    public Map<String, Object> getCryptoStats(
            @Parameter(description = "Crypto symbol", example = "ETH")
            @PathVariable String crypto) {
        return cryptoService.getStatsByCrypto(crypto);
    }

    @Operation(summary = "Get highest normalized range by day", description = "Returns the crypto with the highest normalized range for a specific day")
    @GetMapping("stats/highest-normalized")
    public Map<String, Object> getHighestNormalizedRangeByDay(
            @Parameter(description = "Date in the format yyyy-MM-dd", example = "2022-01-01")
            @RequestParam String date) {
        return cryptoService.getHighestNormalizedRangeByDay(date);
    }

    @Operation(summary = "Get supported cryptos", description = "Returns a set of all supported cryptos")
    @GetMapping("/supported")
    public Set<String> getSupportedCryptos() {
        return cryptoService.getSupportedCryptos();
    }

}
