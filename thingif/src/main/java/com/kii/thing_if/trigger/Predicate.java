package com.kii.thing_if.trigger;

import android.os.Parcelable;

public abstract class Predicate implements Parcelable {
    public abstract EventSource getEventSource();
}