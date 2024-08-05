package com.xmdevs.crypto.data;

import com.xmdevs.crypto.model.Crypto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CyrptoData {
    private Map<String, List<Crypto>> cryptoData = new HashMap<>();

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
