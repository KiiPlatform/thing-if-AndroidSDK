package com.kii.thingif.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kii.thingif.actions.AirConditionerActions;
import com.kii.thingif.command.Action;
import com.kii.thingif.internal.gson.ActionAdapter;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

public class ActionAdapterTest {
    @Test
    public void serializationTest() {
        JsonObject singleActon1 = new JsonObject();
        singleActon1.addProperty("turnPower", true);
        JsonObject singleAction2 = new JsonObject();
        singleAction2.addProperty("setPresetTemperature", 23);

        JsonArray expectedResult = new JsonArray();
        expectedResult.add(singleActon1);
        expectedResult.add(singleAction2);

        AirConditionerActions action = new AirConditionerActions(true, 23);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        Action.class,
                        new ActionAdapter(AirConditionerActions.class))
                .create();

        JsonElement serializedResult = gson.toJsonTree(action, Action.class);

        Assert.assertTrue(serializedResult.isJsonArray());
        Assert.assertEquals(expectedResult.toString(), serializedResult.toString());
    }

    @Test
    public void deserializationTest() throws Exception{
        AirConditionerActions expectedAction = new AirConditionerActions(true, 23);

        JSONArray jsonArray = new JSONArray()
                .put(new JSONObject().put("turnPower", true))
                .put(new JSONObject().put("setPresetTemperature", 23));

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        Action.class,
                        new ActionAdapter(AirConditionerActions.class))
                .create();

        AirConditionerActions deserializedAction =
                (AirConditionerActions) gson.fromJson(jsonArray.toString(), Action.class);
        Assert.assertEquals(
                expectedAction.isPower(),
                deserializedAction.isPower());
        Assert.assertEquals(
                expectedAction.getPresetTemperature(),
                deserializedAction.getPresetTemperature());
    }
}
