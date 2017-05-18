package com.kii.thing_if.actions;

import com.google.gson.annotations.SerializedName;
import com.kii.thing_if.command.Action;

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
