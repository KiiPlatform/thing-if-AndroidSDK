package com.kii.thingif.states;

import com.kii.thingif.TargetState;

public class AirConditionerState implements TargetState {
    public boolean power;
    public int currentTemperature;

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
                this.currentTemperature == other.currentTemperature;
    }
}
