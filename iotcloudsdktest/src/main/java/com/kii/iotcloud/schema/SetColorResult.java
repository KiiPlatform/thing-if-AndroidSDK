package com.kii.iotcloud.schema;

import com.kii.iotcloud.command.ActionResult;

public class SetColorResult extends ActionResult {
    public SetColorResult() {
    }
    public SetColorResult(boolean succeeded) {
        this.succeeded = succeeded;
    }
    @Override
    public String getActionName() {
        return "setColor";
    }
}
