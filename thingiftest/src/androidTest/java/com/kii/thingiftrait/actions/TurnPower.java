package com.kii.thingiftrait.actions;

import com.google.gson.annotations.SerializedName;
import com.kii.thingiftrait.command.Action;

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
