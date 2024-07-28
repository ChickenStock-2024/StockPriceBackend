package com.sascom.stockpricebackend.kis.properties;

import lombok.Getter;

@Getter
public enum TrName {
    HOKA("realtime-hoka"),
    PURCHASE("realtime-purchase");

    private final String value;

    TrName(String value) {
        this.value = value;
    }
}
