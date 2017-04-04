package com.kii.thingif.actions;

import com.google.gson.annotations.SerializedName;
import com.kii.thingif.command.Action;

public class TurnPower implements Action{
    @SerializedName("turnPower")
    public Boolean power;
    public TurnPower(Boolean power) {
        this.power = power;
    }
}
