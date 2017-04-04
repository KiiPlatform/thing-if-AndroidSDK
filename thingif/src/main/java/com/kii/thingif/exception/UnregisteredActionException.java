package com.kii.thingif.exception;

public class UnregisteredActionException extends ThingIFException {
    public UnregisteredActionException(String actionClassName, String alias) {
        super(
                String.format("Concrete Action[name=%s] is not registered to Alias[name=%s], " +
                        "so can not be serialized Please register it first. ",
                        actionClassName,
                        alias));
    }
}
