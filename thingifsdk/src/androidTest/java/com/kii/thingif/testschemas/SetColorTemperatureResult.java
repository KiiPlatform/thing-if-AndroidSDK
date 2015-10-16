package com.kii.thingif.testschemas;

import com.kii.thingif.command.ActionResult;

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
