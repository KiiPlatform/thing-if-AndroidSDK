package com.kii.thingif.testschemas;

import com.kii.thingif.command.Action;

public class SetBrightness extends Action {
    public int brightness;
    public SetBrightness() {
    }
    public SetBrightness(int brightness) {
        this.brightness = brightness;
    }
    @Override
    public String getActionName() {
        return "setBrightness";
    }
}
