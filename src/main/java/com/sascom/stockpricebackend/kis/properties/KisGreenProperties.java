package com.sascom.stockpricebackend.kis.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties("kis.green")
public class KisGreenProperties extends KisUserProperties{

    @ConstructorBinding
    private KisGreenProperties(String appkey, String appsecret, String personalsecKey, String custType, String contentType, String htsId) {
        super(appkey, appsecret, personalsecKey, custType, contentType, htsId);
    }
}
