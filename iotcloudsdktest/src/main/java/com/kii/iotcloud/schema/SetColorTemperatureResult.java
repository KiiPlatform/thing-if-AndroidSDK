package com.kii.iotcloud.schema;

import com.kii.iotcloud.command.ActionResult;

public class SetColorTemperatureResult extends ActionResult{
    public SetColorTemperatureResult() {
    }
    public SetColorTemperatureResult(boolean succeeded) {
        this.succeeded = succeeded;
    }
    @Override
    public String getActionName() {
        return "setColorTemperature";
    }
}
