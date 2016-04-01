package com.kii.thingif.exception;

public class StoredGatewayAPIInstanceNotFoundException extends ThingIFException {
    public StoredGatewayAPIInstanceNotFoundException(String tag) {
        super(String.format("Instance of GatewayAPI that tagged as %s has not stored in the SharedPreferences", tag));
    }
}
