package com.kii.iotcloud.exception;

import org.json.JSONObject;

/**
 * Exception indicating that a request cannot be processed by the server. (HTTP Status 400)
 */
public class BadRequestException extends IoTCloudRestException {
    public BadRequestException(String message, JSONObject body) {
        super(message, 400, body);
    }
}
