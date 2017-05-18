package com.kii.thing_if.exception;

import org.json.JSONObject;

/**
 * Exception indicating that an access to a resource requested by a SDK has been forbidden by the server. (HTTP Status 403)
 */
public class ForbiddenException extends ThingIFRestException {
    public ForbiddenException(String message, JSONObject body) {
        super(message, 403, body);
    }
}
