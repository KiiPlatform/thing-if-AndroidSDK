package com.kii.thingif.core.trigger;

import android.os.Parcelable;

public abstract class Predicate implements Parcelable {
    public abstract EventSource getEventSource();
}