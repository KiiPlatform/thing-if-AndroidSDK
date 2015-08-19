package com.kii.iotcloud.schema;

import com.kii.iotcloud.command.ActionResult;

public class TurnPowerResult extends ActionResult {
    @Override
    public String getActionName() {
        return "turnPower";
    }
}
