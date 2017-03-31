package com.kii.thingif.actions;

import com.google.gson.annotations.SerializedName;
import com.kii.thingif.command.Action;

import org.json.JSONException;
import org.json.JSONObject;

public class SetPresetHumidity implements Action, ActionToJSON{
    @SerializedName("setPresetHumidity")
    private Integer humidity;

    public SetPresetHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Integer getHumidity() {
        return this.humidity;
    }

    @Override
    public JSONObject toJSONObject() {
        try {
            return new JSONObject().put("setPresetHumidity", this.humidity);
        }catch (JSONException e) {
            // never throw
            throw new RuntimeException(e);
        }
    }
}
