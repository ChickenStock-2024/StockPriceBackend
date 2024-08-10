package com.sascom.stockpricebackend.application.kis.properties;

import lombok.Getter;

@Getter
public enum StockName {
    SAMSUNG("samsung");

    private final String name;

    StockName(String name) {
        this.name = name;
    }
}
