package com.kii.thingif.exception;

/**
 * Exception indicating the instance has not stored in the SharedPreferences.
 */
public class StoredInstanceNotFoundException extends ThingIFException {
    public StoredInstanceNotFoundException(String tag) {
        super(String.format("Instance that tagged as %s has not stored in the SharedPreferences", tag));
    }
}
