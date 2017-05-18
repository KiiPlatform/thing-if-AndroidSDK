package com.kii.thing_if;

import android.support.annotation.NonNull;

public interface TargetThing extends Target {
    @NonNull
    public String getThingID();
    @NonNull
    public String getVendorThingID();
}
