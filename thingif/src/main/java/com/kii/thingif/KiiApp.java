package com.kii.thingif;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

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
    KiiApp(String appID, String appKey, String hostName) {
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

    public Site getSite() {
        if ("CUSTOM".equals(this.siteName)) {
            return null;
        }
        return Site.valueOf(this.siteName);
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


    /** KiiApp Builder.
     * Provides fine grained control on creating KiiApp instance.<br>
     * For Private/ Dedicated Kii Cloud instance users.<br><br>
     *
     * Public Kii Cloud user who uses apps created on https://developer.kii.com does not need to
     * interact with this Builder. Just use {@link KiiApp(String, String, Site)} is fine.
     */
    public static class Builder {
        final private String hostName;
        final private String appID;
        final private String appKey;
        private String siteName;
        private int port;
        private String schema;

        Builder(String appID, String appKey, String hostName) {
            this.appID = appID;
            this.appKey = appKey;
            this.hostName = hostName;
            this.port = -1;
            this.schema = "https";
        }

        /** Build with host name
         * @param appID ID of the app.
         * @param appKey Key of the app.
         * @param hostName Hostname where the private/ dedicated Kii Cloud is hosted.
         * @return builder instance.
         * @throws IllegalArgumentException when appID, appKey or hostName is null or empty.
         */
        @NonNull
        public static Builder builderWithHostName(
                @NonNull String appID,
                @NonNull String appKey,
                @NonNull String hostName) {
            if (TextUtils.isEmpty(appID)) {
                throw new IllegalArgumentException("appID is null or empty.");
            }
            if (TextUtils.isEmpty(appKey)) {
                throw new IllegalArgumentException("appKey is null or empty.");
            }
            if (TextUtils.isEmpty(hostName)) {
                throw new IllegalArgumentException("hostName is null or empty.");
            }
            Builder b = new Builder(appID, appKey, hostName);
            b.siteName = "CUSTOM";
            return b;
        }

        /** Set port number
         * Optional. By default no port is specified.
         * @param port port number. 0 or less than 0 would be ignored.
         * @return builder instance.
         */
        @NonNull
        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        /** Set schema
         * Optional. By default https is used.
         * @param urlSchema url schema.
         * @return builder instance.
         * @throws IllegalArgumentException when schema is null or empty.
         */
        @NonNull
        public Builder setURLSchema(@NonNull String urlSchema) {
            if (TextUtils.isEmpty(urlSchema)) {
                throw new IllegalArgumentException("schema is null or empty");
            }
            this.schema = urlSchema;
            return this;
        }

        /** Set site name.
         * Optional. by Default "CUSTOM" is applied.
         * This site name should match with your Gateway Agent configuration
         * if you interact Gateway Agent with this SDK.
         * @param siteName
         * @return builder instance
         * @throws IllegalArgumentException when siteName is null or empty.
         */
        @NonNull
        public Builder setSiteName(@NonNull String siteName) {
            if (TextUtils.isEmpty(siteName)) {
                throw new IllegalArgumentException("siteName is null or empty");
            }
            this.siteName = siteName;
            return this;
        }

        /** Build KiiApp instance.
         * @return KiiApp instance.
         */
        @NonNull
        public KiiApp build() {
            KiiApp app = new KiiApp(appID, appKey, hostName);
            app.baseUrl = this.schema + "://" + this.hostName;
            if (this.port > 0) {
                app.baseUrl = app.baseUrl + ":" + this.port;
            }
            app.siteName = this.siteName;
            return app;
        }
    }
}
