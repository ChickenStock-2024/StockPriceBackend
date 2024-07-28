package com.sascom.stockpricebackend.kis.properties;

import lombok.Getter;

@Getter
public enum StockName {
    SAMSUNG("samsung");

    private final String name;

    StockName(String name) {
        this.name = name;
    }
}
