package com.kii.iotcloud.testmodel;

import com.kii.iotcloud.command.ActionResult;

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
