package com.kii.thingiftrait.internal.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.kii.thingiftrait.actions.SetPresetTemperature;
import com.kii.thingiftrait.actions.TurnPower;
import com.kii.thingiftrait.command.Action;
import com.kii.thingiftrait.exception.UnregisteredActionException;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

class TurnPower3 implements Action  {
    private Boolean turnPower;
    private Integer anotherField;

    TurnPower3(Boolean turnPower, Integer anotherField) {
        this.turnPower = turnPower;
        this.anotherField = anotherField;
    }

    Boolean getPower() {
        return this.turnPower;
    }

    Integer getAnotherField() {
        return this.anotherField;
    }
}

@RunWith(RobolectricTestRunner.class)
public class ActionAdapterTest {
    @Test
    public void serializationBaseTest() {
        JsonObject singleActon1 = new JsonObject();
        singleActon1.addProperty("turnPower", true);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        Action.class,
                        new ActionAdapter("turnPower"))
                .create();

        JsonElement serializedResult = gson.toJsonTree(new TurnPower(true), Action.class);
        Assert.assertTrue(serializedResult.isJsonObject());
        Assert.assertEquals(singleActon1.toString(), serializedResult.toString());

        JsonObject singleActon2 = new JsonObject();
        singleActon2.addProperty("setPresetTemperature", 25);
        gson = new GsonBuilder()
                .registerTypeAdapter(
                        Action.class,
                        new ActionAdapter("setPresetTemperature"))
                .create();

        JsonElement serializedResult2 = gson.toJsonTree(new SetPresetTemperature(25), Action.class);
        Assert.assertTrue(serializedResult2.isJsonObject());
        Assert.assertEquals(singleActon2.toString(), serializedResult2.toString());

        JsonObject singleAction3 = new JsonObject();
        singleAction3.addProperty("turnPower", true);
        gson = new GsonBuilder()
                .registerTypeAdapter(
                        Action.class,
                        new ActionAdapter("turnPower"))
                .create();
        JsonElement serializedResult3 = gson.toJsonTree(new TurnPower3(true, 23), Action.class);
        Assert.assertTrue(serializedResult3.isJsonObject());
        Assert.assertEquals(singleAction3.toString(), serializedResult3.toString());
    }

    @Test
    public void serialize_unRegisteredActionName_should_throw_exceptionTest() {
        try {
            new GsonBuilder()
                    .registerTypeAdapter(
                            Action.class,
                            new ActionAdapter("diffActionName"))
                    .create()
                    .toJson(
                            new TurnPower(true),
                            Action.class);
        }catch (JsonParseException e) {
            Assert.assertTrue(e.getCause() instanceof UnregisteredActionException);
        }
    }
}
