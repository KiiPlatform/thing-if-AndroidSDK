package com.kii.iotcloud.exception;

public class UnsupportedActionException extends IoTCloudException {
    public UnsupportedActionException(String schemaName, int schemaVersion, String actionName) {
        super(String.format("Action[name=%s] is not supported in Schema[name=%s, version=%d].", actionName, schemaName, schemaVersion));
    }
}
