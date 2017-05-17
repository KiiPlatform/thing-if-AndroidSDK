package com.kii.thingiftrait;

import android.support.annotation.NonNull;

public interface TargetThing extends Target {
    @NonNull
    public String getThingID();
    @NonNull
    public String getVendorThingID();
}
