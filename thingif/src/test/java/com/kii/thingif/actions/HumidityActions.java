package com.kii.thingif.actions;

import com.google.gson.annotations.SerializedName;
import com.kii.thingif.command.Action;

public class HumidityActions implements Action {
    @SerializedName("setPresetHumidity")
    private Integer presetHumidity;

    public HumidityActions(Integer presetHumidity) {
        this.presetHumidity = presetHumidity;
    }

    public Integer getPresetHumidity() {
        return presetHumidity;
    }
}
