package com.kii.iotcloud.testmodel;

import com.kii.iotcloud.command.Action;

public class SetColor extends Action {
    public int[] color = new int[3];

    public SetColor() {
    }

    public SetColor(int r, int g, int b) {
        this.color[0] = r;
        this.color[1] = g;
        this.color[2] = b;
    }

    public String getActionName() {
        return "setColor";
    }
}
