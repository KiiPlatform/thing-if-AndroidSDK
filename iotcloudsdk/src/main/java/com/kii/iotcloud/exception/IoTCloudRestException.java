package com.kii.iotcloud.exception;

import org.json.JSONObject;

public class IoTCloudRestException extends IoTCloudException {
    private final int statusCode;
    private final JSONObject body;
    public IoTCloudRestException(String message, int statusCode, JSONObject body) {
        super(message + "    ## Server Returned HttpStatus:" + statusCode);
        this.statusCode = statusCode;
        this.body = body;
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
