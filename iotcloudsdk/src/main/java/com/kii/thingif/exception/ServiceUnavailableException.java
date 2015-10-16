package com.kii.thingif.exception;

import org.json.JSONObject;

/**
 *  Exception indicating a service is not available. (HTTP Status 503)
 */
public class ServiceUnavailableException extends IoTCloudRestException {
    public ServiceUnavailableException(String message, JSONObject body) {
        super(message, 503, body);
    }
}
