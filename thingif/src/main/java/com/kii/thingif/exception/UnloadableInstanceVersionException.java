package com.kii.thingif.exception;

/**
 * Exception indicating the instance couldn't be loaded from the SharedPreferences.
 */
public class UnloadableInstanceVersionException extends ThingIFException {
    public UnloadableInstanceVersionException(String tag) {
        super(String.format("Instance that tagged as %s couldn't be loaded from the SharedPreferences", tag));
    }
}
