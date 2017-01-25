package com.kii.thingiftest.schema;


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
