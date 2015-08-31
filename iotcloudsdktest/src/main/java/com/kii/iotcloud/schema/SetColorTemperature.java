package com.kii.iotcloud.schema;

import com.kii.iotcloud.command.Action;

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
