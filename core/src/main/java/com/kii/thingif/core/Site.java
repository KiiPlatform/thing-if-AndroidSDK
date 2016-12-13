package com.kii.thingif.core;

public enum Site {
    US("api.kii.com"),
    JP("api-jp.kii.com"),
    CN3("api-cn3.kii.com"),
    SG("api-sg.kii.com"),
    EU("api-eu.kii.com");

    private final String hostName;
    private final String baseUrl;
    private Site(String hostName) {
        this.hostName = hostName;
        this.baseUrl = "https://" + hostName;
    }

    public String getHostName() {
        return this.hostName;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public static Site getSite(String hostName) {
        for (Site s : Site.values()) {
            if (hostName.equals(s.hostName)) {
                return s;
            }
        }
        return null;
    }
}
