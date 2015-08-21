package com.kii.iotcloud.testmodel;

import com.kii.iotcloud.command.ActionResult;

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
