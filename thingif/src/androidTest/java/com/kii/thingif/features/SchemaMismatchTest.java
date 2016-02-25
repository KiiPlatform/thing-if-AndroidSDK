package com.kii.thingif.features;

import android.support.test.runner.AndroidJUnit4;
import android.util.Pair;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.ThingIFAPITestBase;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.Command;
import com.kii.thingif.schema.Schema;
import com.kii.thingif.testschemas.SetBrightness;
import com.kii.thingif.testschemas.TurnPower;
import com.kii.thingif.trigger.Trigger;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class SchemaMismatchTest extends ThingIFAPITestBase {

    @Test
    public void testCommandSchemaMismatch() throws Exception {

        // Onboard.
        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema("dummyAppId", "dummyAppKey");
        this.addMockResponseForOnBoard(200, "dummyThingID", "dummyToken");

        api.onboard("dummyThingID", "dummyPassword");

        long currentTime = System.currentTimeMillis();
        Schema s = this.createDefaultSchema();

        // Creates Response of list commands.

        // Schema mismatch command.
        JsonObject command1 = new JsonObject();

        command1.add("schema", new JsonPrimitive("non-match"));
        command1.add("schemaVersion", new JsonPrimitive(100));
        command1.add("target", new JsonPrimitive("thing:dummy-target-id"));
        command1.add("commandState", new JsonPrimitive("SENDING"));
        command1.add("issuer", new JsonPrimitive("user:dummy-user-id"));
        command1.add("commandID", new JsonPrimitive("dummy-command-id1"));
        command1.add("createdAt", new JsonPrimitive(currentTime));
        command1.add("modifiedAt", new JsonPrimitive(currentTime));

        // Actions.
        JsonArray actions = new JsonArray();
        JsonParser p  = new JsonParser();
        JsonElement action1 = p.parse("{\"setBrightness\" : {\"brightness\":50} }");
        actions.add(action1);

        command1.add("actions", actions);

        // Schema match command.
        JsonObject command2 = new JsonObject();

        command2.add("schema", new JsonPrimitive(s.getSchemaName()));
        command2.add("schemaVersion", new JsonPrimitive(s.getSchemaVersion()));
        command2.add("target", new JsonPrimitive("thing:dummy-target-id"));
        command2.add("commandState", new JsonPrimitive("SENDING"));
        command2.add("issuer", new JsonPrimitive("user:dummy-user-id"));
        command2.add("commandID", new JsonPrimitive("dummy-command-id1"));
        command2.add("createdAt", new JsonPrimitive(currentTime));
        command2.add("modifiedAt", new JsonPrimitive(currentTime));

        // Actions.
        JsonArray actions2 = new JsonArray();
        JsonElement action2 = p.parse("{\"setBrightness\" : {\"brightness\":100} }");
        actions2.add(action2);
        command2.add("actions", actions2);

        // Create mock response.
        JsonObject mockResponse = new JsonObject();
        JsonArray commands = new JsonArray();
        commands.add(command1);
        commands.add(command2);
        mockResponse.add("commands", commands);

        this.addMockResponse(200, mockResponse);

        // Inspect Result.
        Pair<List<Command>, String> result = api.listCommands(0, null);
        List<Command> list = result.first;
        Assert.assertEquals(1, list.size());
        Command receivedCommand = list.get(0);

        List<Action> receivedActions = receivedCommand.getActions();
        Assert.assertEquals(1, receivedActions.size());
        Action receivedAction = receivedActions.get(0);
        Assert.assertEquals("setBrightness", receivedAction.getActionName());
        Assert.assertEquals(100, ((SetBrightness)receivedAction).brightness);
    }

    @Test
    public void testTriggerSchemaMismatch() throws Exception {
        // Onboard.
        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema("dummyAppId", "dummyAppKey");
        this.addMockResponseForOnBoard(200, "dummyThingID", "dummyToken");

        api.onboard("dummyThingID", "dummyPassword");

        long currentTime = System.currentTimeMillis();
        Schema s = this.createDefaultSchema();

        // Creates Response of list triggers.
        String listResponse =
                "{" +
                "  \"triggers\": [" +
                "    {" +
                "      \"triggerID\": \"32b86360-d3be-11e5-9872-22000aa79e15\"," +
                "      \"predicate\": {" +
                "        \"triggersWhen\": \"CONDITION_TRUE\"," +
                "        \"condition\": {" +
                "          \"type\": \"eq\"," +
                "          \"field\": \"power\"," +
                "          \"value\": true" +
                "        }," +
                "        \"eventSource\": \"STATES\"" +
                "      }," +
                "      \"triggersWhat\": \"COMMAND\"," +
                "      \"command\": {" +
                "        \"schema\": \"InvalidSchema\"," +
                "        \"schemaVersion\": 1," +
                "        \"target\": \"thing:th.51e97aa00022-e4f8-5e11-771d-0bd87409\"," +
                "        \"issuer\": \"user:33e070341321-5658-5e11-fd86-0637e2e2\"," +
                "        \"actions\": [" +
                "          {" +
                "            \"turnPower\": {" +
                "              \"power\": true" +
                "            }" +
                "          }" +
                "        ]" +
                "      }," +
                "      \"disabled\": false" +
                "    }," +
                "    {" +
                "      \"triggerID\": \"ab7c5100-d178-11e5-8070-22000a7f900d\"," +
                "      \"predicate\": {" +
                "        \"triggersWhen\": \"CONDITION_TRUE\"," +
                "        \"condition\": {" +
                "          \"type\": \"eq\"," +
                "          \"field\": \"power\"," +
                "          \"value\": true" +
                "        }," +
                "        \"eventSource\": \"STATES\"" +
                "      }," +
                "      \"triggersWhat\": \"COMMAND\"," +
                "      \"command\": {" +
                "        \"schema\": \"" + s.getSchemaName() + "\"," +
                "        \"schemaVersion\":" + s.getSchemaVersion() + "," +
                "        \"target\": \"thing:th.51e97aa00022-e4f8-5e11-771d-0bd87409\"," +
                "        \"issuer\": \"user:33e070341321-5658-5e11-fd86-0637e2e2\"," +
                "        \"actions\": [" +
                "          {" +
                "            \"turnPower\": {" +
                "              \"power\": false" +
                "            }" +
                "          }" +
                "        ]" +
                "      }," +
                "      \"disabled\": false" +
                "    }" +
                "  ]" +
                "}";
        JsonParser parser = new JsonParser();
        this.addMockResponse(200, parser.parse(listResponse));

        // Inspect result.
        Pair<List<Trigger>, String> result = api.listTriggers(0, null);
        List<Trigger> list = result.first;
        Assert.assertEquals(1, list.size());
        Trigger receivedTrigger = list.get(0);
        Command command = receivedTrigger.getCommand();
        Assert.assertEquals(s.getSchemaName(), command.getSchemaName());
        Assert.assertEquals(s.getSchemaVersion(), command.getSchemaVersion());
        Assert.assertEquals(1, command.getActions().size());
        Action action = command.getActions().get(0);
        Assert.assertEquals("turnPower", action.getActionName());
        Assert.assertEquals(false, ((TurnPower)action).power);
    }
}
