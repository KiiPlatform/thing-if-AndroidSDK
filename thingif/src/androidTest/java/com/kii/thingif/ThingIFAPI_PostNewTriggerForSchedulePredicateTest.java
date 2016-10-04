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
import com.kii.thingif.trigger.Predicate;
import com.kii.thingif.trigger.SchedulePredicate;
import com.kii.thingif.trigger.ServerCode;
import com.kii.thingif.trigger.Trigger;
import com.kii.thingif.trigger.TriggerOptions;
import com.kii.thingif.trigger.TriggeredCommandForm;
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

@RunWith(AndroidJUnit4.class)
public class ThingIFAPI_PostNewTriggerForSchedulePredicateTest
    extends ThingIFAPITestBase
{
    @Test
    public void postNewTriggerWithSchedulePredicateTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(),
                "vendor-thing-id", accessToken);

        List<Action> actions = new ArrayList<Action>();
        actions.add(new SetColor(128, 0, 255));
        actions.add(new SetColorTemperature(25));
        Predicate predicate = new SchedulePredicate("1 * * * *");

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand = new Command(
                schema.getSchemaName(),
                schema.getSchemaVersion(),
                target.getTypedID(),
                api.getOwner().getTypedID(),
                actions);
        this.addMockResponseForPostNewTrigger(201, triggerID);
        this.addMockResponseForGetTriggerWithCommand(
                200,
                triggerID,
                expectedCommand,
                predicate,
                false,
                null,
                schema);

        ThingIFAPIUtils.setTarget(api, target);
        Trigger trigger = api.postNewTrigger(
                DEMO_SCHEMA_NAME,
                DEMO_SCHEMA_VERSION,
                actions,
                predicate);

        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getServerCode());
        this.assertPredicate(predicate, trigger.getPredicate());
        this.assertCommand(schema, expectedCommand, trigger.getCommand());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(
            BASE_PATH + "/targets/" + thingID.toString() + "/triggers",
            request1.getPath());
        Assert.assertEquals("POST", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 =
                new HashMap<String, String>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization",
                "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.add("command",
                GsonRepository.gson(schema).toJsonTree(expectedCommand));
        expectedRequestBody.add("predicate",
                GsonRepository.gson(schema).toJsonTree(predicate));
        expectedRequestBody.add("triggersWhat", new JsonPrimitive("COMMAND"));
        this.assertRequestBodyByJSON(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(
                BASE_PATH +
                    "/targets/" +
                    thingID.toString() +
                    "/triggers/" +
                    triggerID,
                request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 =
                new HashMap<String, String>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put(
            "Authorization",
            "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }

    @Test
    public void postNewTriggerWithServerCodeTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        Predicate predicate = new SchedulePredicate("1 * * * *");
        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        ServerCode expectedServerCode = new ServerCode("function_name", "token12345", "app0001", new JSONObject("{\"param\":\"p0001\"}"));
        this.addMockResponseForPostNewTrigger(201, triggerID);
        this.addMockResponseForGetTriggerWithServerCode(200, triggerID, expectedServerCode, predicate, false, null, schema);

        ThingIFAPIUtils.setTarget(api, target);
        Trigger trigger = api.postNewTrigger(expectedServerCode, predicate);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getCommand());
        this.assertPredicate(predicate, trigger.getPredicate());
        this.assertServerCode(expectedServerCode, trigger.getServerCode());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers", request1.getPath());
        Assert.assertEquals("POST", request1.getMethod());

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
    public void postNewTrigger403ErrorTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        Predicate predicate = new SchedulePredicate("1 * * * *");

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getTypedID(), api.getOwner().getTypedID(), actions);
        this.addEmptyMockResponse(403);

        try {
            ThingIFAPIUtils.setTarget(api, target);
            api.postNewTrigger(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions, predicate);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ForbiddenException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<String, String>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.add("command", GsonRepository.gson(schema).toJsonTree(expectedCommand));
        expectedRequestBody.add("predicate", GsonRepository.gson(schema).toJsonTree(predicate));
        expectedRequestBody.add("triggersWhat", new JsonPrimitive("COMMAND"));
        this.assertRequestBodyByJSON(expectedRequestBody, request);
    }

    @Test
    public void postNewTrigger404ErrorTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        Predicate predicate = new SchedulePredicate("1 * * * *");

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getTypedID(), api.getOwner().getTypedID(), actions);
        this.addEmptyMockResponse(404);

        try {
            ThingIFAPIUtils.setTarget(api, target);
            api.postNewTrigger(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions, predicate);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (NotFoundException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<String, String>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.add("command", GsonRepository.gson(schema).toJsonTree(expectedCommand));
        expectedRequestBody.add("predicate", GsonRepository.gson(schema).toJsonTree(predicate));
        expectedRequestBody.add("triggersWhat", new JsonPrimitive("COMMAND"));
        this.assertRequestBodyByJSON(expectedRequestBody, request);
    }

    @Test
    public void postNewTrigger503ErrorTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        Predicate predicate = new SchedulePredicate("1 * * * *");

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getTypedID(), api.getOwner().getTypedID(), actions);
        this.addEmptyMockResponse(503);

        try {
            ThingIFAPIUtils.setTarget(api, target);
            api.postNewTrigger(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions, predicate);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ServiceUnavailableException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<String, String>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.add("command", GsonRepository.gson(schema).toJsonTree(expectedCommand));
        expectedRequestBody.add("predicate", GsonRepository.gson(schema).toJsonTree(predicate));
        expectedRequestBody.add("triggersWhat", new JsonPrimitive("COMMAND"));
        this.assertRequestBodyByJSON(expectedRequestBody, request);
    }
    @Test(expected = IllegalStateException.class)
    public void postNewTriggerWithNullTargetTest() throws Exception {
        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        Predicate predicate = new SchedulePredicate("1 * * * *");

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.postNewTrigger(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions, predicate);
    }
    @Test(expected = IllegalArgumentException.class)
    public void postNewTriggerWithNullSchemaNameTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        Predicate predicate = new SchedulePredicate("1 * * * *");

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);
        api.postNewTrigger(null, DEMO_SCHEMA_VERSION, actions, predicate);
    }
    @Test(expected = IllegalArgumentException.class)
    public void postNewTriggerWithEmptySchemaNameTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        Predicate predicate = new SchedulePredicate("1 * * * *");

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);
        api.postNewTrigger("", DEMO_SCHEMA_VERSION, actions, predicate);
    }
    @Test(expected = IllegalArgumentException.class)
    public void postNewTriggerWithNullActionsTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        Predicate predicate = new SchedulePredicate("1 * * * *");

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);
        api.postNewTrigger(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, null, predicate);
    }
    @Test(expected = IllegalArgumentException.class)
    public void postNewTriggerWithEmptyActionsTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<Action> actions = new ArrayList<Action>();
        Predicate predicate = new SchedulePredicate("1 * * * *");

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);
        api.postNewTrigger(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions, predicate);
    }
    @Test(expected = IllegalArgumentException.class)
    public void postNewTriggerWithNullPredicateTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);
        api.postNewTrigger(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions, null);
    }

    @Test
    public void postNewTriggerWithTargetAndSchedulePredicateTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingIDA = new TypedID(TypedID.Types.THING, "th.1234567890");
        TypedID thingIDB = new TypedID(TypedID.Types.THING, "th.9876543210");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingIDA.getID(),
                "vendor-thing-id", accessToken);

        List<Action> actions = new ArrayList<Action>();
        actions.add(new SetColor(128, 0, 255));
        actions.add(new SetColorTemperature(25));
        Predicate predicate = new SchedulePredicate("1 * * * *");

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        JSONObject metadata = new JSONObject();
        metadata.put("key", "value");
        TriggerOptions options = TriggerOptions.Builder.newBuilder().
                setTitle("title").setDescription("description").
                setMetadata(metadata).build();
        Command expectedCommand = new Command(
                schema.getSchemaName(),
                schema.getSchemaVersion(),
                thingIDB,
                api.getOwner().getTypedID(),
                actions);
        this.addMockResponseForPostNewTrigger(201, triggerID);
        this.addMockResponseForGetTriggerWithCommand(
                200,
                triggerID,
                expectedCommand,
                predicate,
                options,
                false,
                null,
                schema);

        ThingIFAPIUtils.setTarget(api, target);
        Trigger trigger = api.postNewTrigger(
                TriggeredCommandForm.Builder.builder(
                    DEMO_SCHEMA_NAME,
                    DEMO_SCHEMA_VERSION,
                    actions).setTargetID(thingIDB).build(),
                predicate,
                options);

        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getServerCode());
        this.assertPredicate(predicate, trigger.getPredicate());
        this.assertCommand(schema, expectedCommand, trigger.getCommand());
        assertTriggerOptions(options, trigger);
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(
                BASE_PATH + "/targets/" + thingIDA.toString() + "/triggers",
                request1.getPath());
        Assert.assertEquals("POST", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 =
                new HashMap<String, String>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization",
                "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request1);

        JsonObject expectedRequestBody =
                GsonRepository.gson().toJsonTree(options).getAsJsonObject();
        expectedRequestBody.add("command",
                GsonRepository.gson(schema).toJsonTree(expectedCommand));
        expectedRequestBody.add("predicate",
                GsonRepository.gson(schema).toJsonTree(predicate));
        expectedRequestBody.add("triggersWhat", new JsonPrimitive("COMMAND"));
        assertRequestBodyByJSON(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(
                BASE_PATH +
                        "/targets/" +
                        thingIDA.toString() +
                        "/triggers/" +
                        triggerID,
                request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 =
                new HashMap<String, String>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put(
                "Authorization",
                "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }

    @Test
    public void postNewTriggerWithTarget403ErrorTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingIDA = new TypedID(TypedID.Types.THING, "th.1234567890");
        TypedID thingIDB = new TypedID(TypedID.Types.THING, "th.9876543210");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingIDA.getID(), "vendor-thing-id", accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        Predicate predicate = new SchedulePredicate("1 * * * *");

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand = new Command(
            schema.getSchemaName(),
            schema.getSchemaVersion(),
            thingIDB,
            api.getOwner().getTypedID(),
            actions);
        this.addEmptyMockResponse(403);

        try {
            ThingIFAPIUtils.setTarget(api, target);
            api.postNewTrigger(
                TriggeredCommandForm.Builder.builder(
                    DEMO_SCHEMA_NAME,
                    DEMO_SCHEMA_VERSION,
                    actions).setTargetID(thingIDB).build(),
                predicate,
                null);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ForbiddenException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingIDA.toString() + "/triggers", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<String, String>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.add("command", GsonRepository.gson(schema).toJsonTree(expectedCommand));
        expectedRequestBody.add("predicate", GsonRepository.gson(schema).toJsonTree(predicate));
        expectedRequestBody.add("triggersWhat", new JsonPrimitive("COMMAND"));
        assertRequestBodyByJSON(expectedRequestBody, request);
    }

    @Test
    public void postNewTriggerWithTarget404ErrorTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingIDA = new TypedID(TypedID.Types.THING, "th.1234567890");
        TypedID thingIDB = new TypedID(TypedID.Types.THING, "th.9876543210");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingIDA.getID(), "vendor-thing-id", accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        Predicate predicate = new SchedulePredicate("1 * * * *");

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand = new Command(schema.getSchemaName(), schema.getSchemaVersion(), thingIDB, api.getOwner().getTypedID(), actions);
        this.addEmptyMockResponse(404);

        try {
            ThingIFAPIUtils.setTarget(api, target);
            api.postNewTrigger(
                TriggeredCommandForm.Builder.builder(
                    DEMO_SCHEMA_NAME,
                    DEMO_SCHEMA_VERSION,
                    actions).setTargetID(thingIDB).build(),
                predicate,
                null);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (NotFoundException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingIDA.toString() + "/triggers", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<String, String>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.add("command", GsonRepository.gson(schema).toJsonTree(expectedCommand));
        expectedRequestBody.add("predicate", GsonRepository.gson(schema).toJsonTree(predicate));
        expectedRequestBody.add("triggersWhat", new JsonPrimitive("COMMAND"));
        this.assertRequestBody(expectedRequestBody, request);
    }

    @Test
    public void postNewTriggerWithTarget503ErrorTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingIDA = new TypedID(TypedID.Types.THING, "th.1234567890");
        TypedID thingIDB = new TypedID(TypedID.Types.THING, "th.9876543210");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingIDA.getID(), "vendor-thing-id", accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        Predicate predicate = new SchedulePredicate("1 * * * *");

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand = new Command(schema.getSchemaName(), schema.getSchemaVersion(), thingIDB, api.getOwner().getTypedID(), actions);
        this.addEmptyMockResponse(503);

        try {
            ThingIFAPIUtils.setTarget(api, target);
            api.postNewTrigger(
                TriggeredCommandForm.Builder.builder(
                    DEMO_SCHEMA_NAME,
                    DEMO_SCHEMA_VERSION,
                    actions).setTargetID(thingIDB).build(),
                predicate,
                null);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ServiceUnavailableException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingIDA.toString() + "/triggers", request.getPath());
        Assert.assertEquals("POST", request.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<String, String>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        this.assertRequestHeader(expectedRequestHeaders1, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.add("command", GsonRepository.gson(schema).toJsonTree(expectedCommand));
        expectedRequestBody.add("predicate", GsonRepository.gson(schema).toJsonTree(predicate));
        expectedRequestBody.add("triggersWhat", new JsonPrimitive("COMMAND"));
        assertRequestBodyByJSON(expectedRequestBody, request);
    }

    @Test(expected = IllegalArgumentException.class)
    public void postNewTriggerWithNullTriggeredCommandForm() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        Predicate predicate = new SchedulePredicate("1 * * * *");

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);
        api.postNewTrigger(null, predicate, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void postNewTriggerWithTargetAndNullPredicateTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);
        api.postNewTrigger(
            TriggeredCommandForm.Builder.builder(
                DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions).build(),
            null, null);
    }

}
