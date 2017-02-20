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

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof HumidityActions)) return false;
        return this.presetHumidity.equals(((HumidityActions) o).getPresetHumidity());
    }
}
