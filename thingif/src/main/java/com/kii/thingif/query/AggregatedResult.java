package com.kii.thingif.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kii.thingif.TargetState;

import java.util.List;

public class AggregatedResult<T extends Number, S extends TargetState> {
    private @NonNull TimeRange timeRange;
    private @NonNull T value;
    private @Nullable List<S> aggregatedObjects;

    public AggregatedResult(
        @NonNull TimeRange timeRange,
        @NonNull T value,
        @Nullable List<S> aggregatedObjects) {
        this.timeRange = timeRange;
        this.value = value;
        this.aggregatedObjects = aggregatedObjects;
    }

    @NonNull
    public TimeRange getTimeRange() {
        return this.timeRange;
    }

    @NonNull
    public T getValue() {
        return this.value;
    }

    @Nullable
    public List<S> getAggregatedObjects() {
        return this.aggregatedObjects;
    }
}
