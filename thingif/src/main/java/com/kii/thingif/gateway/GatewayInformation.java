package com.kii.thingif.gateway;

import android.support.annotation.NonNull;
import android.text.TextUtils;

public class GatewayInformation {
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
}
