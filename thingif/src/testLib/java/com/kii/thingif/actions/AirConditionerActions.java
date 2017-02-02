package com.kii.thingif.actions;

import com.kii.thingif.command.Action;
import com.kii.thingif.command.ActionAnnotation;

public class AirConditionerActions implements Action {
    @ActionAnnotation(actionName = "turnPower")
    private Boolean power;
    @ActionAnnotation(actionName = "setPresetTemperature")
    private Integer presetTemperature;


    public AirConditionerActions(Boolean power,
                                Integer presetTemperature) {
        this.power = power;
        this.presetTemperature = presetTemperature;
    }

    public boolean isPower() {
        return this.power;
    }

    public int getPresetTemperature() {
        return this.presetTemperature;
    }

}
