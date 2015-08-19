package com.kii.iotcloudsample.smart_light_demo;

import com.kii.iotcloud.command.ActionResult;

public class SetColorResult extends ActionResult {
    @Override
    public String getActionName() {
        return "setColor";
    }
}
