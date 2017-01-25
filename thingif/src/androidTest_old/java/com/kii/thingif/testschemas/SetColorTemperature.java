package com.kii.thingif.testschemas;

import com.kii.thingif.command.Action;

public class SetColorTemperature extends Action {
    public int colorTemperature;

    public SetColorTemperature() {
    }

    public SetColorTemperature(int colorTemperature) {
        this.colorTemperature = colorTemperature;
    }

    public String getActionName() {
        return "setColorTemperature";
    }
}
