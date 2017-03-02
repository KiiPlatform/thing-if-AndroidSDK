package com.kii.thingif.states;

import com.kii.thingif.TargetState;

import org.json.JSONException;
import org.json.JSONObject;

public class AirConditionerState implements TargetState, StateToJson {
    public Boolean power;
    public Integer currentTemperature;
    public AirConditionerState(){}

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
        return (this.power == null? other.power == null : this.power.equals(other.power)) &&
                (this.currentTemperature == null? other.currentTemperature == null :
                        this.currentTemperature.equals(other.currentTemperature));
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
