package com.kii.iotcloud.schema;

import com.kii.iotcloud.command.ActionResult;

public class SetColorTemperatureResult extends ActionResult{
    @Override
    public String getActionName() {
        return "setColorTemperature";
    }
}
