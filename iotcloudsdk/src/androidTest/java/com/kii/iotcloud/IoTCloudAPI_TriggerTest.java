package com.kii.iotcloud;

import android.support.test.runner.AndroidJUnit4;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kii.iotcloud.command.Action;
import com.kii.iotcloud.command.Command;
import com.kii.iotcloud.exception.IoTCloudRestException;
import com.kii.iotcloud.exception.UnsupportedActionException;
import com.kii.iotcloud.exception.UnsupportedSchemaException;
import com.kii.iotcloud.schema.Schema;
import com.kii.iotcloud.schema.SchemaBuilder;
import com.kii.iotcloud.testschemas.LightState;
import com.kii.iotcloud.testschemas.SetBrightness;
import com.kii.iotcloud.testschemas.SetBrightnessResult;
import com.kii.iotcloud.testschemas.SetColor;
import com.kii.iotcloud.testschemas.SetColorResult;
import com.kii.iotcloud.testschemas.SetColorTemperature;
import com.kii.iotcloud.testschemas.SetColorTemperatureResult;
import com.kii.iotcloud.testschemas.TurnPower;
import com.kii.iotcloud.testschemas.TurnPowerResult;
import com.kii.iotcloud.trigger.Condition;
import com.kii.iotcloud.trigger.StatePredicate;
import com.kii.iotcloud.trigger.Trigger;
import com.kii.iotcloud.trigger.TriggersWhen;
import com.kii.iotcloud.trigger.clause.Equals;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * https://github.com/KiiCorp/IoTCloud/blob/master/rest_api_spec/trigger-endpoint.yaml
 */
@RunWith(AndroidJUnit4.class)
public class IoTCloudAPI_TriggerTest extends IoTCloudAPITestBase {
    @Test
    public void postNewTriggerTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new Target(thingID, accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getID(), api.getOwner().getID(), actions);
        this.addMockResponseForPostNewTrigger(201, triggerID);
        this.addMockResponseForGetTrigger(200, triggerID, expectedCommand, predicate, false, null, schema);

        Trigger trigger = api.postNewTrigger(target, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions, predicate);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        this.assertPredicate(predicate, trigger.getPredicate());
        this.assertCommand(schema, expectedCommand, trigger.getCommand());
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
        expectedRequestBody.add("command", GsonRepository.gson(schema).toJsonTree(expectedCommand));
        expectedRequestBody.add("predicate", GsonRepository.gson(schema).toJsonTree(predicate));
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
        Target target = new Target(thingID, accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getID(), api.getOwner().getID(), actions);
        this.addEmptyMockResponse(403);

        try {
            api.postNewTrigger(target, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions, predicate);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (IoTCloudRestException e) {
            Assert.assertEquals(403, e.getStatusCode());
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
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void postNewTrigger404ErrorTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new Target(thingID, accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getID(), api.getOwner().getID(), actions);
        this.addEmptyMockResponse(404);

        try {
            api.postNewTrigger(target, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions, predicate);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (IoTCloudRestException e) {
            Assert.assertEquals(404, e.getStatusCode());
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
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test
    public void postNewTrigger503ErrorTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new Target(thingID, accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getID(), api.getOwner().getID(), actions);
        this.addEmptyMockResponse(503);

        try {
            api.postNewTrigger(target, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions, predicate);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (IoTCloudRestException e) {
            Assert.assertEquals(503, e.getStatusCode());
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
        this.assertRequestBody(expectedRequestBody, request);
    }
    @Test(expected = IllegalArgumentException.class)
    public void postNewTriggerWithNullTargetTest() throws Exception {
        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.postNewTrigger(null, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions, predicate);
    }
    @Test(expected = IllegalArgumentException.class)
    public void postNewTriggerWithNullSchemaNameTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new Target(thingID, accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.postNewTrigger(target, null, DEMO_SCHEMA_VERSION, actions, predicate);
    }
    @Test(expected = IllegalArgumentException.class)
    public void postNewTriggerWithEmptySchemaNameTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new Target(thingID, accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.postNewTrigger(target, "", DEMO_SCHEMA_VERSION, actions, predicate);
    }
    @Test(expected = IllegalArgumentException.class)
    public void postNewTriggerWithNullActionsTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new Target(thingID, accessToken);

        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.postNewTrigger(target, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, null, predicate);
    }
    @Test(expected = IllegalArgumentException.class)
    public void postNewTriggerWithEmptyActionsTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new Target(thingID, accessToken);

        List<Action> actions = new ArrayList<Action>();
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.postNewTrigger(target, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions, predicate);
    }
    @Test(expected = IllegalArgumentException.class)
    public void postNewTriggerWithNullPredicateTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new Target(thingID, accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.postNewTrigger(target, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions, null);
    }
    @Test
    public void getTriggerTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new Target(thingID, accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getID(), api.getOwner().getID(), actions);
        this.addMockResponseForGetTrigger(200, triggerID, expectedCommand, predicate, true, "COMMAND_EXECUTION_REJECTED", schema);

        Trigger trigger = api.getTrigger(target, triggerID);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(true, trigger.disabled());
        Assert.assertEquals("COMMAND_EXECUTION_REJECTED", trigger.getDisabledReason());
        this.assertPredicate(predicate, trigger.getPredicate());
        this.assertCommand(schema, expectedCommand, trigger.getCommand());
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID, request.getPath());
        Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void getTrigger403ErrorTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new Target(thingID, accessToken);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        this.addEmptyMockResponse(403);

        try {
            api.getTrigger(target, triggerID);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (IoTCloudRestException e) {
            Assert.assertEquals(403, e.getStatusCode());
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID, request.getPath());
        Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void getTrigger404ErrorTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new Target(thingID, accessToken);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        this.addEmptyMockResponse(404);

        try {
            api.getTrigger(target, triggerID);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (IoTCloudRestException e) {
            Assert.assertEquals(404, e.getStatusCode());
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID, request.getPath());
        Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void getTrigger503ErrorTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new Target(thingID, accessToken);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        this.addEmptyMockResponse(503);

        try {
            api.getTrigger(target, triggerID);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (IoTCloudRestException e) {
            Assert.assertEquals(503, e.getStatusCode());
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID, request.getPath());
        Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void getTriggerWithInvalidSchemaNameTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder(DEMO_THING_TYPE, DEMO_SCHEMA_NAME + "_invalid", DEMO_SCHEMA_VERSION, LightState.class);
        sb.addActionClass(SetColor.class, SetColorResult.class);
        sb.addActionClass(SetBrightness.class, SetBrightnessResult.class);
        sb.addActionClass(SetColorTemperature.class, SetColorTemperatureResult.class);
        sb.addActionClass(TurnPower.class, TurnPowerResult.class);
        Schema invalidSchema = sb.build();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new Target(thingID, accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithSchema(APP_ID, APP_KEY, invalidSchema);

        Command expectedCommand = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getID(), api.getOwner().getID(), actions);
        this.addMockResponseForGetTrigger(200, triggerID, expectedCommand, predicate, true, "COMMAND_EXECUTION_REJECTED", schema);

        try {
            api.getTrigger(target, triggerID);
            Assert.fail("UnsupportedSchemaException should be thrown");
        } catch (UnsupportedSchemaException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID, request.getPath());
        Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void getTriggerWithInvalidSchemaVersionTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder(DEMO_THING_TYPE, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION + 1, LightState.class);
        sb.addActionClass(SetColor.class, SetColorResult.class);
        sb.addActionClass(SetBrightness.class, SetBrightnessResult.class);
        sb.addActionClass(SetColorTemperature.class, SetColorTemperatureResult.class);
        sb.addActionClass(TurnPower.class, TurnPowerResult.class);
        Schema invalidSchema = sb.build();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new Target(thingID, accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithSchema(APP_ID, APP_KEY, invalidSchema);

        Command expectedCommand = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getID(), api.getOwner().getID(), actions);
        this.addMockResponseForGetTrigger(200, triggerID, expectedCommand, predicate, true, "COMMAND_EXECUTION_REJECTED", schema);

        try {
            api.getTrigger(target, triggerID);
            Assert.fail("UnsupportedSchemaException should be thrown");
        } catch (UnsupportedSchemaException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID, request.getPath());
        Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test
    public void getTriggerWithUnknownActionTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder(DEMO_THING_TYPE, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, LightState.class);
        sb.addActionClass(SetBrightness.class, SetBrightnessResult.class);
        sb.addActionClass(SetColorTemperature.class, SetColorTemperatureResult.class);
        sb.addActionClass(TurnPower.class, TurnPowerResult.class);
        Schema unsupportedSetColorSchema = sb.build();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new Target(thingID, accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithSchema(APP_ID, APP_KEY, unsupportedSetColorSchema);

        Command expectedCommand = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getID(), api.getOwner().getID(), actions);
        this.addMockResponseForGetTrigger(200, triggerID, expectedCommand, predicate, true, "COMMAND_EXECUTION_REJECTED", schema);

        GsonRepository.clearCache();
        try {
            api.getTrigger(target, triggerID);
            Assert.fail("UnsupportedActionException should be thrown");
        } catch (UnsupportedActionException e) {
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID, request.getPath());
        Assert.assertEquals("GET", request.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request);
    }
    @Test(expected = IllegalArgumentException.class)
    public void getTriggerWithNullTargetTest() throws Exception {
        String triggerID = "trigger-1234";

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.getTrigger(null, triggerID);
    }
    @Test(expected = IllegalArgumentException.class)
    public void getTriggerWithNullTriggerIDTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new Target(thingID, accessToken);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.getTrigger(target, null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void getTriggerWithEmptyTriggerIDTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new Target(thingID, accessToken);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.getTrigger(target, "");
    }
    @Test
    public void enableTriggerTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new Target(thingID, accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getID(), api.getOwner().getID(), actions);
        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTrigger(200, triggerID, expectedCommand, predicate, false, null, schema);

        Trigger trigger = api.enableTrigger(target, triggerID, true);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertFalse(trigger.disabled());
        this.assertPredicate(predicate, trigger.getPredicate());
        this.assertCommand(schema, expectedCommand, trigger.getCommand());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID + "/enable", request1.getPath());
        Assert.assertEquals("PUT", request1.getMethod());
        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request2);
    }
    @Test
    public void disableTriggerTest() throws Exception {
        Schema schema = this.createDefaultSchema();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new Target(thingID, accessToken);

        List<Action> actions = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions.add(setColor);
        actions.add(setColorTemperature);
        StatePredicate predicate = new StatePredicate(new Condition(new Equals("power", true)), TriggersWhen.CONDITION_CHANGED);

        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);

        Command expectedCommand = new Command(schema.getSchemaName(), schema.getSchemaVersion(), target.getID(), api.getOwner().getID(), actions);
        this.addEmptyMockResponse(204);
        this.addMockResponseForGetTrigger(200, triggerID, expectedCommand, predicate, false, null, schema);

        Trigger trigger = api.enableTrigger(target, triggerID, false);
        // verify the result
        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(false, trigger.disabled());
        Assert.assertNull(trigger.getDisabledReason());
        Assert.assertTrue(trigger.disabled());
        this.assertPredicate(predicate, trigger.getPredicate());
        this.assertCommand(schema, expectedCommand, trigger.getCommand());
        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID + "/disable", request1.getPath());
        Assert.assertEquals("PUT", request1.getMethod());
        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request2);
    }
    @Test
    public void enableTrigger403Test() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new Target(thingID, accessToken);
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        this.addEmptyMockResponse(403);
        try {
            api.enableTrigger(target, triggerID, false);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (IoTCloudRestException e) {
            Assert.assertEquals(403, e.getStatusCode());
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID + "/disable", request.getPath());
        Assert.assertEquals("PUT", request.getMethod());
    }
    @Test
    public void enableTrigger404Test() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new Target(thingID, accessToken);
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        this.addEmptyMockResponse(404);
        try {
            api.enableTrigger(target, triggerID, false);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (IoTCloudRestException e) {
            Assert.assertEquals(404, e.getStatusCode());
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID + "/disable", request.getPath());
        Assert.assertEquals("PUT", request.getMethod());
    }
    @Test
    public void enableTrigger503Test() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        String triggerID = "trigger-1234";
        Target target = new Target(thingID, accessToken);
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        this.addEmptyMockResponse(503);
        try {
            api.enableTrigger(target, triggerID, false);
            Assert.fail("IoTCloudRestException should be thrown");
        } catch (IoTCloudRestException e) {
            Assert.assertEquals(503, e.getStatusCode());
        }
        // verify the request
        RecordedRequest request = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID + "/disable", request.getPath());
        Assert.assertEquals("PUT", request.getMethod());
    }
    @Test(expected = IllegalArgumentException.class)
    public void enableTriggerWithNullTargetTest() throws Exception {
        String triggerID = "trigger-1234";
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.enableTrigger(null, triggerID, false);
    }
    @Test(expected = IllegalArgumentException.class)
    public void enableTriggerWithNullTriggerIDTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new Target(thingID, accessToken);
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.enableTrigger(target, null, false);
    }
    @Test(expected = IllegalArgumentException.class)
    public void enableTriggerWithEmptyTriggerIDTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new Target(thingID, accessToken);
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(APP_ID, APP_KEY);
        api.enableTrigger(target, "", false);
    }
}


