package com.kii.thingif.internal.clause;

import android.os.Parcelable;

import org.json.JSONObject;

public interface Clause extends Parcelable {
    JSONObject toJSONObject();
}
