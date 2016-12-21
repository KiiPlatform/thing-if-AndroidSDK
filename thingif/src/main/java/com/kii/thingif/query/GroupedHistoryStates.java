package com.kii.thingif.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kii.thingif.TargetState;

import java.util.Date;
import java.util.List;

public class GroupedHistoryStates {
    private @NonNull Date rangeFrom;
    private @NonNull Date rangeTo;
    private @NonNull List<TargetState> results;

    public GroupedHistoryStates(
            @NonNull Date rangeFrom,
            @NonNull Date rangeTo,
            @NonNull List<TargetState> results) {
        this.rangeFrom = rangeFrom;
        this.rangeTo = rangeTo;
        this.results = results;
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
    public List<TargetState> getResults() {
        return results;
    }
}
