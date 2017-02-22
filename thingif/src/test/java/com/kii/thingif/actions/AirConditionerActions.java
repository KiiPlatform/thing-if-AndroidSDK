package com.kii.thingif.actions;

import com.google.gson.annotations.SerializedName;
import com.kii.thingif.command.Action;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AirConditionerActions implements Action, ToJSON {
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
                (this.presetTemperature == null?
                action.presetTemperature == null :
                this.presetTemperature.equals(action.presetTemperature));
    }

    @Override
    public JSONArray toJSONArray() {
    try {
            JSONArray ret = new JSONArray();
            if (this.power != null) {
                ret.put(new JSONObject().put("turnPower", this.power));
            }
            if (this.presetTemperature != null) {
                ret.put(new JSONObject().put("setPresetTemperature", this.presetTemperature));
            }
            return ret;
        }catch (JSONException ex) {
            throw new RuntimeException(ex);
        }

    }
}
