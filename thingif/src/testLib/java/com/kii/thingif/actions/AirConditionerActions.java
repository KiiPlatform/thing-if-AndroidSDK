package com.kii.thingif.actions;

import com.google.gson.annotations.SerializedName;
import com.kii.thingif.command.Action;

public class AirConditionerActions implements Action {
    @SerializedName("turnPower")
    private Boolean power;
    @SerializedName("setPresetTemperature")
    private Integer presetTemperature;


    public AirConditionerActions(Boolean power,
                                Integer presetTemperature) {
        this.power = power;
        this.presetTemperature = presetTemperature;
    }

    public boolean isPower() {
        return this.power;
    }

    public int getPresetTemperature() {
        return this.presetTemperature;
    }

}
