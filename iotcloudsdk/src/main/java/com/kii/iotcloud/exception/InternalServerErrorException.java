package com.kii.iotcloud.exception;

import org.json.JSONObject;

/**
 * Exception indicating an internal error occurred on the server. (HTTP Status 500)
 */
public class InternalServerErrorException extends IoTCloudRestException {
    public InternalServerErrorException(String message, JSONObject body) {
        super(message, 500, body);
    }
}
