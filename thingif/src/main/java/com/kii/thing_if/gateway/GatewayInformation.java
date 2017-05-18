package com.kii.thing_if.gateway;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

public class GatewayInformation implements Parcelable {
    private String vendorThingID;
    GatewayInformation() {
    }
    GatewayInformation(@NonNull String vendorThingID) {
        if (TextUtils.isEmpty(vendorThingID)) {
            throw new IllegalArgumentException("vendorThingID is null or empty");
        }
        this.vendorThingID = vendorThingID;
    }
    public String getVendorThingID() {
        return this.vendorThingID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.vendorThingID);
    }

    public GatewayInformation(Parcel in) {
        this.vendorThingID = in.readString();
    }

    public static final Creator<GatewayInformation> CREATOR = new Creator<GatewayInformation>() {
        @Override
        public GatewayInformation createFromParcel(Parcel source) {
            return new GatewayInformation(source);
        }

        @Override
        public GatewayInformation[] newArray(int size) {
            return new GatewayInformation[size];
        }
    };
}
