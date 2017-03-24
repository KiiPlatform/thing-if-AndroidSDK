package com.kii.thingif.actions;

import com.google.gson.annotations.SerializedName;
import com.kii.thingif.command.Action;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Fields of this class are same as {@link AirConditionerActions}, only different is the order
 * of fields.
 */
public class DisOrderedAirConditionerActions implements Action, ToJSON {
    @SerializedName("setPresetTemperature")
    private Integer presetTemperature;

    @SerializedName("turnPower")
    private Boolean power;

    public DisOrderedAirConditionerActions(
            Boolean power,
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
        if (!(o instanceof DisOrderedAirConditionerActions)) return false;
        DisOrderedAirConditionerActions action = (DisOrderedAirConditionerActions)o;
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
