package com.kii.thing_if.mock_test;

import com.kii.thing_if.KiiApp;
import com.kii.thing_if.Owner;
import com.kii.thing_if.ThingIFAPI;
import com.kii.thing_if.TypedID;
import com.kii.thing_if.actions.SetPresetHumidity;
import com.kii.thing_if.actions.SetPresetTemperature;
import com.kii.thing_if.actions.TurnPower;
import com.kii.thing_if.states.AirConditionerState;
import com.kii.thing_if.states.HumidityState;

import org.json.JSONObject;
import org.robolectric.RuntimeEnvironment;

import java.io.BufferedReader;
import java.io.InputStreamReader;

abstract class ThingIFHTTPMockTestBase {

    protected ThingIFAPI.Builder createThingIFAPIBuilder(String testID)
            throws Exception
    {
        JSONObject setting = getSetting();

        return ThingIFAPI.Builder.newBuilder(
            RuntimeEnvironment.application.getApplicationContext(),
            KiiApp.Builder.builderWithHostName(
                testID,
                "http_mock_key",
                setting.getString("address")).setURLSchema(
                    "http").setPort(setting.getInt("port")).build(),
            new Owner(new TypedID(TypedID.Types.USER, "owner-id"),
                    "accesstoken"))
                .registerAction(
                    "AirConditionerAlias", "turnPower", TurnPower.class)
                .registerAction(
                    "AirConditionerAlias",
                    "setPresetTemperature",
                    SetPresetTemperature.class)
                .registerAction(
                    "HumidityAlias",
                    "setPresetHumidity",
                    SetPresetHumidity.class)
                .registerTargetState(
                    "AirConditionerAlias", AirConditionerState.class)
                .registerTargetState("HumidityAlias", HumidityState.class);
    }

    private JSONObject getSetting() throws Exception {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                RuntimeEnvironment.application.getApplicationContext()
                    .getAssets().open("setting.json")));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        return new JSONObject(builder.toString());
    }
}
