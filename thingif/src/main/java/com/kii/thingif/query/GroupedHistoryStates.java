package com.kii.thingif.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kii.thingif.TargetState;

import java.util.List;

public class GroupedHistoryStates<S extends TargetState> {
    private @NonNull TimeRange timeRange;
    private @NonNull List<HistoryState<S>> objects;

    public GroupedHistoryStates(
            @NonNull TimeRange timeRange,
            @NonNull List<HistoryState<S>> objects) {
        this.timeRange = timeRange;
        this.objects = objects;
    }

    @NonNull
    public TimeRange getTimeRange() {
        return this.timeRange;
    }

    @NonNull
    public List<HistoryState<S>> getObjects() {
        return this.objects;
    }
}
