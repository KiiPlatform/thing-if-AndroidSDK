package com.kii.thingif.exception;

/**
 * Exception indicating the instance of IoTCloudAPI has not stored in the SharedPreferences.
 */
public class StoredIoTCloudAPIInstanceNotFoundException extends IoTCloudException {
    public StoredIoTCloudAPIInstanceNotFoundException(String tag) {
        super(String.format("Instance of IoTCloudAPI that tagged as %s has not stored in the SharedPreferences", tag));
    }
}
