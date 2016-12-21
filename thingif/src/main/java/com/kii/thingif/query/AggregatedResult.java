package com.kii.thingif.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kii.thingif.TargetState;

import java.util.Date;
import java.util.List;

public class AggregatedResult<T> {
    private @NonNull Date rangeFrom;
    private @NonNull Date rangeTo;
    private @NonNull T aggregatedResult;
    private @Nullable List<TargetState> aggregatedObjects;

    public AggregatedResult(
        @NonNull Date rangeFrom,
        @NonNull Date rangeTo,
        @NonNull T aggregatedResult,
        @Nullable List<TargetState> aggregatedObjects) {
        this.rangeFrom = rangeFrom;
        this.rangeTo = rangeTo;
        this.aggregatedResult = aggregatedResult;
        this.aggregatedObjects = aggregatedObjects;
    }

    @NonNull
    public Date getRangeFrom() {
        return rangeFrom;
    }

    @NonNull
    public Date getRangeTo() {
        return rangeTo;
    }

    @NonNull
    public T getAggregatedResult() {
        return aggregatedResult;
    }

    @Nullable
    public List<TargetState> getAggregatedObjects() {
        return aggregatedObjects;
    }
}
