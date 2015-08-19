package com.kii.iotcloud.schema;

import com.kii.iotcloud.command.Action;

public class SetColor extends Action {
    public int[] color = new int[3];
    @Override
    public String getActionName() {
        return "setColor";
    }
}
