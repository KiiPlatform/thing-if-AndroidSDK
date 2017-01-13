package com.kii.thingif.query.clause;

import android.os.Parcelable;

import org.json.JSONObject;

public interface Clause extends Parcelable {
    JSONObject toJSONObject();
}
