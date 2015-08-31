package com.kii.iotcloud.exception;

import org.json.JSONObject;

/**
 * Exception indicating the server was acting as a gateway or proxy and did not receive a timely response from the upstream server. (HTTP Status 504)
 */
public class GatewayTimeoutException extends IoTCloudRestException {
    public GatewayTimeoutException(String message, JSONObject body) {
        super(message, 504, body);
    }
}
