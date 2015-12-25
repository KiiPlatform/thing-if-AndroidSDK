package com.kii.thingif;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.ActionResult;
import com.kii.thingif.command.Command;
import com.kii.thingif.internal.GsonRepository;
import com.kii.thingif.testschemas.LightState;
import com.kii.thingif.testschemas.SetColor;
import com.kii.thingif.testschemas.SetColorResult;
import com.kii.thingif.testschemas.SetColorTemperature;
import com.kii.thingif.testschemas.SetColorTemperatureResult;
import com.kii.thingif.schema.Schema;
import com.kii.thingif.schema.SchemaBuilder;
import com.kii.thingif.testschemas.TurnPower;
import com.kii.thingif.testschemas.TurnPowerResult;
import com.kii.thingif.trigger.Condition;
import com.kii.thingif.trigger.Predicate;
import com.kii.thingif.trigger.SchedulePredicate;
import com.kii.thingif.trigger.Schedule;
import com.kii.thingif.trigger.StatePredicate;
import com.kii.thingif.trigger.Trigger;
import com.kii.thingif.trigger.TriggersWhen;
import com.kii.thingif.trigger.clause.And;
import com.kii.thingif.trigger.clause.Clause;
import com.kii.thingif.trigger.clause.Equals;
import com.kii.thingif.trigger.clause.NotEquals;
import com.kii.thingif.trigger.clause.Or;
import com.kii.thingif.trigger.clause.Range;


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

        String serializedUserID = GsonRepository.gson().toJson(userID);
        String serializedGroupID = GsonRepository.gson().toJson(groupID);
        String serializedThingID = GsonRepository.gson().toJson(thingID);

        Assert.assertEquals("\"user:12345\"", serializedUserID);
        Assert.assertEquals("\"group:67890\"", serializedGroupID);
        Assert.assertEquals("\"thing:12345\"", serializedThingID);

        userID = GsonRepository.gson().fromJson(serializedUserID, TypedID.class);
        groupID = GsonRepository.gson().fromJson(serializedGroupID, TypedID.class);
        thingID = GsonRepository.gson().fromJson(serializedThingID, TypedID.class);

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

        JsonObject serializedStringEquals = (JsonObject) new JsonParser().parse(GsonRepository.gson().toJson(stringEquals));
        JsonObject serializedNumberEquals = (JsonObject) new JsonParser().parse(GsonRepository.gson().toJson(numberEquals));
        JsonObject serializedBooleanEquals = (JsonObject) new JsonParser().parse(GsonRepository.gson().toJson(booleanEquals));
        JsonObject serializedStringNotEquals = (JsonObject) new JsonParser().parse(GsonRepository.gson().toJson(stringNotEquals));
        JsonObject serializedNumberNotEquals = (JsonObject) new JsonParser().parse(GsonRepository.gson().toJson(numberNotEquals));
        JsonObject serializedBooleanNotEquals = (JsonObject) new JsonParser().parse(GsonRepository.gson().toJson(booleanNotEquals));
        JsonObject serializedGreaterThan = (JsonObject) new JsonParser().parse(GsonRepository.gson().toJson(greaterThan));
        JsonObject serializedGreaterThanEquals = (JsonObject) new JsonParser().parse(GsonRepository.gson().toJson(greaterThanEquals));
        JsonObject serializedLessThan = (JsonObject) new JsonParser().parse(GsonRepository.gson().toJson(lessThan));
        JsonObject serializedLessThanEquals = (JsonObject) new JsonParser().parse(GsonRepository.gson().toJson(lessThanEquals));
        JsonObject serializedRange = (JsonObject) new JsonParser().parse(GsonRepository.gson().toJson(range));
        JsonObject serializedAnd = (JsonObject) new JsonParser().parse(GsonRepository.gson().toJson(and));
        JsonObject serializedOr = (JsonObject) new JsonParser().parse(GsonRepository.gson().toJson(or));

        Assert.assertEquals((JsonObject) new JsonParser().parse("{\"type\":\"eq\", \"field\":\"f1\", \"value\":\"str\"}"), serializedStringEquals);
        Assert.assertEquals((JsonObject) new JsonParser().parse("{\"type\":\"eq\", \"field\":\"f2\", \"value\":100}"), serializedNumberEquals);
        Assert.assertEquals((JsonObject) new JsonParser().parse("{\"type\":\"eq\", \"field\":\"f3\", \"value\":true}"), serializedBooleanEquals);
        Assert.assertEquals((JsonObject) new JsonParser().parse("{\"type\":\"not\", \"clause\":{\"type\":\"eq\", \"field\":\"f1\", \"value\":\"str\"}}"), serializedStringNotEquals);
        Assert.assertEquals((JsonObject) new JsonParser().parse("{\"type\":\"not\", \"clause\":{\"type\":\"eq\", \"field\":\"f2\", \"value\":100}}"), serializedNumberNotEquals);
        Assert.assertEquals((JsonObject) new JsonParser().parse("{\"type\":\"not\", \"clause\":{\"type\":\"eq\", \"field\":\"f3\", \"value\":true}}"), serializedBooleanNotEquals);
        Assert.assertEquals((JsonObject) new JsonParser().parse("{\"type\":\"range\", \"field\":\"f1\", \"lowerLimit\":1, \"lowerIncluded\":false}"), serializedGreaterThan);
        Assert.assertEquals((JsonObject) new JsonParser().parse("{\"type\":\"range\", \"field\":\"f2\", \"lowerLimit\":2, \"lowerIncluded\":true}"), serializedGreaterThanEquals);
        Assert.assertEquals((JsonObject) new JsonParser().parse("{\"type\":\"range\", \"field\":\"f3\", \"upperLimit\":3, \"upperIncluded\":false}"), serializedLessThan);
        Assert.assertEquals((JsonObject) new JsonParser().parse("{\"type\":\"range\", \"field\":\"f4\", \"upperLimit\":4, \"upperIncluded\":true}"), serializedLessThanEquals);
        Assert.assertEquals((JsonObject) new JsonParser().parse("{\"type\":\"range\", \"field\":\"f5\", \"upperLimit\":5, \"upperIncluded\":true, \"lowerLimit\":6, \"lowerIncluded\":false}"), serializedRange);
        Assert.assertEquals((JsonObject) new JsonParser().parse("{\"type\":\"and\", \"clauses\":[{\"type\":\"eq\", \"field\":\"f1\", \"value\":\"str\"}, {\"type\":\"range\", \"field\":\"f1\", \"lowerLimit\":1, \"lowerIncluded\":false}]}"), serializedAnd);
        Assert.assertEquals((JsonObject) new JsonParser().parse("{\"type\":\"or\", \"clauses\":[{\"type\":\"eq\", \"field\":\"f2\", \"value\":100}, {\"type\":\"range\", \"field\":\"f3\", \"upperLimit\":3, \"upperIncluded\":false}]}"), serializedOr);

        Assert.assertEquals(stringEquals, GsonRepository.gson().fromJson(serializedStringEquals, Clause.class));
        Assert.assertEquals(numberEquals, GsonRepository.gson().fromJson(serializedNumberEquals, Clause.class));
        Assert.assertEquals(booleanEquals, GsonRepository.gson().fromJson(serializedBooleanEquals, Clause.class));
        Assert.assertEquals(stringNotEquals, GsonRepository.gson().fromJson(serializedStringNotEquals, Clause.class));
        Assert.assertEquals(numberNotEquals, GsonRepository.gson().fromJson(serializedNumberNotEquals, Clause.class));
        Assert.assertEquals(booleanNotEquals, GsonRepository.gson().fromJson(serializedBooleanNotEquals, Clause.class));
        Assert.assertEquals(greaterThan, GsonRepository.gson().fromJson(serializedGreaterThan, Clause.class));
        Assert.assertEquals(greaterThanEquals, GsonRepository.gson().fromJson(serializedGreaterThanEquals, Clause.class));
        Assert.assertEquals(lessThan, GsonRepository.gson().fromJson(serializedLessThan, Clause.class));
        Assert.assertEquals(lessThanEquals, GsonRepository.gson().fromJson(serializedLessThanEquals, Clause.class));
        Assert.assertEquals(range, GsonRepository.gson().fromJson(serializedRange, Clause.class));
        Assert.assertEquals(and, GsonRepository.gson().fromJson(serializedAnd, Clause.class));
        Assert.assertEquals(or, GsonRepository.gson().fromJson(serializedOr, Clause.class));
    }

    @Test
    public void schemaTest() throws Exception {
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder("ThingType", "SchemaName1", 10, LightState.class);
        sb.addActionClass(SetColor.class, SetColorResult.class);
        sb.addActionClass(SetColorTemperature.class, SetColorTemperatureResult.class);
        Schema schema = sb.build();

        JsonObject expectedJson = (JsonObject) new JsonParser().parse(
                "{" +
                        "    \"thingType\":\"ThingType\"," +
                        "    \"schemaName\":\"SchemaName1\"," +
                        "    \"schemaVersion\":10," +
                        "    \"stateClass\":\"com.kii.thingif.testschemas.LightState\"," +
                        "    \"actionClasses\":[" +
                        "        \"com.kii.thingif.testschemas.SetColor\"," +
                        "        \"com.kii.thingif.testschemas.SetColorTemperature\"" +
                        "    ]," +
                        "    \"actionResultClasses\":[" +
                        "        \"com.kii.thingif.testschemas.SetColorResult\"," +
                        "        \"com.kii.thingif.testschemas.SetColorTemperatureResult\"" +
                        "    ]" +
                        "}");

        JsonObject serializedJson = (JsonObject) new JsonParser().parse(GsonRepository.gson().toJson(schema));
        Assert.assertEquals(expectedJson, serializedJson);

        Schema deserializedSchema = GsonRepository.gson().fromJson(serializedJson, Schema.class);
        Assert.assertEquals("ThingType", deserializedSchema.getThingType());
        Assert.assertEquals("SchemaName1", deserializedSchema.getSchemaName());
        Assert.assertEquals(10, deserializedSchema.getSchemaVersion());
        Assert.assertEquals(LightState.class, deserializedSchema.getStateClass());
        Assert.assertEquals(SetColor.class, deserializedSchema.getActionClasses().get(0));
        Assert.assertEquals(SetColorTemperature.class, deserializedSchema.getActionClasses().get(1));
        Assert.assertEquals(SetColorResult.class, deserializedSchema.getActionResultClasses().get(0));
        Assert.assertEquals(SetColorTemperatureResult.class, deserializedSchema.getActionResultClasses().get(1));
    }

    @Test
    public void iotCloudAPITest() throws Exception {
        ThingIFAPIBuilder builder = ThingIFAPIBuilder.newBuilder(InstrumentationRegistry.getTargetContext(), "appid", "appkey", Site.JP, new Owner(new TypedID(TypedID.Types.USER, "user1234"), "user-access-token-1234"));
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder("SmartLight", "LightDemoSchema", 1, LightState.class);
        sb.addActionClass(TurnPower.class, TurnPowerResult.class);
        sb.addActionClass(SetColor.class, SetColorResult.class);
        Schema schema = sb.build();
        builder.addSchema(schema);
        ThingIFAPI api = builder.build();
        Target target = new Target(new TypedID(TypedID.Types.THING, "th.1234567890"), "thing-access-token-1234");
        api.setTarget(target);

        JsonObject expectedJson = (JsonObject) new JsonParser().parse(
                "{" +
                        "    \"appID\":\"appid\"," +
                        "    \"appKey\":\"appkey\"," +
                        "    \"baseUrl\":\"https://api-jp.kii.com\"," +
                        "    \"owner\":{\"typedID\":\"user:user1234\",\"accessToken\":\"user-access-token-1234\"}," +
                        "    \"target\":{\"typedID\":\"thing:th.1234567890\",\"accessToken\":\"thing-access-token-1234\"}," +
                        "    \"schemas\":[" +
                        "        {" +
                        "            \"thingType\":\"SmartLight\"," +
                        "            \"schemaName\":\"LightDemoSchema\"," +
                        "            \"schemaVersion\":1," +
                        "            \"stateClass\":\"com.kii.thingif.testschemas.LightState\"," +
                        "            \"actionClasses\":[" +
                        "                \"com.kii.thingif.testschemas.TurnPower\"," +
                        "                \"com.kii.thingif.testschemas.SetColor\"" +
                        "            ]," +
                        "            \"actionResultClasses\":[" +
                        "                \"com.kii.thingif.testschemas.TurnPowerResult\"," +
                        "                \"com.kii.thingif.testschemas.SetColorResult\"" +
                        "            ]" +
                        "        }" +
                        "    ]" +
                        "}");
        JsonObject serializedJson = (JsonObject) new JsonParser().parse(GsonRepository.gson().toJson(api));
        Assert.assertEquals(expectedJson, serializedJson);

        ThingIFAPI deserializedApi = GsonRepository.gson().fromJson(serializedJson, ThingIFAPI.class);
        Assert.assertEquals("appid", deserializedApi.getAppID());
        Assert.assertEquals("appkey", deserializedApi.getAppKey());
        Assert.assertEquals(Site.JP.getBaseUrl(), deserializedApi.getBaseUrl());
        Assert.assertEquals(new TypedID(TypedID.Types.USER, "user1234"), deserializedApi.getOwner().getTypedID());
        Assert.assertEquals("user-access-token-1234", deserializedApi.getOwner().getAccessToken());
        Assert.assertEquals(new TypedID(TypedID.Types.THING, "th.1234567890"), deserializedApi.getTarget().getTypedID());
        Assert.assertEquals("thing-access-token-1234", deserializedApi.getTarget().getAccessToken());
        Assert.assertEquals(1, deserializedApi.getSchemas().size());
        Assert.assertEquals("SmartLight", deserializedApi.getSchemas().get(0).getThingType());
        Assert.assertEquals("LightDemoSchema", deserializedApi.getSchemas().get(0).getSchemaName());
        Assert.assertEquals(1, deserializedApi.getSchemas().get(0).getSchemaVersion());
        Assert.assertEquals(LightState.class, deserializedApi.getSchemas().get(0).getStateClass());
        Assert.assertEquals(TurnPower.class, deserializedApi.getSchemas().get(0).getActionClasses().get(0));
        Assert.assertEquals(SetColor.class, deserializedApi.getSchemas().get(0).getActionClasses().get(1));
        Assert.assertEquals(TurnPowerResult.class, deserializedApi.getSchemas().get(0).getActionResultClasses().get(0));
        Assert.assertEquals(SetColorResult.class, deserializedApi.getSchemas().get(0).getActionResultClasses().get(1));
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

        JsonObject serializedSetColor = (JsonObject) new JsonParser().parse(GsonRepository.gson(schema).toJson(setColor));
        JsonObject serializedSetColorTemperature = (JsonObject) new JsonParser().parse(GsonRepository.gson(schema).toJson(setColorTemperature));
        Assert.assertEquals((JsonObject) new JsonParser().parse("{\"setColor\":{\"color\":[128,0,255]}}"), serializedSetColor);
        Assert.assertEquals((JsonObject) new JsonParser().parse("{\"setColorTemperature\":{\"colorTemperature\":25}}"), serializedSetColorTemperature);

        SetColor deserializedSetColor = (SetColor) GsonRepository.gson(schema).fromJson(serializedSetColor, Action.class);
        SetColorTemperature deserializedSetColorTemperature = (SetColorTemperature) GsonRepository.gson(schema).fromJson(serializedSetColorTemperature, Action.class);

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

        JsonObject serializedSetColorResult = (JsonObject) new JsonParser().parse(GsonRepository.gson(schema).toJson(setColorResult));
        JsonObject serializedSetColorTemperatureResult = (JsonObject) new JsonParser().parse(GsonRepository.gson(schema).toJson(setColorTemperatureResult));
        Assert.assertEquals((JsonObject) new JsonParser().parse("{\"setColor\":{\"succeeded\":true}}"), serializedSetColorResult);
        Assert.assertEquals((JsonObject) new JsonParser().parse("{\"setColorTemperature\":{\"succeeded\":false}}"), serializedSetColorTemperatureResult);

        SetColorResult deserializedSetColorResult = (SetColorResult) GsonRepository.gson(schema).fromJson(serializedSetColorResult, ActionResult.class);
        SetColorTemperatureResult deserializedSetColorTemperatureResult = (SetColorTemperatureResult) GsonRepository.gson(schema).fromJson(serializedSetColorTemperatureResult, ActionResult.class);

        Assert.assertEquals(setColorResult.succeeded, deserializedSetColorResult.succeeded);
        Assert.assertEquals(setColorTemperatureResult.succeeded, deserializedSetColorTemperatureResult.succeeded);
    }

    @Test
    public void conditionTest() throws Exception {
        Equals stringEquals = new Equals("f1", "str");
        Condition condition = new Condition(stringEquals);

        JsonObject serializedJson = (JsonObject) new JsonParser().parse(GsonRepository.gson().toJson(condition));
        Assert.assertEquals((JsonObject) new JsonParser().parse("{\"type\":\"eq\", \"field\":\"f1\", \"value\":\"str\"}"), serializedJson);
        Assert.assertEquals(condition.getClause(), GsonRepository.gson().fromJson(serializedJson, Condition.class).getClause());
    }

    @Test
    public void commandTest() throws Exception {
        // Define the schema
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder("ThingType", "SchemaName1", 10, LightState.class);
        sb.addActionClass(SetColor.class, SetColorResult.class);
        sb.addActionClass(SetColorTemperature.class, SetColorTemperatureResult.class);
        Schema schema = sb.build();

        JsonObject expectedJson = (JsonObject) new JsonParser().parse(
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
        JsonObject serializedJson = (JsonObject) new JsonParser().parse(gson.toJson(command));

        Assert.assertEquals(expectedJson, serializedJson);

        serializedJson.addProperty("commandID", "Command-1234567");
        command = gson.fromJson(serializedJson.toString(), Command.class);

        Assert.assertEquals("Command-1234567", command.getCommandID());
        Assert.assertEquals("SchemaName1", command.getSchemaName());
        Assert.assertEquals(10, command.getSchemaVersion());

        SetColor setColor = (SetColor) command.getActions().get(0);
        Assert.assertArrayEquals(new int[]{128, 0, 255}, setColor.color);

        SetColorTemperature setColorTemperature = (SetColorTemperature) command.getActions().get(1);
        Assert.assertEquals(25, setColorTemperature.colorTemperature);

        SetColorResult setColorResult = (SetColorResult) command.getActionResults().get(0);
        Assert.assertTrue(setColorResult.succeeded);

        SetColorTemperatureResult setColorTemperatureResult = (SetColorTemperatureResult) command.getActionResults().get(1);
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

        JsonObject expectedJson = (JsonObject) new JsonParser().parse(
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
        JsonObject serializedJson = (JsonObject) new JsonParser().parse(gson.toJson(trigger));

        Assert.assertEquals(expectedJson, serializedJson);

        serializedJson.addProperty("triggerID", "Trigger-1234567");
        trigger = gson.fromJson(serializedJson.toString(), Trigger.class);

        Assert.assertEquals("Trigger-1234567", trigger.getTriggerID());

        command = trigger.getCommand();
        Assert.assertEquals("SchemaName1", command.getSchemaName());
        Assert.assertEquals(10, command.getSchemaVersion());

        SetColor setColor = (SetColor) command.getActions().get(0);
        Assert.assertArrayEquals(new int[]{128, 0, 255}, setColor.color);

        SetColorTemperature setColorTemperature = (SetColorTemperature) command.getActions().get(1);
        Assert.assertEquals(25, setColorTemperature.colorTemperature);

        SetColorResult setColorResult = (SetColorResult) command.getActionResults().get(0);
        Assert.assertTrue(setColorResult.succeeded);

        SetColorTemperatureResult setColorTemperatureResult = (SetColorTemperatureResult) command.getActionResults().get(1);
        Assert.assertFalse(setColorTemperatureResult.succeeded);

        predicate = trigger.getPredicate();
        Assert.assertTrue(predicate instanceof SchedulePredicate);
        Assert.assertEquals("1 0 * * *", ((SchedulePredicate) predicate).getSchedule().getCronExpression());
    }

    @Test
    public void stateTriggerTest() throws Exception {
        // Define the schema
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder("ThingType", "SchemaName1", 10, LightState.class);
        sb.addActionClass(SetColor.class, SetColorResult.class);
        sb.addActionClass(SetColorTemperature.class, SetColorTemperatureResult.class);
        Schema schema = sb.build();

        JsonObject expectedJson = (JsonObject) new JsonParser().parse(
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
                        "        \"eventSource\":\"STATES\"," +
                        "        \"triggersWhen\":\"CONDITION_FALSE_TO_TRUE\"," +
                        "        \"condition\":{" +
                        "            \"type\":\"and\"," +
                        "            \"clauses\":[" +
                        "                {\"type\":\"eq\", \"field\":\"prefecture\", \"value\":\"Tokyo\"}," +
                        "                {\"type\":\"not\", \"clause\":{\"type\":\"eq\", \"field\":\"city\", \"value\":\"Akasaka\"}}," +
                        "                {\"type\":\"range\", \"field\":\"temperature\", \"lowerLimit\":25, \"lowerIncluded\":true}" +
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
        JsonObject serializedJson = (JsonObject) new JsonParser().parse(gson.toJson(trigger));

        Assert.assertEquals(expectedJson, serializedJson);

        serializedJson.addProperty("triggerID", "Trigger-1234567");
        trigger = gson.fromJson(serializedJson.toString(), Trigger.class);

        Assert.assertEquals("Trigger-1234567", trigger.getTriggerID());

        command = trigger.getCommand();
        Assert.assertEquals("SchemaName1", command.getSchemaName());
        Assert.assertEquals(10, command.getSchemaVersion());

        SetColor setColor = (SetColor) command.getActions().get(0);
        Assert.assertArrayEquals(new int[]{128, 0, 255}, setColor.color);

        SetColorTemperature setColorTemperature = (SetColorTemperature) command.getActions().get(1);
        Assert.assertEquals(25, setColorTemperature.colorTemperature);

        SetColorResult setColorResult = (SetColorResult) command.getActionResults().get(0);
        Assert.assertTrue(setColorResult.succeeded);

        SetColorTemperatureResult setColorTemperatureResult = (SetColorTemperatureResult) command.getActionResults().get(1);
        Assert.assertFalse(setColorTemperatureResult.succeeded);

        predicate = trigger.getPredicate();
        Assert.assertTrue(predicate instanceof StatePredicate);
        Assert.assertEquals(TriggersWhen.CONDITION_FALSE_TO_TRUE, ((StatePredicate) predicate).getTriggersWhen());
        Assert.assertEquals(condition.getClause(), ((StatePredicate) predicate).getCondition().getClause());
    }
}