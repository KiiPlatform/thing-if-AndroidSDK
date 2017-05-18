package com.kii.thing_if.states;

import com.kii.thing_if.TargetState;

import org.json.JSONException;
import org.json.JSONObject;

public class AirConditionerState implements TargetState, StateToJSON {
    public Boolean power;
    public Integer currentTemperature;

    public AirConditionerState(Boolean power, Integer currentTemperature) {
        this.power = power;
        this.currentTemperature = currentTemperature;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof AirConditionerState)) {
            return false;
        }
        AirConditionerState other = (AirConditionerState)o;
        return this.power == other.power &&
                this.currentTemperature.equals(other.currentTemperature);
    }

    @Override
    public JSONObject toJSONObject() {
        try {
            return new JSONObject()
                    .putOpt("power", this.power)
                    .putOpt("currentTemperature", this.currentTemperature);
        }catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
