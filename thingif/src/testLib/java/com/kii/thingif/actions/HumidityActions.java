package com.kii.thingif.actions;

import com.kii.thingif.command.Action;
import com.kii.thingif.command.ActionAnnotation;

public class HumidityActions implements Action {
    @ActionAnnotation(actionName = "setPresetHumidity")
    private Integer presetHumidity;

    public HumidityActions(Integer presetHumidity) {
        this.presetHumidity = presetHumidity;
    }

    public Integer getPresetHumidity() {
        return presetHumidity;
    }
}
