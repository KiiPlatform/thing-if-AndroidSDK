package com.kii.thingiftest.schema;


import com.kii.thingif.command.ActionResult;

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
