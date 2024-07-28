package com.sascom.stockpricebackend.kis.model;

public record ResolvedData<T>(
        String trId,
        T data
) {
}
