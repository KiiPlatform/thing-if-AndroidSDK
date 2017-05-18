package com.kii.thing_if.states;

import com.kii.thing_if.TargetState;

import org.json.JSONException;
import org.json.JSONObject;

public class HumidityState implements TargetState, StateToJson {
    public Integer currentHumidity;
    public HumidityState(){}

    public HumidityState(Integer currentHumidity) {
        this.currentHumidity = currentHumidity;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof HumidityState)) {
            return false;
        }
        HumidityState other = (HumidityState)o;
        return (this.currentHumidity == null? other.currentHumidity == null :
            this.currentHumidity.equals(other.currentHumidity));
    }

    @Override
    public JSONObject toJSONObject() {
        try {
            return new JSONObject().put("currentHumidity", this.currentHumidity);
        }catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
