package com.kii.thingif.core.trigger.clause;

import android.os.Parcelable;

import org.json.JSONObject;

public abstract class Clause implements Parcelable {

    public abstract JSONObject toJSONObject();

}
