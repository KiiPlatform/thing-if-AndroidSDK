package com.kii.thing_if.exception;

/**
 * Thrown when failed to connect IoT Cloud Server.
 */
public class ThingIFException extends Exception {
    public ThingIFException(String message) {
        super(message);
    }
    public ThingIFException(String message, Throwable cause) {
        super(message, cause);
    }
}
