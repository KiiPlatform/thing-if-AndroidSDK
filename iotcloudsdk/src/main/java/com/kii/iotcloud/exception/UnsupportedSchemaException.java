package com.kii.iotcloud.exception;

public class UnsupportedSchemaException extends IoTCloudException {
    public UnsupportedSchemaException(String schemaName, int schemaVersion) {
        super(String.format("Schema[name=%s, version=%d] is not supported", schemaName, schemaVersion));
    }
}
