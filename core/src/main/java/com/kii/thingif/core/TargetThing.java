package com.kii.thingif.core;

import android.support.annotation.NonNull;

public interface TargetThing extends Target {
    @NonNull
    public String getThingID();
    @NonNull
    public String getVendorThingID();
}
