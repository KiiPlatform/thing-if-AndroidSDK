package com.kii.iotcloud.testmodel;

import com.kii.iotcloud.command.ActionResult;

public class SetColorTemperatureResult extends ActionResult {
    public SetColorTemperatureResult() {
    }

    public SetColorTemperatureResult(boolean succeeded) {
        this.succeeded = succeeded;
    }

    public String getActionName() {
        return "setColorTemperature";
    }
}
