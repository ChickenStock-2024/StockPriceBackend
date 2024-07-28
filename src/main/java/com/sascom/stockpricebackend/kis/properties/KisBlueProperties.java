package com.sascom.stockpricebackend.kis.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties("kis.blue")
public class KisBlueProperties extends KisUserProperties{

    @ConstructorBinding
    KisBlueProperties(String appkey, String appsecret, String personalsecKey, String custType, String contentType, String htsId) {
        super(appkey, appsecret, personalsecKey, custType, contentType, htsId);
    }
}
