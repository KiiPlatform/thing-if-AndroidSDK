package com.kii.thingif.states;

import com.kii.thingif.TargetState;
import com.kii.thingif.TargetStateAnnotation;

public class AirConditionerState implements TargetState {
    @TargetStateAnnotation()
    public boolean power;
    @TargetStateAnnotation()
    public int currentTemperature;
}
