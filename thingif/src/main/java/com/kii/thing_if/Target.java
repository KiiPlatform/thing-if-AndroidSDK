package com.kii.thing_if;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface Target extends Parcelable {
    public @NonNull TypedID getTypedID();
    public @Nullable String getAccessToken();
}

