package com.kii.iotcloud.exception;

/**
 * Thrown when failed to connect IoT Cloud Server.
 */
public class IoTCloudException extends Exception {
    public IoTCloudException(String message) {
        super(message);
    }
    public IoTCloudException(String message, Throwable cause) {
        super(message, cause);
    }
}
