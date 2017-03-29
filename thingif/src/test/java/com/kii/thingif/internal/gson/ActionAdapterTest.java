package com.kii.thingif.internal.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.kii.thingif.actions.SetPresetTemperature;
import com.kii.thingif.actions.TurnPower;
import com.kii.thingif.command.Action;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ActionAdapterTest {
    class EmptyNameAction implements Action {
        private Boolean power;
        public EmptyNameAction(Boolean power) {
            this.power = power;
        }
        @Override
        public String getActionName() {
            return "";
        }

        public Boolean getPower() {
            return this.power;
        }
    }

    class NullNameAction implements Action {
        private Boolean power;
        public NullNameAction(Boolean power) {
            this.power = power;
        }
        @Override
        public String getActionName() {
            return null;
        }

        public Boolean getPower() {
            return this.power;
        }
    }
    @Test
    public void serializationBaseTest() {
        JsonObject singleActon1 = new JsonObject();
        singleActon1.addProperty("turnPower", true);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        Action.class,
                        new ActionAdapter(TurnPower.class))
                .create();

        JsonElement serializedResult = gson.toJsonTree(new TurnPower(true), Action.class);
        Assert.assertTrue(serializedResult.isJsonObject());
        Assert.assertEquals(singleActon1.toString(), serializedResult.toString());

        JsonObject singleActon2 = new JsonObject();
        singleActon2.addProperty("setPresetTemperature", 25);
        gson = new GsonBuilder()
                .registerTypeAdapter(
                        Action.class,
                        new ActionAdapter(SetPresetTemperature.class))
                .create();

        JsonElement serializedResult2 = gson.toJsonTree(new SetPresetTemperature(25), Action.class);
        Assert.assertTrue(serializedResult2.isJsonObject());
        Assert.assertEquals(singleActon2.toString(), serializedResult2.toString());
    }

    @Test(expected = JsonParseException.class)
    public void serialize_emptyActionName_should_throw_exceptionTest() {
        new GsonBuilder()
                .registerTypeAdapter(
                        Action.class,
                        new ActionAdapter(EmptyNameAction.class))
                .create()
                .toJson(
                        new EmptyNameAction(true),
                        Action.class);
    }

    @Test(expected = JsonParseException.class)
    public void serialize_nullActionName_should_throw_exceptionTest() {
        new GsonBuilder()
                .registerTypeAdapter(
                        Action.class,
                        new ActionAdapter(NullNameAction.class))
                .create()
                .toJson(
                        new NullNameAction(true),
                        Action.class);
    }

    @Test(expected = JsonParseException.class)
    public void serialize_nullField_should_throw_exceptionTest() {
        new GsonBuilder()
                .registerTypeAdapter(
                        Action.class,
                        new ActionAdapter(TurnPower.class))
                .create()
                .toJson(
                        new TurnPower(null),
                        Action.class);
    }

    @Test
    public void deserializationTest() throws Exception{
        JSONObject json1 = new JSONObject().put("turnPower", true);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        Action.class,
                        new ActionAdapter(TurnPower.class))
                .create();

        TurnPower deserializedAction1 =
                (TurnPower) gson.fromJson(json1.toString(), Action.class);
        Assert.assertTrue(deserializedAction1.getPower());
    }
}
