package com.kii.thingif.core.exception;

/**
 * Thrown when an unsupported schema is detected.
 */
public class UnsupportedSchemaException extends ThingIFException {
    public UnsupportedSchemaException(String schemaName, int schemaVersion) {
        super(String.format("Schema[name=%s, version=%d] is not supported", schemaName, schemaVersion));
    }
}
