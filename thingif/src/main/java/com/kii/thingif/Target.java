package com.kii.thingif;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface Target extends Parcelable {
    public TypedID getTypedID();
    public String getAccessToken();
}

