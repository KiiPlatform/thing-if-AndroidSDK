package com.kii.thingif.states;

import com.kii.thingif.TargetState;

public class HumidityState implements TargetState {
    public int currentHumidity;

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
}
