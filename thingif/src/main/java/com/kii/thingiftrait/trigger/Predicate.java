package com.kii.thingiftrait.trigger;

import android.os.Parcelable;

public abstract class Predicate implements Parcelable {
    public abstract EventSource getEventSource();
}