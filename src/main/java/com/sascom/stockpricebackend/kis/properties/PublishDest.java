package com.sascom.stockpricebackend.kis.properties;

import lombok.Getter;

@Getter
public enum PublishDest {

    REALTIME_HOKA("/stock-hoka"),
    REALTIME_PURCHASE("/stock-purchase");

    private final String dest;

    PublishDest(String dest) {
        this.dest = dest;
    }
}
