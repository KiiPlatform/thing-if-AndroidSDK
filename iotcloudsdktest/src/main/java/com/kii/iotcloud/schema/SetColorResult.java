package com.kii.iotcloud.schema;

import com.kii.iotcloud.command.ActionResult;

public class SetColorResult extends ActionResult {
    @Override
    public String getActionName() {
        return "setColor";
    }
}
