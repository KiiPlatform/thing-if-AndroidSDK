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
import com.kii.thingif.trigger.Predicate;
import com.kii.thingif.trigger.ScheduleOncePredicate;
import com.kii.thingif.trigger.ServerCode;
import com.kii.thingif.trigger.StatePredicate;
import com.kii.thingif.trigger.Trigger;
import com.kii.thingif.trigger.TriggerOptions;
import com.kii.thingif.trigger.TriggeredCommandForm;
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
    public void patchStateTriggerWithCommandTest() throws Exception {
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

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

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getTypedID(), api.getOwner().getTypedID(), actions);
        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTriggerWithCommand(200, triggerID, expectedCommand, predicate, false, null, schema);

        ThingIFAPIUtils.setTarget(api, target);
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
    public void patchStateTriggerWithFormTest() throws Exception {
        StatePredicate predicate =
            new StatePredicate(
                new Condition(new Equals("power", true)),
                TriggersWhen.CONDITION_CHANGED);

        Schema schema = createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        TypedID thingID2 = new TypedID(TypedID.Types.THING, "th.9876543210");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);

        ThingIFAPI api = createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        JSONObject metadata = new JSONObject();
        metadata.put("key", "value");
        TriggerOptions options =
            TriggerOptions.Builder.newBuilder().setTitle("title").
                setDescription("description").setMetadata(metadata).build();
        Command expectedCommand = new Command(schema.getSchemaName(),
                schema.getSchemaVersion(),
                thingID2, api.getOwner().getTypedID(), actions);
        addEmptyMockResponse(204);
        addMockResponseForGetTriggerWithCommand(200, triggerID,
                expectedCommand, predicate, options, false, null, schema);

        ThingIFAPIUtils.setTarget(api, new StandaloneThing(thingID.getID(),
                        "vendor-thing-id", accessToken));
        Trigger trigger = api.patchTrigger(
            triggerID,
            TriggeredCommandForm.Builder.newBuilder(
                DEMO_SCHEMA_NAME,
                DEMO_SCHEMA_VERSION,
                actions).setTargetID(thingID2).build(),
            predicate,
            options);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getServerCode());
        assertPredicate(predicate, trigger.getPredicate());
        assertCommand(schema, expectedCommand, trigger.getCommand());
        assertTriggerOptions(options, trigger);
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() +
                "/triggers/" + triggerID, request1.getPath());
        Assert.assertEquals("PATCH", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization", "Bearer " +
                api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        assertRequestHeader(expectedRequestHeaders1, request1);

        JsonObject expectedRequestBody =
            GsonRepository.gson().toJsonTree(options).getAsJsonObject();
        expectedRequestBody.add("command",
                GsonRepository.gson(schema).toJsonTree(expectedCommand));
        expectedRequestBody.add("predicate",
                GsonRepository.gson().toJsonTree(predicate));
        expectedRequestBody.add("triggersWhat", new JsonPrimitive("COMMAND"));
        assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() +
                "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization",
                "Bearer " + api.getOwner().getAccessToken());
        assertRequestHeader(expectedRequestHeaders2, request2);
    }

    private void patchTriggerWithServerCodeTest(Predicate predicate) throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);
        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        ServerCode expectedServerCode = new ServerCode("function_name", "token12345", "app0001", new JSONObject("{\"param\":\"p0001\"}"));
        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTriggerWithServerCode(200, triggerID, expectedServerCode, predicate, false, null, schema);

        ThingIFAPIUtils.setTarget(api, target);
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
    public void patchStateTriggerWithServerCodeTest() throws Exception {
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);
        patchTriggerWithServerCodeTest(predicate);
    }
    @Test
    public void patchScheduleOnceTriggerWithServerCodeTest() throws Exception {
        ScheduleOncePredicate predicate = new ScheduleOncePredicate(1000);
        patchTriggerWithServerCodeTest(predicate);
    }

    private void patchTriggerWithServerCodeAndOptionsTest(
            Predicate predicate,
            boolean sendServerCode,
            boolean sendPredicate,
            boolean sendOptions)
        throws Exception
    {
        Schema schema = createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id",
                accessToken);
        ThingIFAPI api = createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        TriggerOptions expectedOptions = TriggerOptions.Builder.newBuilder().
                setTitle("title").
                setDescription("description").
                setMetadata(new JSONObject("{\"key\":\"value\"}")).build();
        ServerCode expectedServerCode = new ServerCode(
            "function_name", "token12345", "app0001",
            new JSONObject("{\"param\":\"p0001\"}"));
        addEmptyMockResponse(204);
        addMockResponseForGetTriggerWithServerCode(200, triggerID,
                expectedServerCode, predicate, expectedOptions, false, null,
                schema);

        ThingIFAPIUtils.setTarget(api, target);
        Trigger trigger = api.patchTrigger(
                triggerID,
                sendServerCode ? expectedServerCode : null,
                sendPredicate ? predicate : null,
                sendOptions ? expectedOptions : null);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertNull(trigger.getCommand());
        assertPredicate(predicate, trigger.getPredicate());
        assertServerCode(expectedServerCode, trigger.getServerCode());
        assertTriggerOptions(expectedOptions, trigger);
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() +
                "/triggers/" + triggerID, request1.getPath());
        Assert.assertEquals("PATCH", request1.getMethod());

        Map<String, String> expectedRequestHeaders1 = new HashMap<>();
        expectedRequestHeaders1.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders1.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders1.put("Authorization",
                "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders1.put("Content-Type", "application/json");
        assertRequestHeader(expectedRequestHeaders1, request1);

        JsonObject expectedRequestBody = null;
        if (sendOptions) {
            expectedRequestBody =
                GsonRepository.gson().toJsonTree(expectedOptions).getAsJsonObject();
        } else {
            expectedRequestBody = new JsonObject();
        }
        if (sendServerCode) {
            expectedRequestBody.add("serverCode",
                    GsonRepository.gson().toJsonTree(expectedServerCode));
        }
        if (sendPredicate) {
            expectedRequestBody.add("predicate",
                    GsonRepository.gson().toJsonTree(predicate));
        }
        expectedRequestBody.add("triggersWhat",
                new JsonPrimitive("SERVER_CODE"));
        this.assertRequestBody(expectedRequestBody, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() +
                "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders2 = new HashMap<>();
        expectedRequestHeaders2.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders2.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders2.put("Authorization", "Bearer " +
                api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders2, request2);
    }

    @Test
    public void patchStateTriggerWithServerCodeAndPrecicateTest()
        throws Exception
    {
        StatePredicate predicate = new StatePredicate(
            new Condition(new Equals("power", true)),
            TriggersWhen.CONDITION_CHANGED);
        patchTriggerWithServerCodeAndOptionsTest(predicate, true, true, true);
    }

    @Test
    public void patchStateTriggerWithNoServerCodeAndPrecicateTest()
        throws Exception
    {
        StatePredicate predicate = new StatePredicate(
            new Condition(new Equals("power", true)),
            TriggersWhen.CONDITION_CHANGED);
        patchTriggerWithServerCodeAndOptionsTest(predicate, false, true, true);
    }

    @Test
    public void patchStateTriggerWithServerCodeAndPrecicateAndNoOptionsTest()
        throws Exception
    {
        StatePredicate predicate = new StatePredicate(
            new Condition(new Equals("power", true)),
            TriggersWhen.CONDITION_CHANGED);
        patchTriggerWithServerCodeAndOptionsTest(predicate, true, true, false);
    }

    @Test
    public void patchScheduleOnceTriggerWithServerCodeAndPrecicateTest()
        throws Exception
    {
        ScheduleOncePredicate predicate = new ScheduleOncePredicate(1000);
        patchTriggerWithServerCodeAndOptionsTest(predicate, true, true, true);
    }

    @Test
    public void patchScheduleOnceTriggerWithNoServerCodeAndPrecicateTest()
        throws Exception
    {
        ScheduleOncePredicate predicate = new ScheduleOncePredicate(1000);
        patchTriggerWithServerCodeAndOptionsTest(predicate, false, true, true);
    }

    @Test
    public void patchScheduleOnceTriggerWithServerCodeAndPrecicateAndNoOptionsTest()
        throws Exception
    {
        ScheduleOncePredicate predicate = new ScheduleOncePredicate(1000);
        patchTriggerWithServerCodeAndOptionsTest(predicate, true, true, false);
    }

    @Test
    public void patchTriggerWithServerCodeAndNoPrecicateAndNoOptionsTest()
        throws Exception
    {
        ScheduleOncePredicate predicate = new ScheduleOncePredicate(1000);
        patchTriggerWithServerCodeAndOptionsTest(predicate, true, false, false);
    }

    @Test
    public void patchTriggerWithServerCodeAndNoPrecicateAndOptionsTest()
        throws Exception
    {
        ScheduleOncePredicate predicate = new ScheduleOncePredicate(1000);
        patchTriggerWithServerCodeAndOptionsTest(predicate, true, false, true);
    }

    @Test
    public void patchTriggerNullTriggerID() throws Exception {
        Schema schema = createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id",
                accessToken);
        ThingIFAPI api = createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        TriggerOptions options = TriggerOptions.Builder.newBuilder().
                setTitle("title").
                setDescription("description").
                setMetadata(new JSONObject("{\"key\":\"value\"}")).build();
        ServerCode serverCode = new ServerCode(
            "function_name", "token12345", "app0001",
            new JSONObject("{\"param\":\"p0001\"}"));
        ScheduleOncePredicate predicate = new ScheduleOncePredicate(1000);

        IllegalArgumentException actual = null;
        ThingIFAPIUtils.setTarget(api, target);
        try {
            api.patchTrigger(null, serverCode, predicate, options);
        } catch (IllegalArgumentException e) {
            actual = e;
        }
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getMessage(), "triggerID is null or empty");
    }

    @Test
    public void patchTriggerEmptyTriggerID() throws Exception {
        Schema schema = createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id",
                accessToken);
        ThingIFAPI api = createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        TriggerOptions options = TriggerOptions.Builder.newBuilder().
                setTitle("title").
                setDescription("description").
                setMetadata(new JSONObject("{\"key\":\"value\"}")).build();
        ServerCode serverCode = new ServerCode(
            "function_name", "token12345", "app0001",
            new JSONObject("{\"param\":\"p0001\"}"));
        ScheduleOncePredicate predicate = new ScheduleOncePredicate(1000);

        IllegalArgumentException actual = null;
        ThingIFAPIUtils.setTarget(api, target);
        try {
            api.patchTrigger("", serverCode, predicate, options);
        } catch (IllegalArgumentException e) {
            actual = e;
        }
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getMessage(), "triggerID is null or empty");
    }

    @Test
    public void patchTriggerOnlyTriggerID() throws Exception {
        Schema schema = createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id",
                accessToken);
        ThingIFAPI api = createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        IllegalArgumentException actual = null;
        ThingIFAPIUtils.setTarget(api, target);
        try {
            api.patchTrigger("triggerID", (ServerCode)null, null, null);
        } catch (IllegalArgumentException e) {
            actual = e;
        }
        Assert.assertNotNull(actual);
        Assert.assertEquals(actual.getMessage(),
                "serverCode, predicate and options are null.");
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

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getTypedID(), api.getOwner().getTypedID(), actions);
        this.addEmptyMockResponse(403);

        try {
            ThingIFAPIUtils.setTarget(api, target);
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

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getTypedID(), api.getOwner().getTypedID(), actions);
        this.addEmptyMockResponse(404);

        try {
            ThingIFAPIUtils.setTarget(api, target);
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

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getTypedID(), api.getOwner().getTypedID(), actions);
        this.addEmptyMockResponse(503);

        try {
            ThingIFAPIUtils.setTarget(api, target);
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

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
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

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);
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

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);
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

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);
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

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);
        api.patchTrigger(triggerID, "", DEMO_SCHEMA_VERSION, actions, predicate);
    }

    @Test
    public void patchTriggerWithCommandTarget403ErrorTest() throws Exception {
        Schema schema = createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        TypedID thingID2 = new TypedID(TypedID.Types.THING, "th.9876543210");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate =
            new StatePredicate(new Condition(new Equals("power", true)),
                    TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand =
            new Command(schema.getSchemaName(), schema.getSchemaVersion(),
                    thingID2, api.getOwner().getTypedID(), actions);
        addEmptyMockResponse(403);

        try {
            ThingIFAPIUtils.setTarget(api, new StandaloneThing(thingID.getID(),
                            "vendor-thing-id", accessToken));
            api.patchTrigger(
                triggerID,
                TriggeredCommandForm.Builder.newBuilder(
                    DEMO_SCHEMA_NAME,
                    DEMO_SCHEMA_VERSION,
                    actions).setTargetID(thingID2).build(),
                predicate,
                null);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ForbiddenException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() +
                "/triggers/" + triggerID, request.getPath());
        Assert.assertEquals("PATCH", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization",
                "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/json");
        assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.add("command",
                GsonRepository.gson(schema).toJsonTree(expectedCommand));
        expectedRequestBody.add("predicate",
                GsonRepository.gson(schema).toJsonTree(predicate));
        expectedRequestBody.add("triggersWhat", new JsonPrimitive("COMMAND"));
        assertRequestBody(expectedRequestBody, request);
    }

    @Test
    public void patchTriggerWithForm404ErrorTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        TypedID thingID2 = new TypedID(TypedID.Types.THING, "th.9876543210");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate =
                new StatePredicate(new Condition(new Equals("power", true)),
                        TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = this.createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand = new Command(schema.getSchemaName(),
                schema.getSchemaVersion(), thingID2,
                api.getOwner().getTypedID(), actions);
        addEmptyMockResponse(404);

        try {
            ThingIFAPIUtils.setTarget(api, new StandaloneThing(thingID.getID(),
                            "vendor-thing-id", accessToken));
            api.patchTrigger(
                triggerID,
                TriggeredCommandForm.Builder.newBuilder(
                    DEMO_SCHEMA_NAME,
                    DEMO_SCHEMA_VERSION,
                    actions).setTargetID(thingID2).build(),
                predicate,
                null);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (NotFoundException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() +
                "/triggers/" + triggerID, request.getPath());
        Assert.assertEquals("PATCH", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization",
                "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/json");
        assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.add("command",
                GsonRepository.gson(schema).toJsonTree(expectedCommand));
        expectedRequestBody.add("predicate",
                GsonRepository.gson(schema).toJsonTree(predicate));
        expectedRequestBody.add("triggersWhat", new JsonPrimitive("COMMAND"));
        assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void patchTriggerWithForm503ErrorTest() throws Exception {
        Schema schema = createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        TypedID thingID2 = new TypedID(TypedID.Types.THING, "th.9876543210");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";

        List<Action> actions = new ArrayList<>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate =
                new StatePredicate(new Condition(new Equals("power", true)),
                        TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand = new Command(schema.getSchemaName(),
                schema.getSchemaVersion(), thingID2,
                api.getOwner().getTypedID(), actions);
        addEmptyMockResponse(503);

        try {
            ThingIFAPIUtils.setTarget(api, new StandaloneThing(thingID.getID(),
                            "vendor-thing-id", accessToken));
            api.patchTrigger(
                triggerID,
                TriggeredCommandForm.Builder.newBuilder(
                    DEMO_SCHEMA_NAME,
                    DEMO_SCHEMA_VERSION,
                    actions).setTargetID(thingID2).build(),
                predicate,
                null);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (ServiceUnavailableException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() +
                "/triggers/" + triggerID, request.getPath());
        Assert.assertEquals("PATCH", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization",
                "Bearer " + api.getOwner().getAccessToken());
        expectedRequestHeaders.put("Content-Type", "application/json");
        assertRequestHeader(expectedRequestHeaders, request);

        JsonObject expectedRequestBody = new JsonObject();
        expectedRequestBody.add("command",
                GsonRepository.gson(schema).toJsonTree(expectedCommand));
        expectedRequestBody.add("predicate",
                GsonRepository.gson(schema).toJsonTree(predicate));
        expectedRequestBody.add("triggersWhat", new JsonPrimitive("COMMAND"));
        assertRequestBody(expectedRequestBody, request);
    }

    @Test(expected = IllegalStateException.class)
    public void patchTriggerWithNullArgumentsTest() throws Exception {
        ThingIFAPI api = createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.patchTrigger("trigger-1234", (TriggeredCommandForm)null, null,
                null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void patchTriggerWithFormAndNullTriggerIDTest() throws Exception {
        Schema schema = createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        TypedID thingID2 = new TypedID(TypedID.Types.THING, "th.9876543210");
        String accessToken = "thing-access-token-1234";

        List<Action> actions = new ArrayList<>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate =
                new StatePredicate(new Condition(new Equals("power", true)),
                        TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, new StandaloneThing(thingID.getID(),
                        "vendor-thing-id", accessToken));
        api.patchTrigger(
            null,
            TriggeredCommandForm.Builder.newBuilder(
                DEMO_SCHEMA_NAME,
                DEMO_SCHEMA_VERSION,
                actions).setTargetID(thingID2).build(),
            predicate,
            null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void patchTriggerWithFormAndEmptyTriggerIDTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        TypedID thingID2 = new TypedID(TypedID.Types.THING, "th.9876543210");
        String accessToken = "thing-access-token-1234";

        List<Action> actions = new ArrayList<>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate = new StatePredicate(
            new Condition(new Equals("power", true)),
            TriggersWhen.CONDITION_CHANGED);

        ThingIFAPI api = createThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, new StandaloneThing(thingID.getID(),
                        "vendor-thing-id", accessToken));
        api.patchTrigger(
            "",
            TriggeredCommandForm.Builder.newBuilder(
                DEMO_SCHEMA_NAME,
                DEMO_SCHEMA_VERSION,
                actions).setTargetID(thingID2).build(),
            predicate,
            null);
    }

}


