package com.kii.iotcloud.trigger.statement;

import android.os.Parcelable;

import org.json.JSONObject;

public interface Statement extends Parcelable {

    public JSONObject toJSONObject();

}
