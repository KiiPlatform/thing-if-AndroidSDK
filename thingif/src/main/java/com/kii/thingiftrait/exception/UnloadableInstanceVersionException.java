package com.kii.thingiftrait.exception;

/**
 * Exception indicating the instance couldn't be loaded from the SharedPreferences.
 */
public class UnloadableInstanceVersionException extends ThingIFException {
    public UnloadableInstanceVersionException(String tag, String storedVersion, String minimumVersion) {
        super(String.format(
                "Instance that tagged as %s couldn't be loaded from the SharedPreferences. " +
                "Stored instance version is %s. Required version is over %s",
                tag, storedVersion, minimumVersion));
    }
}
