package com.kii.thing_if.mock_test;

import com.kii.thing_if.KiiApp;
import com.kii.thing_if.Owner;
import com.kii.thing_if.StandaloneThing;
import com.kii.thing_if.ThingIFAPI;
import com.kii.thing_if.ThingIFAPITestBase;
import com.kii.thing_if.TypedID;
import com.kii.thing_if.actions.SetPresetHumidity;
import com.kii.thing_if.actions.SetPresetTemperature;
import com.kii.thing_if.actions.TurnPower;
import com.kii.thing_if.command.AliasAction;
import com.kii.thing_if.command.Command;
import com.kii.thing_if.command.CommandForm;
import com.kii.thing_if.command.CommandState;
import com.kii.thing_if.states.AirConditionerState;
import com.kii.thing_if.states.HumidityState;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Config(manifest = "src/main/AndroidManifest.xml", assetDir = "../test/assets")
@RunWith(RobolectricTestRunner.class)
public class MockServerTest extends ThingIFAPITestBase {

    private final String alias1 = "AirConditionerAlias";
    private final String alias2 = "HumidityAlias";

    ThingIFAPI api;

    public String getIP() throws Exception {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                RuntimeEnvironment.application.getApplicationContext()
                    .getAssets().open("setting.json")));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        JSONObject json = new JSONObject(builder.toString());
        reader.close();
        return json.getString("IP");
    }

    @Before
    public void before() throws Exception {
        // Create ThingIFAPI instance.
        api = ThingIFAPI.Builder
                .newBuilder(
                    RuntimeEnvironment.application.getApplicationContext(),
                    KiiApp.Builder.builderWithHostName(
                        "wire_mock_app_id",
                        "wire_mock_app_key",
                        getIP()).setURLSchema(
                            "http").setPort(1080).build(),
                    new Owner(
                        new TypedID(
                            TypedID.Types.USER,
                            "my-owner-id"),
                        "owner-access-token-1234"))
                .registerAction(alias1, "turnPower", TurnPower.class)
                .registerAction(
                    alias1,
                    "setPresetTemperature",
                    SetPresetTemperature.class)
                .registerAction(
                    alias2,
                    "setPresetHumidity",
                    SetPresetHumidity.class)
                .registerTargetState(alias1, AirConditionerState.class)
                .registerTargetState(alias2, HumidityState.class)
                .setTarget(new StandaloneThing(
                            "thing-id",
                            "vendor-thing-id",
                            "access-token"))
                .build();
    }

    @Test
    public void getCommand() throws Exception {
        Command command = api.getCommand("XXXXXXXX");
        assertEquals(CommandState.DONE, command.getCommandState());
        assertEquals("XXXXXXXX", command.getCommandID());
        // TODO: check more.
    }

    @Test public void postNewCommand() throws Exception {
        Command command = api.postNewCommand(
            CommandForm.Builder.newBuilder(
                toList(new AliasAction(
                            alias1,
                            toList(new TurnPower(true))))).build());
        assertEquals(CommandState.SENDING, command.getCommandState());
        assertEquals("YYYYYYYY", command.getCommandID());
        // TODO: check more.
    }

    private <T> List<T> toList(T... elements) {
        ArrayList<T> list = new ArrayList<>();
        for (T element : elements) {
            list.add(element);
        }
        return list;
    }
}
