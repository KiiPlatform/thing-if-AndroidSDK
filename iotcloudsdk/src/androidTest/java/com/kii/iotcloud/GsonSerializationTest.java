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
import com.kii.iotcloud.trigger.Condition;
import com.kii.iotcloud.trigger.Predicate;
import com.kii.iotcloud.trigger.SchedulePredicate;
import com.kii.iotcloud.trigger.Schedule;
import com.kii.iotcloud.trigger.StatePredicate;
import com.kii.iotcloud.trigger.Trigger;
import com.kii.iotcloud.trigger.TriggersWhen;
import com.kii.iotcloud.trigger.statement.And;
import com.kii.iotcloud.trigger.statement.Equals;
import com.kii.iotcloud.trigger.statement.GreaterThanOrEquals;
import com.kii.iotcloud.trigger.statement.NotEquals;
import com.kii.iotcloud.utils.GsonRepository;


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

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
                "    \"schema\":\"SchemaName1\"," +
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

        List<Action> actions = new ArrayList<Action>();
        actions.add(new SetColor(128, 0, 255));
        actions.add(new SetColorTemperature(25));
        Command command = new Command("SchemaName1", 10, new TypedID(TypedID.Types.THING, "1234"), new TypedID(TypedID.Types.USER, "9876"), actions);
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
    // SchedulePredicate is not implemented yet.
    @Test
    @Ignore
    public void scheduleTriggerTest() throws Exception {
        // Define the schema
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder("ThingType", "SchemaName1", 10, LightState.class);
        sb.addActionClass(SetColor.class, SetColorResult.class);
        sb.addActionClass(SetColorTemperature.class, SetColorTemperatureResult.class);
        Schema schema = sb.build();

        JsonObject expectedJson = (JsonObject)new JsonParser().parse(
                        "{" +
                        "    \"command\":" +
                        "    {" +
                        "        \"schema\":\"SchemaName1\"," +
                        "        \"schemaVersion\":10," +
                        "        \"actionResults\":[" +
                        "          {\"setColor\":{\"succeeded\":true}}," +
                        "          {\"setColorTemperature\":{\"succeeded\":false}}" +
                        "        ]," +
                        "        \"actions\":[" +
                        "          {\"setColor\":{\"color\":[128,0,255]}}," +
                        "          {\"setColorTemperature\":{\"colorTemperature\":25}}" +
                        "        ]," +
                        "        \"issuer\":\"user:9876\"," +
                        "        \"target\":\"thing:1234\"" +
                        "    }," +
                        "    \"predicate\":" +
                        "    {" +
                        "        \"eventSource\":\"schedule\"," +
                        "        \"schedule\":\"1 0 * * *\"" +
                        "    }," +
                        "    \"disabled\":false" +
                        "}");

        // Command
        List<Action> actions = new ArrayList<Action>();
        actions.add(new SetColor(128, 0, 255));
        actions.add(new SetColorTemperature(25));
        Command command = new Command("SchemaName1", 10, new TypedID(TypedID.Types.THING, "1234"), new TypedID(TypedID.Types.USER, "9876"), actions);
        command.addActionResult(new SetColorResult(true));
        command.addActionResult(new SetColorTemperatureResult(false));
        // SchedulePredicate
        Predicate predicate = new SchedulePredicate(new Schedule("1 0 * * *"));
        // Trigger
        Trigger trigger = new Trigger(predicate, command);


        Gson gson = GsonRepository.gson(schema);
        JsonObject serializedJson = (JsonObject)new JsonParser().parse(gson.toJson(trigger));

        Assert.assertEquals(expectedJson, serializedJson);

        serializedJson.addProperty("triggerID", "Trigger-1234567");
        trigger = gson.fromJson(serializedJson.toString(), Trigger.class);

        Assert.assertEquals("Trigger-1234567", trigger.getTriggerID());

        command = trigger.getCommand();
        Assert.assertEquals("SchemaName1", command.getSchemaName());
        Assert.assertEquals(10, command.getSchemaVersion());

        SetColor setColor = (SetColor)command.getActions().get(0);
        Assert.assertArrayEquals(new int[]{128, 0, 255}, setColor.color);

        SetColorTemperature setColorTemperature = (SetColorTemperature)command.getActions().get(1);
        Assert.assertEquals(25, setColorTemperature.colorTemperature);

        SetColorResult setColorResult = (SetColorResult)command.getActionResults().get(0);
        Assert.assertTrue(setColorResult.succeeded);

        SetColorTemperatureResult setColorTemperatureResult = (SetColorTemperatureResult)command.getActionResults().get(1);
        Assert.assertFalse(setColorTemperatureResult.succeeded);

        predicate = trigger.getPredicate();
        Assert.assertTrue(predicate instanceof SchedulePredicate);
        Assert.assertEquals("1 0 * * *", ((SchedulePredicate)predicate).getSchedule().getCronExpression());
    }
    @Test
    public void stateTriggerTest() throws Exception {
        // Define the schema
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder("ThingType", "SchemaName1", 10, LightState.class);
        sb.addActionClass(SetColor.class, SetColorResult.class);
        sb.addActionClass(SetColorTemperature.class, SetColorTemperatureResult.class);
        Schema schema = sb.build();

        JsonObject expectedJson = (JsonObject)new JsonParser().parse(
                "{" +
                        "    \"command\":" +
                        "    {" +
                        "        \"schema\":\"SchemaName1\"," +
                        "        \"schemaVersion\":10," +
                        "        \"actionResults\":[" +
                        "          {\"setColor\":{\"succeeded\":true}}," +
                        "          {\"setColorTemperature\":{\"succeeded\":false}}" +
                        "        ]," +
                        "        \"actions\":[" +
                        "          {\"setColor\":{\"color\":[128,0,255]}}," +
                        "          {\"setColorTemperature\":{\"colorTemperature\":25}}" +
                        "        ]," +
                        "        \"issuer\":\"user:9876\"," +
                        "        \"target\":\"thing:1234\"" +
                        "    }," +
                        "    \"predicate\":{" +
                        "        \"eventSource\":\"states\"," +
                        "        \"triggersWhen\":\"CONDITION_FALSE_TO_TRUE\"," +
                        "        \"condition\":{" +
                        "            \"type\":\"and\"," +
                        "            \"clauses\":[" +
                        "                {\"type\":\"eq\", \"field\":\"prefecture\", \"value\":\"Tokyo\"}," +
                        "                {\"type\":\"not\", \"clause\":{\"type\":\"eq\", \"field\":\"city\", \"value\":\"Akasaka\"}}," +
                        "                {\"type\":\"range\", \"field\":\"temperature\", \"upperLimit\":25, \"upperIncluded\":true}" +
                        "            ]" +
                        "        }" +
                        "    }," +
                        "    \"disabled\":false" +
                        "}");

        // Command
        List<Action> actions = new ArrayList<Action>();
        actions.add(new SetColor(128, 0, 255));
        actions.add(new SetColorTemperature(25));
        Command command = new Command("SchemaName1", 10, new TypedID(TypedID.Types.THING, "1234"), new TypedID(TypedID.Types.USER, "9876"), actions);
        command.addActionResult(new SetColorResult(true));
        command.addActionResult(new SetColorTemperatureResult(false));
        // StatePredicate
        Equals eq = new Equals("prefecture", "Tokyo");
        NotEquals neq = new NotEquals(new Equals("city", "Akasaka"));
        GreaterThanOrEquals gte = new GreaterThanOrEquals("temperature", 25);
        And and = new And(eq, neq, gte);
        Condition condition = new Condition(and);
        Predicate predicate = new StatePredicate(condition, TriggersWhen.CONDITION_FALSE_TO_TRUE);
        // Trigger
        Trigger trigger = new Trigger(predicate, command);


        Gson gson = GsonRepository.gson(schema);
        JsonObject serializedJson = (JsonObject)new JsonParser().parse(gson.toJson(trigger));

        Assert.assertEquals(expectedJson, serializedJson);

        serializedJson.addProperty("triggerID", "Trigger-1234567");
        trigger = gson.fromJson(serializedJson.toString(), Trigger.class);

        Assert.assertEquals("Trigger-1234567", trigger.getTriggerID());

        command = trigger.getCommand();
        Assert.assertEquals("SchemaName1", command.getSchemaName());
        Assert.assertEquals(10, command.getSchemaVersion());

        SetColor setColor = (SetColor)command.getActions().get(0);
        Assert.assertArrayEquals(new int[]{128, 0, 255}, setColor.color);

        SetColorTemperature setColorTemperature = (SetColorTemperature)command.getActions().get(1);
        Assert.assertEquals(25, setColorTemperature.colorTemperature);

        SetColorResult setColorResult = (SetColorResult)command.getActionResults().get(0);
        Assert.assertTrue(setColorResult.succeeded);

        SetColorTemperatureResult setColorTemperatureResult = (SetColorTemperatureResult)command.getActionResults().get(1);
        Assert.assertFalse(setColorTemperatureResult.succeeded);

        predicate = trigger.getPredicate();
        Assert.assertTrue(predicate instanceof StatePredicate);
        Assert.assertEquals(TriggersWhen.CONDITION_FALSE_TO_TRUE, ((StatePredicate) predicate).getTriggersWhen());
        Assert.assertEquals(condition.getStatement(), ((StatePredicate) predicate).getCondition().getStatement());
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