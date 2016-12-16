package com.kii.thingif.trigger.clause;

import android.os.Parcelable;

import com.kii.thingif.Alias;

import org.json.JSONObject;

public abstract class Clause<T extends Alias> implements Parcelable {

    public abstract JSONObject toJSONObject();

}
