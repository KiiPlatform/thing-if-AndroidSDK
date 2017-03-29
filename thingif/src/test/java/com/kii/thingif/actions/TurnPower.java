package com.kii.thingif.actions;

import com.kii.thingif.command.Action;

import org.json.JSONException;
import org.json.JSONObject;


public class TurnPower implements Action, ActionToJSON {
    private Boolean power;
    public TurnPower(Boolean power) {
        this.power = power;
    }
    @Override
    public String getActionName() {
        return "turnPower";
    }

    public Boolean getPower() {
        return this.power;
    }

    @Override
    public JSONObject toJSONObject() {
        try {
            return new JSONObject().put(this.getActionName(), this.power);
        }catch (JSONException e) {
            // never throw
            throw new RuntimeException(e);
        }
    }
}
