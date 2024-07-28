package com.sascom.stockpricebackend.kis.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.stereotype.Component;

@ConfigurationProperties("kis.green")
public class KisGreenProperties extends KisUserProperties{

    @ConstructorBinding
    KisGreenProperties(String appkey, String appsecret, String personalsecKey, String custType, String contentType, String htsId) {
        super(appkey, appsecret, personalsecKey, custType, contentType, htsId);
    }
}
