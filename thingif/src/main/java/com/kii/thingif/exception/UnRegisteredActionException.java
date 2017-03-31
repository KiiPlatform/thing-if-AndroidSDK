package com.kii.thingif.exception;

public class UnRegisteredActionException extends ThingIFException {
    public UnRegisteredActionException(String actionClassName, String alias) {
        super(
                String.format("Concrete Action[name=%s] is not registered to Alias[name=%s], " +
                        "so can not be serialized Please register it first. ",
                        actionClassName,
                        alias));
    }
}
