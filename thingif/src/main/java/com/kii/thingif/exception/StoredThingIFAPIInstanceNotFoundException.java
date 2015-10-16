package com.kii.thingif.exception;

/**
 * Exception indicating the instance of ThingIFAPI has not stored in the SharedPreferences.
 */
public class StoredThingIFAPIInstanceNotFoundException extends ThingIFException {
    public StoredThingIFAPIInstanceNotFoundException(String tag) {
        super(String.format("Instance of ThingIFAPI that tagged as %s has not stored in the SharedPreferences", tag));
    }
}
