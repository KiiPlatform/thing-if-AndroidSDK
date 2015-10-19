package com.kii.thingif.testschemas;

import com.kii.thingif.command.ActionResult;

public class SetBrightnessResult extends ActionResult {
    public SetBrightnessResult() {
    }
    public SetBrightnessResult(boolean succeeded) {
        this.succeeded = succeeded;
    }
    @Override
    public String getActionName() {
        return "setBrightness";
    }
}
