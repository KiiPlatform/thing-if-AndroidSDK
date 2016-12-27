package com.kii.thingif.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kii.thingif.TargetState;

import java.util.List;

public class GroupedHistoryStates {
    private @NonNull TimeRange timeRange;
    private @NonNull List<TargetState> objects;

    public GroupedHistoryStates(
            @NonNull TimeRange timeRange,
            @NonNull List<TargetState> objects) {
        this.timeRange = timeRange;
        this.objects = objects;
    }

    @NonNull
    public TimeRange getTimeRange() {
        return this.timeRange;
    }

    @NonNull
    public List<TargetState> getObjects() {
        return this.objects;
    }
}
