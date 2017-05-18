package com.kii.thing_if.actions;

import com.google.gson.annotations.SerializedName;
import com.kii.thing_if.command.Action;

public class TurnPower implements Action{
    @SerializedName("turnPower")
    private Boolean power;
    public TurnPower(Boolean power) {
        this.power = power;
    }

    public Boolean getPower() {
        return this.power;
    }
}
