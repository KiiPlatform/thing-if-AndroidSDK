package com.kii.thingif.exception;

import org.json.JSONObject;


/**
 * Thrown when IoT Cloud Server returned an error.
 */
public class ThingIFRestException extends ThingIFException {
    protected final int statusCode;
    protected final JSONObject body;
    public ThingIFRestException(String message, int statusCode, JSONObject body) {
        super(message + "    ## Server Returned HttpStatus:" + statusCode);
        this.statusCode = statusCode;
        this.body = body;
    }
    public int getStatusCode() {
        return this.statusCode;
    }
    public String getErrorCode() {
        if (this.body != null) {
            return this.body.optString("errorCode", null);
        }
        return null;
    }
    @Override
    public String getMessage() {
        if (this.body != null) {
            return super.getMessage() + "        " + this.body.optString("message", null);
        }
        return super.getMessage();
    }
}
