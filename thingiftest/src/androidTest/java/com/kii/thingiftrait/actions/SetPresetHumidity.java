package com.kii.thingiftrait.actions;

import com.google.gson.annotations.SerializedName;
import com.kii.thingiftrait.command.Action;

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
