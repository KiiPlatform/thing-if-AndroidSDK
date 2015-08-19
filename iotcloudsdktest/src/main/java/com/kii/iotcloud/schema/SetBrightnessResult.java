package com.kii.iotcloud.schema;

import com.kii.iotcloud.command.ActionResult;

public class SetBrightnessResult extends ActionResult {
    @Override
    public String getActionName() {
        return "setBrightness";
    }
}
