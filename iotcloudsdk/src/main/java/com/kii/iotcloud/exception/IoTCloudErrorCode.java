package com.kii.iotcloud.exception;

/**
 * Represents the error code that indicate the reason of error.
 */
public enum IoTCloudErrorCode implements ErrorCode {
    MISSING_TOKEN,
    UNDEFINED_ERROR,
    STATE_NOT_FOUND,
    OWNER_NOT_FOUND,
    COMMAND_NOT_FOUND,
    ISSUER_NOT_FOUND,
    TARGET_NOT_FOUND,
    TRIGGER_NOT_FOUND,
    THING_NOT_ONBOARDED,
    WRONG_COMMAND,
    WRONG_TARGET,
    WRONG_THING_TYPE,
    WRONG_PREDICATE,
    ERROR_SENDING_COMMAND;

    public String getCode() {
        return this.name();
    }
}
