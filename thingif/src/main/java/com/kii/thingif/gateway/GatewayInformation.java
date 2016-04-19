package com.kii.thingif.gateway;

public class GatewayInformation {
    private String vendorThingID;
    public GatewayInformation() {
    }
    GatewayInformation(String vendorThingID) {
        this.vendorThingID = vendorThingID;
    }
    public String getVendorThingID() {
        return this.vendorThingID;
    }
    public void setVendorThingID(String vendorThingID) {
        this.vendorThingID = vendorThingID;
    }
}
