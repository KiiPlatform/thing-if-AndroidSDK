package com.kii.iotcloudsample.smart_light_demo;

import com.kii.iotcloud.command.Action;

public class TurnPower extends Action {
    public boolean power;
    @Override
    public String getActionName() {
        return "turnPower";
    }
}
