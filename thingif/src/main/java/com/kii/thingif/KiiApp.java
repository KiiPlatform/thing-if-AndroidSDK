package com.kii.thingif;

/** Represents Application on Kii Cloud */
public class KiiApp {
    private String appID;
    private String appKey;
    private String hostName;
    private String baseUrl;


    /** Instantiate Kii App with App Location.
     * If you haven't created Kii Cloud App yet,
     * Please visit https://developer.kii.com and create your app.
     * @param appID ID of the app.
     * @param appKey Key of the app.
     * @param site Site of the app.
     */
    public KiiApp(String appID, String appKey, Site site) {
        this.appID = appID;
        this.appKey = appKey;
        this.hostName = site.getHostName();
        this.baseUrl = "https://" + this.hostName;
    }

    /** Instantiate Kii App with Host Name.
     * Who host Kii Cloud in private/ dedicated location
     * Will use this constructor to instantiate KiiApp.
     * (Private/ Dedicated location is only available in Enterprise subscription.)
     * If you're using public Kii Cloud, please use {@link KiiApp(String, String, Site)}
     * @param appID ID of the app.
     * @param appKey Key of the app.
     * @param hostName Host Name of the app.
     */
    public KiiApp(String appID, String appKey, String hostName) {
        this.appID = appID;
        this.appKey = appKey;
        this.hostName = hostName;
        this.baseUrl = "https://" + hostName;
    }

    public String getAppID() {
        return this.appID;
    }

    public String getAppKey() {
        return  this.appKey;
    }

    public String getHostName() {
        return this.hostName;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

}
