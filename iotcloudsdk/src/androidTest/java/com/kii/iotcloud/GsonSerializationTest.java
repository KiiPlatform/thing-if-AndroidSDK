package com.kii.iotcloud;

import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kii.iotcloud.command.Action;
import com.kii.iotcloud.command.ActionResult;
import com.kii.iotcloud.command.Command;
import com.kii.iotcloud.internal.GsonRepository;
import com.kii.iotcloud.testschemas.LightState;
import com.kii.iotcloud.testschemas.SetColor;
import com.kii.iotcloud.testschemas.SetColorResult;
import com.kii.iotcloud.testschemas.SetColorTemperature;
import com.kii.iotcloud.testschemas.SetColorTemperatureResult;
import com.kii.iotcloud.schema.Schema;
import com.kii.iotcloud.schema.SchemaBuilder;
import com.kii.iotcloud.trigger.Condition;
import com.kii.iotcloud.trigger.Predicate;
import com.kii.iotcloud.trigger.SchedulePredicate;
import com.kii.iotcloud.trigger.Schedule;
import com.kii.iotcloud.trigger.StatePredicate;
import com.kii.iotcloud.trigger.Trigger;
import com.kii.iotcloud.trigger.TriggersWhen;
import com.kii.iotcloud.trigger.clause.And;
import com.kii.iotcloud.trigger.clause.Clause;
import com.kii.iotcloud.trigger.clause.Equals;
import com.kii.iotcloud.trigger.clause.NotEquals;
import com.kii.iotcloud.trigger.clause.Or;
import com.kii.iotcloud.trigger.clause.Range;


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class GsonSerializationTest extends SmallTestBase {
    @Test
    public void typedIDTest() throws Exception {
        TypedID userID = new TypedID(TypedID.Types.USER, "12345");
        TypedID groupID = new TypedID(TypedID.Types.GRPUP, "67890");
        TypedID thingID = new TypedID(TypedID.Types.THING, "12345");

        String serializedUserID = GsonRepository.gson(null).toJson(userID);
        String serializedGroupID = GsonRepository.gson(null).toJson(groupID);
        String serializedThingID = GsonRepository.gson(null).toJson(thingID);

        Assert.assertEquals("\"user:12345\"", serializedUserID);
        Assert.assertEquals("\"group:67890\"", serializedGroupID);
        Assert.assertEquals("\"thing:12345\"", serializedThingID);

        userID = GsonRepository.gson(null).fromJson(serializedUserID, TypedID.class);
        groupID = GsonRepository.gson(null).fromJson(serializedGroupID, TypedID.class);
        thingID = GsonRepository.gson(null).fromJson(serializedThingID, TypedID.class);

        Assert.assertEquals(TypedID.Types.USER, userID.getType());
        Assert.assertEquals("12345", userID.getID());
        Assert.assertEquals(TypedID.Types.GRPUP, groupID.getType());
        Assert.assertEquals("67890", groupID.getID());
        Assert.assertEquals(TypedID.Types.THING, thingID.getType());
        Assert.assertEquals("12345", thingID.getID());
    }
    @Test
    public void clauseTest() throws Exception {
        Equals stringEquals = new Equals("f1", "str");
        Equals numberEquals = new Equals("f2", 100);
        Equals booleanEquals = new Equals("f3", true);
        NotEquals stringNotEquals = new NotEquals(stringEquals);
        NotEquals numberNotEquals = new NotEquals(numberEquals);
        NotEquals booleanNotEquals = new NotEquals(booleanEquals);
        Range greaterThan = Range.greaterThan("f1", 1);
        Range greaterThanEquals = Range.greaterThanEquals("f2", 2);
        Range lessThan = Range.lessThan("f3", 3);
        Range lessThanEquals = Range.lessThanEquals("f4", 4);
        Range range = Range.range("f5", 5, Boolean.TRUE, 6, Boolean.FALSE);
        And and = new And(stringEquals, greaterThan);
        Or or = new Or(numberEquals, lessThan);

        JsonObject serializedStringEquals = (JsonObject)new JsonParser().parse(GsonRepository.gson(null).toJson(stringEquals));
        JsonObject serializedNumberEquals = (JsonObject)new JsonParser().parse(GsonRepository.gson(null).toJson(numberEquals));
        JsonObject serializedBooleanEquals = (JsonObject)new JsonParser().parse(GsonRepository.gson(null).toJson(booleanEquals));
        JsonObject serializedStringNotEquals = (JsonObject)new JsonParser().parse(GsonRepository.gson(null).toJson(stringNotEquals));
        JsonObject serializedNumberNotEquals = (JsonObject)new JsonParser().parse(GsonRepository.gson(null).toJson(numberNotEquals));
        JsonObject serializedBooleanNotEquals = (JsonObject)new JsonParser().parse(GsonRepository.gson(null).toJson(booleanNotEquals));
        JsonObject serializedGreaterThan = (JsonObject)new JsonParser().parse(GsonRepository.gson(null).toJson(greaterThan));
        JsonObject serializedGreaterThanEquals = (JsonObject)new JsonParser().parse(GsonRepository.gson(null).toJson(greaterThanEquals));
        JsonObject serializedLessThan = (JsonObject)new JsonParser().parse(GsonRepository.gson(null).toJson(lessThan));
        JsonObject serializedLessThanEquals = (JsonObject)new JsonParser().parse(GsonRepository.gson(null).toJson(lessThanEquals));
        JsonObject serializedRange = (JsonObject)new JsonParser().parse(GsonRepository.gson(null).toJson(range));
        JsonObject serializedAnd = (JsonObject)new JsonParser().parse(GsonRepository.gson(null).toJson(and));
        JsonObject serializedOr = (JsonObject)new JsonParser().parse(GsonRepository.gson(null).toJson(or));

        Assert.assertEquals((JsonObject)new JsonParser().parse("{\"type\":\"eq\", \"field\":\"f1\", \"value\":\"str\"}"), serializedStringEquals);
        Assert.assertEquals((JsonObject)new JsonParser().parse("{\"type\":\"eq\", \"field\":\"f2\", \"value\":100}"), serializedNumberEquals);
        Assert.assertEquals((JsonObject)new JsonParser().parse("{\"type\":\"eq\", \"field\":\"f3\", \"value\":true}"), serializedBooleanEquals);
        Assert.assertEquals((JsonObject)new JsonParser().parse("{\"type\":\"not\", \"clause\":{\"type\":\"eq\", \"field\":\"f1\", \"value\":\"str\"}}"), serializedStringNotEquals);
        Assert.assertEquals((JsonObject)new JsonParser().parse("{\"type\":\"not\", \"clause\":{\"type\":\"eq\", \"field\":\"f2\", \"value\":100}}"), serializedNumberNotEquals);
        Assert.assertEquals((JsonObject)new JsonParser().parse("{\"type\":\"not\", \"clause\":{\"type\":\"eq\", \"field\":\"f3\", \"value\":true}}"), serializedBooleanNotEquals);
        Assert.assertEquals((JsonObject)new JsonParser().parse("{\"type\":\"range\", \"field\":\"f1\", \"upperLimit\":1, \"upperIncluded\":false}"), serializedGreaterThan);
        Assert.assertEquals((JsonObject)new JsonParser().parse("{\"type\":\"range\", \"field\":\"f2\", \"upperLimit\":2, \"upperIncluded\":true}"), serializedGreaterThanEquals);
        Assert.assertEquals((JsonObject)new JsonParser().parse("{\"type\":\"range\", \"field\":\"f3\", \"lowerLimit\":3, \"lowerIncluded\":false}"), serializedLessThan);
        Assert.assertEquals((JsonObject)new JsonParser().parse("{\"type\":\"range\", \"field\":\"f4\", \"lowerLimit\":4, \"lowerIncluded\":true}"), serializedLessThanEquals);
        Assert.assertEquals((JsonObject)new JsonParser().parse("{\"type\":\"range\", \"field\":\"f5\", \"upperLimit\":5, \"upperIncluded\":true, \"lowerLimit\":6, \"lowerIncluded\":false}"), serializedRange);
        Assert.assertEquals((JsonObject)new JsonParser().parse("{\"type\":\"and\", \"clauses\":[{\"type\":\"eq\", \"field\":\"f1\", \"value\":\"str\"}, {\"type\":\"range\", \"field\":\"f1\", \"upperLimit\":1, \"upperIncluded\":false}]}"), serializedAnd);
        Assert.assertEquals((JsonObject)new JsonParser().parse("{\"type\":\"or\", \"clauses\":[{\"type\":\"eq\", \"field\":\"f2\", \"value\":100}, {\"type\":\"range\", \"field\":\"f3\", \"lowerLimit\":3, \"lowerIncluded\":false}]}"), serializedOr);

        Assert.assertEquals(stringEquals, GsonRepository.gson(null).fromJson(serializedStringEquals, Clause.class));
        Assert.assertEquals(numberEquals, GsonRepository.gson(null).fromJson(serializedNumberEquals, Clause.class));
        Assert.assertEquals(booleanEquals, GsonRepository.gson(null).fromJson(serializedBooleanEquals, Clause.class));
        Assert.assertEquals(stringNotEquals, GsonRepository.gson(null).fromJson(serializedStringNotEquals, Clause.class));
        Assert.assertEquals(numberNotEquals, GsonRepository.gson(null).fromJson(serializedNumberNotEquals, Clause.class));
        Assert.assertEquals(booleanNotEquals, GsonRepository.gson(null).fromJson(serializedBooleanNotEquals, Clause.class));
        Assert.assertEquals(greaterThan, GsonRepository.gson(null).fromJson(serializedGreaterThan, Clause.class));
        Assert.assertEquals(greaterThanEquals, GsonRepository.gson(null).fromJson(serializedGreaterThanEquals, Clause.class));
        Assert.assertEquals(lessThan, GsonRepository.gson(null).fromJson(serializedLessThan, Clause.class));
        Assert.assertEquals(lessThanEquals, GsonRepository.gson(null).fromJson(serializedLessThanEquals, Clause.class));
        Assert.assertEquals(range, GsonRepository.gson(null).fromJson(serializedRange, Clause.class));
        Assert.assertEquals(and, GsonRepository.gson(null).fromJson(serializedAnd, Clause.class));
        Assert.assertEquals(or, GsonRepository.gson(null).fromJson(serializedOr, Clause.class));
    }
    @Test
    public void actionTest() throws Exception {
        // Define the schema
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder("ThingType", "SchemaName1", 10, LightState.class);
        sb.addActionClass(SetColor.class, SetColorResult.class);
        sb.addActionClass(SetColorTemperature.class, SetColorTemperatureResult.class);
        Schema schema = sb.build();

        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);

        JsonObject serializedSetColor = (JsonObject)new JsonParser().parse(GsonRepository.gson(schema).toJson(setColor));
        JsonObject serializedSetColorTemperature = (JsonObject)new JsonParser().parse(GsonRepository.gson(schema).toJson(setColorTemperature));
        Assert.assertEquals((JsonObject)new JsonParser().parse("{\"setColor\":{\"color\":[128,0,255]}}"), serializedSetColor);
        Assert.assertEquals((JsonObject) new JsonParser().parse("{\"setColorTemperature\":{\"colorTemperature\":25}}"), serializedSetColorTemperature);

        SetColor deserializedSetColor = (SetColor)GsonRepository.gson(schema).fromJson(serializedSetColor, Action.class);
        SetColorTemperature deserializedSetColorTemperature = (SetColorTemperature)GsonRepository.gson(schema).fromJson(serializedSetColorTemperature, Action.class);

        Assert.assertArrayEquals(setColor.color, deserializedSetColor.color);
        Assert.assertEquals(setColorTemperature.colorTemperature, deserializedSetColorTemperature.colorTemperature);
    }
    @Test
    public void actionResultTest() throws Exception {
        // Define the schema
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder("ThingType", "SchemaName1", 10, LightState.class);
        sb.addActionClass(SetColor.class, SetColorResult.class);
        sb.addActionClass(SetColorTemperature.class, SetColorTemperatureResult.class);
        Schema schema = sb.build();

        SetColorResult setColorResult = new SetColorResult(true);
        SetColorTemperatureResult setColorTemperatureResult = new SetColorTemperatureResult(false);

        JsonObject serializedSetColorResult = (JsonObject)new JsonParser().parse(GsonRepository.gson(schema).toJson(setColorResult));
        JsonObject serializedSetColorTemperatureResult = (JsonObject)new JsonParser().parse(GsonRepository.gson(schema).toJson(setColorTemperatureResult));
        Assert.assertEquals((JsonObject)new JsonParser().parse("{\"setColor\":{\"succeeded\":true}}"), serializedSetColorResult);
        Assert.assertEquals((JsonObject) new JsonParser().parse("{\"setColorTemperature\":{\"succeeded\":false}}"), serializedSetColorTemperatureResult);

        SetColorResult deserializedSetColorResult = (SetColorResult)GsonRepository.gson(schema).fromJson(serializedSetColorResult, ActionResult.class);
        SetColorTemperatureResult deserializedSetColorTemperatureResult = (SetColorTemperatureResult)GsonRepository.gson(schema).fromJson(serializedSetColorTemperatureResult, ActionResult.class);

        Assert.assertEquals(setColorResult.succeeded, deserializedSetColorResult.succeeded);
        Assert.assertEquals(setColorTemperatureResult.succeeded, deserializedSetColorTemperatureResult.succeeded);
    }
    @Test
    public void conditionTest() throws Exception {
        Equals stringEquals = new Equals("f1", "str");
        Condition condition = new Condition(stringEquals);

        JsonObject serializedJson = (JsonObject)new JsonParser().parse(GsonRepository.gson(null).toJson(condition));
        Assert.assertEquals((JsonObject)new JsonParser().parse("{\"type\":\"eq\", \"field\":\"f1\", \"value\":\"str\"}"), serializedJson);
        Assert.assertEquals(condition.getClause(), GsonRepository.gson(null).fromJson(serializedJson, Condition.class).getClause());
    }
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
        Range gte = Range.greaterThanEquals("temperature", 25);
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
        Assert.assertEquals(condition.getClause(), ((StatePredicate) predicate).getCondition().getClause());
    }
}