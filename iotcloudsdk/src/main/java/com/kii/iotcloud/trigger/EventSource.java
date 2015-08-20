package com.kii.iotcloud.trigger;

import android.text.TextUtils;

public enum EventSource {
    STATES("states"),
    SCHEDULE("schedule");
    private final String value;
    private EventSource(String value) {
        this.value = value;
    }
    public String getValue() {
        return this.value;
    }
    public static EventSource fromValue(String value) {
        for (EventSource eventSource : values()) {
            if (TextUtils.equals(eventSource.value, value)) {
                return eventSource;
            }
        }
        return null;
    }
}
