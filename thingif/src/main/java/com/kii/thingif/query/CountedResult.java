package com.kii.thingif.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class CountedResult {
    private @NonNull TimeRange timeRange;
    private @NonNull Integer value;

    public CountedResult(
            @NonNull TimeRange timeRange,
            @NonNull Integer value) {
        this.timeRange = timeRange;
        this.value = value;
    }

    @NonNull
    public TimeRange getTimeRange() {
        return this.timeRange;
    }

    @NonNull
    public Integer getValue() {
        return this.value;
    }
}
