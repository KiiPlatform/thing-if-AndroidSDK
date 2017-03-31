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

class OuterClass {

    private String className;
    public OuterClass(String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }

    public class TurnPower implements Action {
        private Boolean power;
        public TurnPower(Boolean power) {
            this.power = power;
        }
        @Override
        public String getActionName() {
            return "turnPower";
        }

        public Boolean getPower() {
            return this.power;
        }
    }
    public static class StaticTurnPower implements Action {
        private Boolean power;
        public StaticTurnPower(Boolean power) {
            this.power = power;
        }
        @Override
        public String getActionName() {
            return "turnPower";
        }

        public Boolean getPower() {
            return this.power;
        }
    }

    public class EmptyTurnPower implements Action {
        @Override
        public String getActionName() {
            return "turnPower";
        }
    }

    public class EmptyStaticTurnPower implements Action {
        @Override
        public String getActionName() {
            return "turnPower";
        }
    }
}

class EmptyAction implements Action {
    @Override
    public String getActionName() {
        return "turnPower";
    }
}
@RunWith(RobolectricTestRunner.class)
public class ActionAdapterTest {
    class EmptyNameAction implements Action {
        private Boolean power;
        EmptyNameAction(Boolean power) {
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
        NullNameAction(Boolean power) {
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

    class InnerTurnPower implements Action {
        private Boolean power;
        InnerTurnPower(Boolean power) {
            this.power = power;
        }

        @Override
        public String getActionName() {
            return "turnPower";
        }

        public Boolean getPower() {
            return this.power;
        }
    }

    static class InnerStaticTurnPower implements Action {
        private Boolean power;

        InnerStaticTurnPower(Boolean power) {
            this.power = power;
        }
        @Override
        public String getActionName() {
            return "turnPower";
        }

        public Boolean getPower() {
            return this.power;
        }
    }

    class InnerEmptyAction implements Action {
        @Override
        public String getActionName() {
            return "turnPower";
        }
    }

    static class InnerStaticEmptyAction implements Action {
        @Override
        public String getActionName() {
            return "turnPower";
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

        // serialize inner class
        JsonObject singleAction3 = new JsonObject();
        singleAction3.addProperty("turnPower", true);
        gson = new GsonBuilder()
                .registerTypeAdapter(
                        Action.class,
                        new ActionAdapter(InnerTurnPower.class))
                .create();
        JsonElement serializedResult3 = gson.toJsonTree(new InnerTurnPower(true), Action.class);
        Assert.assertTrue(serializedResult3.isJsonObject());
        Assert.assertEquals(singleAction3.toString(), serializedResult3.toString());

        // serialize inner static class
        JsonObject singleAction4 = new JsonObject();
        singleAction4.addProperty("turnPower", true);
        gson = new GsonBuilder()
                .registerTypeAdapter(
                        Action.class,
                        new ActionAdapter(InnerStaticTurnPower.class))
                .create();
        JsonElement serializedResult4 = gson.toJsonTree(new InnerStaticTurnPower(true), Action.class);
        Assert.assertTrue(serializedResult4.isJsonObject());
        Assert.assertEquals(singleAction4.toString(), serializedResult4.toString());
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

        // parse to inner class
        gson = new GsonBuilder()
                .registerTypeAdapter(
                    Action.class,
                    new ActionAdapter(InnerTurnPower.class))
                .create();
        InnerTurnPower parsedAction2 =
                (InnerTurnPower) gson.fromJson(json1.toString(), Action.class);
        Assert.assertTrue(parsedAction2.getPower());

        // parse to inner static class
        gson = new GsonBuilder()
                .registerTypeAdapter(
                        Action.class,
                        new ActionAdapter(InnerStaticTurnPower.class))
                .create();
        InnerStaticTurnPower parsedAction3 =
                (InnerStaticTurnPower) gson.fromJson(json1.toString(), Action.class);
        Assert.assertTrue(parsedAction3.getPower());
    }

    @Test(expected = JsonParseException.class)
    public void parse_to_innerEmptyAction_throw_exceptionTest() throws Exception {
        JSONObject json1 = new JSONObject().put("turnPower", true);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        Action.class,
                        new ActionAdapter(InnerEmptyAction.class))
                .create();
        gson.fromJson(json1.toString(), Action.class);
    }

    @Test(expected = JsonParseException.class)
    public void parse_to_innerStaticEmptyAction_throw_exceptionTest() throws Exception {
        JSONObject json1 = new JSONObject().put("turnPower", true);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        Action.class,
                        new ActionAdapter(InnerStaticEmptyAction.class))
                .create();
        gson.fromJson(json1.toString(), Action.class);
    }

    @Test(expected = JsonParseException.class)
    public void parse_to_emptyAction_throw_exceptionTest() throws Exception {
        JSONObject json1 = new JSONObject().put("turnPower", true);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        Action.class,
                        new ActionAdapter(EmptyAction.class))
                .create();
        gson.fromJson(json1.toString(), Action.class);
    }

    @Test
    public void parse_InnerClass_fromOtherClassTest() throws Exception {
        JSONObject json1 = new JSONObject().put("turnPower", true);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        Action.class,
                        new ActionAdapter(OuterClass.TurnPower.class))
                .create();
        OuterClass.TurnPower action =
                (OuterClass.TurnPower)gson.fromJson(json1.toString(), Action.class);
        Assert.assertTrue(action.getPower());
    }

    @Test
    public void parse_InnerStaticClass_fromOtherClassTest() throws Exception {
        JSONObject json1 = new JSONObject().put("turnPower", true);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        Action.class,
                        new ActionAdapter(OuterClass.StaticTurnPower.class))
                .create();
        OuterClass.StaticTurnPower action =
                (OuterClass.StaticTurnPower)gson.fromJson(json1.toString(), Action.class);
        Assert.assertTrue(action.getPower());
    }

    @Test(expected = JsonParseException.class)
    public void parse_to_innerEmptyAction_fromOuterClass_throw_exceptionTest() throws Exception {
        JSONObject json1 = new JSONObject().put("turnPower", true);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        Action.class,
                        new ActionAdapter(OuterClass.EmptyTurnPower.class))
                .create();
        gson.fromJson(json1.toString(), Action.class);
    }

    @Test(expected = JsonParseException.class)
    public void parse_to_innerStaticEmptyAction_fromOuterClass_throw_exceptionTest() throws Exception {
        JSONObject json1 = new JSONObject().put("turnPower", true);

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        Action.class,
                        new ActionAdapter(OuterClass.EmptyStaticTurnPower.class))
                .create();
        gson.fromJson(json1.toString(), Action.class);
    }
}
