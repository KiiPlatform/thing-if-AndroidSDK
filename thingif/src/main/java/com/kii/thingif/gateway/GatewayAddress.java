package com.kii.thingif.gateway;

import android.os.Parcel;
import android.os.Parcelable;

public class GatewayAddress implements Parcelable {

    private final String scheme;
    private final String hostName;
    private final int port;
    private final String baseUrl;

    public GatewayAddress(String hostName) {
        this(null, hostName);
    }
    public GatewayAddress(String scheme, String hostName) {
        this(scheme, hostName, -1);
    }
    public GatewayAddress(String scheme, String hostName, int port) {
        this.scheme = scheme == null ? "http" : scheme;
        this.hostName = hostName;
        this.port = port;
        StringBuilder url = new StringBuilder();
        url.append(this.scheme.toLowerCase() + "://" + this.hostName);
        if (this.port > 0) {
            url.append(":" + this.port);
        }
        this.baseUrl = url.toString();
    }

    public String getScheme() {
        return this.scheme;
    }
    public String getHostName() {
        return this.hostName;
    }
    public int getPort() {
        return this.port;
    }
    public String getBaseUrl() {
        return this.baseUrl;
    }

    // Implementation of Parcelable
    public static final Creator<GatewayAddress> CREATOR = new Creator<GatewayAddress>() {
        @Override
        public GatewayAddress createFromParcel(Parcel in) {
            return new GatewayAddress(in);
        }

        @Override
        public GatewayAddress[] newArray(int size) {
            return new GatewayAddress[size];
        }
    };
    protected GatewayAddress(Parcel in) {
        this.scheme = in.readString();
        this.hostName = in.readString();
        this.port = in.readInt();
        this.baseUrl = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.scheme);
        dest.writeString(this.hostName);
        dest.writeInt(this.port);
        dest.writeString(this.baseUrl);
    }
}
