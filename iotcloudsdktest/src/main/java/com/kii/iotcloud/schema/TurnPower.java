package com.kii.iotcloud.schema;

import com.kii.iotcloud.command.Action;

public class TurnPower extends Action {
    public boolean power;
    @Override
    public String getActionName() {
        return "turnPower";
    }
}
