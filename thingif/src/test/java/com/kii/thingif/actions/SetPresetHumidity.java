package com.kii.thingif.actions;

import com.kii.thingif.command.Action;

import org.json.JSONException;
import org.json.JSONObject;

public class SetPresetHumidity implements Action, ActionToJSON{
    private Integer humidity;

    public SetPresetHumidity(Integer humidity) {
        this.humidity = humidity;
    }
    @Override
    public String getActionName() {
        return "setPresetHumidity";
    }

    public Integer getHumidity() {
        return this.humidity;
    }

    @Override
    public JSONObject toJSONObject() {
        try {
            return new JSONObject().put(this.getActionName(), this.humidity);
        }catch (JSONException e) {
            // never throw
            throw new RuntimeException(e);
        }
    }
}
