package com.kii.thingif.schema;

import com.kii.thingif.command.ActionResult;

public class TurnPowerResult extends ActionResult {
    public TurnPowerResult() {
    }
    public TurnPowerResult(boolean succeeded) {
        this.succeeded = succeeded;
    }
    @Override
    public String getActionName() {
        return "turnPower";
    }
}
