package com.xmdevs.crypto.util;

import java.util.HashMap;

public class FilesToRead {
    private static final String PATH = "src/main/resources/prices/";
    public static final String BTC_PATH = PATH + "BTC_values.csv";
    public static final String ETH_PATH = PATH + "ETH_values.csv";
    public static final String LITE_PATH = PATH + "LTC_values.csv";

    public static final HashMap<String, String> CRYPTO_PATHS = new HashMap<>() {{
        put("BTC", BTC_PATH);
        put("ETH", ETH_PATH);
        put("LTC", LITE_PATH);
    }};

}
