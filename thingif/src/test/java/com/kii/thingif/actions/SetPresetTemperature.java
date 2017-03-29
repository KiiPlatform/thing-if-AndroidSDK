package com.kii.thingif.actions;

import com.kii.thingif.command.Action;

import org.json.JSONException;
import org.json.JSONObject;

public class SetPresetTemperature implements Action, ActionToJSON {

    private Integer temperature;

    public SetPresetTemperature(Integer temperature) {
        this.temperature = temperature;
    }
    @Override
    public String getActionName() {
        return "setPresetTemperature";
    }

    public Integer getTemperature() {
        return this.temperature;
    }

    @Override
    public JSONObject toJSONObject() {
        try {
            return new JSONObject().put(this.getActionName(), this.temperature);
        }catch (JSONException e) {
            // never throw
            throw new RuntimeException(e);
        }
    }
}
