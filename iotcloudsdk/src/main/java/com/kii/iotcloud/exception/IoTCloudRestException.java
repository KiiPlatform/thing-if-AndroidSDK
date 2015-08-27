package com.kii.iotcloud.exception;

import org.json.JSONObject;


/**
 * Thrown when IoT Cloud Server returned an error.
 */
public class IoTCloudRestException extends IoTCloudException {
    protected final int statusCode;
    protected final JSONObject body;
    public IoTCloudRestException(String message, int statusCode, JSONObject body) {
        super(message + "    ## Server Returned HttpStatus:" + statusCode);
        this.statusCode = statusCode;
        this.body = body;
    }
    public int getStatusCode() {
        return this.statusCode;
    }
    public ErrorCode getErrorCode() {
        if (this.body != null) {
            String errorCode = this.body.optString("errorCode", null);
            try {
                return IoTCloudErrorCode.valueOf(errorCode);
            } catch (Exception e) {
                return new UnkownErrorCode(errorCode);
            }
        }
        return new UnkownErrorCode(null);
    }
    @Override
    public String getMessage() {
        if (this.body != null) {
            return super.getMessage() + "        " + this.body.optString("message", null);
        }
        return super.getMessage();
    }
}
