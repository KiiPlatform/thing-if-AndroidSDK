package com.kii.thingif.exception;

import org.json.JSONObject;

/**
 * Exception indicating a resource requested by a SDK was not found on the server. (HTTP Status 404)
 */
public class NotFoundException extends IoTCloudRestException {
    public NotFoundException(String message, JSONObject body) {
        super(message, 404, body);
    }
}
