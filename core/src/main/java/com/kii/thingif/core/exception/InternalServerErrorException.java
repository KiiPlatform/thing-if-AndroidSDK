package com.kii.thingif.core.exception;

import org.json.JSONObject;

/**
 * Exception indicating an internal error occurred on the server. (HTTP Status 500)
 */
public class InternalServerErrorException extends ThingIFRestException {
    public InternalServerErrorException(String message, JSONObject body) {
        super(message, 500, body);
    }
}
