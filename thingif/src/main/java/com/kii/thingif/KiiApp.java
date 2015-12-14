package com.kii.thingif;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.w3c.dom.Text;

/** Represents Application on Kii Cloud */
public class KiiApp implements Parcelable {
    private String appID;
    private String appKey;
    private String hostName;
    private String baseUrl;
    private String siteName;


    /** Instantiate Kii App with App Location.
     * If you haven't created Kii Cloud App yet,
     * Please visit https://developer.kii.com and create your app.
     * @param appID ID of the app.
     * @param appKey Key of the app.
     * @param site Site of the app.
     * @throws  IllegalArgumentException when appID, appKey and site is null or empty string.
     */
    public KiiApp(String appID, String appKey, Site site) {
        if (TextUtils.isEmpty(appID)) {
            throw new IllegalArgumentException("appID is null or empty.");
        }
        if (TextUtils.isEmpty(appKey)) {
            throw new IllegalArgumentException("appKey is null or empty.");
        }
        if (site == null) {
            throw new IllegalArgumentException("site is null.");
        }
        this.appID = appID;
        this.appKey = appKey;
        this.hostName = site.getHostName();
        this.baseUrl = site.getBaseUrl();
        this.siteName = site.name();
    }

    /** Instantiate Kii App with Host Name.
     * Who host Kii Cloud in private/ dedicated location
     * Will use this constructor to instantiate KiiApp.
     * (Private/ Dedicated location is only available in Enterprise subscription.)
     * If you're using public Kii Cloud, please use {@link KiiApp(String, String, Site)}
     * With this constructor, {@link #getSiteName()} returns fixed string "CUSTOM".
     * @param appID ID of the app.
     * @param appKey Key of the app.
     * @param hostName Host Name of the app.
     * @throws  IllegalArgumentException when appID, appKey and hostName is null or empty string.
     */
    public KiiApp(String appID, String appKey, String hostName) {
        if (TextUtils.isEmpty(appID)) {
            throw new IllegalArgumentException("appID is null or empty.");
        }
        if (TextUtils.isEmpty(appKey)) {
            throw new IllegalArgumentException("appKey is null or empty.");
        }
        if (TextUtils.isEmpty(hostName)) {
            throw new IllegalArgumentException("hostName is null or empty.");
        }
        this.appID = appID;
        this.appKey = appKey;
        this.hostName = hostName;
        this.baseUrl = "https://" + hostName;
        this.siteName = "CUSTOM";
    }

    /** Instantiate Kii App with Host Name and Site Name.
     * Who host Kii Cloud in private/ dedicated location
     * Will use this constructor to instantiate KiiApp.
     * (Private/ Dedicated location is only available in Enterprise subscription.)
     * If you're using public Kii Cloud, please use {@link KiiApp(String, String, Site)}
     * siteName is used by Gateway Agent to resolve the server location stored with the siteName
     * in the Gateway configuration.
     * @param appID ID of the app.
     * @param appKey Key of the app.
     * @param hostName Host name of the app.
     * @param siteName Site name of the app. (Corresponds to Gateway Agent configuration.)
     */
    public KiiApp(String appID, String appKey, String hostName, String siteName) {
        if (TextUtils.isEmpty(appID)) {
            throw new IllegalArgumentException("appID is null or empty.");
        }
        if (TextUtils.isEmpty(appKey)) {
            throw new IllegalArgumentException("appKey is null or empty.");
        }
        if (TextUtils.isEmpty(hostName)) {
            throw new IllegalArgumentException("hostName is null or empty.");
        }
        this.appID = appID;
        this.appKey = appKey;
        this.hostName = hostName;
        this.baseUrl = "https://" + hostName;
        this.siteName = siteName;
    }

    protected KiiApp(Parcel in) {
        appID = in.readString();
        appKey = in.readString();
        hostName = in.readString();
        baseUrl = in.readString();
        siteName = in.readString();
    }

    public static final Creator<KiiApp> CREATOR = new Creator<KiiApp>() {
        @Override
        public KiiApp createFromParcel(Parcel in) {
            return new KiiApp(in);
        }

        @Override
        public KiiApp[] newArray(int size) {
            return new KiiApp[size];
        }
    };

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

    public String getSiteName() {
        return this.siteName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(appID);
        dest.writeString(appKey);
        dest.writeString(hostName);
        dest.writeString(baseUrl);
        dest.writeString(siteName);
    }
}
