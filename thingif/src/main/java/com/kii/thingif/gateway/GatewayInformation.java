package com.kii.thingif.gateway;

public class GatewayInformation {
    private String vendorThingID;
    GatewayInformation() {
    }
    GatewayInformation(String vendorThingID) {
        this.vendorThingID = vendorThingID;
    }
    public String getVendorThingID() {
        return this.vendorThingID;
    }
}
