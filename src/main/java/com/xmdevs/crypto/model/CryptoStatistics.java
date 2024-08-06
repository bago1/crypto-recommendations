package com.xmdevs.crypto.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CryptoStatistics {
    private double min;
    private double max;
    private double newest;
    private double oldest;
}
