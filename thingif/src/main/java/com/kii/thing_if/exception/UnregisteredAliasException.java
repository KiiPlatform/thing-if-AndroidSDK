package com.kii.thing_if.exception;

/**
 * Thrown when action/state of specified alias not registered when parsing command
 */
public class UnregisteredAliasException extends ThingIFException {
    public UnregisteredAliasException(String alias, boolean isAction) {
        super(
                String.format("%s of alias:[%s] is not registered",
                        (isAction? "Action": "TargetState"),
                        alias));
    }
}
