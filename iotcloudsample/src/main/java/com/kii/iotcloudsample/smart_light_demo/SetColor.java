package com.kii.iotcloudsample.smart_light_demo;

import com.kii.iotcloud.command.Action;

public class SetColor extends Action {
    public int[] color = new int[3];
    @Override
    public String getActionName() {
        return "setColor";
    }
}
