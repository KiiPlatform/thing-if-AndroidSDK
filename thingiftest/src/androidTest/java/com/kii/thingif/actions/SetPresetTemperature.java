package com.kii.thingif.actions;

import com.kii.thingif.command.Action;

public class SetPresetTemperature implements Action {
    private Integer temperature;
    public SetPresetTemperature(Integer temperature) {
        this.temperature = temperature;
    }
    @Override
    public String getActionName() {
        return "setPresetTemperature";
    }

    public Integer getTemperature() {
        return this.temperature;
    }
}
