package com.kii.thingiftest.schema;


import com.kii.thingif.command.Action;

public class SetColorTemperature extends Action {
    public int colorTemperature;
    public SetColorTemperature() {
    }
    public SetColorTemperature(int colorTemperature) {
        this.colorTemperature = colorTemperature;
    }
    @Override
    public String getActionName() {
        return "setColorTemperature";
    }
}
