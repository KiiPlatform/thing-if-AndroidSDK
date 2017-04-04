package com.kii.thingif.actions;

import com.kii.thingif.command.Action;

public class TurnPower implements Action{
    private Boolean power;
    public TurnPower(Boolean power) {
        this.power = power;
    }

    @Override
    public String getActionName() {
        return "turnPower";
    }

    public Boolean getPower() {
        return this.power;
    }
}
