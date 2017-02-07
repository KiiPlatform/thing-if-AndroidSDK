package com.kii.thingif.states;

import com.kii.thingif.TargetState;
import com.kii.thingif.TargetStateAnnotation;

public class HumidityState implements TargetState {
    @TargetStateAnnotation()
    public int currentHumidity;
}
