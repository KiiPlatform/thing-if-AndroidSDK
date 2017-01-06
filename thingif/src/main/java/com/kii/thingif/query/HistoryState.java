package com.kii.thingif.query;

import android.support.annotation.NonNull;

import com.kii.thingif.TargetState;

import java.util.Date;

public class HistoryState<T extends TargetState> {
    private @NonNull T state;
    private @NonNull Date createdAt;

    public HistoryState(
            @NonNull T state,
            @NonNull Date createdAt) {
        this.state = state;
        this.createdAt = createdAt;
    }

    @NonNull
    public T getState() {
        return state;
    }

    @NonNull
    public Date getCreatedAt() {
        return createdAt;
    }
}
