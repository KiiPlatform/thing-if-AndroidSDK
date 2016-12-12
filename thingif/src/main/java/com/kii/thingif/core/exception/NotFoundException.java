package com.kii.thingif.core.exception;

import org.json.JSONObject;

/**
 * Exception indicating a resource requested by a SDK was not found on the server. (HTTP Status 404)
 */
public class NotFoundException extends ThingIFRestException {
    public NotFoundException(String message, JSONObject body) {
        super(message, 404, body);
    }
}
