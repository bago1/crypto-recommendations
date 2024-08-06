package com.xmdevs.crypto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Crypto {
     private Long timestamp;
    private String symbol;
    private Double price;
 }
