package com.kii.thingif.states;

import com.kii.thingif.TargetState;

import org.json.JSONException;
import org.json.JSONObject;

public class HumidityState implements TargetState, StateToJSON {
    public int currentHumidity;

    public HumidityState(int currentHumidity) {
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
        return this.currentHumidity == other.currentHumidity;
    }

    @Override
    public JSONObject toJSONObject() {
        try {
            return new JSONObject()
                    .putOpt("currentHumidity", this.currentHumidity);
        }catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
