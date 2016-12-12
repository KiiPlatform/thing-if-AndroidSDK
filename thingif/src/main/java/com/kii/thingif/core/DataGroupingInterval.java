package com.kii.thingif.core;

public enum DataGroupingInterval {
    INTERVAL_1_MINUTE("1_MINUTE"),
    INTERVAL_15_MINUTES("15_MINUTES"),
    INTERVAL_30_MINUTES("30_MINUTES"),
    INTERVAL_1_HOUR("1_HOUR"),
    INTERVAL_12_HOURS ("12_HOURS");

    private final String interval;
    private DataGroupingInterval(String interval) {
        this.interval = interval;
    }

    public String getInterval() { return this.interval; }
}
