package com.kii.thingif.testschemas;

import com.kii.thingif.command.ActionResult;

public class SetColorResult extends ActionResult {
    public String getActionName() {
        return "setColor";
    }

    public SetColorResult() {
    }

    public SetColorResult(boolean succeeded) {
        this.succeeded = succeeded;
    }
}
