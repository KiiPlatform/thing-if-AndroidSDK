package com.kii.thingif.core;

/** Represents backend service of the push notification. */
public enum PushBackend {
    /**
     * Google Cloud Messaging
     */
    GCM("ANDROID"),
    /**
     * JPush
     */
    JPUSH("JPUSH");

    private final String deviceType;

    private PushBackend(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceType() {
        return this.deviceType;
    }
}
