package com.kii.thingif.actions;

import com.google.gson.annotations.SerializedName;
import com.kii.thingif.command.Action;

public class SetPresetTemperature implements Action {
    @SerializedName("setPresetTemperature")
    private Integer temperature;
    public SetPresetTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public Integer getTemperature() {
        return this.temperature;
    }
}
