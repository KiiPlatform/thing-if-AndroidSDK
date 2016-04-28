package com.kii.thingif;

import android.support.test.runner.AndroidJUnit4;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.Command;
import com.kii.thingif.exception.ForbiddenException;
import com.kii.thingif.exception.NotFoundException;
import com.kii.thingif.exception.ServiceUnavailableException;
import com.kii.thingif.internal.GsonRepository;
import com.kii.thingif.schema.Schema;
import com.kii.thingif.testschemas.SetColor;
import com.kii.thingif.testschemas.SetColorTemperature;
import com.kii.thingif.trigger.Condition;
import com.kii.thingif.trigger.ServerCode;
import com.kii.thingif.trigger.StatePredicate;
import com.kii.thingif.trigger.Trigger;
import com.kii.thingif.trigger.TriggersWhen;
import com.kii.thingif.trigger.clause.Equals;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * https://github.com/KiiCorp/ThingIF/blob/master/rest_api_spec/trigger-endpoint.yaml
 */
@RunWith(AndroidJUnit4.class)
public class ThingIFAPI_PatchTriggerTest extends ThingIFAPITestBase {
    @Test
    public void patchTriggerWithCommandTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getTypedID(), api.getOwner().getTypedID(), actions);
        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTriggerWithCommand(200, triggerID, expectedCommand, predicate, false, null, schema);

        api.setTarget(target);
        Trigger trigger = api.patchTrigger(triggerID, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions, predicate);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getServerCode());
        this.assertPredicate(predicate, trigger.getPredicate());
        this.assertCommand(schema, expectedCommand, trigger.getCommand());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID, request1.getPath());
        Assert.assertEquals("PATCH", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<String, String>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.add("command", GsonRepository.gson(schema).toJsonTree(expectedCommand));
        expectedRequestBody.add("predicate", GsonRepository.gson(schema).toJsonTree(predicate));
        expectedRequestBody.add("triggersWhat", new JsonPrimitive("COMMAND"));
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<String, String>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }
    @Test
    public void patchTriggerWithServerCodeTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        ServerCode expectedServerCode = new ServerCode("function_name", "token12345", "app0001", new JSONObject("{\"param\":\"p0001\"}"));
        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTriggerWithServerCode(200, triggerID, expectedServerCode, predicate, false, null, schema);

        api.setTarget(target);
        Trigger trigger = api.patchTrigger(triggerID, expectedServerCode, predicate);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getCommand());
        this.assertPredicate(predicate, trigger.getPredicate());
        this.assertServerCode(expectedServerCode, trigger.getServerCode());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID, request1.getPath());
        Assert.assertEquals("PATCH", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<String, String>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.add("serverCode", GsonRepository.gson(schema).toJsonTree(expectedServerCode));
        expectedRequestBody.add("predicate", GsonRepository.gson(schema).toJsonTree(predicate));
        expectedRequestBody.add("triggersWhat", new JsonPrimitive("SERVER_CODE"));
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<String, String>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }
    @Test
    public void patchTrigger403ErrorTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getTypedID(), api.getOwner().getTypedID(), actions);
        this.addEmptyMockResponse(403);

        try {
            api.setTarget(target);
            api.patchTrigger(triggerID, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions, predicate);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ForbiddenException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID, request.getPath());
        Assert.assertEquals("PATCH", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.add("command", GsonRepository.gson(schema).toJsonTree(expectedCommand));
        expectedRequestBody.add("predicate", GsonRepository.gson(schema).toJsonTree(predicate));
        expectedRequestBody.add("triggersWhat", new JsonPrimitive("COMMAND"));
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void patchTrigger404ErrorTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getTypedID(), api.getOwner().getTypedID(), actions);
        this.addEmptyMockResponse(404);

        try {
            api.setTarget(target);
            api.patchTrigger(triggerID, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions, predicate);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (NotFoundException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID, request.getPath());
        Assert.assertEquals("PATCH", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.add("command", GsonRepository.gson(schema).toJsonTree(expectedCommand));
        expectedRequestBody.add("predicate", GsonRepository.gson(schema).toJsonTree(predicate));
        expectedRequestBody.add("triggersWhat", new JsonPrimitive("COMMAND"));
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void patchTrigger503ErrorTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getTypedID(), api.getOwner().getTypedID(), actions);
        this.addEmptyMockResponse(503);

        try {
            api.setTarget(target);
            api.patchTrigger(triggerID, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions, predicate);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ServiceUnavailableException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID, request.getPath());
        Assert.assertEquals("PATCH", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.add("command", GsonRepository.gson(schema).toJsonTree(expectedCommand));
        expectedRequestBody.add("predicate", GsonRepository.gson(schema).toJsonTree(predicate));
        expectedRequestBody.add("triggersWhat", new JsonPrimitive("COMMAND"));
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test(expected = IllegalStateException.class)
    public void patchTriggerWithNullTargetTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        String triggerID = "trigger-1234";

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.patchTrigger(triggerID, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions, predicate);
    }
    @Test(expected = IllegalArgumentException.class)
    public void patchTriggerWithNullTriggerIDTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(target);
        api.patchTrigger(null, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions, predicate);
    }
    @Test(expected = IllegalArgumentException.class)
    public void patchTriggerWithEmptyTriggerIDTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(target);
        api.patchTrigger(null, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions, predicate);
    }
    @Test(expected = IllegalArgumentException.class)
    public void patchTriggerWithNullSchemaNameTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(target);
        api.patchTrigger(triggerID, null, DEMO_SCHEMA_VERSION, actions, predicate);
    }
    @Test(expected = IllegalArgumentException.class)
    public void patchTriggerWithEmptySchemaNameTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(target);
        api.patchTrigger(triggerID, "", DEMO_SCHEMA_VERSION, actions, predicate);
    }
    @Test(expected = IllegalArgumentException.class)
    public void patchTriggerWithNullActionsTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(target);
        api.patchTrigger(triggerID, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, null, predicate);
    }
    @Test(expected = IllegalArgumentException.class)
    public void patchTriggerWithEmptyActionsTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<Action> actions = new ArrayList<Action>();
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(target);
        api.patchTrigger(triggerID, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions, predicate);
    }
    @Test(expected = IllegalArgumentException.class)
    public void patchTriggerWithNullPredicateTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(target);
        api.patchTrigger(triggerID, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions, null);
    }
}


