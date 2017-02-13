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

    public Boolean isPower() {
        return this.power;
    }

    public Integer getPresetTemperature() {
        return this.presetTemperature;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof AirConditionerActions)) return false;
        AirConditionerActions action = (AirConditionerActions)o;
        return this.power == action.power &&
                this.presetTemperature == null?
                action.presetTemperature == null :
                this.presetTemperature.equals(action.presetTemperature);
    }
}
