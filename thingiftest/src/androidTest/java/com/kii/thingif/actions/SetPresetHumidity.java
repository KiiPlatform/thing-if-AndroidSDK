package com.kii.thingif.actions;

import com.google.gson.annotations.SerializedName;
import com.kii.thingif.command.Action;

public class SetPresetHumidity implements Action {
    @SerializedName("setPresetHumidity")
    private Integer humidity;
    public SetPresetHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Integer getHumidity() {
        return this.humidity;
    }
}
