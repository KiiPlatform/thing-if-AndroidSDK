package com.kii.iotcloud.exception;

public class IoTCloudException extends Exception {
    public IoTCloudException(String message) {
        super(message);
    }
    public IoTCloudException(String message, Throwable cause) {
        super(message, cause);
    }
}
