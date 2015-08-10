package com.kii.iotcloud;

import org.json.JSONObject;

public class IoTCloudRestException extends IoTCloudException {
    private final int statusCode;
    private final JSONObject body;
    public IoTCloudRestException(int statusCode, JSONObject body) {
        this.statusCode = statusCode;
        this.body = body;
    }
    public String getErrorCode() {
        if (this.body != null) {
            return this.body.optString("errorCode");
        }
        return null;
    }
    @Override
    public String getMessage() {
        if (this.body != null) {
            return this.body.optString("message");
        }
        return null;
    }
}
