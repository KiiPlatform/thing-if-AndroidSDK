package com.kii.iotcloud;

public enum Site {
    US("https://api.kii.com"),
    JP("https://api-jp.kii.com"),
    CN("https://api-cn2.kii.com"),
    SG("https://api-sg.kii.com");

    private final String baseUrl;
    private Site(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    public String getBaseUrl() {
        return this.baseUrl;
    }
}
