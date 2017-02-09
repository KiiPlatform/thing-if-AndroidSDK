package com.kii.thingif.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.kii.thingif.actions.AirConditionerActions;
import com.kii.thingif.actions.HumidityActions;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.AliasAction;
import com.kii.thingif.internal.gson.AliasActionAdapter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
public class AliasActionAdapterTest {
    private static final String alias1 = "AirConditionerAlias";
    private static final String alias2 = "HumidityAlias";

    private Map<String, Class<? extends Action>> actionTypes = new HashMap<>();
    private Gson gson;
    @Before
    public void before() {
        actionTypes.put(alias1, AirConditionerActions.class);
        actionTypes.put(alias2, HumidityActions.class);

        gson = new GsonBuilder()
                .registerTypeAdapter(
                        AliasAction.class,
                        new AliasActionAdapter(this.actionTypes))
                .create();
    }

    @Test
    public void serializationTest() {
        AliasAction<AirConditionerActions> action1 = new AliasAction<>(
                alias1,
                new AirConditionerActions(true, null));
        String actualAction1 = gson.toJson(action1);
        JsonObject expectedAction1 = new JsonObject();
        JsonObject action1Json = new JsonObject();
        action1Json.addProperty("turnPower", true);
        expectedAction1.add(alias1, action1Json);
        Assert.assertEquals(expectedAction1.toString(), actualAction1);

        AliasAction<HumidityActions> action2 = new AliasAction<>(
                alias2,
                new HumidityActions(34));
        String actualAction2 = gson.toJson(action2);
        JsonObject expectedAction2 = new JsonObject();
        JsonObject action2Json = new JsonObject();
        action2Json.addProperty("setPresetHumidity", 34);
        expectedAction2.add(alias2, action2Json);
        Assert.assertEquals(expectedAction2.toString(), actualAction2);
    }

    @Test
    public void deserializationTest() {
        AliasAction<AirConditionerActions> expectedAction1 = new AliasAction<>(
                alias1, new AirConditionerActions(false, 23));
        AliasAction<HumidityActions> expectedAction2 = new AliasAction<>(
                alias2, new HumidityActions(50));

        JsonObject action1Json = new JsonObject();
        action1Json.addProperty("turnPower", false);
        action1Json.addProperty("setPresetTemperature", 23);
        JsonObject json1 = new JsonObject();
        json1.add(alias1, action1Json);

        AliasAction actualAction1 = gson.fromJson(json1, AliasAction.class);
        Assert.assertEquals(expectedAction1.getAlias(), actualAction1.getAlias());
        Assert.assertEquals(
                expectedAction1.getAction().getClass(),
                actualAction1.getAction().getClass());
        Assert.assertEquals(
                expectedAction1.getAction().isPower(),
                ((AirConditionerActions)actualAction1.getAction()).isPower());
        Assert.assertEquals(
                expectedAction1.getAction().getPresetTemperature(),
                ((AirConditionerActions)actualAction1.getAction()).getPresetTemperature());

        JsonObject action2Json = new JsonObject();
        action2Json.addProperty("setPresetHumidity", 50);
        JsonObject json2 = new JsonObject();
        json2.add(alias2, action2Json);
        AliasAction actualAction2 = gson.fromJson(json2, AliasAction.class);
        Assert.assertEquals(expectedAction2.getAlias(), actualAction2.getAlias());
        Assert.assertEquals(
                expectedAction2.getAction().getPresetHumidity(),
                ((HumidityActions)actualAction2.getAction()).getPresetHumidity());
    }
}
