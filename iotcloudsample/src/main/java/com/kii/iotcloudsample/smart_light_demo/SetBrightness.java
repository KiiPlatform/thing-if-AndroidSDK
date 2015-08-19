package com.kii.iotcloudsample.smart_light_demo;

import com.kii.iotcloud.command.Action;

public class SetBrightness extends Action {
    public int brightness;
    @Override
    public String getActionName() {
        return "setBrightness";
    }
}
