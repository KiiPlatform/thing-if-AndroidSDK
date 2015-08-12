package com.kii.iotcloud;

import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kii.iotcloud.command.Action;
import com.kii.iotcloud.command.ActionResult;
import com.kii.iotcloud.command.Command;
import com.kii.iotcloud.schema.Schema;
import com.kii.iotcloud.schema.SchemaBuilder;
import com.kii.iotcloud.utils.GsonRepository;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class GsonSerializationTest {
    @Test
    public void commandTest() throws Exception {
        // Define the schema
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder("ThingType", "SchemaName1", 10, LightState.class);
        sb.addActionClass(SetColor.class, SetColorResult.class);
        sb.addActionClass(SetColorTemperature.class, SetColorTemperatureResult.class);
        Schema schema = sb.build();

        JsonObject expectedJson = (JsonObject)new JsonParser().parse(
                "{" +
                "    \"schemaName\":\"SchemaName1\"," +
                "    \"schemaVersion\":10," +
                "    \"actionResults\":[" +
                "      {\"setColor\":{\"succeeded\":true}}," +
                "      {\"setColorTemperature\":{\"succeeded\":false}}" +
                "    ]," +
                "    \"actions\":[" +
                "      {\"setColor\":{\"color\":[128,0,255]}}," +
                "      {\"setColorTemperature\":{\"colorTemperature\":25}}" +
                "    ]," +
                "    \"issuer\":\"user:9876\"," +
                "    \"target\":\"thing:1234\"" +
                "}");

        Command command = new Command("SchemaName1", 10, new TypedID(TypedID.Types.THING, "1234"), new TypedID(TypedID.Types.USER, "9876"));
        command.addAction(new SetColor(128,0,255));
        command.addAction(new SetColorTemperature(25));
        command.addActionResult(new SetColorResult(true));
        command.addActionResult(new SetColorTemperatureResult(false));
        Gson gson = GsonRepository.gson(schema);
        JsonObject serializedJson = (JsonObject)new JsonParser().parse(gson.toJson(command));

        Assert.assertEquals(expectedJson, serializedJson);

        serializedJson.addProperty("commandID", "Command-1234567");
        command = gson.fromJson(serializedJson.toString(), Command.class);

        Assert.assertEquals("Command-1234567", command.getCommandID());
        Assert.assertEquals("SchemaName1", command.getSchemaName());
        Assert.assertEquals(10, command.getSchemaVersion());

        SetColor setColor = (SetColor)command.getActions().get(0);
        Assert.assertArrayEquals(new int[]{128,0,255}, setColor.color);

        SetColorTemperature setColorTemperature = (SetColorTemperature)command.getActions().get(1);
        Assert.assertEquals(25, setColorTemperature.colorTemperature);

        SetColorResult setColorResult = (SetColorResult)command.getActionResults().get(0);
        Assert.assertTrue(setColorResult.succeeded);

        SetColorTemperatureResult setColorTemperatureResult = (SetColorTemperatureResult)command.getActionResults().get(1);
        Assert.assertFalse(setColorTemperatureResult.succeeded);
    }
    @Test
    public void triggerTest() throws Exception {
        // TODO:
    }



    public static class SetColor extends Action {
        public int[] color = new int[3];
        public SetColor() {
        }
        public SetColor(int r, int g, int b) {
            this.color[0] = r;
            this.color[1] = g;
            this.color[2] = b;
        }
        public String getActionName() {
            return "setColor";
        }
    }
    public static class SetColorResult extends ActionResult {
        public String getActionName() {
            return "setColor";
        }
        public SetColorResult() {
        }
        public SetColorResult(boolean succeeded) {
            this.succeeded = succeeded;
        }
    }
    public static class SetColorTemperature extends Action {
        public int colorTemperature;
        public SetColorTemperature() {
        }
        public SetColorTemperature(int colorTemperature) {
            this.colorTemperature = colorTemperature;
        }
        public String getActionName() {
            return "setColorTemperature";
        }
    }
    public static class SetColorTemperatureResult extends ActionResult{
        public SetColorTemperatureResult() {
        }
        public SetColorTemperatureResult(boolean succeeded) {
            this.succeeded = succeeded;
        }
        public String getActionName() {
            return "setColorTemperature";
        }
    }
    public static class LightState extends TargetState {
        public boolean power;
        public int brightness;
        public int[] color = new int[3];
        public int colorTemperature;
    }
}