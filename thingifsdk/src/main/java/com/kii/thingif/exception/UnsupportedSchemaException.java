package com.kii.thingif.exception;

/**
 * Thrown when an unsupported schema is detected.
 */
public class UnsupportedSchemaException extends IoTCloudException {
    public UnsupportedSchemaException(String schemaName, int schemaVersion) {
        super(String.format("Schema[name=%s, version=%d] is not supported", schemaName, schemaVersion));
    }
}
