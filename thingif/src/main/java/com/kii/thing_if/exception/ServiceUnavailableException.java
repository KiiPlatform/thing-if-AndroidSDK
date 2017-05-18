package com.kii.thing_if.exception;

import org.json.JSONObject;

/**
 *  Exception indicating a service is not available. (HTTP Status 503)
 */
public class ServiceUnavailableException extends ThingIFRestException {
    public ServiceUnavailableException(String message, JSONObject body) {
        super(message, 503, body);
    }
}
