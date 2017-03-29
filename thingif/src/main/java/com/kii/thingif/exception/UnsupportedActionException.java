package com.kii.thingif.exception;

/**
 * Thrown when an unsupported action is detected.
 */
public class UnsupportedActionException extends ThingIFException {
    public UnsupportedActionException(String actionName, String alias) {
        super(
                String.format("Action[name=%s] of Alias[name=%s] is not supported. " +
                        "Please register an action class for it. ",
                        actionName,
                        alias));
    }
}
