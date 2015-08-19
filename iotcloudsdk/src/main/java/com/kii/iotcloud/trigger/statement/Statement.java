package com.kii.iotcloud.trigger.statement;

import android.os.Parcelable;

import org.json.JSONObject;

public abstract class Statement implements Parcelable {

    public abstract JSONObject toJSONObject();

}
