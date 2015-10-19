package com.kii.thingif.exception;

import org.json.JSONObject;

/**
 * Exception indicating a requested operation or access to a requested resource is not allowed. (HTTP Status 401)
 */
public class UnauthorizedException extends ThingIFRestException {
    public UnauthorizedException(String message, JSONObject body) {
        super(message, 401, body);
    }
}
