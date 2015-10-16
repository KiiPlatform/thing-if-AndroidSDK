package com.kii.thingif.schema;

import com.kii.thingif.command.ActionResult;

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
