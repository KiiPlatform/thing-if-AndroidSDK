package com.kii.thingif.trigger;

import android.os.Parcelable;

public abstract class Predicate implements Parcelable {
    public abstract EventSource getEventSource();
}