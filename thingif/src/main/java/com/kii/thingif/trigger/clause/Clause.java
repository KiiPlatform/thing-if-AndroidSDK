package com.kii.thingif.trigger.clause;

import android.os.Parcelable;

import org.json.JSONObject;

public abstract class Clause implements Parcelable {

    public abstract JSONObject toJSONObject();

}
