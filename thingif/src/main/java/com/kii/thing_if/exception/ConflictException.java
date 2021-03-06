package com.kii.thing_if.exception;

import org.json.JSONObject;

/**
 * Exception indicating that the request could not be processed because of conflict in the request. (HTTP Status 409)
 */
public class ConflictException extends ThingIFRestException {
    public ConflictException(String message, JSONObject body) {
        super(message, 409, body);
    }
}
