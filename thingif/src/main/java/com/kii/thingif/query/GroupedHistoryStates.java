package com.kii.thingif.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kii.thingif.TargetState;

import java.util.Date;
import java.util.List;

public class GroupedHistoryStates {
    private @NonNull Date rangeFrom;
    private @NonNull Date rangeTo;
    private @NonNull List<TargetState> objects;

    public GroupedHistoryStates(
            @NonNull Date rangeFrom,
            @NonNull Date rangeTo,
            @NonNull List<TargetState> objects) {
        this.rangeFrom = rangeFrom;
        this.rangeTo = rangeTo;
        this.objects = objects;
    }

    @NonNull
    public Date getRangeFrom() {
        return this.rangeFrom;
    }

    @NonNull
    public Date getRangeTo() {
        return this.rangeTo;
    }

    @NonNull
    public List<TargetState> getObjects() {
        return this.objects;
    }
}
