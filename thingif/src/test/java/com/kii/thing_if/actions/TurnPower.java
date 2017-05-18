package com.kii.thing_if.actions;

import com.google.gson.annotations.SerializedName;
import com.kii.thing_if.command.Action;

import org.json.JSONException;
import org.json.JSONObject;


public class TurnPower implements Action, ActionToJSON {
    @SerializedName("turnPower")
    private Boolean power;
    public TurnPower(Boolean power) {
        this.power = power;
    }

    public Boolean getPower() {
        return this.power;
    }

    @Override
    public JSONObject toJSONObject() {
        try {
            return new JSONObject().put("turnPower", this.power);
        }catch (JSONException e) {
            // never throw
            throw new RuntimeException(e);
        }
    }
}
