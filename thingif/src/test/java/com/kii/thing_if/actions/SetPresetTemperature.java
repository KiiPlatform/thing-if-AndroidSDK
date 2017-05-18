package com.kii.thing_if.actions;

import com.google.gson.annotations.SerializedName;
import com.kii.thing_if.command.Action;

import org.json.JSONException;
import org.json.JSONObject;

public class SetPresetTemperature implements Action, ActionToJSON {

    @SerializedName("setPresetTemperature")
    private Integer temperature;

    public SetPresetTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public Integer getTemperature() {
        return this.temperature;
    }

    @Override
    public JSONObject toJSONObject() {
        try {
            return new JSONObject().put("setPresetTemperature", this.temperature);
        }catch (JSONException e) {
            // never throw
            throw new RuntimeException(e);
        }
    }
}
