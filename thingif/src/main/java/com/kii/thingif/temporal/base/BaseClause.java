package com.kii.thingif.temporal.base;

import android.os.Parcelable;

import org.json.JSONObject;

public interface BaseClause extends Parcelable{
    JSONObject toJSONObject();
}
