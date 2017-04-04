package com.kii.thingif.actions;

import com.kii.thingif.command.Action;

public class SetPresetHumidity implements Action {
    private Integer humidity;
    public SetPresetHumidity(Integer humidity) {
        this.humidity = humidity;
    }
    @Override
    public String getActionName() {
        return "setPresetHumidity";
    }

    public Integer getHumidity() {
        return this.humidity;
    }
}
