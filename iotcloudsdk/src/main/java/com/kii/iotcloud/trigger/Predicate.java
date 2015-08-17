package com.kii.iotcloud.trigger;

import android.os.Parcelable;

public abstract class Predicate implements Parcelable {
    public abstract EventSource getEventSource();
}