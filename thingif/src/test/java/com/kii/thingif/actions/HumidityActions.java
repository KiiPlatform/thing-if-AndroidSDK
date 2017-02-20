package com.kii.thingif.actions;

import com.google.gson.annotations.SerializedName;
import com.kii.thingif.command.Action;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HumidityActions implements Action, ToJSON {
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

    @Override
    public JSONArray toJSONArray() {
        try {
            JSONArray ret = new JSONArray();
            if (this.presetHumidity != null) {
                ret.put(new JSONObject().put("setPresetHumidity", this.presetHumidity));
            }
            return ret;
        }catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }
}
