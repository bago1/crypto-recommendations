package com.xmdevs.crypto.data;

import com.xmdevs.crypto.model.Crypto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CryptoData {
    private Map<String, List<Crypto>> cryptoData = new ConcurrentHashMap<>();

    public void setCryptoData(String key, List<Crypto> value) {
        cryptoData.put(key, value);
    }

    public List<Crypto> getCryptoData(String key) {
        return cryptoData.get(key);
    }

    public Map<String, List<Crypto>> getCryptoData() {
        return cryptoData;
    }

}